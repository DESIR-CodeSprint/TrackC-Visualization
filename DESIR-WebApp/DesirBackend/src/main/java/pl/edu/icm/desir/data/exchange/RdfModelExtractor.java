package pl.edu.icm.desir.data.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.jena.atlas.RuntimeIOException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileUtils;

import org.apache.log4j.Logger;
import pl.edu.icm.desir.data.model.*;

public class RdfModelExtractor implements ModelBuilder {

	private static final String BASE_URI = "http://desir.icm.edu.pl/";
	private static final String ACTOR_NAMESPACE = "http://desir.icm.edu.pl/actor#";
	private static final String EVENT_NAMESPACE = "http://desir.icm.edu.pl/event#";
	private static final String HAS_NAME = "http://desir.icm.edu.pl/hasName";
	private static final String HAS_TITLE = "http://desir.icm.edu.pl/hasTitle";
	private static final String PART_OF = "http://desir.icm.edu.pl/partOf";
	private static final String OCCURRED = "http://desir.icm.edu.pl/occurred";
	private static final String PARTICIPATES_IN = "http://desir.icm.edu.pl/participatesIn";
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
	}

	public void parseInputData(InputStream in) throws IOException {

		Map<String, Actor> actorsMap = new HashMap<>();
		Map<String, Event> eventsMap = new HashMap<>();
		Map<String, Dependency> dependenciesMap = new HashMap<>();
		Map<String, Participation> participationsMap = new HashMap<>();

		Model model = ModelFactory.createDefaultModel();
		String syntax = FileUtils.guessLang(filename) ;
		if ( syntax == null || syntax.equals("") )
			syntax = FileUtils.langXML ;
		RDFReader r = model.getReader(syntax);
		r.setProperty("iri-rules", "strict");
		r.setProperty("error-mode", "strict"); // Warning will be errors.
		r.read(model, in, BASE_URI);
		in.close();

		StmtIterator iter = model.listStatements();
		try {
			while (iter.hasNext()) {
				Statement stmt = iter.next();

				Resource subject = stmt.getSubject();
				Resource predicate = stmt.getPredicate();
				RDFNode object = stmt.getObject();

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
					case PART_OF:
						Actor target;
						if (actorsMap.containsKey(object.toString())) {
							target = actorsMap.get(object.toString());
						} else {
							target = new Actor(object.toString(), object.toString());
							actorsMap.put(subject.getURI(), target);
						}
						Actor actorPartOf;
						if (actorsMap.containsKey(subject.getURI())) {
							actorPartOf = actorsMap.get(subject.getURI());
						} else {
							actorPartOf = new Actor(subject.getLocalName(), object.toString());
							actorsMap.put(subject.getURI(), actorPartOf);
						}
						PartOf relation = new PartOf(actorPartOf, target);
						relations.add(relation);
						partOfs.add(relation);
						break;
					case OCCURRED:
						SpatiotemporalPoint stPoint = new SpatiotemporalPoint();
						ScaledTime st = new ScaledTime();
						st.setLocalDate(LocalDate.parse(object.toString(), YEAR_FORMATTER));
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
						actor.getParticipation().add(event);
						actorsMap.put(subject.getURI(), actor);
						eventsMap.put(object.toString(), event);
						break;
				}
			}
		} finally {
			if (iter != null)
				iter.close();
		}

		actors = new ArrayList<>(actorsMap.values());
		events = new ArrayList<>(eventsMap.values());
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
