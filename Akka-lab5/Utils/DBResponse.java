package Utils;

public class DBResponse {
    private int id;
    private OperationType type;
    private String title;
    private String msg;

    public OperationType getType() {
        return type;
    }

    public String getTitle(){ return title;}

    public int getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public DBResponse(int id, OperationType type, String title, String msg){
        this.id=id;
        this.type=type;
        this.title=title;
        this.msg=msg;
    }
}
