package app;

import database.DatabaseController;
import model.SimpleResource;
import networking.FileDownload;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;

public class ResourceDownload {
	
	private final static String dir = "d:/java/openkg/resource/";

	public static void main(String[] args) throws SQLException {
		System.out.println("begin to connect database");
		DatabaseController dbc = new DatabaseController("localhost","3306","root","mysql2020","openkg");
        if (!dbc.isConnected()) {
            throw new RuntimeException("connect error");

        }
        System.out.println("connect success");
        
        // get dataset_id, resource_id, url
        List<SimpleResource> list = dbc.getRdfResource();
        System.out.println("rdf resource: " + list.size());
        
        for (SimpleResource res : list) {
        	// 获取文件名，空串代表url无法直接下载
        	String filename = FileDownload.getFileNameFromUrl(res.url);
        	if (filename.equals("")) {
        		System.out.println("Invalid resource, dataset_id: " + res.datasetId 
        				+ ", resource_id: " + res.resourceId + res.resourceId + ", url: " + res.url);
        		continue;
        	}
        	InputStream input = FileDownload.getHttpResponseInformation(res.url);
        	if (input == null) {
        		System.out.println("http error, dataset_id: " + res.datasetId 
        				+ ", resource_id: " + res.resourceId + res.resourceId + ", url: " + res.url);
        		continue;
        	}
        	String destination = dir + res.datasetId + "/" + filename;
        	try {
        		FileDownload.writeToLocal(destination, input);
        		System.out.println("success");
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    			System.out.println("error, dataset_id: " + res.datasetId
    					+ ", resource_id: " + res.resourceId + ", url: " + res.url);
    		}
        }
        
        dbc.close();
		
	}
	
	
}
