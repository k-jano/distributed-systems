package Client;

import akka.actor.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@SuppressWarnings("Duplicates")
public class Customer {
    public  static void main(String[] args) throws Exception{

        File configFile = new File("customer.conf");
        Config config = ConfigFactory.parseFile(configFile);

        final ActorSystem system = ActorSystem.create("customer_system", config);
        final ActorRef actor  = system.actorOf(Props.create(Customer_Actor.class), "customerAct");
        System.out.println("Commands: 's title'");

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
