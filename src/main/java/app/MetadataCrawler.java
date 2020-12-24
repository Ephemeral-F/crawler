package app;

import database.DatabaseController;
import model.*;
import networking.CkanCrawler;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;


public class MetadataCrawler {

    private static final String ApiType = "CKAN";
    private static final String CkanParameter = "api/3/action/package_search?rows=%s&start=%s";

    public static void test() {
        Portal portal = new Portal("annuario.comune.fi.it", "http://annuario.comune.fi.it/", "http://annuario.comune.fi.it/");
        int pageNum = 100, reTry = 5;
        String countInfo = CkanCrawler.getPackageSearchResponse(portal, 1, 0, CkanParameter);
        while (countInfo == null) {
            countInfo = CkanCrawler.getPackageSearchResponse(portal, 1, 0, CkanParameter);
        }
        Analyzer countAnalyzer = new Analyzer(portal, countInfo);
        if (!countAnalyzer.isSuccess()) {
            System.out.println("get FAIL " + portal.getUrl());
        }
    }

    public static int getRest() {
        DatabaseController dbc = new DatabaseController("localhost","3306","root","mysql", "metadata");
        dbc.setCommit(false);
        Portal portal = new Portal("data.gov", "http://data.gov/", "http://catalog.data.gov/");
        int datasetNum=218659, pageNum = 100, reTry = 5;
        for (int i = 1735; i < datasetNum / pageNum; i++) {
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
//        for (Result result : results) {
//            boolean successWhenSave = dbc.saveDataset(portal, result);
//            if (!successWhenSave) {
//                System.out.println("Error when save "+result.getId());
//            }
//        }

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
//        Portal portal1 = new Portal("data.gov", "http://data.gov/", "http://catalog.data.gov/");
//        dbc.saveSuccessfulPortal(portal1, ApiType, CkanParameter, 218659);


        // select portals
        String stmt = "SELECT `name`, `url`, `api_url` " +
                "FROM odps " +
                "WHERE `status_code`=200 AND `api_type`='CKAN' AND (`name` not in (SELECT `name` FROM crawled)) " +
                "AND (`name` not in (SELECT `name` FROM failed))";
        List<Portal> portalList = dbc.getPortals(stmt);
        System.out.println("portal count:"+portalList.size());

        // get and save metadata
        for (Portal portal : portalList) {
            int count = getPortalMetadata(portal);
            if (count > 0) {
                dbc.saveSuccessfulPortal(portal, ApiType, CkanParameter, count);
            } else {
                dbc.saveFailedPortal(portal, ApiType);
            }
        }

        dbc.close();



//        int count = getRest();
//        DatabaseController dbc = new DatabaseController("localhost","3306","root","mysql","portals");
//        Portal portal = new Portal("dados.gov.br", "http://dados.gov.br", "http://dados.gov.br/");
//        dbc.savePortalInfo(portal, ApiType, CkanParameter, 10503);
//        dbc.close();

        test();



//        // The data source can be modified in model.Source
//        Source dataSource = Source.OPENKG;
//
//        // Total datasets num. The number can be found in pageOfNextLine-> "result" -> "count"
//        // https://old.datahub.io/api/3/action/package_search?rows=1&start=1
//        int datasetNum = 11;
//
//        // Get 100 datasets each time
//        int pageNum = 1;
//
////        for (int i = 0 / pageNum; i < datasetNum / pageNum; i++) {
//        for (int i = 0; i < datasetNum / pageNum; i++) {
//            System.out.printf("request from %d to %d%n", i * pageNum, i * pageNum + pageNum);
//            String info = CkanCrawler.getPackageSearchResponse(dataSource, pageNum, pageNum * i);
//            System.out.println("get");
//            if (info == null) {
//                System.out.println("info is null,reGet");
//                i--;
//                continue;
//            }
//
//            Analyzer analyzer = new Analyzer(dataSource, info);
//            Result[] results= analyzer.getResults();
//
//            for (Result result : results) {
//                boolean successWhenSave = dbc.saveDataset(result);
//                if (!successWhenSave) {
//                    System.out.println("Error when save "+result.getId());
//                }
//            }
//
//        }
//
//        dbc.close();
    }
}
