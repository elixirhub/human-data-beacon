ALTER TABLE beacon_sample_table ADD COLUMN IF NOT EXISTS sex text DEFAULT 'UNKNOWN';
alter table beacon_sample_table add column IF NOT EXISTS age integer;
alter table beacon_sample_table add column IF NOT EXISTS age_of_onset integer;
alter table beacon_sample_table add column IF NOT EXISTS provenance text;
