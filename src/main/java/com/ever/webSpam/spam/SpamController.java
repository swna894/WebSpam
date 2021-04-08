package com.ever.webSpam.spam;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ever.webSpam.category.SpamCategory;
import com.ever.webSpam.category.SpamCategoryController;
import com.ever.webSpam.category.SpamCategoryRepository;
import com.ever.webSpam.cressQc.CrossQcController;
import com.ever.webSpam.feedBack.FeedBackExcel;
import com.ever.webSpam.io.ExcelManual;
import com.ever.webSpam.manual.Manual;
import com.ever.webSpam.manual.ManualRepository;
import com.ever.webSpam.manual.RestManualRepository;
import com.ever.webSpam.question.Question;
import com.ever.webSpam.question.QuestionController;
import com.ever.webSpam.question.QuestionRepository;
import com.ever.webSpam.review.ReviewController;
import com.ever.webSpam.utility.Constant;
import com.ever.webSpam.utility.VerifySite;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import net.rgielen.fxweaver.core.FxmlView;

@Component
@FxmlView("main-spam.fxml")
public class SpamController implements Initializable, Constant {

	Logger LOG = LoggerFactory.getLogger(SpamController.class);

	private File file;
	private String path;

	private Process process = null;

	private String clipText;
	private List<Manual> manualList;
	private List<SpamCategory> spamCategoryList;

	// private Manual selectedManual;

	@Autowired
	private RestSpamRepository restSpamRepository;

	@Autowired
	private RestManualRepository restManualRepository;

	@Autowired
	ReviewController reviewController;

//	@Autowired
//	private JsonUtil jsonUtil;

	@Autowired
	private ReviewController feedBackController;

	@SuppressWarnings("rawtypes")
	@Autowired
	private CrossQcController crossQcController;

	@Autowired
	ManualRepository manualRepository;

	@Autowired
	FeedBackExcel feedBackExcel;

	@Autowired
	ExcelManual excelManual;

	@Autowired
	SpamCategoryController spamCategoryController;

	@Autowired
	SpamCategoryRepository spamCategoryRepository;
	
	@Autowired
	QuestionController questionController;
	
	@Autowired
	VerifySite verifySite;

	@Autowired
	QuestionRepository questionRepository;
	
	@FXML
	private BorderPane borderPane;

	@FXML
	SplitPane splitPane;

	@FXML
	HBox hBoxBottom;

	@FXML
	private Button buttonFeedBack;

	@FXML
	private TextField textFieldGoogle;

	@FXML
	private TextField textFieldExplorer;

	@FXML
	private TextField textFieldResult;

	@FXML
	private TextField textFieldCategory;

	@FXML
	private TextField textFieldHiddenText;
	
	@FXML
	private TextField textFieltInspectResult;
	
	@FXML
	private Button buttonInstprctionResult;
	
	@FXML
	private Button buttonGoogle;

	@FXML
	private Button buttonExplorer;

	@FXML
	private Button buttonResult;

	@FXML
	private Button buttonCrossQc;

	@FXML
	private Button buttonReview;

	@FXML
	private Button buttonCategory;

	@FXML
	private Button buttonSave;

	@FXML
	private Button buttonDel;

	@FXML
	private Button buttonSearch;
	
	@FXML
	private Button buttonHiddenText;

	@FXML
	private Button buttonQuestion;

	@FXML
	private Button buttonQuestionList;


	
	@FXML
	private TableView<Spam> checkTableView;

	@FXML
	private TableView<Manual> manualTableView;

	@FXML
	void actionButtonHiddenText(ActionEvent event) {

	}

	@FXML
	void actionButtonSave(ActionEvent event) {
		buttonDel.setDisable(true);
		showEditingWindow(manualTableView.getScene().getWindow(), (String) "● ", newValue -> {
			Manual item = new Manual();
			item.setDoc(newValue);
			if (isDBMS) {
				Manual dbManual = restManualRepository.put(item);
				manualList.add(dbManual);
				manualTableView.getItems().clear();
				manualTableView.getItems().addAll(manualList);
				// jsonUtil.saveJsonToManualFile(manualList);
			} else {
				saveManualAndShowList(item);
			}
		});
		textFieldCategory.clear();
	}

