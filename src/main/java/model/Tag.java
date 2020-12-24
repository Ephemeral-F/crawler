package model;

import org.json.JSONException;
import org.json.JSONObject;

public class Tag extends Data {

    public Tag(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getVocabularyId() throws JSONException {
        return dataGetString("vocabulary_id");
    }

    public String getState() throws JSONException {
        return dataGetString("state");
    }

    public String getDisplayName() throws JSONException {
        return dataGetString("display_name");
    }

    public String getId() throws JSONException {
        return dataGetString("id");
    }

    public String getName() throws JSONException {
        return dataGetString("name");
    }

}
