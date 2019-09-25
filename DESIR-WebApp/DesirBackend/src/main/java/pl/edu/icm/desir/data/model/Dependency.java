package pl.edu.icm.desir.data.model;

import java.util.List;

/**
 * Dependency is the non-abstract subtype of relation. It describes 
 * that subject entity depends on object entity. However, 
 * the most important dependency is between events. 
 * An example of a dependency might be a citation - as scientific 
 * publications may be understood as momentary events, citation 
 * is a dependency of one event on another.
 * Connects any two entities.
 * Dependency does not depend on time, so its start/end 
 * spatiotemporal points are empty.
 * 
 * @author blazejc
 *
 */
public class Dependency extends Relation {

    private static long objectsCounter = 0;
	static final String DEPENDENCY_TYPE = "dependency";
	
	List<Entity> entities;
    
    public Dependency() {
        super();
        this.id = "Dependency"+(objectsCounter++);
    }
	
	@Override
	String getType() {
		return DEPENDENCY_TYPE;
	}

	@Override
	public SpatiotemporalPoint getStartpoint() {
		return null;
	}

	@Override
	public SpatiotemporalPoint getEndpoint() {
		return null;
	}

    @Override
    public float toFloat()
    {
        return 0; //TBD
    }
	
	

}
