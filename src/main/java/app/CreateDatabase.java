package app;

import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.apache.ibatis.jdbc.ScriptRunner;

// DISCARD CLASS
public class CreateDatabase {
    public static void createDB(String server, String port, String user, String password, String database) {
        // TODO Auto-generated method stub
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //加载数据库驱动 这个路径下的静态代码会被执行

//            String url = "jdbc:mysql://localhost:3306?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC";//设置数据库的地址 设置编码  支持汉字
            String url = String.format("jdbc:mysql://%s:%s?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC", server, port);
            Connection conn = DriverManager.getConnection(url, user, password);//使用mysql驱动当中的连接数据库的API
            Statement stmt = conn.createStatement();

            String sql = "CREATE DATABASE " + database;
            stmt.executeUpdate(sql);
            stmt.close();

            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();//异常处理
        }
    }

    public static void createTable(String server, String port, String user, String password, String database, String sqlFilePath) {
        // TODO Auto-generated method stub
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); //加载数据库驱动 这个路径下的静态代码会被执行

//            String url = "jdbc:mysql://localhost:3306/"+name+"?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC";//设置数据库的地址 设置编码  支持汉字
            String url = String.format("jdbc:mysql://%s:%s/%s?useUnicode=true&characterEncoding=utf-8&serverTimezone=UTC", server, port, database);
            Connection conn = DriverManager.getConnection(url, user, password);//使用mysql驱动当中的连接数据库的API

            ScriptRunner runner = new ScriptRunner(conn);
            //下面配置不要随意更改，否则会出现各种问题
            runner.setAutoCommit(true);//自动提交
            runner.setFullLineDelimiter(false);
            runner.setDelimiter(";"); //每条命令间的分隔符
            runner.setSendFullScript(false);
            runner.setStopOnError(false);
            //	runner.setLogWriter(null);//设置是否输出日志
            //如果又多个sql文件，可以写多个runner.runScript(xxx),
            runner.runScript(new InputStreamReader(new FileInputStream(sqlFilePath),"utf-8"));

            conn.close();
        }
        catch(Exception e) {
            e.printStackTrace();//异常处理
        }

    }

    public static void main(String[] args) {

    }
}
