package Server;

import Client.Customer_MsgManager;
import Utils.*;
import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class Bookshop_Actor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private int counter;
    private HashMap<Integer, ActorRef> requests;

    public Bookshop_Actor(){
        this.counter=0;
        requests = new HashMap<Integer, ActorRef>();
    }

    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(CustomerMessage.class, cmsg->{
                    if(cmsg.getOperationType()== OperationType.SEARCH || cmsg.getOperationType() == OperationType.ORDER){
                        requests.put(counter, context().sender());
                        System.out.println(cmsg.getOperationType()+ ": " + cmsg.getTitle());
                        DBRequest dbreq = new DBRequest(counter, cmsg.getOperationType(), cmsg.getTitle());
                        counter++;
                        context().child("dbSearcher").get().tell(dbreq, getSelf());
                    }
                })
                .match(DBResponse.class, dbres ->{
                    if(dbres.getType()==OperationType.SEARCH){
                        BookshopMessage bmsg = new BookshopMessage(dbres.getType(), dbres.getTitle(), dbres.getMsg());
                        requests.get(dbres.getId()).tell(bmsg, getSelf());
                    }
                    else if(dbres.getType() == OperationType.ORDER){
                        if(!dbres.getMsg().startsWith("P")){
                            BookshopMessage bmsg = new BookshopMessage(dbres.getType(), dbres.getTitle(), dbres.getMsg());
                            requests.get(dbres.getId()).tell(bmsg, getSelf());
                        } else {
                            OrderRequest oreq = new OrderRequest(dbres.getId(), dbres.getTitle());
                            context().child("dbOrderTaker").get().tell(oreq, getSelf());
                        }
                    }
                })
                .match(OrderResponse.class, ores -> {
                    BookshopMessage bmsg = new BookshopMessage(OperationType.ORDER, ores.getTitle(), ores.getMsg());
                    requests.get(ores.getId()).tell(bmsg, getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    public void preStart() throws Exception {
        context().actorOf(Props.create(Bookshop_DBSearcher.class), "dbSearcher");
        context().actorOf(Props.create(Bookshop_DBOrderTaker.class), "dbOrderTaker");
    }

    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            match(FileNotFoundException.class, o -> restart()).
            match(NullPointerException.class, o->restart()).
            matchAny(o -> restart()).
            build());

    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
