public class Item {
    private int id;
    private String title;
    private int groupid;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroupid() {
        return groupid;
    }

    public void setIdgroup(int groupid) {
        this.groupid = groupid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

public Item (int id, int groupid, String title) {
    setId(id);
    setIdgroup(groupid);
    setTitle(title);
}

public Item() {

}



   public String toString() {
       return "id = " + this.id + "; title=" + this.title + "; idgroup = "+ this.groupid;
   }

}
