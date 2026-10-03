package com.ejercicios;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import org.json.JSONObject;
import java.util.Objects;

public class ControllerConsoleDetail {

    @FXML
    private StackPane imageContainer;

    @FXML
    private ImageView consoleImage;

    @FXML
    private Label consoleNameLabel;

    @FXML
    private Label consoleDateLabel;

    @FXML
    private Label consoleProcessorLabel;

    @FXML
    private Region consoleColorBadge;

    @FXML
    private Label consoleColorLabel;

    @FXML
    private Label consoleUnitsLabel;

    public void setData(JSONObject console) {
        String name = console.getString("name");
        String imageName = console.getString("image");
        String date = console.getString("date");
        String processor = console.getString("procesador");
        String color = console.getString("color");
        long unitsSold = console.getLong("units_sold");

        consoleNameLabel.setText(name);
        consoleDateLabel.setText("Lanzamiento: " + formatDate(date));
        consoleProcessorLabel.setText(processor);
        consoleColorLabel.setText(capitalize(color));
        consoleUnitsLabel.setText(formatUnits(unitsSold));

        // Cargar imagen
        String imagePath = "/data/images/" + imageName;
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            consoleImage.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("Error loading image: " + imagePath);
        }

        // Configurar badge de color
        setColorBadge(color);
    }

    private void setColorBadge(String colorName) {
        String colorHex = getColorHex(colorName);
        consoleColorBadge.setStyle("-fx-background-radius: 12; -fx-border-radius: 12; -fx-border-color: #DDD; -fx-border-width: 1; -fx-background-color: " + colorHex + ";");
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

    private String formatDate(String date) {
        // formato fecha: "2017-3-3"
        return date;
    }

    private String formatUnits(long units) {
        if (units >= 1000000) {
            return String.format("%.1fM", units / 1000000.0);
        } else if (units >= 1000) {
            return String.format("%.1fK", units / 1000.0);
        }
        return String.valueOf(units);
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}