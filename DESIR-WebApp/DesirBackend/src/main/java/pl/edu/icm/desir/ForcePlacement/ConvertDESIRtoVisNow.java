/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.edu.icm.desir.ForcePlacement;

import java.util.Arrays;
import java.util.Set;
import org.bibsonomy.model.PersonName;
import pl.edu.icm.desir.data.wrappermodel.ActorWrapper;
import pl.edu.icm.desir.data.wrappermodel.EventWrapper;
import pl.edu.icm.desir.data.wrappermodel.ParticipationWrapper;
import pl.edu.icm.jlargearrays.FloatLargeArray;
import pl.edu.icm.jscic.CellArray;
import pl.edu.icm.jscic.CellSet;
import pl.edu.icm.jscic.IrregularField;
import pl.edu.icm.jscic.cells.CellType;
import pl.edu.icm.jscic.dataarrays.DataArray;

/**
 *
 * @author know
 */
public class ConvertDESIRtoVisNow 
{
    private static int PREFERRED_TITLE_LINE_LENGTH = 20;
    
    public static IrregularField generateFieldFromCoparticipation(DESIRcoparticipation coparticipation)
    {
        
        Set<ActorWrapper> actorsSet           = coparticipation.getActors();      // multiple instances of an actor identified by name can be found
        Set<EventWrapper> eventsSet = coparticipation.getEvents();
        Set<ParticipationWrapper> participationsSet   = coparticipation.getParticipations();  
        
        int nEvents = eventsSet.size();
        int nParticipations  = participationsSet.size();
        int nActors      = actorsSet.size();
        int nNodes        = nEvents + nParticipations;
        
        ActorWrapper[] actors            = new ActorWrapper[nActors];
        EventWrapper[] events  = new EventWrapper[nEvents];
        ParticipationWrapper[] participations    = new ParticipationWrapper[nParticipations];
        actorsSet.toArray(actors);
        eventsSet.toArray(events);
        participationsSet.toArray(participations);
        
        float[] actorCoords        = new float[2 * nActors];
        float[] eventCoords   = new float[2 * nEvents];
        
        for (ActorWrapper actor : actors) {
            int i = actor.getActorIndex();
            int iComponent = actor.getComponentIndex();
            actorCoords[2 * i]     = actor.getLocation()[0] + 2 * (iComponent % 10);
            actorCoords[2 * i + 1] = actor.getLocation()[1] + 2 * (iComponent / 10);
        }
        
        for (EventWrapper event : events) {
            int i = event.getEventIndex();
            int iComponent = event.getComponentIndex();
            eventCoords[2 * i]     = event.getLocation()[0] + 2 * (iComponent % 10);
            eventCoords[2 * i + 1] = event.getLocation()[1] + 2 * (iComponent / 10);
        }
        
        int nActorsTimelineIntervals = 0;           
        for (int i = 0; i < nActors; i++)
            nActorsTimelineIntervals += actors[i].getNEvents() - 1;
        
        IrregularField outField = new IrregularField(nNodes);
        float[] coords = new float[3 * nNodes];
        String[] authNames = new String[nNodes];
        String[] docNames = new String[nNodes];
        int[] ranges = new int[nNodes];
        int[] itemIndices =  new int[nNodes];
        
        Arrays.fill(authNames, "");
        Arrays.fill(docNames, "");
        
        float tmin =  Float.MAX_VALUE;
        float tmax = -Float.MAX_VALUE;
        
        
        for (EventWrapper event: events) {
            int iPub = event.getEventIndex();
            coords[3 * iPub] =  coords[3 * iPub + 1] = 0;      // will be set to the center of actors
            float t = event.getEntryTime();
            coords[3 * iPub + 2] = t;
            tmin = Math.min(tmin, t);
            tmax = Math.max(tmax, t);
            String[] words = event.getName().split(" ");
            StringBuilder tBuilder = new StringBuilder();
            int k = 0;
            for (String word : words) {
                String blank = " ";
                k += word.length();
                if (k > PREFERRED_TITLE_LINE_LENGTH) {
                    k = 0;
                    blank = "\n";
                }
                tBuilder.append(word + blank);
            }
            docNames[iPub]    = tBuilder.toString();
            ranges[iPub]      = event.getActors().size();
            itemIndices[iPub] = iPub;
        }
        
        int[] participationEdges = new int[2 * nParticipations];
        int edge = 0;
        for (ParticipationWrapper participation : participations) {
            int iNode = nEvents + participation.getParticipationIndex();
            int iAuth = participation.getActor().getActorIndex();
            int iDoc  = participation.getEvent().getEventIndex();
            for (int i = 0; i < 2; i++) {
                coords[3 * iDoc + i]  = eventCoords[2 * iDoc + i];
                coords[3 * iNode + i] = actorCoords[2 * iAuth + i];
            }
            coords[3 * iNode + 2] = participation.getEvent().getEntryTime();
            participationEdges[2 * edge] = iDoc;
            participationEdges[2 * edge + 1] = iNode;
            edge += 1;
        }
        
        int[] authSetNodes = new int[nActors];
        int[] actorsTimelineEdges = new int[2 * nActorsTimelineIntervals];
        int timelineEdge = 0;
        int iAuth = 0;
        for (ActorWrapper actor : actors) {
            ParticipationWrapper[] cv = new ParticipationWrapper[actor.getParticipations().size()];
            actor.getParticipations().toArray(cv);
            if (cv.length > 1) {
                Arrays.sort(cv);
                itemIndices[nEvents + cv[0].getParticipationIndex()] = nEvents + cv[0].getParticipationIndex();
                for (int i = 1; i < cv.length; i++) {
                    actorsTimelineEdges[2 * timelineEdge]     = nEvents + cv[i - 1].getParticipationIndex();
                    actorsTimelineEdges[2 * timelineEdge + 1] = nEvents + cv[i].getParticipationIndex();
                    timelineEdge += 1;
                    itemIndices[nEvents + cv[i].getParticipationIndex()] = nEvents + cv[0].getParticipationIndex();
                }
            }
            else
                itemIndices[nEvents + cv[0].getParticipationIndex()] = nEvents + cv[0].getParticipationIndex();
            
            authNames[nEvents + cv[0].getParticipationIndex()] = cv[0].getActor().getActorName();
            authSetNodes[iAuth] = nEvents + cv[0].getParticipationIndex();
            iAuth += 1;
        }
        
        for (int i = 0; i < nNodes; i++)
            coords[3 * i + 2] = 10 * (coords[3 * i + 2] - tmin) / (tmax - tmin);
        outField.setCurrentCoords(new FloatLargeArray(coords));
        
        outField.addComponent(DataArray.create(authNames, 1, "actor name"));
        outField.addComponent(DataArray.create(docNames, 1, "event name"));
        outField.addComponent(DataArray.create(ranges, 1, "range"));
        outField.setUserData(new String[] {String.format("%3d actors %3d events", nActors, nEvents)});
        
        CellSet participationCS = new CellSet("participations");
        int[] pubSetNodes = new int[nEvents];
        for (int i = 0; i < pubSetNodes.length; i++)
            pubSetNodes[i] = i;
        participationCS.addCells(new CellArray(CellType.SEGMENT, participationEdges, null, null));
        participationCS.addCells(new CellArray(CellType.POINT, pubSetNodes, null, null));
        outField.addCellSet(participationCS);
        
        CellSet actorsCS = new CellSet("actors");
        actorsCS.addCells(new CellArray(CellType.SEGMENT, actorsTimelineEdges, null, null));
        actorsCS.addCells(new CellArray(CellType.POINT, authSetNodes, null, null));
        outField.addCellSet(actorsCS);
        
        return outField;
    }
    
