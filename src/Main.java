import java.sql.*;

public class Main {


public static void main (String[] args ) throws SQLException {
    DBTester db = new DBTester();
    Connection conn = db.connectToDB();
    db.testConnection(conn);
    System.out.println(conn);
    db.viewGroups(conn);

}

    private static void testConnection(Connection conn) throws SQLException {
        Statement stmt = null;
        ResultSet rst = null;


            stmt = conn.createStatement();
            rst = stmt.executeQuery("SELECT * FROM ITEM");
        System.out.println(rst.getRow());

            System.out.println(
                    "execute() returns " +
                            stmt.execute("SELECT * FROM ITEM"));

                    //+ " WHERE ZIP=''"));
                    // execute вернет true, если получен ResultSet
                    // getResultSet()
                    // если выполнялась DML - операция, вернется false и установится
                    // getUpdateCount() - число измененных строк

                    rst = stmt.getResultSet();
                   // break;

//
//            System.out.println("rst= "+rst.toString());
//
//            System.out.printf("row %d\n",
//                    rst.getRow());
//            // conn.getMetaData().
//            // цикл по результатам
//            while (rst.next()) {
//
//                // номер строки
//                System.out.printf("row %3d:",
//                        rst.getRow());
//
//                int id;
//                String nm, cid;
//
//                id = rst.getInt(1);
//                // получаем столбец по номеру (int!)
//
//// если в базе был NULL, то вместо примитивных типов
//                // вернется значение по умолчанию (0, false)!
//                // если нужна проверка, пишите
//                // СРАЗУ ПОСЛЕ get... "if (rst.wasNull()) ..."
//                if (rst.wasNull()) logger.info("NULL ID FOUND!");
//                BigDecimal bd = rst.getBigDecimal(1);
//
//                nm = rst.getString("NAME"); // получаем столбец по заголовку
//                cid = rst.getString("CUSTOMER_ID"); // получаем число как строку
//                System.out.printf(
//                        "%10d | %30s | %10s\n",
//                        id,nm,cid);
//            }
//
//            // информация о столбцах ResultSet-а
//            System.out.println("\n\nResultSet info:");
//            ResultSetMetaData meta = rst.getMetaData();
//            int n = meta.getColumnCount();
//
//            for (int i=1;i<=n;i++)  {
//                System.out.printf("%d = %s <%s>(%d)",
//                        i,
//                        meta.getColumnName(i),
//                        meta.getColumnTypeName(i),
//                        meta.getPrecision(i));
//                if (meta.isNullable(i)==meta.columnNoNulls) System.out.print(" NOT NULL");
//                if (meta.isAutoIncrement(i)) System.out.print(" AUTO");
//                System.out.println("");
//            }
//        } catch (SQLException ex) {
//            ex.getErrorCode();
//            //printSQLExceptions(ex);
//        } finally {
//            try {
//                if (rst!=null) rst.close();
//                if (stmt!=null) stmt.close();
//            } catch (SQLException ex) {
//               // logger.log(Level.SEVERE, null, ex);
//            }

    }

    }



