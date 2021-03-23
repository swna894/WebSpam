package com.ever.webSpam.question;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.ever.webSpam.category.RestSpamCategoryRepository;
import com.ever.webSpam.category.SpamCategoryController;
import com.ever.webSpam.io.ExcelManual;
import com.ever.webSpam.spam.Spam;
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
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TableView.TableViewSelectionModel;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;

@Controller
public class QuestionController implements Initializable, Constant {

	private String pattern = "yyyy-MM-dd";

	private BorderPane borderPane;
	private HBox hBox;

	private Button buttonRefresh;
	private Button buttonSave;
	private Button buttonDelete;

	private DatePicker datePicker;

	private TextField textFieldFilterURL;
	private TableView<Question> tableView;

	private List<Question> questionList;

	@Autowired
	SpamCategoryController spamCategoryController;

	@Autowired
	RestSpamCategoryRepository restSpamCategoryRepository;

	@Autowired
	ExcelManual excelManual;

	@Autowired
	VerifySite verifySite;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.setProperty("java.awt.headless", "false");
	}

	private void initialPane() {
		borderPane = new BorderPane();
		hBox = new HBox();
		tableView = new TableView<Question>();
		borderPane.setPadding(new Insets(7));
		borderPane.setTop(hBox);
		borderPane.setCenter(tableView);

	}

	private void initialComponent() {

		textFieldFilterURL = new TextField();
		textFieldFilterURL.setFont(Font.font("Verdana", FontWeight.BOLD, 14));
		textFieldFilterURL.setPromptText("Enter URI");
		textFieldFilterURL.setPrefWidth(300);
		textFieldFilterURL.setOnMouseClicked(e -> actionDoubleClickCleanHandler());
		textFieldFilterURL.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				// actionButtonFilterURLHander(textFieldFilterURL.getText());
			}
		});

		buttonSave = new Button();
		buttonSave.setGraphic(new ImageView(new Image("/images/save.png")));
		buttonSave.setOnAction(e -> actionButtonSaveHandler());
		buttonSave.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonDelete = new Button();
		buttonDelete.setGraphic(new ImageView(new Image("/images/delete.png")));
		buttonDelete.setOnAction(e -> actionButtonDeleteHandler());
		buttonDelete.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

		buttonRefresh = new Button();
		buttonRefresh.setGraphic(new ImageView(new Image("/images/refresh.png")));
		buttonRefresh.setTooltip(new Tooltip("화면 다시 불러오기"));
		buttonRefresh.setOnAction(e -> actionButtonRefreshHandler());
		buttonRefresh.setFont(Font.font("Verdana", FontWeight.BOLD, 14));

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
		datePicker.setValue(LocalDate.now());
		datePicker.setOnAction(e -> {
			handlerDatePicker();
		});

		Region region = new Region();
		HBox.setHgrow(region, Priority.ALWAYS);
		hBox.setStyle("-fx-alignment: CENTER;");

		hBox.getChildren().addAll(datePicker, textFieldFilterURL, region, buttonSave, buttonDelete, buttonRefresh);

		hBox.setPadding(new Insets(7, 7, 7, 7));
		hBox.setSpacing(10);

	}

	private void handlerDatePicker() {
		String date = datePicker.getValue().toString();
		List<Question> list = tableView.getItems().stream().filter(item -> item.getDate().equals(date))
				.collect(Collectors.toList());

		tableView.setItems(FXCollections.observableArrayList(list));

	}

	void handlerButtonTopHandler() {
		Platform.runLater(() -> tableView.scrollTo(0));
	}

	void handlerButtonEndHandler() {
		Platform.runLater(() -> tableView.scrollTo(tableView.getItems().size() - 1));
	}

	@SuppressWarnings("unused")
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

	private Object actionDoubleClickCleanHandler() {
		textFieldFilterURL.setOnMouseClicked(e -> {
			if (e.getClickCount() == 2) {
				textFieldFilterURL.clear();
			}
		});
		return null;
	}

	private Object actionButtonDeleteHandler() {
		List<Question> deleList = tableView.getItems().stream().filter(item -> !item.getSelected())
				.collect(Collectors.toList());
		if (excelManual.writeQuestionList(deleList)) {
			buttonRefresh.fire();
		}
		return null;
	}

	private Object actionButtonRefreshHandler() {
		updateTable();
		return null;
	}

	private Object actionButtonSaveHandler() {
		if (tableView.getItems().size() > 0) {
			if (excelManual.writeQuestionList(tableView.getItems())) {
				buttonRefresh.fire();
			}
		}
		return null;
	}
	// 개선

	
	private void updateTable() {

		questionList = excelManual.readQuestionList();
		Collections.sort(questionList);
		updateFiQuestionTextField(FXCollections.observableList(questionList));
		//tableView.getItems().clear();
		//tableView.getItems().addAll(questionList);
	
	}

	Comparator<Spam> compareByTimeOfInspection = (Spam o1, Spam o2) -> o2.getTimeOfInspection()
			.compareTo(o1.getTimeOfInspection());

	// Utility function to find distinct by class field

	public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor)

	{

		Map<Object, Boolean> map = new ConcurrentHashMap<>();

		return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;

	}

	public static <T> Collector<T, ?, Stream<T>> toShuffledStream() {
		return Collectors.collectingAndThen(Collectors.toList(), collected -> {
			Collections.shuffle(collected);
			return collected.stream();
		});
	}

	public void open() {

		initialPane();
		initialComponent();
		initialTableView();
		updateTable();

		// this.spamCheckedList = jsonUtil.convertJsonToSpamList(SPAM_FILE);
		Stage stage = new Stage();
		stage.getIcons().add(new Image("/images/list.png"));
		stage.setTitle("Review result");
		Scene scene = new Scene(borderPane);
		scene.getStylesheets().add("styles/styles.css");
		stage.setScene(scene);
		stage.setOnCloseRequest(event -> {
			stage.close();
		});
		stage.show();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void initialTableView() {

		// centerTableView.prefWidthProperty().bind(anchorPaneRightTableview.widthProperty());
		// centerTableView.prefHeightProperty().bind(anchorPaneRightTableview.Property());

		tableView.getStyleClass().add("spam");

		tableView.setEditable(true);

		TableColumn<Question, Boolean> columnSelected = createCheckBoxHeaderColumn(Question::selectedProperty);
		TableColumn<Question, String> columnUri = createColumn("url", Question::uriProperty);
		TableColumn<Question, String> columnDate = createColumn("DATE", Question::dateProperty);
		TableColumn<Question, String> columnMemo = createColumn("comment", Question::memoProperty);

		columnSelected.setPrefWidth(40);
		columnUri.setPrefWidth(400);
		columnDate.setPrefWidth(100);
		columnMemo.setPrefWidth(150);

		columnMemo.setCellFactory(TextFieldTableCell.forTableColumn());
		columnMemo.setOnEditCommit(
				t -> t.getTableView().getItems().get(t.getTablePosition().getRow()).setMemo(t.getNewValue()));

		TableColumn<Question, Question> numberCol = new TableColumn<Question, Question>("#");
		numberCol.setStyle("-fx-alignment: CENTER;");
		numberCol.setMinWidth(40);
		numberCol.setPrefWidth(40);
		numberCol.setCellValueFactory(new Callback<CellDataFeatures<Question, Question>, ObservableValue<Question>>() {
			@Override
			public ObservableValue<Question> call(CellDataFeatures<Question, Question> p) {
				return new ReadOnlyObjectWrapper(p.getValue());
			}
		});

		numberCol.setCellFactory(new Callback<TableColumn<Question, Question>, TableCell<Question, Question>>() {
			@Override
			public TableCell<Question, Question> call(TableColumn<Question, Question> param) {

				return new TableCell<Question, Question>() {
					@Override
					protected void updateItem(Question item, boolean empty) {
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

		TableColumn<Question, Void> columnButton = createButtonColumn();
		columnButton.setMinWidth(160); // 130
		columnButton.setPrefWidth(160);

		tableView.getColumns().addAll(numberCol, columnSelected, columnButton, columnUri, columnDate, columnMemo);

	}

	private TableColumn<Question, Void> createButtonColumn() {
		TableColumn<Question, Void> column = new TableColumn<Question, Void>("ACTION");
		column.setStyle("-fx-alignment: CENTER;");

		Callback<TableColumn<Question, Void>, TableCell<Question, Void>> cellFactory = new Callback<TableColumn<Question, Void>, TableCell<Question, Void>>() {
			@Override
			public TableCell<Question, Void> call(final TableColumn<Question, Void> param) {

				final TableCell<Question, Void> cell = new TableCell<Question, Void>() {

					private final Button google = new Button();
					private final Button explorer = new Button();
					private final Button result = new Button();
					private final Button text = new Button();
					private final Button delete = new Button();
					private final Button inspect = new Button();
					HBox hBox = new HBox(google, explorer, result, text ,inspect);

					{
						google.setGraphic(new ImageView(new Image("/images/google.png")));
						explorer.setGraphic(new ImageView(new Image("/images/explorer.png")));
						result.setGraphic(new ImageView(new Image("/images/magnify.png")));
						text.setGraphic(new ImageView(new Image("/images/bluelist.png")));
						delete.setGraphic(new ImageView(new Image("/images/delete.png")));
						inspect.setGraphic(new ImageView(new Image("/images/all.png")));
						
						google.setOnAction((ActionEvent event) -> {
							Question spam = getTableView().getItems().get(getIndex());
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
							Question spam = getTableView().getItems().get(getIndex());
							spam.setSelected(true);
							verifySite.startBrowser(spam.getUri(), verifySite.getExplore());
							verifySite.setClipbord(spam.getUri());
						});
						result.setOnAction((ActionEvent event) -> {
							Question spam = getTableView().getItems().get(getIndex());
							spam.setSelected(true);
							verifySite.eventSearchResult(spam.getUri());
						});
						delete.setOnAction((ActionEvent event) -> {
							Question spam = getTableView().getItems().get(getIndex());
							eventDelete(spam);
						});
						text.setOnAction((ActionEvent event) -> {
							Question spam = getTableView().getItems().get(getIndex());
							getTableView().getItems().get(getIndex()).setSelected(true);
							spam.setSelected(true);
							verifySite.hiddenText(spam.getUri());
						});
						inspect.setOnAction((ActionEvent event) -> {
							Question spam = getTableView().getItems().get(getIndex());
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

					private void eventDelete(Question spam) {
//						try {
//							//spamList = restQuestionRepository.deleteByQuestion(spam);
//							//URI uri = new URI(spam.getUri());
//							//actionButtonFilterURLHander(uri.getHost());
//						} catch (URISyntaxException e) {
//							e.printStackTrace();
//						}
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

	private <S, T> TableColumn<S, T> createColumn(String head, Function<S, Property<T>> prop) {
		TableColumn<S, T> tableColumn = new TableColumn<>(head.toUpperCase());
		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));

		makeHeaderWrappable(tableColumn);

		tableColumn.setPrefWidth(80);
		// tableColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		tableColumn.setSortable(true);
		if (head.equals("url") || head.equals("comment")) {
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

	private TableColumn<Question, Boolean> createCheckBoxHeaderColumn(Function<Question, Property<Boolean>> prop) {
		TableColumn<Question, Boolean> tableColumn = new TableColumn<>();
		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));

		tableColumn.setCellFactory(new Callback<TableColumn<Question, Boolean>, TableCell<Question, Boolean>>() {
			@Override

			public TableCell<Question, Boolean> call(TableColumn<Question, Boolean> param) {
				return new CheckBoxTableCell<Question, Boolean>() {
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

//	@SuppressWarnings("unused")
//	private <S, T> TableColumn<S, T> createCheckBoxColumn(String head, String abbr, Function<S, Property<T>> prop) {
//
//		final TableColumn<S, T> tableColumn = new TableColumn<>();
//		tableColumn.setCellValueFactory(cellData -> prop.apply(cellData.getValue()));
//
//		makeHeaderWrappable(tableColumn);
//		Label label = new Label(head);
//		label.setTooltip(new Tooltip(abbr));
//		tableColumn.setGraphic(label);
//		tableColumn.setCellFactory(column -> new CheckBoxTableCell<S, T>());
//		tableColumn.setPrefWidth(50);
//
//		return tableColumn;
//	}

	public DatePicker getDatePicker() {
		return datePicker;
	}

	public void setDatePicker(DatePicker datePicker) {
		this.datePicker = datePicker;
	}

	private void updateFiQuestionTextField(ObservableList<Question> obserableList) {
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<Question> filteredData = new FilteredList<>(obserableList, p -> true);

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
		SortedList<Question> sortedData = new SortedList<>(filteredData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(tableView.comparatorProperty());

		// 5. Add sorted (and filtered) data to the table.
		tableView.setItems(sortedData);

	}
}