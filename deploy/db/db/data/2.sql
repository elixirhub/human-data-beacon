INSERT INTO public.beacon_sample_table (stable_id)
SELECT DISTINCT t.sample_stable_id
FROM public.tmp_sample_table t
LEFT JOIN public.beacon_sample_table sam ON sam.stable_id=t.sample_stable_id
WHERE sam.id IS NULL;
