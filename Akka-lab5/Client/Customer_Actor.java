package Client;

import Utils.*;
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

public class Customer_Actor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public AbstractActor.Receive createReceive(){
        return receiveBuilder()
                .match(String.class, s->{
                    context().child("msgWorker").get().tell(s, getSelf());
                })
                .match(CustomerMessage.class, cmsg->{
                    getContext().actorSelection("akka.tcp://bookshop_system@127.0.0.1:8080/user/Server.Bookshop").tell(cmsg, getSelf());
                })
                .match(BookshopMessage.class, bmsg->{
                    System.out.println(bmsg.getMsg());
                })
                .matchAny(o -> log.info("Received unknown message"))
                .build();
    }

    public void preStart() throws Exception {
        context().actorOf(Props.create(Customer_MsgManager.class), "msgWorker");
    }

    private static SupervisorStrategy strategy
            = new OneForOneStrategy(10, Duration.create("1 minute"), DeciderBuilder.
            match(ArrayIndexOutOfBoundsException.class, o -> resume()).
            matchAny(o -> restart()).
            build());

    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
