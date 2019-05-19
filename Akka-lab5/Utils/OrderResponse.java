package Utils;

public class OrderResponse {
    private int id;
    private String title;
    private String msg;

    public String getTitle(){ return title;}

    public int getId() {
        return id;
    }

    public String getMsg() {
        return msg;
    }

    public OrderResponse(int id, String title, String msg){
        this.id=id;
        this.title=title;
        this.msg=msg;
    }
}
