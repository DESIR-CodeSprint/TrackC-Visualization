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
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.bibsonomy.model.PersonName;
import pl.edu.icm.jscic.dataarrays.DataObjectInterface;

/**
 * @author Piotr Wendykier (piotrw@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class Coauthorship implements DataObjectInterface, Serializable
{

    private static final long serialVersionUID = 8967658541160829771L;

    PersonName author1;
    PersonName author2;
    Set<String> titles;
 
    public Coauthorship(PersonName author1, PersonName author2)
    {
        this.author1 = author1;
        this.author2 = author2;
        this.titles = new HashSet<>(1);
    }

    @Override
    public boolean equals(Object aThat)
    {
        if (this == aThat) return true;
        if (!(aThat instanceof Coauthorship)) return false;

        Coauthorship that = (Coauthorship) aThat;
        return this.author1.equals(that.author1) && this.author2.equals(that.author2);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.author1);
        hash = 17 * hash + Objects.hashCode(this.author2);
        return hash;
    }
    
    public void addTitle(String title) {
        this.titles.add(title);
    }
    
    public PersonName getAuthor1Name() {
        return author1;
    }

    public PersonName getAuthor2Name() {
        return author2;
    }
    
    public Set<String> getTitles() {
        return titles;
    }
    
    @Override
    public String toString()
    {
        StringBuilder bld = new StringBuilder();
        for (String title : titles) {
            bld.append(title).append("\\n");
        }
        return bld.toString();
    }
    
    @Override
    public float toFloat()
    {
        return this.titles.size();
    }
    
}
