/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desir.myfirsmodule;

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
import pl.edu.icm.visnow.lib.templates.visualization.modules.OutFieldVisualizationModule;
import pl.edu.icm.visnow.lib.types.VNIrregularField;

/**
 *
 * @author Med
 */
public class FirstNewClass extends OutFieldVisualizationModule {


    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;

  
    @Override
    public void onActive()
    {
        //create field with 5 nodes
        int nNodes = 5;
        IrregularField myfield = new IrregularField(nNodes);
        
        //create geometry for our 5 nodes
        // coordinates manually created
        float[] coords = new float[] {
            0.0f, 0.0f, 0.0f,
            0.0f,-1.0f, 0.0f,                        
           -2.0f,-2.0f, 0.0f,
           -1.0f,-2.0f, 0.0f,
            1.0f,-2.0f, 0.0f          
        };
        // Create coordinate for myfield objects which are the nodes
        myfield.setCurrentCoords(new FloatLargeArray(coords));

        // coordinates automatically created
//        myfield.setCurrentCoords((FloatLargeArray) LargeArrayUtils.generateRandom(LargeArrayType.FLOAT, 3 * nNodes));

        
        
        //create segments
        int[] segments = new int[] {
            0,1,
            1,3,
            1,4,
            2,3,
            3,4
        };
        
        int[] dataIndices = new int[] {
            0,1,2,3,4
        };
        // Create edges with their incice mapping (last parameter)
        CellArray ca = new CellArray(CellType.SEGMENT, segments, null, dataIndices);
        //Create a set of crearted edges
        CellSet cs = new CellSet("my cellset");
        cs.addCells(ca);
        myfield.addCellSet(cs);
        
        //create values for nodes : author names
        String[] data = new String[] {
            "Author name 1",
            "Author name 2",
            "Author name 3",
            "Author name 4",
            "Author name 5"
        };
        DataArray da = DataArray.create(data, 1, "names");
        myfield.addComponent(da);
         //create values for segments : coworks
         int[] coworks = new int[] {
            1,
            1,
            2,
            1,
            5
        };
         DataArray da_coworks = DataArray.create(coworks, 1, "N coworks");
        cs.addComponent(da_coworks);
        System.out.println("HELLO WORLD!");
        
        outField = myfield;
        outIrregularField = myfield;
        prepareOutputGeometry();
        show();
        
        setOutputValue("outField", new VNIrregularField(outIrregularField));



    }

}
