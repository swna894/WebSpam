package com.ever.webSpam.category;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ever.webSpam.io.ExcelManual;
import com.ever.webSpam.io.JsonUtil;
import com.ever.webSpam.utility.Constant;
import com.ever.webSpam.utility.VerifySite;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Callback;

@Component
public class SpamCategoryController implements Initializable, Constant {

	private BorderPane borderPane;
	private TextField textField;
	private Button buttonSave;
	private Button buttonDelete;
	private Button buttonReload;
	private RadioButton checkBoxMain;
	private RadioButton checkBoxChannel;
	private RadioButton checkBoxList;
	private RadioButton checkBoxHam;
	private RadioButton checkBoxHamLow;
	private RadioButton checkBoxSpamAd;
	private RadioButton checkBoxSpamText;
	private RadioButton checkBoxSpamCopy;
	private RadioButton checkBoxSpamIllegal;
	private TableView<SpamCategory> tableView;
	private List<SpamCategory> spamCategoryList;

	private static int BUTTON_WIDTH = 30;

	@Autowired
	RestSpamCategoryRepository restSpamCategoryRepository;

	@Autowired
	SpamCategoryRepository spamCategoryRepository;

	@Autowired
	JsonUtil jsonUtil;

	@Autowired
	ExcelManual excelManual;
	
