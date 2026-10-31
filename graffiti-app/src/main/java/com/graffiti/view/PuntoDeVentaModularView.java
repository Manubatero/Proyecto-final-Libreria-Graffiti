package com.graffiti.view;

import com.graffiti.dao.ProductoDAO;
import com.graffiti.dao.VentaDAO;
import com.graffiti.dao.impl.ProductoDAOImpl;
import com.graffiti.dao.impl.VentaDAOImpl;
import com.graffiti.model.DetalleVenta;
import com.graffiti.model.Producto;
import com.graffiti.model.Venta;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/** Orquesta el punto de venta; búsqueda, cantidad y pago viven en componentes propios. */
public final class PuntoDeVentaModularView extends Tab {
    private final ProductoDAO productos = new ProductoDAOImpl();
    private final VentaDAO ventas = new VentaDAOImpl();
    private final ObservableList<FilaCarrito> carrito = FXCollections.observableArrayList();
    private final TableView<FilaCarrito> tabla = new TableView<>(carrito);
    private final ResumenCobroView resumen = new ResumenCobroView();
    private SelectorProductoView selectorProductos;
    private final Label estado = new Label("1 · Venta en curso");

    public PuntoDeVentaModularView() {
        super("  Punto de cobro  ");
        setClosable(false);
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(22));
        root.setStyle("-fx-background-color:#182230;");
        Label titulo = new Label("Punto de cobro");
        titulo.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:#f1f5f9;");
        estado.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#38bdf8;-fx-background-color:#263548;-fx-background-radius:8;-fx-padding:8 13;");
        estado.textProperty().addListener((o, anterior, texto) -> {
            String color = texto.startsWith("1") ? "#38bdf8" : texto.startsWith("2") ? "#f59e0b" : "#f87171";
            estado.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:" + color + ";-fx-background-color:#263548;-fx-background-radius:8;-fx-padding:8 13;");
        });
        root.setTop(new VBox(6, titulo, estado));
        SplitPane contenido = new SplitPane(crearPanelCarrito(), resumen);
        contenido.setDividerPositions(.70);
        root.setCenter(contenido);
        BorderPane.setMargin(contenido, new Insets(18, 0, 0, 0));
        setContent(root);
        resumen.setAcciones(this::iniciarPago, this::limpiarVenta);
    }

    public void registrarNuevoVendedor() {
        com.graffiti.model.Vendedor nuevo = DialogoVendedor.mostrar();
        if (nuevo != null) resumen.agregarVendedor(nuevo);
    }

    private VBox crearPanelCarrito() {
        SelectorProductoView selector = new SelectorProductoView(productos, this::agregarProducto);
        selectorProductos = selector;
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tabla.setPlaceholder(new Label("El detalle de la venta aparecerá aquí."));
        tabla.getColumns().setAll(columna("Código", f -> f.producto.getCodigoBarra()),
                columna("Producto", f -> f.producto.getNombre() + (stockBajo(f.producto) ? "   ·   ¡STOCK POR DEBAJO DEL MÍNIMO!" : "")),
                columna("Cant.", f -> String.valueOf(f.cantidad)),
                columna("Precio unitario", f -> dinero(f.producto.getPrecioVenta())),
                columna("Subtotal", f -> dinero(f.subtotal())));
        tabla.setRowFactory(tv -> {
            TableRow<FilaCarrito> fila = new TableRow<>();
            fila.setOnMouseClicked(e -> { if (e.getClickCount() == 2 && !fila.isEmpty()) editarCantidad(fila.getItem()); });
            fila.itemProperty().addListener((obs, anterior, actual) -> {
                boolean bajo = actual != null && stockBajo(actual.producto);
                fila.setStyle(bajo ? "-fx-background-color:#4c1d27;-fx-text-background-color:#fecaca;" : "");
            });
            return fila;
        });
        Button modificar = new Button("Modificar cantidad");
        modificar.setOnAction(e -> { FilaCarrito f = tabla.getSelectionModel().getSelectedItem(); if (f != null) editarCantidad(f); });
        Button quitar = new Button("Quitar seleccionado");
        quitar.setOnAction(e -> { FilaCarrito f = tabla.getSelectionModel().getSelectedItem(); if (f != null) { carrito.remove(f); actualizarTotal(); } });
        VBox panel = new VBox(13, selector, new Label("Detalle de la venta · Doble clic para editar cantidad"), tabla, new HBox(8, modificar, quitar));
        panel.setPadding(new Insets(18));
        panel.setStyle(PuntoDeVentaView.estiloTarjeta());
        VBox.setVgrow(tabla, Priority.ALWAYS);
        return panel;
    }

    private TableColumn<FilaCarrito, String> columna(String titulo, java.util.function.Function<FilaCarrito, String> valor) {
        TableColumn<FilaCarrito, String> columna = new TableColumn<>(titulo);
        columna.setCellValueFactory(c -> new SimpleStringProperty(valor.apply(c.getValue())));
        return columna;
    }

    private void agregarProducto(Producto producto) {
        FilaCarrito existente = buscarFila(producto);
        int stock = producto.getStockActual() == null ? 0 : producto.getStockActual();
        int disponible = stock - (existente == null ? 0 : existente.cantidad);
        if (disponible < 1) { aviso("Stock insuficiente", "No hay unidades disponibles para agregar."); return; }
        Integer cantidad = DialogoCantidadProducto.mostrar(producto, disponible);
        if (cantidad == null) return;
        if (existente == null) carrito.add(new FilaCarrito(producto, cantidad));
        else existente.cantidad += cantidad;
        tabla.refresh();
        actualizarTotal();
    }

    private void editarCantidad(FilaCarrito fila) {
        int stock = fila.producto.getStockActual() == null ? fila.cantidad : fila.producto.getStockActual();
        Integer nuevaCantidad = DialogoCantidadProducto.mostrar(fila.producto, stock, fila.cantidad, "Modificar cantidad", "Nueva cantidad");
        if (nuevaCantidad != null) { fila.cantidad = nuevaCantidad; tabla.refresh(); actualizarTotal(); }
    }

    private void iniciarPago() {
        if (carrito.isEmpty()) { aviso("Carrito vacío", "Agregue productos antes de iniciar el pago."); return; }
        if (resumen.getIdVendedor() == null) { aviso("Vendedor requerido", "Seleccione el vendedor responsable."); return; }
        if (!resumen.importeRecibidoSuficiente()) { aviso("Importe inválido", "Revise el importe recibido."); return; }
        Alert revisar = new Alert(Alert.AlertType.CONFIRMATION);
        revisar.setTitle("Revisar venta"); revisar.setHeaderText("¿Está seguro de que el pedido y las cantidades son correctos?");
        revisar.setContentText("Total: " + dinero(total()));
        if (revisar.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) return;

        ChoiceDialog<String> metodo = new ChoiceDialog<>("Efectivo", List.of("Débito", "Crédito", "QR (Mercado Pago)", "Efectivo"));
        metodo.setTitle("Método de pago"); metodo.setHeaderText("Venta pendiente de pago"); metodo.setContentText("Seleccione el método:");
        String seleccionado = metodo.showAndWait().orElse(null);
        if (seleccionado == null) return;
        estado.setText("2 · Pendiente de pago · " + seleccionado);
        Alert espera = new Alert(Alert.AlertType.CONFIRMATION);
        espera.setTitle("Esperando pago");
        espera.setHeaderText("Confirme cuando el pago haya sido aprobado");
        espera.setContentText(seleccionado.startsWith("QR") ? "Verifique el pago en Mercado Pago y confirme aquí." :
                seleccionado.equals("Efectivo") ? "Reciba el efectivo y confirme el pago aquí." : "Verifique la aprobación en la terminal y confirme aquí.");
        espera.getButtonTypes().setAll(new ButtonType("Pago confirmado", ButtonBar.ButtonData.OK_DONE), ButtonType.CANCEL);
        if (espera.showAndWait().orElse(ButtonType.CANCEL).getButtonData() == ButtonBar.ButtonData.CANCEL_CLOSE) {
            estado.setText("1 · Venta en curso"); return;
        }
        registrarVenta();
    }

    private void registrarVenta() {
        Venta venta = new Venta();
        venta.setFechaHora(LocalDateTime.now());
        venta.setIdVendedor(resumen.getIdVendedor());
        List<DetalleVenta> detalles = new ArrayList<>();
        for (FilaCarrito fila : carrito) {
            DetalleVenta detalle = new DetalleVenta();
            detalle.setIdProducto(fila.producto.getIdProducto());
            detalle.setCantidad(fila.cantidad);
            detalle.setPrecioUnitario(fila.producto.getPrecioVenta());
            detalles.add(detalle);
        }
        boolean registrada = ventas.registrarVenta(venta, detalles);
        if (registrada) selectorProductos.refrescarCatalogo();
        if (registrada) {
            estado.setText("3 · Pagado · Venta #" + venta.getIdVenta());
            Alert ok = new Alert(Alert.AlertType.INFORMATION, "La venta #" + venta.getIdVenta() + " se registró en ventas recientes.", ButtonType.OK);
            ok.setHeaderText("Pago confirmado"); ok.showAndWait();
            limpiarVenta();
        } else {
            estado.setText("2 · Pendiente de pago · No se pudo registrar");
            aviso("No se pudo registrar", "No se guardó la venta. Verifique el stock y la conexión con la base de datos.");
        }
    }

    private FilaCarrito buscarFila(Producto p) { return carrito.stream().filter(f -> f.producto.getIdProducto().equals(p.getIdProducto())).findFirst().orElse(null); }
    private boolean stockBajo(Producto p) { return p.getStockActual() != null && p.getStockMinimo() != null && p.getStockActual() <= p.getStockMinimo(); }
    private BigDecimal total() { return carrito.stream().map(FilaCarrito::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add); }
    private void actualizarTotal() { resumen.actualizarImportes(total()); }
    private void limpiarVenta() { carrito.clear(); tabla.refresh(); resumen.limpiar(); actualizarTotal(); estado.setText("1 · Venta en curso"); }
    private void aviso(String titulo, String mensaje) { Alert a = new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK); a.setTitle(titulo); a.setHeaderText(null); a.showAndWait(); }
    private static String dinero(BigDecimal valor) { return "$ " + (valor == null ? "0.00" : valor.toPlainString()); }

    private static final class FilaCarrito {
        private final Producto producto;
        private int cantidad;
        private FilaCarrito(Producto producto, int cantidad) { this.producto = producto; this.cantidad = cantidad; }
        private BigDecimal subtotal() { return producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)); }
    }
}