	private void saveManualAndShowList(Manual manual) {
		if (manual != null) {
			manualRepository.save(manual);
		}
		manualList = manualRepository.findAll();
		// String jsonManual = jsonUtil.saveJsonToManualFile(manualList);
		Boolean result = excelManual.writeManualList(manualList);
		if (result) {
			manualTableView.getItems().clear();
			manualTableView.getItems().addAll(manualList);
		}
	}

	@FXML
	void actionButtonCategory(ActionEvent event) {
		spamCategoryController.show();
	}

	@FXML
	void actionButtonDel(ActionEvent event) {
		Manual manual = manualTableView.getSelectionModel().getSelectedItem();
		// restManualRepository.delete(manual);
		manualRepository.delete(manual);
		saveManualAndShowList(null);
		textFieldCategory.clear();
	}

	private void initalDoc() {
		if (isDBMS) {
			manualList = restManualRepository.findByAll(null);
		} else {
			// if (new File(JSON_FILE).exists()) {
			// manualList = jsonUtil.convertJsonToManualList(JSON_FILE);
			manualList = excelManual.readManualList();
			manualRepository.saveAll(manualList);
		}
		// convertBeanToJson(docList);
		if (manualList != null) {
			manualTableView.getItems().clear();
			manualTableView.getItems().addAll(manualList);
		}
	}

	@FXML
	void actionButtonInspectResullt(ActionEvent event) {
		inspectResult();
	}

	
	@FXML
	void actionButtonSearch(ActionEvent event) {
		// buttonDel.setDisable(true);
		String search = textFieldCategory.getText().trim();
		List<Manual> list = manualRepository.findByDocContaining(search);
		// List<Manual> list = manualList.stream().filter(item ->
		// item.getDoc().contains(search))
		// .collect(Collectors.toList());
		manualTableView.getItems().clear();
		manualTableView.getItems().addAll(list);
		textFieldCategory.clear();
	}

	@FXML
	void actionButtonQuestionList(ActionEvent event) {
	
		questionController.open();
	}

	@FXML
	void actionButtonQuestion(ActionEvent event) {
		String uri = textFieldCategory.getText().trim();
		List<Question> questionList = excelManual.readQuestionList();;
		if (!uri.equals("")) {
			Question question = new Question();
			question.setUri(uri);
			question.setDate(LocalDate.now().toString());
			questionList.add(question);
			excelManual.writeQuestionList(questionList);
			textFieldCategory.clear();
		}

	}

	@FXML
	void actionButtonCrossQc(ActionEvent event) {
		FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("*.xlsx", "*.xlsx");
		FileChooser fileChooser = new FileChooser();
		String userDirectoryString = HOME_DIR + File.separator + "Downloads";
		// System.err.println(userDirectoryString);
		File userDirectory = new File(userDirectoryString);
		if (!userDirectory.canRead()) {
			userDirectory = new File("c:/");
		}
		fileChooser.setInitialDirectory(userDirectory);
		
		fileChooser.getExtensionFilters().add(extentionFilter);
		file = fileChooser.showOpenDialog(null);
		if (file != null)
			crossQcController.start(file);

	}

	@FXML
	void actionButtonReview(ActionEvent event) {
		feedBackController.open();
	}

	@FXML
	void actionButtonExplorer(ActionEvent event) {

	}

	@FXML
	void actionButtonGoogle(ActionEvent event) {

	}
	
	@FXML
	void actionTextFieldInspectResult(ActionEvent event) {
		inspectResult();
	}
	
	private void inspectResult() {
		clipText = verifySite.getClipboard();
		if(!clipText.isEmpty())
		{
			textFieltInspectResult.setText(clipText);
			verifySite.eventInspectReult(textFieltInspectResult.getText());
		}
	}

	@FXML
	void actionButtonResult(ActionEvent event) {

	}

	@FXML
	void actionTextFieldCrossQc(ActionEvent event) {

	}

	@FXML
	void actionTextFieldResult(ActionEvent event) {
		verifySite.eventSearchResult(textFieldResult.getText());
	}

	@FXML
	void actionTextFieldExplorer(ActionEvent event) {
		writeTextFromClipboard(textFieldGoogle.getText());
	}

	@FXML
	void actionTextFieldGoogle(ActionEvent event) {
		writeTextFromClipboard(textFieldExplorer.getText());
	}

