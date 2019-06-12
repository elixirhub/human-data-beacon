package org.ega_archive.elixirbeacon.constant;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;
import org.ega_archive.elixirbeacon.model.elixirbeacon.*;
import org.ega_archive.elixircore.ApplicationContextProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class CsvsConstants {

    //--------- INIT CONSTANTS DB -----------//
    private static String defaultOntologyTermsFileName = ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("ontology.terms.default.yaml.filename");
    private static String defaultDatasetAccessLevelFileName = ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("dataset.access.levels.default.yaml.filename");
    private static String defaultBeaconDatasetFileName =  ApplicationContextProvider.getApplicationContext().getEnvironment().getProperty("beacon.dateset.default.yaml.filename");

    public static List<OntologyTerm> CSVS_ONTOLOGY_TERMS = initConstants(defaultOntologyTermsFileName, OntologyTerm.class);
    public static List<OntologyTermColumnCorrespondance> CSVS_ONTOLOGY_TERM_COLUMN_CORRESPONDANCE = initConstants(defaultOntologyTermsFileName, OntologyTermColumnCorrespondance.class);
    public static List<DatasetAccessLevel> CSVS_DATASET_ACCESS_LEVEL = initConstants(defaultDatasetAccessLevelFileName, DatasetAccessLevel.class);
    public static List<BeaconDatasetConsentCode> CSVS_DATASET_CONSENT_CODE = new ArrayList<>();
    public static List<BeaconDataSummary> CSVS_DATA_SUMMARY = new ArrayList<>();
    public static  List<BeaconDataset> CSVS_BEACON_DATASET = initConstants(defaultBeaconDatasetFileName, BeaconDataset.class);

    private static <T> T initConstants(String fileName, Class<?> target)  {
        try {
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
            InputStream input = CsvsConstants.class.getClassLoader().getResourceAsStream(fileName);
            return objectMapper.readValue(input, objectMapper.getTypeFactory().constructCollectionType(List.class, Class.forName(target.getName())));

        } catch (IOException e) {
            log.error("Exception parsing yaml", e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
       }
        return null;
    }
    //--------- END CONSTANTS DB  -----------//



    public static final String CSVS_URL =
                (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
                        .getApplicationContext().getEnvironment().getProperty("csvs.url")
                        : "http://csvs.clinbioinfosspa.es:8080/csvs/rest";

    public static final String CSVS_URL_DOWNLOADS =
            (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
                    .getApplicationContext().getEnvironment().getProperty("csvs.url.donwloads")
                    : "http://csvs.clinbioinfosspa.es/downloads/";


    public static final int CSVS_LIMIT =
            (ApplicationContextProvider.getApplicationContext() != null) ? Integer.parseInt(ApplicationContextProvider
                    .getApplicationContext().getEnvironment().getProperty("csvs.limit"))
                    : 10;
    
    public static final int CSVS_BASED = 
            (ApplicationContextProvider.getApplicationContext() != null) ? Integer.parseInt(ApplicationContextProvider
                    .getApplicationContext().getEnvironment().getProperty("csvs.based"))
                    : 1;

    public static final String CSVS_ASSEMMBY_ID =
            (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
                    .getApplicationContext().getEnvironment().getProperty("csvs.assembly.id")
                    : "GRCh37";


    public static final String CELLBASE_SPECIES =
            (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
                    .getApplicationContext().getEnvironment().getProperty("cellbase.species")
                    : "hsapiens";


    public static final String CELLBASE_VERSION =
            (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
                    .getApplicationContext().getEnvironment().getProperty("cellbase.version")
                    : "v4";


    public static final String CELLBASE_URL =
            (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
                    .getApplicationContext().getEnvironment().getProperty("cellbase.url") +"/" + CELLBASE_VERSION + "/" + CELLBASE_SPECIES
                    : "http://cellbase.clinbioinfosspa.es/cb/webservices/rest/v4/hsapiens";


    public static final String dbSNP_URL_DATABASE =
            (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
                    .getApplicationContext().getEnvironment().getProperty("dbSNP.url.database")
                    : "https://www.ncbi.nlm.nih.gov/snp";

    public static final String dbSNP_URL_API =
            (ApplicationContextProvider.getApplicationContext() != null) ? ApplicationContextProvider
                    .getApplicationContext().getEnvironment().getProperty("dbSNP.url.api")
                    : "https://api.ncbi.nlm.nih.gov/variation/v0/beta/refsnp";


}