	@Autowired
	VerifySite verifySite;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	public SpamCategoryController() {
		initialTableView();
		borderPane = new BorderPane();
		borderPane.setPadding(new Insets(7, 7, 7, 7));

		textField = new TextField();
		textField.setPrefWidth(10000);
		textField.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		textField.setOnMouseClicked(event -> {
			String uri = getUri();
			textField.setText(uri);
			textField.positionCaret(textField.getLength());
		});
		textField.setOnAction(event -> {
		
			List<SpamCategory> filterList = spamCategoryList.stream()
					.filter(item -> item.getUri().contains(textField.getText().trim())).collect(Collectors.toList());
			if (filterList.size() > 0) {
				tableView.getItems().clear();
				tableView.getItems().addAll(filterList);
			}
		});

		buttonSave = new Button();
		buttonSave.setMinWidth(BUTTON_WIDTH);
		buttonSave.setPrefWidth(BUTTON_WIDTH);
		buttonSave.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		buttonSave.setOnAction(e -> actionButtonSaveHandler());

		buttonDelete = new Button();
		buttonDelete.setMinWidth(BUTTON_WIDTH);
		buttonDelete.setPrefWidth(BUTTON_WIDTH);
		buttonDelete.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		buttonDelete.setOnAction(e -> actionButtonDeleteHandler());

		buttonReload = new Button();
		buttonReload.setMinWidth(BUTTON_WIDTH);
		buttonReload.setPrefWidth(BUTTON_WIDTH);
		buttonReload.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		buttonReload.setOnAction(e -> initalSpamCategory());

		buttonSave.setGraphic(new ImageView("/images/save.png"));
		buttonDelete.setGraphic(new ImageView("/images/delete.png"));
		buttonReload.setGraphic(new ImageView("/images/refresh.png"));

		HBox hBox1 = new HBox(textField, buttonSave, buttonReload, buttonDelete);
		hBox1.setPadding(new Insets(7, 0, 7, 0));
		hBox1.setSpacing(3);

		checkBoxMain = new RadioButton("서메");
		checkBoxMain.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxMain.setOnAction(e -> actionEventCheckBoxMain());

		checkBoxChannel = new RadioButton("채널");
		checkBoxChannel.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxChannel.setOnAction(e -> actionEventCheckBoxChannel());

		checkBoxList = new RadioButton("컨리");
		checkBoxList.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxList.setOnAction(e -> actionEventCheckBoxList());

		checkBoxHam = new RadioButton("정상");
		checkBoxHam.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxHam.setOnAction(e -> actionEventCheckBoxHam());

		checkBoxHamLow = new RadioButton("정저");
		checkBoxHamLow.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxHamLow.setOnAction(e -> actionEventCheckBoxHamLow());

		checkBoxSpamAd = new RadioButton("비광");
		checkBoxSpamAd.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxSpamAd.setOnAction(e -> actionEventCheckBoxSpamAd());

		checkBoxSpamText = new RadioButton("비텍");
		checkBoxSpamText.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxSpamText.setOnAction(e -> actionEventCheckBoxSpamText());

		checkBoxSpamCopy = new RadioButton("저위");
		checkBoxSpamCopy.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxSpamCopy.setOnAction(e -> actionEventCheckBoxSpamCopy());
		
		checkBoxSpamIllegal = new RadioButton("불사");
		checkBoxSpamIllegal.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		checkBoxSpamIllegal.setOnAction(e -> actionEventCheckBoxSpamIllegal());

	
		ToggleGroup group = new ToggleGroup();
		checkBoxMain.setToggleGroup(group);
		checkBoxChannel.setToggleGroup(group);
		checkBoxList.setToggleGroup(group);
		
		ToggleGroup ham = new ToggleGroup();
		checkBoxHam.setToggleGroup(ham);
		checkBoxHamLow.setToggleGroup(ham);

//		
//		ToggleGroup spam = new ToggleGroup();
//		checkBoxLow.setToggleGroup(spam);
//		checkBoxSpamAd.setToggleGroup(spam);
//		checkBoxSpamText.setToggleGroup(spam);

		HBox hBox2 = new HBox(checkBoxMain, checkBoxChannel, checkBoxList, checkBoxHam, checkBoxHamLow, checkBoxSpamAd,
				checkBoxSpamText, checkBoxSpamCopy, checkBoxSpamIllegal);
		hBox2.setPadding(new Insets(0, 0, 7, 0));
		HBox.setHgrow(checkBoxMain, Priority.ALWAYS);
		HBox.setHgrow(checkBoxChannel, Priority.ALWAYS);
		HBox.setHgrow(checkBoxHamLow, Priority.ALWAYS);
		HBox.setHgrow(checkBoxList, Priority.ALWAYS);
		checkBoxMain.setPrefWidth(100000);
		checkBoxChannel.setPrefWidth(100000);
		checkBoxHam.setPrefWidth(100000);
		checkBoxHamLow.setPrefWidth(100000);
		checkBoxList.setPrefWidth(100000);
		checkBoxSpamAd.setPrefWidth(100000);
		checkBoxSpamText.setPrefWidth(100000);
		checkBoxSpamCopy.setPrefWidth(100000);
		checkBoxSpamIllegal.setPrefWidth(100000);

		VBox VBox = new VBox(hBox1, hBox2);

		borderPane.setTop(VBox);
		borderPane.setCenter(tableView);

	}

	private Object actionButtonDeleteHandler() {
		SpamCategory spamCategory = tableView.getSelectionModel().getSelectedItem();
		spamCategoryRepository.delete(spamCategory);
		threadSaveToExcel();
		clearcheckBox();
		updateTableView();
		return null;
	}

	private Object actionEventCheckBoxList() {
		// List<SpamCategory> confirmList =
		// restSpamCategoryRepository.findAllByOrderByUriAsc();
		List<SpamCategory> filteredList = spamCategoryList.stream().filter(item -> item.getLookList())
				.collect(Collectors.toList());

		updateTableView(filteredList);
		return null;
	}

	private Object actionEventCheckBoxHam() {
		List<SpamCategory> filteredList = spamCategoryList.stream().filter(item -> item.getHam())
				.collect(Collectors.toList());

		updateTableView(filteredList);
		return null;
	}

	private Object actionEventCheckBoxHamLow() {
		List<SpamCategory> filteredList = spamCategoryList.stream().filter(item -> item.getHamLow())
				.collect(Collectors.toList());

		updateTableView(filteredList);
		return null;
	}