	@FXML
	void actionTextFieldHiddenText(ActionEvent event) {
		// writeTextFromClipboard(textFieldHiddenText.getText());
	}

	@FXML
	void mouseClickedCrossQC(MouseEvent event) {
		createQcFile();
	}

	@FXML
	void mouseClickedExplorer(MouseEvent event) {
		openNavigate(textFieldExplorer, verifySite.getExplore());
		textFieldClean(textFieldExplorer);

	}
	
	@FXML
	void mouseClickedInspectResult(MouseEvent event) {
		inspectResult();

	}


	@FXML
	void mouseClickedGoogle(MouseEvent event) {
		final Node source = (Node) event.getSource();
		final Stage stage = (Stage) source.getScene().getWindow();
		openNavigate(textFieldGoogle, verifySite.getChrome());
		textFieldClean(textFieldGoogle);
		isSpamList();
		isSpamCategory(stage);
	}

	@FXML
	void mouseClickedHiddenText(MouseEvent event) {
		clipText = verifySite.getClipboard();
		if (clipText.startsWith(verifySite.getPrefix())) {
			clipText = clipText.substring(verifySite.getPrefix().length());
		}
		textFieldHiddenText.setText(clipText);
		verifySite.hiddenText(clipText);
		textFieldClean(textFieldHiddenText);
	}

//	private void hiddenText(String url) {
//		String prefix = "http://spamdepot.navercorp.com/html/view_doc_text.html?search=";
//		// clipText = getClipboardContents();
//		if (url.startsWith(PREFIX_SEARCH)) {
//			url = url.substring(PREFIX_SEARCH.length());
//		}
//		textFieldHiddenText.setText(url);
//		try {
//			String enStr = URLEncoder.encode(clipText, "UTF-8");
//			startBrowser(prefix + enStr, PATH_CHROME);
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
//		textFieldClean(textFieldHiddenText);
//	}

//	@PostConstruct
//	public void init() {
//		mapper = new ObjectMapper();
//		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
//		mapper.setSerializationInclusion(Include.NON_NULL);
//
//	}
	private void isSpamCategory(Stage stage) {
		List<SpamCategory> spamCategoryList = excelManual.readCategoryList();
		List<SpamCategory> spamList = spamCategoryList.stream()
				.filter(item -> textFieldGoogle.getText().contains(item.getUri())).collect(Collectors.toList());

		if (spamList.size() > 0) {
			String message = convertString(spamList);
			MyAlertDialog(message, stage);
		}
	}

	private String convertString(List<SpamCategory> spamList) {
		String message = "";

		for (SpamCategory spam : spamList) {
			message = message + "\nURL = " + textFieldGoogle.getText() + " -> " + spam.getUri() + "\n";

			if (spam.getLookMain()) {
				message = message + "- 서메 ";
			}

			if (spam.getLookCh()) {
				message = message + "- 채널 ";
			}

			if (spam.getLookCont()) {
				message = message + "- 컨텐 ";
			}

			if (spam.getLookList()) {
				message = message + "- 컨리 ";
			}

			if (spam.getHamLow()) {
				message = message + "- 정저";
			}

			if (spam.getHam()) {
				message = message + "- 정상 ";
			}

			if (spam.getSpamAd()) {
				message = message + "- 비광 ";
			}

			if (spam.getSpamText()) {
				message = message + "- 비텍 ";
			}
			if (spam.getSpamText()) {
				message = message + "- 비텍 ";
			}

			if (spam.getSpamIllegal()) {
				message = message + "- 불사 ";
			}
			message = message + "\n\n ";
		}
		return message;
	}

