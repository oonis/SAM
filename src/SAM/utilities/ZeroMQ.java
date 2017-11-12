package SAM.utilities;

import org.zeromq.ZMQ;
import org.zeromq.ZMQ.Context;
import org.zeromq.ZMQ.Socket;
/**
 * 
 */
public class ZeroMQ {
    public static void main(String[] args) {
        ZMQ.Context context = ZMQ.context(1);

        //  Socket to talk to server
        System.out.println("Connecting to hello world server…");

        ZMQ.Socket requester = context.socket(ZMQ.REQ);
        requester.connect("tcp://35.10.76.162:43210");

        for (int requestNbr = 0; requestNbr != 10; requestNbr++) {
            String request = "command: status";
            System.out.println("Sending status " + requestNbr);
            requester.send(request);
            //requester.send(request.getBytes(), 0);

            byte[] reply = requester.recv(0);
            System.out.println("Received " + new String(reply) + " " + requestNbr);
        }
        requester.close();
        context.term();
    }
    
    
    
}
