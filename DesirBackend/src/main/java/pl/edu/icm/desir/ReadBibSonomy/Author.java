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

package pl.edu.icm.desir.ReadBibSonomy;

import java.io.Serializable;
import org.bibsonomy.model.PersonName;
import pl.edu.icm.jscic.dataarrays.DataObjectInterface;


/**
 * @author Piotr Wendykier (piotrw@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class Author implements DataObjectInterface, Serializable, Comparable
{

    private static final long serialVersionUID = -2678685506431958754L;

    private final PersonName name;

    public Author(PersonName name)
    {
        this.name = name;
    }

    public PersonName getAuthorName()
    {
        return name;
    }
    
    @Override
    public String toString()
    {
        return name.toString();
    }

    @Override
    public float toFloat()
    {
        return name.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        return this.toString().compareTo(( (Author)o ).toString());
    }
}
