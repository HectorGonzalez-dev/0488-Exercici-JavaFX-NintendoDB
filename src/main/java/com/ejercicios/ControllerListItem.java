package com.ejercicios;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import java.util.Objects;

public class ControllerListItem {

    @FXML
    private ImageView itemImage;

    @FXML
    private Label itemNameLabel;

    @FXML
    private HBox itemRoot;

    private Runnable onSelectCallback;
    private boolean selected = false;

    private static final String SELECTED_STYLE = "-fx-padding: 5 10; -fx-border-color: transparent transparent #E0E0E0 transparent; -fx-border-width: 0 0 1 0; -fx-cursor: hand; -fx-background-color: #A8D0E6;";
    private static final String DEFAULT_STYLE = "-fx-padding: 5 10; -fx-border-color: transparent transparent #E0E0E0 transparent; -fx-border-width: 0 0 1 0; -fx-cursor: hand; -fx-background-color: transparent;";

    @FXML
    public void initialize() {
        itemRoot.setStyle(DEFAULT_STYLE);
    }

    @FXML
    private void onItemClicked() {
        if (onSelectCallback != null) {
            onSelectCallback.run();
        }
    }

    public void setOnSelectCallback(Runnable callback) {
        this.onSelectCallback = callback;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        itemRoot.setStyle(selected ? SELECTED_STYLE : DEFAULT_STYLE);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setImage(String imagePath) {
        try {
            Image image = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
            this.itemImage.setImage(image);
        } catch (NullPointerException e) {
            System.err.println("Error loading image asset: " + imagePath);
            e.printStackTrace();
        }
    }

    public void setName(String name) {
        this.itemNameLabel.setText(name);
    }
}