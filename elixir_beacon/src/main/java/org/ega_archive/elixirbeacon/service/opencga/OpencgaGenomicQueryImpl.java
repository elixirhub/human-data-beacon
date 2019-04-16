package org.ega_archive.elixirbeacon.service.opencga;

import java.io.IOException;
import java.util.List;

import org.ega_archive.elixirbeacon.dto.BeaconGenomicRegionResponse;
import org.ega_archive.elixirbeacon.dto.BeaconGenomicSnpResponse;
import org.ega_archive.elixirbeacon.dto.Error;
import org.ega_archive.elixirbeacon.enums.ErrorCode;
import org.ega_archive.elixirbeacon.service.GenomicQuery;
import org.ega_archive.elixircore.event.sender.RestEventSender;
import org.mortbay.log.Log;
import org.opencb.commons.datastore.core.Query;
import org.opencb.commons.utils.ListUtils;
import org.opencb.opencga.client.exceptions.ClientException;
import org.opencb.opencga.client.rest.OpenCGAClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Primary
@Slf4j
@Service
public class OpencgaGenomicQueryImpl implements GenomicQuery {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestEventSender restEventSender;

    @Override
    public BeaconGenomicSnpResponse queryBeaconGenomicSnp(List<String> datasetStableIds, String alternateBases,
                                                          String referenceBases, String chromosome, Integer start, String referenceGenome,
                                                          String includeDatasetResponses, List<String> filters) {
        String genotypes = "1,0/1,1/1";
        Query query = new Query();
        query.put("id", chromosome + ":" + start.toString() + ":" + referenceBases + ":" + alternateBases);
        query.put("chromosome", chromosome);
        query.put("reference", referenceBases);
        query.put("alternate", alternateBases);
        query.put("includeGenotype", genotypes);

        try {
            return ListUtils.isEmpty(filters) ?
                    queryWithoutFilters(query, datasetStableIds, alternateBases, referenceBases, chromosome, start, referenceGenome, includeDatasetResponses)
                    : queryWithFilters(datasetStableIds, alternateBases, referenceBases, chromosome, start, referenceGenome, includeDatasetResponses, filters);
        } catch (IOException | ClientException e) {
            BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
            Error error = new Error();
            error.setErrorCode(ErrorCode.GENERIC_ERROR);
            error.setMessage(e.getMessage());
            response.setError(error);
            return response;
        }


    }

    @Override
    public BeaconGenomicRegionResponse queryBeaconGenomicRegion(List<String> datasetStableIds, String referenceBases,
                                                                String chromosome, Integer start, Integer end, String referenceGenome, String includeDatasetResponses,
                                                                List<String> filters) {
        // TODO Auto-generated method stub
        return null;
    }


    private BeaconGenomicSnpResponse queryWithoutFilters(Query query, List<String> datasetStableIds, String alternateBases,
                                                         String referenceBases, String chromosome, Integer start, String referenceGenome,
                                                         String includeDatasetResponses) throws IOException, ClientException {
        Log.info("\n>>> Query without filters\n");
        query.put("summary", true);
        OpenCGAClient opencga = OpencgaUtils.getClient();
        BeaconSnpVisitorWithoutFilter visitor = new BeaconSnpVisitorWithoutFilter(opencga, query);
        StudyVisitor wrapper = new VisitorByDatasetId(datasetStableIds, visitor);
        wrapper = new VisitorByAssembly(referenceGenome, wrapper);
        OpencgaUtils.visitStudies(wrapper, opencga);
        BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
        response.setDatasetAlleleResponses(visitor.getResults());
        return response;
    }

    private BeaconGenomicSnpResponse queryWithFilters(List<String> datasetStableIds, String alternateBases,
                                                      String referenceBases, String chromosome, Integer start, String referenceGenome,
                                                      String includeDatasetResponses, List<String> filters) throws IOException, ClientException {

        Log.info("\n>>> Query with filters\n");
        Filter filter = Filter.parse(filters);
        if (!filter.isValid()) {
            throw new IOException("invalid filter");
        } else {
            String genotypes = "1,0/1,1/1";
            Query query = new Query();
            query.put("id", chromosome + ":" + start.toString() + ":" + referenceBases + ":" + alternateBases);
            query.put("genotypes", genotypes);
            query.put("all", false);

            OpencgaEnrichedClient opencga = OpencgaUtils.getClient();
            BeaconSnpVisitorWithFilter visitor = new BeaconSnpVisitorWithFilter(opencga, query, filter);
            StudyVisitor wrapper = new VisitorByDatasetId(datasetStableIds, visitor);
            wrapper = new VisitorByAssembly(referenceGenome, wrapper);
            OpencgaUtils.visitStudies(wrapper, opencga);
            BeaconGenomicSnpResponse response = new BeaconGenomicSnpResponse();
            response.setDatasetAlleleResponses(visitor.getResults());
            return response;
        }
    }

}
