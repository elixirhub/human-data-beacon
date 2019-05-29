-- NOTE: Alter view and table to add column label
-- ALTER TABLE ontology_term_table ADD COLUMN label TEXT;
-- DROP VIEW ontology_term;
--CREATE OR REPLACE VIEW ontology_term AS SELECT ontology_term_table.id, ontology_term_table.ontology, ontology_term_table.label, ontology_term_table.term FROM ontology_term_table;

INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'I', '1', 'I Certain infectious and parasitic diseases', 'I Certain infectious and parasitic diseases');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'II', '2', 'II Neoplasms', 'II Neoplasms');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'III', '3', 'III Diseases of the blood and blood-forming organs and certain disorders involving the immune mechanism', 'III Diseases of the blood and blood-forming organs and certain disorders involving the immune mechanism');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'IV', '4', 'IV Endocrine, nutritional and metabolic diseases', 'IV Endocrine, nutritional and metabolic diseases');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10',  'V', '5', 'V Mental and behavioural disorders', 'V Mental and behavioural disorders');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'VI', '6', 'VI Diseases of the nervous system', 'VI Diseases of the nervous system');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'VII', '7', 'VII Diseases of the eye and adnexa', 'VII Diseases of the eye and adnexa');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'VIII', '8', 'VIII Diseases of the ear and mastoid process', 'VIII Diseases of the ear and mastoid process');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'IX', '9', 'IX Diseases of the circulatory system', 'IX Diseases of the circulatory system');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'X', '10', 'X Diseases of the respiratory system', 'X Diseases of the respiratory system');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XI', '11', 'XI Diseases of the digestive system', 'XI Diseases of the digestive system');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XII', '12', 'XII Diseases of the skin and subcutaneous tissue', 'XII Diseases of the skin and subcutaneous tissue');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XIII', '13', 'XIII Diseases of the musculoskeletal system and connective tissue', 'XIII Diseases of the musculoskeletal system and connective tissue');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XIV', '14', 'XIV Diseases of the genitourinary system', 'XIV Diseases of the genitourinary system');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XV', '15', 'XV Pregnancy, childbirth and the puerperium', 'XV Pregnancy, childbirth and the puerperium');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XVI', '16', 'XVI Certain conditions originating in the perinatal period', 'XVI Certain conditions originating in the perinatal period');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XVII', '17', 'XVII Congenital malformations, deformations and chromosomal abnormalities', 'XVII Congenital malformations, deformations and chromosomal abnormalities');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XVIII', '18', 'XVIII Symptoms, signs and abnormal clinical and laboratory findings, not elsewhere classified', 'XVIII Symptoms, signs and abnormal clinical and laboratory findings, not elsewhere classified');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XIX', '19', 'XIX Injury, poisoning and certain other consequences of external causes', 'XIX Injury, poisoning and certain other consequences of external causes');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XX', '20', 'XX External causes of morbidity and mortality', 'XX External causes of morbidity and mortality');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XXI', '21', 'XXI Factors influencing health status and contact with health services', 'XXI Factors influencing health status and contact with health services');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'ICD-10', 'XXII', '22', 'XXII Codes for special purposes', 'XXII Codes for special purposes');


INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'csvs.tech', '1', '1', 'Illumina', 'Illumina');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'csvs.tech', '2', '2', 'SOLiD', 'SOLiD');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'csvs.tech', '3', '3', 'Roche 454', 'Roche 454');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'csvs.tech', '4', '4', 'IonTorrent/IonProton', 'IonTorrent/IonProton');
INSERT INTO ontology_term_table (ontology, term, sample_table_column_name, label, additional_comments) VALUES ( 'csvs.tech', '5', '5', 'Nanopore', 'Nanopore');

