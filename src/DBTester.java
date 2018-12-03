import java.sql.*;
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

    void viewGroups(Connection connection) throws SQLException {
        Statement stmt=null;
        ResultSet rst=null;

            stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            rst = stmt.executeQuery("SELECT * FROM itemgroup");
//            System.out.println("execute() returns "+
//                                stmt.execute("SELECT * FROM CUSTOMER"));
//        System.out.println(rst.last());
        int resulSetSize = rst.getRow();
        System.out.println(resulSetSize);
        System.out.println(stmt.getResultSet());
        System.out.println(rst.last());
        //System.out.println(rst.getString(0));
//        rst.beforeFirst();

        //System.out.println(rst.getClass());


    }
    void viewItems(Connection connection){

    }



}
