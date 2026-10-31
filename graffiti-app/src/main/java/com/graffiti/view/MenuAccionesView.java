package com.graffiti.view;

import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

/** Menú superior extensible para acciones globales. */
public final class MenuAccionesView extends HBox {
    private final Button abrir = new Button("⋮");
    private final VBox opciones = new VBox(4);
    private final Popup popup = new Popup();

    public MenuAccionesView() {
        setAlignment(Pos.CENTER_RIGHT);
        abrir.setStyle("-fx-font-size:22px;-fx-padding:3 13;-fx-text-fill:#e2e8f0;-fx-background-color:#263548;-fx-background-radius:8;");
        opciones.setPadding(new Insets(7));
        opciones.setStyle("-fx-background-color:#172131;-fx-border-color:#3b4b60;-fx-border-radius:8;-fx-background-radius:8;");
        opciones.setMinWidth(230);
        popup.getContent().add(opciones); popup.setAutoHide(true);
        abrir.setOnAction(e -> {
            if (popup.isShowing()) { popup.hide(); return; }
            var bounds = abrir.localToScreen(abrir.getBoundsInLocal());
            if (bounds == null) return;
            popup.show(abrir, bounds.getMaxX() - opciones.getMinWidth(), bounds.getMaxY() + 5);
            opciones.setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(150), opciones);
            fade.setFromValue(0); fade.setToValue(1); fade.play();
        });
        getChildren().add(abrir);
    }

    public void agregarOpcion(String texto, Runnable accion) {
        Button opcion = new Button(texto);
        opcion.setMaxWidth(Double.MAX_VALUE); opcion.setAlignment(Pos.CENTER_LEFT);
        opcion.setOnAction(e -> { popup.hide(); accion.run(); });
        opciones.getChildren().add(opcion);
    }
}
