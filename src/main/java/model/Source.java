package model;

public enum Source {
    OLD_DATAHUB("old_datahub", "https://old.datahub.io/api/3/"),
    DATAGOV("datagov", "https://catalog.data.gov/api/3/"),
    EUROPEAN_DATA_PORTAL("european_data_portal", "http://www.europeandataportal.eu/data/api/3/"),
    DATAGOVUK("datagov_uk", "https://data.gov.uk/api/"),
	OPENKG("openkg", "http://openkg.cn/api/3/");

    private String header;
    private String name;

    private Source(String name, String header) {
        this.name = name;
        this.header = header;
    }

    // The api
    public String getHeader() {
        return header;
    }

    //The name
    public String getName() {
        return name;
    }
}
