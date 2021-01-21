package com.webspam.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ApachePOIExcel {

	private static int WIDTH = 512;
	private ObservableList<String> headerList = FXCollections.observableArrayList("", "URL", "SCOPE", "이름", "검수불가",
			"스팸판정", "품질지수", "목적지수", "일부스팸판정", "허위 URL Prefix", "responseTime");
	private String projectPath = System.getProperty("user.home");

	public ApachePOIExcel() {
		// LocalDate today = LocalDate.now();
		// String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

	}

	public String write(List<String> stringList, String name, File selectedFile) {

		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;
		String excelFile = projectPath + File.separator + "Desktop" + File.separator + getFileName(selectedFile);

		if (new File(excelFile).exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(excelFile);
				workbook = new XSSFWorkbook(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			workbook = new XSSFWorkbook();
		}

		Boolean sheetExist = false;
		// Check if the workbook is empty or not
		if (workbook.getNumberOfSheets() != 0) {
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				if (workbook.getSheetName(i).equals(name)) {
					sheet = workbook.getSheet(name);
					sheetExist = true;
				}
			}
		}
		if (!sheetExist) {
			// Create new sheet to the workbook if empty
			sheet = workbook.createSheet(name);
		}

		// XSSFSheet sheet = workbook.createSheet(name);

		int rowNum = 0;
		// System.out.println("Creating excel");
		Row row = sheet.createRow(rowNum++);

		sheet.setColumnWidth(0, 4 * WIDTH);
		sheet.setColumnWidth(1, 20 * WIDTH);
		sheet.setColumnWidth(2, 8 * WIDTH);
		sheet.setColumnWidth(3, 5 * WIDTH);
		sheet.setColumnWidth(4, 5 * WIDTH);
		sheet.setColumnWidth(5, 5 * WIDTH);
		sheet.setColumnWidth(8, 8 * WIDTH);
		sheet.setColumnWidth(9, 15 * WIDTH);
		sheet.setColumnWidth(10, 10 * WIDTH);

		CellStyle blackStyle = workbook.createCellStyle();
		blackStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		blackStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		blackStyle.setAlignment(HorizontalAlignment.CENTER);
		blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		Font font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		blackStyle.setFont(font);

		CellStyle redStyle = workbook.createCellStyle();
		redStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
		redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		redStyle.setAlignment(HorizontalAlignment.CENTER);
		redStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		int colNum = 0;
		for (String header : headerList) {
			Cell cell = row.createCell(colNum++);
			cell.setCellValue((String) header);
			cell.setCellStyle(blackStyle);
		}

		CellStyle centerStyle = workbook.createCellStyle();
		centerStyle.setAlignment(HorizontalAlignment.CENTER);
		centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		stringList.remove(0);
		int rowNo = 1;
		String preUrl = null;
		for (String line : stringList) {
			String[] array = line.split("\t");
			if (array[4].equals(name) && !array[0].equals(preUrl)) {

				preUrl = array[0];				
				row = sheet.createRow(rowNum++);
				colNum = 0;
				Cell cell = row.createCell(colNum++);
				cell.setCellValue(rowNo++);
				cell.setCellStyle(centerStyle);

				// URL
				cell = row.createCell(colNum++);
				cell.setCellValue(array[0]);

				// SCOPE
				cell = row.createCell(colNum++);
				cell.setCellValue(array[1]);
				cell.setCellStyle(centerStyle);

				// NAME
				cell = row.createCell(colNum++);
				cell.setCellValue(array[4]);
				cell.setCellStyle(centerStyle);

				// 검수불가
				Cell connectCell = row.createCell(colNum++);
				connectCell.setCellValue(checkValue(array[5]));
				connectCell.setCellStyle(centerStyle);
				// cell.setCellValue(array[5]);

				// 스팸판정
				Cell spamCell = row.createCell(colNum++);
				spamCell.setCellValue(checkValue(array[6]));
				spamCell.setCellStyle(centerStyle);
				// cell.setCellValue(array[6]);

				// 품질지수
				Cell qualityCell = row.createCell(colNum++);
				qualityCell.setCellValue(array[7]);
				qualityCell.setCellStyle(centerStyle);

				// 목적지수
				Cell purpuseCell = row.createCell(colNum++);
				purpuseCell.setCellValue(array[8]);
				purpuseCell.setCellStyle(centerStyle);

				if (array[1].equals("1(domain)") || array[1].equals("2(host)")) {
					if (array[5].equals("false") && array[6].equals("false") && array[7].equals("") && array[8].equals("")) {
						connectCell.setCellStyle(redStyle);
						spamCell.setCellStyle(redStyle);
					}
				}

				// array[6] : 스펨판정
				if (array[6].equals("true")) {

					if ((array[1].equals("1(domain)") || array[1].equals("2(host)")) && array[7].equals("")) {
						qualityCell.setCellStyle(redStyle);
					}

					if ((array[1].equals("1(domain)") || array[1].equals("2(host)")) && array[8].equals("")) {
						purpuseCell.setCellStyle(redStyle);
					}

					if (!array[7].equals("") && Integer.valueOf(array[7]) < 5) {
						qualityCell.setCellStyle(redStyle);
					}

					if (!array[8].equals("") && Integer.valueOf(array[8]) < 5) {
						purpuseCell.setCellStyle(redStyle);
					}
				}

				// array[6] : 스펨판정 array[7] 품질지수
				if (array[6].equals("false")) {
					if (!array[7].equals("") && Integer.valueOf(array[7]) > 4) {
						qualityCell.setCellStyle(redStyle);
					}

					if (!array[8].equals("") && Integer.valueOf(array[8]) > 4) {
						purpuseCell.setCellStyle(redStyle);
					}
				}

				if (!array[7].equals("")) {
					if (Integer.valueOf(array[7]) == 0) {
						if (array[8].equals("") || Integer.valueOf(array[8]) != 0) {
							purpuseCell.setCellStyle(redStyle);
						}
					}

					if (Integer.valueOf(array[7]) == 1 || Integer.valueOf(array[7]) == 2) {
						if (array[8].equals("") || Integer.valueOf(array[8]) > 4) {
							purpuseCell.setCellStyle(redStyle);
						}
					}

					if (Integer.valueOf(array[7]) == 3) {
						if (array[8].equals("")) {
							purpuseCell.setCellStyle(redStyle);
						}

						if (!array[8].equals("") && Integer.valueOf(array[8]) == 0) {
							purpuseCell.setCellStyle(redStyle);
						}

						if (!array[8].equals("") && Integer.valueOf(array[8]) == 1) {
							purpuseCell.setCellStyle(redStyle);
						}
					}

					if (Integer.valueOf(array[7]) == 4) {
						if (array[8].equals("") || Integer.valueOf(array[8]) > 4) {
							purpuseCell.setCellStyle(redStyle);
						}
					}

					if (Integer.valueOf(array[7]) == 5) {
						if (array[8].equals("")) {
							purpuseCell.setCellStyle(redStyle);
						} else {
							if (Integer.valueOf(array[8]) < 5) {
								purpuseCell.setCellStyle(redStyle);
							}

							if (Integer.valueOf(array[8]) == 6 || Integer.valueOf(array[8]) == 7
									|| Integer.valueOf(array[8]) == 8) {
								purpuseCell.setCellStyle(redStyle);
							}

						}

					}

					if (Integer.valueOf(array[7]) == 6) {
						if (!array[8].equals("") && Integer.valueOf(array[8]) != 9) {
							purpuseCell.setCellStyle(redStyle);
						}
					}

					if (Integer.valueOf(array[7]) == 7 || Integer.valueOf(array[7]) == 9) {
						if (!array[8].equals("") && Integer.valueOf(array[8]) < 6) {
							purpuseCell.setCellStyle(redStyle);
						}
					}

					if (Integer.valueOf(array[7]) < 5) {
						if (array[6].equals("true")) {
							spamCell.setCellStyle(redStyle);
						}

						if (array[8].equals("") || Integer.valueOf(array[8]) > 4) {
							purpuseCell.setCellStyle(redStyle);
						}
					}

					if (Integer.valueOf(array[7]) > 4) {
						if (!array[6].equals("true")) {
							spamCell.setCellStyle(redStyle);
						}

						if (array[8].equals("") || Integer.valueOf(array[8]) < 5) {
							purpuseCell.setCellStyle(redStyle);
						}
					}
				}

				if (!array[7].equals("") && Integer.valueOf(array[7]) == 8) {
					qualityCell.setCellStyle(redStyle);
				}

				if (!array[8].equals("") && Integer.valueOf(array[8]) == 8) {
					purpuseCell.setCellStyle(redStyle);
				}

				// 일부스팸판정
				cell = row.createCell(colNum++);
				cell.setCellValue(checkValue(array[9]));
				cell.setCellStyle(centerStyle);

				// 허위 URL Prefix
				cell = row.createCell(colNum++);
				cell.setCellValue(array[10]);

				// responseTime
				cell = row.createCell(colNum++);
				cell.setCellValue(array[11]);
				cell.setCellStyle(centerStyle);
			}
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(excelFile);
			workbook.write(outputStream);
			workbook.close();
		} catch (IOException e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("Information Dialog");
			alert.setHeaderText(null);
			alert.setContentText("엑셀파일을 사용 중이기때문에 엑세스할 수 없습니다!");

			alert.showAndWait();
			e.printStackTrace();
			return null;
		}

		return excelFile;
	}

	public boolean write(List<String> stringList, File selectedFile) {

		String sheetName = "통합";
		XSSFWorkbook workbook = null;
		XSSFSheet sheet = null;
		String excelFile = projectPath + File.separator + "Desktop" + File.separator + getFileName(selectedFile);

		if (new File(excelFile).exists()) {
			FileInputStream fis;
			try {
				fis = new FileInputStream(excelFile);
				workbook = new XSSFWorkbook(fis);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			workbook = new XSSFWorkbook();
		}

		Boolean sheetExist = false;
		// Check if the workbook is empty or not
		if (workbook.getNumberOfSheets() != 0) {
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				if (workbook.getSheetName(i).equals(sheetName)) {
					sheet = workbook.getSheet(sheetName);
					sheetExist = true;
				}
			}
		}
		if (!sheetExist) {
			// Create new sheet to the workbook if empty
			sheet = workbook.createSheet(sheetName);
		}

		// XSSFSheet sheet = workbook.createSheet(name);

		int rowNum = 0;
		// System.out.println("Creating excel");
		Row row = sheet.createRow(rowNum++);

		sheet.setColumnWidth(0, 4 * WIDTH);
		sheet.setColumnWidth(1, 20 * WIDTH);
		sheet.setColumnWidth(2, 8 * WIDTH);
		sheet.setColumnWidth(3, 5 * WIDTH);
		sheet.setColumnWidth(4, 5 * WIDTH);
		sheet.setColumnWidth(5, 5 * WIDTH);
		sheet.setColumnWidth(8, 8 * WIDTH);
		sheet.setColumnWidth(9, 15 * WIDTH);
		sheet.setColumnWidth(10, 10 * WIDTH);

		CellStyle blackStyle = workbook.createCellStyle();
		blackStyle.setFillForegroundColor(IndexedColors.BLACK.getIndex());
		blackStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		blackStyle.setAlignment(HorizontalAlignment.CENTER);
		blackStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		Font font = workbook.createFont();
		font.setColor(IndexedColors.WHITE.getIndex());
		blackStyle.setFont(font);

		CellStyle redStyle = workbook.createCellStyle();
		redStyle.setFillForegroundColor(IndexedColors.ROSE.getIndex());
		redStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		redStyle.setAlignment(HorizontalAlignment.CENTER);
		redStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		int colNum = 0;
		for (String header : headerList) {
			Cell cell = row.createCell(colNum++);
			cell.setCellValue((String) header);
			cell.setCellStyle(blackStyle);
		}

		CellStyle centerStyle = workbook.createCellStyle();
		centerStyle.setAlignment(HorizontalAlignment.CENTER);
		centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

		stringList.remove(0);
		int rowNo = 1;
		for (String line : stringList) {
			String[] array = line.split("\t");
			row = sheet.createRow(rowNum++);
			colNum = 0;
			Cell cell = row.createCell(colNum++);
			cell.setCellValue(rowNo++);
			cell.setCellStyle(centerStyle);

			// URL
			cell = row.createCell(colNum++);
			cell.setCellValue(array[0]);

			// SCOPE
			cell = row.createCell(colNum++);
			cell.setCellValue(array[1]);
			cell.setCellStyle(centerStyle);

			// NAME
			cell = row.createCell(colNum++);
			cell.setCellValue(array[4]);
			cell.setCellStyle(centerStyle);

			// 검수불가
			Cell unknownCell = row.createCell(colNum++);
			unknownCell.setCellValue(checkValue(array[5]));
			unknownCell.setCellStyle(centerStyle);
			// cell.setCellValue(array[5]);

			// 스팸판정
			Cell spamCell = row.createCell(colNum++);
			spamCell.setCellValue(checkValue(array[6]));
			spamCell.setCellStyle(centerStyle);
			// cell.setCellValue(array[6]);

			// 품질지수
			Cell qualityCell = row.createCell(colNum++);
			qualityCell.setCellValue(array[7]);
			qualityCell.setCellStyle(centerStyle);

			// 목적지수
			Cell purpuseCell = row.createCell(colNum++);
			purpuseCell.setCellValue(array[8]);
			purpuseCell.setCellStyle(centerStyle);

			if ((array[1].equals("1(domain)") || array[1].equals("2(host)")) && array[5].equals("false")
					&& array[6].equals("false")) {
				if (array[7].equals("") && array[8].equals("")) {
					spamCell.setCellStyle(redStyle);
					unknownCell.setCellStyle(redStyle);
				}
			}

			// array[6] : 스펨판정
			if (array[6].equals("true")) {

				if ((array[1].equals("1(domain)") || array[1].equals("2(host)")) && array[7].equals("")) {
					qualityCell.setCellStyle(redStyle);
				}

				if ((array[1].equals("1(domain)") || array[1].equals("2(host)")) && array[8].equals("")) {
					purpuseCell.setCellStyle(redStyle);
				}

				if (!array[7].equals("") && Integer.valueOf(array[7]) < 5) {
					qualityCell.setCellStyle(redStyle);
				}

				if (!array[8].equals("") && Integer.valueOf(array[8]) < 5) {
					purpuseCell.setCellStyle(redStyle);
				}
			}

			// array[6] : 스펨판정 array[7] 품질지수
			if (array[6].equals("false")) {
				if (!array[7].equals("") && Integer.valueOf(array[7]) > 4) {
					qualityCell.setCellStyle(redStyle);
				}

				if (!array[8].equals("") && Integer.valueOf(array[8]) > 4) {
					purpuseCell.setCellStyle(redStyle);
				}
			}

			if (!array[7].equals("")) {
				if (Integer.valueOf(array[7]) == 0) {
					if (array[8].equals("") || Integer.valueOf(array[8]) != 0) {
						purpuseCell.setCellStyle(redStyle);
					}
				}

				if (Integer.valueOf(array[7]) == 1 || Integer.valueOf(array[7]) == 2) {
					if (array[8].equals("") || Integer.valueOf(array[8]) > 4) {
						purpuseCell.setCellStyle(redStyle);
					}
				}

				if (Integer.valueOf(array[7]) == 3) {
					if (array[8].equals("")) {
						purpuseCell.setCellStyle(redStyle);
					}

					if (!array[8].equals("") && Integer.valueOf(array[8]) == 0) {
						purpuseCell.setCellStyle(redStyle);
					}

					if (!array[8].equals("") && Integer.valueOf(array[8]) == 1) {
						purpuseCell.setCellStyle(redStyle);
					}
				}

				if (Integer.valueOf(array[7]) == 4) {
					if (array[8].equals("") || Integer.valueOf(array[8]) > 4) {
						purpuseCell.setCellStyle(redStyle);
					}
				}

				if (Integer.valueOf(array[7]) == 5) {
					if (array[8].equals("")) {
						purpuseCell.setCellStyle(redStyle);
					} else {
						if (Integer.valueOf(array[8]) < 5) {
							purpuseCell.setCellStyle(redStyle);
						}

						if (Integer.valueOf(array[8]) == 6 || Integer.valueOf(array[8]) == 7
								|| Integer.valueOf(array[8]) == 8) {
							purpuseCell.setCellStyle(redStyle);
						}

					}

				}

				if (Integer.valueOf(array[7]) == 6) {
					if (!array[8].equals("") && Integer.valueOf(array[8]) != 9) {
						purpuseCell.setCellStyle(redStyle);
					}
				}

				if (Integer.valueOf(array[7]) == 7 || Integer.valueOf(array[7]) == 9) {
					if (!array[8].equals("") && Integer.valueOf(array[8]) < 6) {
						purpuseCell.setCellStyle(redStyle);
					}
				}

				if (Integer.valueOf(array[7]) < 5) {
					if (array[6].equals("true")) {
						spamCell.setCellStyle(redStyle);
					}

					if (array[8].equals("") || Integer.valueOf(array[8]) > 4) {
						purpuseCell.setCellStyle(redStyle);
					}
				}

				if (Integer.valueOf(array[7]) > 4) {
					if (!array[6].equals("true")) {
						spamCell.setCellStyle(redStyle);
					}

					if (array[8].equals("") || Integer.valueOf(array[8]) < 5) {
						purpuseCell.setCellStyle(redStyle);
					}
				}
			}

			if (!array[7].equals("") && Integer.valueOf(array[7]) == 8) {
				qualityCell.setCellStyle(redStyle);
			}

			if (!array[8].equals("") && Integer.valueOf(array[8]) == 8) {
				purpuseCell.setCellStyle(redStyle);
			}

			// 일부스팸판정
			cell = row.createCell(colNum++);
			cell.setCellValue(checkValue(array[9]));
			cell.setCellStyle(centerStyle);

			// 허위 URL Prefix
			cell = row.createCell(colNum++);
			cell.setCellValue(array[10]);

			// responseTime
			cell = row.createCell(colNum++);
			cell.setCellValue(array[11]);
			cell.setCellStyle(centerStyle);

		}

		try {
			FileOutputStream outputStream = new FileOutputStream(excelFile);
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

	private String checkValue(String value) {
		String returnValue = "";
		if (value.equals("false")) {
			returnValue = "N";
		} else if (value.equals("true")) {
			returnValue = "Y";
		} else if (value.trim().equals("")) {
			returnValue = "";
		}
		// System.err.println(value + " : " + returnValue);
		return returnValue;
	}

	private String getFileName(File selectedFile) {
		String[] array = selectedFile.getName().split("_");
		return array[1] + "_" + array[0] + ".xlsx";
	}

	public static void main(String[] args) {
	}
}
