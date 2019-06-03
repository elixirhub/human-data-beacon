#!/usr/bin/env bash

# Datasets
psql -h localhost -p 5432 -d elixir_beacon_dev -U microaccounts_dev < /beacon/1.sql

# Variants
psql -h localhost -p 5432 -U microaccounts_dev -c \
"copy beacon_data_table (dataset_id,chromosome,start,variant_id,reference,alternate,\"end\",\"type\",sv_length,variant_cnt,call_cnt,sample_cnt,frequency,matching_sample_cnt) from stdin using delimiters ';' csv header" elixir_beacon_dev < /beacon/1_chrY_subset.variants.csv
psql -h localhost -p 5432 -U microaccounts_dev -c \
    "copy beacon_data_table (dataset_id,chromosome,start,variant_id,reference,alternate,\"end\",\"type\",sv_length,variant_cnt,call_cnt,sample_cnt,frequency,matching_sample_cnt) from stdin using delimiters ';' csv header" elixir_beacon_dev < /beacon/1_chr21_subset.variants.csv
# plant variants
psql -h localhost -p 5432 -U microaccounts_dev -c \
    "copy beacon_data_table (dataset_id,chromosome,start,variant_id,reference,alternate,\"end\",\"type\",sv_length,variant_cnt,call_cnt,sample_cnt,frequency,matching_sample_cnt) from stdin using delimiters ';' csv header" elixir_beacon_dev < /beacon/1_A2360_marouch_v2.variants.csv

# Sample list
psql -h localhost -p 5432 -U microaccounts_dev -c \
"copy tmp_sample_table (sample_stable_id,dataset_id) from stdin using delimiters ';' csv header" elixir_beacon_dev < /beacon/1_chrY_subset.samples.csv
psql -h localhost -p 5432 -U microaccounts_dev -c \
"copy tmp_sample_table (sample_stable_id,dataset_id) from stdin using delimiters ';' csv header" elixir_beacon_dev < /beacon/1_chr21_subset.samples.csv
# plant sample
psql -h localhost -p 5432 -U microaccounts_dev -c \
"copy tmp_sample_table (sample_stable_id,dataset_id) from stdin using delimiters ';' csv header" elixir_beacon_dev < /beacon/1_A3260_marouch_v2.samples.csv


psql -h localhost -p 5432 -d elixir_beacon_dev -U microaccounts_dev < /beacon/2.sql
psql -h localhost -p 5432 -d elixir_beacon_dev -U microaccounts_dev < /beacon/3.sql

# Samples where each variant is found
psql -h localhost -p 5432 -U microaccounts_dev -c \
"copy tmp_data_sample_table (dataset_id,chromosome,start,variant_id,reference,alternate,\"type\",sample_ids) from stdin using delimiters ';' csv header" elixir_beacon_dev \
< /beacon/1_chrY_subset.variants.matching.samples.csv
psql -h localhost -p 5432  -U microaccounts_dev -c \
"copy tmp_data_sample_table (dataset_id,chromosome,start,variant_id,reference,alternate,\"type\",sample_ids) from stdin using delimiters ';' csv header" elixir_beacon_dev \
< /beacon/1_chr21_subset.variants.matching.samples.csv
# plant sample-variants correspondence
psql -h localhost -p 5432  -U microaccounts_dev -c \
"copy tmp_data_sample_table (dataset_id,chromosome,start,variant_id,reference,alternate,\"type\",sample_ids) from stdin using delimiters ';' csv header" elixir_beacon_dev \
< /beacon/1_A2360_marouch_v2.variants.matching.samples.csv


psql -h localhost -p 5432 -d elixir_beacon_dev -U microaccounts_dev < /beacon/4.sql

# Finally
psql -h localhost -p 5432 -d elixir_beacon_dev -U microaccounts_dev < /beacon/5.sql
