package com.ever.webSpam.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.ever.webSpam.spam.Spam;
import com.ever.webSpam.utility.Constant;

@Service
public class ExcelWhiteQc implements Constant {

	private Sheet sheet = null;
	public String FILE = "d:/웹스팸/필터링파일.xlsx";
	private CellStyle style;

	//private Integer SELECTED = 0;


	//private Integer NOTCHECK = 4;
	//private Integer DEFER = 5;

	private Integer URI = 1;
	private Integer SCOPE = 2;
	private Integer DATE = 7;
	private Integer NAME = 8;
	private Integer EMAIL = 9; 	
	private Integer LOOK_MAIN = 10;
	private Integer LOOK_CH = 11;
	private Integer LOOK_LIST = 12;
	private Integer LOOK_CONT = 13;
	private Integer HAM = 14;
	private Integer HAM_LOW = 15;
	private Integer SPAM_AD = 16;
	private Integer SPAM_TEXT = 17;
	private Integer SPAM_REDIR = 18;
	private Integer SPAM_MALWARE = 19;
	private Integer SPAM_COPY = 20;
	private Integer SPAM_PORN = 21;
	private Integer SPAM_DECEP = 22;
	private Integer SPAM_MAINIP = 23;
	private Integer SPAM_ILLEGAL = 24;

	//private Integer COMMENT = 22;
	//private Integer SPAM_WHITE = 23;

//	private  Integer booleanBlock = 9;

	public ExcelWhiteQc() {
	}

	private void initalStyle(Workbook workbook) {
		style = workbook.createCellStyle();
		style.setBorderBottom(BorderStyle.THIN);
		style.setBottomBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderLeft(BorderStyle.THIN);
		style.setLeftBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderRight(BorderStyle.THIN);
		style.setRightBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setBorderTop(BorderStyle.THIN);
		style.setTopBorderColor(IndexedColors.GREY_50_PERCENT.getIndex());
		style.setWrapText(true);
	}

	public String writeSpamList(List<Spam> spamList) {
		Workbook workbook = readFile(FILE);
		initalStyle(workbook);
		sheet = workbook.getSheetAt(0);

		for (int i = 0; i < spamList.size(); i++) {
			Spam category = spamList.get(i);

			Row row = sheet.createRow(i + 3);
			
			Cell cell = row.createCell(URI);
			cell.setCellValue(category.getUri());

			cell = row.createCell(0);
			cell.setCellValue(category.getComment());
			
			cell = row.createCell(3);
			cell.setCellValue(category.getRequestTime());
			
			cell = row.createCell(4);
			cell.setCellValue(false);
			
			cell = row.createCell(5);
			cell.setCellValue(false);
			
			cell = row.createCell(6);
			cell.setCellValue(true);
			
			cell = row.createCell(EMAIL);
			cell.setCellValue(category.getEmail());

			cell = row.createCell(NAME);
			cell.setCellValue(category.getName());

			cell = row.createCell(SCOPE);
			cell.setCellValue(category.getScope());

		
//			cell = row.createCell(NOTCHECK);
//			cell.setCellValue(category.getNotCheck());
//
//			cell = row.createCell(DEFER);
//			String defer = category.getDefer() == null ? "" : category.getDefer();
//			cell.setCellValue(defer);

			cell = row.createCell(LOOK_MAIN);
			cell.setCellValue(category.getLookMain());

			cell = row.createCell(LOOK_CH);
			cell.setCellValue(category.getLookCh());

			cell = row.createCell(LOOK_LIST);
			cell.setCellValue(category.getLookList());

			cell = row.createCell(LOOK_CONT);
			cell.setCellValue(category.getLookCont());

			cell = row.createCell(HAM);
			cell.setCellValue(category.getHam());

			cell = row.createCell(HAM_LOW);
			cell.setCellValue(category.getHamLow());

			cell = row.createCell(SPAM_AD);
			cell.setCellValue(category.getSpamAd());

			cell = row.createCell(SPAM_TEXT);
			cell.setCellValue(category.getSpamText());

			cell = row.createCell(SPAM_REDIR);
			cell.setCellValue(category.getSpamRedir());

			cell = row.createCell(SPAM_MALWARE);
			cell.setCellValue(category.getSpamMalware());

			cell = row.createCell(SPAM_DECEP);
			cell.setCellValue(category.getSpamDecep());

			cell = row.createCell(SPAM_PORN);
			cell.setCellValue(category.getSpamPorn());

			cell = row.createCell(SPAM_COPY);
			cell.setCellValue(category.getSpamCopy());

			cell = row.createCell(SPAM_MAINIP);
			cell.setCellValue(category.getSpamManip());

			cell = row.createCell(SPAM_ILLEGAL);
			cell.setCellValue(category.getSpamIllegal());

			cell = row.createCell(DATE);
			cell.setCellValue(category.getTimeOfInspection());


			cell.getRow().setHeight((short) -1);
		}
		LocalDate localDate = LocalDate.now();
		String prefix = localDate.format(DateTimeFormatter.ofPattern("MM월dd일"));
		String file = "d:/웹스팸/" + prefix + "_필터링파일_나상운.xlsx"; 
		 writeFile(workbook, file);
		 
		 return file;
	}


	private Workbook readFile(String file) {
		Workbook workbook = null;
		if (new File(FILE).exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(file);
				workbook = new XSSFWorkbook(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			workbook = new XSSFWorkbook();
		}

		return workbook;
	}

	private Boolean writeFile(Workbook workbook, String file) {
		
		try {
			FileOutputStream outputStream = new FileOutputStream(file);
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
