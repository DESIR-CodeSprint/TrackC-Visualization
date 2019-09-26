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

/**
 * Note: this class has a natural ordering that is inconsistent with equals
 * @author know
 */
public class ParticipationWrapper extends PrimitiveEntityWrapper
{

    private static int currentParticipationIndex = 0;
    
    private final ActorWrapper actor;
    private final EventWrapper event;
    private final int participationIndex;

    public ParticipationWrapper(ActorWrapper actor, EventWrapper event)
    {
        super(actor.getName() + " : " + event.getName());
        this.actor = actor;
        this.event = event;
        firstEntryDate = lastEntryDate = event.getEntryDate();
        participationIndex = currentParticipationIndex;
        currentParticipationIndex += 1;
    }

    public ActorWrapper getActor()
    {
        return actor;
    }

    public EventWrapper getEvent()
    {
        return event;
    }

    public LocalDate getDate() {
        return firstEntryDate;
    }

    public float getTime()
    {
        return (float)firstEntryDate.toEpochDay();
    }

    public int getIndex()
    {
        return index;
    }

    public int getParticipationIndex() {
        return participationIndex;
    }

    public static void init()
    {
        currentIndex = 0;
        currentParticipationIndex = 0;
    }
    
    public void cancel()
    {
        currentParticipationIndex -= 1;
    }
    
    @Override
    public float toFloat()
    {
        return getTime();
    }

    @Override
    public int compareTo(Object obj)
    {
        ParticipationWrapper a = (ParticipationWrapper)obj;
        return firstEntryDate.compareTo(a.firstEntryDate);
    }
    

    public int getComponentIndex() {
        return componentIndex;
    }

    public void setComponentIndex(int componentIndex) {
        this.componentIndex = componentIndex;
    }
}
