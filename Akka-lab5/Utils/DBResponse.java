package Utils;

public class DBResponse extends OrderResponse{
    private OperationType type;

    public OperationType getType() {
        return type;
    }

    public DBResponse(int id, OperationType type, String title, String msg){
        super(id, title, msg);
        this.type=type;
    }
}
