// Apply demo seed (professionals + availability)
import fs from 'node:fs/promises';
import process from 'node:process';
import { createRequire } from 'module';
const require = createRequire(import.meta.url);
const { Client } = require('pg');

async function main() {
  const uri = process.argv[2] || process.env.DATABASE_URL;
  if (!uri) {
    console.error('Usage: node server/sql/apply_seed_demo.mjs <postgresql://...>');
    process.exit(1);
  }
  const sql = await fs.readFile('server/sql/supabase_seed_demo.sql', 'utf8');
  const client = new Client({ connectionString: uri, ssl: { rejectUnauthorized: false } });
  await client.connect();
  try {
    await client.query('begin');
    await client.query(sql);
    await client.query('commit');
    console.log('Demo seed applied successfully');
  } catch (e) {
    await client.query('rollback');
    console.error('Error applying demo seed:', e.message);
    process.exitCode = 1;
  } finally {
    await client.end();
  }
}

main();

