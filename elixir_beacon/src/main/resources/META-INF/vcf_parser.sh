#!/bin/bash


#############
# EXECUTION #
#############
# 2 arguments required: dataset ID and VCF file
# > /.vcf_parser dataset_id filename


################################
# LAODING OUTPUT FILES INTO DB #
################################
# 1) To load the file with the variants into the DB:
# > cat filename.variants.csv | psql -h host -p port -U username -c \
# "copy beacon_data_table (dataset_id,chromosome,start,variant_id,reference,alternate,\"end\","type",sv_length,variant_cnt,call_cnt,sample_cnt,frequency,matching_sample_cnt) from stdin using delimiters ';' csv header" elixir_beacon_dev

# 2) To load sample list into the DB you need a temporary table:
# > cat filename.samples.csv | psql -h host -p port -U username -c \
# "copy tmp_sample_table (sample_stable_id,dataset_id) from stdin using delimiters ';' csv header" elixir_beacon_dev

# Then run this query to fill the final table beacon_sample_table
# INSERT INTO beacon_sample_table (stable_id)
# SELECT DISTINCT t.sample_stable_id
# FROM tmp_sample_table t
# LEFT JOIN beacon_sample_table sam ON sam.stable_id=t.sample_stable_id
# WHERE sam.id IS NULL;

# Then run this query to fill the final linking table beacon_dataset_sample_table
# INSERT INTO beacon_dataset_sample_table (dataset_id, sample_id)
# select distinct dat.id AS dataset_id, sam.id AS sample_id
# from tmp_sample_table t
# inner join beacon_sample_table sam ON sam.stable_id=t.sample_stable_id
# inner join beacon_dataset_table dat ON dat.id=t.dataset_id
# LEFT JOIN beacon_dataset_sample_table dat_sam ON dat_sam.dataset_id=dat.id AND dat_sam.sample_id=sam.id
# WHERE dat_sam.id IS NULL;

# 3) To load matching samples per variant into the DB you need a temporary table:
# > cat filename.variants.matching.samples.csv | psql -h host -p port -U username -c \
# "copy tmp_data_sample_table (dataset_id,chromosome,start,variant_id,reference,alternate,"type",sample_ids) from stdin using delimiters ';' csv header" elixir_beacon_dev

# Then run this query to fill the final linking table beacon_data_sample_table
# INSERT INTO beacon_data_sample_table (data_id, sample_id)
# select data_sam_unnested.data_id, s.id AS sample_id
# from (
# 	select dt.id as data_id, unnest(t.sample_ids) AS sample_stable_id
# 	from tmp_data_sample_table t
# 	inner join beacon_data_table dt ON dt.dataset_id=t.dataset_id and dt.chromosome=t.chromosome
# 		and dt.variant_id=t.variant_id and dt.reference=t.reference and dt.alternate=t.alternate
# 		and dt.start=t.start and dt.type=t.type
# )data_sam_unnested
# inner join beacon_sample_table s on s.stable_id=data_sam_unnested.sample_stable_id
# left join beacon_data_sample_table ds ON ds.data_id=data_sam_unnested.data_id and ds.sample_id=s.id
# where ds.data_id is null;


#########
# NOTES #
#########
# Definitions from VCF specification #
# INFO - additional information:
# - AN : total number of alleles in called genotypes
# - AC : allele count in genotypes, for each ALT allele, in the same order as listed
# - NS : Number of samples with data
# - AF : allele frequency for each ALT allele in the same order as listed: use this when estimated from primary data, not called genotypes

# These fields correspond to these Beacon fields:
# AN -> callCount
# AC -> variantCount
# AF -> frequency
# (NS cannot be used for sampleCount because the latter is the number of samples with the matching allele)


VCF_FILENAME=$(basename "$2")
VCF_FILENAME="${VCF_FILENAME%.*}"

echo 'Dataset Id: ' $1
echo 'Parsing file:' $VCF_FILENAME

OUTPUT_FILENAME=$1'_'$VCF_FILENAME'.variants.csv'
OUTPUT_SAMPLES_FILENAME=$1'_'$VCF_FILENAME'.samples.csv'
OUTPUT_MATCHING_SAMPLES_FILENAME=$1'_'$VCF_FILENAME'.variants.matching.samples.csv'


########################################
# 				Parsing VCF 		   #
########################################
echo "Parsing meta-information..."

