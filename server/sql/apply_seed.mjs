// Apply seed data to Supabase via Node + pg
import fs from 'node:fs/promises';
import process from 'node:process';
import { createRequire } from 'module';
const require = createRequire(import.meta.url);
const { Client } = require('pg');

async function main() {
  const uri = process.argv[2] || process.env.DATABASE_URL;
  if (!uri) {
    console.error('Usage: node server/sql/apply_seed.mjs <postgresql://...>');
    process.exit(1);
  }
  const sql = await fs.readFile('server/sql/supabase_seed.sql', 'utf8');
  const client = new Client({ connectionString: uri, ssl: { rejectUnauthorized: false } });
  await client.connect();
  try {
    await client.query('begin');
    await client.query(sql);
    await client.query('commit');
    console.log('Seed applied successfully');
  } catch (e) {
    await client.query('rollback');
    console.error('Error applying seed:', e.message);
    process.exitCode = 1;
  } finally {
    await client.end();
  }
}

main();

