package Server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@SuppressWarnings("Duplicates")
public class Bookshop {
    public static void main(String[] args) throws Exception{
        File configFile = new File("bookshop.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("bookshop_system", config);
        final ActorRef actor = system.actorOf(Props.create(Bookshop_Actor.class), "Server.Bookshop");

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            String line = br.readLine();
            if(line.equals("q")){
                break;
            }
            actor.tell(line, null);
        }

        system.terminate();
    }
}
