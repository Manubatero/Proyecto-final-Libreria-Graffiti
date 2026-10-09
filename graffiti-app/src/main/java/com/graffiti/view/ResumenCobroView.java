package com.graffiti.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/** Panel visual con los datos necesarios para revisar y cobrar una venta. */
public class ResumenCobroView extends VBox {

    public ResumenCobroView() {
        super(14);
        setPadding(new Insets(18));
        setPrefWidth(300);
        setMinWidth(260);
        setStyle(PuntoDeVentaView.estiloTarjeta());
        getChildren().addAll(crearTitulo(), crearImportes(), crearFormularioPago(), crearAcciones());
    }

    private Label crearTitulo() {
        Label titulo = new Label("Formulario de cobro");
        titulo.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#172554;");
        return titulo;
    }

    private VBox crearImportes() {
        Label subtotal = new Label("Subtotal                         $ 0,00");
        subtotal.setStyle("-fx-font-size:13px;-fx-text-fill:#475569;");
        Label total = new Label("TOTAL                              $ 0,00");
        total.setStyle("-fx-font-size:21px;-fx-font-weight:bold;-fx-text-fill:#0f766e;");
        VBox importes = new VBox(12, subtotal, new Separator(), total);
        importes.setPadding(new Insets(15));
        importes.setStyle("-fx-background-color:#f8fafc;-fx-background-radius:8;");
        return importes;
    }

    private VBox crearFormularioPago() {
        Label medioLabel = new Label("Medio de pago");
        ComboBox<String> medioPago = new ComboBox<>();
        medioPago.getItems().addAll("Efectivo", "Tarjeta", "Transferencia");
        medioPago.setValue("Efectivo");
        medioPago.setMaxWidth(Double.MAX_VALUE);

        Label recibidoLabel = new Label("Importe recibido");
        TextField recibido = new TextField();
        recibido.setPromptText("$ 0,00");

        Label vueltoLabel = new Label("Vuelto");
        Label vuelto = new Label("$ 0,00");
        vuelto.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#334155;");
        HBox filaVuelto = new HBox(vueltoLabel, new Region(), vuelto);
        HBox.setHgrow(filaVuelto.getChildren().get(1), Priority.ALWAYS);
        filaVuelto.setAlignment(Pos.CENTER_LEFT);

        VBox formulario = new VBox(8, medioLabel, medioPago, recibidoLabel, recibido, filaVuelto);
        return formulario;
    }

    private VBox crearAcciones() {
        Button cobrar = new Button("Cobrar e imprimir ticket");
        cobrar.setMaxWidth(Double.MAX_VALUE);
        cobrar.setPrefHeight(44);
        cobrar.setStyle("-fx-background-color:#0f766e;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:7;");

        Button limpiar = new Button("Cancelar venta");
        limpiar.setMaxWidth(Double.MAX_VALUE);
        limpiar.setPrefHeight(38);
        limpiar.setStyle("-fx-background-color:white;-fx-text-fill:#b91c1c;-fx-border-color:#fecaca;-fx-border-radius:7;-fx-background-radius:7;");

        Label notaTicket = new Label("Comprobante interno no fiscal");
        notaTicket.setMaxWidth(Double.MAX_VALUE);
        notaTicket.setAlignment(Pos.CENTER);
        notaTicket.setStyle("-fx-font-size:11px;-fx-text-fill:#64748b;");

        return new VBox(9, cobrar, limpiar, notaTicket);
    }
}
