package com.graffiti.view;

import com.graffiti.dao.VendedorDAO;
import com.graffiti.dao.impl.VendedorDAOImpl;
import com.graffiti.model.Vendedor;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public final class DialogoVendedor {
    private DialogoVendedor() {}

    public static Vendedor mostrar() {
        TextField nombre = new TextField(); nombre.setPromptText("Nombre");
        TextField apellido = new TextField(); apellido.setPromptText("Apellido");
        for (TextField campo : new TextField[]{nombre, apellido})
            campo.setStyle("-fx-text-fill:#f8fafc;-fx-prompt-text-fill:#94a3b8;-fx-background-color:#1b2533;-fx-border-color:#3b4b60;-fx-border-radius:7;-fx-background-radius:7;-fx-padding:9;");
        VBox contenido = new VBox(12, new Label("Nombre"), nombre, new Label("Apellido"), apellido);
        contenido.setPadding(new Insets(18)); contenido.setPrefWidth(360);
        Dialog<Vendedor> dialogo = new Dialog<>();
        dialogo.setTitle("Registrar nuevo vendedor");
        dialogo.setHeaderText("Ingrese los datos del vendedor");
        ButtonType guardar = new ButtonType("Registrar", ButtonBar.ButtonData.OK_DONE);
        dialogo.getDialogPane().getButtonTypes().addAll(guardar, ButtonType.CANCEL);
        dialogo.getDialogPane().setContent(contenido);
        dialogo.getDialogPane().setStyle("-fx-background-color:#172131;");
        dialogo.getDialogPane().lookupAll(".label").forEach(n -> ((Label)n).setStyle("-fx-text-fill:#e2e8f0;"));
        Button botonGuardar = (Button) dialogo.getDialogPane().lookupButton(guardar);
        botonGuardar.addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            if (nombre.getText().isBlank() || apellido.getText().isBlank()) {
                e.consume();
                Alert alerta = new Alert(Alert.AlertType.WARNING, "Complete el nombre y el apellido.", ButtonType.OK);
                alerta.setHeaderText("Datos incompletos"); alerta.showAndWait();
            }
        });
        dialogo.setResultConverter(b -> {
            if (b != guardar) return null;
            Vendedor vendedor = new Vendedor();
            vendedor.setNombre(nombre.getText().trim()); vendedor.setApellido(apellido.getText().trim());
            VendedorDAO dao = new VendedorDAOImpl();
            if (!dao.insertar(vendedor)) {
                Alert alerta = new Alert(Alert.AlertType.ERROR, "No se pudo registrar el vendedor. Verifique la conexión con la base de datos.", ButtonType.OK);
                alerta.setHeaderText("Error al guardar"); alerta.showAndWait();
                return null;
            }
            return vendedor;
        });
        return dialogo.showAndWait().orElse(null);
    }
}
