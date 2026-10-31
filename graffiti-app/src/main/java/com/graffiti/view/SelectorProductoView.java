package com.graffiti.view;

import com.graffiti.dao.ProductoDAO;
import com.graffiti.model.Producto;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.util.Duration;

import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

/** Búsqueda de productos con sugerencias en vivo, sin modificar datos persistentes. */
public final class SelectorProductoView extends VBox {
    private final TextField busqueda = new TextField();
    private final VBox resultados = new VBox(3);
    private final Popup popup = new Popup();
    private final ProductoDAO dao;
    private final Consumer<Producto> alSeleccionar;
    private List<Producto> catalogo = List.of();

    public SelectorProductoView(ProductoDAO dao, Consumer<Producto> alSeleccionar) {
        super(7);
        this.dao = dao;
        this.alSeleccionar = alSeleccionar;
        busqueda.setPromptText("Buscar producto por nombre o código de barras");
        busqueda.setPrefHeight(40);
        busqueda.textProperty().addListener((o, anterior, actual) -> buscar(actual));
        busqueda.focusedProperty().addListener((o, anterior, enfocada) -> {
            if (enfocada) {
                catalogo = dao.listarTodos();
                buscar(busqueda.getText());
            }
        });
        busqueda.setOnAction(e -> {
            if (!resultados.getChildren().isEmpty()) ((Button) resultados.getChildren().get(0)).fire();
        });
        resultados.setPadding(new Insets(7));
        resultados.setStyle("-fx-background-color:#172131;-fx-border-color:#3b4b60;-fx-border-radius:8;-fx-background-radius:8;");
        popup.getContent().add(resultados);
        popup.setAutoHide(true);
        getChildren().addAll(new Label("Agregar productos"), busqueda);
    }

    private void buscar(String texto) {
        resultados.getChildren().clear();
        String q = texto == null ? "" : texto.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) { popup.hide(); return; }
        if (catalogo.isEmpty()) catalogo = dao.listarTodos();
        catalogo.stream().filter(p -> Boolean.TRUE.equals(p.getActivo()) && p.getNombre() != null
                && (p.getNombre().toLowerCase(Locale.ROOT).contains(q)
                || (p.getCodigoBarra() != null && p.getCodigoBarra().toLowerCase(Locale.ROOT).contains(q))))
                .limit(8).forEach(p -> {
                    Button opcion = new Button(p.getNombre() + "   ·   $ " + p.getPrecioVenta() + "   ·   Stock " + p.getStockActual());
                    opcion.setMaxWidth(Double.MAX_VALUE);
                    opcion.setAlignment(Pos.CENTER_LEFT);
                    if (p.getStockActual() != null && p.getStockMinimo() != null && p.getStockActual() <= p.getStockMinimo()) {
                        opcion.setStyle("-fx-text-fill:#fecaca;-fx-background-color:#7f1d1d;-fx-border-color:#ef4444;-fx-border-radius:7;-fx-background-radius:7;");
                        opcion.setText(opcion.getText() + "   ·   ¡STOCK POR DEBAJO DEL MÍNIMO!");
                    }
                    opcion.setOnAction(e -> { popup.hide(); alSeleccionar.accept(p); busqueda.clear(); busqueda.requestFocus(); });
                    resultados.getChildren().add(opcion);
                });
        if (resultados.getChildren().isEmpty() || busqueda.getScene() == null) { popup.hide(); return; }
        var bounds = busqueda.localToScreen(busqueda.getBoundsInLocal());
        if (bounds == null) return;
        popup.show(busqueda, bounds.getMinX(), bounds.getMaxY());
        resultados.setOpacity(0);
        FadeTransition fade = new FadeTransition(Duration.millis(140), resultados);
        fade.setFromValue(0); fade.setToValue(1); fade.play();
    }

    public void limpiar() { busqueda.clear(); }

    /** Sincroniza nombres y existencias después de registrar una venta. */
    public void refrescarCatalogo() {
        catalogo = dao.listarTodos();
        buscar(busqueda.getText());
    }
}
