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

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import pl.edu.icm.jscic.dataarrays.DataObjectInterface;


/**
 * @author Piotr Wendykier (piotrw@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class PrimitiveEntityWrapper implements DataObjectInterface, Serializable, Comparable, Cloneable
{

    protected static int currentIndex = 0;
    
    protected final String name;
    protected LocalDate firstEntryDate = LocalDate.MAX;
    protected LocalDate lastEntryDate  = LocalDate.MIN;
    protected final float[] location = new float[]{(float)Math.random(), (float)Math.random()};
    protected final int index;
    protected int componentIndex = -1;

    public PrimitiveEntityWrapper(String name)
    {
        this.name = name;
        index = currentIndex;
        currentIndex += 1;
    }

    public String getName()
    {
        return name;
    }
    
    public static void init()
    {
        currentIndex = 0;
    }
    
    @Override
    public String toString()
    {
        return name.toString();
    }
    
    public LocalDate getFirstEntryDate() {
        return firstEntryDate;
    }

    public LocalDate getLastEntryDate() {
        return lastEntryDate;
    }
    
    public float getFirstEntryTime()
    {
        return (float)firstEntryDate.toEpochDay();
    }
    
    public float getLastEntryTime()
    {
        return (float)lastEntryDate.toEpochDay();
    }

    public float[] getLocation() {
        return location;
    }
    
    public float[] getTimeExtent() {
        return new float[] {getFirstEntryTime(), getLastEntryTime()};
    }
    
    public void setLocation(float[] location)
    {
        this.location[0] = location[0];
        this.location[1] = location[1];
    }

    @Override
    public float toFloat()
    {
        return (getFirstEntryTime() + getLastEntryTime()) / 2;
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
        if (obj != null && obj instanceof PrimitiveEntityWrapper)
            return name.equals(((PrimitiveEntityWrapper)obj).name);
        return false;
    }
    
    @Override
    public int compareTo(Object obj)
    {
        if (!(obj instanceof PrimitiveEntityWrapper))
            throw new UnsupportedOperationException("cannot compare"); //To change body of generated methods, choose Tools | Templates.
        return index < ((PrimitiveEntityWrapper)obj).index ? -1 : 1;
    }

    public int getIndex() {
        return index;
    }

    public int getComponentIndex() {
        return componentIndex;
    }

    public void setComponentIndex(int componentIndex) {
        this.componentIndex = componentIndex;
    }
}
