package pl.edu.icm.desir.data.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileUtils;

import org.apache.log4j.Logger;
import pl.edu.icm.desir.data.model.*;

public class RdfModelExtractor implements ModelBuilder {

	private static final String BASE_URI = "http://desir.icm.edu.pl/";
	private static final String ACTOR_NAMESPACE = "http://desir.icm.edu.pl/actor#";
	private static final String EVENT_NAMESPACE = "http://desir.icm.edu.pl/event#";
	private static final String HAS_NAME = "http://desir.icm.edu.pl/hasName";
	private static final String HAS_TITLE = "http://desir.icm.edu.pl/hasTitle";
	private static final String HAS_ROLE = "http://desir.icm.edu.pl/hasRole";
	private static final String HAS_ACTOR = "http://desir.icm.edu.pl/hasActor";
	private static final String HAS_EVENT = "http://desir.icm.edu.pl/hasEvent";
	private static final String START = "http://desir.icm.edu.pl/start";
	private static final String END = "http://desir.icm.edu.pl/end";
	private static final String SOURCE_ACTOR = "http://desir.icm.edu.pl/sourceActor";
	private static final String TARGET_ACTOR = "http://desir.icm.edu.pl/targetActor";
	private static final String IS_PART_OF = "http://desir.icm.edu.pl/isPartOf";
	private static final String OCCURS = "http://desir.icm.edu.pl/occurs";
	private static final String PARTICIPATES_IN = "http://desir.icm.edu.pl/participatesIn";
	private static final String DEPENDS_ON = "http://desir.icm.edu.pl/dependsOn";
	private static final String PART_OF = "http://desir.icm.edu.pl/partOf";
	private static final String PARTICIPATION = "http://desir.icm.edu.pl/participation";
	private static final DateTimeFormatter YEAR_FORMATTER = new DateTimeFormatterBuilder()
			.appendPattern("yyyy")
			.parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
			.parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
			.toFormatter();
	private List<Actor> actors;
	private List<Event> events;
	private List<Relation> relations;
	private List<PartOf> partOfs;
	private List<Dependency> dependencies;
	private List<Participation> participations;
	private String filename;
	private static final Logger LOG = Logger.getLogger(RdfModelExtractor.class);

	public RdfModelExtractor(String filename) {
		this.filename = filename;
		this.relations = new ArrayList<Relation>();
		this.partOfs = new ArrayList<PartOf>();
		this.participations = new ArrayList<Participation>();
		this.dependencies = new ArrayList<Dependency>();
	}

