package pl.edu.icm.desir.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.comparator.ComparableComparator;

import pl.edu.icm.desir.data.model.ScaledTime.Scale;
import pl.edu.icm.desir.data.model.utility.TrajectoryHelper;
import pl.edu.icm.jscic.dataarrays.DataObjectInterface;

/**
 * Entity is one of two basic abstract types of the data structure.
 * It provides a set of generic features and methods for inheriting types,
 * as serves as an abstraction for typesetting of various methods.
 * There are two basic subtypes of entity - actor and event. 
 * May be connected to another entity by relation.

 * @author blazejc
 *
 */
public abstract class Entity implements DataObjectInterface, Serializable {

	public enum GeneralizationLevel {
		DEFAULT
	}

	private String id;
	private String externalId;
	private String typeCommonName = "actor";
	private String name;

	//obligatory fields forced by constructor
	private SpatiotemporalPoint startPoint;
	private SpatiotemporalPoint endPoint;

	/*
	 * Level of generalization (e.g. 0 - states and centuries,
	 * 1 - years and armies etc.)
	 */
	private GeneralizationLevel level; //change to comparable enum, for the time being limit enum to single option e.g.
	// "DEFAULT"
	private List<Relation> relations;
	private Map<String, String> metadata;


	public Entity(String identifier, String name) {
		this.id = identifier;
		this.name = name;
	}

	public Entity(String identifier, String name, SpatiotemporalPoint startPoint, SpatiotemporalPoint endPoint) {
		this.id = identifier;
		this.name = name;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
	}

	/**
	 * That can start a philosophical
	 * discussion of the type “is the second world war real or abstract?
	 */
	abstract boolean isReal();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
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

	/*
	 * argument sorted by caledarTime or abstractTime / copmarator jako argument / defaulyowe po calendarTime jeśli istnieje, abstractTime jeśli nie istnieje calendar
	 */
	public Set<SpatiotemporalPoint> getTrajectory(boolean sortByCalendarTime, Comparator<SpatiotemporalPoint> comparator) {

		//generowanie posortowanej listy punktów: z entity start/end i ze wszystkich relacji biorąc pod uwagę zarówno target realcji, jak i start/end relacji
		//domyślny komparator czasu musi uwzględniać najmniejszą wspólną valid skalę czasu

		return TrajectoryHelper.getTrajectory(sortByCalendarTime, comparator);

	}

	public Set<String> getPath() {
		//wrapper do utility tłumaczącego trajectory na path
		return TrajectoryHelper.getPath(this);
	}


	public ScaledTime[] getCalendarTimeline() {
		//wrapper do utility tłumaczącego trajectory na time
		return TrajectoryHelper.getCalendarTimeline(this);
	}


	//przyjmijmy że wszystkie ScvaledTime mają to samo validity scale
	public ScaledTime[] getCalendarTimeline(Scale scale) {
		////wrapper do utility tłumaczącego trajectory na time
		return TrajectoryHelper.getCalendarTimeline(this, scale);
	}

	public float[] getAbstractTimeline() {
		//wrapper do utility tłumaczącego trajectory na time
		return TrajectoryHelper.getAbstractTimeline(this);
	}

	public GeneralizationLevel getLevel() {
		return level;
	}

	public void setLevel(GeneralizationLevel level) {
		this.level = level;
	}

	public List<Relation> getRelations() {
		return relations;
	}

	public void setRelations(List<Relation> relations) {
		this.relations = relations;
	}

	public void addRelation(Relation relation) {
		if (relations == null) {
			relations = new ArrayList<Relation>();
		}
		relations.add(relation);
	}

	public void removeRelation(Relation relation) {
		if (relations == null) {
			relations = new ArrayList<Relation>();
		}
		relations.remove(relation);
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}

	public void setMetadata(Map<String, String> metadata) {
		this.metadata = metadata;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Entity) {
			Entity entity = (Entity)obj;
			if (!entity.getId().equals(this.getId())) {
				return false;
			}
			if (!entity.getName().equals(this.getName())) {
				return false;
			}
			return true;
		} else {
			return false;
		}
	}

}