	private Object actionEventCheckBoxSpamAd() {
		List<SpamCategory> filteredList = spamCategoryList.stream().filter(item -> item.getSpamAd())
				.collect(Collectors.toList());

		updateTableView(filteredList);
		return null;
	}

	private Object actionEventCheckBoxSpamText() {
		if (checkBoxSpamText.isSelected()) {
			List<SpamCategory> filteredList = spamCategoryList.stream().filter(item -> item.getSpamText())
					.collect(Collectors.toList());

			updateTableView(filteredList);
		} else {
			updateTableView();
		}
		return null;
	}

	private Object actionEventCheckBoxSpamCopy() {
			List<SpamCategory> filteredList = spamCategoryList.stream().filter(item -> item.getSpamCopy())
					.collect(Collectors.toList());

			updateTableView(filteredList);
		return null;
	}
	
	private Object actionEventCheckBoxSpamIllegal() {
		List<SpamCategory> filteredList = spamCategoryList.stream().filter(item -> item.getSpamIllegal())
				.collect(Collectors.toList());

		updateTableView(filteredList);
		return null;
	}

	private Object actionEventCheckBoxChannel() {
		List<SpamCategory> filteredList = spamCategoryList.stream().filter(item -> item.getLookCh())
				.collect(Collectors.toList());

		updateTableView(filteredList);
		return null;
	}

	private Object actionEventCheckBoxMain() {
		List<SpamCategory> filteredList = spamCategoryList.stream().filter(item -> item.getLookMain())
				.collect(Collectors.toList());

		updateTableView(filteredList);
		return null;
	}

	private void updateTableView(List<SpamCategory> filteredList) {
		tableView.getItems().clear();
		tableView.getItems().addAll(filteredList);
	}

	private void updateTableView() {
		spamCategoryList = spamCategoryRepository.findAllByOrderByUriAsc();
		tableView.getItems().clear();
		tableView.getItems().addAll(spamCategoryList);
	}

	private void actionButtonSaveHandler() {

		String uri = textField.getText();
		SpamCategory channelUri = spamCategoryRepository.findByUri(uri);

		if (channelUri == null) {
			channelUri = new SpamCategory();
		}
		channelUri.setUri(uri);
		channelUri.setLookMain(checkBoxMain.isSelected());
		channelUri.setLookCh(checkBoxChannel.isSelected());
		channelUri.setLookList(checkBoxList.isSelected());
		channelUri.setHam(checkBoxHam.isSelected());
		channelUri.setHamLow(checkBoxHamLow.isSelected());
		channelUri.setSpamAd(checkBoxSpamAd.isSelected());
		channelUri.setSpamText(checkBoxSpamText.isSelected());
		channelUri.setSpamCopy(checkBoxSpamCopy.isSelected());
		channelUri.setSpamIllegal(checkBoxSpamIllegal.isSelected());

		if (!uri.isEmpty() && uri != null) {
			spamCategoryRepository.save(channelUri);
			threadSaveToExcel();
		}
		// textField.clear();
		clearcheckBox();
		updateTableView();
	}

	private void threadSaveToExcel() {
		Runnable runnable = () -> {
			spamCategoryList = spamCategoryRepository.findAllByOrderByUriAsc();
			excelManual.writeCategoryList(spamCategoryList);
			// jsonUtil.saveJsonToCategoryFile(spamCategoryList);
		};
		Thread thread = new Thread(runnable);
		thread.start();
	}

	public void show() {
		// updateTableView();

		Stage stage = new Stage();
		stage.setTitle("Insert Category");

		if (borderPane.getScene() == null) {
			Scene scene = new Scene(borderPane, 760, 400);
			stage.setScene(scene);
		} else {
			stage.setScene(borderPane.getScene());
		}

		stage.setAlwaysOnTop(true);
		stage.setOnCloseRequest(event -> {
			clearAll();
			stage.close();
		});
		stage.show();

		initalSpamCategory();
	}

