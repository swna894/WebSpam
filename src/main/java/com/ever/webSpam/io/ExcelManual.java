package com.ever.webSpam.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
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

import com.ever.webSpam.category.SpamCategory;
import com.ever.webSpam.manual.Manual;
import com.ever.webSpam.question.Question;
import com.ever.webSpam.spam.Spam;
import com.ever.webSpam.utility.Constant;

@Service
public class ExcelManual implements Constant {

	private Sheet sheet = null;
	public String FILE = QC_FILE + "/manual.xlsx";
	private static String SHEET_MANUAL = "manual";
	private static String SHEET_CETEGORY = "category";
	private static String SHEET_SPAM = "spam";
	private static String SHEET_QUESTION = "question";
	private CellStyle style;

	private Integer SELECTED = 0;
	private Integer NAME = 2;
	private Integer SCOPE = 3;
	private Integer NOTCHECK = 4;
	private Integer DEFER = 5;

	private Integer URI = 1;
	private Integer LOOK_MAIN = 6;
	private Integer LOOK_CH = 7;
	private Integer LOOK_LIST = 8;
	private Integer LOOK_CONT = 9;
	private Integer HAM = 10;
	private Integer HAM_LOW = 11;
	private Integer SPAM_AD = 12;
	private Integer SPAM_TEXT = 13;
	private Integer SPAM_REDIR = 14;
	private Integer SPAM_MALWARE = 15;
	private Integer SPAM_COPY = 16;
	private Integer SPAM_PORN = 17;
	private Integer SPAM_DECEP = 18;
	private Integer SPAM_MAINIP = 19;
	private Integer SPAM_ILLEGAL = 20;
	private Integer DATE = 21;
	private Integer COMMENT = 22;
	private Integer SPAM_WHITE = 23;

//	private  Integer booleanBlock = 9;

