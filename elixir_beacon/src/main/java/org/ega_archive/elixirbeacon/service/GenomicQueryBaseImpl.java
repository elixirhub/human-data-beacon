package org.ega_archive.elixirbeacon.service;

import org.apache.commons.lang.StringUtils;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpRequest;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixirbeacon.service.opencga.IncludeDatasetResponses;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public abstract class GenomicQueryBaseImpl implements GenomicQuery {

    private static List<String> validIncludeDatasetResponses = Arrays.asList(new String[]{"all", "hit", "miss", "null", "none"});

    @Override
    public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds, String alternateBases, String referenceBases, String chromosome, Integer start, String referenceGenome, String includeDatasetResponses, List<String> filters) {
        BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
        BeaconGenomicSnpRequest request = new BeaconGenomicSnpRequest();
        request.setAlternateBases(alternateBases);
        request.setReferenceBases(referenceBases);
        request.setReferenceName(chromosome);
        request.setStart(start);
        request.setAssemblyId(referenceGenome);
        request.setDatasetIds(datasetStableIds);
        request.setFilters(filters);
        response.setRequest(request);
        response.setError(checkParameters(datasetStableIds, alternateBases, referenceBases, chromosome, start, referenceGenome, includeDatasetResponses, filters));
        return response;
    }

    @Override
    public BeaconGenomicRegionResponse queryBeaconGenomicRegion(List<String> datasetStableIds, String referenceBases, String chromosome, Integer start, Integer end, String referenceGenome, String includeDatasetResponses, List<String> filters) {
        BeaconGenomicRegionResponse response = new BeaconGenomicRegionResponse();
        return response;
    }

    private Error checkParameters(List<String> datasetStableIds, String alternateBases, String referenceBases, String chromosome, Integer start, String referenceGenome, String includeDatasetResponses, List<String> filters) {
        if (!Pattern.matches("([ACGT]*)|N", alternateBases)) {
            Error error = new Error(ErrorCode.GENERIC_ERROR, "Invalid argument [alternateBases]: " + alternateBases);
            return error;
        } else if (!Pattern.matches("[ACGT]*", referenceBases)) {
            Error error = new Error(ErrorCode.GENERIC_ERROR, "Invalid argument [referenceBases]: " + referenceBases);
            return error;
        } else if (start < 0) {
            Error error = new Error(ErrorCode.GENERIC_ERROR, "Invalid argument [start]: " + start);
            return error;
        } else if (StringUtils.isNotEmpty(includeDatasetResponses) && !validIncludeDatasetResponses.contains(includeDatasetResponses)) {
            Error error = new Error(ErrorCode.GENERIC_ERROR, "Invalid argument [includeDatasetResponses]: " + includeDatasetResponses);
            return error;
        } else {
            return null;
        }
    }

    protected static IncludeDatasetResponses parseIncludeDatasetResponses(String value) throws IOException {
        if (Objects.isNull(value)) {
            return IncludeDatasetResponses.ALL;
        } else {
            switch(value.toLowerCase()) {
                case "all":
                    return IncludeDatasetResponses.ALL;
                case "hit":
                    return IncludeDatasetResponses.HIT;
                case "miss":
                    return IncludeDatasetResponses.MISS;
                case "null":
                    return IncludeDatasetResponses.NULL;
                case "none":
                    return IncludeDatasetResponses.NONE;
                default:
                    throw new IOException("invalid parameter: includeDatasetResponses");

            }
        }
    }
}
