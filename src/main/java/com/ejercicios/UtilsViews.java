package com.ejercicios;
import java.util.ArrayList;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class UtilsViews {

    public static StackPane parentContainer = new StackPane();
    public static ArrayList<Object> controllers = new ArrayList<>();

    // Añadir una vista a la lista
    public static void addView(Class<?> cls, String name, String path) throws Exception {
        
        boolean defaultView = false;
        FXMLLoader loader = new FXMLLoader(cls.getResource(path));
        Pane view = loader.load();
        ObservableList<Node> children = parentContainer.getChildren();

        // La primera vista es la vista por defecto
        if (children.isEmpty()) {
            defaultView = true;
        }

        view.setId(name);
        view.setVisible(defaultView);
        view.setManaged(defaultView);

        children.add(view);
        controllers.add(loader.getController());
    }

    // Obtener controlador por id de vista (viewId)
    public static Object getController(String viewId) {
        int index = 0;
        for (Node n : parentContainer.getChildren()) {
            if (n.getId().equals(viewId)) {
                return controllers.get(index);
            }
            index++;
        }
        return null;
    }

    // Obtener nombre de la vista activa
    public static String getActiveView() {
        for (Node n : parentContainer.getChildren()) {
            if (n.isVisible()) {
                return n.getId();
            }
        }
        return null; // No hi ha cap vista activa
    }

    // Establecer vista visible por su id (viewId)
    public static void setView(String viewId) {

        ArrayList<Node> list = new ArrayList<>();
        list.addAll(parentContainer.getChildrenUnmodifiable());

        // Mostrar la vista siguiente, ocultar las demás
        for (Node n : list) {
            if (n.getId().equals(viewId)) {
                n.setVisible(true);
                n.setManaged(true);
            } else {
                n.setVisible(false);
                n.setManaged(false);
            }
        }

        // Quitar foco de los botones
        parentContainer.requestFocus();
    }

    // Establecer vista visible por su id (viewId) con animación
    public static void setViewAnimating(String viewId) {

        ArrayList<Node> list = new ArrayList<>();
        list.addAll(parentContainer.getChildrenUnmodifiable());

        // Obtener vista actual
        Node curView = null;
        for (Node n : list) {
            if (n.isVisible()) {
                curView = n;
            }
        }

        if (curView.getId().equals(viewId)) {
            return; // No hacer nada si la vista actual es la misma que la siguiente
        }

        // Obtener nxtView
        Node nxtView = null;
        for (Node n : list) {
            if (n.getId().equals(viewId)) {
                nxtView = n;
            }
        }

        // Hacer nxtView visible
        nxtView.setVisible(true);
        nxtView.setManaged(true);

        // Por defecto, animar a la izquierda
        double width = parentContainer.getScene().getWidth();
        double xLeftStart = 0;
        double xLeftEnd = 0;
        double xRightStart = 0;
        double xRightEnd = 0;
        Node animatedViewLeft = null;
        Node animatedViewRight = null;

        if (list.indexOf(curView) < list.indexOf(nxtView)) {

            // Si curView es menor que nxtView, animar a la izquierda
            xLeftStart = 0;
            xLeftEnd = -width;
            xRightStart = width;
            xRightEnd = 0;
            animatedViewLeft = curView;
            animatedViewRight = nxtView;

            curView.translateXProperty().set(xLeftStart);
            nxtView.translateXProperty().set(xRightStart);

        } else { 

            // Si curView es mayor que nxtView, animar a la derecha
            xLeftStart = -width;
            xLeftEnd = 0;
            xRightStart = 0;
            xRightEnd = width;
            animatedViewLeft = nxtView;
            animatedViewRight = curView;

            curView.translateXProperty().set(xRightStart);
            nxtView.translateXProperty().set(xLeftStart);
        }

        // Animar vista izquierda 
        final double seconds = 0.4;
        KeyValue kvLeft = new KeyValue(animatedViewLeft.translateXProperty(), xLeftEnd, Interpolator.EASE_BOTH);
        KeyFrame kfLeft = new KeyFrame(Duration.seconds(seconds), kvLeft);
        Timeline timelineLeft = new Timeline();
        timelineLeft.getKeyFrames().add(kfLeft);
        timelineLeft.play();

        // Animar vista derecha 
        KeyValue kvRight = new KeyValue(animatedViewRight.translateXProperty(), xRightEnd, Interpolator.EASE_BOTH);
        KeyFrame kfRight = new KeyFrame(Duration.seconds(seconds), kvRight);
        Timeline timelineRight = new Timeline();
        timelineRight.getKeyFrames().add(kfRight);
        timelineRight.setOnFinished(t -> {
            // Ocultar otras vistas y resetear todas las traducciones
            for (Node n : list) {
                if (!n.getId().equals(viewId)) {
                    n.setVisible(false);
                    n.setManaged(false);
                }
                n.translateXProperty().set(0);
            }
        });
        timelineRight.play();

        // Quitar foco de los botones
        parentContainer.requestFocus();
    }
}
