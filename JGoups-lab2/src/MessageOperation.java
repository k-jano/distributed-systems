import java.io.Serializable;

class MessageOperation implements Serializable {
    private OperationType type;
    private String key;
    private Integer value;

    OperationType getType() {
        return type;
    }


    String getKey() {
        return key;
    }


    Integer getValue() {
        return value;
    }


    MessageOperation(OperationType type, String key, Integer value){
        this.type=type;
        this.key=key;
        this.value=value;
    }

    MessageOperation(OperationType type, String key){
        this.type=type;
        this.key=key;
    }
}
