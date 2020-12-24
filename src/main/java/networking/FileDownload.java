package networking;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;


public class FileDownload {
	private final static String charset = StandardCharsets.UTF_8.name();
	private final static String referer = "http://openkg.cn/";
	private final static String targetUrl = "https://drive.google.com/file/d/0B8VUbXki5Q0ibEIzbkUxSnQ5Ulk/view?usp=sharing";

	// 获取文件名
	public static String getFileNameFromUrl(String url){  
	    String name = new Long(System.currentTimeMillis()).toString() + ".X";  
	    int index = url.lastIndexOf("/");  
	    if(index > 0){  
	        name = url.substring(index + 1);  
			int point = name.lastIndexOf(".");  
			if (point <= 0) {
				return "";
			}
	        if(name.trim().length()>0){  
	        	System.out.println("name: "+ name);
	            return name;  
	        }  
	    }  
	    System.out.println("name: "+ name);
	    return name;  
	}  
	
	
	/**
	 * 将InputStream写入本地文件
	 * @param destination 写入本地目录
	 * @param input	输入流
	 * @throws IOException
	 */
	public static void writeToLocal(String destination, InputStream input)
			throws IOException {
		File file = new File(destination);
        if (!file.getParentFile().exists()) {
            boolean result = file.getParentFile().mkdirs();
            if (!result) {
                System.out.println("创建失败");
            }
        }
		int index;
		byte[] bytes = new byte[1024];
		FileOutputStream downloadFile = new FileOutputStream(destination);
		while ((index = input.read(bytes)) != -1) {
			downloadFile.write(bytes, 0, index);
			downloadFile.flush();
		}
		downloadFile.close();
		input.close();
	}
	

	public static InputStream getHttpResponseInformation(String targetUrl) {
        URLConnection connection;
        try {
            connection = new URL(targetUrl).openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestProperty("Accept-Charset", charset);
            connection.setRequestProperty("Referer", referer);
            connection.setRequestProperty("User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            InputStream response = connection.getInputStream();
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
	}
	
	
}
