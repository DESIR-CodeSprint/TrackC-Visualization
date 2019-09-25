package pl.edu.icm.desir.ReadBibSonomy;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.junit.Test;
import org.springframework.util.ResourceUtils;

import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;

public class ReaderTest {


	@Test
    public void testParseFile() {
 
		/*
		Map<String, Actor> actors = new HashMap<String, Actor>();
		Map<String, Event> events = new HashMap<String, Event>();
		
		FileManager.get().addLocatorClassLoader(ReaderTest.class.getClassLoader());
		try {
			FileManager.get().addLocatorFile(ResourceUtils.getFile("classpath:.").getPath());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Model m = FileManager.get().loadModel("data1.ttl");
		StmtIterator iter = m.listStatements();
        try {
            while ( iter.hasNext() ) {
                Statement stmt = iter.next();
                
                Resource s = stmt.getSubject();
                Resource p = stmt.getPredicate();
                RDFNode o = stmt.getObject();
                
                if (p.toString().equals("<http://desir.icm.edu.pl/hasName>")) {
                	actors.put(s.toString(), new Actor(o.toString()));
                }
                
                if (p.toString().equals("<http://desir.icm.edu.pl/hasTitle>")) {
                	if (events.containsKey(s.toString())) {
                		events.get(s.toString()).setName(o.toString());
                	} else {
                		Event event = new Event(o.toString(), (String)null);
                		events.put(s.toString(), event);
                	}
                }
                if (p.toString().equals("<http://desir.icm.edu.pl/occurred>")) {
                	
                	if (events.containsKey(s.toString())) {
                		events.get(s.toString()).setDate(o.toString());
                	} else {
                		Event event = new Event(null, o.toString());
                		events.put(s.toString(), event);
                	}
                	
                }
                
                if (p.toString().equals("<http://desir.icm.edu.pl/participatesIn>")) {
                	if (!actors.containsKey(s.toString())) {
                		actors.put(s.toString(), new Actor());
                	}
                	
                	if (events.containsKey(o.toString())) {
                		events.get(o.toString()).getActors().add(actors.get(s.toString()));
                	} else {
                		Event event = new Event();
                		event.getActors().add(actors.get(s.toString()));
                		events.put(s.toString(), event);
                		
                	}
                }
                
            }
        } finally {
            if ( iter != null ) iter.close();
		}
		 */

	}


}
