package com.ejercicios;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.json.JSONObject;
import java.util.Objects;

public class ControllerGameDetail {

    @FXML
    private StackPane imageContainer;

    @FXML
    private ImageView gameImage;

    @FXML
    private Label gameNameLabel;

    @FXML
    private Label gameYearLabel;

    @FXML
    private Label gameTypeLabel;

    @FXML
    private Label gamePlotLabel;

    public void setData(JSONObject game) {
        String name = game.getString("name");
        String imageName = game.getString("image");
        int year = game.getInt("year");
        String type = game.getString("type");
        String plot = game.getString("plot");

        gameNameLabel.setText(name);
        gameYearLabel.setText("Año: " + year);
        gameTypeLabel.setText(type);
        gamePlotLabel.setText(plot);

        // Cargar imagen
        String imagePath = "/data/images/" + imageName;
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            gameImage.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + imagePath);
        }
    }
}