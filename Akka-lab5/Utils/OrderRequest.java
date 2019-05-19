package Utils;

public class OrderRequest {
    private int id;
    private String title;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public OrderRequest(int id, String title) {
        this.id = id;
        this.title = title;
    }
}
