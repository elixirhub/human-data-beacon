INSERT INTO public.beacon_dataset_table(id, stable_id, description, access_type, reference_genome, variant_cnt, call_cnt, sample_cnt)  
  VALUES (1, '1000genomes', 'Subset of variants of chromosomes 22 and Y from the 1000 genomes project', 'PUBLIC', 'GRCh37', 3119, 8513330, 2504);
-- Init dataset-ConsentCodes table
INSERT INTO public.beacon_dataset_consent_code_table (dataset_id, consent_code_id , additional_constraint, version) 
  VALUES(1, 1, null, 'v1.0'); -- NRES - No restrictions on data use
  
