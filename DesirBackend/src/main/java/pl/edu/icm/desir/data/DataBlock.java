package pl.edu.icm.desir.data;

import java.text.SimpleDateFormat;

public class DataBlock {
    
    private final String id;
    private final String text;
    private final String timestamp;
    private float[] coords = null;
    private String[] nodeData = null;
    private int[] segments = null;
    private String[] segmentData = null;
    
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
     * @return the nodeData
     */
    public String[] getNodeData()
    {
        return nodeData;
    }

    /**
     * @param nodeData the nodeData to set
     */
    public void setNodeData(String[] nodeData)
    {
        this.nodeData = nodeData;
    }

    /**
     * @return the segmentData
     */
    public String[] getSegmentData()
    {
        return segmentData;
    }

    /**
     * @param segmentData the segmentData to set
     */
    public void setSegmentData(String[] segmentData)
    {
        this.segmentData = segmentData;
    }
}
