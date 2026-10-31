package com.graffiti.view;

import com.graffiti.model.Producto;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.util.Duration;

/** Diálogo animado para confirmar unidades antes de incluir un producto en el carrito. */
public final class DialogoCantidadProducto {
    private DialogoCantidadProducto() {}

    public static Integer mostrar(Producto producto, int maximo) {
        return mostrar(producto, maximo, 0, "Agregar al carrito", "Cantidad a agregar");
    }

    public static Integer mostrar(Producto producto, int maximo, int actual, String tituloDialogo, String etiqueta) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle(tituloDialogo);
        dialog.initModality(Modality.APPLICATION_MODAL);
        ButtonType aceptar = new ButtonType(actual == 0 ? "Agregar" : "Guardar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(aceptar, ButtonType.CANCEL);
        Label titulo = new Label(producto.getNombre());
        titulo.setStyle("-fx-font-size:19px;-fx-font-weight:bold;-fx-text-fill:#f1f5f9;");
        Label disponible = new Label("Disponible: " + maximo + " unidades");
        TextField cantidad = new TextField(actual == 0 ? "0" : String.valueOf(actual));
        cantidad.setPromptText("Ingrese una cantidad entera");
        cantidad.setStyle("-fx-font-size:20px;-fx-padding:10;-fx-text-fill:#f8fafc;-fx-prompt-text-fill:#94a3b8;-fx-background-color:#1b2533;-fx-border-color:#38bdf8;-fx-border-radius:7;-fx-background-radius:7;");
        VBox contenido = new VBox(13, titulo, disponible, new Label(etiqueta), cantidad);
        contenido.setPadding(new Insets(18));
        contenido.setPrefWidth(390);
        dialog.getDialogPane().setContent(contenido);
        dialog.getDialogPane().setStyle("-fx-background-color:#172131;-fx-pref-width:450px;");
        dialog.setResultConverter(b -> {
            if (b != aceptar) return null;
            try { int n = Integer.parseInt(cantidad.getText().trim()); return n >= 1 && n <= maximo ? n : null; }
            catch (NumberFormatException e) { return null; }
        });
        dialog.getDialogPane().lookupButton(aceptar).addEventFilter(javafx.event.ActionEvent.ACTION, e -> {
            try {
                int valor = Integer.parseInt(cantidad.getText().trim());
                if (valor < 1 || valor > maximo) { cantidad.setStyle(cantidad.getStyle() + "-fx-border-color:#ef4444;"); e.consume(); }
            } catch (NumberFormatException ex) { cantidad.setStyle(cantidad.getStyle() + "-fx-border-color:#ef4444;"); e.consume(); }
        });
        cantidad.setOnAction(e -> { e.consume(); ((Button) dialog.getDialogPane().lookupButton(aceptar)).fire(); });
        dialog.setOnShown(e -> {
            dialog.getDialogPane().setOpacity(0);
            FadeTransition fade = new FadeTransition(Duration.millis(160), dialog.getDialogPane());
            fade.setFromValue(0); fade.setToValue(1); fade.play();
            cantidad.requestFocus(); cantidad.selectAll();
        });
        return dialog.showAndWait().orElse(null);
    }
}
