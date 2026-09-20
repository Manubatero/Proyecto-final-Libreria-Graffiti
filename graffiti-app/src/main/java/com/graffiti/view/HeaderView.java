package com.graffiti.view;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class HeaderView extends HBox {
    public HeaderView() {
        super(18);

        Label marca = new Label("GRAFFITI");
        marca.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:white;");

        Label sub = new Label("GESTIÓN DE STOCK  |  PUNTO DE COBRO");
        sub.setStyle("-fx-font-size:10px;-fx-text-fill:#bfdbfe;-fx-font-weight:bold;");

        Label alerta = new Label("⚠️ Alertas de stock activo");
        alerta.setStyle("-fx-font-weight:bold;-fx-text-fill:#fef3c7;-fx-background-color:#854d0e;-fx-background-radius:14;-fx-padding:7 12;");

        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);

        this.getChildren().addAll(new VBox(marca, sub), r, alerta);
        this.setAlignment(Pos.CENTER_LEFT);
        this.setPadding(new javafx.geometry.Insets(14, 28, 14, 28));
        this.setStyle("-fx-background-color:linear-gradient(to right,#172554,#1e40af,#0f766e);");
    }
}