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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/**
 * @author Piotr Wendykier (piotrw@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class ActorWrapper extends PrimitiveEntityWrapper
{

    protected static int currentActorIndex = 0;
    private final Set<ParticipationWrapper> participations = new HashSet(1);
    protected final int actorIndex;

    public ActorWrapper(String name)
    {
        super(name);
        actorIndex = currentActorIndex;
        currentActorIndex += 1;
    }

    public String getActorName()
    {
        return name;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 53 * hash + Objects.hashCode(this.name);
        return hash;
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (obj != null && obj instanceof ActorWrapper)
            return name.equals(((ActorWrapper)obj).name);
        return false;
    }
    
    public static void init()
    {
        currentIndex = 0;
        currentActorIndex = 0;
    }
    
    @Override
    public String getName()
    {
        return name.toString();
    }
    
    public void addParticipation(ParticipationWrapper doc)
    {
        participations.add(doc);
        if (doc.getDate().compareTo(lastEntryDate) > 0)
            lastEntryDate = doc.getDate();
        if (doc.getDate().compareTo(firstEntryDate) < 0)
            firstEntryDate = doc.getDate();
    }

    public Set<ParticipationWrapper> getParticipations()
    {
        return participations;
    }

    public int getNEvents()
    {
        return participations.size();
    }

    public int getActorIndex() {
        return actorIndex;
    }

    @Override
    public float toFloat()
    {
        return participations.size();
    }

    @Override
    public int compareTo(Object obj)
    {
        if (!(obj instanceof ActorWrapper))
            throw new UnsupportedOperationException("cannot compare"); //To change body of generated methods, choose Tools | Templates.
        return index < ((ActorWrapper)obj).index ? -1 : 1;
    }
}
