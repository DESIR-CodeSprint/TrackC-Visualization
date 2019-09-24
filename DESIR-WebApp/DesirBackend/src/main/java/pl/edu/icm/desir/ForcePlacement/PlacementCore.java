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

import pl.edu.icm.jlargearrays.FloatLargeArray;
import pl.edu.icm.jscic.CellArray;
import pl.edu.icm.jscic.IrregularField;
import static pl.edu.icm.jscic.cells.CellType.SEGMENT;
import pl.edu.icm.jscic.dataarrays.DataArray;
import pl.edu.icm.visnow.lib.utils.events.MinimizationStepListener;
import pl.edu.icm.visnow.lib.utils.numeric.minimization.ConjugateGradientsDoublePrecision;
import pl.edu.icm.visnow.lib.utils.numeric.minimization.ConjugateGradientsParameters;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl)
 * Warsaw University, Interdisciplinary Centre
 * for Mathematical and Computational Modelling
 */
public class PlacementCore
{
    
    
    /**
     * Optimizes graph geometry according to a simplified energy function: <p>
     * for connected nodes i, j the energy is <p>
     * <code> connectedWeight * (r^2 - r0^2)^2</code>, where 
     * <code>r0=connectedDistance * connectedScaleFactor^edgeDegree</code> and r is the Euclidean 
     * distance between i and j nodes <p>
     * for unconnected nodes i, j the energy is <p>
     * <code> unconnectedWeight * (s^unconnectedExponent - 2 * s^(unconnectedExponent / 2))</code>, where<p>
     * <code> s = (unconnectedDistance^2) / (r^2 + unconnectedSmoothingCoefficient^2)</code><p>
     * 
     * 
     * @param inField irregular input field (graph)<p>
     * only SEGMENT cell array of the first cell set is used. The <code> nodes</code> array is assumed to be ordered lexicographically
     * and each segment nodes must be ordered, e.g. <code>{0,1, 0,5,  1,2, 1,3,   2,4, 2,5}</code> is acceptable but 
     * <code>{0,1,  1,2, 0,5, 1,3,   4,2, 2,5}</code> is not  (segments 1,2 and 0,5 are in bad order and segment 4,2 has wrong node orders<p>
     * cell data component <code>"edge_degree"</code> must be present
     * @param connectedDistance  - see energy formula for edges (recommended value from the range .3 : 1.)
     * @param connectedScaleFactor  - see energy formula for edges  (recommended value from the range .8 : 1.2), if under 1, edges with large degree will be shortened)
     * @param connectedWeight  - see energy formula for edges (recommended value over 100)
     * @param unconnectedDistance  - see energy formula for unconnected node pairs (recommended value 1. or more)
     * @param unconnectedExponent - see energy formula for unconnected node pairs(recommended value 2, 4 or 6)
     * @param unconnectedWeight - see energy formula for unconnected node pairs (recommended value under .001)
     * @param unconnectedSmoothingCoefficient - see energy formula for unconnected node pairs (a small (under .001) nonnegative smoothing factor
     * @param stepListener - minimization step listener for conjugate gradients algorithm
     * @return a copy of inField with corrected geometry: connected components are separated and optimized to fairly regular layout
     * 
     * note: output can be visualized by connected components module and text glyphs showing author names
     */
    public static final IrregularField optimizePlacement(IrregularField inField,
                                                         float connectedDistance, float connectedScaleFactor, float connectedWeight, 
                                                         float unconnectedDistance, int unconnectedExponent, float unconnectedWeight, 
                                                         float unconnectedSmoothingCoefficient,
                                                         MinimizationStepListener stepListener)
    {
        float[] coords = inField.getCurrentCoords().getData();
        double[] point = new double[coords.length];
        for (int i = 0; i < coords.length; i++)
            point[i] = (coords[i] - .5);
        double[] val = new double[1];
        double[] h = new double[] {.01};
        double[] grad   = new double[coords.length];

        CellArray edgeArray = inField.getCellSet(0).getCellArray(SEGMENT);
        int nEdges = edgeArray.getNCells();
        int[] edges =  edgeArray.getNodes();
        int[] indices = edgeArray.getDataIndices();
        float[] edgeDegrees = inField.getCellSet(0).getComponent("edge_degree").getRawFloatArray().getData();
        float[] edgD = new float[nEdges];
        for (int i = 0; i < nEdges; i++) 
            edgD[i] = edgeDegrees[indices[i]];

        IrregularField outIrregularField = inField.cloneShallow();
        ConjugateGradientsParameters cgParams = new ConjugateGradientsParameters();
        LJGradValDP graphGradVal = new LJGradValDP(edges, edgD, 
                                                   connectedDistance,   connectedScaleFactor, connectedWeight, 
                                                   unconnectedDistance, unconnectedExponent,  unconnectedWeight, 
                                                   unconnectedSmoothingCoefficient);
        double scale = .1;
        for (int i = 0; i < point.length; i++)
            point[i] *= scale;
        double step = Math.pow(10, .1);
        double bestScale = 1;
        double bestE = Double.MAX_VALUE;
        for (int s = 0; s < 20; s++) {
            double e = graphGradVal.computeValGrad(point, grad);
            if (e < bestE) {
                bestE = e;
                bestScale = scale;
            }
            scale *= step;
            for (int i = 0; i < coords.length; i++)
                point[i] = scale * (coords[i] - .5);
        }
        for (int i = 0; i < coords.length; i++)
            point[i] = bestScale * (coords[i] - .5);
        ConjugateGradientsDoublePrecision cg = new ConjugateGradientsDoublePrecision(cgParams, point.length);
        if(stepListener != null) {
            cg.addMinimizationStepListener(stepListener);
        }
        cg.minimum_cg(point, val, .1, .1, .1, h, new int[] {100}, 5, graphGradVal);
        for (int minPhase = 0; minPhase < 5; minPhase++) {
            for (int edge = 0; edge < indices.length; edge++) {
                int i = edges[2 * edge], j = edges[2 * edge + 1];
                double r = 0;
                for (int k = 0; k < 3; k++) 
                    r += (point[3 * i + k] - point[3 * j + k]) * (point[3 * i + k] - point[3 * j + k]);
                if (r < .001)
                    for (int k = 0; k < 3; k++) 
                        point[3 * i + k] += (.01 * Math.random() - .5);
            }
            cg.minimum_cg(point, val, .01, .01, .01, h, new int[] {100}, 5, graphGradVal);
        }

        FloatLargeArray outCoords = new FloatLargeArray(point.length, false);
        double[] gr = new double[point.length];
        for (int i = 0; i < point.length; i++) {
            outCoords.setFloat(i, (float)point[i]);
        }
        graphGradVal.computeValGrad(point, gr);
        outIrregularField.setCoords(outCoords, 0);
        outIrregularField.addComponent(DataArray.create(gr, 3, "gradient"));
        return outIrregularField;
    }
}