	private void MyAlertDialog(String message, Stage stage) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Input Information");
		alert.setHeaderText(null);
		alert.initOwner(stage);
		alert.setContentText(message);
		closeAlert(alert);
		alert.showAndWait();

	}
	
	private void MyAlertDialog(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Input Information");
		alert.setHeaderText(null);
		alert.setContentText(message);
		closeAlert(alert);
		alert.showAndWait();

	}

	private void isSpamList() {
		try {
			if (isDBMS) {
				Map<String, Object> paramater = new HashMap<String, Object>();
				paramater.put("uri", (new URI(clipText).getHost()));
				List<Spam> spamList = restSpamRepository.findByUri(paramater);
				checkTableView.getItems().clear();
				checkTableView.getItems().addAll(reform(spamList));
			} else {
				List<Spam> spamList = reviewController.getSpamList();

				if (spamList != null && spamList.size() > 0) {
					String uri = new URI(clipText).getHost();
					List<Spam> filterList = spamList.stream().filter(item -> item.getUri().contains(uri))
							// .sorted(Comparator.comparing(Spam::getComment))
							.collect(Collectors.toList());
					checkTableView.getItems().clear();
					checkTableView.getItems().addAll(filterList);
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private List<Spam> reform(List<Spam> spamList) {
		// List<Spam> reformList = new ArrayList<>();
		if (spamList == null || spamList.size() < 1) {
			spamList = new ArrayList<Spam>();
			Spam spam = new Spam();
			spam.setComment("No List");
			spamList.add(spam);
			return spamList;
		}

		for (Spam spam : spamList) {
			String s = "";
			if (spam.getLookMain()) {
				s = " / 서메";
			}
			if (spam.getLookCh()) {
				s = " / 채메";
			}
			if (spam.getLookList()) {
				s = s + " / 컨리";
			}
			if (spam.getLookCont()) {
				s = s + " / 컨텐";
			}
			if (spam.getHam()) {
				s = s + " / 정상";
			}
			if (spam.getHamLow()) {
				s = s + " / 정저";
			}
			if (spam.getSpamAd()) {
				s = s + " / 비광";
			}
			if (spam.getSpamText()) {
				s = s + " / 비텍";
			}
			if (spam.getSpamRedir()) {
				s = s + "/ 스리";
			}
			if (spam.getSpamMalware()) {
				s = s + "/ 악소";
			}
			if (spam.getSpamCopy()) {
				s = s + "/ 저위";
			}
			if (spam.getSpamPorn()) {
				s = s + "/ 음란";
			}
			if (spam.getSpamDecep()) {
				s = s + "/ 기만";
			}
			if (spam.getSpamManip()) {
				s = s + "/ 웹조";
			}
			if (spam.getSpamIllegal()) {
				s = s + "/ 불사 ";
			}
			if (spam.getNotCheck().equals("검수불가")) {
				s = s + " / 검수불가 ";
			}
			spam.setComment(s);
		}

		return spamList;
	}

	@FXML
	void mouseClickedResult(MouseEvent event) {
		String uri = verifySite.getClipboard();
		if (!uri.startsWith(verifySite.getPrefix())) {
			uri = verifySite.getPrefix() + uri;
		}
		textFieldResult.setText(uri);
		verifySite.eventSearchResult(textFieldResult.getText().trim());
		textFieldClean(textFieldResult);
	}

	@FXML
	void keyPressedTextFieldCategoryHandler(KeyEvent event) {

		if (event.getCode().equals(KeyCode.ENTER)) {
			buttonSearch.fire();
		}

	}

	@FXML
	void mouseClickedTextFieldCategory(MouseEvent event) {
		if (event.getButton().equals(MouseButton.PRIMARY)) {
			if (event.getClickCount() == 2) {
				clipText = verifySite.getClipboard();
				textFieldCategory.setText(clipText);
				// verifySite.hiddenText(clipText);
			}
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		buttonSave.setGraphic(new ImageView(new Image("/images/save.png")));
		buttonSearch.setGraphic(new ImageView(new Image("/images/magnify.png")));
		buttonDel.setGraphic(new ImageView(new Image("/images/delete.png")));
		buttonCrossQc.setGraphic(new ImageView(new Image("/images/cross.png")));
		buttonGoogle.setGraphic(new ImageView(new Image("/images/google.png")));
		buttonExplorer.setGraphic(new ImageView(new Image("/images/explorer.png")));
		buttonResult.setGraphic(new ImageView(new Image("/images/magnify.png")));
		buttonHiddenText.setGraphic(new ImageView(new Image("/images/bluelist.png")));
		buttonReview.setGraphic(new ImageView(new Image("/images/review.png")));
		buttonCategory.setGraphic(new ImageView(new Image("/images/category.png")));
		buttonQuestion.setGraphic(new ImageView(new Image("/images/question_16.png")));
		buttonQuestionList.setGraphic(new ImageView(new Image("/images/question_16.png")));
		buttonInstprctionResult.setGraphic(new ImageView(new Image("/images/all.png")));

		textFieldGoogle.setPromptText("Goole");
		textFieldExplorer.setPromptText("Explorer");
		textFieldHiddenText.setPromptText("Crawled Text");

		initialCheckTableView();
		initialManualTableView();
		if (isFile) {
			initalDoc();
		} else {
			borderPane.getChildren().remove(splitPane);
			borderPane.getChildren().remove(hBoxBottom);

		}
		spamCategoryList = spamCategoryRepository.findAllByOrderByUriAsc();
	}

	private void initialManualTableView() {
		TableColumn<Manual, String> columnContent = createEditColumn("메    모", Manual::docProperty);
		columnContent.setStyle("-fx-alignment: CENTER-LEFT;  -fx-font-weight: bold; -fx-font-size:14px;");
		manualTableView.setEditable(true);
		manualTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		manualTableView.getColumns().add(columnContent);
		manualTableView.prefWidthProperty().bind(columnContent.widthProperty());

		manualTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				buttonDel.setDisable(false);
				// selectedManual = tableView.getSelectionModel().getSelectedItem();
				// textFieldCategory.setText(selectedManual.getDoc());
			}
		});

	}

	private void initialCheckTableView() {

		TableColumn<Spam, Void> columnButton = createButtonColumn();
		checkTableView.getColumns().add(columnButton);
		checkTableView.setEditable(false);
		checkTableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

		TableColumn<Spam, String> columnUri = createEditColumn("URI", Spam::uriProperty);
		checkTableView.getColumns().add(columnUri);

		TableColumn<Spam, String> columnComment = createEditColumn("결과", Spam::commentProperty);
		checkTableView.getColumns().add(columnComment);
		// checkTableView.prefWidthProperty().bind(columnContent.widthProperty());

		TableColumn<Spam, String> columnName = createEditColumn("이름", Spam::nameProperty);
		checkTableView.getColumns().add(columnName);

		// TableColumn<Spam, String> columnWorkDate = createEditColumn("날자",
		// Spam::workdayProperty);
		// checkTableView.getColumns().add(columnWorkDate);

		columnButton.setPrefWidth(75);
		columnButton.setMaxWidth(75);
		columnButton.setMinWidth(75);
		// columnName.setMinWidth(50);
		// columnButton.setMinWidth(50);
		columnName.setMaxWidth(50);
		columnName.setMinWidth(50);
		// columnWorkDate.setMinWidth(50);
		// columnButton.prefWidthProperty().bind(checkTableView.widthProperty().multiply(0.1));
		columnUri.prefWidthProperty().bind(checkTableView.widthProperty().multiply(0.1));
		columnComment.prefWidthProperty().bind(checkTableView.widthProperty().multiply(0.7));
		columnName.prefWidthProperty().bind(checkTableView.widthProperty().multiply(0.1));
		// columnWorkDate.prefWidthProperty().bind(checkTableView.widthProperty().multiply(0.1));

	}

	public SpamController() {

	}

	public List<String> getSpamListFromDownLoadFile() {
		FileChooser fileChooser = getFileChooser();
		file = fileChooser.showOpenDialog(null);
		List<String> qcList = null;
		if (file != null) {
			path = file.getParent();
			qcList = readFileToList(file);
		}
		return qcList;
	}

	private void createQcFile() {

//		String resultFile = "";
//
//		apachePOIExcel = new ApachePOIExcel();
//
//		List<String> list = getSpamListFromDownLoadFile();
//
//		for (String name : observableListName) {
//			if (name != null && !name.trim().equals("내   역") && resultFile != null && list.size() > 1) {
//				resultFile = apachePOIExcel.write(list, name, file);
//			}
//		}
//
//		if (resultFile != null) {
//			try {
//				Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + resultFile);
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//		}

	}

	private void textFieldClean(TextField textField) {
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

	private void closeAlert(Alert alert) {
		// 1초간 중지시킨다.(단위 : 밀리세컨드)
		Runnable runnable = () -> {
			try {
				Thread.sleep(1000 * 5);
				Platform.runLater(() -> {
					if (alert.isShowing()) {
						Platform.runLater(() -> alert.close());
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		};
		Thread t = new Thread(runnable);
		t.start();
	}

	private List<String> readFileToList(File file) {
		List<String> stringList = new ArrayList<String>();
		BufferedReader reader;
		try {
			reader // = new BufferedReader(new FileReader(selectedFile));
					= new BufferedReader(new InputStreamReader(new FileInputStream(file), "utf-8"));

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

	public FileChooser getFileChooser() {
		FileChooser fileChooser = new FileChooser();
		// Extension filter
		FileChooser.ExtensionFilter extentionFilter = new FileChooser.ExtensionFilter("*.txt, *.xlsx", "*.txt",
				"*.xlsx");
		fileChooser.getExtensionFilters().add(extentionFilter);
		fileChooser.setInitialDirectory(getDowloadsPath());

		return fileChooser;
	}

	private File getDowloadsPath() {
		// Set to user directory or go to default if cannot access
		String userDirectoryString = verifySite.getHome() + File.separator + "Downloads";
		// System.err.println(userDirectoryString);
		File userDirectory = new File(userDirectoryString);
		if (!userDirectory.canRead()) {
			userDirectory = new File("c:/");
		}

		return userDirectory;
	}

	private void openNavigate(TextField textField, String browser) {

		String previous = "";
		clipText = verifySite.getClipboard();
		// setClipbord(clipText);
		if (clipText == null)
			return;

		if (clipText.startsWith(verifySite.getPrefix())) {
			clipText = clipText.substring(verifySite.getPrefix().length());
		}

		textField.setText(clipText);

		if (!clipText.startsWith("http")) {
			clipText = "http://" + clipText;
		}
		// Boolean isURL = CheckUrl.isURI(clipText);
		if (!clipText.equals(previous)) {
			try {
				String[] b = { browser, clipText };
				if (process != null && browser.contains("iexplore")) {
					process.destroyForcibly();
				}
				process = Runtime.getRuntime().exec(b);

			} catch (Exception exc) {
				exc.printStackTrace();
			}
			previous = clipText;
		}
	}

	public void writeTextFromClipboard(String s) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable transferable = new StringSelection(s);
		clipboard.setContents(transferable, null);
	}

//	private void eventSearchResult(TextField textField) {
//		String prefixUrl = "https://search.naver.com/search.naver?sm=top_hty&fbm=0&ie=utf8&query=";
//		String clipText = getClipboardContents();
//		if (!clipText.startsWith(PREFIX_SEARCH)) {
//			clipText = PREFIX_SEARCH + clipText;
//		}
//		textField.setText(clipText);
//		StringSelection stringSelection = new StringSelection(clipText);
//		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		clipboard.setContents(stringSelection, null);
//		clipText = prefixUrl + clipText;
//		try {
//			String[] b = { PATH_CHROME, clipText };
//			process = Runtime.getRuntime().exec(b);
//
//		} catch (Exception exc) {
//			exc.printStackTrace();
//		}
//	}

	private <S, T> TableColumn<S, T> createEditColumn(String head, Function<S, Property<T>> prop) {
		TableColumn<S, T> tableColumn = new TableColumn<>(head.toUpperCase());
		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));
		tableColumn.setSortable(true);
		// set up this column to show an editing window when double-clicked:
		tableColumn.setCellFactory(tc -> {

			TableCell<S, T> cell = new TableCell<S, T>() {
				@Override
				protected void updateItem(T item, boolean empty) {
					super.updateItem(item, empty);
					setText(empty ? null : (String) item);
				}
			};

			cell.setOnMouseClicked(e -> {
				if (e.getClickCount() == 2 && !cell.isEmpty()) {
					showEditingWindow(manualTableView.getScene().getWindow(), (String) cell.getItem(), newValue -> {
						Manual item = manualTableView.getItems().get(cell.getIndex());
						item.setDoc(newValue);
						saveManualAndShowList(item);
						// restManualRepository.put(item);
					});
				}
			});

			return cell;
		});

		makeHeaderWrappable(tableColumn);

		tableColumn.setPrefWidth(80);
		// tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumn.setSortable(true);
		tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<S, T>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<S, T> t) {
			}
		});

		return tableColumn;
	}

	private void showEditingWindow(Window owner, String currentValue, Consumer<String> commitHandler) {
		Stage stage = new Stage();
		stage.initOwner(owner);
		stage.initModality(Modality.APPLICATION_MODAL);

		TextArea textArea = new TextArea(currentValue);

		Button okButton = new Button("OK");
		okButton.setPrefWidth(120);
		okButton.setDefaultButton(true);
		okButton.setOnAction(e -> {
			commitHandler.accept(textArea.getText());
			// showDoc();
			stage.close();
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setPrefWidth(120);
		cancelButton.setCancelButton(true);
		cancelButton.setOnAction(e -> stage.hide());

		HBox buttons = new HBox(5, okButton, cancelButton);
		buttons.setAlignment(Pos.CENTER);
		buttons.setPadding(new Insets(5));

		BorderPane root = new BorderPane(textArea, null, null, buttons, null);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent t) {
				Platform.exit();
				System.exit(0);
			}
		});
	}

	private void makeHeaderWrappable(TableColumn<?, ?> col) {
		Label label = new Label(col.getText());
		label.setStyle("-fx-padding: 8px;");
		label.setWrapText(true);
		label.setAlignment(Pos.CENTER);
		label.setTextAlignment(TextAlignment.CENTER);

		StackPane stack = new StackPane();
		stack.getChildren().add(label);
		stack.prefWidthProperty().bind(col.widthProperty().subtract(5));
		label.prefWidthProperty().bind(stack.prefWidthProperty());
		col.setText(null);
		col.setGraphic(stack);
	}

	private TableColumn<Spam, Void> createButtonColumn() {
		TableColumn<Spam, Void> column = new TableColumn<Spam, Void>("#");
		column.setStyle("-fx-alignment: CENTER;");

		Callback<TableColumn<Spam, Void>, TableCell<Spam, Void>> cellFactory = new Callback<TableColumn<Spam, Void>, TableCell<Spam, Void>>() {
			@Override
			public TableCell<Spam, Void> call(final TableColumn<Spam, Void> param) {

				final TableCell<Spam, Void> cell = new TableCell<Spam, Void>() {

					private final Button google = new Button("G");
					private final Button result = new Button("R");
					private final Button hidden = new Button("T");
					HBox hBox = new HBox(google, result, hidden);
					{
						google.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							getTableView().getItems().get(getIndex()).setSelected(true);
							spam.setSelected(true);
							String url = spam.getUri(); 
							
							verifySite.startBrowser(url, verifySite.getChrome());
							try {
								if(url.contains("http")) {
									verifySite.setClipbord(new URL(spam.getUri()).getHost());
								} else {
									verifySite.setClipbord(spam.getUri());
								}
								
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							checkSpam(url);
						});

						result.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							spam.setSelected(true);
							verifySite.eventSearchResult(spam.getUri());
						});

						hidden.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							spam.setSelected(true);
							verifySite.hiddenText(spam.getUri());
						});
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(hBox);
						}
					}
				};

				cell.setAlignment(Pos.CENTER); // TODO doesn't work !!
				return cell;
			}
		};

		column.setCellFactory(cellFactory);

		return column;
	}

	private void checkSpam(String url) {
		SpamCategory spam = spamCategoryList.stream().filter(item-> item.getUri().contains(url)).findAny().orElse(null);
		
		MyAlertDialog(spam.toString());
		
	}
