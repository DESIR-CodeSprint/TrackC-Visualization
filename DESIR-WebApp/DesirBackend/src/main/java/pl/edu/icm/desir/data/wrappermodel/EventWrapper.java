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

package pl.edu.icm.desir.data.wrappermodel;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * @author Piotr Wendykier (piotrw@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */

public class EventWrapper extends PrimitiveEntityWrapper
{
    protected static int currentEventIndex = 0;
    
    private final Set<String> actors = new HashSet(1);
    protected final int eventIndex;
    
    public EventWrapper(String name, LocalDate entryDate)
    {
        super(name);
        this.firstEntryDate = this.lastEntryDate = entryDate;
        eventIndex = currentEventIndex;
        currentEventIndex += 1;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.name);
        hash = 17 * hash + Objects.hashCode(this.firstEntryDate);
        for (String actor : actors)
            hash = 17 * hash + Objects.hashCode(actor);
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final EventWrapper other = (EventWrapper) obj;
        if (!Objects.equals(this.name, other.name))
            return false;
        return Objects.equals(this.actors, other.actors);
    }

    
    public static void init()
    {
        currentIndex = 0;
        currentEventIndex = 0;
    }
    
    public void addActor(ActorWrapper actor)
    {
        actors.add(actor.getActorName());
    }

    public LocalDate getEntryDate() {
        return firstEntryDate;
    }

    public float getEntryTime()
    {
        return (float)firstEntryDate.toEpochDay();
    }

    public Set<String> getActors() {
        return actors;
    }

    public int getEventIndex() {
        return eventIndex;
    }
    
    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public float toFloat()
    {
        return actors.size();
    }
    
    @Override
    public int compareTo(Object obj)
    {
        if (!(obj instanceof EventWrapper))
            throw new UnsupportedOperationException("cannot compare"); //To change body of generated methods, choose Tools | Templates.
        return index < ((EventWrapper)obj).index ? -1 : 1;
    }
}
