package model;

import org.json.JSONException;
import org.json.JSONObject;

public class Organization extends Data {

    public Organization(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getDescription() throws JSONException {
        return dataGetString("description");
    }

    public String getCreated() throws JSONException {
        return dataGetString("created");
    }

    public String getTitle() throws JSONException {
        return dataGetString("title");
    }

    public String getName() throws JSONException {
        return dataGetString("name");
    }

    public Boolean getIsOrganization() throws JSONException {
        return dataGetBoolean("is_organization");
    }

    public String getState() throws JSONException {
        return dataGetString("state");
    }

    public String getImageUrl() throws JSONException {
        return dataGetString("image_url");
    }

    public String getRevisionId() throws JSONException {
        return dataGetString("revision_id");
    }

    public String getType() throws JSONException {
        return dataGetString("type");
    }

    public String getId() throws JSONException {
        return dataGetString("id");
    }

    public String getApprovalStatus() throws JSONException {
        return dataGetString("approval_status");
    }
}