	private void clearAll() {
		textField.clear();
		clearcheckBox();
	}

	private void clearcheckBox() {
		checkBoxChannel.setSelected(false);
		checkBoxHam.setSelected(false);
		checkBoxHamLow.setSelected(false);
		checkBoxList.setSelected(false);
		checkBoxMain.setSelected(false);
		checkBoxSpamAd.setSelected(false);
		checkBoxSpamIllegal.setSelected(false);
		checkBoxSpamText.setSelected(false);
		checkBoxSpamCopy.setSelected(false);
	}

	public void initalSpamCategory() {
		// if (new File(CATEGORY_FILE).exists()) {
		// spamCategoryList = jsonUtil.convertJsonToCategoryList(CATEGORY_FILE);
		spamCategoryList = excelManual.readCategoryList();
		spamCategoryRepository.saveAll(spamCategoryList);
		// }
		// convertBeanToJson(docList);
		clearAll();
		if (spamCategoryList != null) {
			ObservableList<SpamCategory> observableList = FXCollections.observableArrayList(spamCategoryList);
			tableView.getItems().clear();
			tableView.getItems().addAll(observableList);
		}
	}

	private String getUri() {

		try {
			String clipText = getClipboardContents();
			URI uri = new URI(clipText.trim());
			return uri.getHost();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * CNTL + C 발생시 문자열을 가지고 온다
	 * 
	 * @param
	 * @return 클립보드의 문자열
	 */
	protected String getClipboardContents() {
		Clipboard clipboard = Clipboard.getSystemClipboard();
		if (clipboard.hasString()) {
			return clipboard.getString();
		}
		return "";
	}

	@SuppressWarnings("unchecked")
	private void initialTableView() {

		tableView = new TableView<SpamCategory>();
		tableView.setEditable(true);
		TableColumn<SpamCategory, Void> columnButton = createButtonColumn();
		columnButton.setMinWidth(35); // 130
		columnButton.setPrefWidth(35);

		TableColumn<SpamCategory, String> columnUri = createColumn("uri");

		TableColumn<SpamCategory, Boolean> columnLookMain = createCheckBoxColumn("서메", "서비스 메인페이지");
		TableColumn<SpamCategory, Boolean> columnLookCh = createCheckBoxColumn("채메", "채널 메인페이지");
		TableColumn<SpamCategory, Boolean> columnLookCont = createCheckBoxColumn("컨테", "컨텐트 페이지");
		TableColumn<SpamCategory, Boolean> columnLookList = createCheckBoxColumn("컨리", "컨텐츠리스트 페이지");

		TableColumn<SpamCategory, Boolean> columnHam = createCheckBoxColumn("정상", "정상 컨텐트");
		TableColumn<SpamCategory, Boolean> columnHamLow = createCheckBoxColumn("정저", "정상 컨텐트 저품질");
		TableColumn<SpamCategory, Boolean> columnSpamAd = createCheckBoxColumn("비광", "비정상광고 컨텐트");
		TableColumn<SpamCategory, Boolean> columnSpamText = createCheckBoxColumn("비텍", "비정상 텍스트");
		TableColumn<SpamCategory, Boolean> columnSpamCopy = createCheckBoxColumn("저위", "저작권 위반");
		TableColumn<SpamCategory, Boolean> columnSpamIllegal = createCheckBoxColumn("불사", "불법사이트");

		columnUri.setCellValueFactory(cellData -> cellData.getValue().uriProperty());

		columnLookMain.setCellValueFactory(cellData -> cellData.getValue().lookMainProperty());
		columnLookCh.setCellValueFactory(cellData -> cellData.getValue().lookChProperty());
		columnLookCont.setCellValueFactory(cellData -> cellData.getValue().lookContProperty());
		columnLookList.setCellValueFactory(cellData -> cellData.getValue().lookListProperty());
		columnHam.setCellValueFactory(cellData -> cellData.getValue().hamProperty());
		columnHamLow.setCellValueFactory(cellData -> cellData.getValue().hamLowProperty());
		columnSpamAd.setCellValueFactory(cellData -> cellData.getValue().spamAdProperty());
		columnSpamText.setCellValueFactory(cellData -> cellData.getValue().spamTextProperty());
		columnSpamCopy.setCellValueFactory(cellData -> cellData.getValue().spamCopyProperty());
		columnSpamIllegal.setCellValueFactory(cellData -> cellData.getValue().spamIllegalProperty());

		// nameCol.getStyleClass().add("table-left");
		// columnUri.setPrefWidth(150);

		TableColumn<SpamCategory, SpamCategory> numberCol = new TableColumn<SpamCategory, SpamCategory>("#");
		numberCol.setStyle("-fx-alignment: CENTER;");
		numberCol.setPrefWidth(40);
		numberCol.setCellValueFactory(
				new Callback<CellDataFeatures<SpamCategory, SpamCategory>, ObservableValue<SpamCategory>>() {
					@SuppressWarnings("rawtypes")
					@Override
					public ObservableValue<SpamCategory> call(CellDataFeatures<SpamCategory, SpamCategory> p) {
						return new ReadOnlyObjectWrapper(p.getValue());
					}
				});

		numberCol.setCellFactory(
				new Callback<TableColumn<SpamCategory, SpamCategory>, TableCell<SpamCategory, SpamCategory>>() {
					@Override
					public TableCell<SpamCategory, SpamCategory> call(TableColumn<SpamCategory, SpamCategory> param) {

						return new TableCell<SpamCategory, SpamCategory>() {
							@Override
							protected void updateItem(SpamCategory item, boolean empty) {
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

		tableView.getColumns().addAll(columnButton, numberCol, columnUri, columnLookMain, columnLookCh, columnLookList,
				columnHam, columnHamLow, columnSpamAd, columnSpamText, columnSpamCopy, columnSpamIllegal);
	}

	private TableColumn<SpamCategory, Void> createButtonColumn() {
		TableColumn<SpamCategory, Void> column = new TableColumn<SpamCategory, Void>("");
		column.setStyle("-fx-alignment: CENTER;");

		Callback<TableColumn<SpamCategory, Void>, TableCell<SpamCategory, Void>> cellFactory = new Callback<TableColumn<SpamCategory, Void>, TableCell<SpamCategory, Void>>() {
			@Override
			public TableCell<SpamCategory, Void> call(final TableColumn<SpamCategory, Void> param) {

				final TableCell<SpamCategory, Void> cell = new TableCell<SpamCategory, Void>() {

					private final Button google = new Button();
					HBox hBox = new HBox(google);

					{
						google.setGraphic(new ImageView(new Image("/images/google.png")));
						google.setOnAction((ActionEvent event) -> {
							SpamCategory spam = getTableView().getItems().get(getIndex());
							verifySite.startBrowser(spam.getUri(), verifySite.getChrome());
							verifySite.setClipbord(spam.getUri());
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



	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TableColumn<SpamCategory, Boolean> createCheckBoxColumn(String head, String abbr) {
		final TableColumn<SpamCategory, Boolean> tableColumn = new TableColumn<>(head);
		tableColumn.setCellFactory(column -> new CheckBoxTableCell());
		tableColumn.setPrefWidth(50);

		return tableColumn;
	}

	private TableColumn<SpamCategory, String> createColumn(String head) {
		TableColumn<SpamCategory, String> tableColumn = new TableColumn<>(head.toUpperCase());

		tableColumn.setPrefWidth(200);

		tableColumn.setSortable(false);
		tableColumn.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<SpamCategory, String>>() {
			@Override
			public void handle(TableColumn.CellEditEvent<SpamCategory, String> t) {
			}
		});

		return tableColumn;
	}

	public List<SpamCategory> getSpamCategoryList() {
		return spamCategoryList;
	}
}
