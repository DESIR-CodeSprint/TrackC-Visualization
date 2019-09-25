package pl.edu.icm.desir.data.exchange;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

import pl.edu.icm.desir.data.model.*;
import pl.edu.icm.desir.data.utils.DataUtils;

public class JsonModelExtractor implements ModelBuilder {

	private String filename;
	private List<Actor> actors;
	private List<Event> events;

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

	private void generateModelFromPosts(List<Post<? extends Resource>> posts) {

		Map<PersonName, Actor> actorsMap = new HashMap<PersonName, Actor>();
		Map<Post<BibTex>, Event> eventsMap = new HashMap<Post<BibTex>, Event>();

		actors = new ArrayList<>();
		for (final Post<? extends Resource> apost : posts) {
			Post<BibTex> post = (Post<BibTex>) apost;
			SpatiotemporalPoint stPoint = new SpatiotemporalPoint();
			ScaledTime st = new ScaledTime();
			st.setLocalDate(DataUtils.convertToLocalDate(post.getDate()));
			Event event = new Event(DataUtils.createHashWithTimestamp(post.getResource().getTitle()),
					post.getResource().getTitle(), stPoint, stPoint);
			event.setName(post.getResource().getTitle());
			eventsMap.put(post, event);
			for (PersonName personName:post.getResource().getAuthor()) {
				Actor actor;
				if (actorsMap.containsKey(personName)) {
					actor = actorsMap.get(personName);
				}
				actor =
						new Actor(DataUtils.createHashWithTimestamp(personName.getFirstName() + " " + personName.getLastName()),
								personName.getFirstName() + " " + personName.getLastName());
				Participation participation = new Participation(actor, event, "");
				actorsMap.put(personName, actor);
			}
		}
		actors = new ArrayList<Actor>(actorsMap.values());
		events = new ArrayList<Event>(eventsMap.values());

	}

}
