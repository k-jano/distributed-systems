package Server;

import Utils.*;
import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.io.*;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

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

    private String searchDB(String title) throws IOException, NullPointerException {
        LinkedList<String> paths = new LinkedList<>();
        File dbFolder = new File("src/DB");
        for (File db : dbFolder.listFiles()){
            if(db.isFile() && db.getName().startsWith("db")){
                paths.push(db.getPath());
            }
        }

        LinkedList<BufferedReader> bufferedReaders = new LinkedList<>();
        for(int i=0; i<paths.size(); i++){
            BufferedReader bfReader = new BufferedReader(new FileReader(paths.get(i)));
            bufferedReaders.push(bfReader);
        }

        String msg = "Price: ";
        String line;

        do{
            LinkedList<String> lines = new LinkedList<>();

            line = null;
            for(int i=0; i<paths.size(); i++){
                String singleLine = bufferedReaders.get(i).readLine();
                lines.push(singleLine);
            }

            for (String singleLine : lines){
                if(!(singleLine == null))
                    line = "";
            }

            for(int i=0; i<paths.size(); i++){
                if(lines.get(i) != null && getPrice(lines.get(i), title) != -1)
                    return msg + getPrice(lines.get(i), title);
            }

        } while ( line != null);
        return "Title not in DB";
    }
}