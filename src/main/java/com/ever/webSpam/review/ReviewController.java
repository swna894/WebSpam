package com.ever.webSpam.review;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.validator.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ever.webSpam.category.RestSpamCategoryRepository;
import com.ever.webSpam.category.SpamCategory;
import com.ever.webSpam.category.SpamCategoryController;
import com.ever.webSpam.category.SpamCategoryRepository;
import com.ever.webSpam.cressQc.CrossExcel;
import com.ever.webSpam.excel.ExcelManual;
import com.ever.webSpam.excel.ExcelWhiteQc;
import com.ever.webSpam.excel.JsonUtil;
import com.ever.webSpam.spam.RestSpamRepository;
import com.ever.webSpam.spam.Spam;
import com.ever.webSpam.spam.SpamController;
import com.ever.webSpam.spam.SpamRepository;
import com.ever.webSpam.utility.Constant;
import com.ever.webSpam.utility.VerifySite;

import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;
import one.util.streamex.StreamEx;

@SuppressWarnings("deprecation")
@Controller
public class ReviewController implements Initializable, Constant {
	Logger LOG = LoggerFactory.getLogger(ReviewController.class);
	private final List<String> spamform = Arrays.asList("{서비스}", "{채널}", "{리스트}", "{리스트/컨텐트}", "{서비스/채널 확인 필요}", "검수불가",
			"?서메", "?채메", "?컨리", "?컨테", "?비광", "?비텍", "?스리", "?악소", "?저위", "?음란", "?기컨", "?웹조", "?불사", "리디렉션오류",
			"비광확인");
	private Scene scene;
	private Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	private BorderPane borderPane;
	private HBox hBox;

	private List<SpamCategory> spamCategroyList;

	private ComboBox<String> comboBoxWorker;
	private ComboBox<String> comboBoxCategory;
	private ComboBox<String> comboBoxSite;
	private ComboBox<String> comboBoxWhite;

	private CheckBox checkBoxUseDatePicker;
	private CheckBox wasChecked;
	private Button buttonFilter;
	private Button buttonInsertCategory;
	private Button buttonAll;
	private Button buttonCross;
	private Button buttonResultCross;
	private Button buttonRefresh;
	private Button buttonSave;
	private Button buttonWorkNum;
	private Button buttonFeedBack;
	private Button buttonDelete;
	private Button buttonReview;
	private Button buttonTop;
	private Button buttonEnd;
	private Button buttonWhiteSave;
	private Button buttonCommentSort;
	private Button buttonTenReview;
	private Button buttonTenGroup;
	private Button buttonTenResult;
	private Button buttonTenHiddenText;
	private Button buttonAutoContinue;
	private Button buttonStopAutoReview;
	private DatePicker datePicker;
	private String selectedUri;
	private Label label;

	private TextField textFieldFilterURL;
	private TextField textFieldSearch;
	private TextField textFieldNo;
	private TextField textFieldTime;
	private TableView<Spam> tableView;
	private RadioButton radioButtonReverse;

	private List<Spam> spamList;
	private List<Spam> filtedSpamList;
	private List<Spam> spamCheckedList;
	private List<Spam> tooltipList;
	private Map<String, Long> workingCount;

	@SuppressWarnings("unused")
	private Boolean isWhite = false;
	private String pattern = "yyyy-MM-dd";

	@Autowired
	SpamCategoryController spamCategoryController;

	@Autowired
	RestSpamCategoryRepository restSpamCategoryRepository;

	@Autowired
	private ConverSpamStingToList converSpamStringToList;

	@Autowired
	SpamController spamController;

	@Autowired
	CrossExcel crossExcel;

	@Autowired
	JsonUtil jsonUtil;

	@Autowired
	ExcelManual excelManual;

	@Autowired
	RestSpamRepository restSpamRepository;

	@Autowired
	SpamRepository spamRepository;

	@Autowired
	SpamCategoryRepository spamCategoryRepository;

	@Autowired
	VerifySite verifySite;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.setProperty("java.awt.headless", "false");

	}

	// @PostConstruct
	// public void init() {
	// mapper = new ObjectMapper();
	// mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	// mapper.setSerializationInclusion(Include.NON_NULL);
	//
	// }

	private void initialPane() {
		borderPane = new BorderPane();
		hBox = new HBox();
		tableView = new TableView<Spam>();
		borderPane.setPadding(new Insets(7));
		borderPane.setTop(hBox);
		borderPane.setCenter(tableView);

	}

	private void initialComponent() {
		LOG.info("========== start initialize ");
		initChannelList();

		label = new Label();
		label.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

		wasChecked = new CheckBox("검수");
		wasChecked.setSelected(false);
		wasChecked.setOnAction(e -> {
			// String selectCategor =
			// comboBoxCategory.getSelectionModel().getSelectedItem(); =
			// comboBoxCategory.getSelectionModel().select(0);
			// comboBoxCategory.getSelectionModel().select(selectCategor);
			filtedSpamList = spamList;
			filtedSpamList = actionComboBoxCategoryHandler();
			filtedSpamList = actionComboBoxWorkerHandler();
			filtedSpamList = actionComboBoxSite();
			Platform.runLater(() -> reloadTable(filtedSpamList));
		});

		checkBoxUseDatePicker = new CheckBox("사용");
		checkBoxUseDatePicker.setSelected(false);
		checkBoxUseDatePicker.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxUseDatePicker.setOnAction(e -> reloadTableAction());

		comboBoxWorker = new ComboBox<String>();
		comboBoxWorker.setItems(observableListName);
		comboBoxWorker.setStyle("-fx-font: 14px \"Serif\"; -fx-font-weight: bold;");
		comboBoxWorker.valueProperty().addListener((obs, oldVal, newVal) -> {
			filtedSpamList = spamList;
			filtedSpamList = actionComboBoxWorkerHandler();
			filtedSpamList = actionComboBoxCategoryHandler();
			filtedSpamList = actionComboBoxSite();
			comboBoxWhite.getSelectionModel().select(0);
			Platform.runLater(() -> reloadTable(filtedSpamList));

		});

		comboBoxWhite = new ComboBox<String>();
		comboBoxWhite.setPromptText("WHITE");
		comboBoxWhite.setItems(observableListWhite);
		comboBoxWhite.setStyle("-fx-font: 14px \"Serif\"; -fx-font-weight: bold;");
		comboBoxWhite.valueProperty().addListener((obs, oldVal, newVal) -> {
			comboBoxSite.getSelectionModel().select(0);
			comboBoxSite.getSelectionModel().select(0);
			Platform.runLater(() -> actionComboxWhite());

		});

		comboBoxCategory = new ComboBox<String>();
		comboBoxCategory.setPromptText("SPAM");
		comboBoxCategory.setItems(observableListSpam);
		comboBoxCategory.setStyle("-fx-font: 14px \"Serif\"; -fx-font-weight: bold;");
		comboBoxCategory.valueProperty().addListener((obs, oldVal, newVal) -> {
			comboBoxWhite.getSelectionModel().clearSelection();
			filtedSpamList = spamList;
			filtedSpamList = actionComboBoxCategoryHandler();
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() != 0) {
				filtedSpamList = actionComboBoxWorkerHandler();
			}
			if (comboBoxSite.getSelectionModel().getSelectedIndex() != 0) {
				filtedSpamList = actionComboBoxSite();
			}
			Platform.runLater(() -> reloadTable(filtedSpamList));
		});

		comboBoxSite = new ComboBox<String>();
		comboBoxSite.setPromptText("SITE");
		comboBoxSite.setItems(observableListSite);
		comboBoxSite.setStyle("-fx-font: 14px \"Serif\"; -fx-font-weight: bold;");
		comboBoxSite.valueProperty().addListener((obs, oldVal, newVal) -> {
			filtedSpamList = spamList;
			filtedSpamList = actionComboBoxSite();
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() != 0) {
				filtedSpamList = actionComboBoxWorkerHandler();
			}
			if (comboBoxCategory.getSelectionModel().getSelectedIndex() != 0) {
				filtedSpamList = actionComboBoxCategoryHandler();
			}
			Platform.runLater(() -> reloadTable(filtedSpamList));
			// comboBoxWhite.getSelectionModel().clearSelection();
		});

		textFieldFilterURL = new TextField();
		textFieldFilterURL.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		textFieldFilterURL.setPromptText("Enter URI");
		textFieldFilterURL.setPrefWidth(250);
		textFieldFilterURL.setOnMouseClicked(e -> actionDoubleClickCleanHandler(e));
		textFieldFilterURL.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				actionButtonFilterURLHander(textFieldFilterURL.getText());
			}
		});

		textFieldSearch = new TextField();
		textFieldSearch.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		textFieldSearch.setPromptText("Enter Word");
		textFieldSearch.setPrefWidth(150);
		// textFieldSearch.setOnMouseClicked(e -> System.err.println("clicked"));
		textFieldSearch.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				keyReleadedTextFieldSearchHander(textFieldSearch.getText());
			}
		});

		textFieldNo = new TextField();
		textFieldNo.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		textFieldNo.setPrefWidth(50);
		textFieldNo.setAlignment(Pos.CENTER);
		textFieldNo.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				keyReleadedtextFieldNo(textFieldNo.getText());
			}
		});

		textFieldTime = new TextField();
		textFieldTime.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		textFieldTime.setPrefSize(45, 30);
		textFieldTime.setText("10");
		textFieldTime.setAlignment(Pos.CENTER);
		textFieldTime.setOnMouseClicked(event -> textFieldTime.clear());

		radioButtonReverse = new RadioButton();
		radioButtonReverse.setOnAction(e -> actionRadioButtonReverse());

		buttonCross = new Button();
		buttonCross.setGraphic(new ImageView("/images/cross.png"));
		buttonCross.setTooltip(new Tooltip("Cross QC 파일 만들기"));
		buttonCross.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		buttonCross.setOnAction(e -> actionButtonCrossHander());

		buttonResultCross = new Button();
		buttonResultCross.setGraphic(new ImageView("/images/excel.png"));
		buttonResultCross.setTooltip(new Tooltip("QC 결과 파일 추출"));
		buttonResultCross.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		buttonResultCross.setOnAction(e -> actionButtonResultCrossHander());

		buttonFilter = new Button();
		buttonFilter.setGraphic(new ImageView(new Image("/images/filter.png")));
		buttonFilter.setTooltip(new Tooltip("Filter Site"));
		// buttonFilter.setOnAction(e -> actionComboBoxHandler(""));
		buttonFilter.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		buttonFilter.setOnAction(e -> actionButtonFilterURLHander(textFieldFilterURL.getText()));

		buttonCommentSort = new Button();
		buttonCommentSort.setGraphic(new ImageView(new Image("/images/sort.png")));
		buttonCommentSort.setTooltip(new Tooltip("Sort Commnet"));
		buttonCommentSort.setOnAction(e -> actionButtonCommentSortHandler());
		buttonCommentSort.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonDelete = new Button();
		buttonDelete.setGraphic(new ImageView(new Image("/images/delete.png")));
		buttonDelete.setTooltip(new Tooltip("저장된 QC 삭제하기"));
		buttonDelete.setOnAction(e -> actionButtonDeleteHandler());
		buttonDelete.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonTenReview = new Button();
		buttonTenReview.setGraphic(new ImageView(new Image("/images/keep.png")));
		buttonTenReview.setOnAction(e -> actionButtonTenReview());
		buttonTenReview.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonTenGroup = new Button();
		buttonTenGroup.setGraphic(new ImageView(new Image("/images/three.png")));
		buttonTenGroup.setOnAction(e -> actionButtonAutoGroup());
		buttonTenGroup.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonTenResult = new Button();
		buttonTenResult.setGraphic(new ImageView(new Image("/images/bluelist.png")));
		buttonTenResult.setOnAction(e -> actionButtonTenResult());
		buttonTenResult.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonTenHiddenText = new Button();
		buttonTenHiddenText.setGraphic(new ImageView(new Image("/images/magnify.png")));
		buttonTenHiddenText.setOnAction(e -> actionButtonTenHiddenText());
		buttonTenHiddenText.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonAutoContinue = new Button();
		buttonAutoContinue.setGraphic(new ImageView(new Image("/images/repeat.png")));
		buttonAutoContinue.setOnAction(e -> actionButtonAutoContinue());
		buttonAutoContinue.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonStopAutoReview = new Button();
		buttonStopAutoReview.setGraphic(new ImageView(new Image("/images/stop.png")));
		buttonStopAutoReview.setOnAction(e -> actionButtonStopAutoReview());
		buttonStopAutoReview.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonReview = new Button();
		buttonReview.setGraphic(new ImageView(new Image("/images/folder.png")));
		buttonReview.setTooltip(new Tooltip("검수내역 검토"));
		buttonReview.setOnAction(e -> actionButtonReviewHandler());
		buttonReview.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonInsertCategory = new Button();
		buttonInsertCategory.setGraphic(new ImageView(new Image("/images/category.png")));
		buttonInsertCategory.setTooltip(new Tooltip("Spam Category Management"));
		buttonInsertCategory.setOnAction(e -> actionButtonInsertCategoryHandler());
		buttonInsertCategory.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonAll = new Button();
		buttonAll.setGraphic(new ImageView(new Image("/images/all.png")));
		buttonAll.setTooltip(new Tooltip("All"));
		buttonAll.setOnAction(e -> actionButtonAllHandler());
		buttonAll.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonWorkNum = new Button();
		buttonWorkNum.setGraphic(new ImageView(new Image("/images/Worker.png")));
		buttonWorkNum.setOnMouseEntered(e -> mouseEnterButtonWorkNumHandler());
		buttonWorkNum.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonSave = new Button();
		buttonSave.setGraphic(new ImageView(new Image("/images/save.png")));
		buttonSave.setTooltip(new Tooltip("QC 리스에 저장하기"));
		buttonSave.setOnAction(e -> actionButtonSaveHandler());
		buttonSave.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonRefresh = new Button();
		buttonRefresh.setGraphic(new ImageView(new Image("/images/refresh.png")));
		buttonRefresh.setTooltip(new Tooltip("화면 다시 불러오기"));
		buttonRefresh.setOnAction(e -> actionButtonRefreshHandler());
		buttonRefresh.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonFeedBack = new Button();
		buttonFeedBack.setGraphic(new ImageView(new Image("/images/feedback.png")));
		buttonFeedBack.setTooltip(new Tooltip("저장된 QC 리스트 불러오기"));
		buttonFeedBack.setOnAction(e -> actionButtonFeedbackHandler());
		buttonFeedBack.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonTop = new Button();
		buttonTop.setGraphic(new ImageView(new Image("/images/top.png")));
		buttonTop.setOnAction(e -> handlerButtonTopHandler());
		buttonTop.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonEnd = new Button();
		buttonEnd.setGraphic(new ImageView(new Image("/images/end.png")));
		buttonEnd.setOnAction(e -> handlerButtonEndHandler());
		buttonEnd.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonWhiteSave = new Button();
		buttonWhiteSave.setGraphic(new ImageView("/images/save.png"));
		buttonWhiteSave.setOnAction(e -> actionButtonSaveWhite());
		buttonWhiteSave.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		datePicker = new DatePicker();
		datePicker.setPrefWidth(150);
		datePicker.setConverter(new StringConverter<LocalDate>() {
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

			@Override
			public String toString(LocalDate date) {
				if (date != null) {
					return dateFormatter.format(date);
				} else {
					return "";
				}
			}

			@Override
			public LocalDate fromString(String string) {
				if (string != null && !string.isEmpty()) {
					return LocalDate.parse(string, dateFormatter);
				} else {
					return null;
				}
			}
		});

		datePicker.setOnAction(e -> {
			checkBoxUseDatePicker.setSelected(true);
			reloadTableAction();
		});

		datePicker.setValue(LocalDate.now());

		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);
		hBox.setStyle("-fx-alignment: CENTER;");
