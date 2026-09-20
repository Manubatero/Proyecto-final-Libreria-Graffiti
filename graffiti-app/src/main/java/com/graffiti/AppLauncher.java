package com.graffiti;

import com.graffiti.view.InventarioTab;
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
            tabPane.getTabs().add(inventarioTab);

            Scene scene = new Scene(tabPane, 1000, 600);
            primaryStage.setTitle("Sistema Graffiti - Gestión de Stock");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}