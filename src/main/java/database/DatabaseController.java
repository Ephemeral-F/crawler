package database;

import model.*;

import javax.sound.sampled.Port;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseController {
    private Connection connection;

    private final String sqlFilePath = "src/main/resources/portal-table.sql";

    public DatabaseController(String server, String port, String user, String password, String database) {
        try {
            connection = DriverManager.getConnection(
                    String.format("jdbc:mysql://%s:%s/%s?user=%s&password=%s&serverTimezone=UTC" +
                            "&sessionVariables=sql_mode='NO_ENGINE_SUBSTITUTION'&jdbcCompliantTruncation=false&rewriteBatchedStatements=true",
                            server, port, database, user, password));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setCommit(boolean b){
        //default is true
        try {
            connection.setAutoCommit(b);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("set auto commit error");
        }
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void close() throws SQLException {
        connection.close();
    }

    public void createPortalTable(String tableName) throws IOException, SQLException {
        File file = new File(sqlFilePath);
        FileReader fileReader = new FileReader(file);
        BufferedReader reader = new BufferedReader(fileReader);
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append('\n');
        }
        reader.close();
        String createTableSQL = "CREATE TABLE IF NOT EXISTS `" + tableName + "` " + sb.toString();
//        System.out.println(createTableSQL);
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(createTableSQL);
        stmt.close();
    }

    // optimized from 'public boolean saveDataset(Portal portal, Result dataset)'
    public boolean saveResults(Portal portal, Result[] results) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement( "INSERT INTO `"+ portal.getName() + "`(" +
                    "`mimetype`, `cache_url`, `hash`, `description`, `name`, `format`, `url`, `datafile_date`, " +
                    "`cache_last_updated`, `package_id`, `created`, `state`, `mimetype_inner`, `last_modified`, " +
                    "`position`, `revision_id`, `url_type`, `id`, `resource_type`, `size`, `license_title`, " +
                    "`maintainer`, `private`, `maintainer_email`, `dataset_id`, `metadata_created`, `metadata_modified`," +
                    " `author`, `author_email`, `dataset_state`, `version`, `creator_user_id`, `type`, `license_id`, " +
                    "`dataset_name`, `isopen`, `dataset_url`, `notes`, `owner_org`, `title`, `dataset_revision_id`, " +
                    "`org_description`, `org_created`, `org_title`, `org_name`, `org_is_organization`, `org_state`, " +
                    "`org_image_url`, `org_revision_id`, `org_type`, `org_id`, `org_approval_status`, `tags`, `extras`, " +
                    "`data_source`) "+
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            for (Result dataset : results) {
                statement.clearParameters();
                setDatasetParameters(portal, statement, dataset);
                Resource[] resources = dataset.getResources();
                if (resources == null) {
                    System.out.println("resources not found!");
                    continue;
                }
                setResourceParameters(statement, resources);
            }
            int[] arr = statement.executeBatch();//提交批处理
            connection.commit();//执行
            statement.close();
            int affectRowCount = arr.length;
            System.out.println("insert " + affectRowCount + " rows ");
        } catch (SQLException sqlExc) {
            sqlExc.printStackTrace();
            System.out.println(statement);
            return false;
        }
        return true;
    }

    private void setResourceParameters(PreparedStatement statement, Resource[] resources) throws SQLException {
        for (Resource resource : resources) {
            statement.setString(1, resource.getMimetype());
            statement.setString(2, resource.getCacheUrl());
            statement.setString(3, resource.getHash());
            statement.setString(4, resource.getDescription());
            statement.setString(5, resource.getName());
            statement.setString(6, resource.getFormat());
            statement.setString(7, resource.getUrl());
            statement.setString(8, resource.getDatafileDate());
            statement.setString(9, resource.getCacheLastUpdated());
            statement.setString(10, resource.getPackageId());
            statement.setString(11, resource.getCreated());
            statement.setString(12, resource.getState());
            statement.setString(13, resource.getMimetypeInner());
            statement.setString(14, resource.getLastModified());
            statement.setObject(15, resource.getPosition());
            statement.setString(16, resource.getRevisionId());
            statement.setString(17, resource.getUrlType());
            statement.setString(18, resource.getId());
            statement.setString(19, resource.getResourceType());
            statement.setString(20, resource.getSize());
//            statement.executeUpdate();
            statement.addBatch();
        }
    }

    private void setDatasetParameters(Portal portal, PreparedStatement statement, Result dataset) throws SQLException {
        statement.setString(21, dataset.getLicenseTitle());
        statement.setString(22, dataset.getMaintainer());
        statement.setObject(23, dataset.getPrivate());
        statement.setString(24, dataset.getMaintainerEmail());
        statement.setString(25, dataset.getId());
        statement.setString(26, dataset.getMetadataCreated());
        statement.setString(27, dataset.getMetadataModified());
        statement.setString(28, dataset.getAuthor());
        statement.setString(29, dataset.getAuthorEmail());
        statement.setString(30, dataset.getState());
        statement.setString(31, dataset.getVersion());
        statement.setString(32, dataset.getCreatorUserId());
        statement.setString(33, dataset.getType());
        statement.setString(34, dataset.getLicenseId());
        statement.setString(35, dataset.getName());
        statement.setObject(36, dataset.getIsopen());
        statement.setString(37, dataset.getUrl());
        statement.setString(38, dataset.getNotes());
        statement.setString(39, dataset.getOwnerOrg());
        statement.setString(40, dataset.getTitle());
        statement.setString(41, dataset.getRevisionId());
        Organization organization = dataset.getOrganization();
        if (organization != null) {
            statement.setString(42, organization.getDescription());
            statement.setString(43, organization.getCreated());
            statement.setString(44, organization.getTitle());
            statement.setString(45, organization.getName());
            statement.setObject(46, organization.getIsOrganization());
            statement.setString(47, organization.getState());
            statement.setString(48, organization.getImageUrl());
            statement.setString(49, organization.getRevisionId());
            statement.setString(50, organization.getType());
            statement.setString(51, organization.getId());
            statement.setString(52, organization.getApprovalStatus());
        } else {
            for (int i = 42; i<=52;i++){
                if (i == 46){
                    statement.setObject(i, null);
                } else {
                    statement.setString(i, null);
                }
            }
        }
        Tag[] tags = dataset.getTags();
        if (tags != null) {
            String tagText = "";
            for (Tag tag : tags) {
                tagText = tagText + tag.getName() + ";";
            }
            statement.setString(53, tagText);
        } else {
            statement.setString(53, null);
        }
        Extra[] extras = dataset.getExtras();
        if (extras != null) {
            String extraText = "";
            for (Extra extra : extras) {
                extraText = extraText + extra.getKey() + ":" + extra.getValue() + ";";
            }
            statement.setString(54, extraText);
        } else {
            statement.setString(54, null);
        }
        statement.setString(55, portal.getUrl());

    }

    public boolean saveDataset(Portal portal, Result dataset) {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement( "INSERT INTO `"+ portal.getName() + "`(" +
                    "`mimetype`, `cache_url`, `hash`, `description`, `name`, `format`, `url`, `datafile_date`, " +
                    "`cache_last_updated`, `package_id`, `created`, `state`, `mimetype_inner`, `last_modified`, " +
                    "`position`, `revision_id`, `url_type`, `id`, `resource_type`, `size`, `license_title`, " +
                    "`maintainer`, `private`, `maintainer_email`, `dataset_id`, `metadata_created`, `metadata_modified`," +
                    " `author`, `author_email`, `dataset_state`, `version`, `creator_user_id`, `type`, `license_id`, " +
                    "`dataset_name`, `isopen`, `dataset_url`, `notes`, `owner_org`, `title`, `dataset_revision_id`, " +
                    "`org_description`, `org_created`, `org_title`, `org_name`, `org_is_organization`, `org_state`, " +
                    "`org_image_url`, `org_revision_id`, `org_type`, `org_id`, `org_approval_status`, `tags`, `extras`, " +
                    "`data_source`) "+
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                    "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
            setDatasetParameters(portal, statement, dataset);

            Resource[] resources = dataset.getResources();
            if (resources == null) {
                System.out.println("resources not found!");
                statement.close();
                return false;
            }
            setResourceParameters(statement, resources);
            statement.close();
        } catch (SQLException sqlExc) {
            sqlExc.printStackTrace();
            return false;
        }
        return true;
    }


    public boolean saveDataset(Result dataset) {

        boolean finishPerfect = true;
        PreparedStatement statement = null;
        Organization organization = dataset.getOrganization();

        try {
            statement = connection.prepareStatement(
                    "INSERT INTO dataset VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            statement.setString(1, dataset.getLicenseTitle());
            statement.setString(2, dataset.getMaintainer());
            statement.setString(3, dataset.getMaintainerEmail());
            statement.setString(4, dataset.getId());
            statement.setString(5, dataset.getMetadataCreated());
            statement.setString(6, dataset.getMetadataModified());
            statement.setString(7, dataset.getAuthor());
            statement.setString(8, dataset.getAuthorEmail());
            statement.setString(9, dataset.getState());
            statement.setString(10, dataset.getVersion());
            statement.setString(11, dataset.getCreatorUserId());
            statement.setString(12, dataset.getType());
            statement.setString(13, dataset.getLicenseId());
            statement.setString(14, dataset.getName());
            statement.setString(15, dataset.getUrl());
            statement.setString(16, dataset.getNotes());
            statement.setString(17, dataset.getOwnerOrg());
            statement.setString(18, dataset.getTitle());
            statement.setString(19, dataset.getRevisionId());
            if (organization != null) {
                statement.setString(20, organization.getDescription());
                statement.setString(21, organization.getCreated());
                statement.setString(22, organization.getTitle());
                statement.setString(23, organization.getName());
                statement.setObject(24, organization.getIsOrganization());
                statement.setString(25, organization.getState());
                statement.setString(26, organization.getImageUrl());
                statement.setString(27, organization.getRevisionId());
                statement.setString(28, organization.getType());
                statement.setString(29, organization.getId());
                statement.setString(30, organization.getApprovalStatus());
            } else {

                for(int i = 20;i<=30;i++){
                    if(i==24){
                        statement.setObject(i, null);
                    }else{
                        statement.setString(i, null);
                    }
                }
            }
            statement.setObject(31, dataset.getPrivate());
            statement.setObject(32, dataset.getNumTags());
            statement.setObject(33, dataset.getNumResources());
            statement.setObject(34, dataset.getIsopen());
            statement.setString(35, dataset.getDataSource());

            statement.executeUpdate();
        } catch (SQLException e) {
//            e.printStackTrace();
            System.out.println(e.getMessage());
            finishPerfect = false;
//            try {
//                connection.rollback();
//            } catch (SQLException e1) {
//                throw new RuntimeException("Error when database rollback");
//            }

//            return false;
        } finally {
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                }
            }
        }

        Resource[] resources = dataset.getResources();
        for (Resource resource : resources) {
            try {
                statement = connection
                        .prepareStatement("INSERT INTO resource VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
                statement.setString(1, dataset.getId());
                statement.setString(2, resource.getMimetype());
                statement.setString(3, resource.getCacheUrl());
                statement.setString(4, resource.getHash());
                statement.setString(5, resource.getDescription());
                statement.setString(6, resource.getName());
                statement.setString(7, resource.getFormat());
                statement.setString(8, resource.getUrl());
                statement.setString(9, resource.getCacheLastUpdated());
                statement.setString(10, resource.getPackageId());
                statement.setString(11, resource.getCreated());
                statement.setString(12, resource.getState());
                statement.setString(13, resource.getMimetypeInner());
                statement.setString(14, resource.getLastModified());
                statement.setObject(15, resource.getPosition());
                statement.setString(16, resource.getRevisionId());
                statement.setString(17, resource.getUrlType());
                statement.setString(18, resource.getId());
                statement.setString(19, resource.getResourceType());
                statement.setObject(20, Boolean.FALSE);
                statement.setString(21, resource.getDataSource());
                statement.setObject(22, null);

                statement.executeUpdate();
            } catch (SQLException e) {
//                e.printStackTrace();
                System.out.println(e.getMessage());
                finishPerfect = false;
//                try {
//                    connection.rollback();
//                } catch (SQLException e1) {
//                    throw new RuntimeException("Error when database rollback");
//                }

//                return false;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }

        Tag[] tags = dataset.getTags();
        for (Tag tag : tags) {
            try {
                statement = connection.prepareStatement("INSERT INTO tag VALUES (?,?,?,?,?,?)");

                statement.setString(1, dataset.getId());
                statement.setString(2, tag.getVocabularyId());
                statement.setString(3, tag.getState());
                statement.setString(4, tag.getDisplayName());
                statement.setString(5, tag.getId());
                statement.setString(6, tag.getName());

                statement.executeUpdate();
            } catch (SQLException e) {
//                e.printStackTrace();
                System.out.println(e.getMessage());
                finishPerfect = false;
//                try {
//                    connection.rollback();
//                } catch (SQLException e1) {
//                    throw new RuntimeException("Error when database rollback");
//                }
//
//                return false;
            } finally {
                if (statement != null) {
                    try {
                        statement.close();
                    } catch (SQLException e) {
                    }
                }
            }
        }

//        try {
//            connection.commit();
//        } catch (SQLException e) {
//            throw new RuntimeException("Error when database commit");
//        }

        Extra[] extras = dataset.getExtras();
        if (extras != null) {
            Map<String, String> extraMap = new HashMap<>();
            for (Extra extra : extras) {
                extraMap.put(extra.getKey(), extra.getValue());
            }
            for (String key : extraMap.keySet()) {

                try {
                    statement = connection.prepareStatement("INSERT INTO extra VALUES (?,?,?)");

                    statement.setString(1, dataset.getId());
                    statement.setString(2, key);
//                    statement.setObject(3, extraMap.get(key));
                    statement.setString(3, extraMap.get(key));

                    statement.executeUpdate();
//                    connection.commit();
                } catch (SQLException e) {
//                    e.printStackTrace();
                    System.out.println(e.getMessage());
//                    System.out.println("Extra error");
                    finishPerfect = false;
//                    try {
//                        connection.rollback();
//                    } catch (SQLException e1) {
//                        throw new RuntimeException("Error when database rollback");
//                    }

//                    return false;
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }
        }
        return finishPerfect;

    }

    public void saveSuccessfulPortal(Portal portal, String apiType, String parameterKey, int returnCount) {
        try {
            String sql = "INSERT INTO `crawled`(`name`, `url`, `api_type`, `api_url`, `parameter_key`, `return_count`," +
                    " `crawled_date`) VALUES (?, ?, ?, ?, ?, ?, ?);";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, portal.getName());
            statement.setString(2, portal.getUrl());
            statement.setString(3, apiType);
            statement.setString(4, portal.getApi_url());
            statement.setString(5, parameterKey);
            statement.setInt(6, returnCount);
            statement.setDate(7,new java.sql.Date(new java.util.Date().getTime()));
            statement.executeUpdate();
            statement.close();
            System.out.println("save crawled table: " + portal.getName() + ", " + portal.getUrl());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("save crawled table error: " + portal.getName() + ", " + portal.getUrl());
        }
    }

    public void saveFailedPortal(Portal portal, String apiType) {
        try {
            String sql = "INSERT INTO `failed`(`name`, `url`, `api_url`, `api_type`) VALUES (?,?,?,?);";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, portal.getName());
            statement.setString(2, portal.getUrl());
            statement.setString(4, apiType);
            statement.setString(3, portal.getApi_url());
            statement.executeUpdate();
            statement.close();
            System.out.println("save failed table: " + portal.getName() + ", " + portal.getUrl());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            System.out.println("save failed table error: " + portal.getName() + ", " + portal.getUrl());
        }
    }

    public List<SimpleResource> getRdfResource(){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT url,dataset_id,id FROM resource WHERE format LIKE '%rdf%'");
            List<SimpleResource> list = new ArrayList<>();
            while (resultSet.next()){
                list.add(new SimpleResource(resultSet.getString(1),resultSet.getString(3),resultSet.getString(2)));
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //should not reach here
        return new ArrayList<SimpleResource>();
    }

    public List<SimpleResource> getOtherResource(){
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT url,dataset_id,id FROM resource r1 WHERE r1.format REGEXP CONCAT_WS('|','turtle','ttl','n3','nt') AND r1.dataset_id NOT IN\n" +
                    "(SELECT DISTINCT r2.dataset_id FROM resource r2 WHERE r2.format LIKE '%rdf%')");
            List<SimpleResource> list = new ArrayList<>();
            while (resultSet.next()){
                list.add(new SimpleResource(resultSet.getString(1),resultSet.getString(3),resultSet.getString(2)));
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //should not reach here
        return new ArrayList<SimpleResource>();
    }

    public List<Portal> getPortals(String stmt) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(stmt);
            List<Portal> list = new ArrayList<>();
            while (resultSet.next()){
                list.add(new Portal(resultSet.getString(1),resultSet.getString(2),resultSet.getString(3)));
            }
            return list;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        //should not reach here
        return new ArrayList<Portal>();
    }

}
