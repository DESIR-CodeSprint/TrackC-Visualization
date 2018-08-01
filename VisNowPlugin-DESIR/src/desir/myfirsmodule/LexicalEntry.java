/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desir.myfirsmodule;

import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;

import pl.edu.icm.jlargearrays.FloatLargeArray;
import pl.edu.icm.jlargearrays.LargeArrayType;
import pl.edu.icm.jlargearrays.LargeArrayUtils;
import pl.edu.icm.jscic.CellArray;
import pl.edu.icm.jscic.CellSet;
import pl.edu.icm.jscic.IrregularField;
import pl.edu.icm.jscic.cells.CellType;
import pl.edu.icm.jscic.dataarrays.DataArray;
import pl.edu.icm.visnow.engine.core.InputEgg;
import pl.edu.icm.visnow.engine.core.OutputEgg;
import pl.edu.icm.visnow.lib.types.VNIrregularField;
/**
 *
 * @author Med
 */
public class LexicalEntry  extends OutFieldVisualizationModule {
//    public static void main(String argv[]) {
//        convertEntries();
   
    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;


    @Override
    public void onActive()
    {
        //create field with 5 nodes
        int nNodes = 3;
        IrregularField myfield = new IrregularField(nNodes);

        //create geometry for our 5 nodes
        // coordinates manually created
//        float[] coords = new float[] {
//            0.0f, 0.0f, 0.0f,
//            0.0f,-1.0f, 0.0f,
//           -2.0f,-2.0f, 0.0f,
//           -1.0f,-2.0f, 0.0f,
//            1.0f,-2.0f, 0.0f
//        };
//        // Create coordinate for myfield objects which are the nodes
//        myfield.setCurrentCoords(new FloatLargeArray(coords));

        // coordinates automatically created
        myfield.setCurrentCoords((FloatLargeArray) LargeArrayUtils.generateRandom(LargeArrayType.FLOAT, 3 * nNodes));



        //create segments
        int[] segments = new int[] {
            0,1,
            0,2,
            1,0,
            2,0
        };

        int[] dataIndices = new int[] {
            0,1,2
        };
        // Create edges with their incice mapping (last parameter)
        CellArray ca = new CellArray(CellType.SEGMENT, segments, null, dataIndices);
        //Create a set of crearted edges
        CellSet cs = new CellSet("my cellset");
        cs.addCells(ca);
        myfield.addCellSet(cs);

        //create values for nodes : author names
        String[] entries = new String[] {
            "can (en)",
            "canette (fr)",
            "pouvoir (fr)"
        };
        DataArray da = DataArray.create(entries, 1, "entries");
        myfield.addComponent(da);
         //create values for segments : pos
         String[] pos = new String[] {
            "noun",
            "verb",
            "nom",
            "verbe"
        };
        
         DataArray da_pos = DataArray.create(pos, 1, "pos");
        cs.addComponent(da_pos);
        System.out.println("HELLO WORLD!");

        outField = myfield;
        outIrregularField = myfield;
        prepareOutputGeometry();
        show();

        setOutputValue("outField", new VNIrregularField(outIrregularField));



    }







//        public static void convertEntries() {
//
//            try {
//
//                File fXmlFile = new File("entries.xml");
//                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
//                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
//                Document doc = dBuilder.parse(fXmlFile);
//
//                //optional, but recommended
//                //read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
//
//
//                NodeList nList = doc.getElementsByTagName("entry");
//
//                System.out.println("----------------------------");
//
//                for (int temp = 0; temp < nList.getLength(); temp++) {
//
//                    Node nNode = nList.item(temp);
//
//                    System.out.println("\nCurrent Element :" + nNode.getNodeName());
//
//                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
//
//                        Element eElement = (Element) nNode;
//                        
//                         String language = eElement.getAttribute("xml:lang");
//                         System.out.println("language : " + language);
//                         
//                       for (int formContentCounter = 0; formContentCounter < eElement.getElementsByTagName("form").item(0).getChildNodes().getLength(); formContentCounter++) {
//                           if(eElement.getElementsByTagName("form").item(0).getChildNodes().item(formContentCounter).getNodeName().equals("orth")){
//                                String lemma = eElement.getElementsByTagName("form").item(0).getChildNodes().item(formContentCounter).getTextContent();
//                                System.out.println("Lemma : " + lemma);
//                         
//                            }
//                           if(eElement.getElementsByTagName("form").item(0).getChildNodes().item(formContentCounter).getNodeName().equals("pos")){
//                                String pos = eElement.getElementsByTagName("form").item(0).getChildNodes().item(formContentCounter).getTextContent();
//                                System.out.println("POS : " + pos);
//                         
//                            }
//                       }
//                        
//
//                        
//                         
//                        
//                        
//                     
//                        
//     
//  
//                        Element relatedEntry = (Element) nNode;
//                        
//                       
//                       
//                        System.out.println("POS : " + eElement.getElementsByTagName("form").getElementsByTagName("pos")).item(0).getTextContent());
//                        System.out.println("entry to link to : " + eElement.getElementsByTagName("sense").getElementsByTagName("xr").item(0).getTextContent());
//                        for (int senseCounter = 1; senseCounter < eElement.getElementsByTagName("sense").getLength(); senseCounter++) {
//                            
//                           if(eElement.getElementsByTagName("sense").item(0).getChildNodes().item(senseCounter).getNodeName().equals("gramGrp")){
//                               eElement.getElementsByTagName("sense").item(0).getChildNodes().item(senseCounter).getNodeName()
//                                System.out.println("POS : " + eElement.getElementsByTagName("sense").getElementsByTagName("gramGrp").getElementsByTagName("pos").item(0).getTextContent());
//                               
//                           }
//                            System.out.println("POS : " + eElement.getElementsByTagName("sense").getElementsByTagName("gramGrp").getElementsByTagName("pos").item(0).getTextContent());
//                            System.out.println("entry to link to : " + eElement.getElementsByTagName("xr").item(0).getTextContent());
//                        }
//
//
//
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }



}