	public ExcelManual() {
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

	public boolean writeSpamList(List<Spam> spamList) {
		Workbook workbook = readFile(FILE);
		initalStyle(workbook);
		sheet = workbook.getSheet(SHEET_SPAM);

		clearSteet(sheet);

		for (int i = 0; i < spamList.size(); i++) {
			Spam category = spamList.get(i);

			Row row = sheet.createRow(i);

			Cell cell = row.createCell(URI);
			cell.setCellValue(category.getUri());

			cell = row.createCell(SELECTED);
			cell.setCellValue(category.getSelected());

			cell = row.createCell(NAME);
			cell.setCellValue(category.getName());

			cell = row.createCell(SCOPE);
			cell.setCellValue(category.getScope());

			cell = row.createCell(NOTCHECK);
			cell.setCellValue(category.getNotCheck());

			cell = row.createCell(DEFER);
			String defer = category.getDefer() == null ? "" : category.getDefer();
			cell.setCellValue(defer);

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
			cell.setCellValue(category.getWorkday());

			cell = row.createCell(COMMENT);
			cell.setCellValue(category.getComment());

			cell = row.createCell(SPAM_WHITE);
			cell.setCellValue(category.getSpamWhite());

			cell.getRow().setHeight((short) -1);
		}
		return writeFile(workbook, FILE);
	}

	public boolean writeCategoryList(List<SpamCategory> categoryList) {
		Workbook workbook = readFile(FILE);
		initalStyle(workbook);
		sheet = workbook.getSheet(SHEET_CETEGORY);

		clearSteet(sheet);

		for (int i = 0; i < categoryList.size(); i++) {
			SpamCategory category = categoryList.get(i);

			Row row = sheet.createRow(i);

			Cell cell = row.createCell(URI);
			cell.setCellValue(category.getUri());

			cell = row.createCell(0);
			cell.setCellValue(category.getId());

			cell = row.createCell(LOOK_MAIN);
			cell.setCellValue(category.getLookMain());

			cell = row.createCell(LOOK_CH);
			cell.setCellValue(category.getLookCh());

			cell = row.createCell(LOOK_LIST);
			cell.setCellValue(category.getLookList());

			cell = row.createCell(HAM);
			cell.setCellValue(category.getHam());

			cell = row.createCell(HAM_LOW);
			cell.setCellValue(category.getHamLow());

			cell = row.createCell(SPAM_AD);
			cell.setCellValue(category.getSpamAd());

			cell = row.createCell(SPAM_TEXT);
			cell.setCellValue(category.getSpamText());

			cell = row.createCell(SPAM_COPY);
			cell.setCellValue(category.getSpamCopy());

			cell = row.createCell(SPAM_ILLEGAL);
			cell.setCellValue(category.getSpamIllegal());

			cell = row.createCell(SPAM_WHITE);
			cell.setCellValue(category.getWhiteSite());

			cell.getRow().setHeight((short) -1);
		}
		return writeFile(workbook, FILE);
	}

	public boolean writeManualList(List<Manual> manualList) {
		Workbook workbook = readFile(FILE);
		initalStyle(workbook);
		sheet = workbook.getSheet(SHEET_MANUAL);

		clearSteet(sheet);

		for (int i = 0; i < manualList.size(); i++) {
			Row row = sheet.createRow(i);
			Cell cell = row.createCell(1);
			cell.setCellValue(manualList.get(i).getDoc());

			cell = row.createCell(0);
			cell.setCellValue(manualList.get(i).getId());
			cell.getRow().setHeight((short) -1);
		}
		return writeFile(workbook, FILE);
	}

	public boolean writeQuestionList(List<Question> qList) {
		Workbook workbook = readFile(EXCEL_FILE);
		initalStyle(workbook);
		sheet = workbook.getSheet(SHEET_QUESTION);

		clearSteet(sheet);

		for (int i = 0; i < qList.size(); i++) {
			Row row = sheet.createRow(i);

			Cell cell = row.createCell(0);
			cell.setCellValue(qList.get(i).getUri());

			cell = row.createCell(1);
			cell.setCellValue(qList.get(i).getDate());

			cell = row.createCell(2);
			cell.setCellValue(qList.get(i).getMemo());

			cell.getRow().setHeight((short) -1);
		}
		return writeFile(workbook, EXCEL_FILE);
	}

	public List<Question> readQuestionList() {
		List<Question> list = new ArrayList<Question>();
		Workbook workbook = readFile(EXCEL_FILE);
		initalStyle(workbook);
		sheet = workbook.getSheet(SHEET_QUESTION);

		for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
			Question question = new Question();
			Row row = sheet.getRow(i);
			if (row != null) {
				Cell cell = row.getCell(0);
				String uri = cell.getStringCellValue();
				question.setUri(uri);

				cell = row.getCell(1);
				String date = cell.getStringCellValue();
				question.setDate(date);

				cell = row.getCell(2);
				String memo = cell.getStringCellValue();
				question.setMemo(memo);

				list.add(question);
			}
		}

		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<Manual> readManualList() {
		List<Manual> manualList = new ArrayList<Manual>();
		Workbook workbook = readFile(FILE);
		initalStyle(workbook);
		sheet = workbook.getSheet(SHEET_MANUAL);

		for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
			Manual manual = new Manual();
			Row row = sheet.getRow(i);
			Cell cell = row.getCell(0);
			Long id = (long) cell.getNumericCellValue();
			manual.setId(id);
			cell = row.getCell(1);
			String doc = cell.getStringCellValue();
			manual.setDoc(doc);
			manualList.add(manual);
		}

		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return manualList;
	}

