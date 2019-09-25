package pl.edu.icm.desir.data.exchange;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Test;
import org.springframework.util.ResourceUtils;

import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;

public class RdfModelExtractorTest {

	@Test
	public void test() throws IOException {
		
		File file = ResourceUtils.getFile("classpath:data2.ttl");
		RdfModelExtractor extractor = new RdfModelExtractor(file.getName());
		extractor.parseInputData(new FileInputStream(file));
		assertNotNull("Actors list is empty", extractor.getActors());
		assertTrue("The number of actors is wrong: " + extractor.getActors().size() + " shuld be 5" , extractor.getActors().size() == 5);
		assertNotNull("Events list is null", extractor.getEvents());
		assertTrue("Events list is empty", extractor.getEvents().size() > 0);
		
		Actor actor1 = new Actor("a1", "John Doe");
		assertTrue(actor1.getId() + " is not present", extractor.getActors().contains(actor1));
		
		Actor actor2 = new Actor("a2", "Anthony Monster");
		assertTrue(actor2.getId() + " is not present", extractor.getActors().contains(actor2));

		Actor actor3 = new Actor("a3", "George Someone");
		assertTrue(actor3.getId() + " is not present" , extractor.getActors().contains(actor3));

		Event event1 = new Event("e1", "Very Interesting Article A on the Exemplary RDF Format", null, null);
		Event event2 = new Event("e2", "Very Interesting Article B on the Exemplary RDF Format", null, null);
		Event event3 = new Event("e3", "Not So Interesting Article C on Something Else", null, null);
	
		Actor foundActor1 = extractor.getActors().get(extractor.getActors().indexOf(actor1));
		assertTrue(foundActor1.getId() + " has wrong participation size: " + foundActor1.getParticipation().size(), foundActor1.getParticipation().size() == 2);
		assertTrue(foundActor1.getId() + " does not contain " + event1.getId(), foundActor1.getParticipation().contains(event1));
		assertTrue(foundActor1.getId() + "does not contain" + event2.getId(), foundActor1.getParticipation().contains(event2));

		Actor foundActor2 = extractor.getActors().get(extractor.getActors().indexOf(actor2));
		assertTrue(foundActor2.getId() + " has wrong participation size: " + foundActor2.getParticipation().size(), foundActor2.getParticipation().size() == 2);
		assertTrue(foundActor2.getId() + "does not contain" + event1.getId(), foundActor2.getParticipation().contains(event1));
		assertTrue(foundActor2.getId() + "does not contain" + event3.getId(), foundActor2.getParticipation().contains(event3));
		
		Actor foundActor3 = extractor.getActors().get(extractor.getActors().indexOf(actor3));
		assertTrue(foundActor3.getId() + " has wrong participation size: " + foundActor3.getParticipation().size(), foundActor3.getParticipation().size() == 1);
		assertTrue(foundActor3.getId() + "does not contain" + event2.getId(), foundActor3.getParticipation().contains(event2));

	}

}