    public static IrregularField getConnectedComponent(IrregularField in, int iComponent)
    {
        if (in.getComponent("component") == null || 
            in.getComponent("component").getUserData() == null || 
            in.getComponent("component").getUserData(0) == null ||
            !(in.getComponent("component").getUserData(0).equals("connected component indices")))
            return in;
        int[] components = in.getComponent("component").getRawIntArray().getData();
        float[] inCrds = in.getCurrentCoords().getData();
        int[] inRanges = in.getComponent("type").getRawIntArray().getData();
        int[] inIndices = new int[components.length];
        Arrays.fill(inIndices, -1);
        int nComponentNodes = 0;
        int nComponentEdges = 0;
        for (int i = 0; i < in.getNNodes(); i++) 
            if (components[i] == iComponent)
                nComponentNodes += 1;
        if (nComponentNodes == 0)
            return null;
        IrregularField out = new IrregularField(nComponentNodes);
        float[] outCrds = new float[3 * nComponentNodes];
        int[] outRanges = new int[nComponentNodes];
        for (int i = 0, l = 0; i < in.getNNodes(); i++) 
            if (components[i] == iComponent) {
                System.arraycopy(inCrds, 3 * i, outCrds, 3 * l, 3);
                outRanges[l] = inRanges[i];
                inIndices[i] = l;
                l += 1;
            }
        out.setCurrentCoords(new FloatLargeArray(outCrds));
        out.addComponent(DataArray.create(outRanges, 1, "type"));
        
        int[] inEdges = in.getCellSet(0).getCellArray(CellType.SEGMENT).getNodes();
        for (int i = 0; i < inEdges.length; i += 2) 
            if (components[inEdges[i]] == iComponent && components[inEdges[i + 1]] == iComponent)
                nComponentEdges += 1;
        int[] componentEdges = new int[2 * nComponentEdges];
        int[] componentEdgeIndices = new int[nComponentEdges];
        for (int i = 0, l = 0; i < inEdges.length; i += 2) 
            if (components[inEdges[i]] == iComponent && components[inEdges[i + 1]] == iComponent) {
                componentEdges[2 * l] = inIndices[inEdges[i]];
                componentEdges[2 * l + 1] = inIndices[inEdges[i + 1]];
                componentEdgeIndices[l] = l;
                l += 1;
            }
        IntVectorHeapSort.sort(componentEdges, componentEdgeIndices, 2);
        CellSet cmpCS = new CellSet();
        cmpCS.addCells(new CellArray(CellType.SEGMENT, componentEdges, null, null));
        out.addCellSet(cmpCS);
        return out;
    }
    
    
}
