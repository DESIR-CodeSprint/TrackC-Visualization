/* ***** BEGIN LICENSE BLOCK *****
 *  
 * VisNowPlugin-DESIR
 * Copyright (C) 2018 onward University of Warsaw, ICM
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * ***** END LICENSE BLOCK ***** */
package pl.edu.icm.desir.data.graph;

import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bibsonomy.model.PersonName;

import org.springframework.stereotype.Service;
import pl.edu.icm.desir.ForcePlacement.ConvertDESIRtoVisNow;
import pl.edu.icm.desir.ForcePlacement.DESIRGradValDP;
import pl.edu.icm.desir.ForcePlacement.DESIRcoparticipation;
import pl.edu.icm.desir.ForcePlacement.GraphComponents;

import pl.edu.icm.desir.ForcePlacement.IntVectorHeapSort;
import pl.edu.icm.desir.data.DataBlock;
import pl.edu.icm.desir.data.exchange.ModelBuilder;
import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;
import pl.edu.icm.desir.data.model.Participation;
import pl.edu.icm.desir.data.wrappermodel.ActorWrapper;
import pl.edu.icm.desir.data.wrappermodel.EventWrapper;
import pl.edu.icm.desir.data.wrappermodel.ParticipationWrapper;
import pl.edu.icm.jlargearrays.FloatLargeArray;
import pl.edu.icm.jlargearrays.IntLargeArray;
import pl.edu.icm.jlargearrays.LargeArrayType;
import pl.edu.icm.jlargearrays.LargeArrayUtils;
import pl.edu.icm.jlargearrays.ObjectLargeArray;
import pl.edu.icm.jlargearrays.StringLargeArray;
import pl.edu.icm.jscic.CellArray;
import pl.edu.icm.jscic.CellSet;
import pl.edu.icm.jscic.IrregularField;
import pl.edu.icm.jscic.cells.CellType;
import pl.edu.icm.jscic.dataarrays.DataArray;
import pl.edu.icm.jscic.dataarrays.DataArraySchema;
import pl.edu.icm.jscic.dataarrays.DataArrayType;
import pl.edu.icm.jscic.dataarrays.IntDataArray;
import pl.edu.icm.jscic.dataarrays.ObjectDataArray;
import pl.edu.icm.jscic.dataarrays.StringDataArray;
import pl.edu.icm.visnow.lib.basic.writers.FieldWriter.FieldWriterFileFormat;
import pl.edu.icm.visnow.lib.utils.io.VisNowFieldWriter;
import pl.edu.icm.visnow.lib.utils.numeric.minimization.ConjugateGradientsDoublePrecision;
import pl.edu.icm.visnow.lib.utils.numeric.minimization.ConjugateGradientsParameters;

@Service
public class GraphGenerator {

	public GraphGenerator() {
	}

	private List<Participation> generateInteractionsModel(List<Actor> actors) {
		List<Participation> edges = new ArrayList<>();
		for (Actor actor : actors) {
			for (Event event : actor.getEvents()) {
                boolean found = false;
                for(Participation edge : edges) {
                    if(edge.getActor().getName().equals(actor.getName()) && 
                        edge.getEvent().getName().equals(event.getName())) {
                        found = true;
                        break;
                    }  
                }
                if(!found) {
                    Participation relation = new Participation(actor, event, "");
                    edges.add(relation);
                }
			}
		}
		return edges;
	}

