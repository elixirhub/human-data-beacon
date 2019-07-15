--Precise deletion
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (1, 1, 4, '1', 'CGTG', 'C', 7, 'DEL', -3, 1, 1, 2, 1, 0.5);
 
--Imprecise deletion 
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (2, 1, 2827694, '1', 'CGTGGATGCGGGGAC', 'C', 2827762, 'DEL', -68, 1, 1, 2, 1, 0.4);
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (3, 1, 321682, '1', 'T', '.', 321887, 'DEL', -205, 1, 1, 2, 1, 0.5);
 
--Duplication 
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (4, 1, 12665100, '1', 'A', '.', 12686200, 'DUP', 21100, 1, 1, 2, 2, 0.3);
 
--Insertion 
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (5, 1, 4, '2', 'T', 'TA', null, 'INS', 2, 1, 1, 1, 1, 0.3);
 
--Deletion & Insertion 
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (6, 1, 4, '3', 'GCG', 'G', 6, 'DEL', -2, 1, 1, 1, 1, 0.5);
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (7, 1, 4, '3', 'GCG', 'GCGCG', null, 'INS', 2, 1, 1, 2, 2, 0.5);
 
--Deletion & Insertion & SNP 
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (8, 1, 2, '4', 'TC', 'T', 3, 'DEL', -1, 1, 1, 1, 1, 0.5);
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (9, 1, 2, '4', 'TC', 'TCA', null, 'INS', 1, 1, 1, 1, 1, 0.1);
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (10, 1, 4, '4', 'C', 'G', null, 'SNP', null, 1, 1, 2, 1, 0.1);
INSERT INTO beacon_data_table (id, dataset_id, start, chromosome, reference, alternate, "end", type, sv_length, variant_cnt, call_cnt, sample_cnt, matching_sample_cnt, frequency)
VALUES (11, 5, 4, '4', 'C', 'T', null, 'SNP', null, 1, 1, 2, 1, 0.1);