	public void parseInputData(InputStream in) throws IOException {

		Map<String, Actor> actorsMap = new HashMap<>();
		Map<String, Event> eventsMap = new HashMap<>();
		Map<String, Relation> relationsMap = new HashMap<>();

		Model model = ModelFactory.createDefaultModel();
		String syntax = FileUtils.guessLang(filename) ;
		if ( syntax == null || syntax.equals("") )
			syntax = FileUtils.langXML ;
		RDFReader r = model.getReader(syntax);
		r.setProperty("iri-rules", "strict");
		r.setProperty("error-mode", "strict"); // Warning will be errors.
		r.read(model, in, BASE_URI);
		in.close();

		//Tests to query the model
//		Resource vcard = model.getResource("http://desir.icm.edu.pl/actor#univ1");
//		LOG.info(vcard.getLocalName());
//		LOG.info(vcard.getNameSpace());
//		LOG.info(vcard.getURI());
//		Property isPartOfProperty = model.getProperty("http://desir.icm.edu.pl/", "isPartOf");
//		Statement statement = vcard.getProperty(isPartOfProperty);
//		LOG.info(statement);
//		Property hasNameProperty = model.getProperty("http://desir.icm.edu.pl/", "hasName");
//		statement = vcard.getProperty(hasNameProperty);
//		LOG.info(statement);

		StmtIterator iter = model.listStatements();
		try {
			while (iter.hasNext()) {
				Statement stmt = iter.next();

				Resource subject = stmt.getSubject();
				Resource predicate = stmt.getPredicate();
				RDFNode object = stmt.getObject();
//				Resource objectResource = (Resource)object;

				switch (predicate.getURI()) {
					case HAS_NAME:
						if (actorsMap.containsKey(subject.getURI())) {
							actorsMap.get(subject.getURI()).setName(object.toString());
						} else {
							Actor actor = new Actor(subject.getLocalName(), object.toString());
							actorsMap.put(subject.getURI(), actor);
						}
						break;
					case HAS_TITLE:
						if (eventsMap.containsKey(subject.getURI())) {
							eventsMap.get(subject.getURI()).setName(object.toString());
						} else {
							Event event = new Event(subject.getLocalName(), object.toString(), null, null);
							event.setName(object.toString());
							eventsMap.put(subject.getURI(), event);
						}
						break;
					case IS_PART_OF:
						PartOf relation = null;
						if(subject.getNameSpace().equals(ACTOR_NAMESPACE)) {
							Actor target;
							if (actorsMap.containsKey(object.toString())) {
								target = actorsMap.get(object.toString());
							} else {
								target = new Actor(object.toString(), object.toString());
								actorsMap.put(object.toString(), target);
							}
							Actor actorPartOf;
							if (actorsMap.containsKey(subject.getURI())) {
								actorPartOf = actorsMap.get(subject.getURI());
							} else {
								actorPartOf = new Actor(subject.getLocalName(), object.toString());
								actorsMap.put(subject.getURI(), actorPartOf);
							}
							relation = new PartOf(actorPartOf, target);
						} else if (subject.getNameSpace().equals(EVENT_NAMESPACE)) {
							Event target;
							if (eventsMap.containsKey(object.toString())) {
								target = eventsMap.get(object.toString());
							} else {
								target = new Event(parseIdentifier(object.toString()), object.toString(), null, null);
								eventsMap.put(object.toString(), target);
							}
							Event eventPartOf;
							if (eventsMap.containsKey(subject.getURI())) {
								eventPartOf = eventsMap.get(subject.getURI());
							} else {
								eventPartOf = new Event(subject.getLocalName(), object.toString(), null, null);
								eventsMap.put(subject.getURI(), eventPartOf);
							}
							relation = new PartOf(eventPartOf, target);
						}
						relations.add(relation);
						partOfs.add(relation);
						break;
					case OCCURS:
						SpatiotemporalPoint stPoint = new SpatiotemporalPoint();
						ScaledTime st = new ScaledTime();
						st.setLocalDate(LocalDate.parse(object.toString(), YEAR_FORMATTER));
						stPoint.setCalendarTime(st);
						if (eventsMap.containsKey(subject.getURI())) {
							eventsMap.get(subject.getURI()).setStartPoint(stPoint);
							eventsMap.get(subject.getURI()).setEndPoint(stPoint);
						} else {
							Event event = new Event(subject.getLocalName(), object.toString(), stPoint, stPoint);
							eventsMap.put(subject.getURI(), event);
						}
						break;
					case PARTICIPATES_IN:
						Actor actor;
						if (actorsMap.containsKey(subject.getURI())) {
							actor = actorsMap.get(subject.getURI());
						} else {
							actor = new Actor(subject.getLocalName(), object.toString());
						}

						Event event = new Event(parseIdentifier(object.toString()), object.toString(), null, null);
						if (eventsMap.containsKey(object.toString())) {
							event = eventsMap.get(object.toString());
						}
						Participation participation = new Participation(actor, event, "");
                        relations.add(participation);
                        participations.add(participation);
						actorsMap.put(subject.getURI(), actor);
						eventsMap.put(object.toString(), event);
						break;
					case DEPENDS_ON:
						Event target;
						if (eventsMap.containsKey(object.toString())) {
							target = eventsMap.get(object.toString());
						} else {
							target = new Event(parseIdentifier(object.toString()), object.toString(), null, null);
							eventsMap.put(object.toString(), target);
						}
						Event eventDependsOn;
						if (eventsMap.containsKey(subject.getURI())) {
							eventDependsOn = eventsMap.get(subject.getURI());
						} else {
							eventDependsOn = new Event(subject.getLocalName(), object.toString(), null, null);
							eventsMap.put(subject.getURI(), eventDependsOn);
						}
						Dependency dependency = new Dependency(eventDependsOn, target);
						dependencies.add(dependency);
						relations.add(dependency);
						break;
					case HAS_ROLE:
						Relation relationHasRole;
						if(relationsMap.containsKey(subject.getURI())) {
							relationHasRole = relationsMap.get(subject.getURI());
							if(relationHasRole instanceof PartOf)
								((PartOf)relationHasRole).setRole(object.toString());
							if(relationHasRole instanceof Participation)
								((Participation)relationHasRole).setRole(object.toString());
						} else {
							if(subject.getLocalName().equals(PART_OF)) {
								relationHasRole = new PartOf(subject.getLocalName());
								((PartOf)relationHasRole).setRole(object.toString());
								relationsMap.put(subject.getURI(), relationHasRole);
							} else if(subject.getLocalName().equals(PARTICIPATION)) {
								relationHasRole = new Participation(subject.getLocalName());
								((Participation)relationHasRole).setRole(object.toString());
								relationsMap.put(subject.getURI(), relationHasRole);
							}
						}
						break;
					case START:
						PartOf partOfStart;
						SpatiotemporalPoint stPointStart = new SpatiotemporalPoint();
						ScaledTime stStart = new ScaledTime();
						stStart.setLocalDate(LocalDate.parse(object.toString(), YEAR_FORMATTER));
						stPointStart.setCalendarTime(stStart);
						if(relationsMap.containsKey(subject.getURI())) {
							partOfStart = (PartOf) relationsMap.get(subject.getURI());
							partOfStart.setStartpoint(stPointStart);
						} else {
							partOfStart = new PartOf(subject.getLocalName());
							partOfStart.setStartpoint(stPointStart);
							relationsMap.put(subject.getURI(), partOfStart);
						}
						break;
					case END:
						PartOf partOfEnd;
						SpatiotemporalPoint stPointEnd = new SpatiotemporalPoint();
						ScaledTime stEnd = new ScaledTime();
						stEnd.setLocalDate(LocalDate.parse(object.toString(), YEAR_FORMATTER));
						stPointEnd.setCalendarTime(stEnd);
						if(relationsMap.containsKey(subject.getURI())) {
							partOfEnd = (PartOf) relationsMap.get(subject.getURI());
							partOfEnd.setEndpoint(stPointEnd);
						} else {
							partOfEnd = new PartOf(subject.getLocalName());
							partOfEnd.setEndpoint(stPointEnd);
							relationsMap.put(subject.getURI(), partOfEnd);
						}
						break;
					case SOURCE_ACTOR:
						PartOf partOfSourceActor;
						Actor actorSource;
						if (actorsMap.containsKey(object.toString())) {
							actorSource = actorsMap.get(object.toString());
						} else {
							actorSource = new Actor(parseIdentifier(object.toString()), object.toString());
						}
						if(relationsMap.containsKey(subject.getURI())) {
							partOfSourceActor = (PartOf) relationsMap.get(subject.getURI());
							partOfSourceActor.setSubject(actorSource);
						} else {
							partOfSourceActor = new PartOf(subject.getLocalName());
							partOfSourceActor.setSubject(actorSource);
							relationsMap.put(subject.getURI(), partOfSourceActor);
						}
						//actorSource.addRelation(partOfSourceActor);
						actorsMap.put(object.toString(), actorSource);
						relations.add(partOfSourceActor);
						break;
					case TARGET_ACTOR:
						PartOf partOfTargetActor;
						Actor actorTarget;
						if (actorsMap.containsKey(object.toString())) {
							actorTarget = actorsMap.get(object.toString());
						} else {
							actorTarget = new Actor(parseIdentifier(object.toString()), object.toString());
						}
						if(relationsMap.containsKey(subject.getURI())) {
							partOfTargetActor = (PartOf) relationsMap.get(subject.getURI());
							partOfTargetActor.setTargetObject(actorTarget);
						} else {
							partOfTargetActor = new PartOf(subject.getLocalName());
							partOfTargetActor.setTargetObject(actorTarget);
							relationsMap.put(subject.getURI(), partOfTargetActor);
						}
                        actorsMap.put(object.toString(), actorTarget);
						relations.add(partOfTargetActor);
						break;
					case HAS_ACTOR:
						Participation participationHasActor;
						Actor actorHasActor;
						if (actorsMap.containsKey(object.toString())) {
							actorHasActor = actorsMap.get(object.toString());
						} else {
							actorHasActor = new Actor(parseIdentifier(object.toString()), object.toString());
						}
						if(relationsMap.containsKey(subject.getURI())) {
							participationHasActor = (Participation) relationsMap.get(subject.getURI());
							participationHasActor.setSubject(actorHasActor);
						} else {
							participationHasActor = new Participation(subject.getLocalName());
							participationHasActor.setSubject(actorHasActor);
						}
						//actorHasActor.addRelation(participationHasActor);
						relationsMap.put(subject.getURI(), participationHasActor);
						break;
					case HAS_EVENT:
						Participation participationHasEvent;
						Event eventHasEvent;
						if (eventsMap.containsKey(object.toString())) {
							eventHasEvent = eventsMap.get(object.toString());
						} else {
							eventHasEvent = new Event(parseIdentifier(object.toString()), object.toString(), null,
									null);
						}
						if(relationsMap.containsKey(subject.getURI())) {
							participationHasEvent = (Participation) relationsMap.get(subject.getURI());
							participationHasEvent.setTargetObject(eventHasEvent);
						} else {
							participationHasEvent = new Participation(subject.getLocalName());
							participationHasEvent.setTargetObject(eventHasEvent);
						}
						relationsMap.put(subject.getURI(), participationHasEvent);
						break;
				}
			}
		} finally {
			if (iter != null)
				iter.close();
		}

		actors = new ArrayList<>(actorsMap.values());
		events = new ArrayList<>(eventsMap.values());
		relations.addAll(relationsMap.values());
	}

	private static String parseIdentifier(String fullIdentifier) {
		return fullIdentifier.substring(fullIdentifier.lastIndexOf("#") + 1);
	}

	@Override
	public List<Actor> getActors() {
		return actors;
	}

	@Override
	public List<Event> getEvents() {
		return events;
	}

	@Override
	public List<Relation> getRelations() {
		return relations;
	}

	@Override
	public List<PartOf> getPartOfs() {
		return partOfs;
	}

	@Override
	public List<Dependency> getDependencies() {
		return dependencies;
	}

	@Override
	public List<Participation> getParticipations() {
		return participations;
	}
}