/* 
 * Copyright 2010 Aalto University, ComNet
 * Released under GPLv3. See LICENSE.txt for details. 
 */
package input;

import core.DTNHost;
import java.util.ArrayList;

/**
 * A message related external event
 */
public abstract class MessageEventBusTj extends ExternalEvent {
	/** address of the node the message is from */
	protected ArrayList<Integer> fromAddr;
	/** address of the node the message is to */
	protected int toAddr;
	/** identifier of the message */
	protected ArrayList<String> id;
	
	/**
	 * Creates a message  event
	 * @param from Where the message comes from
	 * @param to Who the message goes to 
	 * @param id ID of the message
	 * @param time Time when the message event occurs
	 */
	public MessageEventBusTj(ArrayList<Integer> from, int to, ArrayList<String> id, double time) {
		super(time);
		this.fromAddr = from;
		this.toAddr= to;
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "MSG @" + this.time + " " + id;
	}
}