//	private void startBrowser(String uri, String browser) {
//
//		try {
//			String[] b = { browser, uri };
//			if (process != null && browser.contains("iexplore")) {
//				process.destroyForcibly();
//			}
//			process = Runtime.getRuntime().exec(b);
//
//		} catch (Exception exc) {
//			exc.printStackTrace();
//		}
//	}

//	private void eventSearchResult(String uri) {
//
//		if (!uri.startsWith(PREFIX_SEARCH)) {
//			uri = PREFIX_SEARCH + uri;
//		}
//
//		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		StringSelection stringSelection = new StringSelection(uri);
//		clipboard.setContents(stringSelection, null);
//	}

	/**
	 * CNTL + C 발생시 문자열을 가지고 온다
	 * 
	 * @param
	 * @return 클립보드의 문자열
	 */
//	protected String getClipboardContents() {
//		String text = "";
//		System.setProperty("java.awt.headless", "false");
//		java.awt.datatransfer.Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//		if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
//			try {
//				Transferable contents = clipboard.getContents(this);
//				text = (String) contents.getTransferData(DataFlavor.stringFlavor);
//			} catch (UnsupportedFlavorException | IOException e) {
//				e.printStackTrace();
//			}
//
//		}
//		return text.trim();
//	}

	public String getPath() {
		return path;
	}
}
