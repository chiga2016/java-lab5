import java.sql.*;

public class Main {


public static void main (String[] args ) throws SQLException {
    DBTester db = new DBTester();
    Connection conn = db.connectToDB();
    db.testConnection(conn);
    //System.out.println(conn);
//   db.viewGroups(conn);
//   db.viewItems(conn);
//    System.out.println(db.getGroupID("Телевизоры", conn));
    //db.viewItemsInGroup(12,conn);
 //  System.out.println(db.getGroupID("ТЕЛЕФОНЫ", conn));
    // db.viewItemsInGroup("Телефоны",conn);
    //System.out.println(db.AddGroup("Электроинструмент, conn));
    //db.addItemToGroup("Xiaomi1","Телефоны",conn);
   // db.addItemToGroup("fff","Телефоны",conn);
    //db.removeItemFromGroup("HTC","Телефоны",conn);
   // db.readFile("itemAndGroup.txt",conn);
    db.readGroups("groups.txt");
    db.addAndRemoveGroup(conn);

 //   db.viewItemsInGroup("ТЕЛЕФОНЫ",conn);
    db.closeConn(conn);

    //System.out.println(db.getGroupID("ТЕЛЕВИЗОРЫ", conn).toString());

//    ItemData itmd = new ItemData(1,2,"Компы");
//    itmd.getItemMap().entrySet().stream()
//            .forEach(p-> System.out.println(p.getKey() + ":" +p.getValue().toString()));

}


}



