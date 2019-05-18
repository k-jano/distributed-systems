package Utils;

import java.io.Serializable;

public class CustomerMessage implements Serializable {
    private OperationType operationType;
    private String title;

    public OperationType getOperationType() {
        return operationType;
    }

    public String getTitle() {
        return title;
    }

    public CustomerMessage(OperationType operationType, String title){
        this.operationType=operationType;
        this.title=title;
    }
}
