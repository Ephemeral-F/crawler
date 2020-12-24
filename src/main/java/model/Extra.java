package model;

import org.json.JSONException;
import org.json.JSONObject;

public class Extra extends Data {

    public Extra(JSONObject jsonObject) {
        super(jsonObject);
    }

    public String getKey() throws JSONException {
        return dataGetString("key");
    }

    public String getValue() throws JSONException {
//        return dataGetString("value");
        return String.valueOf(this.data.get("value"));
    }
}
