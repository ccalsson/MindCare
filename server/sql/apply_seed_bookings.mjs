// Apply demo bookings for a given user UUID (must exist in auth.users and public.users)
import process from 'node:process';
import { createRequire } from 'module';
const require = createRequire(import.meta.url);
const { Client } = require('pg');

async function main() {
  const uri = process.argv[2] || process.env.DATABASE_URL;
  const userId = process.argv[3];
  if (!uri || !userId) {
    console.error('Usage: node server/sql/apply_seed_bookings.mjs <postgresql://...> <USER_UUID>');
    process.exit(1);
  }
  const client = new Client({ connectionString: uri, ssl: { rejectUnauthorized: false } });
  await client.connect();
  try {
    await client.query('begin');
    // Find a professional and an available slot
    const pro = await client.query(`select id from public.professionals order by created_at limit 1`);
    if (pro.rowCount === 0) throw new Error('No professionals found');
    const proId = pro.rows[0].id;

    const slot = await client.query(
      `select id from public.availability_slots where professional_id=$1 and status='available' order by start_at limit 1`,
      [proId]
    );
    if (slot.rowCount === 0) throw new Error('No available slots found');
    const slotId = slot.rows[0].id;

    // Insert booking
    const bk = await client.query(
      `insert into public.bookings (user_id, professional_id, slot_id, price_amount, price_currency, status)
       values ($1, $2, $3, 100, 'usd', 'confirmed') returning id`,
      [userId, proId, slotId]
    );

    // Mark slot as booked
    await client.query(`update public.availability_slots set status='booked' where id=$1`, [slotId]);

    await client.query('commit');
    console.log('Demo booking created:', bk.rows[0].id);
  } catch (e) {
    await client.query('rollback');
    console.error('Error applying demo bookings:', e.message);
    process.exitCode = 1;
  } finally {
    await client.end();
  }
}

main();

