package com.ever.webSpam;

import java.time.LocalDate;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import com.ever.webSpam.spam.SpamController;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import net.rgielen.fxweaver.core.FxWeaver;

public class JavaFxApplication extends Application {

    private ConfigurableApplicationContext applicationContext;

    @Override
    public void init() {
        String[] args = getParameters().getRaw().toArray(new String[0]);

        this.applicationContext = new SpringApplicationBuilder()
                .sources(WebSpamApplication.class)
                .run(args);
    }

    @Override
    public void start(Stage stage) {
        FxWeaver fxWeaver = applicationContext.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(SpamController.class);
        Scene scene = new Scene(root);
    	stage.getIcons().add(new Image("/images/Hyperlink.png"));
    	stage.setTitle("WebSpam Tools");
    	//Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    	//double x = bounds.getMinX() + (bounds.getWidth() - scene.getWidth()) * 0.7;
    	//double y = bounds.getMinY() + (bounds.getHeight() - scene.getHeight()) * 0.1;
    	//stage.setX(x);
    	//stage.setY(y);
    	stage.setAlwaysOnTop(true);
        stage.setScene(scene);
        
        if(diffDate()) {
        	stage.show();
        } else {
        	 System.exit(0);
        }
        
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    @Override
    public void stop() {
        this.applicationContext.close();
        Platform.exit();
    }

    private Boolean diffDate() {
    	LocalDate currentDate = LocalDate.now();
    	LocalDate targetDate = LocalDate.of(2022, 3, 01);
    	
    	if(currentDate.isBefore(targetDate)) {
    		return true;
    	} 
    	
    	return false;	
    }
}
