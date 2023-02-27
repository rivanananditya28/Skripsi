///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package report;
//
//import core.DTNHost;
//import core.Settings;
//import core.SimScenario;
//import java.util.List;
//import routing.ActiveRouter;
//import routing.AmbilCoords;
//import core.Coord;
//import java.util.ArrayList;
//import java.util.Iterator;
//import movement.Path;
//import movement.map.MapRoute;
//import routing.DecisionEngineRouter;
//import routing.EpidemicRouter;
//import routing.MessageRouter;
//import routing.RoutingDecisionEngine;
//
///**
// *
// * @author WINDOWS_X
// */
//public class ReportCobaCoba extends Report {
//
//    public ReportCobaCoba() {
//        Settings settings = getSettings();
//        init();
//    }
//
//    @Override
//    protected void init() {
//        super.init();
//    }
//
//    public void done() {
//        List<DTNHost> nodes = SimScenario.getInstance().getHosts();
//
//        for (DTNHost host : nodes) {
//            
//          //  Coord a = host.getLocation();
//
//     //   List<Coord> c = (List<Coord>)a.;
//           Path b = host.getMovement().getPath();
//          ArrayList<Coord> c = (ArrayList<Coord>) b.getCoords();
//            String cetak = "Koordinat : " + b.toString();
//
//            for (int i = 0; i < c.size(); i++) {
//               c.get(i).toString();
//
//            }
//            write(host + ", " + cetak);
//        }
//        super.done();
//    }
//}
