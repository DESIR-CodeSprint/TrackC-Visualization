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

import pl.edu.icm.visnow.engine.core.Parameter;
import pl.edu.icm.visnow.engine.core.ParameterName;

/**
 * @author Piotr Wendykier (piotrw@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class ReadBibSonomyShared
{

    public static final ParameterName<String> FILENAME = new ParameterName("File name");
    public static final ParameterName<String> LOGIN = new ParameterName("Login");
    public static final ParameterName<String> APIKEY = new ParameterName("API key");
    public static final ParameterName<String> USERNAME = new ParameterName("User name");
    public static final ParameterName<String[]> TAGS = new ParameterName("Tags");
    public static final ParameterName<Boolean> RESTRUN = new ParameterName("REST run");
    
    public static Parameter[] getDefaultParameters()
    {
        return new Parameter[]{
            new Parameter<>(FILENAME, ""),           
            new Parameter<>(LOGIN, ""),           
            new Parameter<>(APIKEY, ""),           
            new Parameter<>(USERNAME, ""),           
            new Parameter<>(TAGS, new String[0]),
            new Parameter<>(RESTRUN, false)  
        };
    }
}
