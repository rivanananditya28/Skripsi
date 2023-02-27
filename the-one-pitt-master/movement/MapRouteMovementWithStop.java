/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package movement;

import java.util.List;

import core.SettingsError;
import movement.map.DijkstraPathFinder;
import movement.map.MapNode;
import movement.map.MapRoute;
import core.Coord;
import core.Settings;
import core.SimError;
import java.util.ArrayList;
import gui.playfield.NodeGraphic;
import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Graphics2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Map based movement model that uses predetermined paths within the map area.
 * Nodes using this model (can) stop on every route waypoint and find their way
 * to next waypoint using {@link DijkstraPathFinder}. There can be different
 * type of routes; see {@link #ROUTE_TYPE_S}.
 */
public class MapRouteMovementWithStop extends MapBasedMovement implements
        SwitchableMovement {

    /**
     * Per node group setting used for selecting a route file ({@value})
     */
    public static final String ROUTE_FILE_S = "routeFile";
    /**
     * Per node group setting used for selecting a route's type ({@value}).
     * Integer value from {@link MapRoute} class.
     */
    public static final String ROUTE_TYPE_S = "routeType";

    /**
     * Per node group setting for selecting which stop (counting from 0 from the
     * start of the route) should be the first one. By default, or if a negative
     * value is given, a random stop is selected.
     */
    public static final String ROUTE_FIRST_STOP_S = "routeFirstStop";

    public static final String STOP_FILE_S = "stopFile";

    public static final String STOP_TIME = "stopTime";

    /**
     * the Dijkstra shortest path finder
     */
    private DijkstraPathFinder pathFinder;

    /**
     * Prototype's reference to all routes read for the group
     */
    private List<MapRoute> allRoutes = null;
    /**
     * next route's index to give by prototype
     */
    private Integer nextRouteIndex = null;
    /**
     * Index of the first stop for a group of nodes (or -1 for random)
     */
    private int firstStopIndex = -1;

    /**
     * Route of the movement model's instance
     */
    private MapRoute route;

    private List<MapNode> stopPath;
    private double minStopTime;
    private double maxStopTime;

    /**
     * Creates a new movement model based on a Settings object's settings.
     *
     * @param settings The Settings object where the settings are read from
     */
    public MapRouteMovementWithStop(Settings settings) {
        super(settings);
        String fileName = settings.getSetting(ROUTE_FILE_S);
        int type = settings.getInt(ROUTE_TYPE_S);
        allRoutes = MapRoute.readRoutes(fileName, type, getMap());
        nextRouteIndex = 0;
        pathFinder = new DijkstraPathFinder(getOkMapNodeTypes());
        this.route = this.allRoutes.get(this.nextRouteIndex).replicate();
        if (this.nextRouteIndex >= this.allRoutes.size()) {
            this.nextRouteIndex = 0;

        }
        String fileStop = settings.getSetting(STOP_FILE_S);
        try {
            stopPath = readStopPoint(fileStop);
        } catch (IOException ex) {
            Logger.getLogger(MapRouteMovementWithStop.class.getName()).log(Level.SEVERE, null, ex);
        }
//        String colorName = settings.getSetting(COLOR_CHANGE);
//        color = new NodeGraphic(colorName);
//        
        if (settings.contains(ROUTE_FIRST_STOP_S)) {
            this.firstStopIndex = settings.getInt(ROUTE_FIRST_STOP_S);
            if (this.firstStopIndex >= this.route.getNrofStops()) {
                throw new SettingsError("Too high first stop's index ("
                        + this.firstStopIndex + ") for route with only "
                        + this.route.getNrofStops() + " stops");
            }
        }
        double[] times;
        if (settings.contains(STOP_TIME)) {
            times = settings.getCsvDoubles(STOP_TIME, 2);
        } else {
            times = DEF_WAIT_TIMES;
        }

        minStopTime = times[0];
        maxStopTime = times[1];
        
 //       System.out.println("semua jalur = "+fileName);
    }

    /**
     * Copyconstructor. Gives a route to the new movement model from the list of
     * routes and randomizes the starting position.
     *
     * @param proto The MapRouteMovement prototype
     */
    protected MapRouteMovementWithStop(MapRouteMovementWithStop proto) {
        super(proto);
        this.route = proto.allRoutes.get(proto.nextRouteIndex).replicate();
        this.firstStopIndex = proto.firstStopIndex;

        if (firstStopIndex < 0) {
            /* set a random starting position on the route */
            this.route.setNextIndex(rng.nextInt(route.getNrofStops() - 1));
        } else {
            /* use the one defined in the config file */
            this.route.setNextIndex(this.firstStopIndex);
        }

        this.pathFinder = proto.pathFinder;

        proto.nextRouteIndex++; // give routes in order
        if (proto.nextRouteIndex >= proto.allRoutes.size()) {
            proto.nextRouteIndex = 0;
        }

        this.minStopTime = proto.minStopTime;
        this.maxStopTime = proto.maxStopTime;
        this.stopPath = proto.stopPath;
//        this.color = proto.color;
    }

    @Override
    public Path getPath() {
        Path p = new Path(generateSpeed());
        MapNode to = route.nextStop();
        double a = route.nextStop().getLocation().distance(new Coord(7502, 5207));
      double b = route.nextStop().getLocation().distance(new Coord(9647,1620));
        for (MapNode stopPath1 : stopPath) {
            if (to.getLocation().equals(stopPath1.getLocation())) {
                minWaitTime = minStopTime;
                maxWaitTime = maxStopTime;
                break;
            } else {
                minWaitTime = 0;
                maxWaitTime = 0;
            }
        }
        List<MapNode> nodePath = pathFinder.getShortestPath(lastMapNode, to);

        // this assertion should never fire if the map is checked in read phase
        assert nodePath.size() > 0 : "No path from " + lastMapNode + " to "
                + to + ". The simulation map isn't fully connected";

        for (MapNode node : nodePath) { // create a Path from the shortest path
            p.addWaypoint(node.getLocation());
        }

        lastMapNode = to;
       
     //   System.out.println("A = ");
       // System.out.println("Sumber: " + to.getLocation().getX() + "x" + to.getLocation().getY() + "y" + " Distance: " + a);
          
        return p;
     

        // stopPath.add(new MapNode(new Coord(13464.68340406493, 4529.4286311043525)));// berhenti di Bandara Adisucipto
//        stopPath.add(new MapNode(new Coord(13464.683771983122, 4529.429080190909)));// berhenti di Bandara Adisucipto
//        stopPath.add(new MapNode(new Coord(19266.397705878782, 1345.5364542240604)));// berhenti di Candi Prambanan
//        stopPath.add(new MapNode(new Coord(8959.478388944415, 1433.663757911801)));// berhenti di Terminal Condongcatur
//        stopPath.add(new MapNode(new Coord(5129.961727381085, 388.6810000000005)));// berhenti di Terminal Jombor
//        stopPath.add(new MapNode(new Coord(37.42558716438907, 6524.173837266606)));// berhenti di Park n Ride Gamping
//        stopPath.add(new MapNode(new Coord(4605.476393451463, 6628.852926804137)));// berhenti di Terminal Ngabean
//        stopPath.add(new MapNode(new Coord(4605.476037654997, 6628.801747877669)));// berhenti di Terminal Ngabean
//        stopPath.add(new MapNode(new Coord(4605.45560349944, 6628.949268324977)));// berhenti di Terminal Ngabean
//        stopPath.add(new MapNode(new Coord(4605.906191935791, 6628.315608767085)));// berhenti di Terminal Ngabean
//        stopPath.add(new MapNode(new Coord(8586.069702359946, 10037.966503924243)));// berhenti di Terminal Giwangan
    }

    /**
     * Returns the first stop on the route
     */
    @Override
    public Coord getInitialLocation() {
        if (lastMapNode == null) {
            lastMapNode = route.nextStop();
        }

        return lastMapNode.getLocation().clone();
    }

    @Override
    public Coord getLastLocation() {
        if (lastMapNode != null) {
            return lastMapNode.getLocation().clone();
        } else {
            return null;
        }
    }

    @Override
    public MapRouteMovementWithStop replicate() {
        return new MapRouteMovementWithStop(this);
    }

    /**
     * Returns the list of stops on the route
     *
     * @return The list of stops
     */
    public List<MapNode> getStops() {
        return route.getStops();
    }

    public int getRouteType() {
        return route.CIRCULAR;
    }

    public static List<MapNode> readStopPoint(String fileName) throws FileNotFoundException, IOException {
        File file = new File(fileName);
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        String temp = null;
        while ((line = br.readLine()) != null) {
            temp = line;
        }
        List<MapNode> stopPoint = new ArrayList<>();
        String[] simpan = temp.split(",");
        for (String temp2 : simpan) {
            String[] temp4 = temp2.split(" ");
            double x = Double.parseDouble(temp4[0]);
            double y = Double.parseDouble(temp4[1]);
            stopPoint.add(new MapNode(new Coord(x, y)));
        }
        return stopPoint;
    }

}
