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
import pl.edu.icm.desir.ForcePlacement.PlacementCore;
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
            -1.0f, 0.0f, -1.0f,
            0.5f, 1.0f, 1.0f,
            -0.5f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.5f,
            1.0f, 0.0f, 1.0f,};
        String[] nodeData = new String[]{
            "Branden Kutz",
            "Eboni Mantle",
            "Alysa Haigler",
            "Keena Dragoo",
            "Barbra Staller"
        };

        int[] segments = new int[]{
            0, 1,
            2, 1,
            3, 4,
            3, 1,};
        String[] segmentData = new String[segments.length / 2];
        for (int i = 0; i < segmentData.length; i++) {
            segmentData[i] = nodeData[segments[2 * i]] + " <---> " + nodeData[segments[2 * i + 1]];
        }
        
        float[] segmentFData = new float[segments.length / 2];
        for (int i = 0; i < segmentFData.length; i++) {
            segmentFData[i] = (float) Math.random() * 100;   
        }

        DataBlock db = new DataBlock(id, text);
        db.setCoords(coords, false);
        db.setNodeData(nodeData);
        db.setSegments(segments);
        db.setSegmentData(segmentData);
        db.setSegmentFData(segmentFData);

        return db;
    }

    private static DataBlock createTest2(String id, String text)
    {

//        float[] coords = new float[]{
//            0.0f, 0.0f, 0.0f,
//            2.0f, 0.0f, 0.0f,
//            2.0f, 2.0f, 0.0f,
//            0.0f, 2.0f, 0.0f,
//            0.0f, 0.0f, 2.0f,
//            2.0f, 0.0f, 2.0f,
//            2.0f, 2.0f, 2.0f,
//            0.0f, 2.0f, 2.0f,};
//        String[] nodeData = new String[]{
//            "A",
//            "B",
//            "C",
//            "D",
//            "E",
//            "F",
//            "G",
//            "H",};
//
//        int[] segments = new int[]{
//            0, 1,
//            1, 2,
//            2, 3,
//            3, 0,
//            4, 5,
//            5, 6,
//            6, 7,
//            7, 4,};
//        String[] segmentData = new String[]{
//            "A<->B",
//            "B<->C",
//            "C<->D",
//            "D<->A",
//            "E<->F",
//            "F<->G",
//            "G<->H",
//            "H<->E",};
//        float[] segmentFData = new float[segments.length / 2];
//        for (int i = 0; i < segmentFData.length; i++) {
//            segmentFData[i] = (float) Math.random() * 100;   
//        }
//
//        DataBlock db = new DataBlock(id, text);
//        db.setCoords(coords, true);
//        db.setNodeData(nodeData);
//        db.setSegments(segments);
//        db.setSegmentData(segmentData);
//        db.setSegmentFData(segmentFData);
//        return db;
        
        float[] coords = new float[]{
            
            //year 1
            -2.0f,  0.0f,  8.0f, //A1Y1
             0.0f,  0.0f,  8.0f, //A2Y1
            -1.0f, -2.0f,  8.0f, //A3Y1
             
            -1.0f, -1.0f,  8.0f, //P1Y1

            //year 2
             0.0f,  0.0f,  4.0f, //A2Y2
             2.0f,  0.0f,  4.0f, //A4Y2
             1.0f, -2.0f,  4.0f, //A5Y2
             
             1.0f, -1.0f,  4.0f, //P2Y2


            //year 3
            -2.0f,  0.0f,  0.0f, //A1Y3
             0.0f,  0.0f,  0.0f, //A2Y3
             2.0f,  0.0f,  0.0f, //A4Y3
             0.0f,  2.0f,  0.0f, //A6Y3
             0.0f,  4.0f,  0.0f, //A7Y3
             
             0.0f,  1.0f,  0.0f, //P3Y3
             0.5f,  3.0f,  0.0f, //P4Y3
             
        };
        
        int VIRTUAL = 0;
        int ACTOR_POINT = 10;
        int ACTOR_START = 11;
        int ACTOR_END = 12;
        int EVENT_POINT = 20;
        
         int[] nodeType = new int[]{
             //year 1
            ACTOR_START, //"A1Y1"
            ACTOR_START, //"A2Y1"
            ACTOR_POINT, //"A3Y1"
             
            EVENT_POINT, //"P1Y1"

            //year 2
            VIRTUAL, //"A2Y2"
            ACTOR_START, //"A4Y2"
            ACTOR_POINT, //"A5Y2"
             
            EVENT_POINT, //"P2Y2"

            //year 3
            ACTOR_END, //"A1Y3"
            ACTOR_END, //"A2Y3"
            ACTOR_END, //"A4Y3"
            ACTOR_POINT, //"A6Y3"
            ACTOR_POINT, //"A7Y3"
             
            EVENT_POINT, //"P3Y3"
            EVENT_POINT, //"P4Y3"          
        };   
        
        String[] nodeData = new String[]{
             //year 1
            "A1", //0
            "A2", //1
            "A3", //2
             
            "P1 @ YEAR1", //3

            //year 2
            "A2", //4
            "A4", //5
            "A5", //6
             
            "P2 @ YEAR2", //7

            //year 3
            "A1", //8
            "A2", //9
            "A4", //10
            "A6", //11
            "A7", //12
             
            "P3 @ YEAR3", //13
            "P4 @ YEAR3", //14           
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
            1,9,
            //A4
            5,10,
            
            
            
        };
        
        String[] segmentData = new String[]{
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
            "A4",
        };
        
        float[] segmentFData = new float[segments.length / 2];
        for (int i = 0; i < segmentFData.length; i++) {
            segmentFData[i] = (float) Math.random() * 100;   
        }

        DataBlock db = new DataBlock(id, text);
        db.setCoords(coords, true);
        db.setNodeData(nodeData);
        db.setSegments(segments);
        db.setSegmentData(segmentData);
        db.setSegmentFData(segmentFData);
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
        IrregularField outField = graphGenerator.generateGraphDataFromModel(builder);
        if(doPlacement)
            outField = PlacementCore.optimizePlacement(outField,
                                                      (float) .7, 1, 100,
                                                      1, 4, (float) .001, 0,
                                                      null);
        return generateRelationsDataBlock(outField);   
            
      //      return graphGenerator.generateDataBlockFromModel(builder);
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
        db.setNodeData(nodeData);
        db.setSegments(segments);
        db.setSegmentData(segmentData);
        db.setSegmentFData(segmentFData);

        return db;
    }
}
