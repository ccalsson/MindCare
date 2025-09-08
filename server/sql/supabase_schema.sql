-- MindCare Supabase schema (tables, enums, indexes, RLS policies)
-- Safe to run multiple times; CREATE IF NOT EXISTS used when possible.

-- Extensions
create extension if not exists "uuid-ossp";
create extension if not exists pgcrypto;

-- Helpers: updated_at trigger
create or replace function set_updated_at()
returns trigger as $$
begin
  new.updated_at = now();
  return new;
end;
$$ language plpgsql;

-- Enums
do $$ begin
  if not exists (select 1 from pg_type where typname = 'subscription_type') then
    create type subscription_type as enum ('free','basic','plus','premium');
  end if;
  if not exists (select 1 from pg_type where typname = 'content_type') then
    create type content_type as enum ('guidedMeditation','masterclass','workshop','course','expertTalk');
  end if;
  if not exists (select 1 from pg_type where typname = 'booking_status') then
    create type booking_status as enum ('pending','confirmed','canceled','completed');
  end if;
end $$;

-- Users (links to auth.users)
create table if not exists public.users (
  id uuid primary key references auth.users(id) on delete cascade,
  email text unique,
  display_name text,
  role text default 'user',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create trigger users_set_updated_at
  before update on public.users
  for each row execute function set_updated_at();

-- Subscriptions
create table if not exists public.subscriptions (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.users(id) on delete cascade,
  type subscription_type not null default 'free',
  is_active boolean not null default false,
  start_date timestamptz not null default now(),
  end_date timestamptz,
  has_promotional_premium boolean not null default false,
  promotion_end_date timestamptz,
  daily_minutes_limit int,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create index if not exists idx_subscriptions_user_active_end
  on public.subscriptions(user_id, is_active, end_date);
create trigger subscriptions_set_updated_at
  before update on public.subscriptions
  for each row execute function set_updated_at();

-- Meditation sessions
create table if not exists public.meditation_sessions (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  description text,
  duration int not null,
  category text not null,
  audio_url text not null,
  image_url text,
  is_premium boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create trigger meditation_sessions_set_updated_at
  before update on public.meditation_sessions
  for each row execute function set_updated_at();

-- Meditation history
create table if not exists public.meditation_history (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.users(id) on delete cascade,
  session_id uuid not null references public.meditation_sessions(id) on delete cascade,
  duration int not null,
  completed_at timestamptz not null default now()
);
create index if not exists idx_meditation_history_user_completed
  on public.meditation_history(user_id, completed_at desc);

-- Audio resources
create table if not exists public.audio_resources (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  url text not null,
  thumbnail_url text,
  duration int,
  is_premium boolean not null default false,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create trigger audio_resources_set_updated_at
  before update on public.audio_resources
  for each row execute function set_updated_at();

-- Premium content
create table if not exists public.premium_content (
  id uuid primary key default gen_random_uuid(),
  title text not null,
  description text,
  type content_type not null,
  tags text[] default '{}',
  metadata jsonb default '{}',
  release_date timestamptz not null default now(),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create trigger premium_content_set_updated_at
  before update on public.premium_content
  for each row execute function set_updated_at();

-- Professionals
create table if not exists public.professionals (
  id uuid primary key default gen_random_uuid(),
  name text not null,
  specialty text,
  verified boolean not null default false,
  verified_by uuid references public.users(id),
  verified_at timestamptz,
  profile jsonb default '{}',
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create trigger professionals_set_updated_at
  before update on public.professionals
  for each row execute function set_updated_at();

-- Availability slots
create table if not exists public.availability_slots (
  id uuid primary key default gen_random_uuid(),
  professional_id uuid not null references public.professionals(id) on delete cascade,
  start_at timestamptz not null,
  end_at timestamptz not null,
  status text not null default 'available'
);
create index if not exists idx_availability_professional_start
  on public.availability_slots(professional_id, start_at);

-- Bookings
create table if not exists public.bookings (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.users(id) on delete cascade,
  professional_id uuid not null references public.professionals(id) on delete cascade,
  slot_id uuid references public.availability_slots(id) on delete set null,
  price_amount numeric,
  price_currency text,
  status booking_status not null default 'pending',
  payment_intent_id text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now()
);
create index if not exists idx_bookings_professional_status
  on public.bookings(professional_id, status);
create index if not exists idx_bookings_user_created
  on public.bookings(user_id, created_at desc);
create trigger bookings_set_updated_at
  before update on public.bookings
  for each row execute function set_updated_at();

-- Chats
create table if not exists public.chats (
  id uuid primary key default gen_random_uuid(),
  created_at timestamptz not null default now()
);

create table if not exists public.chat_participants (
  chat_id uuid not null references public.chats(id) on delete cascade,
  user_id uuid not null references public.users(id) on delete cascade,
  primary key (chat_id, user_id)
);

create table if not exists public.chat_messages (
  id uuid primary key default gen_random_uuid(),
  chat_id uuid not null references public.chats(id) on delete cascade,
  sender_id uuid not null references public.users(id) on delete cascade,
  receiver_id uuid not null references public.users(id) on delete cascade,
  content text not null,
  timestamp timestamptz not null default now(),
  is_read boolean not null default false
);
create index if not exists idx_chat_messages_chat_ts
  on public.chat_messages(chat_id, timestamp desc);

-- Usage daily
create table if not exists public.usage_daily (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.users(id) on delete cascade,
  date date not null,
  minutes int not null default 0,
  updated_at timestamptz not null default now()
);
create unique index if not exists uq_usage_daily_user_date
  on public.usage_daily(user_id, date);
create trigger usage_daily_set_updated_at
  before update on public.usage_daily
  for each row execute function set_updated_at();

-- Activity logs
create table if not exists public.activity_logs (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.users(id) on delete cascade,
  event text not null,
  context jsonb default '{}',
  created_at timestamptz not null default now()
);
create index if not exists idx_activity_logs_user_created
  on public.activity_logs(user_id, created_at desc);

-- Analytics (anonymized)
create table if not exists public.analytics (
  id uuid primary key default gen_random_uuid(),
  event text not null,
  data jsonb default '{}',
  created_at timestamptz not null default now()
);
create index if not exists idx_analytics_event_created
  on public.analytics(event, created_at desc);

-- Admin roles
create table if not exists public.admin_roles (
  user_id uuid primary key references public.users(id) on delete cascade,
  role text not null default 'admin',
  granted_at timestamptz not null default now()
);

-- Stripe prices
create table if not exists public.stripe_prices (
  id text primary key,
  region text not null,
  plan text not null,
  price_amount numeric not null,
  price_currency text not null,
  updated_at timestamptz not null default now()
);

-- Billing
create table if not exists public.billing (
  id uuid primary key default gen_random_uuid(),
  user_id uuid not null references public.users(id) on delete cascade,
  provider text not null,
  data jsonb default '{}',
  created_at timestamptz not null default now()
);

-- User resource access
create table if not exists public.user_resource_access (
  user_id uuid not null references public.users(id) on delete cascade,
  resource_id uuid not null references public.audio_resources(id) on delete cascade,
  accessed_at timestamptz not null default now(),
  primary key (user_id, resource_id)
);

-- Row Level Security (RLS)
alter table public.users enable row level security;
alter table public.subscriptions enable row level security;
alter table public.meditation_history enable row level security;
alter table public.usage_daily enable row level security;
alter table public.activity_logs enable row level security;
alter table public.bookings enable row level security;
alter table public.chats enable row level security;
alter table public.chat_participants enable row level security;
alter table public.chat_messages enable row level security;
alter table public.billing enable row level security;
alter table public.user_resource_access enable row level security;

-- Public read tables (content catalogs): allow read to authenticated users
alter table public.meditation_sessions enable row level security;
alter table public.audio_resources enable row level security;
alter table public.premium_content enable row level security;

-- Policies
-- Users: self access
create policy users_self_select on public.users
  for select using (id = auth.uid());
create policy users_self_update on public.users
  for update using (id = auth.uid()) with check (id = auth.uid());
create policy users_self_insert on public.users
  for insert with check (id = auth.uid());

-- Subscriptions: owner-only
create policy subscriptions_owner_select on public.subscriptions
  for select using (user_id = auth.uid());
create policy subscriptions_owner_modify on public.subscriptions
  for all using (user_id = auth.uid()) with check (user_id = auth.uid());

-- Meditation history: owner-only
create policy history_owner_select on public.meditation_history
  for select using (user_id = auth.uid());
create policy history_owner_modify on public.meditation_history
  for all using (user_id = auth.uid()) with check (user_id = auth.uid());

-- Usage daily: owner-only
create policy usage_owner_select on public.usage_daily
  for select using (user_id = auth.uid());
create policy usage_owner_modify on public.usage_daily
  for all using (user_id = auth.uid()) with check (user_id = auth.uid());

-- Activity logs: owner-only
create policy activity_owner_select on public.activity_logs
  for select using (user_id = auth.uid());
create policy activity_owner_insert on public.activity_logs
  for insert with check (user_id = auth.uid());

-- Bookings: user can see their bookings
create policy bookings_user_select on public.bookings
  for select using (user_id = auth.uid());
create policy bookings_user_insert on public.bookings
  for insert with check (user_id = auth.uid());

-- Chats & messages: participant access
create policy chats_participant_select on public.chats
  for select using (exists (
    select 1 from public.chat_participants cp
    where cp.chat_id = chats.id and cp.user_id = auth.uid()
  ));
create policy chat_participants_self on public.chat_participants
  for all using (user_id = auth.uid()) with check (user_id = auth.uid());
create policy chat_messages_participant_select on public.chat_messages
  for select using (exists (
    select 1 from public.chat_participants cp
    where cp.chat_id = chat_messages.chat_id and cp.user_id = auth.uid()
  ));
create policy chat_messages_sender_insert on public.chat_messages
  for insert with check (sender_id = auth.uid());

-- Billing: owner-only
create policy billing_owner_select on public.billing
  for select using (user_id = auth.uid());
create policy billing_owner_insert on public.billing
  for insert with check (user_id = auth.uid());

-- User resource access: owner-only
create policy ura_owner_select on public.user_resource_access
  for select using (user_id = auth.uid());
create policy ura_owner_insert on public.user_resource_access
  for insert with check (user_id = auth.uid());

-- Catalog tables readable by authenticated users
create policy sessions_read_auth on public.meditation_sessions
  for select using (auth.role() = 'authenticated');
create policy audio_read_auth on public.audio_resources
  for select using (auth.role() = 'authenticated');
create policy premium_content_read_auth on public.premium_content
  for select using (auth.role() = 'authenticated');

-- Grant minimal privileges to anon/auth (RLS will enforce row access)
grant usage on schema public to anon, authenticated;
grant select, insert, update, delete on all tables in schema public to authenticated;
grant select on all tables in schema public to anon;

-- Future: refine admin policies using service role or admin_roles table as needed.
