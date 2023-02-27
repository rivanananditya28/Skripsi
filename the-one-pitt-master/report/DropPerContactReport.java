/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

import core.ConnectionListener;
import core.DTNHost;
import core.Message;
import core.MessageListener;
import core.Settings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Khusus Skripsi Fuzzy
 */
public class DropPerContactReport extends Report implements MessageListener, ConnectionListener {

    private int nrofDropped;
    private int nrofRemoved;
    public static final String TOTAL_CONTACT_INTERVAL = "perTotalContact";
    public static final int DEFAULT_CONTACT_COUNT = 500;
    private int totalContact;
    private int lastRecord;
    private int interval;
//    private List<Double> drop;
    private Map<Integer, String> nrofDrop;

    public DropPerContactReport() {
        init();
        Settings s = getSettings();
        if (s.contains(TOTAL_CONTACT_INTERVAL)) {
            interval = s.getInt(TOTAL_CONTACT_INTERVAL);
        } else {
            interval = DEFAULT_CONTACT_COUNT;
        }
    }
    
 @Override
    protected void init() {
        super.init();
        this.interval = 0;
        this.lastRecord = 0;
        this.totalContact = 0;
        this.nrofDropped = 0;
        this.nrofRemoved = 0;
//         this.drop = new ArrayList<Double>();
        this.nrofDrop = new HashMap<>();
        
    }
    @Override
    public void newMessage(Message m) {

    }

    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {

    }

    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {
        if (isWarmupID(m.getId())) {
            return;
        }
        if (dropped) {
            this.nrofDropped++;
        } else {
            this.nrofRemoved++;
        }
    }

    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {

    }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean firstDelivery) {

    }

    @Override
    public void hostsConnected(DTNHost host1, DTNHost host2) {
        totalContact++;
        if (totalContact - lastRecord >= interval) {
            lastRecord = totalContact;
            nrofDrop.put(lastRecord, String.valueOf(nrofDropped));
        }
    }

    @Override
    public void hostsDisconnected(DTNHost host1, DTNHost host2) {

    }
     @Override
    public void done() {
        String statsText = "Contact\tDroppedMessage\n";
        for (Map.Entry<Integer, String> entry : nrofDrop.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            statsText += key + "\t" + value + "\n";
        }
        write(statsText);
        super.done();
    }

}