	public IrregularField generateGraphDataFromModel(ModelBuilder modelBuilder) {

		List<Actor> actors = modelBuilder.getActors();
		List<Event> events = modelBuilder.getEvents();
		List<Participation> relation = generateInteractionsModel(actors);
		List<Interaction> interactions = generateInteractionsFromModel(relation);
		ObjectLargeArray la_actors = new ObjectLargeArray(actors.size());
		StringLargeArray la_names = new StringLargeArray(actors.size());
		IntLargeArray la_ids = new IntLargeArray(actors.size());
		int i = 0;
		for (Actor next : actors) {
			la_actors.set(i, next);
			la_names.set(i, next.getName());
			la_ids.set(i, i);
			i++;
		}
		IrregularField field = new IrregularField(actors.size());
		field.setCurrentCoords(
				(FloatLargeArray) LargeArrayUtils.generateRandom(LargeArrayType.FLOAT, 3 * actors.size()));
		ObjectDataArray actorsDataArray = new ObjectDataArray(la_actors,
				new DataArraySchema("actors", DataArrayType.FIELD_DATA_OBJECT, actors.size(), 1));
		field.addComponent(actorsDataArray);
		StringDataArray names = new StringDataArray(la_names,
				new DataArraySchema("names", DataArrayType.FIELD_DATA_STRING, actors.size(), 1));
		field.addComponent(names);
		IntDataArray ids = new IntDataArray(la_ids,
				new DataArraySchema("ids", DataArrayType.FIELD_DATA_INT, actors.size(), 1));
		field.addComponent(ids);

		CellSet cs_interactions = new CellSet("interactions");
		int[] segments = new int[interactions.size() * 2];
		int[] indices = new int[interactions.size()];
		for (int j = 0; j < indices.length; j++)
			indices[j] = j;
		int i1, i2, s = 0;
		ObjectLargeArray la_edges = new ObjectLargeArray(interactions.size());
		for (Interaction edge : interactions) {
			la_edges.set(s, edge);
			String[] actorsInvolved = edge.getActors().toArray(new String[edge.getActors().size()]);
			i1 = -1;
			i2 = -1;
			for (int j = 0; j < actors.size(); j++) {
				Actor a3 = (Actor) la_actors.get(j);
				if (i1 < 0 && a3.getName().equals(actorsInvolved[0])) {
					i1 = j;
				}
				if (i2 < 0 && a3.getName().equals(actorsInvolved[1])) {
					i2 = j;
				}
				if (i1 >= 0 && i2 >= 0) {
					break;
				}
			}
			if (i1 < i2) {
				segments[2 * s] = i1;
				segments[2 * s + 1] = i2;
			} else {
				segments[2 * s] = i2;
				segments[2 * s + 1] = i1;
			}
			s++;
		}
		IntVectorHeapSort.sort(segments, indices, 2);
		CellArray ca_interactions = new CellArray(CellType.SEGMENT, segments, null, null);
		ca_interactions.setDataIndices(indices);
		cs_interactions.setCellArray(ca_interactions);
		cs_interactions.addComponent(DataArray.create(la_edges, 1, "edges"));
		float[] degrees = new float[interactions.size()];
		for (int j = 0; j < degrees.length; j++)
			degrees[j] = ((Interaction) la_edges.get(j)).getNames().size();
		cs_interactions.addComponent(DataArray.create(degrees, 1, "edge_degree"));
		field.addCellSet(cs_interactions);

		return field;
	}

	private List<Interaction> generateInteractionsFromModel(List<Participation> relations) {
        List<Interaction> edges = new ArrayList<>();
        int idx;
        for (Participation relation1:relations) {
			for (Participation relation2:relations) {
				if (!relation1.equals(relation2)) {
					if (relation1.getEvent().equals(relation2.getEvent())) {
                        if ((idx = edges.indexOf(new Interaction(relation1.getActor().getName(), relation2.getActor().getName()))) > -1) {
                            edges.get(idx).addName(relation1.getEvent().getName());
                        } else {
                            Interaction edge = new Interaction(relation1.getActor().getName(), relation2.getActor().getName());
                            edge.addName(relation1.getEvent().getName());
                            edges.add(edge);
                        }

					}
				}
			}
		}
		return edges;
	}
    
