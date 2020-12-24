package model;

import org.json.JSONException;
import org.json.JSONObject;

public class Data {
    JSONObject data;

    public Data(JSONObject jsonObject) {
        data = jsonObject;
    }

    public Data(String info) {
        if (info != null) {
            try {
                data = new JSONObject(info);
            } catch (Exception e) {
                e.printStackTrace();
                data = null;
            }

        }
    }

    public boolean isDataNull() {
        return data == null;
    }

    protected String dataGetString(String key) throws JSONException {
        // Debug
        assert (!isDataNull() && data.has(key));

        if (data.isNull(key)) {
            return null;
        } else {
            Object object = data.get(key);
            return object.toString();
//            return data.getString(key);
        }
    }

    protected Integer dataGetInt(String key) throws JSONException {
        // Debug
        assert (!isDataNull() && data.has(key));

        if (data.isNull(key)) {
            return null;
        } else {
            return data.getInt(key);
        }
    }

    protected Boolean dataGetBoolean(String key) throws JSONException {
        // Debug
        assert (!isDataNull() && data.has(key));

        if (data.isNull(key)) {
            return null;
        } else {
            return data.getBoolean(key);
        }
    }

}
