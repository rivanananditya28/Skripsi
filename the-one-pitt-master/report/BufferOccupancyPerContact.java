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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Khusus Skripsi Fuzzy
 */
public class BufferOccupancyPerContact extends Report implements MessageListener, ConnectionListener {

    private int totalContact;
    private int interval;
    private int lastRecord;
    public static final String TOTAL_CONTACT_INTERVAL = "perTotalContact";
    public static final int DEFAULT_CONTACT_COUNT = 500;
    private List<Double> bufferOcc;
    private Map<Integer, String> nrofLatency;

    public BufferOccupancyPerContact() {
        init();
        Settings s = getSettings();
        if (s.contains(TOTAL_CONTACT_INTERVAL)) {
            interval = s.getInt(TOTAL_CONTACT_INTERVAL);
        } else {
            interval = DEFAULT_CONTACT_COUNT;
        }
    }

    protected void init() {
        super.init();
        this.interval = 0;
        this.lastRecord = 0;
        this.totalContact = 0;
        this.bufferOcc = new LinkedList<>();
        this.nrofLatency = new HashMap<>();
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
        double buffer = from.getBufferOccupancy();
        bufferOcc.add(buffer);
    }

    @Override
    public void hostsConnected(DTNHost host1, DTNHost host2) {
        totalContact++;
        if (totalContact - lastRecord >= interval) {
            lastRecord = totalContact;
            String latenciesValue;
            if (bufferOcc.size() == 0) {
                latenciesValue = "0";
            } else {
                latenciesValue = getAverage(bufferOcc);
            }
            nrofLatency.put(lastRecord, latenciesValue);
        }
    }

    @Override
    public void hostsDisconnected(DTNHost host1, DTNHost host2) {

    }

    @Override
    public void done() {
        String statsText = "Contact\tBufferOccupancy\n";
        for (Map.Entry<Integer, String> entry : nrofLatency.entrySet()) {
            Integer key = entry.getKey();
            String value = entry.getValue();
            statsText += key + "\t" + value + "\n";
        }
        write(statsText);
        super.done();
    }
}
