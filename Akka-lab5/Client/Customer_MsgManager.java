package Client;

import Utils.CustomerMessage;
import Utils.OperationType;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Customer_MsgManager extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s -> {
                    String title = SplitTitle(s);
                    if(s.startsWith("s")){
                        CustomerMessage msg = new CustomerMessage(OperationType.SEARCH, title);
                        getSender().tell(msg, getSelf());
                    } else if(s.startsWith("o")){
                        CustomerMessage msg = new CustomerMessage(OperationType.ORDER, title);
                        getSender().tell(msg, getSelf());
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    private String SplitTitle(String s){
        String[] split = s.split(" ");
        String title, tmp = "";
        for(int i=1; i<split.length; i++){
            tmp+= split[i];
            tmp+= " ";
        }
        tmp = tmp.substring(0, tmp.length()-1);
        title = "'" + tmp + "'";
        return title;
    }
}
