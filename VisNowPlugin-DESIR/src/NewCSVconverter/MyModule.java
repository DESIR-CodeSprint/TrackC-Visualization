/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package NewCSVconverter;

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
 * @author paco
 */
public class MyModule extends OutFieldVisualizationModule
{

	public static InputEgg[] inputEggs = null;
	public static OutputEgg[] outputEggs = null;

	@Override
	public void onActive()
	{

		int nNodes = 34;
		IrregularField myfield = new IrregularField(nNodes);

// create geometry for our nodes

		
		myfield.setCurrentCoords((FloatLargeArray)LargeArrayUtils.generateRandom(LargeArrayType.FLOAT, 3 * nNodes));



//create segments
		int[] segments;
		segments = new int[] {
			1,2,
			1,3,
			1,4,
			1,5,
			1,6,
			1,7,
			1,8,
			1,9,
			1,10,
			1,11,
			2,4,
			2,5,
			2,6,
			2,7,
			2,8,
			2,9,
			2,10,
			2,11,
			12,4,
			12,13,
			12,14,
			15,16,
			15,5,
			15,6,
			15,7,
			15,17,
			15,10,
			3,5,
			3,9,
			18,4,
			18,5,
			18,6,
			18,7,
			18,19,
			18,20,
			18,13,
			18,10,
			21,22,
			21,4,
			21,5,
			21,6,
			21,7,
			21,10,
			21,23,
			24,25,
			24,4,
			24,5,
			24,6,
			24,8,
			24,26,
			24,17,
			24,27,
			24,28,
			24,29,
			24,10,
			24,14,
			24,11,
			22,4,
			22,5,
			22,6,
			22,7,
			22,10,
			22,23,
			30,5,
			30,31,
			25,4,
			25,5,
			25,6,
			25,8,
			25,26,
			25,17,
			25,27,
			25,28,
			25,29,
			25,10,
			25,14,
			25,11,
			16,5,
			16,6,
			16,7,
			16,17,
			16,10,
			32,4,
			32,5,
			32,8,
			32,9,
			32,10,
			4,5,
			4,6,
			4,7,
			4,8,
			4,26,
			4,19,
			4,9,
			4,17,
			4,20,
			4,13,
			4,27,
			4,28,
			4,29,
			4,10,
			4,14,
			4,23,
			4,11,
			5,6,
			5,7,
			5,8,
			5,26,
			5,19,
			5,9,
			5,17,
			5,20,
			5,13,
			5,27,
			5,28,
			5,29,
			5,10,
			5,14,
			5,23,
			5,31,
			5,11,
			6,7,
			6,8,
			6,26,
			6,19,
			6,9,
			6,33,
			6,17,
			6,20,
			6,13,
			6,27,
			6,28,
			6,29,
			6,10,
			6,14,
			6,23,
			6,11,
			7,8,
			7,19,
			7,9,
			7,33,
			7,17,
			7,20,
			7,13,
			7,10,
			7,23,
			7,11,
			8,26,
			8,9,
			8,17,
			8,27,
			8,28,
			8,29,
			8,10,
			8,34,
			8,14,
			8,11,
			26,17,
			26,27,
			26,28,
			26,29,
			26,10,
			26,14,
			19,20,
			19,13,
			19,10,
			9,10,
			9,11,
			33,10,
			17,27,
			17,28,
			17,29,
			17,10,
			17,34,
			17,14,
			20,13,
			20,10,
			13,10,
			13,14,
			27,28,
			27,29,
			27,10,
			27,14,
			28,29,
			28,10,
			28,14,
			29,10,
			29,14,
			10,14,
			10,23,
			10,11,
			14,11
};
			CellArray ca = new CellArray(CellType.SEGMENT, segments, null, null);
			CellSet cs = new CellSet("my cellset");
			cs.addCells(ca);
			myfield.addCellSet(cs);

//create values
			int[] data = new int[] {
				1,
				2,
				3,
				4,
				5,
				6,
				7,
				8,
				9, 
				10,
				11,
				12,
				13,
				14,
				15,
				16,
				17,
				18,
				19, 
				20,
				21,
				22,
				23,
				24,
				25,
				26,
				27,
				28,
				29, 
				30,
				31,
				32,
				33,
				34
			};

			DataArray da = DataArray.create(data, 1, "numbers");
			myfield.addComponent(da);

        //create author names
			String[] names = new String[]{
				"BERNARDO",
				"CORNELIUS",
				"FRANCISCO",
				"HAMLET",
				"HORATIO",
				"KING CLAUDIUS",
				"LAERTES",
				"LORD POLONIUS",
				"MARCELLUS",
				"QUEEN GERTRUDE",
				"VOLTIMAND",
				"Captain",
				"PRINCE FORTINBRAS",
				"ROSENCRANTZ",
				"Danes",
				"Gentleman",
				"OPHELIA",
				"First Ambassador",
				"Lord",
				"OSRIC",
				"First Clown",
				"First Priest",
				"Second Clown",
				"First Player",
				"GUILDENSTERN",
				"LUCIANUS",
				"Player King",
				"Player Queen",
				"Prologue",
				"First Sailor",
				"Servant",
				"Ghost",
				"Messenger",
				"REYNALDO"
			};
			DataArray da_names = DataArray.create(names, 1, "names");
			myfield.addComponent(da_names);


			System.out.println("HELLO WORLD!");

			outField = myfield;
			outIrregularField = myfield;
			prepareOutputGeometry();
			show();

			setOutputValue("MyOutput", new VNIrregularField(outIrregularField));
		}

	}
