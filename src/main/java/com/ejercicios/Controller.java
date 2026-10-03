package com.ejercicios;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
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

    // Vistas
    @FXML private BorderPane desktopView;
    @FXML private VBox mobileView;
    
    // Mobile screens
    @FXML private VBox mobileCategoryScreen;
    @FXML private VBox mobileItemScreen;
    @FXML private VBox mobileDetailScreen;
    
    // Mobile lists
    @FXML private VBox mobileCategoryList;
    @FXML private VBox mobileItemList;
    @FXML private VBox mobileDetailContent;
    @FXML private ScrollPane mobileDetailScrollPane;
    
    // Desktop components
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private VBox scrollContent;
    @FXML private VBox detailContent;
    
    // Top bar
    @FXML private BorderPane topBar;
    @FXML private StackPane mainContentArea;
    @FXML private StackPane backButtonContainer;
    @FXML private Button backButton;
    @FXML private ImageView backArrow;
    @FXML private Label titleLabel;
    
    // Data
    private JSONArray charactersData;
    private JSONArray consolesData;
    private JSONArray gamesData;
    
    // State
    private ControllerListItem currentlySelectedItem = null;
    private String currentCategory = "Personajes";
    private boolean isMobileMode = false;
    
    // Navigation stack for mobile
    private enum MobileScreen { CATEGORY, ITEM, DETAIL }
    private MobileScreen currentMobileScreen = MobileScreen.CATEGORY;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Inicializar ComboBox desktop
        categoryComboBox.getItems().addAll("Personajes", "Consolas", "Juegos");
        categoryComboBox.getSelectionModel().select("Personajes");
        
        // Listener para cambio de categoría en desktop
        categoryComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadCategory(newVal);
            }
        });
        
        // Listener para responsive: detectar ancho de ventana
        topBar.widthProperty().addListener((obs, oldVal, newVal) -> {
            checkResponsive(newVal.doubleValue());
        });
        
        // Botón volver
        backButton.setOnAction(e -> onBackPressed());
        
        loadAllData();
        loadCategory("Personajes");
        loadMobileCategories();
    }
    
    private void checkResponsive(double width) {
        boolean shouldBeMobile = width < 700;
        if (shouldBeMobile != isMobileMode) {
            isMobileMode = shouldBeMobile;
            switchViewMode();
        }
    }
    
    private void switchViewMode() {
        if (isMobileMode) {
            // Cambiar a vista móvil
            desktopView.setVisible(false);
            desktopView.setManaged(false);
            mobileView.setVisible(true);
            mobileView.setManaged(true);
            showMobileScreen(MobileScreen.CATEGORY);
        } else {
            // Cambiar a vista escritorio - restaurar estado
            mobileView.setVisible(false);
            mobileView.setManaged(false);
            desktopView.setVisible(true);
            desktopView.setManaged(true);
            // Ocultar botón volver en desktop
            backButtonContainer.setVisible(false);
            backButtonContainer.setManaged(false);
            // Restaurar título
            titleLabel.setText("Nintendo DB");
            // Restaurar selección en desktop
            categoryComboBox.getSelectionModel().select(currentCategory);
            loadCategory(currentCategory);
        }
    }
    
    private void showMobileScreen(MobileScreen screen) {
        // Ocultar todas
        mobileCategoryScreen.setVisible(false);
        mobileCategoryScreen.setManaged(false);
        mobileItemScreen.setVisible(false);
        mobileItemScreen.setManaged(false);
        mobileDetailScreen.setVisible(false);
        mobileDetailScreen.setManaged(false);
        backButtonContainer.setVisible(false);
        backButtonContainer.setManaged(false);
        
        // Mostrar la solicitada
        switch (screen) {
            case CATEGORY:
                mobileCategoryScreen.setVisible(true);
                mobileCategoryScreen.setManaged(true);
                titleLabel.setText("Nintendo DB");
                break;
            case ITEM:
                mobileItemScreen.setVisible(true);
                mobileItemScreen.setManaged(true);
                backButtonContainer.setVisible(true);
                backButtonContainer.setManaged(true);
                titleLabel.setText(currentCategory);
                break;
            case DETAIL:
                mobileDetailScreen.setVisible(true);
                mobileDetailScreen.setManaged(true);
                backButtonContainer.setVisible(true);
                backButtonContainer.setManaged(true);
                if (currentlySelectedItem != null) {
                    titleLabel.setText(currentlySelectedItem.getName());
                }
                break;
        }
        currentMobileScreen = screen;
    }
    
    @FXML
    private void onBackPressed() {
        switch (currentMobileScreen) {
            case DETAIL:
                showMobileScreen(MobileScreen.ITEM);
                break;
            case ITEM:
                showMobileScreen(MobileScreen.CATEGORY);
                break;
            case CATEGORY:
                // Ya estamos en la pantalla principal, no hacer nada
                break;
        }
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
    private void loadPersonajes() { loadCategory("Personajes"); }
    @FXML private void loadConsolas() { loadCategory("Consolas"); }
    @FXML private void loadJuegos() { loadCategory("Juegos"); }

    private void loadCategory(String category) {
        currentCategory = category;
        
        // Desktop
        if (scrollContent != null) {
            currentlySelectedItem = null;
            scrollContent.getChildren().clear();
            
            JSONArray data = getDataForCategory(category);
            URL itemResource = getClass().getResource("/assets/list_item.fxml");
            
            for (int i = 0; i < data.length(); i++) {
                try {
                    JSONObject item = data.getJSONObject(i);
                    addItemToList(item, itemResource, true, false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        // Mobile - actualizar lista de items si estamos en esa pantalla
        if (isMobileMode && currentMobileScreen == MobileScreen.ITEM) {
            loadMobileItems(category);
        }
    }
    
    private JSONArray getDataForCategory(String category) {
        if (category.equals("Personajes")) return charactersData;
        if (category.equals("Consolas")) return consolesData;
        if (category.equals("Juegos")) return gamesData;
        return new JSONArray();
    }
    
    private void addItemToList(JSONObject item, URL itemResource, boolean isDesktop, boolean isCategoryItem) {
        try {
            String name = item.getString("name");
            String imageName = item.getString("image");
            String imageUrl = "/data/images/" + imageName;
            
            FXMLLoader loader = new FXMLLoader(itemResource);
            Parent itemTemplate = loader.load();
            ControllerListItem itemController = loader.getController();
            
            itemController.setName(name);
            itemController.setImage(imageUrl);
            
            // Para items de categoría en móvil, ocultar el contenedor de imagen
            if (isCategoryItem) {
                itemController.hideImageContainer();
            }
            
            final ControllerListItem currentItemController = itemController;
            final JSONObject itemData = item;
            itemController.setOnSelectCallback(() -> {
                handleItemSelection(currentItemController, itemData);
            });
            
            if (isDesktop) {
                if (currentlySelectedItem == null) {
                    currentlySelectedItem = itemController;
                    itemController.setSelected(true);
                    loadDetailView(itemData);
                }
                scrollContent.getChildren().add(itemTemplate);
            } else {
                mobileItemList.getChildren().add(itemTemplate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void loadMobileCategories() {
        mobileCategoryList.getChildren().clear();
        
        String[] categories = {"Personajes", "Consolas", "Juegos"};
        URL itemResource = getClass().getResource("/assets/list_item.fxml");
        
        for (String cat : categories) {
            try {
                FXMLLoader loader = new FXMLLoader(itemResource);
                Parent itemTemplate = loader.load();
                ControllerListItem itemController = loader.getController();
                
                itemController.setName(cat);
                itemController.setImage("");
                itemController.hideImageContainer();
                
                final String categoryName = cat;
                itemController.setOnSelectCallback(() -> {
                    selectMobileCategory(categoryName);
                });
                
                mobileCategoryList.getChildren().add(itemTemplate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private void selectMobileCategory(String category) {
        currentCategory = category;
        loadMobileItems(category);
        showMobileScreen(MobileScreen.ITEM);
    }
    
    private void loadMobileItems(String category) {
        mobileItemList.getChildren().clear();
        
        JSONArray data = getDataForCategory(category);
        URL itemResource = getClass().getResource("/assets/list_item.fxml");
        
        for (int i = 0; i < data.length(); i++) {
            try {
                JSONObject item = data.getJSONObject(i);
                addItemToList(item, itemResource, false, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleItemSelection(ControllerListItem selectedItem, JSONObject itemData) {
        // Deseleccionar item anterior
        if (currentlySelectedItem != null && currentlySelectedItem != selectedItem) {
            currentlySelectedItem.setSelected(false);
        }
        
        // Seleccionar nuevo
        selectedItem.setSelected(true);
        currentlySelectedItem = selectedItem;
        
        // Cargar vista de detalle
        loadDetailView(itemData);
        
        // En móvil, navegar a pantalla de detalle
        if (isMobileMode) {
            showMobileScreen(MobileScreen.DETAIL);
        }
    }

    private void loadDetailView(JSONObject itemData) {
        if (detailContent == null && mobileDetailContent == null) {
            return;
        }

        try {
            // A JavaFX Node can only have one parent, so inflate
            // an independent instance for each container.
            if (detailContent != null) {
                Parent desktopDetail = createDetailView(itemData);
                if (desktopDetail != null) {
                    detailContent.getChildren().setAll(desktopDetail);
                }
            }

            if (mobileDetailContent != null) {
                Parent mobileDetail = createDetailView(itemData);
                if (mobileDetail != null) {
                    mobileDetailContent.getChildren().setAll(mobileDetail);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Inflate a FRESH detail view for the current category and bind itemData to it. */
    private Parent createDetailView(JSONObject itemData) throws Exception {
        String fxmlPath;
        switch (currentCategory) {
            case "Personajes":
                fxmlPath = "/assets/character_detail.fxml";
                FXMLLoader charLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent charView = charLoader.load();
                ((ControllerCharacterDetail) charLoader.getController()).setData(itemData);
                return charView;
            case "Consolas":
                fxmlPath = "/assets/console_detail.fxml";
                FXMLLoader consoleLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent consoleView = consoleLoader.load();
                ((ControllerConsoleDetail) consoleLoader.getController()).setData(itemData);
                return consoleView;
            case "Juegos":
                fxmlPath = "/assets/game_detail.fxml";
                FXMLLoader gameLoader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent gameView = gameLoader.load();
                ((ControllerGameDetail) gameLoader.getController()).setData(itemData);
                return gameView;
            default:
                return null;
        }
    }
}