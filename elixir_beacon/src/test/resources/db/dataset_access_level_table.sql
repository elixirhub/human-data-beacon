INSERT INTO public.dataset_access_level_table(dataset_id, parent_field, field, access_level) VALUES (1, 'accessLevelSummary', '-', 'PUBLIC');
INSERT INTO public.dataset_access_level_table(dataset_id, parent_field, field, access_level) VALUES (1, 'datasetAlleleResponses', 'new_sensitive_field', 'PUBLIC');
INSERT INTO public.dataset_access_level_table(dataset_id, parent_field, field, access_level) VALUES (1, 'beaconDataset', 'new_sensitive_field', 'REGISTERED');
INSERT INTO public.dataset_access_level_table(dataset_id, parent_field, field, access_level) VALUES (1, 'beaconDataset', 'new_field', 'NOT_SUPPORTED');
INSERT INTO public.dataset_access_level_table(dataset_id, parent_field, field, access_level) VALUES (2, 'accessLevelSummary', '-', 'CONTROLLED');
INSERT INTO public.dataset_access_level_table(dataset_id, parent_field, field, access_level) VALUES (2, 'datasetAlleleResponses', 'new_sensitive_field', 'CONTROLLED');
INSERT INTO public.dataset_access_level_table(dataset_id, parent_field, field, access_level) VALUES (2, 'beaconDataset', 'new_sensitive_field', 'REGISTERED');
INSERT INTO public.dataset_access_level_table(dataset_id, parent_field, field, access_level) VALUES (2, 'beaconDataset', 'new_field', 'NOT_SUPPORTED');
