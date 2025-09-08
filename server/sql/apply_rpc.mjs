// Apply RPC functions to Supabase
import fs from 'node:fs/promises';
import process from 'node:process';
import { createRequire } from 'module';
const require = createRequire(import.meta.url);
const { Client } = require('pg');

async function applyFile(client, path) {
  const sql = await fs.readFile(path, 'utf8');
  await client.query(sql);
}

async function main() {
  const uri = process.argv[2] || process.env.DATABASE_URL;
  if (!uri) {
    console.error('Usage: node server/sql/apply_rpc.mjs <postgresql://...>');
    process.exit(1);
  }
  const client = new Client({ connectionString: uri, ssl: { rejectUnauthorized: false } });
  await client.connect();
  try {
    await client.query('begin');
    await applyFile(client, 'server/sql/supabase_rpc_book_slot.sql');
    await client.query('commit');
    console.log('RPC applied successfully');
  } catch (e) {
    await client.query('rollback');
    console.error('Error applying RPC:', e.message);
    process.exitCode = 1;
  } finally {
    await client.end();
  }
}

main();

