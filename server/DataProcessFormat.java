import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class DataProcessFormat {
	private double[][] datas;

	public DataProcessFormat(String filename) {
		try {
			FileInputStream file = new FileInputStream(new File(filename));

			//Create Workbook instance holding reference to .xlsx file
			HSSFWorkbook workbook = new HSSFWorkbook(file);

			//Get first/desired sheet from the workbook
			HSSFSheet sheet = workbook.getSheetAt(0);

			//Iterate through each rows one by one
			Iterator<Row> rowIterator = sheet.iterator();
			
			List<Double[]> row_Array = new ArrayList<Double[]>();
			
			while (rowIterator.hasNext()) 
			{
				
				Row row = rowIterator.next();
				//For each row, iterate through all the columns
				Iterator<Cell> cellIterator = row.cellIterator();
				
				List<Double> row_data = new ArrayList<Double>();
				
				while (cellIterator.hasNext()) 
				{
					Cell cell = cellIterator.next();
					//Check the cell type and format accordingly
					switch (cell.getCellType()) 
					{

					case Cell.CELL_TYPE_NUMERIC:
						row_data.add(cell.getNumericCellValue());
						break;
					}
				}
				row_Array.add(row_data.toArray(new Double[row_data.size()]));
			}
			row_Array.remove(0);
			Double[][] convert_data = row_Array.toArray(new Double[row_Array.size()][row_Array.get(0).length]);
			this.convert(convert_data);
			
			file.close();

		} catch (Exception e) {
			System.out.println("File " + filename + " open Error!");
		}

	}

	private void prettyPrint(Double[][] raw_data) {
		int i = 0;
		for (Double[] row : raw_data) {
			if (i > 10) break;
			System.out.print("Row" + i + ": ");
			for (Double data : row) {
				System.out.print(data + "\t");
			}
			System.out.println();
			i++;
		}
		
	}
	
	private void convert(Double[][] raw_data) {
		this.datas = new double[raw_data[1].length][raw_data.length];
		for(int i = 0; i < raw_data.length; i++) {
			for (int j=0; j < raw_data[0].length; j++) {
				if (j == 0) this.datas[j][i] = raw_data[i][j]*1000000000;
				else this.datas[j][i] = raw_data[i][j]/10;
			}
		}
	}
	
	public double[][] getData(){
		return this.datas;
	}

}
