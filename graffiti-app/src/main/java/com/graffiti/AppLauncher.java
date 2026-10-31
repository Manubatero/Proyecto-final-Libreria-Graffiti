package com.graffiti;

import com.graffiti.view.InventarioTab;
import com.graffiti.view.VentasTab;
import com.graffiti.view.MenuAccionesView;
import com.graffiti.view.PuntoDeVentaModularView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;

public class AppLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            TabPane tabPane = new TabPane();

            // Carga la pestaña del CRUD de Inventario
            InventarioTab inventarioTab = new InventarioTab();
            inventarioTab.setClosable(false);
            tabPane.getTabs().add(inventarioTab);
            PuntoDeVentaModularView puntoVenta = new PuntoDeVentaModularView();
            tabPane.getTabs().add(puntoVenta);
            tabPane.getTabs().add(new VentasTab());
            tabPane.getTabs().forEach(tab -> tab.setClosable(false));

            MenuAccionesView menu = new MenuAccionesView();
            menu.agregarOpcion("Registrar nuevo vendedor", puntoVenta::registrarNuevoVendedor);
            HBox barraSuperior = new HBox(menu);
            barraSuperior.setAlignment(Pos.CENTER_RIGHT);
            barraSuperior.setPadding(new Insets(8, 18, 8, 18));
            barraSuperior.setStyle("-fx-background-color:#0b1220;");
            BorderPane root = new BorderPane(tabPane);
            root.setTop(barraSuperior);
            Scene scene = new Scene(root, 1000, 600);
            scene.getStylesheets().add(AppLauncher.class.getResource("/theme.css").toExternalForm());
            primaryStage.setTitle("Sistema Graffiti - Gestión de Stock");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
