    /* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import core.DTNHost;
import core.Message;
import core.World;
import java.util.ArrayList;

/**
 * External event for creating a message.
 */
public class MessageCreateEventBusTj extends MessageEventBusTj {

    private ArrayList<Integer> size;
    private int responseSize;
    int idMsg=0;

    /**
     * Creates a message creation event with a optional response request
     *
     * @param from The creator of the message
     * @param to Where the message is destined to
     * @param id ID of the message
     * @param size Size of the message
     * @param responseSize Size of the requested response message or 0 if no
     * response is requested
     * @param time Time, when the message is created
     */
    public MessageCreateEventBusTj(ArrayList<Integer> from, int to, ArrayList<String> id, ArrayList<Integer> size,
            int responseSize, double time) {
        super(from, to, id, time);
        this.size = size;
        this.responseSize = responseSize;
    }

    /**
     * Creates the message this event represents.
     */
    @Override
    public void processEvent(World world) {
        DTNHost to = world.getNodeByAddress(this.toAddr);
        ArrayList<DTNHost> from = world.getNodeByAddressFrom(this.fromAddr);
        
//        System.out.println("ID MSG "+this.id);
        int indexLengthMsg=0;
        for (DTNHost dTNHost : from) {
//            System.out.println("HOST " + dTNHost);           
            if(true) {
//                System.out.println("ID MSg "+id.get(idMsg));
                Message m = new Message(dTNHost, to, this.id.get(idMsg), this.size.get(indexLengthMsg));
                m.setResponseSize(this.responseSize);
                dTNHost.createNewMessage(m);
//                break;           
            }
            idMsg++;
            indexLengthMsg++;
        }
//        this.id.clear();
    }

    @Override
    public String toString() {
        return super.toString() + " [" + fromAddr + "->" + toAddr + "] "
                + "size:" + size + " CREATE";
    }
}
