Supabase schema setup

How to apply
- Open Supabase Dashboard → SQL → New query.
- Paste the contents of `server/sql/supabase_schema.sql` and Run.
- If you get “extension already exists” warnings, it’s fine.

Notes
- This creates tables, enums, indexes, timestamps, and basic RLS policies.
- Owner-based RLS allows users to read/write only their own rows (via `auth.uid()`).
- Admin/service actions should use the Supabase service role key (bypasses RLS).
- Adjust policies to your needs (e.g., who can read premium_content, bookings by pro users, etc.).

Seed data
- File: `server/sql/supabase_seed.sql`
- Apply with the same connection:
  - Node: `node server/sql/apply_seed.mjs <postgresql://...>`
  - Or paste into Supabase SQL editor and run.
  - Idempotent: only inserts rows if a row with the same natural key (e.g., title) does not already exist.
