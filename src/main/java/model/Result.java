package model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Result extends Data {
    Resource[] resources;
    Tag[] tags;
    Organization organization;
    Extra[] extras;
    Source dataSource;
    Portal portal;

    public Result(Source dataSource, JSONObject jsonObject) {
        super(jsonObject);
        this.dataSource = dataSource;
        this.portal = null;
    }

    public Result(Portal portal, JSONObject jsonObject) {
        super(jsonObject);
        this.dataSource = null;
        this.portal = portal;
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

    public String getLicenseTitle() throws JSONException {
        return dataGetString("license_title");
    }

    public String getMaintainer() throws JSONException {
        return dataGetString("maintainer");
    }

//    public JSONArray getRelationshipsAsObject() {
//        return data.getJSONArray("relationships_as_object");
//    }

    public Boolean getPrivate() throws JSONException {
        return dataGetBoolean("private");
    }

    public String getMaintainerEmail() throws JSONException {
        return dataGetString("maintainer_email");
    }

    public Integer getNumTags() throws JSONException {
        return dataGetInt("num_tags");
    }

    public String getId() throws JSONException {
        return dataGetString("id");
    }

    public String getMetadataCreated() throws JSONException {
        return dataGetString("metadata_created");
    }

    public String getMetadataModified() throws JSONException {
        return dataGetString("metadata_modified");
    }

    public String getAuthor() throws JSONException {
        return dataGetString("author");
    }

    public String getAuthorEmail() throws JSONException {
        return dataGetString("author_email");
    }

    public String getState() throws JSONException {
        return dataGetString("state");
    }

    public String getVersion() throws JSONException {
        return dataGetString("version");
    }

    public String getCreatorUserId() throws JSONException {
        return dataGetString("creator_user_id");
    }

    public String getType() throws JSONException {
        return dataGetString("type");
    }

    public Resource[] getResources() throws JSONException {
        if (resources == null) {
            JSONArray resourceJsonArray = data.getJSONArray("resources");
            resources = new Resource[resourceJsonArray.length()];
            for (int i = 0; i < resources.length; i++) {
                if (this.portal != null) {
                    resources[i] = new Resource(portal, resourceJsonArray.getJSONObject(i));
                } else {
                    resources[i] = new Resource(dataSource, resourceJsonArray.getJSONObject(i));
                }
            }
        }
        return resources;
    }

    public Integer getNumResources() throws JSONException {
        return dataGetInt("num_resources");
    }

    public Tag[] getTags() throws JSONException {
        if (tags == null) {
            try {
                JSONArray tagJsonArray = data.getJSONArray("tags");
                tags = new Tag[tagJsonArray.length()];
                for (int i = 0; i < tags.length; i++) {
                    tags[i] = new Tag(tagJsonArray.getJSONObject(i));
                }
            } catch (JSONException e) {
//                e.printStackTrace();
            }
        }
        return tags;
    }

    public String getLicenseId() throws JSONException {
        return dataGetString("license_id");
    }

    public Organization getOrganization() throws JSONException {
        if (organization == null) {
            try {
                organization = new Organization(data.getJSONObject("organization"));
            } catch (Exception e) {
//                System.out.println("organization to JSON error,id is " + getId());
            }
        }
        return organization;
    }

    public String getName() throws JSONException {
        return dataGetString("name");
    }

    public Boolean getIsopen() throws JSONException {
        return dataGetBoolean("isopen");
    }

    public String getUrl() throws JSONException {
        return dataGetString("url");
    }

    public String getNotes() throws JSONException {
        return dataGetString("notes");
    }

    public String getOwnerOrg() throws JSONException {
        return dataGetString("owner_org");
    }

    public Extra[] getExtras() {
        if (extras == null) {
            try {
                JSONArray extraJsonArray = data.getJSONArray("extras");
                extras = new Extra[extraJsonArray.length()];
                for (int i = 0; i < extras.length; i++) {
                    extras[i] = new Extra(extraJsonArray.getJSONObject(i));
                }
            } catch (Exception e) {
            }
        }
        return extras;
    }

    public String getTitle() throws JSONException {
        return dataGetString("title");
    }

    public String getRevisionId() throws JSONException {
        return dataGetString("revision_id");
    }
}
