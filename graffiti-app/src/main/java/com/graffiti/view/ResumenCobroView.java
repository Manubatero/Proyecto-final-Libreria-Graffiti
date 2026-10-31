package com.graffiti.view;

import com.graffiti.dao.UsuarioDAO;
import com.graffiti.dao.impl.UsuarioDAOImpl;
import com.graffiti.model.Usuario;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import java.math.BigDecimal;

/** Panel visual con los datos necesarios para revisar y cobrar una venta. */
public class ResumenCobroView extends VBox {
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();
    private final ComboBox<Usuario> vendedor = new ComboBox<>();
    private final TextField recibido = new TextField();
    private final Label subtotal = new Label("Subtotal                         $ 0,00");
    private final Label total = new Label("TOTAL                              $ 0,00");
    private final Label vuelto = new Label("$ 0,00");
    private final Button cobrar = new Button("Revisar venta y cobrar");
    private final Button cancelar = new Button("Cancelar venta");
    private BigDecimal importeTotal = BigDecimal.ZERO;

    public ResumenCobroView() {
        super(14);
        setPadding(new Insets(18));
        setPrefWidth(300);
        setMinWidth(260);
        setStyle(PuntoDeVentaView.estiloTarjeta());
        getChildren().addAll(crearTitulo(), crearImportes(), crearFormularioPago(), crearAcciones());
        cargarVendedores();
        actualizarImportes(BigDecimal.ZERO);
    }

    private Label crearTitulo() {
        Label titulo = new Label("Formulario de cobro");
        titulo.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:#f1f5f9;");
        return titulo;
    }

    private VBox crearImportes() {
        subtotal.setStyle("-fx-font-size:13px;-fx-text-fill:#475569;");
        total.setStyle("-fx-font-size:21px;-fx-font-weight:bold;-fx-text-fill:#0f766e;");
        VBox importes = new VBox(12, subtotal, new Separator(), total);
        importes.setPadding(new Insets(15));
        importes.setStyle("-fx-background-color:#f8fafc;-fx-background-radius:8;");
        return importes;
    }

    private VBox crearFormularioPago() {
        Label medioLabel = new Label("Vendedor responsable");
        vendedor.setMaxWidth(Double.MAX_VALUE);
        vendedor.setPromptText("Seleccione el vendedor");
        vendedor.setConverter(new StringConverter<>() {
            public String toString(Usuario u) { return u == null ? "" : u.getNombre() + " " + u.getApellido() + " (" + u.getNombreUsuario() + ")"; }
            public Usuario fromString(String value) { return null; }
        });
        vendedor.setCellFactory(list -> new ListCell<>() {
            protected void updateItem(Usuario u, boolean empty) { super.updateItem(u, empty); setText(empty || u == null ? null : u.getNombre() + " " + u.getApellido() + " (" + u.getNombreUsuario() + ")"); }
        });
        vendedor.setButtonCell(new ListCell<>() {
            protected void updateItem(Usuario u, boolean empty) { super.updateItem(u, empty); setText(empty || u == null ? null : u.getNombre() + " " + u.getApellido() + " (" + u.getNombreUsuario() + ")"); }
        });

        Label recibidoLabel = new Label("Importe recibido (opcional)");
        recibido.setPromptText("$ 0,00");
        recibido.textProperty().addListener((obs, old, value) -> actualizarVuelto());

        Label vueltoLabel = new Label("Vuelto");
        vuelto.setStyle("-fx-font-size:14px;-fx-font-weight:bold;-fx-text-fill:#334155;");
        HBox filaVuelto = new HBox(vueltoLabel, new Region(), vuelto);
        HBox.setHgrow(filaVuelto.getChildren().get(1), Priority.ALWAYS);
        filaVuelto.setAlignment(Pos.CENTER_LEFT);

        VBox formulario = new VBox(8, medioLabel, vendedor, recibidoLabel, recibido, filaVuelto);
        return formulario;
    }

    private VBox crearAcciones() {
        cobrar.setMaxWidth(Double.MAX_VALUE);
        cobrar.setPrefHeight(44);
        cobrar.setStyle("-fx-background-color:#0f766e;-fx-text-fill:white;-fx-font-size:14px;-fx-font-weight:bold;-fx-background-radius:7;");

        Label notaTicket = new Label("Comprobante interno no fiscal");
        notaTicket.setMaxWidth(Double.MAX_VALUE);
        notaTicket.setAlignment(Pos.CENTER);
        notaTicket.setStyle("-fx-font-size:11px;-fx-text-fill:#64748b;");

        cancelar.setMaxWidth(Double.MAX_VALUE);
        cancelar.setPrefHeight(38);
        cancelar.setStyle("-fx-background-color:white;-fx-text-fill:#b91c1c;-fx-border-color:#fecaca;-fx-border-radius:7;-fx-background-radius:7;");
        return new VBox(9, cobrar, cancelar, notaTicket);
    }

    public void setAcciones(Runnable accionCobrar, Runnable accionCancelar) {
        cobrar.setOnAction(e -> accionCobrar.run());
        cancelar.setOnAction(e -> accionCancelar.run());
    }

    public void actualizarImportes(BigDecimal importe) {
        importeTotal = importe == null ? BigDecimal.ZERO : importe;
        subtotal.setText("Subtotal                         $ " + importeTotal.toPlainString());
        total.setText("TOTAL                              $ " + importeTotal.toPlainString());
        actualizarVuelto();
    }

    public Integer getIdVendedor() { return vendedor.getValue() == null ? null : vendedor.getValue().getIdUsuario(); }
    public boolean vendedorDisponible() { return !vendedor.getItems().isEmpty(); }
    public boolean importeRecibidoSuficiente() {
        if (recibido.getText().isBlank()) return true;
        try { return new BigDecimal(recibido.getText().trim().replace(',', '.')).compareTo(importeTotal) >= 0; }
        catch (NumberFormatException e) { return false; }
    }
    public void limpiar() { recibido.clear(); }

    private void cargarVendedores() {
        vendedor.getItems().setAll(usuarioDAO.listarTodos());
        if (!vendedor.getItems().isEmpty()) vendedor.getSelectionModel().selectFirst();
        else vendedor.setDisable(true);
    }

    private void actualizarVuelto() {
        try {
            BigDecimal recibidoValue = new BigDecimal(recibido.getText().trim().replace(',', '.'));
            BigDecimal diferencia = recibidoValue.subtract(importeTotal);
            vuelto.setText("$ " + (diferencia.signum() < 0 ? BigDecimal.ZERO : diferencia).toPlainString());
        } catch (NumberFormatException e) { vuelto.setText("$ 0,00"); }
    }
}
