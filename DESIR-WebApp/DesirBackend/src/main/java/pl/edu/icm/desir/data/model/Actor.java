package pl.edu.icm.desir.data.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Actor is a non-abstract subtype of entity and is a basic object 
 * that acts in the graph. It exists in time and it can be involved 
 * in both events and relations. Most often it can be understood 
 * as a person or organization (e.g. corporation, institution, 
 * military unit etc.)
 * Lasts from tstart to tend, that are either defined directly by 
 * actor’s own start point (e.g. birth) and end point (e.g. death) 
 * or defined indirectly by the earliest event start and latest event 
 * end of all events related to a given actor.
 * May hold a participation relation to events (e.g. be born, get married, 
 * be married)
 * Needs a type common name that is a descriptive string what type of actor
 * this is (e.g. character, author).
 *
 * @author blazejc
 *
 */
public class Actor extends Entity {
	private Date timelifeStartPoint;
	private Date timelifeEndPoint;
	private String type;

	public Actor(String identifier, String name) {
		super(identifier, name);
	}

	@Override
	boolean isReal() {
		return false;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Date getTimelifeStartPoint() {
		return timelifeStartPoint;
	}

	public void setTimelifeStartPoint(Date timelifeStartPoint) {
		this.timelifeStartPoint = timelifeStartPoint;
	}

	public Date getTimelifeEndPoint() {
		return timelifeEndPoint;
	}

	public void setTimelifeEndPoint(Date timelifeEndPoint) {
		this.timelifeEndPoint = timelifeEndPoint;
	}

	@Override
	public float toFloat() {
		return 0;
	}


}
