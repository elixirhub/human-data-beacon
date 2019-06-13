INSERT INTO beacon_dataset_table(id, stable_id, species_id, description, access_type, reference_genome, variant_cnt, call_cnt, sample_cnt, info)
  VALUES (1, '1000genomes', 'human','Subset of variants of chromosomes 22 and Y from the 1000 genomes project', 'PUBLIC', 'GRCh37', 3119, 8513330, 2504, '{"biosamples":{"age":"20","sampletype":"cellculture","tissue":"lymph"}}');
INSERT INTO beacon_dataset_table(id, stable_id, species_id, description, access_type, reference_genome, variant_cnt, call_cnt, sample_cnt, info)
  VALUES (2, 'marouch_v2', 'abricot','variants from sample A2360 of the Marouch Abricot genome', 'PUBLIC', 'GRCh37', 4057792, 8115584, 1, '{"mcpd":{"puid":"db0f9fb8-aefd-4174-9509-e6c117eb6e63 ","accenumb":"PRJNA292050","ancest":"VELAZQUEZ//BULIDA","cropname":"apricot tree"},"biosamples":{"age":"10","sampletype":"cellculture","tissue":"leaf"}}');
-- Init dataset-ConsentCodes table
INSERT INTO beacon_dataset_consent_code_table (dataset_id, consent_code_id , additional_constraint, version)
  VALUES(1, 1, null, 'v1.0'); -- NRES - No restrictions on data use
INSERT INTO beacon_dataset_consent_code_table (dataset_id, consent_code_id , additional_constraint, version)
  VALUES(2, 1, null, 'v1.0'); -- NRES - No restrictions on data use
