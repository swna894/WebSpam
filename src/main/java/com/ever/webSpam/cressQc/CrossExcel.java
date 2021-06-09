package com.ever.webSpam.cressQc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ever.webSpam.spam.Spam;
import com.ever.webSpam.spam.SpamController;
import com.ever.webSpam.utility.Constant;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

@Component
public class CrossExcel implements Constant {

	// private String projectPath = System.getProperty("user.home");

	@Autowired
	SpamController spamController;

	Workbook workbook = null;
	Sheet sheet = null;

	List<Spam> spamList;
	private String path;
	
	public List<Spam> getSpamList(File file) {
		path = spamController.getPath();

		spamList = new ArrayList<>();
		FileInputStream inputStream = null;
		String s;
		try {
			inputStream = new FileInputStream(file);
			workbook = new XSSFWorkbook(inputStream);

			sheet = workbook.getSheetAt(0);

			Iterator<Row> rowItr = sheet.iterator();

			while (rowItr.hasNext()) {
				Row row = rowItr.next();
				if (row.getRowNum() < 4) {
					continue;
				}
				Spam spam = new Spam();
				Iterator<Cell> cellItr = row.cellIterator();
				while (cellItr.hasNext()) {

					Cell cell = cellItr.next();
					int index = cell.getColumnIndex();

					switch (index) {
					case 1:
						s = (String) getValueFromCell(cell);
						if (s == null || s.equals("")) {
							continue;
						}
						spam.setUri(s);
						break;
					case 2:
						s = (String) getValueFromCell(cell);
						spam.setScope(s);
						break;
					case 3:
						s = (String) getValueFromCell(cell);
						spam.setRequestTime(s);
						break;

					case 4:
						Object b = getValueFromCell(cell);
						if (b instanceof Boolean) {
							spam.setBooleanNotCheck((boolean) b);
							spam.setNotCheck((boolean) b ? "검수불가" : "");
						} else {
							spam.setNotCheck("");
						}
						break;
					case 5:
						b = getValueFromCell(cell);
						if (b instanceof Boolean) {
							spam.setBooleanDefer((boolean) b);
							spam.setDefer((boolean) b ? "보류" : "");
						} else {
							spam.setDefer("");
						}
						break;
					case 6:
						b = getValueFromCell(cell);
						if (b instanceof Boolean) {
							spam.setBooleanBlock((boolean) b);
						}
						break;
					case 7:
						s = (String) getValueFromCell(cell);
						spam.setTimeOfInspection(s);
						break;
					case 8:
						s = (String) getValueFromCell(cell);
						spam.setName(s);
						break;
					case 9:
						s = (String) getValueFromCell(cell);
						spam.setEmail(s);
						break;
					case 10:
						if (cell.getBooleanCellValue()) { // b
							spam.setLookMain(true);
						}
						break;
					case 11:
						if (cell.getBooleanCellValue()) {
							spam.setLookCh(true);
						}
						break;
					case 12:
						if (cell.getBooleanCellValue()) {
							spam.setLookList(true);
						}
						break;
					case 13:
						if (cell.getBooleanCellValue()) {
							spam.setLookCont(true);
						}
						break;
					case 14:
						if (cell.getBooleanCellValue()) {
							spam.setHam(true);
						}
						break;
					case 15:
						if (cell.getBooleanCellValue()) {
							spam.setHamLow(true);
						}
						break;
					case 16:
						if (cell.getBooleanCellValue()) {
							spam.setHamFish(true);
						}
						break;
					case 17:
						if (cell.getBooleanCellValue()) {
							spam.setSpamAd(true);
						}
						break;
					case 18:
						if (cell.getBooleanCellValue()) {
							spam.setSpamText(true);
						}
						break;
					case 19:
						if (cell.getBooleanCellValue()) {
							spam.setSpamRedir(true);
						}
						break;
					case 20:
						if (cell.getBooleanCellValue()) {
							spam.setSpamMalware(true);
						}
						break;
					case 21:
						if (cell.getBooleanCellValue()) {
							spam.setSpamCopy(true);
						}
						break;
					case 22:
						if (cell.getBooleanCellValue()) {
							spam.setSpamPorn(true);
						}
						break;
					case 23:
						if (cell.getBooleanCellValue()) {
							spam.setSpamPornWeak(true);
						}
						break;
					case 24:
						if (cell.getBooleanCellValue()) {
							spam.setSpamDecep(true);
						}
						break;
					case 25:
						if (cell.getBooleanCellValue()) {
							spam.setSpamManip(true);
						}
						break;
					case 26:
						if (cell.getBooleanCellValue()) {
							spam.setSpamIllegal(true);
						}
						break;
					case 45:
						spam.setQc((String) getValueFromCell(cell));
						break;
					case 46:
						spam.setComment((String) getValueFromCell(cell));
					default:
					}
				}
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

	public boolean writeSpam(File selectedFile, Spam spam) {

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
		while (rowIterator.hasNext()) {
			Row row = rowIterator.next();

			if (row.getRowNum() < 4) {
				continue;
			}

			Cell cell = row.getCell(1);

			String url = cell.getStringCellValue();
			if (spam.getUri().trim().equals(url)) {
				cell = row.getCell(10);
				cell.setCellValue(spam.getLookMain());
				cell = row.getCell(11);
				cell.setCellValue(spam.getLookCh());
				cell = row.getCell(12);
				cell.setCellValue(spam.getLookList());
				cell = row.getCell(13);
				cell.setCellValue(spam.getLookCont());
				cell = row.getCell(14);
				cell.setCellValue(spam.getHam());
				cell = row.getCell(15);
				cell.setCellValue(spam.getHamLow());
				cell = row.getCell(16);
				cell.setCellValue(spam.getSpamAd());
				cell = row.getCell(17);
				cell.setCellValue(spam.getSpamText());
				cell = row.getCell(18);
				cell.setCellValue(spam.getSpamRedir());
				cell = row.getCell(19);
				cell.setCellValue(spam.getSpamMalware());
				cell = row.getCell(20);
				cell.setCellValue(spam.getSpamCopy());
				cell = row.getCell(21);
				cell.setCellValue(spam.getSpamPorn());
				cell = row.getCell(22);
				cell.setCellValue(spam.getSpamPornWeak());
				cell = row.getCell(23);
				cell.setCellValue(spam.getSpamDecep());
				cell = row.getCell(24);
				cell.setCellValue(spam.getSpamManip());
				cell = row.getCell(25);
				cell.setCellValue(spam.getSpamIllegal());
				cell = row.getCell(43);
				cell.setCellValue(spam.getQc());
				cell = row.getCell(44);
				cell.setCellValue(spam.getComment());
				cell.setCellStyle(style);
				cell.getRow().setHeight((short) -1);

				for (int i = 27; i < 42; i++) {
					cell = row.getCell(i);
					cell.setCellFormula(cell.getCellFormula());
				}
				break;
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

	public boolean writeSpam(File selectedFile, List<Spam> spamList) {

		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;

		FileInputStream fis;
		try {
			if (selectedFile.exists()) {
				fis = new FileInputStream(selectedFile);
				workbook = new XSSFWorkbook(fis);
			} else {
				fis = new FileInputStream(new File(QC_FILE + "/크로스qc.xlsx"));
				workbook = new XSSFWorkbook(fis);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		workbook.setForceFormulaRecalculation(true);

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
					cell = row.getCell(10);
					cell.setCellValue(spam.getLookMain());
					cell = row.getCell(11);
					cell.setCellValue(spam.getLookCh());
					cell = row.getCell(12);
					cell.setCellValue(spam.getLookList());
					cell = row.getCell(13);
					cell.setCellValue(spam.getLookCont());
					cell = row.getCell(14);
					cell.setCellValue(spam.getHam());
					cell = row.getCell(15);
					cell.setCellValue(spam.getHamLow());
					cell = row.getCell(16);
					cell.setCellValue(spam.getHamFish());
					cell = row.getCell(17);
					cell.setCellValue(spam.getSpamAd());
					cell = row.getCell(18);
					cell.setCellValue(spam.getSpamText());
					cell = row.getCell(19);
					cell.setCellValue(spam.getSpamRedir());
					cell = row.getCell(20);
					cell.setCellValue(spam.getSpamMalware());
					cell = row.getCell(21);
					cell.setCellValue(spam.getSpamCopy());
					cell = row.getCell(22);
					cell.setCellValue(spam.getSpamPorn());
					cell = row.getCell(23);
					cell.setCellValue(spam.getSpamPornWeak());
					cell = row.getCell(24);
					cell.setCellValue(spam.getSpamDecep());
					cell = row.getCell(25);
					cell.setCellValue(spam.getSpamManip());
					cell = row.getCell(26);
					cell.setCellValue(spam.getSpamIllegal());
					cell = row.getCell(45);
					cell.setCellValue(spam.getQc());
					cell = row.getCell(46);
					cell.setCellValue(spam.getComment());
					cell.setCellStyle(style);
					for (int i = 29; i < 44; i++) {
						cell = row.getCell(i);
						cell.setCellFormula(cell.getCellFormula());
					}
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

	public Boolean writeCrossFile(String doWorker, List<Spam> spamList) {
		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;
		path = spamController.getPath();
		if (new File(QC_FILE).exists()) {
			path = QC_FILE;
		} else {
			path = spamController.getPath();
		}
		//File inFile = new File(QC_FILE + "/크로스qc.xlsx");
		//FileInputStream fis;
		try {
			//fis = new FileInputStream(inFile);
			InputStream is = getClass().getResourceAsStream("/file/신규크로스파일.xlsx");
			//InputStream is = getClass().getResourceAsStream("/file/크로스qc.xlsx");
			workbook = new XSSFWorkbook(is);
		} catch (IOException e) {
			alert("지정된 경로를 찾을 수 없습니다");
			e.printStackTrace();
		}

		workbook.setForceFormulaRecalculation(true);

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
				cell.setCellValue(spam.getUri());

				cell = row.getCell(2);
				cell.setCellValue(spam.getScope());

				cell = row.getCell(3); // requetTime
				cell.setCellValue(spam.getRequestTime());

				cell = row.getCell(4);
				cell.setCellValue(spam.getBooleanNotCheck());

				cell = row.getCell(5);
				cell.setCellValue(spam.getBooleanDefer());

				cell = row.getCell(6); // block
				cell.setCellValue(spam.getBooleanBlock());

				cell = row.getCell(7); // time of inspection
				cell.setCellValue(spam.getTimeOfInspection());

				cell = row.getCell(8);
				cell.setCellValue(spam.getName());

				cell = row.getCell(9);
				cell.setCellValue(spam.getEmail());

				cell = row.getCell(10);
				cell.setCellValue(spam.getLookMain());

				cell = row.getCell(11);
				cell.setCellValue(spam.getLookCh());

				cell = row.getCell(12);
				cell.setCellValue(spam.getLookList());

				cell = row.getCell(13);
				cell.setCellValue(spam.getLookCont());

				cell = row.getCell(14);
				cell.setCellValue(spam.getHam());

				cell = row.getCell(15);
				cell.setCellValue(spam.getHamLow());
				
				cell = row.getCell(16);
				cell.setCellValue(spam.getHamFish());

				cell = row.getCell(17);
				cell.setCellValue(spam.getSpamAd());

				cell = row.getCell(18);
				cell.setCellValue(spam.getSpamText());

				cell = row.getCell(19);
				cell.setCellValue(spam.getSpamRedir());

				cell = row.getCell(20);
				cell.setCellValue(spam.getSpamMalware());

				cell = row.getCell(21);
				cell.setCellValue(spam.getSpamCopy());

				cell = row.getCell(22);
				cell.setCellValue(spam.getSpamPorn());
				
				cell = row.getCell(23);
				cell.setCellValue(spam.getSpamPornWeak());

				cell = row.getCell(24);
				cell.setCellValue(spam.getSpamDecep());

				cell = row.getCell(25);
				cell.setCellValue(spam.getSpamManip());

				cell = row.getCell(26);
				cell.setCellValue(spam.getSpamIllegal());

//				for (int i = 27; i < 42; i++) {
//					cell = row.getCell(i);
//					cell.setCellFormula(cell.getCellFormula());
//				}

				if (spam.getQc() != null) {
					cell = row.getCell(42);
					cell.setCellValue(spam.getQc());
				}
				if (spam.getComment() != null) {
					cell = row.getCell(43);
					cell.setCellValue(spam.getComment());
				}

				cell.getRow().setHeight((short) -1);
				break;
			}
		}
		File outFile = null;

		String fileName = path + "/크로스qc(" + LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd")) + ")-"
				+ doWorker + ".xlsx";
		try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
			workbook.write(outputStream);
			outputStream.flush();

		} catch (IOException e) {
			alert(outFile + " (사용 중입니다.)");
			e.printStackTrace();
			return false;
		}
		if (doWorker.equals("RESULT")) {

			try {
				Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileName);
				p.waitFor();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	public Boolean writeResultFile(String doWorker, List<Spam> spamList) {
		Integer GAP = 2;
		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;
		path = spamController.getPath();
		if (new File(QC_FILE).exists()) {
			path = QC_FILE;
		} else {
			path = spamController.getPath();
		}
		//File inFile = new File(path + "/크로스qc_Result.xlsx");
		//FileInputStream fis;
		try {
			//fis = new FileInputStream(inFile);
			InputStream is = getClass().getResourceAsStream("/file/크로스qc_Result.xlsx");
			workbook = new XSSFWorkbook(is);
		} catch (IOException e) {
			alert("지정된 경로를 찾을 수 없습니다");
			e.printStackTrace();
		}

		workbook.setForceFormulaRecalculation(true);

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
		
		LocalDate localDate = LocalDate.now();
		String today = localDate.format(DateTimeFormatter.ofPattern("MM월 dd일"));
		for (Spam spam : spamList) {
			while (rowIterator.hasNext()) {
				Row row = rowIterator.next();
				if (row.getRowNum() < 12) {
					continue;
				}
				
				Cell  cell = row.getCell(1);
				cell.setCellValue(today);
				
				cell = row.getCell(2);
				cell.setCellValue(spam.getLabel());
				
				cell = row.getCell(1 + GAP);
				cell.setCellValue(spam.getUri());
				
				cell = row.getCell(2 + GAP);
				cell.setCellValue(spam.getScope());

				cell = row.getCell(3 + GAP); // requetTime
				cell.setCellValue(spam.getRequestTime());

				cell = row.getCell(4 + GAP);
				cell.setCellValue(spam.getBooleanNotCheck());

				cell = row.getCell(5 + GAP);
				cell.setCellValue(spam.getBooleanDefer());

				cell = row.getCell(6 + GAP); // block
				cell.setCellValue(spam.getBooleanBlock());

				cell = row.getCell(7 + GAP); // time of inspection
				cell.setCellValue(spam.getTimeOfInspection());

				cell = row.getCell(8 + GAP);
				cell.setCellValue(spam.getName());

				cell = row.getCell(9 + GAP);
				cell.setCellValue(spam.getEmail());

				cell = row.getCell(10 + GAP);
				cell.setCellValue(spam.getLookMain());

				cell = row.getCell(11 + GAP);
				cell.setCellValue(spam.getLookCh());

				cell = row.getCell(12 + GAP);
				cell.setCellValue(spam.getLookList());

				cell = row.getCell(13 + GAP);
				cell.setCellValue(spam.getLookCont());

				cell = row.getCell(14 + GAP);
				cell.setCellValue(spam.getHam());

				cell = row.getCell(15 + GAP);
				cell.setCellValue(spam.getHamLow());

				cell = row.getCell(16 + GAP);
				cell.setCellValue(spam.getSpamAd());

				cell = row.getCell(17 + GAP);
				cell.setCellValue(spam.getSpamText());

				cell = row.getCell(18 + GAP);
				cell.setCellValue(spam.getSpamRedir());

				cell = row.getCell(19 + GAP);
				cell.setCellValue(spam.getSpamMalware());

				cell = row.getCell(20 + GAP);
				cell.setCellValue(spam.getSpamCopy());

				cell = row.getCell(21 + GAP);
				cell.setCellValue(spam.getSpamPorn());

				cell = row.getCell(22 + GAP);
				cell.setCellValue(spam.getSpamDecep());

				cell = row.getCell(23 + GAP);
				cell.setCellValue(spam.getSpamManip());

				cell = row.getCell(24 + GAP);
				cell.setCellValue(spam.getSpamIllegal());

//				for (int i = 27; i < 42; i++) {
//					cell = row.getCell(i);
//					cell.setCellFormula(cell.getCellFormula());
//				}

				if (spam.getQc() != null) {
					cell = row.getCell(42 + GAP);
					cell.setCellValue(spam.getQc());
				}
				if (spam.getComment() != null) {
					cell = row.getCell(43 + GAP);
					cell.setCellValue(spam.getComment());
				}

				cell.getRow().setHeight((short) -1);
				break;
			}
		}
		File outFile = null;

		String fileName = path + "/크로스qc(" + LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd")) + ")-"
				+ doWorker + ".xlsx";
		try (FileOutputStream outputStream = new FileOutputStream(fileName)) {
			workbook.write(outputStream);
			outputStream.flush();

		} catch (IOException e) {
			alert(outFile + " (사용 중입니다.)");
			e.printStackTrace();
			return false;
		}
		if (doWorker.equals("RESULT")) {

			try {
				Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + fileName);
				p.waitFor();
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}
//	private String randomFileName(String name) {		
//		return  path + "/크로스qc(" + LocalDate.now().format(DateTimeFormatter.ofPattern("MMdd")) + ")-" + name + ".xlsx";
//	}

	private void alert(String messge) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("배정 현황");
		alert.setHeaderText(null);
		alert.setGraphic(null);
		alert.setContentText(messge);

		alert.showAndWait();
	}

	public String getPath() {
		return path;
	}

}

//SpamCheck spamCheck;
//String label = (String) getValueFromCell(cell);
//spam.setLabel(label);
//if (label != null && !label.equals("")) {
//	ObjectMapper mapper = new ObjectMapper();
//	spamCheck = mapper.readValue(label, SpamCheck.class);
//	spam.setSpamCheck(spamCheck);
//}
//
//if (label.contains("lookMain"))
//	spam.setLookMain(true);
//if (label.contains("lookCh"))
//	spam.setLookCh(true);
//if (label.contains("lookList"))
//	spam.setLookList(true);
//if (label.contains("lookCont"))
//	spam.setLookCont(true);
//if (label.contains("ham")) {
//	try {
//		final JSONParser jsonParser = new JSONParser();
//		final JSONObject jsonObj = (JSONObject) jsonParser.parse(label);
//		final Boolean ham = (Boolean) jsonObj.get("ham");
//		final Boolean hamLow = (Boolean) jsonObj.get("hamLow");
//		if (ham != null && ham) {
//			spam.setHam(true);
//		}
//		if (hamLow != null && hamLow) {
//			spam.setHamLow(true);
//		}
//
//	} catch (ParseException e) {
//		e.printStackTrace();
//	}
//}
//if (label.contains("hamLow"))
//	spam.setHamLow(true);
//if (label.contains("spamAd"))
//	spam.setSpamAd(true);
//if (label.contains("spamText"))
//	spam.setSpamText(true);
//if (label.contains("spamRedir"))
//	spam.setSpamRedir(true);
//if (label.contains("spamMalware"))
//	spam.setSpamMalware(true);
//if (label.contains("spamCopy"))
//	spam.setSpamCopy(true);
//if (label.contains("spamPorn"))
//	spam.setSpamPorn(true);
//if (label.contains("spamDecep"))
//	spam.setSpamDecep(true);
//if (label.contains("spamManip"))
//	spam.setSpamManip(true);
//if (label.contains("spamIllegal"))
//	spam.setSpamIllegal(true);
