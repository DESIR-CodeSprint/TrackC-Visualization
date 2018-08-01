package desir.babor;

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

public class MyModule extends OutFieldVisualizationModule
{
    
    public static InputEgg[] inputEggs = null;
    public static OutputEgg[] outputEggs = null;

    @Override
    public void onActive()
    {
        VNIrregularField in = (VNIrregularField) getInputFirstValue("MyInput");
        if(in == null)
            return;
        IrregularField inField = in.getField();
 
        //get number of nodes
        int input_nnodes = (int) inField.getNNodes();
        //get first data ccomponent on nodes
        DataArray input_dataarray0 = inField.getComponent(0);
        //get segment cells from first cellset
        CellArray input_segments0 = inField.getCellSet(0).getCellArray(CellType.SEGMENT);
        //get segments from cellarray
        int[] input_segments = input_segments0.getNodes();
        //get first component from cells
        DataArray input_cell_dataarray0 = inField.getCellSet(0).getComponent(0);
        //get coordinates
        FloatLargeArray input_coords_LA = inField.getCurrentCoords();
        
        //example to iterate ove large array
        for (long i = 0; i < input_coords_LA.length(); i++) {
            float v = input_coords_LA.get(i);
            input_coords_LA.set(i, v+1.0f);
        }
        
        //example of iterating on standard array
        float[] input_coords = input_coords_LA.getData();
        for (int i = 0; i < input_coords.length; i++) {
            input_coords[i] = input_coords[i]-1.0f; 
            
        }
        
        //here you can do something with input data and create output
        
        
        
        
        
        
        
        
        //create field with 5 nodes
        int nNodes = 5;
        IrregularField myfield = new IrregularField(nNodes);
        
        //create geometry for our 5 nodes
        float[] coords = new float[] {
            0.0f, 0.0f, 0.0f,
            0.0f,-1.0f, 0.0f,                        
           -2.0f,-2.0f, 0.0f,
           -1.0f,-2.0f, 0.0f,
            1.0f,-2.0f, 0.0f          
        };
        myfield.setCurrentCoords(new FloatLargeArray(coords));
       // myfield.setCurrentCoords((FloatLargeArray) LargeArrayUtils.generateRandom(LargeArrayType.FLOAT, 3 * nNodes));
        
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
        CellArray ca = new CellArray(CellType.SEGMENT, segments, null, dataIndices);
        CellSet cs = new CellSet("my cellset");
        cs.addCells(ca);
        myfield.addCellSet(cs);
        
        //create values
        int[] data = new int[] {
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
            "Author Name 1111",
            "Author Name 222",
            "Author Name 33",
            "Author Name 4",
            "Author Name 5"
        };
        DataArray da_names = DataArray.create(names, 1, "names");
        myfield.addComponent(da_names);
        
        
        int[] coworks = new int[] {
            1,
            1,
            2,
            1,
            5
        };
        DataArray da_coworks = DataArray.create(coworks, 1, "N coworks");
        cs.addComponent(da_coworks);
        

        
        
        outField = myfield;
        outIrregularField = myfield;
        prepareOutputGeometry();
        show();
        
        setOutputValue("MyOutput", new VNIrregularField(outIrregularField));   
    }
    
}
