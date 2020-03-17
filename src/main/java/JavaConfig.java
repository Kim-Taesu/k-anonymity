public class JavaConfig {
    public static final String TAXONOMY_INFO_PATH = "data/raw/taxonomy.txt";
    public static final String RAW_DATA_PATH = "data/raw/sample.csv";
    public static final String TAXONOMY_CASES_PATH = "data/raw/taxonomy_cases.txt";

    public static final String JAVA_RESULT_DIR = "data/java/";
    public static final String EXTENSION = ".csv";

    public static final String DELIMITER = ",";
    public static final Integer K_VALUE = 1000;
    private static JavaConfig javaConfig = new JavaConfig();

    private JavaConfig() {

    }

    public static JavaConfig getInstance() {
        return javaConfig;
    }
}
