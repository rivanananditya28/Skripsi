/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.test;

import core.*;
import java.util.*;
import routing.*;
import routing.community.Duration;
import routing.community.FrekuensiConnectionHistoryEngine;

/**
 *
 * @author Wen
 */
public class RoutingFrekuensiConntactHistory implements RoutingDecisionEngine, FrekuensiConnectionHistoryEngine {

    protected Map<DTNHost, Double> startTimestamps;
    protected Map<DTNHost, List<Duration>> connHistory;
    private int encouterThis, encounterPeer;

    public RoutingFrekuensiConntactHistory(Settings s) {
        this.encounterPeer = 0;
        this.encouterThis = 0;
    }

    public RoutingFrekuensiConntactHistory(RoutingFrekuensiConntactHistory proto) {
        startTimestamps = new HashMap<DTNHost, Double>();
        connHistory = new HashMap<DTNHost, List<Duration>>();
    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer) {
    }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
        DTNHost myHost = con.getOtherNode(peer);
        RoutingFrekuensiConntactHistory de = this.getOtherDecisionEngine(peer);

        this.startTimestamps.put(peer, SimClock.getTime());
        de.startTimestamps.put(myHost, SimClock.getTime());

    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer) {

        double time = startTimestamps.get(peer);
        double etime = SimClock.getTime();

        // Find or create the connection history list
        List<Duration> history;
        if (!connHistory.containsKey(peer)) {
            history = new LinkedList<Duration>();
            connHistory.put(peer, history);
        } else {
            history = connHistory.get(peer);
        }

        // add this connection to the list
        if (etime - time > 0) {
            history.add(new Duration(time, etime));
        }

    }

    @Override
    public boolean newMessage(Message m) {
        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost) {
        return m.getTo() == aHost;
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost) {
        return m.getTo() != thisHost;
    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost) {
        if (m.getTo() == otherHost) {
            return true; // deliver to final destination
        }
        DTNHost dest = m.getTo();
        RoutingFrekuensiConntactHistory de = getOtherDecisionEngine(otherHost);

        if (de.connHistory.containsKey(dest)) {
            encounterPeer = de.connHistory.get(dest).size();
        } else if (this.connHistory.containsKey(dest)) {
            encouterThis = this.connHistory.get(dest).size();
        }
        if (encounterPeer > encouterThis) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost) {
        return true;
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld) {
        return true;
    }

    @Override
    public RoutingDecisionEngine replicate() {
        return new RoutingFrekuensiConntactHistory(this);
    }

    private RoutingFrekuensiConntactHistory getOtherDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only works "
                + " with other routers of same type";

        return (RoutingFrekuensiConntactHistory) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
    }

    @Override
    public Integer getFrekuensi() {
        return this.connHistory.size();
    }

}
