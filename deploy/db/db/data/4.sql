<<<<<<< HEAD
INSERT INTO beacon_data_sample_table (data_id, sample_id)
select data_sam_unnested.data_id, s.id AS sample_id
from (
    select dt.id as data_id, unnest(t.sample_ids) AS sample_stable_id
    from tmp_data_sample_table t
    inner join beacon_data_table dt ON dt.dataset_id=t.dataset_id and dt.chromosome=t.chromosome
        and dt.variant_id=t.variant_id and dt.reference=t.reference and dt.alternate=t.alternate
        and dt.start=t.start and dt.type=t.type 
)data_sam_unnested
inner join beacon_sample_table s on s.stable_id=data_sam_unnested.sample_stable_id
left join beacon_data_sample_table ds ON ds.data_id=data_sam_unnested.data_id and ds.sample_id=s.id
where ds.data_id is null;
=======
INSERT INTO public.beacon_data_sample_table (data_id, sample_id)
select data_sam_unnested.data_id, s.id AS sample_id
from (
    select dt.id as data_id, unnest(t.sample_ids) AS sample_stable_id
    from public.tmp_data_sample_table t
    inner join public.beacon_data_table dt ON dt.dataset_id=t.dataset_id and dt.chromosome=t.chromosome
        and dt.variant_id=t.variant_id and dt.reference=t.reference and dt.alternate=t.alternate
        and dt.start=t.start and dt.type=t.type 
)data_sam_unnested
inner join public.beacon_sample_table s on s.stable_id=data_sam_unnested.sample_stable_id
left join public.beacon_data_sample_table ds ON ds.data_id=data_sam_unnested.data_id and ds.sample_id=s.id
where ds.data_id is null;

>>>>>>> v1.1.0_dev
