package pl.edu.icm.desir.data.exchange;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.util.ResourceUtils;
import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

public class RdfModelExtractorTest {

    @Test
    public void testParseInputData() throws IOException {
        String filename = "data2.ttl";
        ModelBuilder builder = new RdfModelExtractor(filename);
        builder.parseInputData(RdfModelExtractorTest.class.getResourceAsStream("/" + filename));
        List<Actor> actors = builder.getActors();
        List<Event> events = builder.getEvents();
        Assert.assertEquals(5, actors.size());
        Assert.assertEquals(5, events.size());

        Assert.assertEquals("univ1", actors.get(0).getId());
        Assert.assertEquals("University of Somewhere", actors.get(0).getName());

        Assert.assertEquals("dep1", actors.get(4).getId());
        Assert.assertEquals("Department of Something", actors.get(4).getName());
        Assert.assertEquals(1, actors.get(4).getRelations().size());
        Assert.assertEquals("univ1", actors.get(4).getRelations().get(0).getTargetObject().getId());

        Assert.assertEquals("a1", actors.get(1).getId());
        Assert.assertEquals("John Doe", actors.get(1).getName());
        Assert.assertEquals(3, actors.get(1).getRelations().size());
        Assert.assertEquals("e2", actors.get(1).getRelations().get(0).getTargetObject().getId());
        Assert.assertEquals("e1", actors.get(1).getRelations().get(1).getTargetObject().getId());
        Assert.assertEquals("dep1", actors.get(1).getRelations().get(2).getTargetObject().getId());

        Assert.assertEquals("a2", actors.get(2).getId());
        Assert.assertEquals("Anthony Monster", actors.get(2).getName());
        Assert.assertEquals(2, actors.get(2).getRelations().size());
        Assert.assertEquals("e3", actors.get(2).getRelations().get(0).getTargetObject().getId());
        Assert.assertEquals("e1", actors.get(2).getRelations().get(1).getTargetObject().getId());

        Assert.assertEquals("a3", actors.get(3).getId());
        Assert.assertEquals("George Someone", actors.get(3).getName());
        Assert.assertEquals(1, actors.get(3).getRelations().size());
        Assert.assertEquals("e2", actors.get(3).getRelations().get(0).getTargetObject().getId());

        Assert.assertEquals("e5", events.get(0).getId());
        Assert.assertEquals("Chapter 1 of Book A", events.get(0).getName());
        Assert.assertEquals(2007, events.get(0).getStartPoint().getCalendarTime().getLocalDate().getYear());
        Assert.assertEquals(1, events.get(0).getRelations().size());
        Assert.assertEquals("e4", events.get(0).getRelations().get(0).getTargetObject().getId());

        Assert.assertEquals("e2", events.get(1).getId());
        Assert.assertEquals("Very Interesting Article B on the Exemplary RDF Format", events.get(1).getName());
        Assert.assertEquals(2003, events.get(1).getStartPoint().getCalendarTime().getLocalDate().getYear());
        Assert.assertEquals(1, events.get(1).getRelations().size());
        Assert.assertEquals("e1", events.get(1).getRelations().get(0).getTargetObject().getId());

        Assert.assertEquals("e1", events.get(2).getId());
        Assert.assertEquals("Very Interesting Article A on the Exemplary RDF Format", events.get(2).getName());
        Assert.assertEquals(2001, events.get(2).getStartPoint().getCalendarTime().getLocalDate().getYear());

        Assert.assertEquals("e4", events.get(3).getId());
        Assert.assertEquals("Book A which contains 1 chapter", events.get(3).getName());
        Assert.assertEquals(2007, events.get(3).getStartPoint().getCalendarTime().getLocalDate().getYear());

        Assert.assertEquals("e3", events.get(4).getId());
        Assert.assertEquals("Not So Interesting Article C on Something Else", events.get(4).getName());
        Assert.assertEquals(2007, events.get(4).getStartPoint().getCalendarTime().getLocalDate().getYear());
        Assert.assertEquals(2, events.get(4).getRelations().size());
        Assert.assertEquals("e2", events.get(4).getRelations().get(0).getTargetObject().getId());
        Assert.assertEquals("e1", events.get(4).getRelations().get(1).getTargetObject().getId());
    }

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
	
//		Actor foundActor1 = extractor.getActors().get(extractor.getActors().indexOf(actor1));
//		assertTrue(foundActor1.getId() + " has wrong participation size: " + foundActor1.getParticipation().size(), foundActor1.getParticipation().size() == 2);
//		assertTrue(foundActor1.getId() + " does not contain " + event1.getId(), foundActor1.getParticipation().contains(event1));
//		assertTrue(foundActor1.getId() + "does not contain" + event2.getId(), foundActor1.getParticipation().contains(event2));
//
//		Actor foundActor2 = extractor.getActors().get(extractor.getActors().indexOf(actor2));
//		assertTrue(foundActor2.getId() + " has wrong participation size: " + foundActor2.getParticipation().size(), foundActor2.getParticipation().size() == 2);
//		assertTrue(foundActor2.getId() + "does not contain" + event1.getId(), foundActor2.getParticipation().contains(event1));
//		assertTrue(foundActor2.getId() + "does not contain" + event3.getId(), foundActor2.getParticipation().contains(event3));
//
//		Actor foundActor3 = extractor.getActors().get(extractor.getActors().indexOf(actor3));
//		assertTrue(foundActor3.getId() + " has wrong participation size: " + foundActor3.getParticipation().size(), foundActor3.getParticipation().size() == 1);
//		assertTrue(foundActor3.getId() + "does not contain" + event2.getId(), foundActor3.getParticipation().contains(event2));

	}

}
