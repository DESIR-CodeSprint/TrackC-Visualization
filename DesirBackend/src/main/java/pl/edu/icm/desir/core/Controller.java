package pl.edu.icm.desir.core;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import pl.edu.icm.desir.data.DataBlock;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.icm.desir.ForcePlacement.PlacementCore;
import pl.edu.icm.desir.ReadBibSonomy.Author;
import pl.edu.icm.desir.ReadBibSonomy.Coauthorship;
import pl.edu.icm.desir.ReadBibSonomy.ReadBibSonomyCore;
import pl.edu.icm.jlargearrays.ObjectLargeArray;
import pl.edu.icm.jscic.IrregularField;
import pl.edu.icm.jscic.cells.CellType;

@RestController
public class Controller
{

    @CrossOrigin(origins = "http://localhost:8383")
    @GetMapping("/dataobject")
    public DataBlock getDataBlock(@RequestParam String id, @RequestParam(required = false, defaultValue = "") String text)
    {
        System.out.println("==== in controller - data block request: id=" + id + " text=" + text);

        switch (id) {
            case "Test1":
                return createTest1(id, text);
            case "Test2":
                return createTest2(id, text);
            case "TestJSON":
                return createTestJson(id, text, false);
            case "TestQuery":
                return createTestQuery(id, text);
            case "TestYearJSON":
                return createTestYearJson(id, text);
            case "smallTestYearJSON":
                return createSmallTestYearJson(id, text);
        }

        return null;
    }
    
    private static DataBlock createTestQuery(String id, String text)
    {
        //TBD
        //IrregularField outField = ReadBibSonomyCore.generateCoauthorshipUsingREST(login, apikey, username, Arrays.asList(tags));
 
        return null;

    }
    

    private static DataBlock createTestJson(String id, String text, boolean doPlacement)
    {
        try {
            IrregularField outField = ReadBibSonomyCore.generateCoauthorshipFromFile(System.getProperty("user.dir") + File.separator + "posts.json");

//TBD - graph placement
//
//            if(doPlacement)
//                outField = PlacementCore.optimizePlacement(outField,  
//                                                           (float).7, 1, 100,
//                                                           1, 4, (float).001, 0, 
//                                                           null);
            
            
            //coords
            float[] coords = outField.getCurrentCoords().getData();

            //author data
            int nNodes = (int) outField.getNNodes();
            String[] nodeData = new String[nNodes];
            ObjectLargeArray authorData = (ObjectLargeArray) outField.getComponent("authors").getRawArray();
            for (int i = 0; i < nNodes; i++) {
                Author a = (Author) authorData.get(i);
                nodeData[i] = a.getAuthorName().getFirstName() + " " + a.getAuthorName().getLastName();
            }

            //segments
            int[] segments = outField.getCellSet(0).getCellArray(CellType.SEGMENT).getNodes();

            //coauthorship data
            int nEdges = (int) outField.getCellSet(0).getCellArray(CellType.SEGMENT).getNCells();
            String[] segmentData = new String[nEdges];
            ObjectLargeArray coauthorshipData = (ObjectLargeArray) outField.getCellSet(0).getComponent("edges").getRawArray();
            for (int i = 0; i < nEdges; i++) {
                Coauthorship ca = (Coauthorship) coauthorshipData.get(i);
                segmentData[i] = ""+ca.getTitles().size();
            }
            

            //create data block
            DataBlock db = new DataBlock(id, text);
            db.setCoords(coords, true);
            db.setNodeData(nodeData);
            db.setSegments(segments);
            db.setSegmentData(segmentData);

            return db;

        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;

    }

        private static DataBlock createTestYearJson(String id, String text)
    {
        try {
            Logger.getLogger(Controller.class.getName()).log(Level.INFO, "Opening file: " + System.getProperty("user.dir") + File.separator + "posts-small.json");

            IrregularField outField = ReadBibSonomyCore.generateCoauthorshipFromFile2(System.getProperty("user.dir") + File.separator + "posts-small.json");


            //author data
            int nNodes = (int) outField.getNNodes();
            String[] nodeData = new String[nNodes];
            ObjectLargeArray authorData = (ObjectLargeArray) outField.getComponent("authors").getRawArray();
            for (int i = 0; i < nNodes; i++) {
                Author a = (Author) authorData.get(i);
System.out.println(" === Controller === : parsing author: " + a.getAuthorName().getFirstName() + " " + a.getAuthorName().getLastName());
                nodeData[i] = a.getAuthorName().getFirstName() + " " + a.getAuthorName().getLastName();
            }


            //publications data
            int iPublications = (int) outField.getNNodes();
            String[] publicationsData = new String[iPublications];
            ObjectLargeArray publicationData = (ObjectLargeArray) outField.getComponent("publications").getRawArray();
            for (int i = 0; i < iPublications; i++) {
                String iPub = (String) publicationData.get(i);
System.out.println(" === Controller === : parsing publications: " + iPub);
                publicationsData[i] = iPub;
            }            
            
            /*
            //segments
            int[] segments = outField.getCellSet(0).getCellArray(CellType.SEGMENT).getNodes();

            //coauthorship data
            int nEdges = (int) outField.getCellSet(0).getCellArray(CellType.SEGMENT).getNCells();
            String[] segmentData = new String[nEdges];
            ObjectLargeArray coauthorshipData = (ObjectLargeArray) outField.getCellSet(0).getComponent("edges").getRawArray();
            for (int i = 0; i < nEdges; i++) {
                Coauthorship ca = (Coauthorship) coauthorshipData.get(i);
                segmentData[i] = ""+ca.getTitles().size();
            }
*/            

            //create data block
            DataBlock db = new DataBlock(id, text);
            db.setNodeData(nodeData);
            db.setSegmentData(publicationsData);

            return db;

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
            "A",
            "B",
            "C",
            "D",
            "E",};

        int[] segments = new int[]{
            0, 1,
            2, 1,
            3, 4,
            3, 1,};
        String[] segmentData = new String[]{
            "A<->B",
            "C<->B",
            "D<->E",
            "E<->A",};

        DataBlock db = new DataBlock(id, text);
        db.setCoords(coords, false);
        db.setNodeData(nodeData);
        db.setSegments(segments);
        db.setSegmentData(segmentData);

        return db;
    }

    private static DataBlock createTest2(String id, String text)
    {

        float[] coords = new float[]{
            0.0f, 0.0f, 0.0f,
            2.0f, 0.0f, 0.0f,
            2.0f, 2.0f, 0.0f,
            0.0f, 2.0f, 0.0f,
            0.0f, 0.0f, 2.0f,
            2.0f, 0.0f, 2.0f,
            2.0f, 2.0f, 2.0f,
            0.0f, 2.0f, 2.0f,};
        String[] nodeData = new String[]{
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",};

        int[] segments = new int[]{
            0, 1,
            1, 2,
            2, 3,
            3, 0,
            4, 5,
            5, 6,
            6, 7,
            7, 4,};
        String[] segmentData = new String[]{
            "A<->B",
            "B<->C",
            "C<->D",
            "D<->A",
            "E<->F",
            "F<->G",
            "G<->H",
            "H<->E",};

        DataBlock db = new DataBlock(id, text);
        db.setCoords(coords, true);
        db.setNodeData(nodeData);
        db.setSegments(segments);
        db.setSegmentData(segmentData);
        return db;
    }

}
