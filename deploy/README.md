# How to deploy

* [Deploy with Docker](#deploy-with-docker)
* [Deploy manually](#deploy-manually)

## Deploy with Docker

We create 2 images: one for the beacon, and one for a database, preloading some test data. 
Run the following command in the current directory:

	docker-compose build db
	docker-compose build beacon
	
You can now instanciate the images into 2 containers. The docker-compose file contains the settings to create the network to attach the containers to, and a volume to store the data for the database. We are ready to boot up a test system, with:

	docker-compose up -d
	
You are now ready to query the beacon on `localhost` (port 9075). For example:  

* [localhost:9075/elixirbeacon/v1/beacon/](http://localhost:9075/elixirbeacon/v1/beacon/)
* [localhost:9075/elixirbeacon/v1/beacon/query?referenceName=Y&start=2655179&referenceBases=G&alternateBases=A&assemblyId=GRCh37&datasetIds=1000genomes](http://localhost:9075/elixirbeacon/v1/beacon/query?referenceName=Y&start=2655179&referenceBases=G&alternateBases=A&assemblyId=GRCh37&datasetIds=1000genomes)
* [localhost:9075/elixirbeacon/v1/beacon/query?referenceName=Y&start=2655179&referenceBases=G&alternateBases=A&assemblyId=GRCh37&datasetIds=1000genomes&includeDatasetResponses=HIT](http://localhost:9075/elixirbeacon/v1/beacon/query?referenceName=Y&start=2655179&referenceBases=G&alternateBases=A&assemblyId=GRCh37&datasetIds=1000genomes&includeDatasetResponses=HIT)


The `-d` flag runs the containers _detached_, ie we get the prompt back. You can check the logs with:

	docker-compose logs -f

Tear down the system and remove the database volume, with:

	docker-compose down -v

## Deploy manually

This quick start guide uses the default configuration and sets the application up using some sample data. It requires a Postgres server running in the local machine and listening to the default port 5432.  
  
If you want to tune the configuration or load custom data, please, skip this section and keep reading.  

1. Create 2 databases and a new user (use *r783qjkldDsiu* as password)  
    ```  
    createuser -P microaccounts_dev  
    psql -h localhost -p 5432 -U postgres  
    ```  
    ```sql  
    CREATE DATABASE elixir_beacon_dev;  
    CREATE DATABASE elixir_beacon_testing;  
    GRANT ALL PRIVILEGES ON DATABASE elixir_beacon_dev TO microaccounts_dev;  
    GRANT ALL PRIVILEGES ON DATABASE elixir_beacon_testing TO microaccounts_dev;  
    ```  
2. Load the schema ([elixir_beacon_db_schema.sql](db/db/db_schema.sql))  
    ```  
    psql -h localhost -p 5432 -d elixir_beacon_dev -U microaccounts_dev < elixir_beacon_db_schema.sql  
    psql -h localhost -p 5432 -d elixir_beacon_testing -U microaccounts_dev < elixir_beacon_db_schema.sql  
    ```  
3. Load data 
    ```  
    psql -h localhost -p 5432 -d elixir_beacon_dev -U microaccounts_dev  
    ```  
    * Dataset:
        ```sql  
        INSERT INTO beacon_dataset_table(id, stable_id, description, access_type, reference_genome, variant_cnt, call_cnt, sample_cnt)  
          VALUES (1, '1000genomes', 'Subset of variants of chromosomes 22 and Y from the 1000 genomes project', 'PUBLIC', 'GRCh37', 3119, 8513330, 2504);
        -- Init dataset-ConsentCodes table
        INSERT INTO beacon_dataset_consent_code_table (dataset_id, consent_code_id , additional_constraint, version) 
          VALUES(1, 1, null, 'v1.0'); -- NRES - No restrictions on data use
        ```  
   * Variants: [1_chrY_subset.variants.csv](db/db/data/1_chrY_subset.variants.csv) and [1_chr21_subset.variants.csv](db/db/data/1_chr21_subset.variants.csv)
        ```  
        cat 1_chrY_subset.variants.csv | psql -h localhost -p 5432 -U microaccounts_dev -c \
            "copy beacon_data_table (dataset_id,chromosome,start,variant_id,reference,alternate,\"end\","type",sv_length,variant_cnt,call_cnt,sample_cnt,frequency,matching_sample_cnt) from stdin using delimiters ';' csv header" elixir_beacon_dev
        cat 1_chr21_subset.variants.csv | psql -h localhost -p 5432 -U microaccounts_dev -c \
            "copy beacon_data_table (dataset_id,chromosome,start,variant_id,reference,alternate,\"end\","type",sv_length,variant_cnt,call_cnt,sample_cnt,frequency,matching_sample_cnt) from stdin using delimiters ';' csv header" elixir_beacon_dev
        ```  
   * Sample list: [1_chrY_subset.samples.csv](db/db/data/1_chrY_subset.samples.csv)  and [1_chr21_subset.samples.csv](db/db/data/1_chr21_subset.samples.csv)
        * Load sample list into the DB you need a temporary table, `tmp_sample_table`:
            ```  
            cat 1_chrY_subset.samples.csv | psql -h localhost -p 5432 -U microaccounts_dev -c \
                "copy tmp_sample_table (sample_stable_id,dataset_id) from stdin using delimiters ';' csv header" elixir_beacon_dev
            cat 1_chr21_subset.samples.csv | psql -h localhost -p 5432 -U microaccounts_dev -c \
                "copy tmp_sample_table (sample_stable_id,dataset_id) from stdin using delimiters ';' csv header" elixir_beacon_dev
            ```  
        * Run this query to fill the final table `beacon_sample_table`:
            ```sql
            INSERT INTO beacon_sample_table (stable_id)
            SELECT DISTINCT t.sample_stable_id
            FROM tmp_sample_table t
            LEFT JOIN beacon_sample_table sam ON sam.stable_id=t.sample_stable_id
            WHERE sam.id IS NULL;
            ```
        * Run this query to fill the final linking table `beacon_dataset_sample_table`:
            ```sql
            INSERT INTO beacon_dataset_sample_table (dataset_id, sample_id)
            SELECT DISTINCT dat.id AS dataset_id, sam.id AS sample_id
            FROM tmp_sample_table t
            INNER JOIN beacon_sample_table sam ON sam.stable_id=t.sample_stable_id
            INNER JOIN beacon_dataset_table dat ON dat.id=t.dataset_id
            LEFT JOIN beacon_dataset_sample_table dat_sam ON dat_sam.dataset_id=dat.id AND dat_sam.sample_id=sam.id
            WHERE dat_sam.id IS NULL;
            ```
   * Samples where each variant is found: [1_chrY_subset.variants.matching.samples.csv](db/db/data/1_chrY_subset.variants.matching.samples.csv)  and [1_chr21_subset.variants.matching.samples.csv](db/db/data/1_chr21_subset.variants.matching.samples.csv)
       * Load samples by variant into the DB you need a temporary table, `tmp_data_sample_table`:
            ```  
            cat 1_chrY_subset.variants.matching.samples.csv | psql -h localhost -p 5432 -U microaccounts_dev -c \
                "copy tmp_data_sample_table (dataset_id,chromosome,start,variant_id,reference,alternate,"type",sample_ids) from stdin using delimiters ';' csv header" elixir_beacon_dev
            cat 1_chr21_subset.variants.matching.samples.csv | psql -h localhost -p 5432  -U microaccounts_dev -c \
                "copy tmp_data_sample_table (dataset_id,chromosome,start,variant_id,reference,alternate,"type",sample_ids) from stdin using delimiters ';' csv header" elixir_beacon_dev
            ```  
        * Run this query to fill the final linking table `beacon_data_sample_table`:
            ```sql
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
            ```
    * Truncate temporary tables, `tmp_sample_table` and `tmp_data_sample_table`:
        ```sql
        TRUNCATE TABLE tmp_sample_table;
        TRUNCATE TABLE tmp_data_sample_table;
        ```
5. Create the function ([elixir_beacon_function_summary_response.sql](db/db/db_functions.sql))  
    ```  
    psql -h localhost -p 5432 -d elixir_beacon_dev -U microaccounts_dev < elixir_beacon_function_summary_response.sql  
    psql -h localhost -p 5432 -d elixir_beacon_testing -U microaccounts_dev < elixir_beacon_function_summary_response.sql  
    ```  
6. Download the code  and swith branch
    ```  
    git clone https://github.com/ga4gh-beacon/beacon-elixir.git  
    cd beacon-elixir
    git checkout v1.1.0
    ```  
7. Prepare dependencies  
    ```  
    cd elixir_core  
    mvn clean compile jar:jar -Djar.finalName=elixir-core
    mvn install:install-file -Dfile=target/elixir-core.jar
    ```  
8. Compile and deploy the application  
    ```  
    cd ../elixir_beacon  
    mvn clean compile package -Dmaven.test.skip=true
    java -jar target/elixir-beacon-1.1.0-SNAPSHOT.jar --spring.profiles.active=dev  
    ```  
9. Go to   
    * [localhost:9075/elixirbeacon/v1/beacon/](http://localhost:9075/elixirbeacon/v1/beacon/)  
    * [localhost:9075/elixirbeacon/v1/beacon/query?referenceName=Y&start=2655179&referenceBases=G&alternateBases=A&assemblyId=GRCh37&datasetIds=1000genomes](http://localhost:9075/elixirbeacon/v1/beacon/query?referenceName=Y&start=2655179&referenceBases=G&alternateBases=A&assemblyId=GRCh37&datasetIds=1000genomes)  
    * [localhost:9075/elixirbeacon/v1/beacon/query?referenceName=Y&start=2655179&referenceBases=G&alternateBases=A&assemblyId=GRCh37&datasetIds=1000genomes&includeDatasetResponses=HIT](http://localhost:9075/elixirbeacon/v1/beacon/query?referenceName=Y&start=2655179&referenceBases=G&alternateBases=A&assemblyId=GRCh37&datasetIds=1000genomes&includeDatasetResponses=HIT)  