	public List<SpamCategory> readCategoryList() {
		List<SpamCategory> categoryList = new ArrayList<SpamCategory>();
		Workbook workbook = readFile(FILE);
		initalStyle(workbook);
		sheet = workbook.getSheet(SHEET_CETEGORY);

		for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
			SpamCategory category = new SpamCategory();

			Row row = sheet.getRow(i);

			Cell cell = row.getCell(URI);
			String uri = cell.getStringCellValue();
			category.setUri(uri);

			cell = row.getCell(0);
			category.setId((long) cell.getNumericCellValue());

			cell = row.getCell(LOOK_MAIN);
			Boolean b = cell.getBooleanCellValue();
			category.setLookMain(b);

			cell = row.getCell(LOOK_CH);
			b = cell.getBooleanCellValue();
			category.setLookCh(b);

			cell = row.getCell(LOOK_LIST);
			b = cell.getBooleanCellValue();
			category.setLookList(b);

			cell = row.getCell(HAM);
			b = cell.getBooleanCellValue();
			category.setHam(b);

			cell = row.getCell(HAM_LOW);
			b = cell.getBooleanCellValue();
			category.setHamLow(b);

			cell = row.getCell(SPAM_AD);
			b = cell.getBooleanCellValue();
			category.setSpamAd(b);

			cell = row.getCell(SPAM_TEXT);
			b = cell.getBooleanCellValue();
			category.setSpamText(b);

			cell = row.getCell(SPAM_COPY);
			b = cell.getBooleanCellValue();
			category.setSpamCopy(b);

			cell = row.getCell(SPAM_ILLEGAL);
			b = cell.getBooleanCellValue();
			category.setSpamIllegal(b);

			cell = row.getCell(SPAM_WHITE);
			if (cell != null) {
				b = cell.getBooleanCellValue();
				category.setWhiteSite(b);
			}

			categoryList.add(category);
		}
		try {
			workbook.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return categoryList;
	}

	public List<Spam> readSpamList() {
		List<Spam> spamList = new ArrayList<Spam>();
		Workbook workbook = readFile(FILE);

		sheet = workbook.getSheet(SHEET_SPAM);

		for (int i = 0; i < sheet.getLastRowNum() + 1; i++) {
			Spam spam = new Spam();
			Row row = sheet.getRow(i);

			spam.setNo(i + 1);

			Cell cell = row.getCell(URI);
			spam.setUri(cell.getStringCellValue());

			cell = row.getCell(SELECTED);
			spam.setSelected(cell.getBooleanCellValue());

			cell = row.getCell(NAME);
			spam.setName(cell.getStringCellValue());

			cell = row.getCell(SCOPE);
			spam.setScope(cell.getStringCellValue());

			cell = row.getCell(NOTCHECK);
			spam.setNotCheck(cell.getStringCellValue());

			cell = row.getCell(DEFER);
			spam.setDefer(cell.getStringCellValue());

			cell = row.getCell(LOOK_MAIN);
			spam.setLookMain(cell.getBooleanCellValue());

			cell = row.getCell(LOOK_CH);
			spam.setLookCh(cell.getBooleanCellValue());

			cell = row.getCell(LOOK_LIST);
			spam.setLookList(cell.getBooleanCellValue());

			cell = row.getCell(LOOK_CONT);
			spam.setLookCont(cell.getBooleanCellValue());

			cell = row.getCell(HAM);
			spam.setHam(cell.getBooleanCellValue());

			cell = row.getCell(HAM_LOW);
			spam.setHamLow(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_AD);
			spam.setSpamAd(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_TEXT);
			spam.setSpamText(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_REDIR);
			spam.setSpamRedir(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_MALWARE);
			spam.setSpamMalware(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_DECEP);
			spam.setSpamDecep(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_PORN);
			spam.setSpamPorn(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_COPY);
			spam.setSpamCopy(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_MAINIP);
			spam.setSpamManip(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_ILLEGAL);
			spam.setSpamIllegal(cell.getBooleanCellValue());

			cell = row.getCell(SPAM_WHITE);
			spam.setSpamWhite(cell.getBooleanCellValue());

			cell = row.getCell(DATE);
			spam.setWorkday(cell.getStringCellValue());

			cell = row.getCell(COMMENT);
			spam.setComment(cell.getStringCellValue());
			spamList.add(spam);
		}

		return spamList;
	}

	private void clearSteet(Sheet sheet) {
		for (int i = sheet.getLastRowNum(); i >= 0; i--) {
			Row row = sheet.getRow(i);
			if (row != null)
				sheet.removeRow(sheet.getRow(i));
		}
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
