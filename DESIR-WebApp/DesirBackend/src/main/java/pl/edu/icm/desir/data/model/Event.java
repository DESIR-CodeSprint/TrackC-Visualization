package pl.edu.icm.desir.data.model;

/**
 * Event is an non-abstract subtype of entity representing a generic 
 * time period (in a specific case this can be a moment i.e. a point in time). 
 * It can be either lasting in time (start point is different from end point) 
 * or momentary (start point equal to end point). One of the spatiotemporal 
 * points might be empty to represent events that have undefined start or end.
 * Needs a type common name that is a descriptive string what type of event 
 * this is (e.g. marriage, birth, publication, dialogue).
 * 
 * @author blazejc
 *
 */
public class Event extends Entity {

	String type;
	
	SpatiotemporalPoint startPoint;
	SpatiotemporalPoint endPoint;
	
	public Event(SpatiotemporalPoint startPoint, SpatiotemporalPoint endPoint) {
		super(startPoint, endPoint);
	}

	public boolean isMomentary() {
		return false;
	}
	
	public boolean isLasting() {
		return false;
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

	public SpatiotemporalPoint getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(SpatiotemporalPoint startPoint) {
		this.startPoint = startPoint;
	}

	public SpatiotemporalPoint getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(SpatiotemporalPoint endPoint) {
		this.endPoint = endPoint;
	}

    @Override
    public float toFloat()
    {
        return 0;
    }
	
	
}
