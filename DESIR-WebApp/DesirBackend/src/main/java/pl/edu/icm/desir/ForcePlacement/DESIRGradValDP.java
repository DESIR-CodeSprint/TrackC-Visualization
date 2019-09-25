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
import java.util.Set;
import pl.edu.icm.desir.data.wrappermodel.ActorWrapper;
import pl.edu.icm.desir.data.wrappermodel.EventWrapper;
import pl.edu.icm.desir.data.wrappermodel.ParticipationWrapper;
import pl.edu.icm.desir.data.wrappermodel.PrimitiveEntityWrapper;
import pl.edu.icm.visnow.lib.utils.numeric.minimization.GradValDP;

/**
 * @author Krzysztof S. Nowinski (know@icm.edu.pl) Warsaw University,
 * Interdisciplinary Centre for Mathematical and Computational Modelling
 */
public class DESIRGradValDP implements GradValDP {
    
    private final int     nNodes;
    private final int[]   connections;
    private final int[][] neighbors;
    private final PrimitiveEntityWrapper[] nodes;
    
    private final float[][] repulsionCoeff;
    
    private final float preferredConnectionLength;
    private final float connectionEnergyCoeff;
    
    private final float centerShiftPenalty;
    
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


    public Map<String, Double> getVariablesAsMap() {
        return varMap;
    }
    
    public DESIRGradValDP(DESIRcoparticipation problem, 
                          float preferredConnectionLength, float connectionEnergyCoeff, 
                          float centerShiftPenalty, float baseRepulsionCoeff) 
    {
        this.preferredConnectionLength = preferredConnectionLength;
        this.connectionEnergyCoeff     = connectionEnergyCoeff;
        this.centerShiftPenalty        = centerShiftPenalty;
        this.nNodes = problem.getEvents().size() + problem.getActors().size();
        int nEvents = problem.getEvents().size();
        nodes = new PrimitiveEntityWrapper[nNodes];
        for (EventWrapper event : problem.getEvents())
            nodes[event.getEventIndex()] = event;
        for (ActorWrapper actor : problem.getActors())
            nodes[actor.getActorIndex() + nEvents] = actor;
        Set<ParticipationWrapper> participations = problem.getParticipations();
        connections = new int[2 * participations.size()];
        int[] nNeighbors = new int[nEvents];
        neighbors = new int[nEvents][];
        Arrays.fill(nNeighbors, 0);
        int iConn = 0;
        for (ParticipationWrapper participation : participations) {
            int pub = participation.getEvent().getEventIndex();
            connections[2 * iConn]     = pub;
            connections[2 * iConn + 1] = participation.getActor().getActorIndex() + nEvents;
            nNeighbors[pub] += 1;
            iConn += 1;
        }
        for (int i = 0; i < nEvents; i++)
            neighbors[i] = new int[nNeighbors[i]];
        Arrays.fill(nNeighbors, 0);
        
        for (int i = 0; i < connections.length; i += 2) {
            int k = connections[i];
            neighbors[k][nNeighbors[k]] = connections[i + 1];
            nNeighbors[k] += 1;
        }
        
        repulsionCoeff = new float[nNodes][];
        float tMin = Float.MAX_VALUE;
        float tMax = Float.MIN_VALUE;
        for (int i = 0; i < nNodes; i++) {
            if (tMin > nodes[i].getFirstEntryTime())
                tMin = nodes[i].getFirstEntryTime();
            if (tMax < nodes[i].getLastEntryTime())
                tMax = nodes[i].getLastEntryTime();
        }
        for (int i = 0; i < nNodes; i++) {
            repulsionCoeff[i] = new float[i];
            for (int j = 0; j < i; j++) {
                float tRel = TemporalDistance.dist(nodes[i], nodes[j]) / (tMax - tMin + 1);
                repulsionCoeff[i][j] = .0001f * baseRepulsionCoeff / (.01f + tRel);
            }
        }
    }

    /**
     * Computes value and gradient of a function at pointCoords
     * <p>
     * @param pointCoords point
     * @param gradient gradient at point (must be preallocated)
     * <p>
     * @return value at point
     */
    @Override
    public double computeValGrad(double[] pointCoords, double[] gradient) {
            
        double e = 0;
        Arrays.fill(gradient, 0);
        
        for (int i0 = 0; i0 < nNodes; i0++) 
            for (int i1 = 0; i1 < i0; i1++) {
                if (nodes[i0].getComponentIndex() != nodes[i1].getComponentIndex())
                    continue;
                double[] v = new double[] {
                    pointCoords[2 * i1] -     pointCoords[2 * i0],
                    pointCoords[2 * i1 + 1] - pointCoords[2 * i0 + 1]
                };
                double invR = 1 / (v[0] * v[0] + v[1] * v[1] + .01);
                float repCoeff = repulsionCoeff[i0][i1];
                e += repCoeff * (invR * invR - 2 * invR);
                double d = -4 * repCoeff * invR * invR * (invR - 1);
                for (int i = 0; i < 2; i++) {
                    gradient[2 * i1 + i] += d * v[i];
                    gradient[2 * i0 + i] -= d * v[i];
                }
            }
        
        for (int k = 0; k < connections.length; k += 2) {
            int i0 = connections[k];
            int i1 = connections[k + 1];
            double[] v = new double[] {
                pointCoords[2 * i1] -     pointCoords[2 * i0],
                pointCoords[2 * i1 + 1] - pointCoords[2 * i0 + 1]
            };
            double r = v[0] * v[0] + v[1] * v[1] + preferredConnectionLength;
            e +=           connectionEnergyCoeff * r * r;
            double d = 4 * connectionEnergyCoeff * r;
            for (int i = 0; i < 2; i++) {
                gradient[2 * i1 + i] += d * v[i];
                gradient[2 * i0 + i] -= d * v[i];
            }
        }
        
        float[] center = new float[2];
        for (int iPub = 0; iPub < neighbors.length; iPub++) {
            Arrays.fill(center, 0);
            int[] nbhs = neighbors[iPub];
            if (nbhs.length < 2)
                continue;
            for (int iAuth = 0; iAuth < nbhs.length; iAuth++) {
                int nbh = nbhs[iAuth];
                for (int i = 0; i < 2; i++)
                    center[i] += pointCoords[2 * nbh + i];
            }
            double dCent = 0;
            double[] v = new double[2];
            for (int i = 0; i < 2; i++) {
                center[i] /= nbhs.length;
                v[i] = pointCoords[2 * iPub + i] - center[i];
                dCent += v[i] * v[i];
            }
            e += centerShiftPenalty * dCent;   
            for (int i = 0; i < 2; i++) {
                gradient[2 * iPub + i] += 2 * centerShiftPenalty * v[i];
                v[i] /= nbhs.length;
                for (int iAuth = 0; iAuth < nbhs.length; iAuth++)  {
                    int nbh = nbhs[iAuth];
                    gradient[2 * nbh + i] -= 2 * centerShiftPenalty * v[i];
                }
            }
            
        }
        return e;
    }

    @Override
    public void setNThreads(int nThreads) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
