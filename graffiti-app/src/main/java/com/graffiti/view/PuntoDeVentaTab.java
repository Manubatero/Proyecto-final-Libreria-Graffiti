package com.graffiti.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class PuntoDeVentaTab extends Tab {

    public PuntoDeVentaTab() {
        super("  Punto de cobro  ");
        initUI();
    }

    private void initUI() {
        Label title = new Label("PUNTO DE COBRO\nEscanee códigos de barras con la pistola lectora USB.");
        title.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:#172554;");

        TextField lector = new TextField();
        lector.setPromptText("Escanee el código de barras aquí...");
        lector.setStyle("-fx-font-size:16px;-fx-padding:10;");

        TableView<String> tablaVenta = new TableView<>();
        tablaVenta.setPlaceholder(new Label("No hay productos en el carrito actual."));

        Label total = new Label("TOTAL: $ 0.00");
        total.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:#172554;");

        Button btnCobrar = new Button("Cobrar e Imprimir");
        btnCobrar.setStyle("-fx-background-color:#0f766e;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:14px;-fx-padding:10 20;");

        HBox footer = new HBox(15, total, btnCobrar);
        footer.setAlignment(Pos.CENTER_RIGHT);

        VBox box = new VBox(12, title, lector, tablaVenta, footer);
        box.setPadding(new Insets(20));
        VBox.setVgrow(tablaVenta, Priority.ALWAYS);

        this.setContent(box);
    }
}