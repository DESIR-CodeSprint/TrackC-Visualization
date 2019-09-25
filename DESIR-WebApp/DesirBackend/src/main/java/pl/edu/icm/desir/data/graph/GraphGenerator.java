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

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import pl.edu.icm.desir.ForcePlacement.IntVectorHeapSort;
import pl.edu.icm.desir.data.DataBlock;
import pl.edu.icm.desir.data.exchange.ModelBuilder;
import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.desir.data.model.Event;
import pl.edu.icm.desir.data.model.Participation;
import pl.edu.icm.desir.data.model.Relation;
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

@Service
public class GraphGenerator {

	public GraphGenerator() {
	}

	private List<Participation> generateInteractionsModel(List<Actor> actors) {
		List<Participation> edges = new ArrayList<>();
		for (Actor actor : actors) {
			for (Relation relation : actor.getRelations()) {
				if(relation instanceof Participation) {
					((Participation) relation).setRole("author");
					edges.add((Participation) relation);
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
                        idx = -1;
                        for(Interaction e : edges) {
                            if(e.getActors().get(0).equals(relation1.getActor()) && e.getActors().get(1).equals(relation2.getActor()) ||
                                e.getActors().get(0).equals(relation2.getActor()) && e.getActors().get(1).equals(relation1.getActor())) {
                                idx = edges.indexOf(e);
                                break;
                            }
                        }
                         if (idx > -1) {
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
    
        public DataBlock generateDataBlockFromModel(ModelBuilder builder) {
        
        
        
        
        return null;
    }

}
