package pl.edu.icm.desir.data.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
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
import org.bibsonomy.model.Resource;
import org.bibsonomy.model.enums.Order;
import org.bibsonomy.model.logic.LogicInterface;
import org.bibsonomy.rest.client.RestLogicFactory;

import pl.edu.icm.desir.data.model.*;
import pl.edu.icm.desir.data.utils.DataUtils;

public class BibsonomyApiModelExtractor implements ModelBuilder {

	private static final DateTimeFormatter YEAR_FORMATTER = new DateTimeFormatterBuilder()
		     .appendPattern("yyyy")
		     .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
		     .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
		     .toFormatter();

	
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
	private List<Relation> relations;
	private List<Participation> participations;

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

	@Override
	public List<Relation> getRelations() {
		return null;
	}

	@Override
	public List<PartOf> getPartOfs() {
		return null;
	}

	@Override
	public List<Dependency> getDependencies() {
		return null;
	}

	@Override
	public List<Participation> getParticipations() {
		return null;
	}

	private void generateModelFromPosts(List<Post<BibTex>> posts) {

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
			Event event = new Event(DataUtils.createHashWithTimestamp(title),
                                    title, stPoint, stPoint);
			eventsMap.put(post, event);
			for (PersonName personName:post.getResource().getAuthor()) {
                Actor actor;
				String name = personName.getFirstName() + " " + personName.getLastName();
				if (actorsMap.containsKey(name)) {
					actor = actorsMap.get(name);
				} else {
                    actor = new Actor(DataUtils.createHashWithTimestamp(name), name);
                }
				Participation participation = new Participation(actor, event, "");
				actorsMap.put(name, actor);
			}
		}
		actors = new ArrayList<Actor>(actorsMap.values());
		events = new ArrayList<Event>(eventsMap.values());
	}

}
