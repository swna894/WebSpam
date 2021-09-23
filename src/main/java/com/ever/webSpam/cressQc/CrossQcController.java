package com.ever.webSpam.cressQc;



import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ever.webSpam.category.RestSpamCategoryRepository;
import com.ever.webSpam.spam.Spam;
import com.ever.webSpam.utility.Constant;
import com.ever.webSpam.utility.VerifySite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;
import javafx.util.Callback;

@Component
public class CrossQcController<S> implements Initializable, Constant {
	Logger LOG = LoggerFactory.getLogger(CrossQcController.class);
	@Autowired
	CrossExcel crossExcel;

	@Autowired
	RestSpamCategoryRepository spamCategoryRepository;
	
	@Autowired
	VerifySite verifySite;
	

	private ObservableList<String> levelChoice = FXCollections.observableArrayList("", "오처리", "참고", "문의");
	private Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
	private BorderPane borderPane;
	private HBox hBox;
	private TableView<Spam> tableView;
	private Button buttonSave;
	private Button buttonReload;
	private Button buttonAutoReview;
	private ComboBox<String> comboBoxCategory;
	private ComboBox<String> comboBoxSite;
	
	private TextField textField;
	private TextField textFieldWorker;
	private Label label;
	private File file;
	private CheckBox wasChecked;
	
