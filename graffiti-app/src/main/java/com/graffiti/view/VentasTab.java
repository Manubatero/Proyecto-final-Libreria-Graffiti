package com.graffiti.view;

import com.graffiti.dao.DetalleVentaDAO;
import com.graffiti.dao.VentaDAO;
import com.graffiti.dao.impl.DetalleVentaDAOImpl;
import com.graffiti.dao.impl.VentaDAOImpl;
import com.graffiti.model.DetalleVenta;
import com.graffiti.model.Venta;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;

public class VentasTab extends Tab {

    private final VentaDAO ventaDAO = new VentaDAOImpl();
    private final DetalleVentaDAO detalleVentaDAO = new DetalleVentaDAOImpl();
    private final ObservableList<Venta> ventas = FXCollections.observableArrayList();
    private final TableView<Venta> tablaVentas = new TableView<>(ventas);

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public VentasTab() {
        super("  Ventas recientes  ");
        inicializarUI();
        cargarVentas();
    }

    private void inicializarUI() {
        Label titulo = new Label("VENTAS CONFIRMADAS");
        titulo.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#e2e8f0;");

        Label descripcion = new Label("Historial de ventas registradas, ordenado desde la más reciente.");
        descripcion.setStyle("-fx-text-fill:#94a3b8;");

        configurarTablaVentas();

        Button btnDetalle = new Button("Ver detalle");
        btnDetalle.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;-fx-font-weight:bold;");
        btnDetalle.disableProperty().bind(tablaVentas.getSelectionModel().selectedItemProperty().isNull());
        btnDetalle.setOnAction(e -> mostrarDetalleSeleccionado());

        Button btnRefrescar = new Button("Refrescar listado");
        btnRefrescar.setOnAction(e -> cargarVentas());

        tablaVentas.setRowFactory(tv -> {
            TableRow<Venta> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !fila.isEmpty()) {
                    mostrarDetalle(fila.getItem());
                }
            });
            return fila;
        });

        HBox acciones = new HBox(10, btnDetalle, btnRefrescar);
        acciones.setAlignment(Pos.CENTER_RIGHT);

        VBox contenido = new VBox(10, titulo, descripcion, tablaVentas, acciones);
        contenido.setPadding(new Insets(18));
        VBox.setVgrow(tablaVentas, Priority.ALWAYS);
        setContent(contenido);
    }

    private void configurarTablaVentas() {
        tablaVentas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaVentas.setPlaceholder(new Label("Todavía no hay ventas registradas."));

        TableColumn<Venta, String> colId = new TableColumn<>("N.º de venta");
        colId.setCellValueFactory(d -> new SimpleStringProperty("#" + d.getValue().getIdVenta()));

        TableColumn<Venta, String> colFecha = new TableColumn<>("Fecha y hora");
        colFecha.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getFechaHora() != null ? d.getValue().getFechaHora().format(FORMATO_FECHA) : "—"));

        TableColumn<Venta, String> colUsuario = new TableColumn<>("Usuario");
        colUsuario.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getIdUsuario() != null ? String.valueOf(d.getValue().getIdUsuario()) : "—"));

        TableColumn<Venta, String> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(d -> new SimpleStringProperty(formatearImporte(d.getValue().getTotal())));

        tablaVentas.getColumns().addAll(colId, colFecha, colUsuario, colTotal);
    }

    private void cargarVentas() {
        ventas.setAll(ventaDAO.listarTodas());
    }

    private void mostrarDetalleSeleccionado() {
        Venta seleccionada = tablaVentas.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            mostrarDetalle(seleccionada);
        }
    }

    private void mostrarDetalle(Venta venta) {
        ObservableList<DetalleVenta> detalles = FXCollections.observableArrayList(
                detalleVentaDAO.listarPorVenta(venta.getIdVenta()));
        TableView<DetalleVenta> tablaDetalle = new TableView<>(detalles);
        tablaDetalle.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaDetalle.setPlaceholder(new Label("Esta venta no tiene productos en el detalle."));

        TableColumn<DetalleVenta, String> colProducto = new TableColumn<>("Producto (ID)");
        colProducto.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getIdProducto())));

        TableColumn<DetalleVenta, String> colCantidad = new TableColumn<>("Cantidad");
        colCantidad.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getCantidad())));

        TableColumn<DetalleVenta, String> colPrecio = new TableColumn<>("Precio unitario");
        colPrecio.setCellValueFactory(d -> new SimpleStringProperty(formatearImporte(d.getValue().getPrecioUnitario())));

        TableColumn<DetalleVenta, String> colSubtotal = new TableColumn<>("Subtotal");
        colSubtotal.setCellValueFactory(d -> new SimpleStringProperty(formatearImporte(d.getValue().getSubtotal())));

        tablaDetalle.getColumns().addAll(colProducto, colCantidad, colPrecio, colSubtotal);

        String fecha = venta.getFechaHora() != null ? venta.getFechaHora().format(FORMATO_FECHA) : "Fecha no disponible";
        Label encabezado = new Label("Venta #" + venta.getIdVenta() + "  ·  " + fecha);
        encabezado.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#e2e8f0;");
        Label total = new Label("Total: " + formatearImporte(venta.getTotal()));
        total.setStyle("-fx-font-size:15px;-fx-font-weight:bold;-fx-text-fill:#e2e8f0;");

        VBox contenido = new VBox(12, encabezado, tablaDetalle, total);
        contenido.setPadding(new Insets(15));
        VBox.setVgrow(tablaDetalle, Priority.ALWAYS);

        Dialog<Void> dialogo = new Dialog<>();
        dialogo.setTitle("Detalle de venta #" + venta.getIdVenta());
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialogo.getDialogPane().setPrefSize(620, 400);
        dialogo.showAndWait();
    }

    private String formatearImporte(BigDecimal importe) {
        return importe != null ? "$ " + importe.toPlainString() : "$ 0.00";
    }
}
