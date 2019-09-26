package pl.edu.icm.desir.core;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bibsonomy.common.enums.GroupingEntity;
import org.bibsonomy.model.enums.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pl.edu.icm.desir.data.DataBlock;
import pl.edu.icm.desir.data.exchange.BibsonomyApiModelExtractor;
import pl.edu.icm.desir.data.exchange.JsonModelExtractor;
import pl.edu.icm.desir.data.exchange.ModelBuilder;
import pl.edu.icm.desir.data.exchange.RdfModelExtractor;
import pl.edu.icm.desir.data.graph.GraphGenerator;
import pl.edu.icm.desir.data.graph.Interaction;
import pl.edu.icm.desir.data.model.Actor;
import pl.edu.icm.jlargearrays.ObjectLargeArray;
import pl.edu.icm.jscic.IrregularField;
import pl.edu.icm.jscic.cells.CellType;
//import org.json.JSONObject;

@RestController
public class Controller
{

    @Autowired
	GraphGenerator graphGenerator;
	
	@CrossOrigin
    @GetMapping("/dataobject")
    public DataBlock getDataBlock(@RequestParam String id, @RequestParam(required = false, defaultValue = "") String text)
    {
        System.out.println("==== in controller - data block request: id=" + id + " text=" + text);

        switch (id) {
            case "Test1":
                return createTest1(id, text);
            case "Test2":
                return createTest2(id, text);
        }

        return null;
    }

