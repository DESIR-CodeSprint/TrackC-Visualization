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

import pl.edu.icm.jscic.IrregularField;
import pl.edu.icm.jscic.TimeData;
import pl.edu.icm.jscic.dataarrays.DataArrayType;
import static pl.edu.icm.desir.ForcePlacement.ForcePlacementShared.*;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.engine.core.Parameter;
import pl.edu.icm.visnow.engine.core.ParameterChangeListener;
import pl.edu.icm.visnow.engine.core.Parameters;
import static pl.edu.icm.visnow.gui.widgets.RunButton.RunState.NO_RUN;
import static pl.edu.icm.visnow.gui.widgets.RunButton.RunState.RUN_DYNAMICALLY;
import static pl.edu.icm.visnow.gui.widgets.RunButton.RunState.RUN_ONCE;
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
import pl.edu.icm.visnow.lib.utils.SwingInstancer;
import pl.edu.icm.visnow.lib.utils.events.MinimizationStepEvent;
import pl.edu.icm.visnow.lib.utils.events.MinimizationStepListener;
import pl.edu.icm.visnow.lib.utils.numeric.minimization.ConjugateGradientsDoublePrecision;
import pl.edu.icm.visnow.lib.utils.numeric.minimization.ConjugateGradientsParameters;
import pl.edu.icm.visnow.lib.utils.numeric.minimization.GradValDP;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class ForcePlacement extends OutFieldVisualizationModule
{

    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;
    protected IrregularField inField = null;
    protected GUI computeUI = null;
    protected GradValDP graphGradVal;
    
    protected double[] point, grad;
    protected int[] edges;
    protected float[] edgD;
    
    protected TimeData timeCoords = new TimeData(DataArrayType.FIELD_DATA_FLOAT);
    protected int n;
    protected int runQueue = 0;
    
    protected ConjugateGradientsParameters cgParams;
    protected ConjugateGradientsDoublePrecision cg;
    protected TimeData gradient;
    
    protected MinimizationStepListener stepListener =
            new MinimizationStepListener()
            {
                @Override
                public void minimizationStepChanged(MinimizationStepEvent e)
                {
//                    if (e.getStepNumber() % 10 == 0) 
//                        System.out.printf("%45s%n", e.formatted());
                }
            };

    public ForcePlacement()
    {
        parameters.addParameterChangelistener(new ParameterChangeListener()
        {
            @Override
            public void parameterChanged(String name)
            {
                if (name != null && name.equals(RUNNING_MESSAGE.getName()) && parameters.get(RUNNING_MESSAGE) == RUN_ONCE) {
                    runQueue++;
                    startAction();
                } else if (parameters.get(RUNNING_MESSAGE) == RUN_DYNAMICALLY)
                    startIfNotInQueue();
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
    protected Parameter[] createDefaultParameters()
    {
        return new Parameter[]{
            new Parameter<>(CONNECTED_R, .7f),
            new Parameter<>(CONNECTED_S, 1f),
            new Parameter<>(CONNECTED_C, 100.f),
            new Parameter<>(UNCONNECTED_EXPONENT, 4),
            new Parameter<>(UNCONNECTED_R, 1f),
            new Parameter<>(UNCONNECTED_C, .001f),
            new Parameter<>(SMOOTH_C, 0f),
            new Parameter<>(RUNNING_MESSAGE, NO_RUN)
        };
    }

    @Override
    protected void notifySwingGUIs(pl.edu.icm.visnow.engine.core.ParameterProxy clonedParameterProxy, boolean resetFully, boolean setRunButtonPending)
    {
        computeUI.updateGUI(clonedParameterProxy);
    }
    

    @Override
    public void onActive()
    {
        if (getInputFirstValue("inField") != null) {
            inField = ((VNIrregularField) getInputFirstValue("inField")).getField();
            Parameters par = parameters.getReadOnlyClone();
            notifyGUIs(par, true, true);
            outIrregularField = PlacementCore.optimizePlacement(inField, 
                                                                parameters.get(CONNECTED_R), parameters.get(CONNECTED_S),  
                                                                parameters.get(CONNECTED_C), 
                                                                parameters.get(UNCONNECTED_R), parameters.get(UNCONNECTED_EXPONENT), 
                                                                parameters.get(UNCONNECTED_C), parameters.get(SMOOTH_C),
                                                                stepListener);
            setOutputValue("outField", new VNIrregularField(outIrregularField));
            outIrregularField.setCurrentTime(n);
            outField = outIrregularField;
            prepareOutputGeometry();
            show();
        }
    }
}
