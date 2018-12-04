import com.sun.xml.internal.bind.v2.model.core.ID;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;

public class DBTester {
    public Connection connectToDB () {
        String dbURL = "jdbc:derby://localhost:1527/DB"; //;create=true";
        String user = "admin";
        String password = "admin";
        Connection connection;
        try {
            connection = DriverManager.getConnection(dbURL, user, password);
            //System.out.println(connection);
            return connection;
        } catch (SQLException ex) {
            System.out.println("SQL exception - connectToDB(): " + ex.getMessage());
        }
        return null;
    }
    public void testConnection (Connection conn) {
        if (conn!=null) {
            System.out.println("Соединение с БД установлено!");
        }
        else {
            System.out.println("Что-то пошло не так!");
        }
    }
    public void doWork (Connection connection) throws SQLException {

    }

    public void closeConn(Connection connection) throws SQLException {
       connection.close();
    }

    public void viewGroups(Connection connection) throws SQLException {
        Statement stmt = null;
        ResultSet rst = null;
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rst = stmt.executeQuery("SELECT * FROM itemgroup");
//        for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++){
//            System.out.println(rst.getMetaData().getColumnName(i));
//        }
            while (rst.next()) {
              //  System.out.println(rst.getRow());
                System.out.println(rst.getString(2));
                //System.out.println(rst.getString(rst.getRow()));
//                for (int i=1; i<=rst.getMetaData().getColumnCount();i++)
//                {
//                    System.out.println(rst.getMetaData().getColumnName(i));
//                }

               // System.out.println(rst.getMetaData().getColumnCount());
            }
    }

    public void viewItems(Connection connection) throws SQLException {
        Statement stmt = null;
        ResultSet rst = null;
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rst = stmt.executeQuery("SELECT * FROM item");
//        for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++){
//            System.out.println(rst.getMetaData().getColumnName(i));
//        }
        while (rst.next()) {
            //  System.out.println(rst.getRow());
            System.out.println(rst.getString(2));
            //System.out.println(rst.getString(rst.getRow()));
//                for (int i=1; i<=rst.getMetaData().getColumnCount();i++)
//                {
//                    System.out.println(rst.getMetaData().getColumnName(i));
//                }

            // System.out.println(rst.getMetaData().getColumnCount());
        }
    }

    void viewItems2(Connection connection) throws SQLException {
        Statement stmt = null;
        ResultSet rst = null;
        ItemData itemdata = new ItemData();
        stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        rst = stmt.executeQuery("SELECT * FROM item");
        for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++){
            System.out.print(rst.getMetaData().getColumnName(i)+" ");
        }
        System.out.println();

        while (rst.next()) {
            for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++){
                //System.out.print(rst.getString(rst.getMetaData().getColumnLabel(i)) +" ");
               // System.out.println();
                itemdata.setItemMap(rst.getInt("id"),rst.getInt("groupid"), rst.getString("title"));
            }
           //System.out.println();
        }
        itemdata.getItemMap().entrySet().stream()
                .forEach(p-> System.out.println(p));
    }


    int getGroupID(String key, Connection connection) throws SQLException {
        int tmpInt=0;
        PreparedStatement stmt=null;
        ResultSet rst=null;
        final String sql = "select id from itemgroup where title =?"; //select id from itemgroup where title = 'ТЕЛЕВИЗОРЫ'
        stmt = connection.prepareStatement(sql);
        stmt.setString(1,key.toUpperCase());
        rst = stmt.executeQuery();
        while (rst.next()) {
            tmpInt = rst.getInt(1);
        }
        return tmpInt;
    }

    void viewItemsInGroup (int groupid, Connection connection) throws SQLException {
        PreparedStatement stmt=null;
        ResultSet rst=null;
        final String sql = "select it.* from itemgroup gr, item it where it.groupid = gr.id  and gr.id=?"; //select id from itemgroup where title = 'ТЕЛЕВИЗОРЫ'
        stmt = connection.prepareStatement(sql);
        stmt.setInt(1,groupid);
        rst = stmt.executeQuery();
        while (rst.next()){
            System.out.println(rst.getString(2));
        }

    }
    void viewItemsInGroup (String groupname, Connection connection) throws SQLException {
        PreparedStatement stmt=null;
        ResultSet rst=null;
        final String sql = "select it.* from itemgroup gr, item it where it.groupid = gr.id  and gr.title =?"; //select id from itemgroup where title = 'ТЕЛЕВИЗОРЫ'
        stmt = connection.prepareStatement(sql);
        stmt.setString(1,groupname.toUpperCase());
        rst = stmt.executeQuery();
        while (rst.next()){
            System.out.println(rst.getString(2));
        }

    }

    void addItemToGroup(String itemName, String groupName) {
        String sql1 = "insert into admin.item (groupid,title) values (6,'Xiaomi');";
        String sql2 = "insert into admin.itemgroup (title) values ('Планшеты');";
    }

    void removeItemFromGroup(String itemName, String groupName){

    }

}
