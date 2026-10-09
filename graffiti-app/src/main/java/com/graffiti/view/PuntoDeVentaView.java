package com.graffiti.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

/** Pantalla visual del punto de venta; las operaciones de cobro se integrarán luego con los DAO. */
public class PuntoDeVentaView extends Tab {

    public PuntoDeVentaView() {
        super("  Punto de venta  ");
        setClosable(false);

        BorderPane contenido = new BorderPane();
        contenido.setPadding(new Insets(22));
        contenido.setStyle("-fx-background-color:#f1f5f9;");
        contenido.setTop(crearEncabezado());
        contenido.setCenter(crearAreaVenta());
        BorderPane.setMargin(contenido.getCenter(), new Insets(18, 0, 0, 0));
        setContent(contenido);
    }

    private VBox crearEncabezado() {
        Label titulo = new Label("Punto de venta");
        titulo.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:#172554;");
        Label ayuda = new Label("Escanee el código de barras o busque un producto para iniciar la venta.");
        ayuda.setStyle("-fx-font-size:13px;-fx-text-fill:#64748b;");
        return new VBox(5, titulo, ayuda);
    }

    private SplitPane crearAreaVenta() {
        SplitPane area = new SplitPane(crearPanelCarrito(), new ResumenCobroView());
        area.setDividerPositions(0.70);
        area.setStyle("-fx-background-color:transparent;");
        return area;
    }

    private VBox crearPanelCarrito() {
        TextField busqueda = new TextField();
        busqueda.setPromptText("Código de barras o nombre del producto");
        busqueda.setPrefHeight(40);
        busqueda.setStyle("-fx-font-size:14px;-fx-background-radius:7;");

        Button buscar = new Button("Buscar producto");
        buscar.setPrefHeight(40);
        buscar.setStyle("-fx-background-color:#1d4ed8;-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:7;-fx-padding:0 16;");
        HBox filaBusqueda = new HBox(10, busqueda, buscar);
        filaBusqueda.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(busqueda, Priority.ALWAYS);

        Label tituloCarrito = new Label("Detalle de la venta");
        tituloCarrito.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#172554;");
        TableView<FilaCarrito> tabla = crearTablaCarrito();
        VBox.setVgrow(tabla, Priority.ALWAYS);

        Label ayudaEscaner = new Label("El lector de código de barras puede escribir directamente en el campo de búsqueda.");
        ayudaEscaner.setWrapText(true);
        ayudaEscaner.setStyle("-fx-font-size:11px;-fx-text-fill:#64748b;");

        VBox panel = new VBox(14, filaBusqueda, tituloCarrito, tabla, ayudaEscaner);
        panel.setPadding(new Insets(18));
        panel.setStyle(estiloTarjeta());
        return panel;
    }

    private TableView<FilaCarrito> crearTablaCarrito() {
        TableView<FilaCarrito> tabla = new TableView<>();
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPlaceholder(new Label("El detalle de la venta aparecerá aquí."));
        TableColumn<FilaCarrito, String> codigo = columna("Código", FilaCarrito::getCodigo);
        TableColumn<FilaCarrito, String> producto = columna("Producto", FilaCarrito::getProducto);
        TableColumn<FilaCarrito, String> cantidad = columna("Cant.", FilaCarrito::getCantidad);
        TableColumn<FilaCarrito, String> precio = columna("Precio unitario", FilaCarrito::getPrecio);
        TableColumn<FilaCarrito, String> subtotal = columna("Subtotal", FilaCarrito::getSubtotal);
        tabla.getColumns().add(codigo);
        tabla.getColumns().add(producto);
        tabla.getColumns().add(cantidad);
        tabla.getColumns().add(precio);
        tabla.getColumns().add(subtotal);
        return tabla;
    }

    private TableColumn<FilaCarrito, String> columna(String titulo, java.util.function.Function<FilaCarrito, String> valor) {
        TableColumn<FilaCarrito, String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(fila -> new SimpleStringProperty(valor.apply(fila.getValue())));
        return columna;
    }

    static String estiloTarjeta() {
        return "-fx-background-color:white;-fx-background-radius:10;-fx-border-color:#e2e8f0;-fx-border-radius:10;";
    }

    private static class FilaCarrito {
        private String getCodigo() { return ""; }
        private String getProducto() { return ""; }
        private String getCantidad() { return ""; }
        private String getPrecio() { return ""; }
        private String getSubtotal() { return ""; }
    }
}
