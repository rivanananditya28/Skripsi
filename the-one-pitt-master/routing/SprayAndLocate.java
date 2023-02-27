/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package routing;

import java.util.*;

import core.*;
import routing.*;

/**
 *
 * @author oreliayacob
 */
public class SprayAndLocate implements RoutingDecisionEngineWithSpray {

    /**
     * identifier for the initial number of copies setting ({@value})
     */
    public static final String NROF_COPIES = "nrofCopies";
    /**
     * identifier for the binary-mode setting ({@value})
     */
    public static final String BINARY_MODE = "binaryMode";
    /**
     * SprayAndWait router's settings name space ({@value})
     */
    public static final String SPRAYANDWAIT_NS = "SprayAndWaitRouter";
    /**
     * Message property key
     */
    public static final String MSG_COUNT_PROPERTY = SPRAYANDWAIT_NS + "."
            + "copies";

    protected int initialNrofCopies;
    protected boolean isBinary;
    //membuat dictionary
    protected Map<DTNHost, GeoInfo> dictionary = new HashMap<DTNHost, GeoInfo>();
    

//    Coord destination;
    public SprayAndLocate(Settings s) {
        initialNrofCopies = s.getInt(NROF_COPIES);
        isBinary = s.getBoolean(BINARY_MODE);
    }

    //copy constructor
    protected SprayAndLocate(SprayAndLocate r) {
        this.initialNrofCopies = r.initialNrofCopies;
        this.isBinary = r.isBinary;
    }

    //@Override
    public void connectionUp(DTNHost thisHost, DTNHost peer) {
        //tukarDictionary(thisHost, peer);
    }

    @Override
    public void connectionDown(DTNHost thisHost, DTNHost peer) {
    }

    @Override
    public void doExchangeForNewConnection(Connection con, DTNHost peer) {
        DTNHost thisHost = con.getOtherNode(peer);
        tukarDictionary(thisHost, peer);
    }

    @Override
    public boolean newMessage(Message m) {
        //bikin pesan baru
        m.addProperty(MSG_COUNT_PROPERTY, new Integer(initialNrofCopies));
        return true;
    }

    @Override
    public boolean isFinalDest(Message m, DTNHost aHost) {
        //determine apakh ahost tu final destination dr m
        return m.getTo() == aHost;
    }

    @Override
    public boolean shouldSaveReceivedMessage(Message m, DTNHost thisHost) {
        // determine if a new message received from a peer should be saved to the host's message store and further forwarded on.

        Integer nrofCopies = (Integer) m.getProperty(MSG_COUNT_PROPERTY);

        assert nrofCopies != null : "Not a SnW message: " + m;

        if (isBinary) {
            
            // binary SnW 
            nrofCopies = (int) Math.ceil(nrofCopies / 2.0);
            //System.out.println(nrofCopies);
        } else {
            // standard SnW
            nrofCopies = 1;
        }

        m.updateProperty(MSG_COUNT_PROPERTY, nrofCopies);
        
        
        return m.getTo() != thisHost;
    }

    @Override
    public boolean shouldDeleteSentMessage(Message m, DTNHost otherHost) {
        //return m.getTo() == otherHost;
        return true;
    }

    @Override
    public boolean shouldDeleteOldMessage(Message m, DTNHost hostReportingOld) {
        //return m.getTo() == hostReportingOld;
        return true;
    }

    @Override
    public RoutingDecisionEngineWithSpray replicate() {
        return new SprayAndLocate(this);
    }

    @Override
    public boolean shouldSendMessageToHost(Message m, DTNHost otherHost, DTNHost thisHost) {
        
       tukarDictionary(thisHost, otherHost);
  
        //jika ketemu sensor tidak dikirim
        
        if (otherHost.toString().startsWith("s") || otherHost.toString().startsWith("t")) {
            return false;
        }
                
        if (m.getTo() == otherHost) {
            return true;
        }
         
        int nrofCopies = (Integer) m.getProperty(MSG_COUNT_PROPERTY);
        if (nrofCopies > 1) { //masih spray phase 
            //reduce amount of copies
         
            if (isBinary) {
                
                // binary SnW 
                nrofCopies = (int) Math.ceil(nrofCopies / 2.0);
            } else {
                // standard SnW
                nrofCopies--;
            }
            m.updateProperty(MSG_COUNT_PROPERTY, nrofCopies);
            return true;
        } else {
            
            SprayAndLocate de = this.getOtherSprayAndLocateDecisionEngine(otherHost);
//            System.out.println(m.getTo());
//            System.out.println(dictionary.containsKey(m.getTo()));
//            System.out.println(dictionary);

            //jika tujuan tidak ada di dictionary peer maka tidak kirim
            if (dictionary.containsKey(m.getTo())) {
                return ShouldSendbySudut(m, thisHost, otherHost);
                
            }
            //jika ada maka jalankan locate
            else {
//                System.out.println("BBB");
                return false;
           }
        }

    }
      
    

    public Coord HitungVector(Coord saatini, Coord tujuan) {

        double x = tujuan.getX() - saatini.getX();
        double y = tujuan.getY() - saatini.getY();
        //HASILNYA VEKTOR DALAM BENTUK (X,Y) karena disimpan dalam bentuk besaran 
        Coord hasil = new Coord(x, y);

        return hasil;
    }

