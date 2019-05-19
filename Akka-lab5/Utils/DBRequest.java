package Utils;

public class DBRequest extends OrderRequest{
    private OperationType type;

    public OperationType getType() {
        return type;
    }

    public DBRequest(int id, OperationType type, String title) {
        super(id, title);
        this.type= type;
    }
}
