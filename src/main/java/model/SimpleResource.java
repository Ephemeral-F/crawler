package model;

import java.io.Serializable;

public class SimpleResource implements Serializable {
    public String url;
    public String resourceId;
    public String datasetId;
    public SimpleResource(String url,String resourceId,String datasetId){
        this.url = url;
        this.resourceId = resourceId;
        this.datasetId = datasetId;
    }
}
