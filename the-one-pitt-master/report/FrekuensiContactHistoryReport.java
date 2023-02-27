///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package report;
//
//import core.DTNHost;
//import core.SimScenario;
//import java.util.List;
//import routing.DecisionEngineRouter;
//import routing.MessageRouter;
//import routing.RoutingDecisionEngine;
//import routing.community.FrekuensiConnectionHistoryEngine;
//
///**
// *
// * @author Wen
// */
//public class FrekuensiContactHistoryReport extends Report {
//
//    @Override
//    public void done() {
//        List<DTNHost> nodes = SimScenario.getInstance().getHosts();
//        for (DTNHost h : nodes) {
//            MessageRouter r = h.getRouter();
//            if (!(r instanceof DecisionEngineRouter)) {
//                continue;
//            }
////            RoutingDecisionEngine de = ((DecisionEngineRouter) r).getDecisionEngine();
//            if (!(de instanceof FrekuensiConnectionHistoryEngine)) {
//                continue;
//            }
//            FrekuensiConnectionHistoryEngine cd = (FrekuensiConnectionHistoryEngine) de;
//            Integer nodeComm = cd.getFrekuensi();
//            write(h + " " + nodeComm);
//        }
//        super.done();
//    }
//}
