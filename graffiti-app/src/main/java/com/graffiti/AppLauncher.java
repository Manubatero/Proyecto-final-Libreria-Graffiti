package com.graffiti;

import com.graffiti.view.InventarioTab;
import com.graffiti.view.PuntoDeVentaView;
import com.graffiti.view.VentasTab;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
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
            tabPane.getTabs().add(new PuntoDeVentaView());
            tabPane.getTabs().add(new VentasTab());

            Scene scene = new Scene(tabPane, 1000, 600);
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
