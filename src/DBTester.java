import com.sun.xml.internal.bind.v2.model.core.ID;
import org.apache.derby.client.am.SqlException;

import java.io.*;
import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBTester {
    HashSet<String> addGroups = new HashSet<String>();
    HashSet<String> removeGroups = new HashSet<String>();

    public Connection connectToDB() {
        String dbURL = "jdbc:derby://localhost:1527/DB"; //;create=true";
        String user = "admin";
        String password = "admin";
        Connection connection;
        try {
            connection = DriverManager.getConnection(dbURL, user, password);
            return connection;
        } catch (SQLException ex) {
            System.out.println("SQL exception - connectToDB(): " + ex.getMessage());
        }
        return null;
    }

    public void testConnection(Connection conn) {
        if (conn != null) {
            System.out.println("Соединение с БД установлено!");
        } else {
            System.out.println("Что-то пошло не так!");
        }
    }

    public void closeConn(Connection connection) throws SQLException {
        connection.close();
    }

    public void viewGroups(Connection connection) throws SQLException {
        try(
        Statement stmt = connection.createStatement();
        ResultSet rst = stmt.executeQuery("SELECT * FROM itemgroup");
        ) {
            ResultSetMetaData rsmd = rst.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                System.out.print(rsmd.getColumnName(i));
                System.out.print("\t");
            }
            System.out.println();
            while (rst.next()) {
                System.out.print(rst.getRow());
                System.out.print("\t");
                System.out.println(rst.getString(2));
            }
        }
    }

    public void viewItems(Connection connection) throws SQLException {
        try(
                Statement stmt = connection.createStatement();
                ResultSet rst = stmt.executeQuery("SELECT * FROM item");
        )
        {
            for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++) {
                System.out.print(rst.getMetaData().getColumnName(i));
                System.out.print("\t");
            }
            System.out.println();
            while (rst.next()) {
                System.out.print(rst.getRow());
                System.out.print("\t");
                System.out.print(rst.getString(2));
                System.out.print("\t");
                System.out.println(rst.getInt(3));
            }
        }
    }

    int getGroupID(String key, Connection connection) throws SQLException {
        try (
                PreparedStatement stmt = connection.prepareStatement("select id from itemgroup where title =?");
        ) {
            int GroupID = 0;
            stmt.setString(1, key.toUpperCase());
            ResultSet rst = stmt.executeQuery();
            if (rst.next()) {
                GroupID = rst.getInt(1);
            }
            return GroupID;
        }
    }

    void viewItemsInGroup(int groupid, Connection connection) throws SQLException {
        try (
                PreparedStatement stmt = connection.prepareStatement("select it.groupid, it.title from item it where it.groupid=?");
        ) {
//        PreparedStatement stmt = null;
//        ResultSet rst = null;
//        String sql = "select it.groupid, it.title from item it where it.groupid=?"; //select id from itemgroup where title = 'ТЕЛЕВИЗОРЫ'
//        stmt = connection.prepareStatement(sql);
            stmt.setInt(1, groupid);
            ResultSet rst = stmt.executeQuery();
            while (rst.next()) {
                System.out.println(rst.getString(2));
            }
        }
    }


    void viewItemsInGroup(String groupname, Connection connection) throws SQLException {
        try (
                PreparedStatement stmt = connection.prepareStatement("select it.* from itemgroup gr left join item it on it.groupid = gr.id  where gr.title =?");
        ) {
//        PreparedStatement stmt = null;
//        ResultSet rst = null;
//        String sql = "select it.* from itemgroup gr left join item it on it.groupid = gr.id  and gr.title =?"; //select id from itemgroup where title = 'ТЕЛЕВИЗОРЫ'
//        stmt = connection.prepareStatement(sql);
            stmt.setString(1, groupname.toUpperCase());
            ResultSet rst = stmt.executeQuery();
            while (rst.next()) {
                System.out.println(rst.getString(2));
            }
        }
    }

    int getAndAddGroup(String groupName, Connection connection) throws SQLException {
        try (
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO ADMIN.ITEMGROUP(TITLE) VALUES(?)");
        ) {
            int idgroup = 0;
//            PreparedStatement stmt = null;
//            ResultSet rst = null;
            idgroup = getGroupID(groupName, connection);
            if (idgroup == 0) {
//                String sql1 = "INSERT INTO ADMIN.ITEMGROUP(TITLE) VALUES(?)";
//                stmt = connection.prepareStatement(sql1);
                stmt.setString(1, groupName.toUpperCase());
                stmt.execute();
                connection.commit();
                idgroup = getGroupID(groupName, connection);
                //System.out.println(idgroup);
            }
            return idgroup;
        }
    }

    public void readFile(String fileName, Connection connection) {
        int lineCount = 0;
        String item = null;
        String group = null;
        String operator = null;
        Pattern itemAndGroup = Pattern.compile("([А-Яа-яЕёA-Za-z]+)([+-])([А-Яа-яЕёA-Za-z]+)");
        boolean result = true;
        try (
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(fileName)
                                , "cp1251")
                        // читаем файл из двоичного потока
                        // в виде текста с нужной кодировкой
                )
        ) {
            String s;
            while ((s = br.readLine()) != null) { // пока readLine() возвращает не null
                Matcher m = itemAndGroup.matcher(s);
                if (!m.find()) {
                    System.out.println("error in line:\n" + s);
                    System.exit(1);
                    continue;
                }
                group = m.group(1);
                operator = m.group(2);
                item = m.group(3);
                switch (operator) {
                    case "+":
                        result &= addItemToGroup(item, group, connection);
                        break;
                    case "-":
                        result &= removeItemFromGroup(item, group, connection);
                        break;
                }
                // System.out.println(group + " " + operator + " " + item + " ");
            }
            if (result) {
                connection.commit();
                System.out.println("Все изменения зафиксированы");
            } else {
                connection.rollback();
                System.out.println("Откат всех изменений");
            }
        } catch (IOException | SQLException ex) {
            System.out.println("Reading error in line " + lineCount);
            ex.printStackTrace();
        }

    }

    boolean addItemToGroup(String itemName, String groupName, Connection connection) throws SQLException {
        //connection.setAutoCommit(false);
        boolean resultat = false;
        try (
                PreparedStatement stmt = connection.prepareStatement("insert into admin.item (groupid,title) values (?,?)");
        ) {
//            PreparedStatement stmt = null;
//            ResultSet rst = null;
            int idgroup = getAndAddGroup(groupName, connection);
//            String sql2 = "insert into admin.item (groupid,title) values (?,?)";
//            stmt = connection.prepareStatement(sql2);
            stmt.setInt(1, idgroup);
            stmt.setString(2, itemName);
            if (stmt.executeUpdate() == 1) {
                resultat = true;
            }
            System.out.println("Добавление " + itemName + " " + resultat);

        } catch (SQLException ex) {
            System.out.println("Добавить " + itemName + " не получилось");
            ex.getMessage();
            resultat = false;
            // System.out.println(ex.getMessage());
        }
        return resultat;
    }

    boolean removeItemFromGroup(String itemName, String groupName, Connection connection) throws SQLException {
        try (
                PreparedStatement stmt = connection.prepareStatement("delete from admin.item where title=? and groupid=?");
        ) {
            boolean resultat = false;
            connection.setAutoCommit(false);
//            PreparedStatement stmt = null;
//            ResultSet rst = null;
            int idgroup = getGroupID(groupName, connection);
//            String sql3 = "delete from admin.item where title=? and groupid=?";
//            stmt = connection.prepareStatement(sql3);
            stmt.setString(1, itemName);
            stmt.setInt(2, idgroup);
            if (stmt.executeUpdate() == 1) {
                resultat = true;
            }
            System.out.println("Удаление: " + itemName + " " + resultat);
            return resultat;
        }
    }


    void readGroups(String fileName) {
        int lineCount = 0;
        String group = null;
        String operator = null;
        Pattern patternGroup = Pattern.compile("([+-])([А-Яа-яЕёA-Za-z0-9]+)");
        boolean result = true;
        try (
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(fileName)
                                , "cp1251")
                        // читаем файл из двоичного потока
                        // в виде текста с нужной кодировкой
                )
        ) {
            String s;
            while ((s = br.readLine()) != null) { // пока readLine() возвращает не null
                Matcher m = patternGroup.matcher(s);
                if (!m.find()) {
                    System.out.println("error in line:\n" + s);
                    System.exit(1);
                    continue;
                }
                group = m.group(2);
                operator = m.group(1);
                switch (operator) {
                    case "+":
                        addGroups.add(group.toUpperCase());
                        //System.out.println("На добавление:"+group.toUpperCase());
                        break;
                    case "-":
                        removeGroups.add(group.toUpperCase());
                        //System.out.println("На удаление:"+group.toUpperCase());
                        break;
                }
            }
        } catch (IOException ex) {
            System.out.println("Reading error in line " + lineCount);
            ex.printStackTrace();
        }
    }

    void addAndRemoveGroup(Connection connection) throws SQLException {
        try (
                Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                ResultSet rst = stmt.executeQuery("SELECT * FROM itemgroup");
                )
        {
            connection.setAutoCommit(false);
            //boolean resultat = false;
            // Пройдемся по результсету
            while (rst.next()) {
                String group = rst.getString(2); // Сохраним текущее значение в переменную
                //  пройдемся по множеству удаляемых значений и если нашлось совпадение то удаляем
                if (removeGroups.contains(group)) {
                    System.out.println("Удалим: " + group);
                    rst.deleteRow();
                }
                // Удаляем из сета добавляемых значений те которые уже есть в базе
                if (addGroups.contains(group)) {
                    addGroups.remove(group);
                }
            }
            // теперь уже по множеству без лишних  элементов просто добавим использую изменяемый resultset
            Iterator<String> iteratorAdd2 = addGroups.iterator();
            while (iteratorAdd2.hasNext()) {
                String addGroup2 = iteratorAdd2.next();
                System.out.println("Добавим: " + addGroup2);
                rst.moveToInsertRow();
                rst.updateString(2, addGroup2);
                rst.insertRow();
            }
            System.out.println("-" + removeGroups);
            System.out.println("+" + addGroups);
            connection.commit();
        }
         catch (SQLException e) {
             System.out.println("откат всех изменений");
            connection.rollback();
            e.printStackTrace();
        }
        finally {
            connection.setAutoCommit(true);
        }


    }

    }
