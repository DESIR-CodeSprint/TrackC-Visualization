package pl.edu.icm.desir.data.model;

import java.io.Serializable;
import java.util.Map;
import pl.edu.icm.jscic.dataarrays.DataObjectInterface;

/**
 * Relation is the second of two basic abstract types of the data structure.
 * It also provides a set of generic features and methods for inheriting types,
 * as serves as a connector between entities. A relation is always created 
 * as a triple entity - relation - entity, and is directional. 
 * It can be understood as subject - predicate - object construct. 
 * There are three subtypes of relation - dependency, participation and part-of.
 * @author blazejc
 *
 */
public abstract class Relation implements DataObjectInterface, Serializable {

	String id;
	String typeCommonName;
	String name;
	
	Entity subject;
	Entity targetObject;
	
	SpatiotemporalPoint startpoint;
	SpatiotemporalPoint endpoint;
	
	Map<String, String> metadata;

	public Relation(Entity subject, Entity targetObject) {
		this.subject = subject;
		this.targetObject = targetObject;
		this.subject.addRelation(this);
	}
	
	abstract String getType();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTypeCommonName() {
		return typeCommonName;
	}

	public void setTypeCommonName(String typeCommonName) {
		this.typeCommonName = typeCommonName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Entity getSubject() {
		return subject;
	}

	public void setSubject(Entity subject) {
		this.subject = subject;
	}

	public Entity getTargetObject() {
		return targetObject;
	}

	public void setTargetObject(Entity targetObject) {
		this.targetObject = targetObject;
	}

	public SpatiotemporalPoint getStartpoint() {
		return startpoint;
	}

	public void setStartpoint(SpatiotemporalPoint startpoint) {
		this.startpoint = startpoint;
	}

	public SpatiotemporalPoint getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(SpatiotemporalPoint endpoint) {
		this.endpoint = endpoint;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}
	
}