	private List<Spam> spamList;
	private List<Spam> sitedSpamList;
	private List<Spam> filtedSpamList;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
	}

	private void initialComponent() {
		LOG.info("========== start initialize ");
		buttonSave = new Button("_SAVE ");
		buttonSave.setPrefWidth(200);
		buttonSave.setGraphic(new ImageView(new Image("/images/save.png")));
		buttonSave.setTooltip(new Tooltip("저장"));
		buttonSave.setOnAction(e -> actionButtonSaveHandler());
		buttonSave.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonReload = new Button("_RELOAD");
		buttonReload.setPrefWidth(200);
		buttonReload.setGraphic(new ImageView(new Image("/images/reload.png")));
		buttonReload.setTooltip(new Tooltip("불러오기"));
		buttonReload.setOnAction(e -> actionButtonReloadHandler());
		buttonReload.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		
		buttonAutoReview = new Button("OPEN");
		buttonAutoReview.setOnAction(e -> actionButtonAutoReview());
		buttonAutoReview.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
//		buttonTimeReload = new Button();
//		buttonTimeReload.setPrefWidth(200);
//		buttonTimeReload.setGraphic(new ImageView(new Image("/images/refresh.png")));	
//		buttonTimeReload.setOnAction(e -> actionButtonTimeReloadHandler());
//		buttonTimeReload.setFont(Font.font("Verdana", FontWeight.BOLD, 14));


		wasChecked = new CheckBox("검수");
		wasChecked.setSelected(false);
		wasChecked.setOnAction(e -> {
			filtedSpamList = spamList;
			filtedSpamList = actionComboBoxCategoryHandler();
			Platform.runLater(() -> reloadTable(filtedSpamList));
		});
		
		comboBoxCategory = new ComboBox<String>();
		comboBoxCategory.setPromptText("QC");
		comboBoxCategory.setItems(observableListSpam);
		comboBoxCategory.setStyle("-fx-font: 14px \"Serif\"; -fx-font-weight: bold;");
		comboBoxCategory.valueProperty().addListener((obs, oldVal, newVal) -> {
			filtedSpamList = spamList;
			List<Spam> categorySpam = actionComboBoxCategoryHandler();
			Platform.runLater(() -> reloadTable(categorySpam));
		});
		
		comboBoxSite = new ComboBox<String>();
		comboBoxSite.setPromptText("SITE");
		comboBoxSite.setItems(observableListSite);
		comboBoxSite.setStyle("-fx-font: 14px \"Serif\"; -fx-font-weight: bold;");
		comboBoxSite.valueProperty().addListener((obs, oldVal, newVal) -> {
			filtedSpamList = spamList;
			sitedSpamList = actionComboBoxSite();
//			if (comboBoxWorker.getSelectionModel().getSelectedIndex() != 0) {
//				filtedSpamList = actionComboBoxWorkerHandler();
//			}
//			if (comboBoxCategory.getSelectionModel().getSelectedIndex() != 0) {
//				filtedSpamList = actionComboBoxCategoryHandler();
//			}
			Platform.runLater(() -> reloadTable(filtedSpamList));
			// comboBoxWhite.getSelectionModel().clearSelection();
		});
		
		textField = new TextField();
		textField.setPrefWidth(400);
		textField.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		textField.setOnMouseClicked(e -> {
			if (e.getClickCount() == 1) {
				actionTextFieldHandler();
			}

			if (e.getClickCount() == 2) {
				textField.clear();
				tableView.getItems().clear();
				tableView.getItems().setAll(spamList);
			}
		});

		textField.setOnAction(e -> actionTextFieldHandler());

		label = new Label();
		label.setFont(Font.font("Verdana", FontWeight.BOLD, 12));

		textFieldWorker = new TextField();
		textFieldWorker.setPrefWidth(80);
		textFieldWorker.setEditable(false);
		textFieldWorker.setStyle("-fx-alignment: CENTER;");
		textFieldWorker.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		tableView = new TableView<Spam>();

		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				String url = tableView.getSelectionModel().getSelectedItem().getUri().trim();
				verifySite.setClipbord(url);

				try {
					URI uri = new URI(url);
					String selectedUri = uri.getHost();
					if (selectedUri == null) {
						textField.setText(url);
					} else {
						textField.setText(selectedUri);
					}

				} catch (URISyntaxException e) {
					e.printStackTrace();
				}

			}
		});

		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);

		borderPane = new BorderPane();
		hBox = new HBox(textFieldWorker, buttonSave, buttonReload, wasChecked, comboBoxSite, comboBoxCategory, textField, label, buttonAutoReview , region);
		hBox.setStyle("-fx-alignment: CENTER-LEFT;");
		hBox.setSpacing(10);
		hBox.setPadding(new Insets(0, 7, 7, 0));

		borderPane.setPadding(new Insets(7));
		borderPane.setTop(hBox);
		borderPane.setCenter(tableView);
		LOG.info("========== start initialize ");
	}

	private List<Spam> actionComboBoxSite() {
		Boolean isSeleted = wasChecked.isSelected();
		String review = comboBoxSite.getSelectionModel().getSelectedItem();


		switch (comboBoxSite.getSelectionModel().getSelectedIndex()) {
		case 0:
			if (comboBoxCategory.getSelectionModel().getSelectedIndex() == 0) {
				reloadTable(filtedSpamList);
			}

			break;
		case 1:
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> review.equals("서비스 메인 페이지") && item.getLookMain()) // 서메
						.collect(Collectors.toList());
			break;
		case 2:
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> review.equals("채널 메인 페이지") && item.getLookCh()) // 채널
						.collect(Collectors.toList());
			break;
		case 3:
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> review.equals("컨텐츠 리스트 페이지") && item.getLookList()) // 리스트
						.collect(Collectors.toList());
			break;
		case 4:
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> review.equals("컨텐트 페이지") && item.getLookCont()) // 켄테
						.collect(Collectors.toList());
			break;
		case 5:
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> !item.getNotCheck().equals("검수불가") && !item.getBooleanDefer()) // 켄테
						.filter(item -> !item.getLookMain() && !item.getLookCh() && !item.getLookList()
								&& !item.getLookCont()) // 켄테
						.collect(Collectors.toList());
			break;
		case 6:
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getNotCheck().equals("검수불가") || item.getBooleanDefer())
						.collect(Collectors.toList());
			break;
		case 7:
				filtedSpamList = filtedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> item.getBooleanDefer())
						.collect(Collectors.toList());
			break;
		case 8:
			break;
		default:
			System.out.println("그 외의 숫자");
		}

		return filtedSpamList;

	}
	
	private void actionButtonAutoReview() {
		int i = 0;
		for (Spam spam : tableView.getItems()) {
			if (!spam.getSelected()) {
		
				try {
					spam.setSelected(true);
					String url = spam.getUri();
					verifySite.startBrowser(url, verifySite.getChrome());			
					i++;
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} // 1초 대기
//				if (comboBoxSite.getSelectionModel().getSelectedIndex() == 5) {
//					verifySite.eventSearchResultAutoReview(spam.getUri()); // 검색결과
//				     verifySite.hiddenText(spam.getUri());   // 저장된 텍스트
//					verifySite.eventInspectReultAutoReview(spam.getUri()); // 결과 수정
//				}
			}
			if(i == 10) {
				break;
			}
		}
	}

	private void reloadTable(List<Spam> filedList) {
		if (filedList != null) {
			// tableView.getItems().clear();
			// tableView.getItems().addAll(filedList);
			tableView.setItems(FXCollections.observableArrayList(filedList));
		}
	}
	
	private List<Spam> actionComboBoxCategoryHandler() {
		Boolean isSeleted = wasChecked.isSelected();
		String category = comboBoxCategory.getSelectionModel().getSelectedItem();
		List<Spam> categoryList = null;
		switch (comboBoxCategory.getSelectionModel().getSelectedIndex()) {
		case 0:
			tableView.setItems(FXCollections.observableArrayList(sitedSpamList));
			break;
		case 1:

			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("비정상 광고 컨테트") && item.getSpamAd()) // 비광
						.collect(Collectors.toList());
			break;
		case 2:
	
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("비정상 텍스트") && item.getSpamText()) // 비텍
						.collect(Collectors.toList());
			break;
		case 3:
	
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("스펨 사이트로 리디렉션") && item.getSpamRedir())
						.collect(Collectors.toList());
			break;
		case 4:
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("악성 소프트웨어") && item.getSpamMalware()) // 악성
						.collect(Collectors.toList());
			break;
		case 5:
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("저작권위반") && item.getSpamCopy()).collect(Collectors.toList());
			break;
		// .filter(item -> category.equals("악성 소프트웨어") && item.getSpamCopy()) // 악성
		// .filter(item -> category.equals("저작권위반") && item.getSpamIllegal()) // 저위
		case 6:
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("음란물") && item.getSpamPorn()) // 음란
						.collect(Collectors.toList());
			break;
		case 7:
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("기만적 컨텐트") && item.getSpamDecep()) // 기컨
						.collect(Collectors.toList());
			break;
		case 8:
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("웹순위 조작 활동") && item.getSpamManip()) // 웹조
						.collect(Collectors.toList());
			break;
		case 9:
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("불법 사이트") && item.getSpamIllegal()) // 불사
						.collect(Collectors.toList());
			break;
		case 10:
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("정상 컨텐트") && item.getHam()) // 정상
						.collect(Collectors.toList());
			break;
		case 11:
			categoryList = sitedSpamList.stream().filter(item -> isSeleted == item.getSelected())
						.filter(item -> category.equals("정상 컨텐트 저품질") && item.getHamLow() ||
								category.equals("정상 컨텐트 저품질") && item.getHamFish()) // 정저
						.collect(Collectors.toList());
			break;
		default:
			System.out.println("그 외의 숫자");
		}
		return categoryList;
	}
	
	
	@SuppressWarnings("unused")
	private Object actionButtonTimeReloadHandler() {
		new Timer().schedule(
			    new TimerTask() {

			        @Override
			        public void run() {
			        	 Platform.runLater(() -> {			        	       
			        	        	buttonSave.fire();
						        	System.err.println("fire");		        	       
			        	    });
			        	
			        }
			    }, 0, 50000);
		return null;
	}

	private void actionTextFieldHandler() {
		String string = textField.getText().trim();
		if (string.length() > 0) {
			List<Spam> filterList = spamList.stream().filter(item -> item.getUri().contains(string))
					.collect(Collectors.toList());
			tableView.getItems().clear();
			tableView.getItems().setAll(filterList);
		} else {
			tableView.getItems().clear();
			tableView.getItems().setAll(spamList);
		}
	}

	private Object actionButtonReloadHandler() {
		buttonSave.fire();
		initialSpamList(file);
		tableView.getItems().clear();
		tableView.getItems().addAll(spamList);

		try {
			Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler " + file);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		return null;
	}

	private Object actionButtonSaveHandler() {
		Boolean result = crossExcel.writeSpam(file, spamList);
		if (result) {
			label.setText("저장되었습니다.");
			label.setStyle("-fx-alignment: CENTER; -fx-text-fill: #99e699;");
		} else {
			label.setText("엑셀파일을 사용 중이기때문에 엑세스할 수 없습니다 !");
			label.setStyle("-fx-alignment: CENTER; -fx-text-fill: #ff9999;");
		}
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
		return null;
	}

	public void initialSpamList(File file) {
		spamList = crossExcel.getSpamList(file);
		textFieldWorker.setText(spamList.get(0).getName());

	}

	public void start(File file) {
		this.file = file;
		initialComponent();
		initialTableView();
		initialSpamList(file);

		tableView.getItems().clear();
		tableView.getItems().addAll(spamList);
		// handlerButtonEndHandler();

		Stage stage = new Stage();
		stage.getIcons().add(new Image("/images/cross.png"));
		stage.setTitle("Cross QC");
		Scene scene = new Scene(borderPane);
		scene.getStylesheets().add("styles/styles.css");
		stage.setScene(scene);
		stage.setWidth(primaryScreenBounds.getWidth());
		stage.setHeight(primaryScreenBounds.getHeight());
		stage.setOnCloseRequest(event -> {
			stage.close();
		});
		stage.setOnShowing(new EventHandler<WindowEvent>() {
			@Override
			public void handle(WindowEvent event) {
			}
		});
		
		stage.setOnCloseRequest(event -> {
		    buttonSave.fire();
		});
		
		stage.show();
	}

	void handlerButtonEndHandler() {
		Platform.runLater(() -> tableView.scrollTo(tableView.getItems().size() - 1));
	}

	@SuppressWarnings("unchecked")
	private void initialTableView() {

		// tableView.getStyleClass().add("spam");

		tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				String url = tableView.getSelectionModel().getSelectedItem().getUri().trim();
				verifySite.setClipbord(url);

			}
		});

		tableView.setEditable(true);
		TableColumn<Spam, String> columnUri = createColumn("uri", Spam::uriProperty);
		TableColumn<Spam, String> columnName = createColumn("name", Spam::nameProperty);
		TableColumn<Spam, String> columnScope = createColumn("scope", Spam::scopeProperty);
		TableColumn<Spam, String> columnEmail = createColumn("email", Spam::emailProperty);
		TableColumn<Spam, String> columnNotCheck = createColumn("불가", Spam::notCheckProperty);
		TableColumn<Spam, String> columnDefer = createColumn("보류", Spam::deferProperty);
		TableColumn<Spam, String> columnQc = createComboBoxColumn("QC", Spam::qcProperty);

		TableColumn<Spam, String> columnComment = createEditColumn("comment", Spam::commentProperty);

		TableColumn<Spam, Boolean> columnSelected = createCheckBoxHeaderColumn(Spam::selectedProperty);

		TableColumn<Spam, Boolean> columnLookMain = createCheckBoxColumn("서메", "서비스 메인페이지", Spam::lookMainProperty);
		TableColumn<Spam, Boolean> columnLookCh = createCheckBoxColumn("채메", "채널 메인페이지", Spam::lookChProperty);
		TableColumn<Spam, Boolean> columnLookList = createCheckBoxColumn("컨리", "컨텐츠 리스트 페이지", Spam::lookListProperty);
		TableColumn<Spam, Boolean> columnLookCont = createCheckBoxColumn("컨테", "컨텐트 페이지", Spam::lookContProperty);

		TableColumn<Spam, Boolean> columnHam = createCheckBoxColumn("정상", "정상 컨텐트", Spam::hamProperty);
		TableColumn<Spam, Boolean> columnHamLow = createCheckBoxColumn("정저", "정상 컨텐트 저품질", Spam::hamLowProperty);
		TableColumn<Spam, Boolean> columnHamFish = createCheckBoxColumn("정낚", "정상 컨텐트 저품질 낚시", Spam::hamFishProperty);
		
		TableColumn<Spam, Boolean> columnSpamAd = createCheckBoxColumn("비광", "비정상 광고 컨텐트", Spam::spamAdProperty);
		TableColumn<Spam, Boolean> columnSpamText = createCheckBoxColumn("비텍", "비장상 텍스트", Spam::spamTextProperty);
		TableColumn<Spam, Boolean> columnSpamRedir = createCheckBoxColumn("스리", "스펨 사이트로 리디렉션", Spam::spamRedirProperty);
		TableColumn<Spam, Boolean> columnSpamMalware = createCheckBoxColumn("악소", "악성 소프트웨어 사이트", Spam::spamMalwareProperty);

		TableColumn<Spam, Boolean> columnSpamCopy = createCheckBoxColumn("저위", "저작권위반", Spam::spamCopyProperty);
		TableColumn<Spam, Boolean> columnSpamPorn = createCheckBoxColumn("음란", "음란물", Spam::spamPornProperty);
		TableColumn<Spam, Boolean> columnSpamDecep = createCheckBoxColumn("기컨", "기만적 컨텐트", Spam::spamDecepProperty);
		TableColumn<Spam, Boolean> columnSpamManip = createCheckBoxColumn("웹조", "웹순위 조작활동", Spam::spamManipProperty);
		TableColumn<Spam, Boolean> columnSpamIllegal = createCheckBoxColumn("불사", "불법 사이트", Spam::spamIllegalProperty);		
		
