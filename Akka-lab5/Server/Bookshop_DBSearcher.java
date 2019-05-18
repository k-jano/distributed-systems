package Server;

import Utils.*;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.*;
import java.net.URL;

public class Bookshop_DBSearcher extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    @Override
    public AbstractActor.Receive createReceive() {
        return receiveBuilder()
                .match(DBRequest.class, dbreq->{
                    //find price
                    String msg = searchDB(dbreq.getTitle());
                    DBResponse dbres = new DBResponse(dbreq.getId(), dbreq.getType(), dbreq.getTitle(), msg);
                    getSender().tell(dbres, getSelf());
                })
                .matchAny(o -> log.info("received unknown message"))
                .build();
    }

    private int getPrice(String line, String title){
        String[] split = line.split(" ");
        String tmpTitle="";
        for(int i=0; i<split.length-1; i++){
            tmpTitle += split[i];
            tmpTitle += " ";
        }
        tmpTitle = tmpTitle.substring(0, tmpTitle.length()-1);

        if(tmpTitle.equals(title))
            return Integer.parseInt(split[split.length-1]);

        return -1;
    }

    private String searchDB(String title) throws FileNotFoundException, IOException {
        String filePath = new File("").getAbsolutePath();
        BufferedReader reader1 = new BufferedReader(new FileReader(filePath + "/src/DB/db1.txt"));
        BufferedReader reader2 = new BufferedReader(new FileReader(filePath + "/src/DB/db2.txt"));
        int val;
        String msg = "Price: ";
        String line1, line2, line = null;
        do{
            line1 = reader1.readLine();
            line2 = reader2.readLine();
            if (line1 == null && line2 == null)
                line = null;
            else
                line = "";

            if(line1 != null && getPrice(line1, title)!= -1)
                return msg+ getPrice(line1, title);
            else if(line2 != null && getPrice(line2, title)!= -1)
                return msg + getPrice(line2, title);

        } while ( line != null);
        return "Title not in DB";
    }
}