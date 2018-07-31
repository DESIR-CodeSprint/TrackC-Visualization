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

import pl.edu.icm.visnow.engine.core.ParameterName;
import pl.edu.icm.visnow.gui.widgets.RunButton;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ForcePlacementShared
{
    static final ParameterName<Float>   CONNECTED_R          = new ParameterName("connected r");
    static final ParameterName<Float>   CONNECTED_S          = new ParameterName("connected s");
    static final ParameterName<Float>   CONNECTED_C          = new ParameterName("connected c");
    static final ParameterName<Integer> UNCONNECTED_EXPONENT = new ParameterName("unconnected exp");
    static final ParameterName<Float>   UNCONNECTED_R        = new ParameterName("unconnected r");
    static final ParameterName<Float>   UNCONNECTED_C        = new ParameterName("unconnected c");
    static final ParameterName<Float>   SMOOTH_C             = new ParameterName("smooth c");
    static final ParameterName<RunButton.RunState> RUNNING_MESSAGE = new ParameterName<>("Running message");
}