//		columnLookMain.setCellValueFactory(cellData -> {
//            Spam cellValue = cellData.getValue();     
//            BooleanProperty property = cellValue.lookMainProperty();
//            property.addListener((observable, oldValue, newValue) -> cellValue.setLookMain(newValue));
//            updateSpam(cellData.getValue());
//            return property;
//        });
//		
		
		columnSpamIllegal.setCellValueFactory(cellData -> {
            Spam cellValue = cellData.getValue();
            BooleanProperty property = cellValue.spamIllegalProperty();
            property.addListener((observable, oldValue, newValue) -> cellValue.setSpamIllegal(newValue));
            updateSpam(cellData.getValue());
            return property;
        });
		
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
		columnQc.setStyle("-fx-alignment: CENTER");

		columnUri.setPrefWidth(400);
		columnName.setPrefWidth(70);
		columnScope.setPrefWidth(70);
		columnEmail.setPrefWidth(100);
		columnNotCheck.setPrefWidth(70);
		columnDefer.setPrefWidth(50);
		columnComment.setPrefWidth(400);
		columnQc.setPrefWidth(70);

		columnComment.setSortable(true);

		TableColumn<Spam, Spam> numberCol = new TableColumn<Spam, Spam>("#");
		numberCol.setStyle("-fx-alignment: CENTER;");
		numberCol.setMinWidth(40);
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
							setText(this.getTableRow().getIndex() + 1 + "");
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

		TableColumn<Spam, Void> columnNum = createButtonColumn();
		columnNum.setStyle("-fx-alignment: CENTER;");

		tableView.getColumns().addAll(numberCol, columnSelected, columnNum, columnUri, columnName, columnScope,
				columnNotCheck, columnDefer, columnLookMain, columnLookCh, columnLookList, columnLookCont, columnHam,
				columnHamLow, columnHamFish, columnSpamAd, columnSpamText, columnSpamRedir, columnSpamMalware, columnSpamCopy,
				columnSpamPorn, columnSpamDecep, columnSpamManip, columnSpamIllegal, columnQc, columnComment);

	}

	private void updateSpam(Spam spam) {		
		//if(initEnd) {
			 //Platform.runLater(() -> crossExcel.writeSpam(file, spam));
			//crossExcel.writeSpam(file, spam);
		//}
		
	}
	private TableColumn<Spam, Void> createButtonColumn() {
		TableColumn<Spam, Void> column = new TableColumn<Spam, Void>("ACTION");
		// column.setStyle("-fx-alignment: CENTER;");

		Callback<TableColumn<Spam, Void>, TableCell<Spam, Void>> cellFactory = new Callback<TableColumn<Spam, Void>, TableCell<Spam, Void>>() {
			@Override
			public TableCell<Spam, Void> call(final TableColumn<Spam, Void> param) {

				final TableCell<Spam, Void> cell = new TableCell<Spam, Void>() {

					private final Button google = new Button();
					private final Button explorer = new Button();
					private final Button result = new Button();
					private final Button text = new Button();
					HBox hBox = new HBox(google, explorer, result, text);

					{	
						google.setGraphic(new ImageView(new Image("/images/google.png")));
						explorer.setGraphic(new ImageView(new Image("/images/explorer.png")));
						result.setGraphic(new ImageView(new Image("/images/bluelist.png")));  
						text.setGraphic(new ImageView(new Image("/images/magnify.png")));
						
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
						text.setOnAction((ActionEvent event) -> {
							Spam spam = getTableView().getItems().get(getIndex());
							getTableView().getItems().get(getIndex()).setSelected(true);
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
				cell.setStyle("-fx-alignment: CENTER;");
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

	private TableColumn<Spam, String> createComboBoxColumn(String head, Function<Spam, Property<String>> prop) {
		TableColumn<Spam, String> tableColumn = new TableColumn<>(head);
		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));
		tableColumn.setCellFactory(ComboBoxTableCell.forTableColumn(levelChoice));
		tableColumn.setOnEditCommit(e -> updateComment(e));

		return tableColumn;
	}

	private Object updateComment(CellEditEvent<Spam, String> e) {
		Spam spam = e.getRowValue();
		String newValue = e.getNewValue();
		if (newValue.equals("참고")) {
			spam.setQc("참고");
			spam.setComment("검수시점에는 검수불가로 보여집니다.");
		} else if (newValue.trim().equals("")) {
			spam.setQc("");
			spam.setComment("");
		} else {
			spam.setQc(newValue);
		}
		buttonSave.fire();
		// crossExcel.writeSpam(file, spam);
		return null;
	}

	@SuppressWarnings("hiding")
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
		tableColumn.setSortable(false);
		if (head.equals("uri")) {
			tableColumn.setStyle("-fx-alignment: CENTER-LEFT;  -fx-font-weight: bold; -fx-font-size:16px;");
		} else {
			tableColumn.setStyle("-fx-alignment: CENTER;");
		}

		return tableColumn;
	}

	private void showEditingWindow(Window owner, String currentValue, Consumer<String> commitHandler) {
		Stage stage = new Stage();
		stage.initOwner(owner);
		stage.initModality(Modality.APPLICATION_MODAL);

		if (currentValue == null || currentValue.trim().length() == 0) {
			//currentValue = "\n\r       [사이트형태]\n\r       [스 팸 체 크]";
		}
		TextArea textArea = new TextArea(currentValue);

		Button okButton = new Button("OK");
		okButton.setPrefWidth(60);
		okButton.setDefaultButton(true);
		okButton.setOnAction(e -> {
			commitHandler.accept(textArea.getText());
			actionButtonSaveHandler();
			stage.hide();
		});

		Button cancelButton = new Button("Cancel");
		cancelButton.setPrefWidth(60);
		cancelButton.setCancelButton(true);
		cancelButton.setOnAction(e -> stage.hide());

		HBox buttons = new HBox(5, okButton, cancelButton);
		buttons.setAlignment(Pos.CENTER);
		buttons.setPadding(new Insets(5));

		BorderPane root = new BorderPane(textArea, null, null, buttons, null);
		Scene scene = new Scene(root);
		stage.setScene(scene);
		stage.show();
	}

	@SuppressWarnings("hiding")
	private <S, T> TableColumn<S, T> createColumn(String head, Function<S, Property<T>> prop) {
		TableColumn<S, T> tableColumn = new TableColumn<>(head.toUpperCase());
		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));

		makeHeaderWrappable(tableColumn);

		tableColumn.setPrefWidth(80);
		// tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumn.setSortable(false);
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

	@SuppressWarnings("hiding")
	private <S, T> TableColumn<S, T> createCheckBoxColumn(String head, String abbr, Function<S, Property<T>> prop) {
		final TableColumn<S, T> tableColumn = new TableColumn<>();
		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));
		makeHeaderWrappable(tableColumn);
		Label label = new Label(head);
		label.setTooltip(new Tooltip(abbr));
		tableColumn.setGraphic(label);
        tableColumn.setCellFactory(column -> new CheckBoxTableCell<S, T>());
		tableColumn.setPrefWidth(50);

		
//		tableColumn.setCellFactory(new Callback<TableColumn<S, T>, TableCell<S, T>>() {
//			@Override
//			public TableCell<S, T> call(TableColumn<S, T> param) {
//				return new CheckBoxTableCell<S, T>() {
//					{
//						setAlignment(Pos.CENTER);
//					}
//
//					@Override
//					public void updateItem(T item, boolean empty) {
//						if (!empty) {
//							TableRow<?> row = getTableRow();
//							// System.err.println(row.getIndex() + " ? " + intialWrite + " -> " +
//							// spamList.size() + " : " + spamList.size() * 15 + " ?? " +
//							// spamList.get(row.getIndex()).getUri());
//							if (row != null) {
//								//updateLabel(row.getIndex());
//								Spam spam = spamList.get(row.getIndex());
//								System.err.println(spam);
//								//crossExcel.writeSpam(file, spam);
//							}
//
//						}
//
//						super.updateItem(item, empty);
//					}
//
//				};
//			}
//		});

		return tableColumn;
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private void updateLabel(int index) {
		Spam spam = spamList.get(index);
		SpamCheck spamCheck = spam.getSpamCheck();
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Object> objectAsMap = mapper.convertValue(spamCheck, Map.class);
		while (objectAsMap.values().remove(false))
		try {
			String jsonInString = mapper.writeValueAsString(objectAsMap);
			// System.err.println( " -> " + spamList.size() + " : " + spamList.size() * 15 +
			// " ?? " + spamList.get(index).getUri());
			if (!spam.getLabel().equals(jsonInString)) {
				spam.setLabel(jsonInString);
				crossExcel.writeSpam(file, spam);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
