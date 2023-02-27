/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing.test;

import core.Connection;
import core.DTNHost;
import core.Message;
import core.Settings;
import core.SimClock;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import routing.DecisionEngineRouter;
import routing.MessageRouter;
import routing.RoutingDecisionEngine;
import routing.community.Duration;

/**
 *
 * @author Wen
 */
public class RoutingAverageConntactPeriod implements RoutingDecisionEngine {

    protected Map<DTNHost, Double> startTimestamps;
    protected Map<DTNHost, List<Duration>> connHistory;

    private double AverageThis, AveragePeer;

    public RoutingAverageConntactPeriod(Settings s) {
        AveragePeer = 0;
        AverageThis = 0;
    }

    public RoutingAverageConntactPeriod(RoutingAverageConntactPeriod proto) {
        startTimestamps = new HashMap<DTNHost, Double>();
        connHistory = new HashMap<DTNHost, List<Duration>>();
    }

    @Override
    public void connectionUp(DTNHost thisHost, DTNHost peer) {
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
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
        DTNHost myHost = con.getOtherNode(peer);
        RoutingAverageConntactPeriod de = this.getOtherDecisionEngine(peer);

        this.startTimestamps.put(peer, SimClock.getTime());
        de.startTimestamps.put(myHost, SimClock.getTime());

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
        RoutingAverageConntactPeriod de = getOtherDecisionEngine(otherHost);
        List<Duration> waktu;
        if (de.connHistory.containsKey(dest)) {
            waktu = de.connHistory.get(dest);
            for (Duration dest1 : waktu) {
                AveragePeer = (AveragePeer + (dest1.getEnd() - dest1.getStart()) / waktu.size());
            }

        } else if (this.connHistory.containsKey(dest)) {
            waktu = this.connHistory.get(dest);
            for (Duration dest1 : waktu) {
                AverageThis = (AverageThis + (dest1.getEnd() - dest1.getStart())) / waktu.size();
            }
        }
        if (AveragePeer > AverageThis) {
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
        return new RoutingAverageConntactPeriod(this);
    }

    private RoutingAverageConntactPeriod getOtherDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only works "
                + " with other routers of same type";

        return (RoutingAverageConntactPeriod) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
    }
}
