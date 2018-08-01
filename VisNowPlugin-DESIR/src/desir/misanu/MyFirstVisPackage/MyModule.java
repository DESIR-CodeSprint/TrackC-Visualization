/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desir.misanu.MyFirstVisPackage;
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
 * @author milica
 */
public class MyModule extends OutFieldVisualizationModule {
    
    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;

    @Override
    public void onActive()
    {
        int nNodes = 5;
        IrregularField myField = new IrregularField(nNodes);
        
        // create geometry for our 5 nodes
        float[] coords = new float[] {
            0.0f, 0.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            -2.0f, -2.0f, 0.0f,
            -1.0f, -2.0f, 0.0f,
            1.0f, -2.0f, 0.0f
        };
        myField.setCurrentCoords(new FloatLargeArray(coords));
        
        // create segments
        int[] segments = new int[] {
            0, 1,
            1, 0,
            1, 3,
            1, 4,
            2, 3,
            3, 4
        };
        
        int[] dataIndices = new int[] {0, 1, 2, 3, 4, 5};
        
        CellArray ca = new CellArray(CellType.SEGMENT, segments, null, dataIndices);
        CellSet cs = new CellSet("all segments");
        cs.addCells(ca);
        myField.addCellSet(cs);
        
//        // irregular fields cannot work wo any data in nodes. Let's add some data
//        int[] data = new int[]{
//            1, 2, 3, 4, 5
//        };
//        DataArray da = DataArray.create(data, 1, "numbers");
//        myField.addComponent(da);
        
        // create author names
        String[] names = new String[] {
            "Mirko",
            "Petar",
            "Djordje",
            "Ana",
            "Vid"
        };
        
        DataArray da_names = DataArray.create(names, 1, "author names");
        myField.addComponent(da_names);
        
        int[] citations = new int[] {
            1,
            5,
            1,
            2,
            1,
            5
        };
        
        DataArray da_coworks = DataArray.create(citations, 1, "number of citations");
        cs.addComponent(da_coworks);
        
        System.out.println("Hi");
        
        outField = myField;
        outIrregularField = myField;
        prepareOutputGeometry();
        show();
        
        setOutputValue("outField", new VNIrregularField(outIrregularField));
        
    }
    
}
