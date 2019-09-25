package pl.edu.icm.desir.data.exchange;

import org.junit.Assert;
import org.junit.Test;
import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;

import java.io.IOException;
import java.util.List;

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

        Assert.assertEquals("e1", events.get(2).getId());
        Assert.assertEquals("Very Interesting Article A on the Exemplary RDF Format", events.get(2).getName());
        Assert.assertEquals(2001, events.get(2).getStartPoint().getCalendarTime().getLocalDate().getYear());

        Assert.assertEquals("e4", events.get(3).getId());
        Assert.assertEquals("Book A which contains 1 chapter", events.get(3).getName());
        Assert.assertEquals(2007, events.get(3).getStartPoint().getCalendarTime().getLocalDate().getYear());

        Assert.assertEquals("e3", events.get(4).getId());
        Assert.assertEquals("Not So Interesting Article C on Something Else", events.get(4).getName());
        Assert.assertEquals(2007, events.get(4).getStartPoint().getCalendarTime().getLocalDate().getYear());
    }

}
