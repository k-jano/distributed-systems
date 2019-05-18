package Server;

import Client.Customer_MsgManager;
import Utils.BookshopMessage;
import Utils.CustomerMessage;
import Utils.OperationType;
import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import scala.concurrent.duration.Duration;

import static akka.actor.SupervisorStrategy.restart;
import static akka.actor.SupervisorStrategy.resume;

public class Bookshop_Actor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(CustomerMessage.class, cmsg->{
                    if(cmsg.getOperationType()== OperationType.SEARCH){
                        System.out.println("Remote: " + cmsg.getTitle());
                        BookshopMessage bmsg = new BookshopMessage(cmsg.getOperationType(), cmsg.getTitle(), "Przyjeto");
                        context().sender().tell(bmsg, getSelf());
                    }
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    public void preStart() throws Exception {
        context().actorOf(Props.create(Bookshop_DBSearcher.class), "dbSearcher");
    }

    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            matchAny(o -> restart()).
            build());

    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
