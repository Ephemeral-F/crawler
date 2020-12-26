package app;

import database.DatabaseController;
import model.*;
import networking.CkanCrawler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class MetadataCrawler {

    private static final String ApiType = "CKAN";
    private static final String CkanParameter = "api/3/action/package_search?rows=%s&start=%s";
    private static final String PythonFileRootPath = "E:\\python\\portal\\json\\";

    public static void test() {
        Portal portal = new Portal("annuario.comune.fi.it", "http://annuario.comune.fi.it/","http://annuario.comune.fi.it/");
//        DatabaseController dbc = new DatabaseController("localhost","3306","root","mysql", "metadata");
//        dbc.setCommit(false);
//        System.out.println("now get: " + portal.getName() + ", " + portal.getUrl());

        String filePath = "E:\\python\\portal\\json\\" + portal.getName();
        File file = new File(filePath);
        long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[(int) filelength];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] fileContentArr = new String(filecontent).split("\n");
        System.out.println(fileContentArr[3]);
//        int line = 1;
//        for (String text : fileContentArr) {
//            boolean isSuccess = analyzerInfo(portal, dbc, text);
//            if (!isSuccess) {
//                System.out.println("FAIL");
//                break;
//            }
//            System.out.printf("save line %d\n", line);
//            line++;
//        }
//        try {
//            dbc.close();
//        } catch (SQLException closeExc) {
//            closeExc.printStackTrace();
//        }
//        return fileContentArr;// 返回文件内容,默认编码
    }

    public static int getRest() {
        DatabaseController dbc = new DatabaseController("localhost","3306","root","mysql", "metadata");
        dbc.setCommit(false);
        Portal portal = new Portal("annuario.comune.fi.it", "http://annuario.comune.fi.it/","http://annuario.comune.fi.it/");
        System.out.println("now get: " + portal.getName() + ", " + portal.getUrl());

        // read lines from file
        String filePath = PythonFileRootPath + portal.getName();
        File file = new File(filePath);
        long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[(int) filelength];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] fileContentArr = new String(filecontent).split("\n");

        // analyzer json text
        Analyzer countAnalyzer = new Analyzer(portal, fileContentArr[0]);
        int count = countAnalyzer.getCount();
        for (int i = 1; i < fileContentArr.length; i++) {
            boolean isSuccess = analyzerInfo(portal, dbc, fileContentArr[i]);
            if (!isSuccess) {
                System.out.printf("FAIL at line %d\n", i+1);
                break;
            }
            System.out.printf("save line %d\n", i+1);
        }
        try {
            dbc.close();
        } catch (SQLException closeExc) {
            closeExc.printStackTrace();
        }
        return count;

    }

    public static int getMetadataFromFile(Portal portal, String rootPath) {
        DatabaseController dbc = new DatabaseController("localhost","3306","root","mysql", "metadata");
        dbc.setCommit(false);
        System.out.println("now get: " + portal.getName() + ", " + portal.getUrl());

        // read lines from file
        String filePath = rootPath + portal.getName();
        File file = new File(filePath);
        long filelength = file.length(); // 获取文件长度
        byte[] filecontent = new byte[(int) filelength];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(filecontent);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] fileContentArr = new String(filecontent).split("\n");

        // analyzer json text
        Analyzer countAnalyzer = new Analyzer(portal, fileContentArr[0]);
        int count = countAnalyzer.getCount();
        int line = 1;
        for (String text : fileContentArr) {
            boolean isSuccess = analyzerInfo(portal, dbc, text);
            if (!isSuccess) {
                System.out.printf("FAIL at line %d\n", line);
                break;
            }
            System.out.printf("save line %d\n", line);
            line++;
        }
        try {
            dbc.close();
        } catch (SQLException closeExc) {
            closeExc.printStackTrace();
        }
        return count;
    }

    public static int getPortalMetadata(Portal portal) {
        DatabaseController dbc = new DatabaseController("localhost","3306","root","mysql", "metadata");
        dbc.setCommit(false);
        System.out.println("now get: " + portal.getName() + ", " + portal.getUrl());
        try {
            // create table
            dbc.createPortalTable(portal.getName());
        } catch (SQLException | IOException createTableException) {
            createTableException.printStackTrace();
        }

        // get dataset count
        int pageNum = 100, reTry = 5;
        String countInfo = CkanCrawler.getPackageSearchResponse(portal, 1, 0, CkanParameter);
        while (countInfo == null && reTry > 0) {
            countInfo = CkanCrawler.getPackageSearchResponse(portal, 1, 0, CkanParameter);
            reTry--;
        }
        Analyzer countAnalyzer = new Analyzer(portal, countInfo);
        if (!countAnalyzer.isSuccess()) {
            System.out.println("get FAIL " + portal.getUrl());
            return 0;
        }
        int datasetNum = countAnalyzer.getCount();
        System.out.println("total datasets: " + datasetNum);
        // Get 100 datasets each time
        for (int i = 0; i < datasetNum / pageNum; i++) {
            System.out.printf("request from %d to %d ", i * pageNum, i * pageNum + pageNum);
            String info = CkanCrawler.getPackageSearchResponse(portal, pageNum, pageNum * i, CkanParameter);
            if (info == null) {
                System.out.println("info is null, reGet");
                i--;
                continue;
            }
            boolean isSuccess = analyzerInfo(portal, dbc, info);
            if (!isSuccess) {
                reTry--;
                if (reTry > 0) {
                    i--;
                } else {
                    System.out.printf("FAIL from %d to %d, skip\n", i * pageNum, i * pageNum + pageNum);
                    return 0;
                }
            } else {
                reTry = 5;
            }
        }

        int lastStart = datasetNum / pageNum * pageNum;
        System.out.printf("request from %d to %d ", lastStart, datasetNum);
        String info = null;
        while (info == null) {
            info = CkanCrawler.getPackageSearchResponse(portal, datasetNum-lastStart, lastStart, CkanParameter);
        }
        boolean isSuccess = analyzerInfo(portal, dbc, info);
        if (!isSuccess) {
            System.out.printf("FAIL from %d to %d, skip\n", lastStart, datasetNum);
        }

        try {
            dbc.close();
        } catch (SQLException closeExc) {
            closeExc.printStackTrace();
        }
        return datasetNum;
    }

    private static boolean analyzerInfo(Portal portal, DatabaseController dbc, String info) {
        Analyzer analyzer = new Analyzer(portal, info);
        Result[] results = analyzer.getResults();

        if (results == null) {
            System.out.println("get info error");
            return false;
        }

        boolean successWhenSave = dbc.saveResults(portal, results);
        if (!successWhenSave) {
            System.out.println("Save Error");
            return false;
        }

        return true;
    }

    public static void main(String[] args) throws SQLException {
        // connecting to database 'portals' and get portal list
        System.out.println("begin to connect database portals");
//        DatabaseController dbc = new DatabaseController("Host","3306","UserName","Password","DatabaseName");
        DatabaseController dbc = new DatabaseController("localhost","3306","root","mysql","portals");
        if (!dbc.isConnected()) {
            throw new RuntimeException("connect error");
        }
        System.out.println("connect success");


//        //get rest dataset
//        int count1 = getRest();
//        Portal portal1 = new Portal("data.london.gov.uk","http://data.london.gov.uk/","http://data.london.gov.uk/");
//        dbc.saveSuccessfulPortal(portal1, ApiType, CkanParameter, 11396);

//        // get from http
//        // select portals
//        String stmt = "SELECT `name`, `url`, `api_url` " +
//                "FROM odps " +
//                "WHERE `status_code`=200 AND `api_type`='CKAN' AND (`name` not in (SELECT `name` FROM crawled)) " +
//                "AND (`name` not in (SELECT `name` FROM failed))";
//        List<Portal> portalList = dbc.getPortals(stmt);
//        System.out.println("portal count:"+portalList.size());
//
//        // get and save metadata
//        for (Portal portal : portalList) {
//            int count = getPortalMetadata(portal);
//            if (count > 0) {
//                dbc.saveSuccessfulPortal(portal, ApiType, CkanParameter, count);
//            } else {
//                dbc.saveFailedPortal(portal, ApiType);
//            }
//        }


//        // get from file
//        // select portals
//        String stmt = "SELECT `name`, `url`, `api_url` FROM failed " +
//                "WHERE `save_file`=1 and `name` not in (SELECT `name` FROM crawled)";
//        List<Portal> portalList = dbc.getPortals(stmt);
//        System.out.println("portal count:"+portalList.size());
//
//        // get and save metadata
//        for (Portal portal : portalList) {
//            int count = getMetadataFromFile(portal, PythonFileRootPath);
//            if (count > 0) {
//                dbc.saveSuccessfulPortal(portal, ApiType, CkanParameter, count);
//            } else {
//                System.out.println("error in save " + portal.getName() + ", " + portal.getUrl());
//            }
//        }


        dbc.close();


//        test();

    }
}
