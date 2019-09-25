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
	static final String PARTICIPATION_TYPE = "participation";
	
	private String role;
	
	public Participation(Actor actor, Event event, String role) {
		super(actor, event);
		this.role = role;
	}

	public Participation(String identifier) {
		super(identifier);
	}

	@Override
	String getType() {
		return PARTICIPATION_TYPE;
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
    
    public Actor getActor() {
        return (Actor) this.subject;
    }
    
    public Event getEvent() {
        return (Event) this.targetObject;
    }



}
