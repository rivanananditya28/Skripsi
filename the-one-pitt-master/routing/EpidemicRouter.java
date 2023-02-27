/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package routing;

import core.Settings;
import core.SimScenario;
import java.util.List;
import static movement.MapRouteMovementWithStop.ROUTE_FILE_S;
import static movement.MapRouteMovementWithStop.ROUTE_TYPE_S;
import movement.map.MapRoute;
import core.Coord;
import java.util.LinkedList;
import movement.Path;
import movement.map.MapNode;

/**
 * Epidemic message router with drop-oldest buffer and only single transferring
 * connections at a time.
 */
public class EpidemicRouter extends ActiveRouter {

    
    private List<List<Coord>> coords;
    

    /**
     * Constructor. Creates a new message router based on the settings in the
     * given Settings object.
     *
     * @param s The settings object
     */
    public EpidemicRouter(Settings s) {
        super(s);

        //coords = new LinkedList<List<Coord>>();
        //  System.out.println(""+s);
    }

    /**
     * Copy constructor.
     *
     * @param r The router prototype where setting values are copied from
     */
    protected EpidemicRouter(EpidemicRouter r) {
        super(r);

        //TODO: copy epidemic settings here (if any)
    }

    @Override
    public void update() {
        super.update();
        if (isTransferring() || !canStartTransfer()) {
            return; // transferring, don't try other connections yet
        }

        // Try first the messages that can be delivered to final recipient
        if (exchangeDeliverableMessages() != null) {
            return; // started a transfer, don't try others (yet)
        }

        // then try any/all message to any/all connection
        this.tryAllMessagesToAllConnections();

    }

    @Override
    public EpidemicRouter replicate() {
        return new EpidemicRouter(this);
    }


}