    @CrossOrigin
    @GetMapping("/retrieveBibsonomyQuery")
    public DataBlock retrieveBibsonomyQuery(@RequestParam String login, String text, String apikey, String refUsername, String tags, boolean doPlacement, String query)
    {
        /*
    	String login = "bborucki";
        String apikey = "478befa38932d2cf85674ae05ebdad57";
        String refUsername = "jaeschke";
        String[] tags = new String[]{
            "recommender"
        };
        */
      
    	try {
    		ModelBuilder builder = new BibsonomyApiModelExtractor(login, apikey, GroupingEntity.USER, refUsername, Arrays.asList(tags.split(",")), "", (query != null && (query.isEmpty() || query.contentEquals("undefined")))?null:query, null, null, Order.ADDED, null, null, 0, 1000);
            builder.parseInputData(null);
    		return processModel(builder, true);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static DataBlock createTest1(String id, String text)
    {
         float[] coords = new float[]{
            
            //year 1
            -1.0f,  0.0f,  8.0f, //A1Y1s 0
             1.0f,  0.0f,  8.0f, //A2Y1s 1           
            -2.0f,  1.0f,  8.0f, //E1Y1p 2
             2.0f,  1.0f,  8.0f, //E2Y1p 3

            //year 2
            -1.0f,  0.0f,  6.0f, //A1Y2v 4
             0.0f,  0.5f,  6.0f, //E3Y2s 5

            //year 3        
             1.0f,  0.0f,  4.0f, //A2Y1v 6
             0.0f,  0.5f,  4.0f, //E3Y2v 7
             
            //year 4
            -1.0f,  0.0f,  2.0f, //A1Y2v 8
             1.0f,  0.0f,  2.0f, //A2Y1v 9
             0.0f,  0.5f,  2.0f, //E3Y2e 10
             
            //year 5
            -1.0f,  0.0f,  0.0f, //A1Y1e 11
             1.0f,  0.0f,  0.0f, //A2Y1e 12          
            -2.0f, -1.0f,  0.0f, //E4Y1p 13
             2.0f, -1.0f,  0.0f, //E5Y1p 14          
        };
        
        
        int[] actorNodeIndices = new int[] {
            0,1,4,6,8,9,11,12
        };
        
        int[] eventNodeIndices = new int[] {
            2,3,5,7,10,13,14
        };
        
        String[] nodeDataIDs = new String[]{
             //year 1
            "A1", //0
            "A2", //1
            "E1", //2
            "E2", //3

            //year 2
            "A1", //4
            "E3", //5
            
            //year 3
            "A2", //6
            "E3", //7
            
            //year 4
            "A1", //8
            "A2", //9
            "E3", //10
            
            //year 5
            "A1", //11
            "A2", //12
            "E4", //13
            "E5", //14         
        };
        

        int[] segments = new int[]{
            //year1
            0,2,
            1,3,

            //year2
            4,5,
            
            //year3
            6,7,
            
            //year4
            8,10,
            9,10,    
            
            //year5
            11,13,
            12,14,
            
            //A1
            0,4,
            4,8,
            8,11,
            //A2
            1,6,
            6,9,
            9,12,
            
            //E3
            5,7,
            7,10,
        };
        
        int[] actorSegmentIndices = new int[] {
            8,9,10,11,12,13
        };
        
        int[] eventSegmentIndices = new int[] {
            14,15
        };
        
        int[] participSegmentIndices = new int[] {
            0,1,2,3,4,5,6,7
        };
        
        String[] segmentDataIDs = new String[]{
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            
            "A1",
            "A1",
            "A1",
            "A2",
            "A2",
            "A2",
            
            "E3",
            "E3",
        };
        
        int[] quads = new int[] {
            4,5,8,10,
            6,7,9,10
        };
        
        String[] quadDataIDs = new String[]{
            "A1@E3",
            "A2@E3"
        };
        
      
        DataBlock db = new DataBlock(id, text);
        db.setCoords(coords, true);
        db.setActorNodeIndices(actorNodeIndices);
        db.setEventNodeIndices(eventNodeIndices);
        db.setNodeDataIDs(nodeDataIDs);
        db.setSegments(segments);
        db.setActorSegmentIndices(actorSegmentIndices);
        db.setEventSegmentIndices(eventSegmentIndices);
        db.setParticipSegmentIndices(participSegmentIndices);
        db.setSegmentDataIDs(segmentDataIDs);
        db.setQuads(quads);
        db.setQuadDataIDs(quadDataIDs);
 
        return db;

    }

    private static DataBlock createTest2(String id, String text)
    {
        float[] coords = new float[]{
            
            //year 1
            -2.0f,  0.0f,  8.0f, //A1Y1 0
             0.0f,  0.0f,  8.0f, //A2Y1 1
            -1.0f, -2.0f,  8.0f, //A3Y1 2
             
            -1.0f, -1.0f,  8.0f, //P1Y1 3

            //year 2
             0.0f,  0.0f,  4.0f, //A2Y2 4
             2.0f,  0.0f,  4.0f, //A4Y2 5
             1.0f, -2.0f,  4.0f, //A5Y2 6
             
             1.0f, -1.0f,  4.0f, //P2Y2 7


            //year 3
            -2.0f,  0.0f,  0.0f, //A1Y3 8
             0.0f,  0.0f,  0.0f, //A2Y3 9
             2.0f,  0.0f,  0.0f, //A4Y3 10
             0.0f,  2.0f,  0.0f, //A6Y3 11
             0.0f,  4.0f,  0.0f, //A7Y3 12
             
             0.0f,  1.0f,  0.0f, //P3Y3 13
             0.5f,  3.0f,  0.0f, //P4Y3 14
             
        };
        
        
        int[] actorNodeIndices = new int[] {
            0,1,2,4,5,6,8,9,10,11,12
        };
        
        int[] eventNodeIndices = new int[] {
            3,7,13,14
        };
        
        String[] nodeDataIDs = new String[]{
             //year 1
            "A1", //0
            "A2", //1
            "A3", //2
             
            "P1", //3

            //year 2
            "A2", //4
            "A4", //5
            "A5", //6
             
            "P2", //7

            //year 3
            "A1", //8
            "A2", //9
            "A4", //10
            "A6", //11
            "A7", //12
             
            "P3", //13
            "P4", //14           
        };
        

        int[] segments = new int[]{
            //year1
            0,3, 
            1,3,
            2,3,
            
            //year2
            4,7,
            5,7,
            6,7,
            
            //year3
            8,13,
            9,13,
            10,13,
            11,13,
            11,14,
            12,14,
            
            //A1
            0,8,
            //A2
            1,4,
            4,9,
            //A4
            5,10,
        };
        
        int[] actorSegmentIndices = new int[] {
            12,13,14,15
        };
        
        int[] eventSegmentIndices = null;
        
        int[] participSegmentIndices = new int[] {
            0,1,2,3,4,5,6,7,8,9,10,11
        };
        
        String[] segmentDataIDs = new String[]{
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            
            "A1",
            "A2",
            "A2",
            "A4",
        };
        
        int[] quads = null;
        String[] quadDataIDs = null;
        
      
        DataBlock db = new DataBlock(id, text);
        db.setCoords(coords, true);
        db.setActorNodeIndices(actorNodeIndices);
        db.setEventNodeIndices(eventNodeIndices);
        db.setNodeDataIDs(nodeDataIDs);
        db.setSegments(segments);
        db.setActorSegmentIndices(actorSegmentIndices);
        db.setEventSegmentIndices(eventSegmentIndices);
        db.setParticipSegmentIndices(participSegmentIndices);
        db.setSegmentDataIDs(segmentDataIDs);
        db.setQuads(quads);
        db.setQuadDataIDs(quadDataIDs);
 
        return db;
        
    }


    @CrossOrigin
    @PostMapping("/fileupload")
    public DataBlock fileUpload(@RequestParam("fileupload") MultipartFile file)
    {
        try {
        	ModelBuilder builder = new JsonModelExtractor(file.getOriginalFilename());            
        	builder.parseInputData(file.getInputStream());
            return processModel(builder, true);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @CrossOrigin
    @PostMapping("/rdfFileUpload")
    public DataBlock rdfFileUpload(@RequestParam("rdfFileUpload") MultipartFile file)
    {
        try {  
        	ModelBuilder builder = new RdfModelExtractor(file.getOriginalFilename());
        	builder.parseInputData(file.getInputStream());
        	return processModel(builder, false);
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    private DataBlock processModel(ModelBuilder builder, boolean doPlacement) {        
        //IrregularField outField = graphGenerator.generateGraphDataFromModel(builder);
        //return generateRelationsDataBlock(outField);  
        return graphGenerator.generatePlacedDataBlockFromModel(builder);
    }

    private DataBlock generateRelationsDataBlock(IrregularField outField) {
        float[] coords = outField.getCurrentCoords().getData();

        //author data
        int nNodes = (int) outField.getNNodes();
        String[] nodeData = new String[nNodes];
        ObjectLargeArray authorData = (ObjectLargeArray) outField.getComponent("actors").getRawArray();
        for (int i = 0; i < nNodes; i++) {
            Actor a = (Actor) authorData.get(i);
            nodeData[i] = a.getName();
        }

        //segments
        int[] segments = outField.getCellSet(0).getCellArray(CellType.SEGMENT).getNodes();

        //coauthorship data
        int nEdges = (int) outField.getCellSet(0).getCellArray(CellType.SEGMENT).getNCells();
        String[] segmentData = new String[nEdges];
        float[] segmentFData = new float[nEdges];
        ObjectLargeArray coauthorshipData = (ObjectLargeArray) outField.getCellSet(0).getComponent("edges").getRawArray();
        for (int i = 0; i < nEdges; i++) {
            Interaction ca = (Interaction) coauthorshipData.get(i);
            segmentData[i] = "" + ca.getNames().size();
            segmentFData[i] = ca.getNames().size();
        }

        //create data block
        DataBlock db = new DataBlock("", "");
        db.setCoords(coords, true);
//        db.setNodeData(nodeData);
//        db.setSegments(segments);
//        db.setSegmentData(segmentData);
//        db.setSegmentFData(segmentFData);

        //TBD

        return db;
    }
    
}
