/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desir.tibormodule;

import java.sql.Timestamp;
import pl.edu.icm.jlargearrays.FloatLargeArray;
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
 * @author tkalman
 */
public class TiborModule extends OutFieldVisualizationModule {

    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;

    @Override
    public void onActive() {
        //extract from input
        
        
        
        //create field with 5 nodes
        int nNodes = 5;
        IrregularField myfield = new IrregularField(nNodes);

        //create geometry for our 5 nodes
        float[] coords = new float[]{
            0.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            -2.0f, -2.0f, 0.0f,
            -1.0f, -2.0f, 0.0f,
            1.0f, -2.0f, 0.0f
        };
        myfield.setCurrentCoords(new FloatLargeArray(coords));
        /* same as myfield.setCoords(new FloatLargeArray(coords), 0.0f); */

        //or random:
        //myfield.setCurrentCoords((FloatLargeArray) LargeArrayUtils.generateRandom(LargeArrayType.FLOAT, 3 * nNodes));
 
        // create segments
        int[] segments = new int[]{
            0, 1,
            1, 3,
            1, 4,
            2, 3,
            3, 4
        };
        
        int[] dataIndices = new int[]{
            0, 1, 2, 3, 4
        };                          //direct mapping: data 0 <-> segment 0 , data 1 <-> segment 1 , ...

        CellArray ca = new CellArray(CellType.SEGMENT, segments, null, dataIndices);
        CellSet cs = new CellSet("Tibor's cellset");
        cs.addCells(ca);
        myfield.addCellSet(cs);

        //create values
        int[] data = new int[]{
            1,
            2,
            3,
            4,
            5
        };

        DataArray da = DataArray.create(data, 1, "numbers");
        myfield.addComponent(da);

        //create author names
        String[] names = new String[]{
            "Author Name 1",
            "Author Name 2",
            "Author Name 3",
            "Author Name 4",
            "Author Name 5"
        };

        DataArray da_names = DataArray.create(names, 1, "names");
        myfield.addComponent(da_names);

        //create coworks / segments
        int[] coworks = new int[]{
            1,
            1,
            2,
            1,
            5
        };
        DataArray da_coworks = DataArray.create(coworks, 1, "N coworks");
        cs.addComponent(da_coworks);        //adding not to the field, but to the CellSet

        System.out.println("HELLO WORLD" + ", Timestamp: " + new Timestamp(System.currentTimeMillis()));

        outField = myfield;
        outIrregularField = myfield;
        prepareOutputGeometry();        // do the visualization
        show();

        setOutputValue("TiborOutField", new VNIrregularField(outIrregularField));

    }
}
