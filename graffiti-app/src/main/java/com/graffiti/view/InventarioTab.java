package com.graffiti.view;

import com.graffiti.dao.ProductoDAO;
import com.graffiti.dao.impl.ProductoDAOImpl;
import com.graffiti.model.Producto;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;

public class InventarioTab extends Tab {

    private final ProductoDAO productoDAO = new ProductoDAOImpl();
    private final ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private TableView<Producto> tabla;

    // Campos del formulario
    private TextField txtCodigo, txtNombre, txtPrecioVenta, txtStockActual, txtStockMinimo, txtImagenPath;
    private ImageView imgPreview;
    private Button btnBuscarImagen, btnGuardar, btnEliminar, btnLimpiar;
    private Producto productoSeleccionado = null; // Controla si es edición o alta

    public InventarioTab() {
        super("  Gestión de Inventario (CRUD)  ");
        cargarProductos();
        initUI();
    }

    public void cargarProductos() {
        listaProductos.clear();
        listaProductos.addAll(productoDAO.listarTodos());
    }

    private void initUI() {
        Label title = new Label("ADMINISTRACIÓN DE PRODUCTOS");
        title.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#172554;");

        // Configuración de Tabla
        tabla = new TableView<>(listaProductos);
        tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Producto, String> colCodigo = new TableColumn<>("Código");
        colCodigo.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getCodigoBarra()));

        TableColumn<Producto, String> colNombre = new TableColumn<>("Producto");
        colNombre.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNombre()));

        TableColumn<Producto, String> colPrecio = new TableColumn<>("Precio Venta");
        colPrecio.setCellValueFactory(d -> new SimpleStringProperty(
                d.getValue().getPrecioVenta() != null ? "$ " + d.getValue().getPrecioVenta() : "$ 0.00"
        ));

        TableColumn<Producto, String> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStockActual())));

        TableColumn<Producto, String> colMinimo = new TableColumn<>("Mínimo");
        colMinimo.setCellValueFactory(d -> new SimpleStringProperty(String.valueOf(d.getValue().getStockMinimo())));

        tabla.getColumns().addAll(colCodigo, colNombre, colPrecio, colStock, colMinimo);

        // CARD 3.3: Alerta visual de stock bajo (pinta en rojo tenue las filas con stockActual <= stockMinimo)
        tabla.setRowFactory(tv -> new TableRow<>() {
            @Override
            protected void updateItem(Producto item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (item.getStockActual() != null && item.getStockMinimo() != null
                        && item.getStockActual() <= item.getStockMinimo()) {
                    setStyle("-fx-background-color: #f8d7da; -fx-text-fill: #721c24; -fx-font-weight: bold;");
                } else {
                    setStyle("");
                }
            }
        });

        // Evento de Selección de Fila para Edición (Modificación)
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                productoSeleccionado = newVal;
                txtCodigo.setText(newVal.getCodigoBarra());
                txtNombre.setText(newVal.getNombre());
                txtPrecioVenta.setText(newVal.getPrecioVenta() != null ? newVal.getPrecioVenta().toString() : "");
                txtStockActual.setText(String.valueOf(newVal.getStockActual()));
                txtStockMinimo.setText(String.valueOf(newVal.getStockMinimo()));
                txtImagenPath.setText(newVal.getImagenPath() != null ? newVal.getImagenPath() : "");

                cargarVistaPreviaImagen(newVal.getImagenPath());

                btnGuardar.setText("Actualizar Producto");
                btnEliminar.setDisable(false);
            }
        });

        // Formulario Lateral
        VBox formulario = crearFormulario();

        Button btnRefrescar = new Button("🔄 Refrescar Listado");
        btnRefrescar.setOnAction(e -> cargarProductos());

        VBox contenedorIzquierdo = new VBox(10, title, btnRefrescar, tabla);
        contenedorIzquierdo.setPadding(new Insets(15));
        VBox.setVgrow(tabla, Priority.ALWAYS);

        SplitPane split = new SplitPane(contenedorIzquierdo, formulario);
        split.setDividerPositions(0.68);

        this.setContent(split);
    }

    private VBox crearFormulario() {
        Label tituloForm = new Label("Formulario de Producto");
        tituloForm.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#172554;");

        txtCodigo = new TextField(); txtCodigo.setPromptText("Código de barras");
        txtNombre = new TextField(); txtNombre.setPromptText("Nombre del producto");
        txtPrecioVenta = new TextField(); txtPrecioVenta.setPromptText("Precio de venta ($)");
        txtStockActual = new TextField(); txtStockActual.setPromptText("Stock actual");
        txtStockMinimo = new TextField(); txtStockMinimo.setPromptText("Stock mínimo");

        // Componentes para selección y vista previa de foto
        txtImagenPath = new TextField();
        txtImagenPath.setPromptText("Ruta de la imagen...");
        txtImagenPath.setEditable(false);

        btnBuscarImagen = new Button("📷 Seleccionar Foto");
        btnBuscarImagen.setMaxWidth(Double.MAX_VALUE);
        btnBuscarImagen.setOnAction(e -> seleccionarImagen());

        imgPreview = new ImageView();
        imgPreview.setFitWidth(90);
        imgPreview.setFitHeight(90);
        imgPreview.setPreserveRatio(true);

        VBox boxFoto = new VBox(5, new Label("Foto del Producto:"), btnBuscarImagen, txtImagenPath, imgPreview);
        boxFoto.setAlignment(Pos.CENTER_LEFT);

        btnGuardar = new Button("Guardar Nuevo");
        btnGuardar.setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;-fx-font-weight:bold;");
        btnGuardar.setMaxWidth(Double.MAX_VALUE);

        btnEliminar = new Button("Dar de Baja (Eliminar)");
        btnEliminar.setStyle("-fx-background-color:#dc2626;-fx-text-fill:white;-fx-font-weight:bold;");
        btnEliminar.setMaxWidth(Double.MAX_VALUE);
        btnEliminar.setDisable(true); // Se activa al seleccionar una fila

        btnLimpiar = new Button("Limpiar Formulario");
        btnLimpiar.setMaxWidth(Double.MAX_VALUE);

        // --- Lógica del CRUD ---

        // 1. ALTA Y MODIFICACIÓN
        btnGuardar.setOnAction(e -> {
            try {
                Producto p = (productoSeleccionado != null) ? productoSeleccionado : new Producto();
                p.setCodigoBarra(txtCodigo.getText().trim());
                p.setNombre(txtNombre.getText().trim());
                p.setPrecioVenta(new BigDecimal(txtPrecioVenta.getText().trim()));
                p.setStockActual(Integer.parseInt(txtStockActual.getText().trim()));
                p.setStockMinimo(Integer.parseInt(txtStockMinimo.getText().trim()));
                p.setImagenPath(txtImagenPath.getText().trim());
                p.setActivo(true);

                boolean exito;
                if (productoSeleccionado == null) {
                    exito = productoDAO.insertar(p); // Alta
                } else {
                    exito = productoDAO.actualizar(p); // Modificación
                }

                if (exito) {
                    cargarProductos();
                    limpiarFormulario();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Operación exitosa", "El producto se guardó correctamente en la Base de Datos.");
                }
            } catch (Exception ex) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de validación", "Verifique los datos ingresados: " + ex.getMessage());
            }
        });

        // 2. BAJA (ELIMINACIÓN LÓGICA)
        btnEliminar.setOnAction(e -> {
            if (productoSeleccionado != null) {
                Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                        "¿Desea dar de baja el producto " + productoSeleccionado.getNombre() + "?",
                        ButtonType.YES, ButtonType.NO);

                confirmacion.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        if (productoDAO.eliminarLogico(productoSeleccionado.getIdProducto())) {
                            cargarProductos();
                            limpiarFormulario();
                            mostrarAlerta(Alert.AlertType.INFORMATION, "Producto Eliminado", "Baja realizada con éxito.");
                        }
                    }
                });
            }
        });

        btnLimpiar.setOnAction(e -> limpiarFormulario());

        VBox form = new VBox(8, tituloForm,
                new Label("Código:"), txtCodigo,
                new Label("Nombre:"), txtNombre,
                new Label("Precio Venta:"), txtPrecioVenta,
                new Label("Stock Actual:"), txtStockActual,
                new Label("Stock Mínimo:"), txtStockMinimo,
                boxFoto,
                new Separator(), btnGuardar, btnEliminar, btnLimpiar);

        form.setPadding(new Insets(15));
        form.setPrefWidth(300);
        return form;
    }

    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Foto del Producto");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Todas las Imágenes", "*.jpg", "*.jpeg", "*.png", "*.webp"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPG / JPEG", "*.jpg", "*.jpeg")
        );
        File archivo = fileChooser.showOpenDialog(null);
        if (archivo != null) {
            txtImagenPath.setText(archivo.getAbsolutePath());
            cargarVistaPreviaImagen(archivo.getAbsolutePath());
        }
    }

    private void cargarVistaPreviaImagen(String path) {
        if (path != null && !path.trim().isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                imgPreview.setImage(new Image(f.toURI().toString()));
                return;
            }
        }
        imgPreview.setImage(null);
    }

    private void limpiarFormulario() {
        productoSeleccionado = null;
        txtCodigo.clear();
        txtNombre.clear();
        txtPrecioVenta.clear();
        txtStockActual.clear();
        txtStockMinimo.clear();
        txtImagenPath.clear();
        imgPreview.setImage(null);
        btnGuardar.setText("Guardar Nuevo");
        btnEliminar.setDisable(true);
        tabla.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert a = new Alert(tipo);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(mensaje);
        a.showAndWait();
    }
}