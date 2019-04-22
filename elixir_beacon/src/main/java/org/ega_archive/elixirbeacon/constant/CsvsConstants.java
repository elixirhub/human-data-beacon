package org.ega_archive.elixirbeacon.constant;

import org.ega_archive.elixircore.ApplicationContextProvider;

public class CsvsConstants {

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
