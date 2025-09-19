-- Demo seed for professionals and availability (idempotent)

-- Professionals
insert into public.professionals (id, name, specialty, verified, profile)
select gen_random_uuid(), 'Dra. Ana Pérez', 'Psicología Clínica', true, '{"experience":"10 años","languages":["es","en"]}'
where not exists (select 1 from public.professionals where name='Dra. Ana Pérez');

insert into public.professionals (id, name, specialty, verified, profile)
select gen_random_uuid(), 'Lic. Martín López', 'Mindfulness y Estrés', true, '{"experience":"7 años","languages":["es"]}'
where not exists (select 1 from public.professionals where name='Lic. Martín López');

-- Availability: create 3 upcoming 50-min slots per professional starting tomorrow
with pros as (
  select id, name from public.professionals
  where name in ('Dra. Ana Pérez','Lic. Martín López')
), base as (
  select id as professional_id, name,
         (date_trunc('day', now()) + interval '1 day' + interval '10 hour') as start1
    from pros
), slots as (
  select professional_id, name, start1 as start_at
    from base
  union all
  select professional_id, name, start1 + interval '2 hour' from base
  union all
  select professional_id, name, start1 + interval '4 hour' from base
)
insert into public.availability_slots (id, professional_id, start_at, end_at, status)
select gen_random_uuid(), s.professional_id, s.start_at, s.start_at + interval '50 minutes', 'available'
from slots s
where not exists (
  select 1 from public.availability_slots x
  where x.professional_id = s.professional_id and x.start_at = s.start_at
);

-- Note: bookings demo omitted due to FK to users (requires auth.users).