//		if (isFile) {
		// datePicker, checkBoxUseDatePicker, // buttonAll,
		hBox.getChildren().addAll(comboBoxWorker, buttonWorkNum, textFieldFilterURL, buttonFilter, buttonRefresh,
				buttonCross, buttonResultCross, buttonReview, buttonInsertCategory, buttonCommentSort, label, region,
				wasChecked, radioButtonReverse, textFieldNo, comboBoxCategory, comboBoxSite, buttonTenReview,
				buttonTenGroup, buttonTenResult, buttonTenHiddenText, textFieldTime, buttonAutoContinue, buttonStopAutoReview,
				comboBoxWhite, buttonWhiteSave, buttonTop, buttonEnd);
//		} else {
//			hBox.getChildren().addAll(datePicker, checkBoxUseDatePicker, comboBoxWorker, buttonAll, textFieldFilterURL,
//					buttonFilter, buttonRefresh, buttonCross, buttonResultCross, buttonReview, label, region,
//					comboBoxCategory);
//		}
		hBox.setPadding(new Insets(7, 7, 7, 7));
		hBox.setSpacing(5);
		LOG.info("========== start initialize ");
	}

	private Object actionRadioButtonReverse() {
		if (radioButtonReverse.isSelected() && !textFieldNo.getText().isEmpty()) {
			keyReleadedtextFieldNo(textFieldNo.getText());
		}
		return null;
	}

	private List<String> autoReviewList = new ArrayList<String>();

	private void actionButtonTenReview() { 
		autoReviewList.clear();
		int i = 0;
		int postion = 0;
		List<Spam> checkList = tableView.getItems();
		for (Spam spam : checkList) {
			postion++;
			if (spam.getSelected() || !spam.getNotCheck().isEmpty()) {
				spam.setSelected(true);
				continue;
			}
			
			if (!spam.getSelected()) {
				try {
					spam.setSelected(true);
					String url = spam.getUri();
					autoReviewList.add(url);
					verifySite.startBrowser(url, verifySite.getChrome());
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 1초 대기

				i++;
//				if (comboBoxSite.getSelectionModel().getSelectedIndex() == 5) {
//					verifySite.eventSearchResultAutoReview(spam.getUri()); // 검색결과
//				    verifySite.hiddenText(spam.getUri());   // 저장된 텍스트
//					verifySite.eventInspectReultAutoReview(spam.getUri()); // 결과 수정
//				}
			}
			tableView.scrollTo(postion - 10);
			if (i == 10) {
				break;
			}
		}
	}

	private void actionButtonTenResult() {
		for (String url : autoReviewList) {			
			try {
				verifySite.eventSearchResultAutoReview(url); // 검색결과
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void actionButtonTenHiddenText() {
		for (String url : autoReviewList) {			
			try {
				verifySite.hiddenText(url);   // 저장된 텍스트
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void actionButtonAutoGroup() {
		int i = 0;
		int index = 0;
		List<Spam> checkList = tableView.getItems();
		for (Spam spam : checkList) {
			index++;
			if (!spam.getSelected()) {
				try {
					spam.setSelected(true);
					String url = spam.getUri();
					verifySite.eventSearchResultAutoReview(url); // 검색결과
					verifySite.hiddenText(url); // 저장된 텍스트
					Thread.sleep(200); // 1초 대기
					verifySite.startBrowser(url, verifySite.getChrome());

					i++;
					Thread.sleep(200); // 1초 대기
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (i == 10) {
				break;
			}
		}
		tableView.scrollTo(index - 10);
	}

	private Object actionButtonStopAutoReview() {
		timer.cancel(); // 타이머 종료
		timerTask.cancel();
		System.out.println("[카운트다운 : 종료]");
		label.setText("종료");
		return null;
	}

	Timer timer;
	TimerTask timerTask;
	long delay = 5000L;

	private Object actionButtonAutoContinue() {
		delay = Long.valueOf(textFieldTime.getText()) * 1000;
		List<Spam> checkList = tableView.getItems();
		timerTask = new TimerTask() {
			int i = 0;

			@Override
			public void run() {
				// System.out.println("Task performed on: " + new Date());

				if (i >= checkList.size()) {
					timer.cancel(); // 타이머 종료
					System.out.println("[카운트다운 : 종료]");
					label.setText("종료");
				} else {

					Spam spam = checkList.get(i);
					while (spam.getSelected() || !spam.getNotCheck().isEmpty()) {
						spam.setSelected(true);
						spam = checkList.get(++i);
					}
					// System.err.println(spam);
					spam.setSelected(true);
					String url = spam.getUri();
					// System.err.println(spam);
					if (!spam.getLookMain() && !spam.getLookCh() && !spam.getLookList() && !spam.getLookCh()
							|| spam.getSpamAd() || spam.getSpamText()) {
						if (radioButtonReverse.isSelected() && !spam.getHam() && !spam.getHamLow()) {
							verifySite.eventSearchResultAutoReview(url); // 검색결과
							verifySite.hiddenText(url); // 저장된 텍스트 =
						}
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					tableView.scrollTo(i);

					Platform.runLater(() -> textFieldNo.setText(String.valueOf(i)));
					verifySite.startBrowser(url, verifySite.getChrome());
					i++;
				}
			}
		};

		timer = new Timer();
		timer.schedule(timerTask, 0, delay); // Every 1 second
		return null;
	}

	private Object actionComboxWhite() {
		isWhite = true;
		List<Spam> spams;
		if (filtedSpamList == null) {
			filtedSpamList = spamList;

		}

		// filtedSpamList.forEach(item -> item.setComment(""));
		switch (comboBoxWhite.getSelectionModel().getSelectedIndex()) {

		case 0:
			isWhite = false;
			if (spamList != null)
				spamList.forEach(item -> item.setComment(""));

			reloadTable(spamList);
			break;
		case 1:
			filtedSpamList = filtedSpamList.stream()
					.filter(item -> item.getScope().equals("domain") && !item.getNotCheck().equals("검수불가")
							&& !item.getBooleanDefer() && !item.getLookMain() && !item.getLookCh()
							&& !item.getLookList() && !item.getLookCont())
					.collect(Collectors.toList());
			filtedSpamList.forEach(item -> item.setComment(""));
			reloadTable(filtedSpamList);
			break;
		case 2:
			spams = filtedSpamList.stream().filter(item -> !item.getUri().endsWith(".com")
					&& !item.getUri().endsWith(".kr") && !item.getUri().endsWith(".net")).collect(Collectors.toList());
			spams.forEach(item -> item.setComment(""));
			reloadTable(spams);
			break;

		case 3:
			spams = filtedSpamList.stream()
					.filter(item -> item.getScope().equals("domain") && !item.getNotCheck().equals("검수불가")
							&& !item.getBooleanDefer() && !item.getLookMain() && !item.getLookCh()
							&& !item.getLookList() && !item.getLookCont())
					.filter(item -> item.getUri().endsWith(".com") || item.getUri().endsWith(".kr")
							|| item.getUri().endsWith(".net"))
					.collect(Collectors.toList());
			spams.forEach(item -> item.setComment(""));
			reloadTable(spams);
			break;
		case 4:
			filtedSpamList = spamList;
			spams = filtedSpamList.stream()
					.filter(item -> item.getScope().equals("domain") && !item.getNotCheck().equals("검수불가")
							&& !item.getBooleanDefer() && !item.getHam() && !item.getHamLow()
							&& (item.getLookMain() || item.getLookCh()) && !item.getLookList() && !item.getLookCont()
							&& item.getName().equals(comboBoxWorker.getSelectionModel().getSelectedItem()))
					.collect(Collectors.toList());
			spams.forEach(item -> item.setComment(""));
			reloadTable(spams);
			break;

		case 5:
			filtedSpamList = spamList;
			spams = filtedSpamList.stream()
					.filter(item -> item.getScope().equals("domain") && !item.getNotCheck().equals("검수불가")
							&& !item.getBooleanDefer() && !item.getHam() && !item.getHamLow() && !item.getLookMain()
							&& !item.getLookCh() && (item.getLookList() || item.getLookCont())
							&& item.getName().equals(comboBoxWorker.getSelectionModel().getSelectedItem()))
					.collect(Collectors.toList());
			spams.forEach(item -> item.setComment(""));
			reloadTable(spams);
			break;
		default:
			System.out.println("그 외의 숫자");
		}
		return null;
	}

	@Autowired
	ExcelWhiteQc excelWhiteQc;

	private Object actionButtonSaveWhite() {
		String sheet = comboBoxWhite.getSelectionModel().getSelectedItem();
		String file = excelWhiteQc.writeSpamList(tableView.getItems(), sheet);
		try {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	void handlerButtonTopHandler() {
		Platform.runLater(() -> tableView.scrollTo(0));
	}

	void handlerButtonEndHandler() {
		Platform.runLater(() -> tableView.scrollTo(tableView.getItems().size() - 1));
	}

	private Object actionButtonRefreshHandler() {
		int index = comboBoxWorker.getSelectionModel().getSelectedIndex();
		if (index == -1 || index == 0) {
			reloadTable(spamList);
		} else {
			String worker = comboBoxWorker.getSelectionModel().getSelectedItem().trim();
			List<Spam> filterList = spamList.stream().filter(item -> item.getName().equals(worker))
					.collect(Collectors.toList());
			reloadTable(filterList);
		}
		tooltipList = spamList;
		return null;
	}

	private Object actionButtonCommentSortHandler() {
		String name = comboBoxWorker.getSelectionModel().getSelectedItem();
		if (!name.equals("")) {
			List<Spam> fileterList = spamList.stream()
					.filter(item -> datePicker.getValue().toString().equals(item.getWorkday()))
					.filter(item -> item.getName().equals(name))
					.sorted(Comparator.comparing(Spam::getComment, Comparator.nullsLast(Comparator.naturalOrder())))
					.collect(Collectors.toList());

			reloadTable(fileterList);
		}

		return null;
	}

	private void updateLabel() {
		label.setText("OK");
		label.setStyle("-fx-alignment: CENTER; -fx-text-fill: #ff6666;");
		cleanNode();

	}

	private void cleanNode() {
		// 1초간 중지시킨다.(단위 : 밀리세컨드)
		Runnable runnable = () -> {
			try {
				Thread.sleep(1000 * 5);
				Platform.runLater(() -> {
					label.setText("");
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		};
		Thread t = new Thread(runnable);
		t.start();
	}

	private List<Spam> actionComboBoxSite() {
		Boolean isSeleted = wasChecked.isSelected();
		String review = comboBoxSite.getSelectionModel().getSelectedItem();
		String worker = comboBoxWorker.getSelectionModel().getSelectedItem();

		switch (comboBoxSite.getSelectionModel().getSelectedIndex()) {
		case 0:
			if (comboBoxCategory.getSelectionModel().getSelectedIndex() == 0) {
				reloadTable(filtedSpamList);
			}

			break;
		case 1:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> review.equals("서비스 메인 페이지") && item.getLookMain()) // 서메
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> review.equals("서비스 메인 페이지") && item.getLookMain()) // 서메
						.collect(Collectors.toList());
			break;
		case 2:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> review.equals("채널 메인 페이지") && item.getLookCh()) // 채널
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> review.equals("채널 메인 페이지") && item.getLookCh()) // 채널
						.collect(Collectors.toList());
			break;
		case 3:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> review.equals("컨텐츠 리스트 페이지") && item.getLookList()) // 리스트
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> review.equals("컨텐츠 리스트 페이지") && item.getLookList()) // 리스트
						.collect(Collectors.toList());
			break;
		case 4:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> review.equals("컨텐트 페이지") && item.getLookCont()) // 켄테
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> review.equals("컨텐트 페이지") && item.getLookCont()) // 켄테
						.collect(Collectors.toList());
			break;
		case 5:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected()).filter(
						item -> !item.getLookMain() && !item.getLookCh() && !item.getLookList() && !item.getLookCont()) // 켄테
						.filter(item -> !item.getNotCheck().equals("검수불가") && !item.getBooleanDefer()) // 켄테
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> !item.getNotCheck().equals("검수불가") && !item.getBooleanDefer()) // 켄테
						.filter(item -> !item.getLookMain() && !item.getLookCh() && !item.getLookList()
								&& !item.getLookCont()) // 켄테
						.collect(Collectors.toList());
			break;
		case 6:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getNotCheck().equals("검수불가") || item.getBooleanDefer()) // 켄테
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> item.getNotCheck().equals("검수불가") || item.getBooleanDefer())
						.collect(Collectors.toList());
			break;
		case 7:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getBooleanDefer()) // 켄테
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker)).filter(item -> item.getBooleanDefer())
						.collect(Collectors.toList());
			break;
		case 8:
			filtedSpamList.forEach(item -> checkSpamAction(item));
			filtedSpamList = filtedSpamList.stream().filter(item -> item.getComment() != null)
					.collect(Collectors.toList());
			break;
		default:
			System.out.println("그 외의 숫자");
		}
		if (filtedSpamList != null && filtedSpamList.size() > 0)
			datePicker.setValue(LocalDate.parse(filtedSpamList.get(0).getWorkday()));

		return filtedSpamList;

	}

	private List<Spam> actionComboBoxWorkerHandler() {
		String worker = comboBoxWorker.getSelectionModel().getSelectedItem();

		if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0) {
			filtedSpamList = spamList;
			comboBoxCategory.getSelectionModel().select(0);
			comboBoxSite.getSelectionModel().select(0);
		} else {
			filtedSpamList = filtedSpamList.stream().filter(item -> item.getName().equals(worker)) // 비광
					.collect(Collectors.toList());
		}
		return filtedSpamList;
	}

	private List<Spam> actionComboBoxCategoryHandler() {

		// ObservableList<String> observableListSpam =
		// FXCollections.observableArrayList(" ", "비정상 광고 컨테트", "비정상 텍스트",
		// "스펨 사이트로 리디렉션", "악성 소프트웨어", "저작권위반", "음란물", "기만적 컨텐트", "웹순위 조작 활동", "불법 사이트",
		// "정상 컨텐트",
		// "정상 컨텐트 저품질", "서비스 메인 페이지", "채널 메인 페이지", "컨텐츠 리스트 페이지", "컨텐트 페이지");

		// filtedSpamList = spamList.stream().filter(item -> isSeleted ==
		// item.getSelected())
		// .filter(item -> item.getName().equals(worker))
		// .filter(item -> category.equals("비정상 광고 컨테트") && item.getSpamAd()) // 비광
		// .filter(item -> category.equals("비정상 텍스트") && item.getSpamText()) // 비텍
		// .filter(item -> category.equals("스펨 사이트로 리디렉션") && item.getSpamRedir()) // 스피
		// .filter(item -> category.equals("악성 소프트웨어") && item.getSpamCopy()) // 악성
		// .filter(item -> category.equals("저작권위반") && item.getSpamIllegal()) // 저위
		// .filter(item -> category.equals("음란물") && item.getSpamPorn()) // 음란
		// .filter(item -> category.equals("기만적 컨텐트") && item.getSpamDecep()) // 기컨
		// .filter(item -> category.equals("웹순위 조작 활동") && item.getSpamManip()) // 웹조
		// .filter(item -> category.equals("불법 사이트") && item.getSpamIllegal()) // 불사
		// 10
		// .filter(item -> category.equals("정상 컨텐트") && item.getHam()) // 정상
		// .filter(item -> category.equals("정상 컨텐트 저품질") && item.getHamLow()) // 정저

		// .filter(item -> review.equals("서비스 메인 페이지") && item.getLookMain()) // 서메
		// .filter(item -> review.equals("채널 메인 페이지") && item.getLookCh()) // 채널
		// .filter(item -> review.equals("컨텐츠 리스트 페이지") && item.getLookList()) // 리스트
		// .filter(item -> review.equals("컨텐트 페이지") && item.getLookCont()) // 켄테
		// .collect(Collectors.toList());

		Boolean isSeleted = wasChecked.isSelected();
		String category = comboBoxCategory.getSelectionModel().getSelectedItem();
		String worker = comboBoxWorker.getSelectionModel().getSelectedItem();
		switch (comboBoxCategory.getSelectionModel().getSelectedIndex()) {
		case 0:
			reloadTable(spamList);

			break;
		case 1:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("비정상 광고 컨테트") && item.getSpamAd()) // 비광
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("비정상 광고 컨테트") && item.getSpamAd()) // 비광
						.collect(Collectors.toList());
			break;
		case 2:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("비정상 텍스트") && item.getSpamText()) // 비텍
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("비정상 텍스트") && item.getSpamText()) // 비텍
						.collect(Collectors.toList());
			break;
		case 3:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("스펨 사이트로 리디렉션") && item.getSpamRedir())
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("스펨 사이트로 리디렉션") && item.getSpamRedir())
						.collect(Collectors.toList());
			break;
		case 4:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("악성 소프트웨어") && item.getSpamMalware()) // 악성
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("악성 소프트웨어") && item.getSpamMalware()) // 악성
						.collect(Collectors.toList());
			break;
		case 5:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("저작권위반") && item.getSpamCopy()).collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("저작권위반") && item.getSpamCopy()).collect(Collectors.toList());
			break;
		// .filter(item -> category.equals("악성 소프트웨어") && item.getSpamCopy()) // 악성
		// .filter(item -> category.equals("저작권위반") && item.getSpamIllegal()) // 저위
		case 6:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("음란물") && item.getSpamPorn()) // 음란
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("음란물") && item.getSpamPorn()) // 음란
						.collect(Collectors.toList());
			break;
		case 7:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("기만적 컨텐트") && item.getSpamDecep()) // 기컨
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("기만적 컨텐트") && item.getSpamDecep()) // 기컨
						.collect(Collectors.toList());
			break;
		case 8:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("웹순위 조작 활동") && item.getSpamManip()) // 웹조
						.collect(Collectors.toList());
			else
				filtedSpamList = spamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("웹순위 조작 활동") && item.getSpamManip()) // 웹조
						.collect(Collectors.toList());
			break;
		case 9:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("불법 사이트") && item.getSpamIllegal()) // 불사
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("불법 사이트") && item.getSpamIllegal()) // 불사
						.collect(Collectors.toList());
			break;
		case 10:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("정상 컨텐트") && item.getHam()) // 정상
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("정상 컨텐트") && item.getHam()) // 정상
						.collect(Collectors.toList());
			break;
		case 11:
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0)
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("정상 컨텐트 저품질") && item.getHamLow()
								|| category.equals("정상 컨텐트 저품질") && item.getHamFish()) // 정저
						.collect(Collectors.toList());
			else
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getName().equals(worker))
						.filter(item -> category.equals("정상 컨텐트 저품질") && item.getHamLow()
								|| category.equals("정상 컨텐트 저품질") && item.getHamFish()) // 정저
						.collect(Collectors.toList());
			break;
		default:
			System.out.println("그 외의 숫자");
		}
		if (filtedSpamList != null && filtedSpamList.size() > 0)
			datePicker.setValue(LocalDate.parse(filtedSpamList.get(0).getWorkday()));
		return filtedSpamList;
	}

	private Object cleanComBox() {
		comboBoxWorker.getSelectionModel().select(0);
		comboBoxCategory.getSelectionModel().select(0);
		return null;
	}

	private Object actionButtonReviewHandler() {
		cleanComBox();
		checkBoxUseDatePicker.setSelected(true);
		// string file 에서 spam 내역을 가져온다.
		spamList = getSpamListFromDownloadList();
		// spamList.sort(Comparator.comparing(Spam::getUri));
		reloadTable(spamList);
		if (spamList != null)
			datePicker.setValue(LocalDate.parse(spamList.get(0).getWorkday()));
		return null;
	}

	private List<Spam> getSpamListFromDownloadList() {
		
		List<String> downloadList = spamController.getSpamListFromDownLoadFile();

		if (downloadList != null) {
			// string file 에서 spam 내역을 가져온다.
			spamList = converSpamStringToList.post(downloadList);
		}

		return spamList;
	}

	private void keyReleadedTextFieldSearchHander(String text) {
		// List<Spam> searchList = spamList.stream().filter(item -> item.getComment() !=
		// null)
		// .filter(item ->
		// item.getComment().contains(text)).collect(Collectors.toList());

		List<Spam> searchList = tableView.getItems().stream().peek(item -> System.err.println(item))
				.filter(item -> item.getNo() < Integer.valueOf(text)).collect(Collectors.toList());
		if (searchList.size() > 0) {
			reloadTable(searchList);
		}
		textFieldClean(textFieldSearch);

	}

	private void keyReleadedtextFieldNo(String text) {

		filtedSpamList = tableView.getItems();
		if (text.trim().equals("")) {
			filtedSpamList.forEach(item -> item.setSelected(false));
		} else {
			for (int i = 0; i < filtedSpamList.size(); i++) {
				if (radioButtonReverse.isSelected()) {
					if (i > (Integer.valueOf(text) - 1)) {
						filtedSpamList.get(i).setSelected(false);
					} else {
						filtedSpamList.get(i).setSelected(true);
					}
				} else {
					if (i > (Integer.valueOf(text) - 1)) {
						filtedSpamList.get(i).setSelected(true);
					} else {
						filtedSpamList.get(i).setSelected(false);
					}
				}

			}
		}
	}

	private void textFieldClean(TextField textField) {
		// 1초간 중지시킨다.(단위 : 밀리세컨드)
		Runnable runnable = () -> {
			try {
				Thread.sleep(1000 * 30);
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

	private Object actionDoubleClickCleanHandler(MouseEvent e) {
		textFieldFilterURL.clear();
		if (e.getClickCount() == 2) {
			Clipboard clipboard = Clipboard.getSystemClipboard();
			if (clipboard.hasString()) {
				String text = clipboard.getString();
				try {
					UrlValidator urlValidator = new UrlValidator();
					if (urlValidator.isValid(text)) {
						URL url = new URL(text);
						textFieldFilterURL.setText(url.getHost().toString());
					} else {
						textFieldFilterURL.setText(text);
					}

				} catch (MalformedURLException e1) {
					e1.printStackTrace();
				}

			}
		}
		return null;
	}

	private Object actionButtonDeleteHandler() {
		// if (checkBoxUseDatePicker.isSelected()) {
		// String date = datePicker.getValue().toString();
		// spamList = restSpamRepository.deleteBySelectedAndWorkday(false, date);
		// List<Spam> filteList = spamList.stream().filter(item ->
		// item.getWorkday().equals(date))
		// .collect(Collectors.toList());
		// reloadTable(filteList);
		// }

		Spam spam = tableView.getSelectionModel().getSelectedItem();
		spamRepository.delete(spam);
		spamCheckedList = spamRepository.findAll();
		// jsonUtil.saveJsonToSpamFile(spamCheckedList);
		excelManual.writeSpamList(spamCheckedList);
		updateTable(spamCheckedList);
		clearTextFileQuery();
		cleanComBox();
		return null;
	}

	@SuppressWarnings({ "unused" })
	private Object actionCheckBoxAllHandler() {

		clearTextFileQuery();
		cleanComBox();
		if (checkBoxUseDatePicker.isSelected()) {
			if (comboBoxWorker.getValue() == null || comboBoxWorker.getValue().trim().equals("")) {
				// spamList = spamRepository.findAll();
				spamList = restSpamRepository.findAllByOrderByWorkdayDesc();
				reloadTable(spamList);
			} else {
				String name = comboBoxWorker.getSelectionModel().getSelectedItem();
				List<Spam> filteredList = spamList.stream().filter(item -> item.getName().equals(name))
						.collect(Collectors.toList());
				reloadTable(filteredList);
				if (filteredList.size() > 0)
					datePicker.setValue(LocalDate.parse(spamList.get(0).getWorkday()));
				// spamList =
				// spamRepository.findByNameOrderByWorkdayAsc(comboBoxWorker.getSelectionModel().getSelectedItem());
			}
		} else {
			reloadTableAction();
		}
		if (spamList.size() > 0)
			datePicker.setValue(LocalDate.parse(spamList.get(0).getWorkday()));
		return null;
	}

	// 개선
	private Object actionButtonAllHandler() {
		checkBoxUseDatePicker.setSelected(false);
		comboBoxWorker.getSelectionModel().select(0);
		// spamList = restSpamRepository.findAllByOrderByWorkdayDesc();
		reloadTable(spamList);
		datePicker.setValue(LocalDate.parse(spamList.get(0).getWorkday()));
		return null;
	}

	// 개선
	private Object reloadTableAction() {
		buttonDelete.setDisable(true);
		List<Spam> filtedList;
		clearTextFileQuery();

		if (comboBoxWorker.getSelectionModel().getSelectedIndex() == -1 && !checkBoxUseDatePicker.isSelected()) {
			spamList = restSpamRepository.findAllByOrderByWorkdayDesc();
			reloadTable(spamList);

			return null;
		}

		String worker = comboBoxWorker.getSelectionModel().getSelectedItem();
		// 날짜 무시
		if (!checkBoxUseDatePicker.isSelected()) {
			// 모든 작업자
			if (worker.isEmpty()) {
				actionButtonAllHandler();
			} else { // 특정 작업자
				// filtedList = spamList.stream().filter(item -> item.getName().equals(worker))
				// .collect(Collectors.toList());
				// reloadTable(filtedList);
			}
		} else { // 특정날짜

			if (worker == null) { // 모든 작업자
				filtedList = spamList.stream()
						.filter(item -> item.getWorkday().equals(datePicker.getValue().toString()))
						.collect(Collectors.toList());

			} else if (worker.trim().equals("")) {
				filtedList = spamList.stream()
						.filter(item -> item.getWorkday().equals(datePicker.getValue().toString()))
						.collect(Collectors.toList());

			} else { // 특정 작업자
				filtedList = spamList.stream().filter(item -> item.getName().equals(worker))
						.filter(item -> item.getWorkday().equals(datePicker.getValue().toString()))
						.collect(Collectors.toList());
			}
			reloadTable(filtedList);
		}
		buttonDelete.setDisable(false);
		return null;
	}

	private void reloadTable(List<Spam> filedList) {
		// comboBoxWhite.getSelectionModel().select(0);
		tooltipList = filedList;
		if (filedList != null) {
			// tableView.getItems().clear();
			// tableView.getItems().addAll(filedList); -
			Platform.runLater(() -> {
				tableView.setItems(FXCollections.observableArrayList(filedList));
				updateFilterTextField(FXCollections.observableArrayList(filedList));
			});
		}

		updateLabel();
	}

	private Object actionButtonFeedbackHandler() {
		initialShortKey();
		clearTextFileQuery();
		cleanComBox();
		// spamCheckedList = jsonUtil.convertJsonToSpamList(SPAM_FILE);
		this.spamCheckedList = excelManual.readSpamList();
		datePicker.setValue(LocalDate.parse(spamCheckedList.get(0).getWorkday()));
		reloadTable(spamCheckedList);

		return null;
	}

	private Object actionButtonSaveHandler() {
		List<Spam> spamList = spamRepository.findAll();

		if (spamList != null && spamList.size() > 0) {
			List<Spam> filtedList = spamList.stream().filter(item -> item.getSelected()).collect(Collectors.toList());
			if (filtedList.size() > 0) {
				spamList.addAll(filtedList);
			}
		}
		// jsonUtil.saveJsonToSpamFile(spamList);
		excelManual.writeSpamList(spamCheckedList);
		// this.spamCheckedList = jsonUtil.convertJsonToSpamList(SPAM_FILE);
		this.spamCheckedList = excelManual.readSpamList();
		updateTable(spamCheckedList);
		clearTextFileQuery();
		cleanComBox();

		return null;
	}

	private void updateTable(List<Spam> spamList) {
		tableView.getItems().clear();
		tableView.getItems().addAll(spamList);
	}

	private Object mouseEnterButtonWorkNumHandler() {
		clearTextFileQuery();

		Tooltip tt = null;
		if (tooltipList == null) {
			tooltipList = spamList;
		}

		if (tooltipList != null) {
			workingCount = tooltipList.stream().collect(Collectors.groupingBy(Spam::getName, Collectors.counting()));
			tt = new Tooltip(workingCount.toString());

			buttonWorkNum.setTooltip(tt);
			tt.setStyle("-fx-font: normal bold 16 Langdon; " + "-fx-base: #AE3522; " + "-fx-text-fill: orange;");
		}
		return null;
	}

	public void actionButtonInsertCategoryHandler() {
		clearTextFileQuery();
		cleanComBox();
		spamCategoryController.show();
	}

	private void actionButtonCrossHander() {

		LOG.info("========== start actionButtonCrossHander() ");

		// if (spamList == null) {

		spamList = getSpamListFromDownloadList();

		reloadTable(spamList);
		datePicker.setValue(LocalDate.parse(spamList.get(0).getWorkday()));
		// }

		Random rand = new Random();

		String notCheck = "검수불가";

		int sizeQc = 50;

		List<String> workedMan = new ArrayList<String>();

		// 작업자를 spamList 에서 추출 해야한다.

		List<Spam> wrokingSpamList = StreamEx.of(spamList).distinct(Spam::getName).toList();

		List<String> workingMan = wrokingSpamList.stream().map(item -> item.getName())
				.filter(item -> observableListName.contains(item)).collect(Collectors.toList());

		Map<String, String> historyMap = new HashMap<String, String>();
		workingMan.remove("admin");

		LOG.info("========== make cross file ");

		for (Spam spam : wrokingSpamList) {

			String name = spam.getName();

			if (!name.equals("admin")) {

				List<Spam> crossSpamList = spamList.stream().filter(item -> !item.getNotCheck().equals(notCheck))
						.filter(item -> !item.getDefer().equals("보류")).filter(item -> item.getName().equals(name))

						.filter(distinctByKey(s -> s.getUri()))

						.collect(Collectors.toList());

				if (crossSpamList.size() > sizeQc) {

					workingMan.remove(name);

					workingMan.removeAll(workedMan);

					String doWorker = workingMan.get(rand.nextInt(workingMan.size()));

					if (!doWorker.equals(name)) {

						workedMan.add(doWorker);

						workingMan.add(name);

						// System.err.println(name + " => " + m);

						List<Spam> qcSpameList = pickRandom(crossSpamList, sizeQc);

						// LOG.info("========== writeCrossFile " + doWorker);

						Collections.sort(qcSpameList, compareByTimeOfInspection);

						writeCrossFile(doWorker, qcSpameList);

						historyMap.put(doWorker, name);

					}

				}

			}

		}

		AtomicInteger index = new AtomicInteger();

		String mapAsString = historyMap.keySet().stream()

				.map(key -> index.getAndIncrement() + ". " + key + "  -> " + historyMap.get(key))

				.collect(Collectors.joining(" \n \t", "\t", ""));

		ShowAlert("\tPath = " + QC_FILE + "\n\n\n" + mapAsString);

	}

	Comparator<Spam> compareByTimeOfInspection = (Spam o1, Spam o2) -> o2.getTimeOfInspection()
			.compareTo(o1.getTimeOfInspection());

	// Utility function to find distinct by class field

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)

	{

		Map<Object, Boolean> map = new ConcurrentHashMap<>();

		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;

	}

	private void actionButtonResultCrossHander() {

		LOG.info("========== start actionButtonResultCrossHander() ");
		Stage stage = (Stage) buttonResultCross.getScene().getWindow();
		FileChooser fileChooser = new FileChooser();
		if (crossExcel.getPath() != null)
			fileChooser.setInitialDirectory(new File(crossExcel.getPath()));
		fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Excels Files", "*.xlsx"));
		List<File> fileList = fileChooser.showOpenMultipleDialog(stage);
		List<Spam> qcResultList = new ArrayList<Spam>();
		for (File file : fileList) {
			LOG.info("========== start " + file.toString());
			List<Spam> spamList = crossExcel.getSpamList(file);
			List<Spam> filteredList = spamList.stream().filter(item -> !item.getQc().trim().equals(""))
					.collect(Collectors.toList());
			String worker = file.getName().substring(12, 15);
			filteredList.forEach(item -> item.setLabel(worker));
			qcResultList.addAll(filteredList);
		}
		Boolean result = crossExcel.writeResultFile("RESULT", qcResultList);

		if (result) {
			Platform.runLater(() -> {
				label.setText("결과 파일이 작성되었습니다.");
				label.setStyle("-fx-alignment: CENTER; -fx-text-fill: #0033cc;");
			});
		} else {
			label.setText("엑셀파일을 사용 중이기때문에 엑세스할 수 없습니다 !");
			label.setStyle("-fx-alignment: CENTER; -fx-text-fill: #ff9999;");
		}
		clearLabel();
	}

	private void ShowAlert(String message) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Cross QC 파일 만들기");
		alert.setHeaderText(null);
		alert.setGraphic(null);
		alert.setContentText(message);
		Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
		stage.setAlwaysOnTop(true);

		alert.showAndWait();
	}

	private Object writeCrossFile(String doWorker, List<Spam> qcSpameList) {
		Boolean result = crossExcel.writeCrossFile(doWorker, qcSpameList);
		if (result) {
			Platform.runLater(() -> {
				label.setText("Cross QC 파일을 만들었습니다.");
				label.setStyle("-fx-alignment: CENTER; -fx-text-fill: #0033cc;");
			});

		} else {
			label.setText("엑셀파일을 사용 중이기때문에 엑세스할 수 없습니다 !");
			label.setStyle("-fx-alignment: CENTER; -fx-text-fill: #ff9999;");
		}
		clearLabel();
		return null;
	}

	private void clearLabel() {
		Runnable runnable = () -> {
			try {
				Thread.sleep(1000 * 10);
				Platform.runLater(() -> {
					label.setText("");
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		};
		Thread t = new Thread(runnable);
		t.start();

	}

	private static <E> List<E> pickRandom(List<E> list, int n) {
		Collections.shuffle(list);
		return list.subList(0, n);
		// return new Random().ints(n, 0,
		// list.size()).mapToObj(list::get).collect(Collectors.toList());
	}

	public static <T> Collector<T, ?, Stream<T>> toShuffledStream() {
		return Collectors.collectingAndThen(Collectors.toList(), collected -> {
			Collections.shuffle(collected);
			return collected.stream();
		});
	}

	private Object actionButtonFilterURLHander(String uri) {
		List<Spam> filedList = new ArrayList<Spam>();
		if (spamList != null) {
			if (comboBoxWorker.getSelectionModel().getSelectedIndex() == 0) {
				filedList = spamList.stream().filter(item -> item.getUri().contains(uri)).collect(Collectors.toList());
			} else {
				String name = comboBoxWorker.getSelectionModel().getSelectedItem();
				filedList = spamList.stream().filter(item -> item.getName().equals(name))
						.filter(item -> item.getUri().contains(uri)).collect(Collectors.toList());
			}
		}

		if (spamCheckedList != null) {
			List<Spam> checkedList = spamCheckedList.stream().filter(item -> item.getUri().contains(uri))
					.collect(Collectors.toList());
			if (checkedList != null && checkedList.size() > 0) {
				filedList.addAll(checkedList);
			}
		}
		reloadTable(filedList);
		return null;
	}

	public void open() {

		initialPane();
		initialComponent();
		initialTableView();
		//
		// // this.spamCheckedList = jsonUtil.convertJsonToSpamList(SPAM_FILE);
		// if (isFile) {
		// this.spamCheckedList = excelManual.readSpamList();
		// }
		if (spamCheckedList != null) {
			tableView.getItems().addAll(spamCheckedList);
			spamRepository.saveAll(spamCheckedList);
		}

		if (spamCheckedList != null && spamCheckedList.size() > 0) {
			datePicker.setValue(LocalDate.parse(spamCheckedList.get(0).getWorkday()));
		} else {
			datePicker.setValue(LocalDate.now());
		}

		Stage stage = new Stage();
		stage.getIcons().add(new Image("/images/list.png"));
		stage.setTitle("Review result");
		scene = new Scene(borderPane);
		//scene.getStylesheets().add("styles/styles.css");
		stage.setScene(scene);
		stage.setWidth(primaryScreenBounds.getWidth());
		stage.setHeight(primaryScreenBounds.getHeight());
		stage.setOnCloseRequest(event -> {
			stage.close();
		});
		stage.show();
		initialShortKey();
		buttonReview.fire();
	}

	private void initialShortKey() {
		KeyCombination ka = new KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN);
		Runnable rn = () -> buttonAll.fire();

		KeyCombination kf = new KeyCodeCombination(KeyCode.F, KeyCombination.CONTROL_DOWN);
		Runnable rf = () -> buttonFilter.fire();

		KeyCombination ks = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
		Runnable rs = () -> buttonSave.fire();

		KeyCombination kb = new KeyCodeCombination(KeyCode.B, KeyCombination.CONTROL_DOWN);
		Runnable rb = () -> buttonFeedBack.fire();

		KeyCombination kc = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_DOWN);
		Runnable rc = () -> buttonInsertCategory.fire();

		KeyCombination kr = new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN);
		Runnable rr = () -> buttonReview.fire();

		scene.getAccelerators().put(ka, rn);
		scene.getAccelerators().put(kf, rf);
		scene.getAccelerators().put(ks, rs);
		scene.getAccelerators().put(kb, rb);
		scene.getAccelerators().put(kc, rc);
		scene.getAccelerators().put(kr, rr);

	}

	@SuppressWarnings("unchecked")
	private void initialTableView() {

		// centerTableView.prefWidthProperty().bind(anchorPaneRightTableview.widthProperty());
		// centerTableView.prefHeightProperty().bind(anchorPaneRightTableview.heightProperty());
		tableView.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.isPrimaryButtonDown() && event.getClickCount() == 2) {
					try {
						String spamUrl = tableView.getSelectionModel().getSelectedItem().getUri();
						if (!spamUrl.startsWith("http")) {
							spamUrl = "http://" + spamUrl;
						}
						URL url = new URL(spamUrl);
						textFieldFilterURL.setText(url.getHost());
					} catch (MalformedURLException e) {
						e.printStackTrace();
					}
				}
			}
		});

		tableView.getStyleClass().add("spam");
		tableView.setTableMenuButtonVisible(true);

		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				String url = tableView.getSelectionModel().getSelectedItem().getUri().trim();
				// setClipbord(url);

				try {
					URI uri = new URI(url);
					selectedUri = uri.getHost();
					if (selectedUri == null) {
						// textFieldFilterURL.setText(url);
					} else {
						// textFieldFilterURL.setText(selectedUri);
					}

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

			}
		});

		tableView.setRowFactory(tv -> new TableRow<Spam>() {
			@Override
			public void updateItem(Spam item, boolean empty) {
				super.updateItem(item, empty);
				if (item == null) {
					setStyle("");
				} else if (item.getComment() != null && (spamform.contains(item.getComment()))
						&& item.getNotCheck() != null && !(spamform.contains(item.getNotCheck()))) {
					if (item.getComment().contains("리디렉션오류") || item.getComment().contains("비광확인")
							|| item.getComment().contains("서비스/채널 확인 필요")) {
						setStyle("-fx-background-color: #f9e6ff;");
					} else {
						setStyle("-fx-background-color: null;");
					}

				} else {
					setStyle("");
				}

				if (item != null && item.getIsCheck()) {
					setStyle("-fx-background-color: #f9e6ff;");
				} else {
					// setStyle("-fx-background-color: null;");
				}
			}
		});

		// tableView.setStyle("-fx-selection-bar: red; -fx-selection-bar-non-focused:
		// salmon;");
		tableView.setEditable(true);
		TableColumn<Spam, Boolean> columnSelected = createCheckBoxHeaderColumn(Spam::selectedProperty);
		TableColumn<Spam, String> columnUri = createColumn("url", Spam::uriProperty);
		TableColumn<Spam, String> columnName = createColumn("name", Spam::nameProperty);
		TableColumn<Spam, String> columnScope = createColumn("scope", Spam::scopeProperty);
		TableColumn<Spam, String> columnEmail = createColumn("email", Spam::emailProperty);
		TableColumn<Spam, String> columnWorkDay = createColumn("date", Spam::workdayProperty);
		TableColumn<Spam, String> columnNotCheck = createColumn("불가", Spam::notCheckProperty);
		TableColumn<Spam, String> columnDefer = createColumn("보류", Spam::deferProperty);
		TableColumn<Spam, String> columnComment = createEditColumn("comment", Spam::commentProperty);

		TableColumn<Spam, Boolean> columnLookMain = createCheckBoxColumn("서메", "서비스 메인페이지", Spam::lookMainProperty);
		TableColumn<Spam, Boolean> columnLookCh = createCheckBoxColumn("채메", "채널 메인페이지", Spam::lookChProperty);
		TableColumn<Spam, Boolean> columnLookList = createCheckBoxColumn("컨리", "컨텐츠 리스트 페이지", Spam::lookListProperty);
		TableColumn<Spam, Boolean> columnLookCont = createCheckBoxColumn("컨테", "컨텐트 페이지", Spam::lookContProperty);

		TableColumn<Spam, Boolean> columnHam = createCheckBoxColumn("정상", "정상 컨텐트", Spam::hamProperty);
		TableColumn<Spam, Boolean> columnHamLow = createCheckBoxColumn("정저", "정상 컨텐트 저품질", Spam::hamLowProperty);
		TableColumn<Spam, Boolean> columnHamFish = createCheckBoxColumn("정낚", "정상 컨텐트 저품질 낚시", Spam::hamFishProperty);

		TableColumn<Spam, Boolean> columnSpamAd = createCheckBoxColumn("비광", "비정상 광고 컨텐트", Spam::spamAdProperty);
		TableColumn<Spam, Boolean> columnSpamText = createCheckBoxColumn("비텍", "비장상 텍스트", Spam::spamTextProperty);
		TableColumn<Spam, Boolean> columnSpamRedir = createCheckBoxColumn("스리", "스펨 사이트로 리디렉션",
				Spam::spamRedirProperty);
		TableColumn<Spam, Boolean> columnSpamMalware = createCheckBoxColumn("악소", "악성 소프트웨어 사이트",
				Spam::spamMalwareProperty);

		TableColumn<Spam, Boolean> columnSpamCopy = createCheckBoxColumn("저위", "저작권위반", Spam::spamCopyProperty);
		TableColumn<Spam, Boolean> columnSpamPorn = createCheckBoxColumn("음란", "음란물", Spam::spamPornProperty);
		TableColumn<Spam, Boolean> columnSpamDecep = createCheckBoxColumn("기컨", "기만적 컨텐트", Spam::spamDecepProperty);
		TableColumn<Spam, Boolean> columnSpamManip = createCheckBoxColumn("웹조", "웹순 조작활동", Spam::spamManipProperty);
		TableColumn<Spam, Boolean> columnSpamIllegal = createCheckBoxColumn("불사", "불법 사이트", Spam::spamIllegalProperty);

		columnLookMain.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #e6ffff;");
		columnLookCh.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #e6ffff;");
		columnLookList.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #e6ffff;");
		columnLookCont.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #e6ffff;");

		columnSpamAd.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #ffffcc;");
		columnSpamText.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #ffffcc;");
		columnSpamRedir.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #ffffcc;");
		columnSpamMalware.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #ffffcc;");

		columnSpamCopy.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #fff2e6;");
		columnSpamPorn.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #fff2e6;");
		columnSpamDecep.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #fff2e6;");
		columnSpamManip.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #fff2e6;");
		columnSpamIllegal.setStyle(
				"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #fff2e6;");
		columnComment.setStyle("-fx-alignment: CENTER-LEFT;");

		columnSelected.setPrefWidth(40);
		columnUri.setPrefWidth(400);
		columnName.setPrefWidth(65);
		columnScope.setPrefWidth(65);
		columnEmail.setPrefWidth(100);
		columnNotCheck.setPrefWidth(65);
		columnDefer.setPrefWidth(50);
		columnComment.setPrefWidth(400);
		columnWorkDay.setPrefWidth(100);

		TableColumn<Spam, Spam> numberCol = new TableColumn<Spam, Spam>("#");
		numberCol.setStyle("-fx-alignment: CENTER;");
		numberCol.setMinWidth(40);
		numberCol.setPrefWidth(40);
		numberCol.setCellValueFactory(new Callback<CellDataFeatures<Spam, Spam>, ObservableValue<Spam>>() {
			@SuppressWarnings("rawtypes")
			@Override
			public ObservableValue<Spam> call(CellDataFeatures<Spam, Spam> p) {
				return new ReadOnlyObjectWrapper(p.getValue());
			}
		});

		numberCol.setCellFactory(new Callback<TableColumn<Spam, Spam>, TableCell<Spam, Spam>>() {
			@Override
			public TableCell<Spam, Spam> call(TableColumn<Spam, Spam> param) {

				return new TableCell<Spam, Spam>() {
					@Override
					protected void updateItem(Spam item, boolean empty) {
						super.updateItem(item, empty);
						if (this.getTableRow() != null && item != null) {
							Spam spam = (Spam) this.getTableRow().getItem();
							if (spam != null && !spam.getSelected()) {
								Boolean r = checkSpamAction(spam);
								if (r) {
									this.setStyle(
											"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #f9e6ff;");
								} else {
									this.setStyle("-fx-alignment: CENTER;");
								}
							}

							if (spam != null && spam.getSpamMalware()) {
								this.setStyle(
										"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #000000; -fx-text-fill:#ffffff");
							}
							setText(this.getTableRow().getIndex() + 1 + "");
							// numberCol.setStyle("-fx-alignment: CENTER; -fx-background-color: #e6ffff;");
						} else {
							setText("");
						}
					}

				};
			}
		});
		numberCol.setSortable(false);

		columnNotCheck.setCellFactory(e -> new TableCell<Spam, String>() {
			@Override
			public void updateItem(String item, boolean empty) {
				// Always invoke super constructor.
				super.updateItem(item, empty);

				if (item == null || empty) {
					setText(null);
				} else {
					setText(item);
					Spam spam = (Spam) this.getTableRow().getItem();
					if (spam != null && !spam.getLookCh() && !spam.getLookCont() && !spam.getLookList()
							&& !spam.getLookMain()) {
						this.setStyle(
								"-fx-alignment: CENTER; -fx-border-color: black; -fx-border-width : 0.03px; -fx-background-color: #ffcc99;");
					} else {
						this.setStyle("-fx-alignment: CENTER;");
					}

				}
			}
		});
		columnNotCheck.setMinWidth(60);
		columnNotCheck.setPrefWidth(60);

		TableColumn<Spam, Void> columnButton = createButtonColumn();
		columnButton.setMinWidth(168);
		columnButton.setPrefWidth(168);

		tableView.getColumns().addAll(numberCol, columnSelected, columnButton, columnUri, columnName, columnScope,
				columnNotCheck, columnDefer, columnLookMain, columnLookCh, columnLookList, columnLookCont, columnHam,
				columnHamLow, columnHamFish, columnSpamAd, columnSpamText, columnSpamRedir, columnSpamMalware,
				columnSpamCopy, columnSpamPorn, columnSpamDecep, columnSpamManip, columnSpamIllegal, columnComment,
				columnWorkDay);

	}

	List<String> channelOne = Arrays.asList("tumblr.com", "tistory.com", "blogspot.com", "tiktok.com", "postype.com",
			"egloos.com", "wordpress.com");
	List<String> channelTwo = Arrays.asList("blog.naver.com", "soundcloud.com", "blog.daum.net", "facebook.com",
			".tiktok.com", "twitter.com", "blog.sina.com.cn", "pinterest.com", "instagram.com", "flipboard.com");
	List<String> channelThree = Arrays.asList("youtube.com/channel/", "youtube.com/user/");
	List<String> channelAll = new ArrayList<String>();
	List<String> server = Arrays.asList("sites.google.com/view/");

	public void initChannelList() {
		channelAll.addAll(channelOne);
		channelAll.addAll(channelTwo);
		channelAll.addAll(channelThree);
	}

	public Boolean checkSpamAction(Spam spam) {
		URL url = null;

		if (spam.getNotCheck().equals("검수불가")) {
			return false;
		}

		if (!spam.getLookCh() && !spam.getLookCont() && !spam.getLookList() && !spam.getLookMain()) {
			return false;
		}

		if (humanErrer(spam)) {
			return true;
		}

		ErrorCheckDbms(spam);

		String urlString = spam.getUri();
		try {
			if (!urlString.startsWith("http")) {
				urlString = "http://" + urlString;
			}
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		String host = url.getHost();
		String path = url.getFile();

//		System.out.println("url =" + urlString);
//		System.out.println("host =" + url.getHost()); // 호스트 이름 출력
//		System.out.println("filename =" + url.getFile());
//		System.out.println("path =" + url.getPath() + "\n\n");

//		if (urlString.contains("pinterest.com")) {
//			System.err.println(urlString);
//		}

		if (path.equals("") || path.equals("/")) {
			boolean isContain = channelAll.stream().anyMatch(u -> host.contains(u));
			if (isContain) {
				if (spam.getLookCh()) {
					return false;
				}
				spam.setComment("채널 확인요");
				spam.setIsCheck(true);
				// System.out.println("0. 채널 => " + spam.getLookCh());
				return true;
			} else if (!spam.getLookMain()) {
				// System.err.println(spam);
				spam.setComment("시비스 확인요");
				// System.out.println("0. 시비스 확인요 => " + spam.getLookCh());
				spam.setIsCheck(true);
				return true;
			}
		} else {

			if (urlString.endsWith("/")) {
				urlString = urlString.substring(0, urlString.length() - 1);
			}

//			if (urlString.contains("www.sites.google.com")) {
//				System.err.println(urlString);
//			}

			// 1. 채널 확인
			if (isChannel(urlString)) {
				if (spam.getLookCh()) {
					return false;
				}
				spam.setComment("채널 확인요");
				spam.setIsCheck(true);
				return true;
			} else if (isServer(urlString)) {
				if (spam.getLookMain()) {
					return false;
				}
				spam.setComment("서버확인");
				spam.setIsCheck(true);
				return true;
			} else if (isList(path)) {
				if (spam.getLookList()) {
					return false;
				}
				spam.setComment("리스트 확인요");
				spam.setIsCheck(true);
				return true;
			} else if (spam.getLookCh() && spam.getLookMain()) {
				spam.setComment("채널/리스트 확인요");
				spam.setIsCheck(true);
				return true;
			}

			if (!path.equals("") && !spam.getSpamRedir() && (spam.getLookCh() || spam.getLookMain())) {
				spam.setIsCheck(true);
				spam.setComment("3. 채널/리스트 확인요");
				return true;
			}
		}

		return false;
	}

	private void ErrorCheckDbms(Spam spam) {

		// spamCategroyList = spamCategoryRepository.findAllByOrderByUriAsc();
		// System.err.println(spamCategroyList);

		// spamCategoryList 에서 체크한다.
		if (spamCategroyList != null) {
			SpamCategory spamCategroy = spamCategroyList.stream().filter(item -> item != null)
					.filter(item -> spam.getUri().contains(item.getUri())).findAny().orElse(null);
			String comment = spam.getComment();
			if (spamCategroy != null) {
				if (spamCategroy.getLookMain() && spam.getLookCh()) { // 그룹 : 메인 , 작업 : 채널
					if (spam.getComment() == null || spam.getComment().isEmpty()) {
						spam.setComment("검증 -> {서비스}" + comment);
						spam.setIsCheck(true);
					}
					// return true;
				} else if (spamCategroy.getLookCh() && spam.getLookMain()) { // 그룹 : 채널 , 작업 : 메인
					if (spam.getComment() == null || spam.getComment().isEmpty()) {
						spam.setComment("{채널}" + comment);
						spam.setIsCheck(true);
					}
					// return true;
				} else if (spamCategroy.getHamLow() && !spam.getHamLow()) { // 그룹 : 저품질 , 작업 : !저품질
					if (spam.getComment() == null || spam.getComment().isEmpty()) {
						spam.setComment("{저품질}" + comment);
						spam.setIsCheck(true);
					}
					// return true;
				} else if (spamCategroy.getLookList() && (spam.getLookMain() || spam.getLookCh())) { // 그룹 : 리스트 , 작업 :
																										// !리스트
					if (spam.getComment() == null || spam.getComment().isEmpty()) {
						spam.setComment("{리스트}" + comment);
						spam.setIsCheck(true);
					}
					// return true;
				} else {
					String s = comment;
					if (spamCategroy.getSpamAd() && !spam.getSpamAd()) {
						if (spam.getComment() == null || spam.getComment().isEmpty())
							s = "{비광}";
						spam.setIsCheck(true);

					}

					if (spamCategroy.getSpamText() && !spam.getSpamText()) { // 그룹 : 리스트 , 작업 : !리스트
						if (spam.getComment() == null || spam.getComment().isEmpty())
							s = s + "{비광}";
						spam.setIsCheck(true);
					}
					spam.setComment(s);
					// return true;
				}
			}
		}
	}

	private boolean humanErrer(Spam spam) {
		// 작업 실수 확인
		if (spam.getSpamRedir() && spam.getSpamText() && !spam.getSpamAd()) {
			spam.setComment("리디렉션오류");
			spam.setIsCheck(true);
			return true;
		}

		if (spam.getUri().contains(".go.kr") && (spam.getSpamAd() || spam.getSpamText())) {
			spam.setComment("관공서 오류 체크");
			spam.setIsCheck(true);
		}

		if (spam.getSpamMalware()) {
			spam.setComment("?악소");
			spam.setIsCheck(true);
			return true;
		}

		if (!spam.getDefer().equals("")) {
			spam.setComment("?보류");
			spam.setIsCheck(true);
			return true;
		}

		if (spam.getSpamDecep()) {
			spam.setComment("?기컨");
			spam.setIsCheck(true);
			return true;
		}

		if (spam.getSpamMalware()) {
			spam.setComment("?악성");
			spam.setIsCheck(true);
			return true;
		}
		return false;
	}

	private boolean isServer(String urlString) {
		boolean isContain = server.stream().anyMatch(u -> urlString.contains(u));
		if (isContain) {
			long count = urlString.chars().filter(ch -> ch == '/').count();
			if (count < 5) {
				return true;
			}
		}
		return false;
	}

	private boolean isList(String path) {
		if (((path.contains("list") || path.contains("tag") || path.contains("search") || path.contains("category")))) {
			return true;
		}
		return false;
	}

	// 채널 확인
	private Boolean isChannel(String url) {

		boolean isContain = channelTwo.stream().anyMatch(u -> url.contains(u));

		if (isContain) {
			long count = url.chars().filter(ch -> ch == '/').count();
			if (count < 4) {
				return true;
			}
		}

		isContain = channelThree.stream().anyMatch(u -> url.contains(u));

		if (isContain) {
			long count = url.chars().filter(ch -> ch == '/').count();
			if (count < 5) {
				return true;
			}
		}
		return false;
	}


	@SuppressWarnings("unused")
	private String isSpam(Spam spam) {
		List<Spam> isSpam = null;
		String s = null;
		try {
			URI uri = new URI(spam.getUri().trim());
			String host = uri.getHost();

			if (host != null && spamList != null) {

				isSpam = spamList.stream().filter(item -> !item.getUri().equals(spam.getUri()))
						.filter(item -> item.getUri().contains(host)).collect(Collectors.toList());

				if (isSpam != null && isSpam.size() > 0) {
					// s = "" + isSpam.size() + " -> ";

					for (Spam spam2 : isSpam) {
						s = spam2.getWorkday();
						if (spam2.getLookMain()) {
							s = s + " / 서메";
						}
						if (spam2.getLookCh()) {
							s = s + " / 채메";
						}
						if (spam2.getLookList()) {
							s = s + " / 컨리";
						}
						if (spam2.getLookCont()) {
							s = s + " / 컨텐";
						}
						if (spam2.getHam()) {
							s = s + " / 정상";
						}
						if (spam2.getHamLow()) {
							s = s + " / 정저";
						}
						if (spam2.getSpamAd()) {
							s = s + " / 비광";
						}
						if (spam2.getSpamText()) {
							s = s + " / 비텍";
						}
						if (spam2.getSpamRedir()) {
							s = s + "/ 스리";
						}
						if (spam2.getSpamMalware()) {
							s = s + "/ 악소";
						}
						if (spam2.getSpamCopy()) {
							s = s + "/ 저위";
						}
						if (spam2.getSpamPorn()) {
							s = s + "/ 음란";
						}
						if (spam2.getSpamDecep()) {
							s = s + "/ 기만";
						}
						if (spam2.getSpamManip()) {
							s = s + "/ 웹조";
						}
						if (spam2.getSpamIllegal()) {
							s = s + "/ 불사 ";
						}

						if (spam2.getNotCheck().equals("검수불가")) {
							s = s + " / 검수불가 ";
						}
						s = s + "/ " + spam2.getName();
					}
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		return s;
	}

	private void clearTextFileQuery() {
		textFieldFilterURL.clear();
	}

	// private void checkFeedBack() {
	//
	// if (isFeedBack) {
	// isFeedBack = false;
	// // if(checkBoxAll.isSelected()) {
	// spamList = spamRepository.findAllByOrderByWorkdayDesc();
	// // } else {
	// // spamList = spamRepository.findByWorkday(datePicker.getValue().toString());
	// // }
	// }
	//
	// }

	private TableColumn<Spam, Void> createButtonColumn() {
		TableColumn<Spam, Void> column = new TableColumn<Spam, Void>("ACTION");
		column.setStyle("-fx-alignment: CENTER;");

		Callback<TableColumn<Spam, Void>, TableCell<Spam, Void>> cellFactory = new Callback<TableColumn<Spam, Void>, TableCell<Spam, Void>>() {
			@Override
			public TableCell<Spam, Void> call(final TableColumn<Spam, Void> param) {

				final TableCell<Spam, Void> cell = new TableCell<Spam, Void>() {

					private final Button google = new Button();
					private final Button explorer = new Button();
					private final Button result = new Button();
					private final Button text = new Button();
					private final Button delete = new Button();
					private final Button inspect = new Button();
					HBox hBox = new HBox(google, explorer, result, text, inspect);

					{
						google.setGraphic(new ImageView(new Image("/images/google.png")));
						explorer.setGraphic(new ImageView(new Image("/images/explorer.png")));
						result.setGraphic(new ImageView(new Image("/images/bluelist.png")));
						text.setGraphic(new ImageView(new Image("/images/magnify.png")));
						delete.setGraphic(new ImageView(new Image("/images/check.png")));
						inspect.setGraphic(new ImageView(new Image("/images/all.png")));

						google.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							getTableView().getItems().get(getIndex()).setSelected(true);
							spam.setSelected(true);
							String url = spam.getUri();
							verifySite.startBrowser(url, verifySite.getChrome());
							try {
								if (url.contains("http")) {
									verifySite.setClipbord(new URL(spam.getUri()).getHost());
								} else {
									verifySite.setClipbord(spam.getUri());
								}

							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

						});
						explorer.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							spam.setSelected(true);
							verifySite.startBrowser(spam.getUri(), verifySite.getExplore());
							verifySite.setClipbord(spam.getUri());
						});
						result.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							spam.setSelected(true);
							verifySite.eventSearchResult(spam.getUri());
						});

						inspect.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							spam.setSelected(true);
							verifySite.setClipbord(spam.getUri());
							verifySite.eventInspectReult(spam.getUri());
						});

						delete.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							spam.setSelected(true);
							verifySite.setClipbord(spam.getUri());
							try {
								String spamUrl = spam.getUri();
								if (!spamUrl.startsWith("http")) {
									spamUrl = "http://" + spamUrl;
								}
								URL url = new URL(spamUrl);
								textFieldFilterURL.setText(url.getHost());
							} catch (MalformedURLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							// textFieldSearch
							// Spam spam = getTableView().getItems().get(getIndex());
							// eventDelete(spam);
						});
						text.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							getTableView().getItems().get(getIndex()).setSelected(true);
							spam.setSelected(true);

							verifySite.hiddenText(spam.getUri());
						});

						inspect.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							spam.setSelected(true);
							verifySite.setClipbord(spam.getUri());
							verifySite.eventInspectReult(spam.getUri());
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

					@SuppressWarnings("unused")
					private void eventDelete(Spam spam) {
						try {
							spamList = restSpamRepository.deleteBySpam(spam);
							URI uri = new URI(spam.getUri());
							actionButtonFilterURLHander(uri.getHost());
						} catch (URISyntaxException e) {
							e.printStackTrace();
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

	// private static <S,T> TableColumn<S,T> column(String title, Function<S,
	// Property<T>> prop) {
	// TableColumn<S,T> col = new TableColumn<>(title);
	// col.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));
	// return col ;
	// }

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
					showEditingWindow(tableView.getScene().getWindow(), (String) cell.getItem(), newValue -> {
						Spam item = tableView.getItems().get(cell.getIndex());
						item.setComment(newValue);
					});
				}
			});

			return cell;
		});

		makeHeaderWrappable(tableColumn);

		tableColumn.setPrefWidth(80);
		// tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		if (head.equals("uri")) {
			tableColumn.setStyle("-fx-alignment: CENTER-LEFT;  -fx-font-weight: bold; -fx-font-size:16px;");
		} else {
			tableColumn.setStyle("-fx-alignment: CENTER;");
		}
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

		if (currentValue == null) {
			currentValue = "● ";
		} else if (!currentValue.startsWith("● ")) {
			currentValue = "● " + currentValue;
		}
		TextArea textArea = new TextArea(currentValue);

		Button okButton = new Button("OK");
		okButton.setPrefWidth(60);
		okButton.setDefaultButton(true);
		okButton.setOnAction(e -> {
			commitHandler.accept(textArea.getText());
			stage.hide();
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setCancelButton(true);
		cancelButton.setPrefWidth(60);
		cancelButton.setOnAction(e -> stage.hide());

		HBox buttons = new HBox(5, okButton, cancelButton);
		buttons.setAlignment(Pos.CENTER);
		buttons.setPadding(new Insets(5));

		BorderPane root = new BorderPane(textArea, null, null, buttons, null);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	private <S, T> TableColumn<S, T> createColumn(String head, Function<S, Property<T>> prop) {
		TableColumn<S, T> tableColumn = new TableColumn<>(head.toUpperCase());
		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));

		makeHeaderWrappable(tableColumn);

		tableColumn.setPrefWidth(80);
		// tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumn.setSortable(true);
		if (head.equals("url")) {
			tableColumn.setStyle("-fx-alignment: CENTER-LEFT;  -fx-font-weight: bold; -fx-font-size:16px;");
		} else {
			tableColumn.setStyle("-fx-alignment: CENTER;");
		}
		tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<S, T>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<S, T> t) {
			}
		});

		return tableColumn;
	}

	private TableColumn<Spam, Boolean> createCheckBoxHeaderColumn(Function<Spam, Property<Boolean>> prop) {
		TableColumn<Spam, Boolean> tableColumn = new TableColumn<>();
		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));

		tableColumn.setCellFactory(new Callback<TableColumn<Spam, Boolean>, TableCell<Spam, Boolean>>() {
			@Override

			public TableCell<Spam, Boolean> call(TableColumn<Spam, Boolean> param) {
				return new CheckBoxTableCell<Spam, Boolean>() {
					{
						setAlignment(Pos.CENTER);
					}

					@Override
					public void updateItem(Boolean item, boolean empty) {
						if (!empty) {
							TableRow<?> row = getTableRow();

							if (row != null) {
								int rowNo = row.getIndex();

								TableViewSelectionModel<?> sm = getTableView().getSelectionModel();

								if ((boolean) item)
									sm.select(rowNo);
								else
									sm.clearSelection(rowNo);
							}
						}

						super.updateItem(item, empty);
					}
				};
			}
		});
		CheckBox checkAll = new CheckBox();
		checkAll.setOnAction(e -> {
			if (checkAll.isSelected()) {
				tableView.getItems().forEach(p -> p.setSelected(true));
			} else {
				tableView.getItems().forEach(p -> p.setSelected(false));
			}
		});

		tableColumn.setGraphic(checkAll);
		tableColumn.setEditable(true);

		return tableColumn;
	}

	private <S, T> TableColumn<S, T> createCheckBoxColumn(String head, String abbr, Function<S, Property<T>> prop) {

		final TableColumn<S, T> tableColumn = new TableColumn<>();
		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));

		makeHeaderWrappable(tableColumn);
		Label label = new Label(head);
		label.setTooltip(new Tooltip(abbr));
		tableColumn.setGraphic(label);
		tableColumn.setCellFactory(column -> new CheckBoxTableCell<S, T>());
		tableColumn.setPrefWidth(50);

		// tableColumn.setCellFactory(CheckBoxTableCell.forTableColumn(new
		// Callback<Integer, ObservableValue<Boolean>>() {
		// @Override
		// public ObservableValue<Boolean> call(Integer param) {
		// leftNameTableView.getItems().get(param).setIsSelect(true);
		// return leftNameTableView.getItems().get(param).showingProperty();
		// }
		// }));

		return tableColumn;
	}

	public DatePicker getDatePicker() {
		return datePicker;
	}

	public void setDatePicker(DatePicker datePicker) {
		this.datePicker = datePicker;
	}

	public List<Spam> getSpamList() {
		return spamList;
	}

//	public void setSpamList(List<Spam> spamList) {
//		this.spamList = spamList;
//	}

	public CheckBox getWasChecked() {
		return wasChecked;
	}

	public void setWasChecked(CheckBox wasChecked) {
		this.wasChecked = wasChecked;
	}

	private void updateFilterTextField(ObservableList<Spam> obserableList) {
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<Spam> filteredData = new FilteredList<>(obserableList, p -> true);

		// 2. Set the filter Predicate whenever the filter changes.
		textFieldFilterURL.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(item -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();

				if (item.getUri().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches first name.
				} else if (item.getUri().toLowerCase().contains(lowerCaseFilter)) {
					return true; // Filter matches last name.
				}
				return false; // Does not match.
			});
		});

		// 3. Wrap the FilteredList in a SortedList.
		SortedList<Spam> sortedData = new SortedList<>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		tableView.setItems(sortedData);

	}
}