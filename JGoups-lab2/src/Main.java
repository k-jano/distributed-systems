import org.jgroups.*;
import org.jgroups.protocols.UDP;
import org.jgroups.util.Util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {

    private String myChannel="MyCluster";
    private JChannel channel;
    private DistributedMap distributedMap;


    private void processTheMessage(LinkedList<String> list) throws Exception {
        String type = list.pop();
        type = type.toUpperCase();
        String key;
        String integerStr;
        switch (type) {
            case "CONTAINS": key = list.pop();
                             System.out.println(distributedMap.containsKey(key));
                             break;
            case "GET": key = list.pop();
                        System.out.println(distributedMap.get(key));
                        break;
            case "PUT": key = list.pop();
                         integerStr =list.pop();
                         Integer integer = Integer.parseInt(integerStr);
                         channel.send(new Message(null, null, new MessageOperation(OperationType.PUT, key, integer)));
                         distributedMap.put(key, integer);
                         break;
            case "REMOVE": key = list.pop();
                           channel.send(new Message(null, null, new MessageOperation(OperationType.REMOVE, key)));
                           distributedMap.remove(key);
                           break;
            case "CLOSE": channel.close();
                          System.exit(0);
                          break;

            case "SHOW": System.out.println(distributedMap.getMap());
                         break;

            case "CHANGE":  if(list.size()==0)
                                return;
                            myChannel = list.pop();
                            channel.disconnect();
                            channel.connect(myChannel, null, 0);

            default: System.out.println("Wrong command");
        }
    }

    private void processTheLine(String line) throws Exception {
        line = line.trim();
        if(line.length()==0)
            return;
        LinkedList<String> list = new LinkedList<>();
        int last=0;
        for(int i=0; i<line.length(); i++){
            if(line.charAt(i)==' '){
                list.add(line.substring(last, i));
                last=i+1;
            } else if(i== line.length()-1){
                list.add(line.substring(last, i+1));
            }
        }

        processTheMessage(list);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void inputCommand() throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (true){
            String line = scanner.nextLine();
            processTheLine(line);
        }
    }

    private void handleReceivedOperation(MessageOperation messageOperation){
        OperationType operationType = messageOperation.getType();
        if (operationType.equals(OperationType.PUT)){
            distributedMap.put(messageOperation.getKey(), messageOperation.getValue());
        } else if (operationType.equals(OperationType.REMOVE)){
            distributedMap.remove(messageOperation.getKey());
        }
    }

    private void listening(){
        channel.setReceiver(new ReceiverAdapter(){
            public void receive(Message msg){
                Object object = msg.getObject();
                if( object instanceof MessageOperation){
                    handleReceivedOperation((MessageOperation) object);
                }
            }

            @Override
            public void getState(OutputStream output) throws Exception {
                Util.objectToStream(distributedMap.getMap(), new DataOutputStream(output));
            }

            @Override
            @SuppressWarnings("unchecked")
            public void setState(InputStream input) throws Exception {
                HashMap<String, Integer> map;
                map = (HashMap<String, Integer>) Util.objectFromStream(new DataInputStream(input));
                distributedMap.setMap(map);
            }

            @Override
            public void viewAccepted(View view) {
                if(view instanceof  MergeView){
                    //System.out.println("In view accepted");
                    ViewHandler handler = new ViewHandler(channel, (MergeView) view);
                    handler.start();
                }
            }
        });
    }

    private void start() throws Exception{
        distributedMap = new DistributedMap();
        channel = new JChannel();
        listening();
        channel.setDiscardOwnMessages(true);
        channel.connect(myChannel, null, 0);
        inputCommand();
    }

    public static void main (String[] args) throws Exception{
        System.setProperty("java.net.preferIPv4Stack","true");
        //new UDP().setValue("mcast_group_addr", InetAddress.getByName("230.100.200.x"));
        new Main().start();
    }

    private static class ViewHandler extends Thread {
        JChannel ch;
        MergeView view;

        private ViewHandler(JChannel ch, MergeView view) {
            this.ch = ch;
            this.view = view;
        }

        public void run() {
            View tmp_view = view.getSubgroups().get(0);
            Address local_addr = ch.getAddress();
            if (!tmp_view.getMembers().contains(local_addr)) {
                try {
                    ch.getState(null, 0);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}


