package model;

import org.json.JSONException;
import org.json.JSONObject;

public class Resource extends Data {

    Source dataSource;
    Portal portal;

    public Resource(Source dataSource, JSONObject jsonObject) {
        super(jsonObject);
        this.dataSource = dataSource;
        this.portal = null;
    }

    public Resource(Portal portal, JSONObject jsonObject) {
        super(jsonObject);
        this.portal = portal;
        this.dataSource = null;
    }

    public String getDataSource() {
        if (dataSource == null) {
            return null;
        } else {
            return dataSource.getName();
        }
    }

    public String getPortal() {
        if (portal == null) {
            return null;
        } else {
            return portal.getName();
        }
    }

    public String getMimetype() throws JSONException {
        return dataGetString("mimetype");
    }

    public String getCacheUrl() throws JSONException {
        return dataGetString("cache_url");
    }

    public String getHash() throws JSONException {
        return dataGetString("hash");
    }

    public String getDescription() throws JSONException {
        return dataGetString("description");
    }

    public String getName() throws JSONException {
        return dataGetString("name");
    }

    public String getFormat() throws JSONException {
        return dataGetString("format");
    }

    public String getUrl() throws JSONException {
        return dataGetString("url");
    }

    public String getDatafileDate() throws JSONException {
        return dataGetString("datafile_date");
    }

//    public Boolean getDatastoreActive() {
//        return dataGetBoolean("datastore_active");
//    }

    public String getCacheLastUpdated() throws JSONException {
        return dataGetString("cache_last_updated");
    }

    public String getPackageId() throws JSONException {
        return dataGetString("package_id");
    }

    public String getCreated() throws JSONException {
        return dataGetString("created");
    }

    public String getState() throws JSONException {
        return dataGetString("state");
    }

    public String getMimetypeInner() throws JSONException {
        return dataGetString("mimetype_inner");
    }

    public String getLastModified() throws JSONException {
        return dataGetString("last_modified");
    }

    public Integer getPosition() throws JSONException {
        return dataGetInt("position");
    }

    public String getRevisionId() throws JSONException {
        return dataGetString("revision_id");
    }

    public String getUrlType() throws JSONException {
        return dataGetString("url_type");
    }

    public String getId() throws JSONException {
        return dataGetString("id");
    }

    public String getResourceType() throws JSONException {
        return dataGetString("resource_type");
    }

    public String getSize(){
        String size = null;
        try {
             size = dataGetString("size");
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println(getId());
        }
        return size;
    }

}
