package com.ejercicios;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    final int WINDOW_WIDTH = 800;
    final int WINDOW_HEIGHT = 400;
    final int MAX_WINDOW_HEIGHT = 700;

    static String age;
    static String name;

    @Override
    public void start(Stage stage) throws Exception {

        UtilsViews.parentContainer.setStyle("-fx-font: 14 arial;");
        UtilsViews.addView(getClass(), "layout_responsive", "/assets/layout_responsive.fxml");

        Scene scene = new Scene(UtilsViews.parentContainer);

        stage.setScene(scene);
        stage.setTitle("NintendoDB");
        stage.setMinWidth(400);
        stage.setMinHeight(500);
        stage.setMaxHeight(MAX_WINDOW_HEIGHT);
        stage.show();

        // Afegeix una icona només si no és un Mac
        if (!System.getProperty("os.name").contains("Mac")) {
            var iconUrl = getClass().getResource("/icons/icon.png");
            if (iconUrl != null) {
                stage.getIcons().add(new Image(iconUrl.toExternalForm()));
            } else {
                System.err.println("Icon not found: /icons/icon.png");
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
