package pl.edu.icm.desir.data.model;

import java.util.Date;

/**
 * Part-of is the non-abstract subtype of relation. It describes that subject 
 * entity is part of an object entity. This relation describes encapsulation. 
 * An example of part-of relation might be an institutional hierarchy - 
 * ICM is part of the University of Warsaw, employment - employee is part-of 
 * (works for) employer, or sub-event - workshop is part of conference.
 *    
 * @author blazejc
 *
 */
public class PartOf extends Relation {

	private static final String PARTOF_TYPE = "part-of";
	
	private String role;

	public PartOf(Entity subject, Entity targetObject) {
		super(subject, targetObject);
	}

	public PartOf(String identifier) {
		super(identifier);
	}

	@Override
	String getType() {
		return PARTOF_TYPE;
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
