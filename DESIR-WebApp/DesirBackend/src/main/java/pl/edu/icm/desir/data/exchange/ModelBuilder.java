package pl.edu.icm.desir.data.exchange;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Set;

import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;

public interface ModelBuilder {

	void parseInputData(InputStream in) throws IOException;
	
	List<Actor> getActors();

	List<Event> getEvents();

}
