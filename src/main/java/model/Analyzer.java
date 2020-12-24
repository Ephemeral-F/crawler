package model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Analyzer extends Data {
    private Result[] results;
    private Source dataSource;

    private Portal portal;

    public Analyzer(Source dataSource, String info) {
        super(info);
        this.dataSource = dataSource;
        this.portal = null;
    }

    public Analyzer(Portal portal, String info) {
        super(info);
        this.portal = portal;
        this.dataSource = null;
    }

    public boolean isSuccess() {
        return !isDataNull() && data.getBoolean("success");
    }

    public Integer getCount() {
        return data.getJSONObject("result").getInt("count");
    }

    public String getSort() throws JSONException {
        String key = "sort";
        JSONObject resultJsonObject = data.getJSONObject("result");
        if (resultJsonObject.isNull(key)) {
            return null;
        } else {
            return resultJsonObject.getString(key);
        }
    }

    public Result[] getResults() {
        if (results == null) {
            if (data == null) {
                return null;
            }
            try {
                JSONArray resultsJsonArray = data.getJSONObject("result").getJSONArray("results");
                results = new Result[resultsJsonArray.length()];
                for (int i = 0; i < results.length; i++) {
                    if (this.portal != null) {
                        results[i] = new Result(portal, resultsJsonArray.getJSONObject(i));
                    } else {
                        results[i] = new Result(dataSource, resultsJsonArray.getJSONObject(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
                results = null;
            }
        }
        return results;
    }

    public static void main(String[] args) {
//        String info = CkanCrawler.getPackageSearchResponse(Source.OLD_DATAHUB, 2, 0);
//        Analyzer analyzer = new Analyzer(info);
//        System.out.println(analyzer.getResults()[0].getTags()[1].getName());
    }

}
