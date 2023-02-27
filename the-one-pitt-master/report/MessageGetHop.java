/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;
import java.util.List;

/**
 *
 * @author georg
 */
public class MessageGetHop extends Report implements MessageListener{

       public MessageGetHop() {
        Settings setting = getSettings();
        init();
    }

    private String getPathString(Message m) {
        List<DTNHost> hops = m.getHops();
        String str = m.getFrom().toString();

        for (int i = 1; i < hops.size(); i++) {
            str += "->" + hops.get(i) + " (" + m.getId()+") ";
        }

        return str;
    }
    
     private String getNodeMessage(Message m, DTNHost thisHost) {
        List<DTNHost> hops = m.getHops();
        String str = m.getFrom().toString();

        for (int i = 1; i < hops.size(); i++) {
            str += "->" + hops.get(i) + " (" + m.getId()+") ";
        }

        return str;
    }

    @Override
    public void newMessage(Message m) {
    }

    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
    }

    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {
    }

    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
        
    }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {
        if (!isWarmupID(m.getId()) && firstDelivery) {
            int ttl = m.getTtl();
            write(getPathString(m));
        }
    }

    @Override
    public void done() {
    }

}
