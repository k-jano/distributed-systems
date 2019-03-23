import java.util.HashMap;

public class DistributedMap implements SimpleStringMap {

    private HashMap<String, Integer> hashMap;

    DistributedMap(){
        hashMap = new HashMap<>();
    }

    HashMap getMap(){
        return hashMap;
    }

    void setMap(HashMap<String, Integer> map){
        this.hashMap=map;
    }

    @Override
    public boolean containsKey(String key) {
        return hashMap.containsKey(key);
    }

    @Override
    public Integer get(String key) {
        return hashMap.get(key);
    }

    @Override
    public void put(String key, Integer value) {
        hashMap.put(key, value);
    }

    @Override
    public Integer remove(String key) {
        return hashMap.remove(key);
    }
}
