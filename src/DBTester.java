import com.sun.xml.internal.bind.v2.model.core.ID;

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
            //System.out.println(connection);
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

    public void doWork(Connection connection) throws SQLException {

    }

    public void closeConn(Connection connection) throws SQLException {
        connection.close();
    }

    public void viewGroups(Connection connection) throws SQLException {
        Statement stmt = null;
        ResultSet rst = null;
        stmt = connection.createStatement();
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
        stmt = connection.createStatement();
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
        stmt = connection.createStatement();
        rst = stmt.executeQuery("SELECT * FROM item");
        for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++) {
            System.out.print(rst.getMetaData().getColumnName(i) + " ");
        }
        System.out.println();

        while (rst.next()) {
            for (int i = 1; i <= rst.getMetaData().getColumnCount(); i++) {
                //System.out.print(rst.getString(rst.getMetaData().getColumnLabel(i)) +" ");
                // System.out.println();
                itemdata.setItemMap(rst.getInt("id"), rst.getInt("groupid"), rst.getString("title"));
            }
            //System.out.println();
        }
        itemdata.getItemMap().entrySet().stream()
                .forEach(p -> System.out.println(p));
    }


    int getGroupID(String key, Connection connection) throws SQLException {
        int GroupID = 0;
        PreparedStatement stmt = null;
        ResultSet rst = null;
        String sql = "select id from itemgroup where title =?"; //select id from itemgroup where title = 'ТЕЛЕВИЗОРЫ'
        stmt = connection.prepareStatement(sql);
        stmt.setString(1, key.toUpperCase());
        rst = stmt.executeQuery();
        while (rst.next()) {
            GroupID = rst.getInt(1);
        }
        return GroupID;
    }

    void viewItemsInGroup(int groupid, Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rst = null;
        String sql = "select it.* from itemgroup gr, item it where it.groupid = gr.id  and gr.id=?"; //select id from itemgroup where title = 'ТЕЛЕВИЗОРЫ'
        stmt = connection.prepareStatement(sql);
        stmt.setInt(1, groupid);
        rst = stmt.executeQuery();
        while (rst.next()) {
            System.out.println(rst.getString(2));
        }

    }

    void viewItemsInGroup(String groupname, Connection connection) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rst = null;
        String sql = "select it.* from itemgroup gr, item it where it.groupid = gr.id  and gr.title =?"; //select id from itemgroup where title = 'ТЕЛЕВИЗОРЫ'
        stmt = connection.prepareStatement(sql);
        stmt.setString(1, groupname.toUpperCase());
        rst = stmt.executeQuery();
        while (rst.next()) {
            System.out.println(rst.getString(2));
        }
    }

    int getAndAddGroup(String groupName, Connection connection) throws SQLException {
        int idgroup = 0;
        PreparedStatement stmt = null;
        ResultSet rst = null;
        idgroup = getGroupID(groupName, connection);
        while (idgroup == 0) {
            String sql1 = "INSERT INTO ADMIN.ITEMGROUP(TITLE) VALUES(?)";
            stmt = connection.prepareStatement(sql1);
            stmt.setString(1, groupName.toUpperCase());
            stmt.execute();
            connection.commit();
            idgroup = getGroupID(groupName, connection);
            //System.out.println(idgroup);
        }
        return idgroup;
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
                        ;
                        if (addItemToGroup(item, group, connection)) {
                            result = result && true;
                        } else {
                            result = result && false;
                        }
                        break;
                    case "-":
                        if (removeItemFromGroup(item, group, connection)) {
                            result = result && true;
                        } else {
                            result = result && false;
                        }
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
        connection.setAutoCommit(false);
        boolean resultat = false;
        try {
            PreparedStatement stmt = null;
            ResultSet rst = null;
            int idgroup = getAndAddGroup(groupName, connection);
            String sql2 = "insert into admin.item (groupid,title) values (?,?)";
            stmt = connection.prepareStatement(sql2);
            stmt.setInt(1, idgroup);
            stmt.setString(2, itemName);
            if (stmt.executeUpdate() == 1) {
                resultat = true;
            }
            System.out.println("Добавление " + itemName + " " + resultat);

        } catch (SQLException ex) {
            System.out.println("Добавить " + itemName + " не получилось");
            resultat = false;
            // System.out.println(ex.getMessage());
        }
        return resultat;
    }

    boolean removeItemFromGroup(String itemName, String groupName, Connection connection) throws SQLException {
        boolean resultat = false;
        connection.setAutoCommit(false);
        PreparedStatement stmt = null;
        ResultSet rst = null;

        int idgroup = getGroupID(groupName, connection);
        String sql3 = "delete from admin.item where title=? and groupid=?";
        stmt = connection.prepareStatement(sql3);

        stmt.setString(1, itemName);
        stmt.setInt(2, idgroup);

        if (stmt.executeUpdate() == 1) {
            resultat = true;
        }

        System.out.println("Удаление: " + itemName + " " + resultat);
        return resultat;
    }


    void readGroups(String fileName) {
        int lineCount = 0;
        String group = null;
        String operator = null;

        Pattern patternGroup = Pattern.compile("([+-])([А-Яа-яЕёA-Za-z]+)");
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
        //boolean resultat = false;
        Statement stmt = null;
        ResultSet rst = null;
        String sql = "select * from itemgroup";
        stmt = connection.createStatement(
                ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_UPDATABLE);
        rst = stmt.executeQuery(sql);

        // Пройдемся по результсету
        while (rst.next()) {
            String group = rst.getString(2); // Сохраним текущее значение в переменную

            // Итератором пройдемся по множеству удаляемых значений и если нашлось совпадение то удаляем
            Iterator<String> iteratorRemove = removeGroups.iterator();
            while (iteratorRemove.hasNext()) {
                String removegroup = iteratorRemove.next();
                if (removegroup.equals(group)) {
                    System.out.println("Удалим: " + removegroup);
                    rst.deleteRow();
                }
            }

            // Удаляем из сета добавляемых значений те которые уже есть в базе
//            addGroups.stream()
//                    .filter(v -> v.equals(group))
//                    .forEach(addGroups::remove);
            Iterator<String> iteratorAdd = addGroups.iterator();
            while (iteratorAdd.hasNext()) {
                String addGroup1 = iteratorAdd.next();
                if (addGroup1.equals(group)) {
                    iteratorAdd.remove();
                    //addGroups.remove(addGroup1);
                }
            }
        }
            // теперь уже по множеству без лишних  элементов просто добавим использую изменяемый resultset
            Iterator<String> iteratorAdd2 = addGroups.iterator();
            while (iteratorAdd2.hasNext()) {
                String addGroup2 = iteratorAdd2.next();
                System.out.println("Добавим: " + addGroup2);
            rst.moveToInsertRow();
            rst.updateString(2,addGroup2);
            rst.insertRow();
            }

            System.out.println("-" + removeGroups);
            System.out.println("+" + addGroups);
        }

    }
