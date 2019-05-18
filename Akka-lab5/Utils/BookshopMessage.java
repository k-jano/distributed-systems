package Utils;

import java.io.Serializable;

public class BookshopMessage implements Serializable {
    private OperationType operationType;
    private String title;
    private String msg;

    public OperationType getOperationType() {
        return operationType;
    }

    public String getTitle() {
        return title;
    }

    public String getMsg() {
        return msg;
    }

    public BookshopMessage(OperationType operationType, String title, String msg){
        this.operationType=operationType;
        this.title=title;
        this.msg=msg;
    }
}
