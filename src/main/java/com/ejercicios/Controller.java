package com.ejercicios;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.VBox;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private VBox scrollContent;

    private JSONArray charactersData;
    private JSONArray consolesData;
    private JSONArray gamesData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categoryComboBox.getItems().addAll("Personajes", "Consolas", "Juegos");
        categoryComboBox.getSelectionModel().select("Personajes");

        loadAllData();
        loadCategory("Personajes");

        // Listener para cuando se cambia de categoría
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadCategory(newVal);
        });
    }

    private void loadAllData() {
        charactersData = loadJsonArray("/data/characters.json");
        consolesData = loadJsonArray("/data/consoles.json");
        gamesData = loadJsonArray("/data/games.json");
    }

    private JSONArray loadJsonArray(String filePath) {
        try {
            URL jsonFileURL = getClass().getResource(filePath);
            Path path = Paths.get(jsonFileURL.toURI());
            String content = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);

            return new JSONArray(content);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private void loadPersonajes() {
        loadCategory("Personajes");
    }

    @FXML
    private void loadConsolas() {
        loadCategory("Consolas");
    }

    @FXML
    private void loadJuegos() {
        loadCategory("Juegos");
    }

    private void loadCategory(String category) {
        if (scrollContent == null) return;

        scrollContent.getChildren().clear();

        JSONArray data;
        if (category.equals("Personajes")) {
            data = charactersData;
        } else if (category.equals("Consolas")) {
            data = consolesData;
        } else if (category.equals("Juegos")) {
            data = gamesData;
        } else {
            data = new JSONArray();
        }

        URL itemResource = getClass().getResource("/assets/list_item.fxml");

        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject item = data.getJSONObject(i);

                String name = item.getString("name");
                String imageName = item.getString("image");
                String imageUrl = "/data/images/" + imageName;

                FXMLLoader loader = new FXMLLoader(itemResource);
                Parent itemTemplate = loader.load();
                ControllerListItem itemController = loader.getController();

                itemController.setName(name);
                itemController.setImage(imageUrl);

                scrollContent.getChildren().add(itemTemplate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}