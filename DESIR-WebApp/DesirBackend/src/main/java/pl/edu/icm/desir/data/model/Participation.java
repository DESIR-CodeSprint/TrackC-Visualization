package pl.edu.icm.desir.data.model;

import java.util.Date;

/**
 * Participation is the non-abstract subtype of relation. It describes 
 * that subject actor participates in object event. An example 
 * of a participation might be an authorship - as scientific publications 
 * may be understood as momentary events, authorship is a participation 
 * of an author (actor) in an event. Another example of participation 
 * might be a marriage - two actors participate in a lasing event of marriage.
 * Connects actor and event (actor participates in event).
 * Can depend on time (e.g. actor can participate in a lasting event only 
 * for a certain period)
 * Optional attributes
 * role (String) - describes the role of the participating actor in the event 
 * (e.g. parent in birth event, child in birth event, author in publication event)
 * 
 * @author blazejc
 *
 */
public class Participation extends Relation {
    
    private static long objectsCounter = 0;
	static final String PARTICIPATION_TYPE = "participation";

	Actor actor;
	Event event;
	
	/*
	 * actor can participate in lasting event only for certain period
	 */
	Date start;
	Date end;
	
	String role;
	
	
	
	public Participation(Actor actor, Event event, String role) {
		super(actor, event);
		this.actor = actor;
		this.event = event;
		this.role = role;
	}

	@Override
	String getType() {
		return PARTICIPATION_TYPE;
	}

	public Actor getActor() {
		return actor;
	}

	public void setActor(Actor actor) {
		this.actor = actor;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

    @Override
    public float toFloat()
    {
        return 0;
    }



}
