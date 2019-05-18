package Utils;

public class DBRequest {
    private int id;
    private OperationType type;
    private String title;

    public int getId() {
        return id;
    }

    public OperationType getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public DBRequest(int id, OperationType type, String title) {
        this.id = id;
        this.type = type;
        this.title = title;
    }
}
