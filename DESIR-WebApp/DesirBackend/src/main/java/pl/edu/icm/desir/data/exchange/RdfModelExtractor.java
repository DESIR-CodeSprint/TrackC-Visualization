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

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.RDFReader;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileUtils;

import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;
import pl.edu.icm.desir.data.model.ScaledTime;
import pl.edu.icm.desir.data.model.SpatiotemporalPoint;

public class RdfModelExtractor implements ModelBuilder {

	static final String BASE_URI = "http://desir.icm.edu.pl/";
	private static final DateTimeFormatter YEAR_FORMATTER = new DateTimeFormatterBuilder()
		     .appendPattern("yyyy")
		     .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
		     .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
		     .toFormatter();
	private List<Actor> actors;
	private List<Event> events;
	private String filename;
	
	
	public RdfModelExtractor(String filename) {
		this.filename = filename;
	}

	public void parseInputData(InputStream in) throws IOException {

		Map<String, Actor> actorsMap = new HashMap<String, Actor>();
		Map<String, Event> eventsMap = new HashMap<String, Event>();

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

				Resource s = stmt.getSubject();
				Resource p = stmt.getPredicate();
				RDFNode o = stmt.getObject();

				if (p.toString().equals("http://desir.icm.edu.pl/hasName")) {
					if (actorsMap.containsKey(s.toString())) {
						actorsMap.get(s.toString()).setName(o.toString());
					} else {
						Actor actor = new Actor(null, null);
						actor.setName(o.toString());
						actorsMap.put(s.toString(), actor);
					}
				}

				if (p.toString().equals("http://desir.icm.edu.pl/hasTitle")) {
					if (eventsMap.containsKey(s.toString())) {
						eventsMap.get(s.toString()).setName(o.toString());
					} else {
						Event event = new Event(null, null);
						event.setName(o.toString());
						eventsMap.put(s.toString(), event);
					}
				}
				if (p.toString().equals("http://desir.icm.edu.pl/occured")) {

					SpatiotemporalPoint stPoint = new SpatiotemporalPoint();
					ScaledTime st = new ScaledTime();
					st.setLocalDate(LocalDate.parse(o.toString(), YEAR_FORMATTER));
                    stPoint.setCalendarTime(st);
					if (eventsMap.containsKey(s.toString())) {
						eventsMap.get(s.toString()).setStartPoint(stPoint);
						eventsMap.get(s.toString()).setEndPoint(stPoint);
					} else {
						Event event = new Event(stPoint, stPoint);
						eventsMap.put(s.toString(), event);
					}

				}

				if (p.toString().equals("http://desir.icm.edu.pl/participatesIn")) {
					Actor actor = new Actor(null, null);
					actor.setEvents(new ArrayList<Event>());
					
					if (actorsMap.containsKey(s.toString())) {
						actor = actorsMap.get(s.toString());
					}

					Event event = new Event(null, null);
					if (eventsMap.containsKey(o.toString())) {
						event = eventsMap.get(o.toString());
					}
					actor.getEvents().add(event);
					actorsMap.put(s.toString(), actor);
					eventsMap.put(o.toString(), event);
				}

			}
		} finally {
			if (iter != null)
				iter.close();
		}
		
		actors = new ArrayList<Actor>(actorsMap.values());
		events = new ArrayList<Event>(eventsMap.values());
	}

	@Override
	public List<Actor> getActors() {
		return actors;
	}

	@Override
	public List<Event> getEvents() {
		return events;
	}

}