    public DataBlock generatePlacedDataBlockFromModel(ModelBuilder modelBuilder) {
        ActorWrapper.init();
        EventWrapper.init();
        ParticipationWrapper.init();
        
		List<Actor> actors = modelBuilder.getActors();
		List<Event> events = modelBuilder.getEvents();
   		List<Participation> participations = generateInteractionsModel(actors);
		//List<Interaction> interactions = generateInteractionsFromModel(participations);
        
        for(Participation p : participations) {
            Event e = p.getEvent();
            if(!events.contains(e)) {
                System.out.println("huhu: "+e.toString());
            }
        }
        
        
        Map<Actor,ActorWrapper> actorsMap = new HashMap<>();
        Map<Event,EventWrapper> eventsMap = new HashMap<>();
        Map<Participation,ParticipationWrapper> participationsMap = new HashMap<>();
        
        //wrap model
        for(Participation participation : participations) {
            if(participationsMap.containsKey(participation))
                continue;
            
            Actor actor = participation.getActor();
            ActorWrapper wrappedActor = actorsMap.get(actor);
            if(wrappedActor == null) {
                wrappedActor = new ActorWrapper(actor.getName());
                actorsMap.put(actor, wrappedActor);
            }
              
            Event event = participation.getEvent();
            EventWrapper wrappedEvent = eventsMap.get(event);
            if(wrappedEvent == null) {
                wrappedEvent = new EventWrapper(event.getName(), event.getStartPoint().getCalendarTime().getLocalDate()); 
                //WHOOPS! wrapped events work only for LocalDates :(
                eventsMap.put(event, wrappedEvent);
            }
            if(!wrappedEvent.getActors().contains(wrappedActor.getName()))
                wrappedEvent.addActor(wrappedActor);
            
            ParticipationWrapper wrappedParticipation = new ParticipationWrapper(wrappedActor, wrappedEvent);
            if(!wrappedActor.getParticipations().contains(wrappedParticipation))
                wrappedActor.addParticipation(wrappedParticipation); 
            participationsMap.put(participation, wrappedParticipation);
        }
        
        
        for(Actor actor : actors) {
            if(actorsMap.containsKey(actor))
                continue;
            ActorWrapper wrappedActor = new ActorWrapper(actor.getName());
            actorsMap.put(actor, wrappedActor);
        }
        
        for(Event event : events) {
            if(eventsMap.containsKey(event))
                continue;
            EventWrapper wrappedEvent = new EventWrapper(event.getName(), event.getStartPoint().getCalendarTime().getLocalDate()); 
            //WHOOPS! wrapped events work only for LocalDates :(
            eventsMap.put(event, wrappedEvent);
        }
        
        
        Set<ActorWrapper> wrappedActorsSet = new HashSet<>(actorsMap.values());
        Set<EventWrapper> wrappedEventsSet = new HashSet<>(eventsMap.values());
        Set<ParticipationWrapper> wrappedParticipationsSet = new HashSet<>(participationsMap.values()); 
        
        
        
        //check wrapping
        if(actors.size() != wrappedActorsSet.size()) {
            System.err.println("ERROR while wrapping actors");
            return null;
        }
        if(events.size() != wrappedEventsSet.size()) {
            System.err.println("ERROR while wrapping events");
            return null;
        }
        if(participations.size() != wrappedParticipationsSet.size()) {
            System.err.println("ERROR while wrapping participations");
            return null;
        }

        
        DESIRcoparticipation result = new DESIRcoparticipation(wrappedActorsSet, wrappedEventsSet, wrappedParticipationsSet);
        GraphComponents.createComponentIndices(result); 
        //graph is ready but has no placement
        

        //prepare for placement
        double[] pointCoords = new double[2 * (result.getNActors() + result.getNEvents())];

        //optimization parameters
        float CONNECTED_R = .4f;
        float CONNECTED_C = 1.f;
        float UNCONNECTED_C = .1f;
        float CENTER_SHIFT_C = 1.f;
            
        DESIRGradValDP gradVal = new DESIRGradValDP(result,CONNECTED_R, CONNECTED_C,CENTER_SHIFT_C, UNCONNECTED_C);

        for (EventWrapper event : wrappedEventsSet) {
            int l = event.getEventIndex();
            float[] location = event.getLocation();
            for (int i = 0; i < location.length; i++)
                pointCoords[2 * l + i] = location[i];
        }

        for (ActorWrapper actor : wrappedActorsSet) {
            int l = actor.getActorIndex() + events.size();
            float[] location = actor.getLocation();
            for (int i = 0; i < location.length; i++)
                pointCoords[2 * l + i] = location[i];
        }

        //do placement optimization
        ConjugateGradientsParameters cgParams = new ConjugateGradientsParameters();
        double[] val = new double[1];
        double[] h = new double[] {.01};

        ConjugateGradientsDoublePrecision cg = new ConjugateGradientsDoublePrecision(cgParams, pointCoords.length);
        cg.minimum_cg(pointCoords, val, .1, .1, .1, h, new int[] {100}, 5, gradVal);

        //copy coords from placement to wrapped model
        for (EventWrapper event : wrappedEventsSet) {
            int l = event.getEventIndex();
            float[] location = event.getLocation();
            for (int i = 0; i < location.length; i++)
                location[i] = (float)pointCoords[2 * l + i];
        }

        for (ActorWrapper actor : wrappedActorsSet) {
            int l = actor.getActorIndex() + events.size();
            float[] location = actor.getLocation();
            for (int i = 0; i < location.length; i++)
                location[i] = (float)pointCoords[2 * l + i];
        }

        IrregularField field = ConvertDESIRtoVisNow.generateFieldFromCoparticipation(result);
//        //save VisNow field for testing
//        try {
//            VisNowFieldWriter.writeField(field, "/Users/babor/tmp/desir_simplest.vns", FieldWriterFileFormat.SERIALIZED, true);
//        } catch (FileSystemException ex) {
//            ex.printStackTrace();
//        }
        
        //needed for DataBLock
        float[] coords = null; 
        int[] actorNodeIndices = null;    
        int[] eventNodeIndices = null;       
        String[] nodeDataIDs = null;
        
        int[] segments = null;
        int[] actorSegmentIndices = null;
        int[] eventSegmentIndices = null;
        int[] participSegmentIndices = null;
        String[] segmentDataIDs = null;
        
        int[] quads = null;
        String[] quadDataIDs = null;
        
        
        //field
        //nNodes = nEvent + nParticipations
        //component "actor name" has actor names over nodes (on actor nodes, "" on other)
        //component "event name" has event names over nodes (on event nodes, "" on other)
        //component "range" has number of event participants over nodes (on event nodes, 0 on actor nodes)
        

        //use field to generate DataBlock
        int nNodes = (int) field.getNNodes();
        String[] allActorNames = ((StringDataArray)field.getComponent("actor_name")).getRawArray().getData();
        String[] allEventNames = ((StringDataArray)field.getComponent("event_name")).getRawArray().getData();
        int[] allEventRanges = ((IntDataArray)field.getComponent("range")).getRawArray().getData();
        int[] allItemIndices = ((IntDataArray)field.getComponent("item_index")).getRawArray().getData();
        
        coords = field.getCurrentCoords().getFloatData();
       
        int[] tmpNodeTypes = new int[nNodes];
        int nEventNodes = 0;      
        for (int i = 0; i < nNodes; i++) {
            if(!allEventNames[i].isEmpty())
                nEventNodes++;
        }
        int nActorNodes = nNodes - nEventNodes;  
        for (int i = 0; i < nEventNodes; i++) {
            tmpNodeTypes[i] = DataBlock.NODE_TYPE_EVENT_POINT;
        }
        for (int i = nEventNodes; i < nNodes; i++) {
            tmpNodeTypes[i] = DataBlock.NODE_TYPE_ACTOR_POINT;
        }
        
        if(nEventNodes > 0) {
            eventNodeIndices = new int[nEventNodes];
            for (int i = 0, j = 0; i < nNodes; i++) {
                if(tmpNodeTypes[i] == DataBlock.NODE_TYPE_EVENT_POINT)
                    eventNodeIndices[j++] = i;
            }
        }
        if(nActorNodes > 0) {
            actorNodeIndices = new int[nActorNodes];
            for (int i = 0, j = 0; i < nNodes; i++) {
                if(tmpNodeTypes[i] == DataBlock.NODE_TYPE_ACTOR_POINT)
                    actorNodeIndices[j++] = i;
            }
        }
        
        nodeDataIDs = new String[nNodes];
        for (int i = 0; i < nNodes; i++) {
            switch(tmpNodeTypes[i]) {
                case DataBlock.NODE_TYPE_EVENT_POINT:
                    nodeDataIDs[i] = allEventNames[allItemIndices[i]];
                    break;
                case DataBlock.NODE_TYPE_ACTOR_POINT:
                    nodeDataIDs[i] = allActorNames[allItemIndices[i]];
                    break;                    
            }
        }

        CellSet participationsCellSet = field.getCellSet(0);
        //participSegmentIndices = participationsCellSet.getCellArray(CellType.SEGMENT).getNodes(); getFaceIndices?
        
        CellSet actorsCellSet = field.getCellSet(1);
        //actorNodeIndices = actorsCellSet.getCellArray(CellType.POINT).getNodes();
        //int nActors = actorNodeIndices.length; 
        //actorSegmentIndices = actorsCellSet.getCellArray(CellType.SEGMENT).getNodes();
        //actorNodeIndices = actorsCellSet.getCellArray(CellType.POINT).getDataIndices();
        
        
        //TBD - translate segments to DataBlock
        

        
        
        
        
        
        
        DataBlock db = new DataBlock("", "");
        db.setCoords(coords, true);
        db.setActorNodeIndices(actorNodeIndices);
        db.setEventNodeIndices(eventNodeIndices);
        db.setNodeDataIDs(nodeDataIDs);
        db.setSegments(segments);
        db.setActorSegmentIndices(actorSegmentIndices);
        db.setEventSegmentIndices(eventSegmentIndices);
        db.setParticipSegmentIndices(participSegmentIndices);
        db.setSegmentDataIDs(segmentDataIDs);
        db.setQuads(quads);
        db.setQuadDataIDs(quadDataIDs);
 
        return db;
    }
    

}
