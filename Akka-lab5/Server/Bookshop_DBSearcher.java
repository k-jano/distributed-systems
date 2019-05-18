package Server;

import Utils.BookshopMessage;
import Utils.CustomerMessage;
import Utils.DBResponse;
import Utils.OperationType;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class Bookshop_DBSearcher extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(String.class, s->{
                    int val = 1;//findTitleValue(s);
                    String msg;
                    if(val ==-1)
                        msg="Can not find title in db";
                    else
                        msg="Price "+val;
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }
}