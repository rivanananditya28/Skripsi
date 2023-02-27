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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Provides the inter-contact duration data for making probability density
 * function
 *
 * @author Gregorius Bima, Sanata Dharma University
 */
public class ReportBusPesanLalulintas extends Report implements MessageListener {

    public static final String NODE_ID = "ToNodeID";
    private String pesan;
    private int nrofDropped;
    private int nrofRemoved;
    private int nrofStarted;
    private int nrofAborted;
    private int nrofRelayed;
    private int nrofDelivered;
    private int nrofCreated;
    private double maxLatency, minLatency;
    private Map<String, Double> creationTimes;
    private List<Double> latencies;
    private List<Integer> hopCounts;

    public ReportBusPesanLalulintas() {
        init();
        Settings s = getSettings();
        if (s.contains(NODE_ID)) {
            pesan = s.getSetting(NODE_ID);
        } else {
            pesan = "L";
        }

    }

    @Override
    public void init() {
        super.init();
        this.nrofCreated = 0;
        this.nrofDelivered = 0;
        this.nrofDropped = 0;
        this.nrofRemoved = 0;
        this.nrofStarted = 0;
        this.nrofAborted = 0;
        this.nrofRelayed = 0;
        this.maxLatency = 0;
        this.minLatency = 0;
        latencies = new LinkedList<>();
        creationTimes = new HashMap<>();
        this.hopCounts = new ArrayList<Integer>();
    }

    @Override
    public void done() {
        double deliveryProb = 0; // delivery probability
//        double responseProb = 0; // request-response success probability
        double overHead = Double.NaN;	// overhead ratio

        if (this.nrofCreated > 0) {
            deliveryProb = (1.0 * this.nrofDelivered) / this.nrofCreated;
        }
        if (this.nrofDelivered > 0) {
            overHead = (1.0 * (this.nrofRelayed - this.nrofDelivered))
                    / this.nrofDelivered;
        }
        String output = "Pesan " + pesan + "\n";

        output += "nrofCreated = " + this.nrofCreated + "\n";
        output += "nrofDropped = " + this.nrofDropped + "\n";
        output += "nrofRemoved = " + this.nrofRemoved + "\n";
        output += "nrofStarted = " + this.nrofStarted + "\n";
        output += "nrofAborted = " + this.nrofAborted + "\n";
        output += "nrofRelayed = " + this.nrofRelayed + "\n";
        output += "ndelivered = " + this.nrofDelivered + "\n";
        output += "deliveryProb = " + format(deliveryProb) + "\n";
        output += "OverheadRatio = " + format(overHead) + "\n";
        output += "averageLatency = " + getAverage(this.latencies) + "\n";
        output += "medianLatency = " + getMedian(this.latencies) + "\n";
        output += "maxLatency = " + this.maxLatency + "\n";
        output += "minLatency = " + this.minLatency + "\n";
        output += "nhopcount_avg = " + getIntAverage(this.hopCounts) + "\n";
        output += "nhopcount_med= " + getIntMedian(this.hopCounts) + "\n";
        write(output);
        super.done();
    }

    @Override
    public void newMessage(Message m) {
        if (String.valueOf(m.getId().charAt(0)).equals(pesan)) {
            this.creationTimes.put(m.getId(), getSimTime());
            this.nrofCreated++;
        }

    }

    @Override
    public void messageTransferStarted(Message m, DTNHost from, DTNHost to) {
        if (String.valueOf(m.getId().charAt(0)).equals(pesan)) {
            this.nrofStarted++;
        }
    }

    @Override
    public void messageDeleted(Message m, DTNHost where, boolean dropped) {
        if (String.valueOf(m.getId().charAt(0)).equals(pesan)) {
            if (dropped) {
                this.nrofDropped++;
            } else {
                this.nrofRemoved++;
            }
        }

    }

    @Override
    public void messageTransferAborted(Message m, DTNHost from, DTNHost to) {
        if (String.valueOf(m.getId().charAt(0)).equals(pesan)) {
            this.nrofAborted++;
        }
    }

    @Override
    public void messageTransferred(Message m, DTNHost from, DTNHost to, boolean finalTarget) {

        if (String.valueOf(m.getId().charAt(0)).equals(pesan)) {
            this.nrofRelayed++;
        }

        if (finalTarget) {
            //            System.out.println(String.valueOf(m.getId().charAt(0)) + "\t" + pesan + "\t" + String.valueOf(m.getId().charAt(0)).equals(pesan));
//            System.out.println();
            if (String.valueOf(m.getId().charAt(0)).equals(pesan)) {
                this.nrofDelivered++;
                this.latencies.add(getSimTime() - this.creationTimes.get(m.getId()));
                if (this.maxLatency == 0 && this.minLatency == 0) {
                    this.maxLatency = getSimTime() - this.creationTimes.get(m.getId());
                    this.minLatency = getSimTime() - this.creationTimes.get(m.getId());
                } else {
                    double temp = getSimTime() - this.creationTimes.get(m.getId());
                    if (temp >= this.maxLatency) {
                        this.maxLatency = temp;
                    } else if (temp < this.minLatency) {
                        this.minLatency = temp;
                    }
                }
                this.hopCounts.add(m.getHops().size() - 1);
            }
        }

    }

}
