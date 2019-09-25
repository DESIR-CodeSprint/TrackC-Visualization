package pl.edu.icm.desir.data;

import java.text.SimpleDateFormat;

public class DataBlock {
    
    public static final int NODE_TYPE_ACTOR_POINT = 10;
    public static final int NODE_TYPE_ACTOR_START = 11;
    public static final int NODE_TYPE_ACTOR_END = 12;
    public static final int NODE_TYPE_ACTOR_INTERMEDIATE = 13;
    
    public static final int NODE_TYPE_EVENT_POINT = 20;
    public static final int NODE_TYPE_EVENT_START = 21;
    public static final int NODE_TYPE_EVENT_END = 22;
    public static final int NODE_TYPE_EVENT_INTERMEDIATE = 23;
    
    public static final int SEGMENT_TYPE_ACTOR = 101;
    public static final int SEGMENT_TYPE_EVENT = 102;
    public static final int SEGMENT_TYPE_PARTICIP = 103;
    
    private final String id;
    private final String text;
    private final String timestamp;
    
    private float[] coords = null;
    private int nNodes = 0;
    
    private int[] actorNodeIndices = null;
    private int[] eventNodeIndices = null;
    private String[] nodeDataIDs = null;
    
    private int[] segments = null;
    private int[] actorSegmentIndices = null;
    private int[] eventSegmentIndices = null;
    private int[] participSegmentIndices = null;
    private String[] segmentDataIDs = null;
    
    private int[] quads = null;
    private String[] quadDataIDs = null;
    
    private String[] data = null;
    
    public DataBlock(String id, String text) {
        this.id = id;
        this.text = text;
        this.timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getId() {
        return id;
    }    
              
    public String getText(){
        return text;
    }
    
    public float[] getCoords()
    {
        return coords;
    }
    
    /**
     * @param coords the coords to set
     */
    public void setCoords(float[] coords, boolean normalize)
    {
        this.coords = coords;
        this.nNodes = coords.length/3;
        if(normalize) {
            float[] min = new float[]{Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE};
            float[] max = new float[]{Float.MIN_VALUE,Float.MIN_VALUE,Float.MIN_VALUE};
            int nPoints = this.coords.length/3;
            float[] p = new float[3];
            for (int i = 0; i < nPoints; i++) {
                System.arraycopy(this.coords, 3*i, p, 0, 3);
                for (int j = 0; j < 3; j++) {
                    if(p[j] < min[j]) min[j] = p[j];    
                    if(p[j] > max[j]) max[j] = p[j];  
                }
            }
            float[] center = new float[3];
            float[] range = new float[3];
            float maxRange = 0.0f;
            for (int i = 0; i < 3; i++) {
                center[i] = (max[i]+min[i])/2.0f;
                range[i] = Math.abs(max[i]-min[i]);   
                if(range[i] > maxRange) maxRange = range[i];
            }
            maxRange /= 2.0f;
            
            for (int i = 0; i < nPoints; i++) {
                for (int j = 0; j < 3; j++) {
                    this.coords[3*i+j] = (this.coords[3*i+j] - center[j])/maxRange;
                }
            }
        }
    }
    
    /**
     * @return the actorNodeIndices
     */
    public int[] getActorNodeIndices()
    {
        return actorNodeIndices;
    }

    /**
     * @param actorNodeIndices the actorNodeIndices to set
     */
    public void setActorNodeIndices(int[] actorNodeIndices)
    {
        this.actorNodeIndices = actorNodeIndices;
    }

    /**
     * @return the eventNodeIndices
     */
    public int[] getEventNodeIndices()
    {
        return eventNodeIndices;
    }

    /**
     * @param eventNodeIndices the eventNodeIndices to set
     */
    public void setEventNodeIndices(int[] eventNodeIndices)
    {
        this.eventNodeIndices = eventNodeIndices;
    }

    /**
     * @return the nodeDataIDs
     */
    public String[] getNodeDataIDs()
    {
        return nodeDataIDs;
    }

    /**
     * @param nodeDataIDs the nodeDataIDs to set
     */
    public void setNodeDataIDs(String[] nodeDataIDs)
    {
        this.nodeDataIDs = nodeDataIDs;
    }

    /**
     * @return the segments
     */
    public int[] getSegments()
    {
        return segments;
    }

    /**
     * @param segments the segments to set
     */
    public void setSegments(int[] segments)
    {
        this.segments = segments;
    }

    /**
     * @return the actorSegmentIndices
     */
    public int[] getActorSegmentIndices()
    {
        return actorSegmentIndices;
    }

    /**
     * @param actorSegmentIndices the actorSegmentIndices to set
     */
    public void setActorSegmentIndices(int[] actorSegmentIndices)
    {
        this.actorSegmentIndices = actorSegmentIndices;
    }

    /**
     * @return the eventSegmentIndices
     */
    public int[] getEventSegmentIndices()
    {
        return eventSegmentIndices;
    }

    /**
     * @param eventSegmentIndices the eventSegmentIndices to set
     */
    public void setEventSegmentIndices(int[] eventSegmentIndices)
    {
        this.eventSegmentIndices = eventSegmentIndices;
    }

    /**
     * @return the participSegmentIndices
     */
    public int[] getParticipSegmentIndices()
    {
        return participSegmentIndices;
    }

    /**
     * @param participSegmentIndices the participSegmentIndices to set
     */
    public void setParticipSegmentIndices(int[] participSegmentIndices)
    {
        this.participSegmentIndices = participSegmentIndices;
    }

    /**
     * @return the segmentDataIDs
     */
    public String[] getSegmentDataIDs()
    {
        return segmentDataIDs;
    }

    /**
     * @param segmentDataIDs the segmentDataIDs to set
     */
    public void setSegmentDataIDs(String[] segmentDataIDs)
    {
        this.segmentDataIDs = segmentDataIDs;
    }

    /**
     * @return the quads
     */
    public int[] getQuads()
    {
        return quads;
    }

    /**
     * @param quads the quads to set
     */
    public void setQuads(int[] quads)
    {
        this.quads = quads;
    }

    /**
     * @return the quadDataIDs
     */
    public String[] getQuadDataIDs()
    {
        return quadDataIDs;
    }

    /**
     * @param quadDataIDs the quadDataIDs to set
     */
    public void setQuadDataIDs(String[] quadDataIDs)
    {
        this.quadDataIDs = quadDataIDs;
    }

    /**
     * @return the data
     */
    public String[] getData()
    {
        return data;
    }

    /**
     * @param data the data to set
     */
    public void setData(String[] data)
    {
        this.data = data;
    }

    /**
     * @return the nNodes
     */
    public int getnNodes()
    {
        return nNodes;
    }
  
    
    
}
