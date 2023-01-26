package fi.fileuploader.test.container;


import org.testcontainers.containers.PostgreSQLContainer;

public final class TestDBContainer extends PostgreSQLContainer<TestDBContainer> {
    private static TestDBContainer INSTANCE;

    private static final String DB_NAME = "file-uploader-test";
    private static final String IMAGE_VERSION = "postgres:14.2";
    private static final String FSYNC_OFF_OPTION = "fsync=off";

    private TestDBContainer() {
        super(IMAGE_VERSION);
        this.withDatabaseName(DB_NAME);
        this.setCommand("postgres", "-c", FSYNC_OFF_OPTION);
        this.withInitScript("db/test_db_init.sql");
    }

    public static synchronized TestDBContainer getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TestDBContainer();
        }
        return INSTANCE;
    }

    @Override
    public void start() {
        super.start();

        System.setProperty("DB_URL", this.getJdbcUrl());
        // user file-uploader-application-test initialized in db/test_db_init.sql
        System.setProperty("DB_USERNAME", "file-uploader-application-test");
        System.setProperty("DB_PASSWORD", this.getPassword());
    }

    @Override
    public void stop() {
        // Override stop so JVM will handle shutdown
    }
}