while IFS='' read -r line;
do
	#Parse some fields in the header section to know if there is one value per alternate allele (Number=A)
	if [[ $line == \#\#INFO*=AC*Number=A* ]]
	then
		HEADER_AC=1;
	fi
	if [[ $line == \#\#INFO*=AN*Number=A* ]]
	then
		HEADER_AN=1;
	fi
	if [[ $line == \#\#INFO*=AF*Number=A* ]]
	then
		HEADER_AF=1;
	fi
	if [[ $line == \#\#INFO*=NS*Number=A* ]]
	then
		HEADER_NS=1;
	fi
	if [[ $line == \#\#INFO*=END*Number=A* ]]
	then
		HEADER_END=1;
	fi
	if [[ $line == \#\#INFO*=SVLEN*Number=A* ]]
	then
		HEADER_SVLEN=1;
	fi
	if [[ $line == \#\#INFO*=SVTYPE*Number=A* ]]
	then
		HEADER_SVTYPE=1;
	fi
	if [[ $line == \#\#INFO*=TYPE*Number=A* ]]
	then
		HEADER_TYPE=1;
	fi
	if [[ $line == \#CHROM* ]]
	then
		sample_list=($(echo $line | cut -d$' ' -f10- | awk -F "" -v ds=$1 '
		{
			numFields=split($0,fields," ");
			for(i=1;i<=numFields;i++) {
				printf("%s,",fields[i])
			}
		}'))
	fi
	if [[ $line != \#* ]]
	then
		break;
	fi
done < "$2"
#NOTE: Extracting this data using a while loop is much faster than using grep (to filter rows beginning with #) and awk
########################################
########################################

TOTAL_SAMPLES=`echo "$sample_list" | grep -o "," | wc -l`
echo "Total samples: $TOTAL_SAMPLES"

# Write headers
echo "datasetId;chromosome;position;variantId;reference;alternate;end;svType;svLength;variantCount;callCount;sampleCount;frequency;sampleMatchingCount" > $OUTPUT_FILENAME
echo "datasetId;chromosome;position;variantId;reference;alternate;svType;sampleId" > $OUTPUT_MATCHING_SAMPLES_FILENAME
echo "sampleId;datasetId" > $OUTPUT_SAMPLES_FILENAME

echo "Parsing VCF..."
#Column number in VCF file (#parameter index in awk function): 1=chrom (#1), 2=pos (#2), 3=ID (#3), 4=ref (#4), 5=alt (#5), 7=filter (#6), 8=info(#7), 9=format(#8)
#This awk script also parses some keys inside the INFO field. Some of these keys may contain more than one value
#separated with commas if there is more than one alternate allele. In order to know this, the meta-information has been already scanned.
grep -v "^#" $2 | cut -f1,2,3,4,5,7,8,9- | awk -v ds=$1 -v header_ac=$HEADER_AC -v header_an=$HEADER_AN -v header_ns=$HEADER_NS -v header_af=$HEADER_AF \
	-v header_end=$HEADER_END -v header_svlen=$HEADER_SVLEN -v header_svtype=$HEADER_SVTYPE -v header_type=$HEADER_TYPE \
	-v snps_file=$OUTPUT_FILENAME -v samples_file=$OUTPUT_MATCHING_SAMPLES_FILENAME -v total_samples=$TOTAL_SAMPLES -v sample_list=$sample_list '
{
	if ( $6 == "PASS" || $6 == "InDel" || $6 == "." ) {

		numIdField=split($3,idField,";")
		numAltField=split($5,altField,",")
		numInfoField=split($7, infoField, ";")

		# Remember that the split() function returns the values starting with index 1
		acTokens[1]=null
		anTokens[1]=null
		afTokens[1]=null
		nsTokens[1]=null
		variantTokens[1]=null
		callTokens[1]=null
		sampleTokens[1]=null
		frequencyTokens[1]=null
		fieldEndTokens[1]=null
		endTokens[1]=null
		svLenTokens[1]=null
		svLengthTokens[1]=null
		structuralVariantTypeTokens[1]=null
		svTypeTokens[1]=null

		#print "Processing columns 1st (chrom) to 6th (filter)..."

		for(i = 1; i <= numInfoField; i++) {
			if(infoField[i] ~ /^[[:blank:]]*AC=/) {
				split(infoField[i], acTokens, "=")

				if(header_ac == 1) {
					split(acTokens[2], variantTokens, ",")
				} else {
					variantTokens[1]=acTokens[2]
				}
				continue
			}
			if(infoField[i] ~ /^[[:blank:]]*AN=/) {
				split(infoField[i], anTokens, "=")

				if(header_an == 1) {
					split(anTokens[2], callTokens, ",")
				} else {
					callTokens[1]=anTokens[2]
				}
				continue
			}
			if(infoField[i] ~ /^[[:blank:]]*NS=/) {
				split(infoField[i], nsTokens, "=")

				if(header_ns == 1) {
					split(nsTokens[2], sampleTokens, ",")
				} else {
					sampleTokens[1]=nsTokens[2]
				}
				continue
			}

			if(infoField[i] ~ /^[[:blank:]]*AF=/) {
				split(infoField[i], afTokens, "=")

				if(header_af == 1) {
					split(afTokens[2], afTokens, ",")
				} else {
					afTokens[1]=afTokens[2]
				}
				continue
			}
			if(infoField[i] ~ /^[[:blank:]]*END=/) {
				split(infoField[i], fieldEndTokens, "=")

				if(header_end == 1) {
					split(fieldEndTokens[2], endTokens, ",")
				} else {
					endTokens[1]=fieldEndTokens[2]
				}
				continue
			}
			if(infoField[i] ~ /^[[:blank:]]*SVLEN=/) {
				split(infoField[i], svLenTokens, "=")

				if(header_svlen == 1) {
					split(svLenTokens[2], svLengthTokens, ",")
				} else {
					svLengthTokens[1]=svLenTokens[2]
				}
				continue
			}
			if(infoField[i] ~ /^[[:blank:]]*INDEL/) {
				structuralVariantTypeTokens[1] = "INDEL"
				continue
			}
			if(infoField[i] ~ /^[[:blank:]]*SVTYPE=/ || infoField[i] ~ /^[[:blank:]]*TYPE=/) {
				split(infoField[i], svTypeTokens, "=")

				if(infoField[i] ~ /SVTYPE=/ && header_svtype == 1 \
					|| infoField[i] ~ /TYPE=/ && header_type == 1) {
					split(svTypeTokens[2], structuralVariantTypeTokens, ",")
				} else {
					structuralVariantTypeTokens[1]=svTypeTokens[2]
				}
				continue
			}
		} # end for

		#print "Calculating AC, AN and AF..."

		# Check if genotype information is present in this file
		if (length(sample_list) != 0) {
			# Manual calculation of AN, AF, AC
			# This also saves the samples where the variant is found in whichSampleMatching
			countAN = 0
			countAF[1] = 0
			countAC[1] = 0
			samplesMatching[1] = 0
			whichSampleMatching[1","1] = 0
			# Init arrays
			for (j = 1; j <= numAltField; j++) {
				countAF[j]=0;
				countAC[j]=0;
				samplesMatching[j]=0;
			}
			for (j = 1; j <= numAltField; j++) {
				k = 1
				for (i = 9; i <= NF; i++) {
					# Some examples of codification of GT field:
					# 0|1
					# 1/1:160,45,0,146,19,143:16:0:87
					# 0
					# NA|NA

					found = index($i, ":")
					if (found==0) {
						found = match($i, /[0-9]+(\||\/)[0-9]+|[0-9]+/)
						if (found==0) {
							#print "No genotype info: "$i
							continue
						} else {
							# Cut the string
							search = substr($i, found, RLENGTH)
						}
					} else {
						# Cut the string and do not include the ":"
						search = substr($i, 1, found-1)
					}
					#print "2nd search="search

					# Make a copy
					search2 = search

					matches = gsub(j,"",search)
					if (matches > 0) {
						samplesMatching[j] = samplesMatching[j] + 1
						countAC[j]=countAC[j] + matches;
						whichSampleMatching[j","k] = 1;
					} else {
						whichSampleMatching[j","k] = 0;
					}
					if (j == 1) {
						# Calculate AN only once
						matches = gsub(/[0-9]+/,"",search2)
						countAN = countAN + matches
					}
					k++
				}
			}
			if (countAN != 0) {
				for (j = 1; j <= numAltField; j++) {
					#print "countAC[j]="countAC[j]", countAN="countAN
					# Calculate AF: AF = AC/AN
					countAF[j] = countAC[j]/countAN
				}
			}

			#print "Splitting sample list into an array..."

			# Split the sample_list variable which contains the sample IDs by comma (,)
			split(sample_list,sample_array,",");
		}

		#print "Processing information in INFO fields..."

		for( i = 1; i <= numAltField; i++ ) {
			position=null
			variantId=null
			alt=null
			end=null
			type=null
			svLen=null
			variantCount=null
			callCount=null
			sampleCount=null
			frequency=null

			if(header_ac == 1) {
				variantCount=variantTokens[i]
			} else {
				variantCount=variantTokens[1]
			}

			if(header_an == 1) {
				callCount=callTokens[i]
			} else {
				callCount=callTokens[1]
			}

			if(header_ns == 1) {
				sampleCount=sampleTokens[i]
			} else {
				sampleCount=sampleTokens[1]
			}

			if(header_af == 1) {
				frequency=afTokens[i]
			} else {
				frequency=afTokens[1]
			}

			if(header_end == 1) {
				end=endTokens[i]
			} else {
				end=endTokens[1]
			}

			if(header_svlen == 1) {
				svLen=svLengthTokens[i]
			} else {
				svLen=svLengthTokens[1]
			}

			if(header_svtype == 1 || header_type == 1) {
				type=toupper(structuralVariantTypeTokens[i])
			} else {
				type=toupper(structuralVariantTypeTokens[1])
			}
			# As VCF is quite flexible, sometimes we can find ALT=<INS:ME:ALU> and SVTYPE=ALU but we want to capture that it is an INS:ME
			if(altField[i] ~ /<INS:ME/) {
				type="INS:ME"
			} else if(altField[i] ~ /<DEL:ME/) {
				type="DEL:ME"
			} else if(altField[i] ~ /<DUP:TANDEM/) {
				type="DUP:TANDEM"
			}

			alt=altField[i]
			if (numIdField > 1) {
				variantId=idField[i]
			} else {
				variantId = $3
			}

			position = ($2-1)
			if(end != null) {
				end = end-1

			}
			if(type == "DEL" || type == "DUP" || type == "CNV" || type == "INS:ME" || type == "INS:ME" || type == "INS:ME") {
				if(end == null) {
					# Calculate "end" value
					if (svLen != null) {
						end = position + (svLen < 0 ? -svLen : svLen)
					} else {
						end = position + (length($4) - length(alt))
					}
				}
				if (svLen == null) {
					# Calculate "svLength"
					svLen = position - end
				}
			}
			if(type == null) {
				type = "SNP"
			}
			if ((variantCount == null || variantCount == 0) && countAC[i] != 0) {
				variantCount = countAC[i];
			}
			if ((callCount == null || callCount == 0) && countAN != 0) {
				callCount = countAN;
			}
			if ((frequency == null || frequency == 0) && countAF[i] != 0) {
				frequency = countAF[i];
			}
			if ((sampleCount == null || sampleCount == 0) && total_samples != 0) {
				sampleCount = total_samples;
			}
			print(ds";"$1";"position";"variantId";"$4";"alt";"end";"type";"svLen";"variantCount";"callCount";"sampleCount";"frequency";"samplesMatching[i]) >> snps_file

			#print("sampleCount= ", sampleCount)
			printf "%s;%s;%s;%s;%s;%s;%s;{",ds,$1,position,variantId,$4,alt,type >> samples_file
			printed = 0;
			for(k = 1; k <= sampleCount; k++) {
				if (whichSampleMatching[i","k] == 1) {
					if (printed) {
						printf "," >> samples_file
					}
					printf "%s", sample_array[k] >> samples_file
					printed = 1;
				}
			}
			# Insert newline
			print "}" >> samples_file
		} # end for
	} # end if
}' #> $OUTPUT_FILENAME
########################################

#Output format: datasetId;chromosome;position;variantId;referenceBases;alternateBases;end;structuralVariantType (e.g. DUP, DEL, INDEL, CNV,...);svLength;variantCount;callCount;sampleCount;frequency;sampleMatchingCount
echo "Output file with variants: $OUTPUT_FILENAME"

# This file will list for each of the variants, the list of samples having it
# Output format: datasetId;chromosome;position;variantId;reference;alternate;svType;sampleId
echo "Output file with matching samples per variant: $OUTPUT_MATCHING_SAMPLES_FILENAME"

echo "$sample_list" | sed "s/,/;$1\n/g" | sed -r '/^\s*$/d' >> "$OUTPUT_SAMPLES_FILENAME"
# This file contains the list of samples in the VCF
# Output format: dataset_id;sample_id
echo "Output file with sample list: $OUTPUT_SAMPLES_FILENAME"

########################################
########################################
