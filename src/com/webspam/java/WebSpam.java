package com.webspam.java;

import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class WebSpam extends Application {

	private int WIDTH = 400;
	private int WIDTH_BUTTON = 100;
	private String File_SEARCH = "site:";
	private String HOME_DIR = System.getProperty("user.home");
	private String SPAMMGR = "http://spammgr.navercorp.com/doc.html?search=";
	private String PATH_EXPLORE = "C:\\Program Files\\Internet Explorer\\iexplore.exe ";
	private String PATH_CHROME = "C:\\Program Files (x86)\\Google\\Chrome\\Application\\chrome.exe";
	private List<TargetQC> targetList;
	private List<TargetNJK> njkList;
	private TargetQC target;
	private TargetNJK targetNjk;
	private VBox vBox;
	private ObservableList<String> nameList = FXCollections.observableArrayList(" ", "나상운", "송호열", "엄정란", "연제관", "이미원",
			"이성규", "임수진", "정영미", "정영선", "최동우", "최붕희", "허창건");
	private ObservableList<String> njkQuality = FXCollections.observableArrayList(" ", "0.정부/지방", "0.교육", "1.고품질",
			"2.중품질", "3.저품질", "4.최저품질", "5.자동생성", "6.해킹/리디렉션");
	private ObservableList<String> veryHi = FXCollections.observableArrayList("", "0.정부공식", "6.교육(초중고등학교)",
			"7.교육(대학교홈페이지)");
	private ObservableList<String> hi = FXCollections.observableArrayList("", "0.업체(홈페이지)", "1.쇼핑몰", "2.뉴스", "3.커뮤니티",
			"4.블러그", "5.해외사이트", "6.보험");
	private ObservableList<String> low = FXCollections.observableArrayList("", "0.업체(홈페이지)", "1.쇼핑몰", "2.뉴스", "3.커뮤니티",
			"4.블러그", "5.해외사이트", "6.보험", "7.성인", "8.토렌트");
	private ObservableList<String> auto = FXCollections.observableArrayList("", "0.카트형", "1.블로그형", "2.리스트형", "3.게시판형",
			"4.검색형");
	private ObservableList<String> haking = FXCollections.observableArrayList("", "1.해킹(보험 등)", "3.리디렉션", "5.도매인선점",
			"6.스팸링크");
	private ObservableList<String> take = FXCollections.observableArrayList("", "탬플릿", "소유주");
	private ObservableList<String> checkList = FXCollections.observableArrayList(" ", "오판단", "오체크", "오추출");
	private String cssLayout = "-fx-border-color: black;\n" + "-fx-border-insets: 5;\n"
			+ "-fx-border-width: 0 0 1 0;\n";
	private ApachePOIExcel apachePOIExcel;
	private ApachePOITarget apachePOITarget;
	private ApacheNJK apacheNjk;
	private File selectedFile;

	private ComboBox<String> comboBoxQC;
	private ComboBox<String> comboBoxQuality;
	private ComboBox<String> comboBoxPurpose;
	private TextArea textAreaComment;
	private TextArea textAreaTypeComment;
    private TextArea textAreaContents;
	private TextField textFieldNote;
	private TextField templeteId;
	private ComboBox<String> taskName;
	private Label labelNjkUrl;
	private int count = 1;

	@Override
	public void start(Stage primaryStage) throws Exception {

		primaryStage.getIcons().add(new Image("/image/hyperlink.jpg"));
		primaryStage.setOnCloseRequest(e -> Platform.exit());
		apachePOIExcel = new ApachePOIExcel();
		apachePOITarget = new ApachePOITarget();
		apacheNjk = new ApacheNJK();

		primaryStage.setTitle("웹스팸 Ver 20.01.08");
		primaryStage.setAlwaysOnTop(true);

		Button buttonGoogle = new Button("Google search");
		buttonGoogle.setPrefWidth(WIDTH_BUTTON);
		TextField textFieldGoogle = new TextField();
		textFieldGoogle.setPrefWidth(WIDTH);
		textFieldGoogle.setOnMouseClicked(e -> {
			eventSearch(textFieldGoogle, PATH_CHROME);
			writeTextFromClipboard(textFieldGoogle.getText());
			afterClean(textFieldGoogle);

		});
		buttonGoogle.setOnAction(e -> textFieldGoogle.clear());
		HBox hboxGoogle = new HBox(textFieldGoogle, buttonGoogle);

		Button buttonExplorer = new Button("Explorer search");
		buttonExplorer.setPrefWidth(WIDTH_BUTTON);
		TextField textFieldExplorer = new TextField();
		textFieldExplorer.setPrefWidth(WIDTH);
		textFieldExplorer.setOnMouseClicked(e -> {
			eventSearch(textFieldExplorer, PATH_EXPLORE);
			writeTextFromClipboard(textFieldExplorer.getText());
			afterClean(textFieldExplorer);
		});
		buttonExplorer.setOnAction(e -> textFieldExplorer.clear());
		HBox hboxExplorer = new HBox(textFieldExplorer, buttonExplorer);

		Button buttonSearchResult = new Button("검색결과 ");
		buttonSearchResult.setPrefWidth(WIDTH_BUTTON);
		TextField textFieldStoredPage = new TextField();
		textFieldStoredPage.setOnMouseClicked(e -> {
			eventSearchResult(textFieldStoredPage);
			// afterClean(textFieldStoredPage);
			// textFieldStoredPage.setText("site:");
		});
		textFieldStoredPage.setText(File_SEARCH);
		textFieldStoredPage.setPrefWidth(WIDTH);

		buttonSearchResult.setOnAction(e -> textFieldStoredPage.setText(File_SEARCH));
		HBox hboxStroedPage = new HBox(textFieldStoredPage, buttonSearchResult);

		Button buttonStoredPage = new Button("Stored  page");
		buttonStoredPage.setPrefWidth(WIDTH_BUTTON);
		TextField textFieldSpam = new TextField();
		textFieldSpam.setPrefWidth(WIDTH);
		textFieldSpam.setOnMouseClicked(e -> {
			eventStoredPage(textFieldSpam, PATH_CHROME);
			afterClean(textFieldSpam);
		});
		buttonStoredPage.setOnAction(e -> textFieldSpam.clear());
		HBox hboxSpam = new HBox(textFieldSpam, buttonStoredPage);

		ComboBox<String> comboBoxName = new ComboBox<String>(nameList);
		comboBoxName.setPrefWidth(WIDTH * 0.2);
		TextField textFieldExcel = new TextField();
		textFieldExcel.setPrefWidth(WIDTH * 0.8);
		FileChooser fileChooser = getFileChooser();
		Button buttonExcel = new Button("Select File");
		buttonExcel.setPrefWidth(WIDTH_BUTTON);
		buttonExcel.setOnAction(e -> {
			selectedFile = fileChooser.showOpenDialog(primaryStage);
			if (selectedFile != null) {
				textFieldExcel.setText(selectedFile.getName());
				List<String> stringList = readFileToList(selectedFile);
				String seletedName = comboBoxName.getValue();
				String resultFile = null;
				if (seletedName == null || seletedName.trim().equals("")) {
					for (String name : nameList) {
						if (!name.trim().equals("")) {
							resultFile = apachePOIExcel.write(stringList, name, selectedFile);
						}
					}

					// 통합 sheet만들기
					// popExcel.write(stringList, selectedFile);
				} else {
					resultFile = apachePOIExcel.write(stringList, seletedName, selectedFile);
				}
				if (resultFile != null) {

					try {
						Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + resultFile);

					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}

				}
				seletedName = null;
				afterClean(textFieldExcel);
			}
		});
		HBox hboxExcel = new HBox(comboBoxName, textFieldExcel, buttonExcel);

		WordSearch wordSearch = new WordSearch();

		TextField textFieldWordCount = new TextField();
		textFieldWordCount.setPrefWidth(WIDTH);
		textFieldWordCount.setOnMouseClicked(e -> {
			countSpamWord(textFieldWordCount, wordSearch);
			afterClean(textFieldWordCount);
		});

		Button buttonWordCount = new Button("Count word");
		buttonWordCount.setPrefWidth(WIDTH_BUTTON);
		buttonWordCount.setOnAction(e -> {
			countSpamWord(textFieldWordCount, wordSearch);
			// afterClean(textFieldWordCount);
		});
		HBox hboxWordCount = new HBox(textFieldWordCount, buttonWordCount);

		// Cross QC
		TextField textFieldCrossQc = new TextField();
		textFieldCrossQc.setPrefWidth(WIDTH);

		Button buttonCrossQc = new Button("QC File");
		buttonCrossQc.setPrefWidth(WIDTH_BUTTON);
		buttonCrossQc.setOnAction(e -> {
			selectedFile = fileChooser.showOpenDialog(primaryStage);
			if (selectedFile != null) {
				textFieldCrossQc.setText(selectedFile.getName());
				targetList = apachePOITarget.getTargetList(selectedFile);
				dailoagQualityControl(primaryStage, targetList);
			}

		});

		HBox hboxCrossQc = new HBox(textFieldCrossQc, buttonCrossQc);

		// NJK
		TextField textFieldNjk = new TextField();
		textFieldNjk.setPrefWidth(WIDTH);

		Button buttonNjk = new Button("NJK File");
		buttonNjk.setPrefWidth(WIDTH_BUTTON);
		buttonNjk.setOnAction(e -> {
			selectedFile = fileChooser.showOpenDialog(primaryStage);
			if (selectedFile != null) {
				textFieldNjk.setText(selectedFile.getName());
				njkList = apacheNjk.getTargetList(selectedFile);
				dialogNjk(primaryStage, njkList);
			}

		});

		HBox hboxNjk = new HBox(textFieldNjk, buttonNjk);

		VBox vbox = new VBox(hboxGoogle, hboxExplorer, hboxStroedPage, hboxSpam, hboxWordCount, hboxExcel, hboxCrossQc,
				hboxNjk);
		vbox.setPadding(new Insets(10, 10, 10, 10));
		vbox.setSpacing(5);

		Scene scene = new Scene(vbox);
		primaryStage.setScene(scene);
		primaryStage.setX(150);
		primaryStage.setY(800);
		primaryStage.show();
	}

	private void dialogNjk(Stage primaryStage, List<TargetNJK> njkList) {
		final Stage dialog = new Stage();
		// dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(primaryStage);
		VBox dialogVbox = new VBox(20);
		dialogVbox.getChildren().addAll(getBorderPaneNjk(njkList, dialog));
		Scene dialogScene = new Scene(dialogVbox);
		dialog.setScene(dialogScene);
		dialog.show();
	}

	private BorderPane getBorderPaneNjk(List<TargetNJK> njkList, Stage dialog) {
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(20, 10, 10, 10));

		templeteId = new TextField();
		templeteId.setPrefWidth(515);
		taskName = new ComboBox<String>(take); 
		HBox HBoxTask = new HBox(templeteId, taskName);
		HBoxTask.setPadding(new Insets(5, 0, 5, 0));

		
		comboBoxQuality = new ComboBox<String>(njkQuality);
		comboBoxQuality.setPromptText("품질");
		comboBoxPurpose = new ComboBox<String>(hi);
		comboBoxPurpose.setPromptText("목적");
		comboBoxQuality.setMinWidth(300);
		comboBoxPurpose.setMinWidth(300);
		HBox HBoxComboBox = new HBox(comboBoxQuality, comboBoxPurpose);
		HBoxComboBox.setPadding(new Insets(5, 0, 5, 0));

		comboBoxQuality.setOnAction(e -> updateComboBoxPurpose());
		textAreaTypeComment = new TextArea();
		textAreaTypeComment.setPromptText("Type comment"); 
		textAreaTypeComment.setPrefHeight(60);
		textAreaTypeComment.setMaxHeight(60);
		textFieldNote = new TextField();
		textFieldNote.setPromptText("비고");
		textAreaContents = new TextArea();
		textAreaContents.setPromptText("내용");
		textAreaContents.setPrefHeight(60);
		textAreaContents.setMaxHeight(60);
		targetNjk = njkList.get(0);
		Button buttonExplorer = new Button(" E ");
		buttonExplorer.setOnAction(e -> eventSearch(targetNjk.getUrl(), PATH_EXPLORE));
		labelNjkUrl = new Label();
		labelNjkUrl.setStyle("-fx-font-weight: bold");
		labelNjkUrl.setPadding(new Insets(0, 0, 0, 10));
		labelNjkUrl.setFont(new Font("Godic", 16));
		labelNjkUrl.setOnMouseClicked(e -> {
			StringSelection stringSelection = new StringSelection(targetNjk.getUrl());
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			eventSearch(targetNjk.getUrl(), PATH_CHROME);
		});
		HBox hBoxUrl = new HBox();
		hBoxUrl.getChildren().setAll(buttonExplorer, labelNjkUrl);

		// cleanQCResult(target);
		labelNjkUrl.setText(targetNjk.getUrl());
		eventSearch(targetNjk.getUrl(), PATH_CHROME);
		// borderPane.setCenter(vBox);

		HBox hBoxSave = new HBox();
		Button buttonSave = new Button("저   장 [ 1 / " + njkList.size() + " ]");
		buttonSave.setMaxWidth(Double.MAX_VALUE);
		buttonSave.prefWidthProperty().bind(Bindings.divide(hBoxSave.widthProperty(), 1.0));

		Button buttonBefore = new Button("<");
		buttonBefore.setPrefWidth(250);
		Button buttonNext = new Button(">");
		buttonNext.setPrefWidth(250);

        buttonSave.setOnAction(e -> {
           	//comment = comment.replace("\n", "\\n");
           	targetNjk.setPurpose(comboBoxPurpose.getSelectionModel().getSelectedItem());
           	targetNjk.setQuality(comboBoxQuality.getSelectionModel().getSelectedItem());
           	targetNjk.setNote(textFieldNote.getText().trim());
           	targetNjk.setTypeComment(textAreaTypeComment.getText().trim());
           	targetNjk.setContents(textAreaContents.getText().trim());
            // 저장절차
           	apacheNjk.writeTarget(selectedFile, targetNjk);
        	buttonNext.fire();   
        	cleanNjkResult();
         });

        buttonBefore.setOnAction(e -> {
        	if(njkList != null ) {
	        	count--;
	        	if(count <= 0) {
	        		count = 1;
	        		buttonBefore.setDisable(true);
	        	} else {
	        		buttonSave.setText("저   장 [ " + count + " /" +  njkList.size() + "]");
	        		targetNjk = njkList.get(count-1);
	        		labelNjkUrl.setText(targetNjk.getUrl());
	        		templeteId.setText(targetNjk.getTempleteId());
	        		taskName.getSelectionModel().select(targetNjk.getTask());
	            	buttonBefore.setDisable(false);
	            	buttonNext.setDisable(false);
	            	eventSearch(targetNjk.getUrl(), PATH_CHROME);
	            	cleanNjkResult();
	        	}
        	}
        });

        buttonNext.setOnAction(e -> {
        	if(njkList != null ) {
	        	count++;
	        	if(count > njkList.size()-1) {
	        		count = njkList.size();
	        		buttonNext.setDisable(true);
	        	} else {
	        		buttonBefore.setDisable(false);
	        		buttonNext.setDisable(false);
	        	}
	        	buttonSave.setText("저   장 [ " + count + " /" +  njkList.size() + "]");   
	        	targetNjk = njkList.get(count-1);
	        	labelNjkUrl.setText(targetNjk.getUrl());
        		templeteId.setText(targetNjk.getTempleteId());
        		taskName.getSelectionModel().select(targetNjk.getTask());
	        	eventSearch(targetNjk.getUrl(), PATH_CHROME);
	        	cleanNjkResult();
        	}
        });

		hBoxSave.getChildren().addAll(buttonBefore, buttonSave, buttonNext);

		VBox vBoxButton = new VBox(5);
		vBoxButton.getChildren().addAll(hBoxUrl, HBoxTask, HBoxComboBox, textAreaTypeComment, textFieldNote, textAreaContents, hBoxSave);

		borderPane.setCenter(vBoxButton);

		return borderPane;
	}

	private void cleanNjkResult() {
		comboBoxQuality.getSelectionModel().select(0);
		comboBoxPurpose.getSelectionModel().select(0);
		textAreaTypeComment.setText(" ");
		textFieldNote.setText(" ");
		textAreaContents.setText(" ");
	}

	private void updateComboBoxPurpose() {
         
             switch(comboBoxQuality.getSelectionModel().getSelectedIndex()) {
             case 1:
             case 2:
             	 comboBoxPurpose.setItems(veryHi);
                 break;
             case 3:
             case 4:
            	 comboBoxPurpose.setItems(hi);
                 break;
             case 5:
             case 6:
            	 comboBoxPurpose.setItems(low);
                 break;
             case 7:
            	 comboBoxPurpose.setItems(auto);
                 break;
             case 8:
            	 comboBoxPurpose.setItems(haking);
                 break;
             }
             comboBoxPurpose.getSelectionModel().select(0);
	}

	private void dailoagQualityControl(Stage primaryStage, List<TargetQC> targetList) {
		final Stage dialog = new Stage();
		// dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.initOwner(primaryStage);
		VBox dialogVbox = new VBox(20);
		dialogVbox.getChildren().addAll(borderPaneQualityControl(targetList, dialog));
		Scene dialogScene = new Scene(dialogVbox);
		dialog.setScene(dialogScene);
		dialog.show();
	}

	private BorderPane borderPaneQualityControl(List<TargetQC> targetList, Stage dialog) {
		BorderPane borderPane = new BorderPane();
		borderPane.setPadding(new Insets(20, 10, 10, 10));

		comboBoxQC = new ComboBox<String>(checkList);
		comboBoxQC.setMinWidth(80);
		textAreaComment = new TextArea();
		HBox HBox6 = new HBox(comboBoxQC, textAreaComment);
		HBox6.setPadding(new Insets(5, 10, 5, 10));

		target = targetList.get(0);
		vBox = getVBoxResult(target);
		cleanQCResult(target);

		eventSearch(target.getUrl(), PATH_CHROME);
		borderPane.setCenter(vBox);

		HBox hBoxSave = new HBox();
		Button buttonSave = new Button("저   장 [ 1 / 30 ]");
		buttonSave.setMaxWidth(Double.MAX_VALUE);
		buttonSave.prefWidthProperty().bind(Bindings.divide(hBoxSave.widthProperty(), 1.0));

		Button buttonBefore = new Button("<");
		buttonBefore.setPrefWidth(250);
		Button buttonNext = new Button(">");
		buttonNext.setPrefWidth(250);

		buttonSave.setOnAction(e -> {
			String qc = comboBoxQC.getValue();
			String comment = textAreaComment.getText();
			// comment = comment.replace("\n", "\\n");
			target.setQc(qc);
			target.setComment(comment);
			target.setCommit(".");
			// 저장절차
			apachePOITarget.writeTarget(selectedFile, target);
			buttonNext.fire();
			cleanQCResult(target);
		});

		buttonBefore.setOnAction(e -> {
			if (targetList != null) {
				count--;
				if (count <= 0) {
					count = 1;
					buttonBefore.setDisable(true);
				} else {
					buttonSave.setText("저   장 [ " + count + " / 30 ]");
					target = targetList.get(count - 1);

					buttonBefore.setDisable(false);
					buttonNext.setDisable(false);
					vBox = getVBoxResult(target);
					borderPane.setCenter(vBox);
					eventSearch(target.getUrl(), PATH_CHROME);
					cleanQCResult(target);
				}
			}
		});

		buttonNext.setOnAction(e -> {
			if (targetList != null) {
				count++;
				if (count > 29) {
					count = 30;
					buttonNext.setDisable(true);
				} else {
					buttonBefore.setDisable(false);
					buttonNext.setDisable(false);
				}
				buttonSave.setText("저   장 [ " + count + " / 30 ]");
				target = targetList.get(count - 1);
				vBox = getVBoxResult(target);
				borderPane.setCenter(vBox);
				eventSearch(target.getUrl(), PATH_CHROME);
				cleanQCResult(target);
			}
		});

		hBoxSave.getChildren().addAll(buttonBefore, buttonSave, buttonNext);

		VBox vBoxButton = new VBox(5);
		vBoxButton.getChildren().addAll(HBox6, hBoxSave);

		borderPane.setBottom(vBoxButton);

		return borderPane;
	}

	private void cleanQCResult(TargetQC target) {
		comboBoxQC.setValue(target.getQc());
		textAreaComment.setText(target.getComment());
	}

	private VBox getVBoxResult(TargetQC target) {

		String url = target.getUrl();
		String scope = target.getScope();
		String donotQc = target.getDonotQc();
		String spam = target.getSpam();
		String quality = target.getQuality();
		String purpose = target.getPurpose();
		String subSpam = target.getSubSpam();
		String subUrl = target.getSubUrl();
		String worker = target.getName();

		Button buttonExplorer = new Button("  E  ");
		buttonExplorer.setPrefWidth(50);
		buttonExplorer.setOnAction(e -> eventSearch(url, PATH_EXPLORE));
		Label labelURL = new Label(url);
		labelURL.setStyle("-fx-font-weight: bold");
		labelURL.setPadding(new Insets(0, 0, 0, 10));
		labelURL.setFont(new Font("Godic", 16));
		labelURL.setOnMouseClicked(e -> {
			eventSearch(url, PATH_CHROME);
		});

		labelURL.setWrapText(true);
		HBox HBox0 = new HBox(buttonExplorer, labelURL);

		Label labelScope = new Label("- Scope : ");
		labelScope.setFont(new Font("Godic", 16));
		labelScope.setMinWidth(80);
		Label labelScopeText = new Label(scope);
		labelScopeText.setFont(new Font("Godic", 16));
		labelScopeText.setMinWidth(180);

		Button buttonScoredPage = new Button("Stroed Page ");
		buttonScoredPage.setOnMouseClicked(e -> {
			eventStoredPage(url, PATH_CHROME);
		});
		// buttonScoredPage.setMinWidth(180);

		Button buttonResult = new Button("검 색  결 과");
		buttonResult.setOnMouseClicked(e -> {
			String clipText = url;
			if (!clipText.startsWith(File_SEARCH)) {
				clipText = File_SEARCH + clipText;
			}
			StringSelection stringSelection = new StringSelection(clipText);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
		});

		HBox HBox1 = new HBox(labelScope, labelScopeText, buttonScoredPage, buttonResult);
		HBox1.setPadding(new Insets(0, 0, 0, 0));

		Label labelDoQc = new Label("- 검수불가 : ");
		labelDoQc.setFont(new Font("Godic", 16));
		labelDoQc.setMinWidth(100);
		Label labelDoQcText = new Label(donotQc);
		labelDoQcText.setFont(new Font("Godic", 18));
		labelDoQcText.setMinWidth(150);

		Label labelSpam = new Label("- 스팸판정 : ");
		labelSpam.setFont(new Font("Godic", 16));
		labelSpam.setMinWidth(100);

		Label labelSpamText = new Label(spam);
		labelSpamText.setFont(new Font("Godic", 18));
		labelSpamText.setMinWidth(100);
		HBox HBox2 = new HBox(labelDoQc, labelDoQcText, labelSpam, labelSpamText);
		// HBox2.setStyle("-fx-background-color:POWDERBLUE"); // NOI18N
		HBox2.setPadding(new Insets(0, 0, 0, 0));

		Label labelQuelty = new Label("- 품     질 : ");
		labelQuelty.setFont(new Font("Godic", 16));
		labelQuelty.setMinWidth(100);
		Label labelQueltyText = new Label(quality);
		labelQueltyText.setFont(new Font("Godic", 18));
		labelQueltyText.setMinWidth(150);

		Label labelPurpose = new Label("- 목     적 : ");
		labelPurpose.setFont(new Font("Godic", 16));
		labelPurpose.setMinWidth(100);

		Label labelPurposetyText = new Label(purpose);
		labelPurposetyText.setFont(new Font("Godic", 18));
		labelPurposetyText.setMinWidth(100);
		HBox HBox3 = new HBox(labelQuelty, labelQueltyText, labelPurpose, labelPurposetyText);

		Label labelSubSpam = new Label("- 하위일부스팸판정 : ");
		labelSubSpam.setFont(new Font("Godic", 16));
		labelSubSpam.setMinWidth(100);

		Label labelSubSpamText = new Label(subSpam);
		labelSubSpamText.setFont(new Font("Godic", 18));
		labelSubSpamText.setMinWidth(100);

		Label workingUser = new Label("- 작 업 자 : " + worker);
		workingUser.setFont(new Font("Godic", 16));
		workingUser.setMinWidth(100);

		HBox HBox4 = new HBox(labelSubSpam, labelSubSpamText, workingUser);

		Label labelSubSpamURL = new Label("- 하위 URL : ");
		labelSubSpamURL.setFont(new Font("Godic", 16));
		labelSubSpamURL.setMinWidth(100);
		Label labelSubSpamURLText = new Label(subUrl);
		labelSubSpamURLText.setFont(new Font("Godic", 18));
		labelSubSpamURLText.setMinWidth(100);
		labelSubSpamURLText.setWrapText(true);
		labelSubSpamURLText.setOnMouseClicked(e -> {
			StringSelection stringSelection = new StringSelection(subUrl);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			clipboard.setContents(stringSelection, null);
			eventSearch(subUrl, PATH_CHROME);
		});
		HBox HBox5 = new HBox(labelSubSpamURL, labelSubSpamURLText);

		Label label6 = new Label("  ");
		label6.setFont(new Font("Godic", 18));
		Label label7 = new Label("  ");
		label7.setFont(new Font("Godic", 18));
		Label label8 = new Label("  ");
		label8.setFont(new Font("Godic", 18));

		HBox HBox6 = new HBox(label6);
		HBox HBox7 = new HBox(label7);
		HBox HBox8 = new HBox(label8);

		VBox vBox = new VBox(5);

		vBox.setStyle(cssLayout);
		if (scope.trim().equals("1(domain)") || scope.trim().equals("2(host)")) {
			vBox.getChildren().addAll(HBox0, HBox1, HBox2, HBox3, HBox4, HBox5);
		} else {
			vBox.getChildren().addAll(HBox0, HBox6, HBox1, HBox2, HBox7, HBox8);
		}
		return vBox;
	}

	private FileChooser getFileChooser() {
		FileChooser fileChooser = new FileChooser();
		// E xtention filter
		FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("WebSpam files (*.txt, *.xlsx)",
				"*.txt", "*.xlsx");
		fileChooser.getExtensionFilters().add(extentionFilter);

		// Set to user directory or go to default if cannot access
		String userDirectoryString = HOME_DIR + File.separator + "Downloads";
		// System.err.println(userDirectoryString);
		File userDirectory = new File(userDirectoryString);
		if (!userDirectory.canRead()) {
			userDirectory = new File("c:/");
		}
		fileChooser.setInitialDirectory(userDirectory);

		return fileChooser;
	}

	private void countSpamWord(TextField textFieldWordCount, WordSearch wordSearch) {
		String clipText = getClipboardContents();
		textFieldWordCount.setText(wordSearch.CountWord(clipText));
	}

	private void afterClean(TextField textField) {
		// 1초간 중지시킨다.(단위 : 밀리세컨드)
		Runnable runnable = () -> {
			try {
				Thread.sleep(1000 * 20);
				Platform.runLater(() -> {
					textField.clear();
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		};
		Thread t = new Thread(runnable);
		t.start();
	}

	private List<String> readFileToList(File selectedFile) {
		List<String> stringList = new ArrayList<String>();
		BufferedReader reader;
		try {
			reader // = new BufferedReader(new FileReader(selectedFile));
					= new BufferedReader(new InputStreamReader(new FileInputStream(selectedFile), "utf-8"));

			String line = reader.readLine();
			while (line != null) {
				stringList.add(line);
				line = reader.readLine();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringList;
	}

	private void eventStoredPage(TextField textField, String browser) {

		String previous = "";
		String clipText = getClipboardContents();
		if (clipText == null)
			return;
		clipText = eraseFileSearchText(clipText);
		// System.out.println(clipText);
		if (!clipText.startsWith(SPAMMGR)) {
			// String sbuURL = URLEncoder.encode(clipText, "UTF-8");
			clipText = SPAMMGR + clipText;
		}
		textField.setText(clipText);
		// Boolean isURL = CheckUrl.isURI(clipText);
		if (!clipText.equals(previous)) {
			// buttonGoogle.setTooltip(new Tooltip(clipText));

			try {
				// System.out.println(clipText);
				// clipText = clipText.replace("://", "%3A%2F%2F");
				// clipText = clipText.replace("/", "%2");
				// clipText = clipText.replace("?", "%3F");
				// clipText = clipText.replace("=", "%3D");
				clipText = clipText.replace("&", "%26");
				// Desktop.getDesktop().browse(URI.create(clipText));
				String[] b = { browser, clipText };
				Runtime.getRuntime().exec(b);
				// System.out.println(p);

			} catch (Exception e) {
				e.printStackTrace();
			}

			previous = clipText;
		}
	}

	private void eventStoredPage(String url, String browser) {

		String previous = "";
		String clipText = url;
		if (!clipText.startsWith(SPAMMGR)) {
			// try {
			// String sbuURL = URLEncoder.encode(clipText, "UTF-8");
			// System.out.println(sbuURL);
			// } catch (UnsupportedEncodingException e) {
			// e.printStackTrace();
			// }
			clipText = SPAMMGR + clipText;
		}
		// Boolean isURL = CheckUrl.isURI(clipText);
		if (!clipText.equals(previous)) {
			// buttonGoogle.setTooltip(new Tooltip(clipText));
			try {
				// System.out.println(clipText);
				// clipText = clipText.replace("://", "%3A%2F%2F");
				// clipText = clipText.replace("/", "%2");
				// clipText = clipText.replace("?", "%3F");
				// clipText = clipText.replace("=", "%3D");
				clipText = clipText.replace("&", "%26");
				// String command = URLEncoder.encode(clipText, "utf-8");
				// Desktop.getDesktop().browse(URI.create(clipText));
				String[] b = { browser, clipText };
				Runtime.getRuntime().exec(b);
			} catch (Exception e) {
				e.printStackTrace();
			}

			previous = clipText;
		}
	}

	private void eventSearchResult(TextField textFieldStoredPage) {
		String clipText = getClipboardContents();
		if (!clipText.startsWith(File_SEARCH)) {
			clipText = File_SEARCH + clipText;
		}
		textFieldStoredPage.setText(clipText);
		StringSelection stringSelection = new StringSelection(clipText);
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(stringSelection, null);
	}

	@SuppressWarnings("unused")
	private void eventGoogleSearch(TextField textFieldSearch) {
		String previous = "";
		String clipText = getClipboardContents();
		// textFieldSearch.setText(clipText);
		if (clipText == null)
			return;
		clipText = eraseFileSearchText(clipText);
		textFieldSearch.setText(clipText);
		if (!clipText.startsWith("http")) {
			clipText = "http://" + clipText;
		}
		// Boolean isURL = CheckUrl.isURI(clipText);
		if (!clipText.equals(previous)) {
			// buttonGoogle.setTooltip(new Tooltip(clipText));
			try {
				Desktop.getDesktop().browse(URI.create(clipText));
			} catch (Exception e) {
				e.printStackTrace();
			}

			previous = clipText;
		}
	}

	private String eraseFileSearchText(String clipText) {
		if (clipText.startsWith(File_SEARCH)) {
			clipText = clipText.substring(File_SEARCH.length());
		}
		return clipText;
	}

	Process process = null;

	@SuppressWarnings("unused")
	private void eventExplorerSearch(TextField textFieldSearch, String browser) {

		String previous = "";
		String clipText = getClipboardContents();
		textFieldSearch.setText(clipText);
		if (clipText == null)
			return;
		clipText = eraseFileSearchText(clipText);
		textFieldSearch.setText(clipText);
		if (!clipText.startsWith("http")) {
			clipText = "http://" + clipText;
		}
		// Boolean isURL = CheckUrl.isURI(clipText);
		if (!clipText.equals(previous)) {
			try {
				String[] b = { browser, clipText };
				if (process != null) {
					process.destroy();
				}
				process = Runtime.getRuntime().exec(b);
				// System.out.println(p);
			} catch (Exception exc) {
				exc.printStackTrace();
			}
			previous = clipText;
		}
	}

	private void eventSearch(TextField textFieldSearch, String browser) {

		String previous = "";
		String clipText = getClipboardContents();
		textFieldSearch.setText(clipText);
		if (clipText == null)
			return;
		clipText = eraseFileSearchText(clipText);
		textFieldSearch.setText(clipText);
		if (!clipText.startsWith("http")) {
			clipText = "http://" + clipText;
		}
		// Boolean isURL = CheckUrl.isURI(clipText);
		if (!clipText.equals(previous)) {
			try {
				String[] b = { browser, clipText };
				if (process != null) {
					process.destroyForcibly();
					// p.destroy();

					// String cmd = "taskkill /F /IM iexplore.exe";
					// Runtime.getRuntime().exec(cmd);
				}
				process = Runtime.getRuntime().exec(b);

			} catch (Exception exc) {
				exc.printStackTrace();
			}
			previous = clipText;
		}
	}

	private void eventSearch(String url, String browser) {

		String previous = "";
		String clipText = url;
		if (clipText == null)
			return;
		if (!clipText.startsWith("http")) {
			clipText = "http://" + clipText;
		}
		// Boolean isURL = CheckUrl.isURI(clipText);
		if (!clipText.equals(previous)) {
			try {
				String[] b = { browser, clipText };
				if (process != null) {
					process.destroyForcibly();
				}
				process = Runtime.getRuntime().exec(b);

			} catch (Exception exc) {
				exc.printStackTrace();
			}
			previous = clipText;
		}
	}

	public static void main(String[] args) {
		Application.launch(args);
	}

	/**
	 * CNTL + C 발생시 문자열을 가지고 온다
	 * 
	 * @param
	 * @return 클립보드의 문자열
	 */
	protected String getClipboardContents() {
		String text = "";
		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
			try {
				Transferable contents = clipboard.getContents(this);
				text = (String) contents.getTransferData(DataFlavor.stringFlavor);
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
			}

		}
		return text.trim();
	}

	public void writeTextFromClipboard(String s) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = new StringSelection(s);
		clipboard.setContents(transferable, null);
	}

	@SuppressWarnings("unused")
	private String getQuality(String s) {

		String quality;
		int index = Integer.valueOf(s);

		switch (index) {
		case 0:
			quality = "0.국가기관, 학교, 위키피다아 급";
			break;
		case 1:
			quality = "1.대기업,대형쇼핑몰,대형서비스/언론급";
			break;
		case 2:
			quality = "2.소기업,소형쇼핑몰,소형서비스/언론급";
			break;
		case 3:
			quality = "3.개인,소규모 커뮤니티 급";
			break;
		case 4:
			quality = "4.정보미약";
			break;
		case 5:
			quality = "5.복사물,도배,검색 방해, 도매인 선점";
			break;
		case 6:
			quality = "6.자동검색/검색어 어뷰징";
			break;
		case 7:
			quality = "7.링크 오뷰징";
			break;
		case 8:
			quality = "8.악성 소프트웨어 설치";
			break;
		case 9:
			quality = "9.기타스펨등급";
			break;

		default:
			quality = "";
			break;
		}
		return quality;
	}

	@SuppressWarnings("unused")
	private String getPurpose(String s) {

		String quality;
		int index = Integer.valueOf(s);

		switch (index) {
		case 0:
			quality = "0.기관/기업/단체 소게";
			break;
		case 1:
			quality = "1.정보제공 (방송,신문,잡지,백과사전 )";
			break;
		case 2:
			quality = "2.정보제공, 취미, 친목(개인블로그 송형단체 등)";
			break;
		case 3:
			quality = "3.쇼핑몰";
			break;
		case 4:
			quality = "4.기타 정상 목적";
			break;
		case 5:
			quality = "5.광고 목적(보험,중고차,블로그,쇼핑몰등)";
			break;
		case 6:
			quality = "6.불법 다운로드, 불법 컨텐츠 링크";
			break;
		case 7:
			quality = "7.음란물, 도박";
			break;
		case 8:
			quality = "8.피싱 등 범죄 목적(가짜 은행등)";
			break;
		case 9:
			quality = "9.기타 불량한 목적";
			break;

		default:
			quality = "";
			break;
		}
		return quality;
	}
}