    public double HitungSudut(Coord vektorBus, Coord vektorDestination) {

        double uxv = ((vektorBus.getX() * vektorDestination.getX()) + (vektorBus.getY() * vektorDestination.getY()));
        double uxu = Math.sqrt(Math.pow(vektorBus.getX(), 2) + Math.pow(vektorBus.getY(), 2));
        double vxv = Math.sqrt(Math.pow(vektorDestination.getX(), 2) + Math.pow(vektorDestination.getY(), 2));
        double sudut;
        //jika bis diam atau vektor (0,0) maka sudut dibuat sangat besar supaya pesan tidak di berikan
        if ((vektorBus.getX() == 0) && (vektorBus.getY() == 0)) {
            sudut = Double.MAX_VALUE;
        } else {
            sudut = Math.toDegrees(Math.acos(uxv / (uxu * vxv)));
        }
        if (uxv == 0 && uxu == 0 && vxv == 0) {
            sudut = 0;
        }
        return sudut;
       
    }

    public boolean ShouldSendbySudut(Message m, DTNHost host, DTNHost peer){
        SprayAndLocate de = this.getOtherSprayAndLocateDecisionEngine(peer);
        
        Coord punyaKu;
        try {
            punyaKu = host.getPath().getNextWaypoint();
        } catch (Exception e) {
            punyaKu = host.getLocation();
        }
        
        Coord vektorKu = HitungVector(host.getLocation(), punyaKu);
        Coord vektorKuDest= HitungVector(host.getLocation(), (Coord)dictionary.get(m.getTo()).getLoc());
        
        Coord punyaPeer;
        try {
            punyaPeer = peer.getPath().getNextWaypoint();
        } catch (Exception e) {
            punyaPeer = peer.getLocation();
        }
        
         //SprayAndLocate de = this.getOtherSprayAndLocateDecisionEngine(peer);
       //System.out.println("this : "+host.toString().startsWith("t"));
       //System.out.println(dictionary + "\n");
       //System.out.println("other : "+peer.toString().startsWith("t"));
       //System.out.println(de.dictionary + "\n");
       //System.out.println("lokasi : "+m.getTo());
        
        Coord vektorPeer = HitungVector(peer.getLocation(), punyaPeer);
        Coord vektorPeerDest = HitungVector(peer.getLocation(),(Coord)de.dictionary.get(m.getTo()).getLoc());
        
        double sudutKu = HitungSudut(vektorKu, vektorKuDest);
        double sudutPeer = HitungSudut(vektorPeer, vektorPeerDest);
        
        //System.out.println(host + " : " + sudutKu);
        //System.out.println(peer + " : " + sudutPeer);
        
        if (sudutKu < sudutPeer) {
                return false;
            } else if(sudutKu > sudutPeer){
                return true;
            }else if(sudutKu == sudutPeer){
//                System.out.println("this host :"+ host);
//                System.out.println("other host :"+ peer);
//                System.out.println("apa ini :"+de.dictionary.containsKey(host));
           double speedku = (double)de.dictionary.get(host).getSpeed();
           double speedmu = (double)dictionary.get(peer).getSpeed();
         
            if (speedku > speedmu) {
                return false;
            }else{
            return true;
            }
            }
        return false;
    }
    
    public void updateDictionary (DTNHost peer, GeoInfo peerGeoinfo){
        
        if (dictionary.containsKey(peer)) {
            dictionary.replace(peer, peerGeoinfo);
        } else {
            dictionary.put(peer, peerGeoinfo);
        }
    }
    
    public void tukarDictionary(DTNHost thisHost, DTNHost peer){
    if (!(peer.toString().startsWith("s"))&&!(peer.toString().startsWith("t")) ) {
                           
            GeoInfo<Coord, Coord, Double, Double> thisGeo = new GeoInfo<Coord, Coord, Double, Double>();

            //bikin Geoinfo thisHost
            Coord next;

            //ambil coor selanjutnya, jika tidak ada maka dimasukkan posisi sekarang
            try {
                next = thisHost.getPath().getNextWaypoint();
            } catch (Exception e) {
                next = thisHost.getLocation();
            }
            //this geo isinya data thisHost, dictionary diganti pake punya other tapi bikin method untuk nambahin ke dictionary

            thisGeo.setDir(next);
            thisGeo.setLoc(thisHost.getLocation());
            thisGeo.setSpeed(thisHost.getMovement().getPath().getSpeed());
            thisGeo.setTime(SimClock.getTime());

            SprayAndLocate de = this.getOtherSprayAndLocateDecisionEngine(peer);

            //kirim dictionary thisHost ke peer
            de.updateDictionary(peer, thisGeo);

            //untuk tukar isi dictionary
            for (Map.Entry<DTNHost, GeoInfo> entry : de.dictionary.entrySet()) {
                if (!this.dictionary.containsKey(entry.getKey())){
                    this.dictionary.put(entry.getKey(), entry.getValue());
                } else if((Double) this.dictionary.get(entry.getKey()).getTime() < (Double) entry.getValue().getTime()){
                    this.dictionary.replace(entry.getKey(), entry.getValue());
                }
            }
            for (Map.Entry<DTNHost, GeoInfo> entry : this.dictionary.entrySet()) {
                if (!de.dictionary.containsKey(entry.getKey())){
                    de.dictionary.put(entry.getKey(), entry.getValue());
                } else if((Double) de.dictionary.get(entry.getKey()).getTime() < (Double) entry.getValue().getTime()){
                    de.dictionary.replace(entry.getKey(), entry.getValue());
                }
            }
        }
    }
    
    private SprayAndLocate getOtherSprayAndLocateDecisionEngine(DTNHost h) {
        MessageRouter otherRouter = h.getRouter();
        assert otherRouter instanceof DecisionEngineRouter : "This router only works "
                + " with other routers of same type";

        return (SprayAndLocate) ((DecisionEngineRouter) otherRouter).getDecisionEngine();
    }

}
