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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import pl.edu.icm.visnow.lib.utils.numeric.minimization.GradValDP;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class LJGradValDP implements GradValDP {

    private final int[] edges;
    private final float[] edgeVals;
    private final float rC;
    private final float sfC;
    private final float cC;
    private final float rU;
    private final int expU;
    private final float cU;
    private final float smoothC;
    private final Map<String, Double> varMap = new HashMap<>();

    @Override
    public String[] getVarNames() {
        int i = 0;
        String[] keys = new String[varMap.size()];
        for (String value : varMap.keySet()) {
            keys[i] = value;
            i += 1;
        }
        return keys;
    }

    @Override
    public double[] getVariables() {
        int i = 0;
        double[] vals = new double[varMap.size()];
        for (Double value : varMap.values()) {
            vals[i] = value;
            i += 1;
        }
        return vals;
    }

    class ComputeChunk implements Runnable {

        @Override
        public void run() {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    public Map<String, Double> getVariablesAsMap() {
        return varMap;
    }

    public LJGradValDP(int[] edges, float[] edgeVals,
            float rC, float sfC, float cC,
            float rU, int expU, float cU,
            float smoothC) {
        this.edges = edges;
        this.edgeVals = edgeVals;
        this.expU = expU;
        this.rC = rC * rC;
        this.sfC = sfC;
        this.cC = cC;
        this.rU = rU * rU;
        this.cU = cU;
        this.smoothC = smoothC;
    }

    /**
     * Computes value and gradient of a function at p
     * <p>
     * @param p point
     * @param g gradient at point (must be preallocated)
     * <p>
     * @return value at point
     */
    @Override
    public double computeValGrad(double[] p, double[] g) {
        int nNodes = p.length / 3;
        int nEdges = edges.length / 2;
        double r0;
        double[] v = new double[3];
        double e = 0;
        Arrays.fill(g, 0);
        for (int i0 = 0, iEdge = 0; i0 < nNodes; i0++) {
            for (int i1 = i0 + 1; i1 < nNodes; i1++) {
                double d = 0;
                for (int i = 0; i < v.length; i++) {
                    v[i] = p[3 * i1 + i] - p[3 * i0 + i];
                }
                double r = v[0] * v[0] + v[1] * v[1] + v[2] * v[2];
                if (iEdge < nEdges && edges[2 * iEdge] == i0 && edges[2 * iEdge + 1] == i1) {
                    r0 = rC * Math.pow(sfC, edgeVals[iEdge]);
                    r0 = r0 * r0;
                    e += cC * (r - r0) * (r - r0);
                    d = 4 * cC * (r - r0);
                    iEdge += 1;
                } else {
                    r += smoothC * smoothC;
                    r0 = rU * rU;
                    double r2 = r0 / r, s0 = 1;
                    for (int i = 0; i < expU / 2; i++) {
                        s0 *= r2;
                    }
                    e += cU * (s0 * s0 - 2 * s0);
                    d = 2 * expU * cU * (s0 - s0 * s0) / r;
                }
                for (int i = 0; i < v.length; i++) {
                    g[3 * i1 + i] += d * v[i];
                    g[3 * i0 + i] -= d * v[i];
                }
            }
        }
        return e;
    }

    @Override
    public void setNThreads(int nThreads) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static void main(String[] args) {
        int n = 5;
        double[] p = new double[3 * n];
        double[] g = new double[3 * n];
        double[] g1 = new double[3 * n];
        double[] ng = new double[3 * n];
        int[] edges = {0, 1, 0, 2, 1, 2, 3, 4};
        float[] edgeVals = {5, 5, 5, 1};
        GradValDP graphGradVal = new LJGradValDP(edges, edgeVals,
                (float) .7, 1, 100,
                1, 4, (float) .001, 0);
        for (int step = 0; step < 3; step++) {
            for (int i = 0; i < p.length; i++) {
                p[i] = Math.random();
            }
            graphGradVal.computeValGrad(p, g);
            for (int i = 0; i < p.length; i++) {
                p[i] -= .00005;
                double e0 = graphGradVal.computeValGrad(p, g1);
                p[i] += .0001;
                ng[i] = 10000 * (graphGradVal.computeValGrad(p, g1) - e0);
                p[i] -= .00005;
            }
            for (int i = 0; i < n; i++) {
                System.out.printf("%12.6f %12.6f %12.6f     %12.6f %12.6f %12.6f     %12.6f %12.6f %12.6f     %12.9f %12.9f %12.9f     %n",
                        p[3 * i], p[3 * i + 1], p[3 * i + 2],
                        g[3 * i], g[3 * i + 1], g[3 * i + 2],
                        ng[3 * i], ng[3 * i + 1], ng[3 * i + 2],
                        (ng[3 * i] - g[3 * i]) / (Math.abs(ng[3 * i]) + Math.abs(g[3 * i])),
                        (ng[3 * i + 1] - g[3 * i + 1]) / (Math.abs(ng[3 * i + 1]) + Math.abs(g[3 * i + 1])),
                        (ng[3 * i + 2] - g[3 * i + 2]) / (Math.abs(ng[3 * i + 2]) + Math.abs(g[3 * i + 2])));
            }
        }
    }
}
