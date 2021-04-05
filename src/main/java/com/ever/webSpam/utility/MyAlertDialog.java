package com.ever.webSpam.utility;

import java.util.Locale;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

@Component
public class MyAlertDialog {

	@Autowired
	MessageSource messageSource;

	public void dialogInformationString(String error) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(error);

		alert.showAndWait();
	}

	public void dialogInformation(String error) {
		String message = messageSource.getMessage(error, null, Locale.KOREA);
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}

	public void dialogInformation(String error, Stage stage) {
		String message = messageSource.getMessage(error, null, Locale.KOREA);
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Information");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.initOwner(stage);
		alert.initModality(Modality.WINDOW_MODAL);

		alert.showAndWait();
	}

	// Optional<ButtonType> result = MyAlertDialog.dialogConfirmation("Really !!
	// Want to Delete ? \r\n");
	// if (result.get().equals(ButtonType.OK))

	public Optional<ButtonType> dialogConfirmation(String error) {
		String message = messageSource.getMessage(error, null, Locale.KOREA);
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation ...");
		alert.setHeaderText(null);
		alert.setContentText(message);

		Optional<ButtonType> result = alert.showAndWait();

		return result;
	}

	public Optional<ButtonType> alertDelete() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation");
		alert.setHeaderText(null);
		alert.getDialogPane().setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
		alert.setContentText(
				"Do you really want to delete seleted records?\n\n" + "This process cannot be undone.\n\n");

		return alert.showAndWait();
	}

	public void writeInform(Label label, String string) {
		Platform.runLater(() -> {
			label.setText(string);
		});
		// 1초간 중지시킨다.(단위 : 밀리세컨드)
		Runnable runnable = () -> {
			try {
				Thread.sleep(1000 * 15);
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
}
