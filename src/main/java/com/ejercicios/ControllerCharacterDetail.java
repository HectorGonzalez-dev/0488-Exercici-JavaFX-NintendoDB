package com.ejercicios;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.json.JSONObject;
import java.util.Objects;

public class ControllerCharacterDetail {

    @FXML
    private StackPane imageContainer;

    @FXML
    private ImageView characterImage;

    @FXML
    private Label characterNameLabel;

    @FXML
    private Label characterGameLabel;

    @FXML
    private Region colorBadge;

    public void setData(JSONObject character) {
        String name = character.getString("name");
        String imageName = character.getString("image");
        String color = character.getString("color");
        String game = character.getString("game");

        characterNameLabel.setText(name);
        characterGameLabel.setText("Juego principal: " + game);

        // Cargar imagen
        String imagePath = "/data/images/" + imageName;
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            characterImage.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + imagePath);
        }

        // Configurar badge de color
        setColorBadge(color);
    }

    private void setColorBadge(String colorName) {
        String colorHex = getColorHex(colorName);
        colorBadge.setStyle("-fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #DDD; -fx-border-width: 1; -fx-background-color: " + colorHex + ";");
    }

    private String getColorHex(String colorName) {
        return switch (colorName.toLowerCase()) {
            case "red" -> "#E74C3C";
            case "green" -> "#27AE60";
            case "blue" -> "#3498DB";
            case "yellow" -> "#F39C12";
            case "orange" -> "#E67E22";
            case "pink" -> "#E91E63";
            case "brown" -> "#8D6E63";
            case "grey", "gray" -> "#95A5A6";
            case "black" -> "#2C3E50";
            case "white" -> "#ECF0F1";
            case "purple" -> "#9B59B6";
            default -> "#95A5A6";
        };
    }
}