package model;

public class Portal {
    public String name;
    public String url;
    public String api_url;

    public Portal(String name, String url, String api_url) {
        this.name = name;
        this.url = url;
        this.api_url = api_url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getApi_url() {return api_url;}
}
