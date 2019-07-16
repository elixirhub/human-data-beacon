<<<<<<< HEAD
INSERT INTO beacon_dataset_sample_table (dataset_id, sample_id)
SELECT DISTINCT dat.id AS dataset_id, sam.id AS sample_id
FROM tmp_sample_table t
INNER JOIN beacon_sample_table sam ON sam.stable_id=t.sample_stable_id
INNER JOIN beacon_dataset_table dat ON dat.id=t.dataset_id
LEFT JOIN beacon_dataset_sample_table dat_sam ON dat_sam.dataset_id=dat.id AND dat_sam.sample_id=sam.id
WHERE dat_sam.id IS NULL;
=======
INSERT INTO public.beacon_dataset_sample_table (dataset_id, sample_id)
SELECT DISTINCT dat.id AS dataset_id, sam.id AS sample_id
FROM public.tmp_sample_table t
INNER JOIN public.beacon_sample_table sam ON sam.stable_id=t.sample_stable_id
INNER JOIN public.beacon_dataset_table dat ON dat.id=t.dataset_id
LEFT JOIN public.beacon_dataset_sample_table dat_sam ON dat_sam.dataset_id=dat.id AND dat_sam.sample_id=sam.id
WHERE dat_sam.id IS NULL;

>>>>>>> v1.1.0_dev
