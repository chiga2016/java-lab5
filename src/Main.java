import java.sql.*;

public class Main {


public static void main (String[] args ) throws SQLException {
    DBTester db = new DBTester();
    Connection conn = db.connectToDB();
    db.testConnection(conn);
    //System.out.println(conn);
//   db.viewGroups(conn);
//   db.viewItems(conn);
    db.viewItems2(conn);
    db.getGroupID("ТЕЛЕВИЗОРЫ", conn).stream()
            .forEach(p-> System.out.println(p));


    //System.out.println(db.getGroupID("ТЕЛЕВИЗОРЫ", conn).toString());

//    ItemData itmd = new ItemData(1,2,"Компы");
//    itmd.getItemMap().entrySet().stream()
//            .forEach(p-> System.out.println(p.getKey() + ":" +p.getValue().toString()));



}


}



