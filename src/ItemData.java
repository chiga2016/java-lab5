import java.util.HashMap;
import java.util.Map;

public class ItemData {
Map<Integer,Item> ItemMap =  new HashMap<Integer,Item>();

    public ItemData(int id, int idgroup, String title) {
        ItemMap.put(id, new Item(id,idgroup,title));
    }
    public ItemData() {

    }

    public void setItemMap(int id, int groupid, String title) {
        ItemMap.put(id, new Item(id,groupid,title));
    }

    public Map<Integer, Item> getItemMap() {
        return ItemMap;
    }
}


