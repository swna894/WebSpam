package com.webspam.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ApacheNJK {

	//private String projectPath = System.getProperty("user.home");

	Workbook workbook = null;
    Sheet sheet = null;
	
	public ApacheNJK() {
	}

	public List<TargetNJK> getTargetList(File selectedFile) {
		List<TargetNJK> targetList = null;
		FileInputStream inputStream;
		try {
			  inputStream = new FileInputStream(selectedFile);
			  workbook = new XSSFWorkbook(inputStream);
		      sheet = workbook.getSheetAt(0);
		      
		      targetList = readSheet(sheet); 
		      
		      workbook.close();
		      inputStream.close();
		      
		} catch (IOException e) {
			e.printStackTrace();
		}  
		return targetList;
	}
	
	

	@SuppressWarnings("unused")
	private List<TargetNJK> readSheet(Sheet sheet) {
		   Iterator<Row> rowItr = sheet.iterator();
	       List<TargetNJK> targetList = new ArrayList<>();
	        // Iterate through rows
	        while(rowItr.hasNext()) {
	        	TargetNJK target = new TargetNJK();
	            Row row = rowItr.next();
	            // skip header (First row)
	            if(row.getRowNum() < 9) {
	                continue;
	            }
	            Iterator<Cell> cellItr = row.cellIterator();
	            // Iterate each cell in a row
	            while(cellItr.hasNext()) {
	                
	                Cell cell = cellItr.next();
	                int index = cell.getColumnIndex();
	                switch(index) {
	                case 2:
	                	target.setTempleteId((String)getValueFromCell(cell));
	                    break;
	                case 3:
	                	target.setTask((String)getValueFromCell(cell));
	                    break;
	                case 4:
	                	target.setUrl((String)getValueFromCell(cell));
	                    break;
	                }
	            }
	            if(target == null) break;
	            targetList.add(target);
	        }
	        
	        return targetList;
	}

	// Utility method to get cell value based on cell type
    private Object getValueFromCell(Cell cell) {
        switch(cell.getCellTypeEnum()) {
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case NUMERIC:
            	 if (DateUtil.isCellDateFormatted(cell)) {
            		 return cell.getDateCellValue();
                 } else {
                	 return  String.valueOf((int)cell.getNumericCellValue());
                 }
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";                                
        }
    }


	public boolean writeTarget(File selectedFile, TargetNJK targetNjk) {

		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;

		if (selectedFile.exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(selectedFile);
				workbook = new XSSFWorkbook(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			workbook = new XSSFWorkbook();
		}

		CellStyle cs = workbook.createCellStyle();  
        cs.setWrapText(true);  
      
        
		sheet = workbook.getSheetAt(0);

		Iterator<Row> rowIterator = sheet.iterator();
	        // Iterate through rows
	    while(rowIterator.hasNext()) {
	    	 Row row = rowIterator.next();    	 
	    	 Cell cell = row.getCell(4);
	    	 if(cell == null) continue;
	    	 String url = cell.getStringCellValue();
	    	 if(targetNjk.getUrl().trim().equals(url)) {
	    		 cell = row.getCell(5);
	    		 cell.setCellValue(targetNjk.getQuality());
	    		 cell = row.getCell(6);
	    		 cell.setCellValue(targetNjk.getPurpose());
	    		 cell.setCellStyle(cs);
	    		 cell = row.getCell(7);
	    		 cell.setCellValue(targetNjk.getTypeComment());
	    		 cell.setCellStyle(cs);
	    		 cell = row.getCell(8);
	    		 cell.setCellValue(targetNjk.getNote());
	    		 cell = row.getCell(9);
	    		 cell.setCellStyle(cs);
	    		 cell.setCellValue(targetNjk.getContents());
	    		 break;
	    	 }
	    }


		try {
			FileOutputStream outputStream = new FileOutputStream(selectedFile);
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("엑셀파일을 사용 중이기때문에 엑세스할 수 없습니다!");

			alert.showAndWait();
			e.printStackTrace();
			return false;
		}

		return true;
	}


	@SuppressWarnings("unused")
	private String getFileName(File selectedFile) {
		String[] array = selectedFile.getName().split("_");
		return array[1] + "_" + array[0] + ".xlsx";
	}

	public static void main(String[] args) {
	}




}
