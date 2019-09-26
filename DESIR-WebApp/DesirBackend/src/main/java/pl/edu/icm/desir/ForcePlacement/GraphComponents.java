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

package pl.edu.icm.desir.ForcePlacement;

import java.util.Arrays;
import java.util.Set;
import pl.edu.icm.desir.data.wrappermodel.ActorWrapper;
import pl.edu.icm.desir.data.wrappermodel.EventWrapper;
import pl.edu.icm.desir.data.wrappermodel.ParticipationWrapper;


/**
 *
 * @author know
 */
public class GraphComponents
{
    
    public static void createComponentIndices(DESIRcoparticipation inData)
    {
        
        Set<ActorWrapper> actorsSet   = inData.getActors();      // multiple instances of an actor identified by name can be found
        Set<EventWrapper> eventsSet = inData.getEvents();
        Set<ParticipationWrapper> participationsSet   = inData.getParticipations();  
        
        int nEvents = eventsSet.size();
        int nActors      = actorsSet.size();
        int nEntities     = nActors + nEvents;
        
        int[] nNeighb = new int[nEntities];
        Arrays.fill(nNeighb, 0);
        
        for (ParticipationWrapper participation : participationsSet) {
            nNeighb[participation.getActor().getActorIndex()+ nEvents] += 1;
            nNeighb[participation.getEvent().getEventIndex()] += 1;
        }
        
        int[][] neighbors = new int[nEntities][];
        for (int i = 0; i < neighbors.length; i++) 
            neighbors[i] = new int[nNeighb[i]];
        
        Arrays.fill(nNeighb, 0);
        
        for (ParticipationWrapper participation : participationsSet) {
            int ia = participation.getActor().getActorIndex()+ nEvents;
            int id = participation.getEvent().getEventIndex();
            neighbors[ia][nNeighb[ia]] = id;
            nNeighb[ia] += 1;
            neighbors[id][nNeighb[id]] = ia;
            nNeighb[id] += 1;
        }
        
        int[] components = new int[nEntities];
        Arrays.fill(components, -1);
        
        int[] stack = new int[nEntities];
        
        int nComponents = 0;
        int maxSize = 0;
        for (int iEntity = 0; iEntity < components.length; iEntity++) 
            if (components[iEntity] == -1) {
                int size = 1;
                int itemsOnStack = 0;
                components[iEntity] = nComponents;
                stack[itemsOnStack] = iEntity;
                itemsOnStack = 1;
                while (itemsOnStack > 0) {
                    int node = stack[itemsOnStack - 1];
                    itemsOnStack -= 1;
                    for (int neigbor : neighbors[node]) 
                        if (components[neigbor] == -1) {
                            components[neigbor] = nComponents;
                            stack[itemsOnStack] = neigbor;
                            itemsOnStack += 1;
                            size += 1;
                        }
                }
                nComponents += 1;
                if (size > maxSize)
                    maxSize = size;
            }
        
        int[] componentSizes = new int[nComponents];
        Arrays.fill(componentSizes, 0);
        for (int i = 0; i < components.length; i++)
            componentSizes[components[i]] += 1;
        
        for (ActorWrapper actor : actorsSet)
            actor.setComponentIndex(components[actor.getActorIndex() + nEvents]);
        for (EventWrapper event : eventsSet)
            event.setComponentIndex(components[event.getEventIndex()]);
        for (ParticipationWrapper participation : participationsSet)
            participation.setComponentIndex(components[participation.getEvent().getEventIndex()]);
        
    }
    
}
