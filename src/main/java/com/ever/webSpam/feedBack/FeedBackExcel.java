package com.ever.webSpam.feedBack;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.ever.webSpam.spam.Spam;

@Component
public class FeedBackExcel {

	// private String projectPath = System.getProperty("user.home");
	private int URI = 3;
	private int COMMENT = 23;

	Workbook workbook = null;
	Sheet sheet = null;

	List<Spam> spamList;

	public List<Spam> getSpamList(File file) {

		spamList = new ArrayList<>();
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			workbook = new XSSFWorkbook(inputStream);

			sheet = workbook.getSheetAt(0);

			updateColumn(sheet);

			Iterator<Row> rowItr = sheet.iterator();

			while (rowItr.hasNext()) {
				Row row = rowItr.next();
				if (row.getRowNum() < 3) {
					continue;
				}
				Spam spam = new Spam();
				Cell cell = row.getCell(URI);
				
				String cellValue = (String) getValueFromCell(cell);
				spam.setUri(cellValue);
				
				cell = row.getCell(COMMENT);
				cellValue = (String) getValueFromCell(cell);
				spam.setUri(cellValue);
				
				if (spam.getUri() != null && !spam.getUri().trim().isEmpty())
					spamList.add(spam);
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				workbook.close();
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return spamList;
	}

	private void updateColumn(Sheet sheet) {

		Row row = sheet.getRow(1);

		for (int i = 0; i < row.getLastCellNum(); i++) {
			if (row.getCell(i) != null) {
				String s = row.getCell(i).getStringCellValue();

				if(s.trim().toUpperCase().equals("URI")) {
					URI = i;
				} else if(s.trim().equals("코멘트2")) {
					COMMENT = i;
				}
			}
		}

	}

	// Utility method to get cell value based on cell type
	private Object getValueFromCell(Cell cell) {
		switch (cell.getCellTypeEnum()) {
		case STRING:
			return cell.getStringCellValue();
		case BOOLEAN:
			return cell.getBooleanCellValue();
		case NUMERIC:
			if (DateUtil.isCellDateFormatted(cell)) {
				return cell.getDateCellValue();
			} else {
				return String.valueOf((int) cell.getNumericCellValue());
			}
		case FORMULA:
			return cell.getCellFormula();
		case BLANK:
			return "";
		default:
			return "";
		}
	}

	public boolean writeSpam(File selectedFile, List<Spam> spamList) {

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

		CellStyle style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setWrapText(true);

		sheet = workbook.getSheetAt(0);

		Iterator<Row> rowIterator = sheet.iterator();
		// Iterate through rows
		for (Spam spam : spamList) {
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();

				if (row.getRowNum() < 4) {
					continue;
				}

				Cell cell = row.getCell(1);

				String url = cell.getStringCellValue();
				if (spam.getUri().trim().equals(url)) {
					cell = row.getCell(26);
					cell.setCellValue(spam.getQc());
					cell = row.getCell(27);
					cell.setCellValue(spam.getComment());
					cell.setCellStyle(style);
					cell.getRow().setHeight((short) -1);
					break;
				}
			}
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(selectedFile);
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
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
