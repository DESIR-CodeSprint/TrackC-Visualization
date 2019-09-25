package pl.edu.icm.desir.data.exchange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.assertj.core.api.Condition;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;

public class JsonModelExtractorTest {

	@Test
	public void test() throws IOException {
		File file = ResourceUtils.getFile("classpath:bibsonomy_export_simple.json");
		JsonModelExtractor extractor = new JsonModelExtractor(file.getName());
		extractor.parseInputData(new FileInputStream(file));
		
		assertNotNull("Actors list is empty", extractor.getActors());
		assertTrue("The number of actors is wrong: " + extractor.getActors().size() + " should be 34" , extractor.getActors().size() == 34);
		assertNotNull("Events list is null", extractor.getEvents());
		assertTrue("Events list is empty", extractor.getEvents().size() > 0);
		
		
		assertThat(extractor.getActors()).areExactly(1, new Condition<Actor>() {
             public boolean matches(Actor actor) {
            	 if (actor.getName().equals("Loren G. Terveen")) {
                	 if (actor.getRelations().size() == 1) {
                		 Event event = (Event) actor.getRelations().get(0).getTargetObject();
                		 if (event.getName().equals("Evaluating collaborative filtering recommender systems")) {
                			 return true;
                		 }
                	 }
            	 }
            	 return false;
             }
		 });
	}

}
