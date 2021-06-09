package com.ever.webSpam.excel;

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
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
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
	public String FILE = "d:/웹스팸/웹호스팅 예시 파일.xlsx";
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
	private Integer HAM_FISH = 16;
	private Integer SPAM_AD = 17;
	private Integer SPAM_TEXT = 18;
	private Integer SPAM_REDIR = 19;
	private Integer SPAM_MALWARE = 20;
	private Integer SPAM_COPY = 21;
	private Integer SPAM_PORN = 22;
	private Integer SPAM_DECEPWEAK = 23;
	private Integer SPAM_DECEP = 24;
	private Integer SPAM_MAINIP = 25;
	private Integer SPAM_ILLEGAL = 26;

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
		//style.setWrapText(true);
	}
	
	LocalDate localDate = LocalDate.now();
	String prefix = localDate.format(DateTimeFormatter.ofPattern("MM월dd일"));
	String file = "d:/웹스팸/" + prefix + "_필터링파일_나상운.xlsx"; 
	int stageTwo = 0;
	int stageThree = 0;
	int startRow = 4;
	
	public String writeSpamList(List<Spam> spamList, String sheetName) {	
		Workbook workbook;
		if(!new File(file).exists())  {
			 workbook = readFile(FILE);
		} else {
			 workbook = readFile(file);
		}
		CellStyle cs = workbook.createCellStyle();
		
		if(sheetName.equals("1단계 사이트")) {
			stageTwo = startRow + spamList.size() + 1;
			startRow = 4;
		} else if(sheetName.equals("2단계 사이트")){
			stageThree = stageTwo + spamList.size() + 1;
			startRow = stageTwo;
		} else if(sheetName.equals("3단계 사이트")){
			startRow = stageThree;
		}
			
		initalStyle(workbook);
		sheet = workbook.getSheetAt(0);
		for (int i = 0; i < spamList.size(); i++) {
			
			Spam category = spamList.get(i);
			Row row = sheet.getRow(i + startRow);
			
			Cell cell = row.createCell(URI);
			cell.setCellValue(category.getUri());
			cell.setCellStyle(style);

			if(i == 0) {
				cell = row.createCell(0);
				if(sheetName.equals("1단계 사이트")) {			
					cs.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cell.setCellValue("1순위");
					cell.setCellStyle(cs);
				} else if(sheetName.equals("2단계 사이트")){
					cs.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cell.setCellValue("2순위-서메");
					cell.setCellStyle(cs);
				} else if(sheetName.equals("3단계 사이트")){
					cs.setFillForegroundColor(IndexedColors.GOLD.getIndex());
					cs.setFillPattern(FillPatternType.SOLID_FOREGROUND);
					cell.setCellValue("3순위-컨텐");
					cell.setCellStyle(cs);
				}
			}
			
			cell = row.createCell(45);
			cell.setCellValue(category.getComment());
			cell.setCellStyle(style);
			
			cell = row.createCell(3);
			cell.setCellValue(category.getRequestTime());
			cell.setCellStyle(style);
			
			cell = row.createCell(4);
			cell.setCellValue(false);
			cell.setCellStyle(style);
			
			cell = row.createCell(5);
			cell.setCellValue(false);
			cell.setCellStyle(style);
			
			cell = row.createCell(6);
			cell.setCellValue(true);
			cell.setCellStyle(style);
			
			cell = row.createCell(EMAIL);
			cell.setCellValue(category.getEmail());
			cell.setCellStyle(style);
			
			cell = row.createCell(NAME);
			cell.setCellValue(category.getName());
			cell.setCellStyle(style);
			
			cell = row.createCell(SCOPE);
			cell.setCellValue(category.getScope());
			cell.setCellStyle(style);
			
			cell = row.createCell(LOOK_MAIN);
			cell.setCellValue(category.getLookMain());
			cell.setCellStyle(style);
			
			cell = row.createCell(LOOK_CH);
			cell.setCellValue(category.getLookCh());
			cell.setCellStyle(style);
			
			cell = row.createCell(LOOK_LIST);
			cell.setCellValue(category.getLookList());
			cell.setCellStyle(style);
			
			cell = row.createCell(LOOK_CONT);
			cell.setCellValue(category.getLookCont());
			cell.setCellStyle(style);
			
			cell = row.createCell(HAM);
			cell.setCellValue(category.getHam());
			cell.setCellStyle(style);
			
			cell = row.createCell(HAM_LOW);
			cell.setCellValue(category.getHamLow());
			cell.setCellStyle(style);
			
			cell = row.createCell(HAM_FISH);
			cell.setCellValue(category.getHamFish());
			cell.setCellStyle(style);
			
			cell = row.createCell(SPAM_AD);
			cell.setCellValue(category.getSpamAd());
			cell.setCellStyle(style);
			
			cell = row.createCell(SPAM_TEXT);
			cell.setCellValue(category.getSpamText());
			cell.setCellStyle(style);
			
			cell = row.createCell(SPAM_REDIR);
			cell.setCellValue(category.getSpamRedir());
			cell.setCellStyle(style);
			
			cell = row.createCell(SPAM_MALWARE);
			cell.setCellValue(category.getSpamMalware());
			cell.setCellStyle(style);
			
			cell = row.createCell(SPAM_DECEP);
			cell.setCellValue(category.getSpamDecep());
			cell.setCellStyle(style);
			
			cell = row.createCell(SPAM_PORN);
			cell.setCellValue(category.getSpamPorn());
			cell.setCellStyle(style);
			
			cell = row.createCell(SPAM_DECEPWEAK);
			cell.setCellValue(category.getSpamPorn());
			cell.setCellStyle(style); 
			
			cell = row.createCell(SPAM_COPY);
			cell.setCellValue(category.getSpamCopy());
			cell.setCellStyle(style);
			
			cell = row.createCell(SPAM_MAINIP);
			cell.setCellValue(category.getSpamManip());
			cell.setCellStyle(style);
			
			cell = row.createCell(SPAM_ILLEGAL);
			cell.setCellValue(category.getSpamIllegal());
			cell.setCellStyle(style);
			
			cell = row.createCell(DATE);
			cell.setCellValue(category.getTimeOfInspection());
			cell.setCellStyle(style);
			

			cell.getRow().setHeight((short) -1);
		}
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
			FormulaEvaluator mainWorkbookEvaluator = workbook.getCreationHelper().createFormulaEvaluator();
			mainWorkbookEvaluator.evaluateAll();
			
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
