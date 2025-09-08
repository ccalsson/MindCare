-- RPC: book_slot
-- Atomically creates a booking for the current authenticated user and marks the slot as booked.
-- Usage from client: rpc('book_slot', { professional_id, slot_id, price_amount, price_currency })

create or replace function public.book_slot(
  professional_id uuid,
  slot_id uuid,
  price_amount numeric,
  price_currency text
)
returns uuid
language plpgsql
security definer
as $$
declare
  v_user uuid;
  v_booking_id uuid;
  v_now timestamptz := now();
begin
  -- Require authenticated user
  v_user := auth.uid();
  if v_user is null then
    raise exception 'unauthenticated user' using errcode = '28000';
  end if;

  -- Lock the slot row to avoid race conditions
  perform 1 from public.availability_slots where id = slot_id for update;
  if not found then
    raise exception 'slot not found' using errcode = 'P0002';
  end if;

  -- Check slot availability and time
  if exists (
    select 1 from public.availability_slots s
    where s.id = slot_id
      and (s.status <> 'available' or s.start_at <= v_now)
  ) then
    raise exception 'slot not available' using errcode = 'P0001';
  end if;

  -- Create booking
  insert into public.bookings (user_id, professional_id, slot_id, price_amount, price_currency, status)
  values (v_user, professional_id, slot_id, price_amount, price_currency, 'confirmed')
  returning id into v_booking_id;

  -- Mark slot as booked
  update public.availability_slots set status = 'booked' where id = slot_id;

  return v_booking_id;
end;
$$;

-- Allow authenticated users to execute
revoke all on function public.book_slot(uuid, uuid, numeric, text) from public;
grant execute on function public.book_slot(uuid, uuid, numeric, text) to authenticated;

