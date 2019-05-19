package Server;

import Utils.OrderRequest;
import Utils.OrderResponse;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Bookshop_DBOrderTaker extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final Lock lock = new ReentrantLock();

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(OrderRequest.class, dbreq->{
                    saveTheOrder(dbreq.getTitle());
                    OrderResponse ores = new OrderResponse(dbreq.getId(), dbreq.getTitle(), "Order completed");
                    getSender().tell(ores, getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    private void saveTheOrder(String title) throws IOException, InterruptedException {
        lock.lock();
        String filePath = new File("").getAbsolutePath();
        FileWriter fstream = new FileWriter(filePath + "/src/DB/orders.txt", true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(title + "\n");
        out.close();
        lock.unlock();
    }
}
