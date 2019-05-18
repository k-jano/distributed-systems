package Utils;

public class DBResponse {
    private int id;
    private String msg;

    public int getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public DBResponse(int id, String msg){
        this.id=id;
        this.msg=msg;
    }
}
