package pl.edu.icm.desir.data.exchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bibsonomy.common.exceptions.InternServerException;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.util.data.NoDataAccessor;
import org.bibsonomy.rest.exceptions.BadRequestOrResponseException;
import org.bibsonomy.rest.renderer.RendererFactory;
import org.bibsonomy.rest.renderer.RenderingFormat;
import org.bibsonomy.rest.renderer.UrlRenderer;

import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;
import pl.edu.icm.desir.data.model.ScaledTime;
import pl.edu.icm.desir.data.model.SpatiotemporalPoint;
import pl.edu.icm.desir.data.utils.DataUtils;

public class JsonModelExtractor implements ModelBuilder {

	private String filename;
	private List<Actor> actors;
	private List<Event> events;
    
    private static final DateTimeFormatter YEAR_FORMATTER = new DateTimeFormatterBuilder()
		     .appendPattern("yyyy")
		     .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
		     .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
		     .toFormatter();

	public JsonModelExtractor(String filename) {
		this.filename = filename;
	}

	@Override
	public void parseInputData(InputStream in) throws IOException {
        final RenderingFormat renderingFormat;
        final UrlRenderer urlRenderer = new UrlRenderer("");
        final RendererFactory rendererFactory = new RendererFactory(urlRenderer);
        renderingFormat = RenderingFormat.JSON;

        final BufferedReader reader = new BufferedReader(new InputStreamReader(in, "utf-8"));

        try {
        	List<Post<? extends Resource>> posts =  rendererFactory.getRenderer(renderingFormat).parsePostList(reader, NoDataAccessor.getInstance());
        	generateModelFromPosts(posts);
        } catch (final InternServerException ex) {
            reader.close();
            throw new BadRequestOrResponseException(ex);
        }

	}

	@Override
	public List<Actor> getActors() {
		return actors;
	}

	@Override
	public List<Event> getEvents() {
		return events;
	}


    private void generateModelFromPosts(List<Post<? extends Resource>> posts) {

		Map<String, Actor> actorsMap = new HashMap<String, Actor>();
		Map<Post<BibTex>, Event> eventsMap = new HashMap<Post<BibTex>, Event>();
    	
    	actors = new ArrayList<>();
        for (final Post<? extends Resource> apost : posts) {
        	Post<BibTex> post = (Post<BibTex>) apost; 
            if(post.getResource().getAuthor().size() == 0)
                continue;
            String title = post.getResource().getTitle();
            boolean found = false;
            for(Event e : eventsMap.values()) {
                if(e.getName().equals(title)) {
                    found = true;
                    break;
                }
            }
            if(found)
                continue;
            
        	SpatiotemporalPoint stPoint = new SpatiotemporalPoint();
			ScaledTime st = new ScaledTime();
			st.setLocalDate(LocalDate.parse(post.getResource().getYear(), YEAR_FORMATTER));
            stPoint.setCalendarTime(st);
			Event event = new Event(stPoint, stPoint);
			event.setName(post.getResource().getTitle());
			eventsMap.put(post, event);
            for (PersonName personName:post.getResource().getAuthor()) {
                String name = personName.getFirstName() + " " + personName.getLastName();
            	if (actorsMap.containsKey(name)) {
            		Actor actor = actorsMap.get(name);
            		actor.getEvents().add(event);
            	} else {
                    Actor actor = new Actor(null, null);
                    actor.setName(name);
                    actor.setEvents(new ArrayList<Event>());
                    actor.getEvents().add(event);
                    actorsMap.put(name, actor);
                }
            }
        }
		actors = new ArrayList<Actor>(actorsMap.values());
		events = new ArrayList<Event>(eventsMap.values());

    }

}
