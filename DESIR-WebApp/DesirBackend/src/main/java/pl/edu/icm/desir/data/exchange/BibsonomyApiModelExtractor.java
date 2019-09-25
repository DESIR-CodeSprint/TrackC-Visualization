package pl.edu.icm.desir.data.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bibsonomy.common.enums.Filter;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.common.enums.SearchType;
import org.bibsonomy.model.BibTex;
import org.bibsonomy.model.PersonName;
import org.bibsonomy.model.Post;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.client.RestLogicFactory;

import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;
import pl.edu.icm.desir.data.model.ScaledTime;
import pl.edu.icm.desir.data.model.SpatiotemporalPoint;
import pl.edu.icm.desir.data.utils.DataUtils;

public class BibsonomyApiModelExtractor implements ModelBuilder {

	String login;
	String apikey;
	GroupingEntity grouping;
	String groupingName;
	List<String> tags;
	String hash;
	String search;
	SearchType searchType;
	Set<Filter> filters;
	Order order;
	Date startDate;
	Date endDate;
	int start;
	int end;
	
	
	private List<Actor> actors;
	private List<Event> events;

	public BibsonomyApiModelExtractor(String login, String apikey, GroupingEntity grouping, String groupingName, List<String> tags, String hash, String search, SearchType searchType, Set<Filter> filters, Order order, Date startDate, Date endDate, int start, int end) {
		this.login = login;
		this.apikey = apikey;
		this.grouping = grouping;
		this.groupingName = groupingName;
		this.tags = tags;
		this.hash = hash;
		this.search = search;
		this.searchType = searchType;
		this.filters = filters;
		this.order = order;
		this.startDate = startDate;
		this.endDate = endDate;
		this.start = start;
		this.end = end;

	}

	@Override
	public void parseInputData(InputStream in) throws IOException {

        final RestLogicFactory rlf = new RestLogicFactory();
        final LogicInterface logic = rlf.getLogicAccess(login, apikey);
        final List<Post<BibTex>> posts = logic.getPosts(BibTex.class, grouping, groupingName, tags, hash, search, searchType, filters, order, startDate, endDate, start, end);
		
       	generateModelFromPosts(posts);
	}

	@Override
	public List<Actor> getActors() {
		return actors;
	}

	@Override
	public List<Event> getEvents() {
		return events;
	}


    private void generateModelFromPosts(List<Post<BibTex>> posts) {

		Map<PersonName, Actor> actorsMap = new HashMap<PersonName, Actor>();
		Map<Post<BibTex>, Event> eventsMap = new HashMap<Post<BibTex>, Event>();
    	
    	actors = new ArrayList<>();
        for (final Post<BibTex> post : posts) {
        	SpatiotemporalPoint stPoint = new SpatiotemporalPoint();
			ScaledTime st = new ScaledTime();
			st.setLocalDate(DataUtils.convertToLocalDate(post.getDate()));
			Event event = new Event(stPoint, stPoint);
			event.setName(post.getResource().getTitle());
			eventsMap.put(post, event);
            for (PersonName personName:post.getResource().getAuthor()) {
            	if (actorsMap.containsKey(personName)) {
            		Actor actor = actorsMap.get(personName);
            		
            		actor.getEvents().add(event);
            	}
            	Actor actor = new Actor(null, null);
            	actor.setName(personName.getFirstName() + " " + personName.getLastName());
				actor.setEvents(new ArrayList<Event>());
				actor.getEvents().add(event);
				actorsMap.put(personName, actor);
            }
        }
		actors = new ArrayList<Actor>(actorsMap.values());
		events = new ArrayList<Event>(eventsMap.values());

    }

}
