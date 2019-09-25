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


import java.io.Serializable;
import java.util.Set;
import pl.edu.icm.desir.data.wrappermodel.ActorWrapper;
import pl.edu.icm.desir.data.wrappermodel.EventWrapper;
import pl.edu.icm.desir.data.wrappermodel.ParticipationWrapper;

/**
 * @author know
 */
public class DESIRcoparticipation implements Serializable
{

    private final Set<ActorWrapper>         actors;     
    private final Set<EventWrapper>    events;
    private final Set<ParticipationWrapper>     participations;    

    public DESIRcoparticipation(Set<ActorWrapper> actors, Set<EventWrapper> events, Set<ParticipationWrapper> participations) {
        this.actors = actors;
        this.events = events;
        this.participations = participations;
    }

    public Set<ActorWrapper> getActors() {
        return actors;
    }

    public Set<EventWrapper> getEvents() {
        return events;
    }

    public Set<ParticipationWrapper> getParticipations() {
        return participations;
    }
    
    public int getNActors()
    {
        return actors.size();
    }

    public int getNEvents()
    {
        return events.size();
    }
    
    public int getNParticipations()
    {
        return participations.size();
    }

}
