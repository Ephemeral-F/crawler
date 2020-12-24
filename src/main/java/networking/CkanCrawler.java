package networking;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import model.Portal;
import model.Source;

import org.json.JSONException;
import org.json.JSONObject;

public class CkanCrawler {
    private final static String charset = StandardCharsets.UTF_8.name();

    public static int getSourceTotalCount(Source source) throws JSONException {
        String info = getPackageSearchResponse(source, 0, 0);
        if (info == null) {
            return -1;
        }

        JSONObject object = new JSONObject(info);
        int count = object.getJSONObject("result").getInt("count");
        return count;
    }

    public static String getPackageSearchResponse(Source source, int rows, int start) {
        if (rows < 0 || start < 0) {
            return null;
        }
        try {
            String query = String.format("action/package_search?rows=%s&start=%s",
                    URLEncoder.encode(String.valueOf(rows), charset),
                    URLEncoder.encode(String.valueOf(start), charset));
            return getHttpResponseInformation(source.getHeader() + query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getPackageSearchResponse(Portal portal, int rows, int start, String parameter) {
        if (rows < 0 || start < 0) {
            return null;
        }
        try {
            String query = String.format(parameter,
                    URLEncoder.encode(String.valueOf(rows), charset),
                    URLEncoder.encode(String.valueOf(start), charset));
            System.out.println("url: " + portal.getApi_url() + query);
            return getHttpResponseInformation(portal.getApi_url() + query);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private static String getHttpResponseInformation(String targetUrl) {
//        URLConnection connection;
        HttpURLConnection connection;
        try {
            connection = (HttpURLConnection) new URL(targetUrl).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.88 Safari/537.36");
            String redirect = connection.getHeaderField("Location");
            if (redirect != null){
                connection = (HttpURLConnection) new URL(redirect).openConnection();
            }
            InputStream response = connection.getInputStream();
            Scanner scanner = new Scanner(response);
            String result = scanner.useDelimiter("\\A").next();
            scanner.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println("error in getHttpResponseInformation");
            return null;
        }
    }

    public static void main(String[] args) {
//        System.out.println(getPackageSearchResponse(Source.DATAGOV, 10, 0));
        System.out.println(getPackageSearchResponse(Source.OPENKG, 10, 0));
    }
}
