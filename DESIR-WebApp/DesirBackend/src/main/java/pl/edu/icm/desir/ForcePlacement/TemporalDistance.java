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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import pl.edu.icm.desir.data.wrappermodel.PrimitiveEntityWrapper;

/**
 *
 * @author know
 */
public class TemporalDistance {
/**
 * time distance between two entities (events, actor lifespans etc.
 * @param x first entity
 * @param y second entity
 * @return 0 if x and y overlap in time, 
 * positive time difference between end of x and start of y if x preceeding y
 * positive time difference between end of y and start of x if y preceeding x
 */
    public static float dist(PrimitiveEntityWrapper x, PrimitiveEntityWrapper y)
    {
        float tx0 = x.getFirstEntryTime();
        float tx1 = x.getLastEntryTime();
        float ty0 = y.getFirstEntryTime();
        float ty1 = y.getLastEntryTime();
        if (tx1 < ty0)
            return ty0 - tx1;  // x before y
        if (ty1 < tx0)
            return tx0 - ty1;  // y before x
        return 0;              // x and y overlap in time
    }

/**
 * time distance between two entities (events, actor lifespans etc.
 * @param x first entity
 * @param y second entity
 * @return 0 if x and y overlap in time, 
 * positive time difference (in days) between end of x and start of y if x preceeding y
 * positive time difference (in days) between end of y and start of x if y preceeding x
 */
    public static int dateDist(PrimitiveEntityWrapper x, PrimitiveEntityWrapper y)
    {
        LocalDate tx0 = x.getFirstEntryDate();
        LocalDate tx1 = x.getLastEntryDate();
        LocalDate ty0 = y.getFirstEntryDate();
        LocalDate ty1 = y.getLastEntryDate();
        if (tx1.isBefore(ty0))
            return (int)tx1.until(ty0, ChronoUnit.DAYS);  // x before y
        if (ty1.isBefore(tx0))
            return (int)ty1.until(tx0, ChronoUnit.DAYS);  // y before x
        return 0;              // x and y overlap in time
    }

    private TemporalDistance() {}
    
}
