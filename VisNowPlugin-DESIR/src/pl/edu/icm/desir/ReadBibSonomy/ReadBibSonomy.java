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


import java.io.IOException;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.engine.core.Parameter;
import pl.edu.icm.visnow.engine.core.ParameterChangeListener;
import pl.edu.icm.visnow.engine.core.Parameters;
import static pl.edu.icm.desir.ReadBibSonomy.ReadBibSonomyShared.*;
import pl.edu.icm.visnow.lib.types.VNIrregularField;

/**
 * @author Piotr Wendykier (piotrw@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class ReadBibSonomy extends OutFieldVisualizationModule
{

    private GUI computeUI = null;
    public static OutputEgg[] outputEggs = null;
    
    /**
     * Creates a new instance of ReadImage
     */
    public ReadBibSonomy()
    {
        parameters.addParameterChangelistener(new ParameterChangeListener()
        {
            @Override
            public void parameterChanged(String name)
            {
                startAction();
            }
        });
        SwingInstancer.swingRunAndWait(new Runnable()
        {
            @Override
            public void run()
            {
                computeUI = new GUI();
                ui.addComputeGUI(computeUI);
                setPanel(ui);
                computeUI.setParameters(parameters);
            }
        });
    }

    @Override
    public void onInitFinishedLocal()
    {
        if (isForceFlag())
            SwingUtilities.invokeLater(new Runnable()
            {
                @Override
                public void run()
                {
                    computeUI.activateOpenDialog();
                }
            });
    }

    @Override
    protected Parameter[] createDefaultParameters()
    {
        return ReadBibSonomyShared.getDefaultParameters();
    }

    @Override
    protected void notifySwingGUIs(final pl.edu.icm.visnow.engine.core.ParameterProxy clonedParameterProxy, boolean resetFully, boolean setRunButtonPending)
    {
        computeUI.updateGUI(clonedParameterProxy);
    }

    @Override
    public void onActive()
    {
        Parameters p;
        synchronized (parameters) {
            p = parameters.getReadOnlyClone();
        }
        notifyGUIs(p, false, false);
        if (p.get(RESTRUN)) {
            outIrregularField = ReadBibSonomyCore.generateCoauthorshipUsingREST(p.get(LOGIN), p.get(APIKEY), p.get(USERNAME), Arrays.asList(p.get(TAGS)));
        } else {
            if (p.get(FILENAME).isEmpty()) {
                outIrregularField = null;
            } else {
                try {
                    outIrregularField = ReadBibSonomyCore.generateCoauthorshipFromFile(p.get(FILENAME));
                } catch (IOException ex) {
                    outIrregularField = null;
                }
            }
        }
        if (outIrregularField == null) {
            setOutputValue("outField", null);
        }
        else {
            outField = outIrregularField;
            setOutputValue("outField", new VNIrregularField(outIrregularField));
        }
        prepareOutputGeometry();
        show();
    }

}
