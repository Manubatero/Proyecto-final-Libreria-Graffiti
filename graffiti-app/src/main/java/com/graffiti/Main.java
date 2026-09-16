package com.graffiti;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/** Visual prototype only: no database, scanner, or printer integration. */
public class Main extends Application {
    private final ObservableList<ProductoVista> productos = FXCollections.observableArrayList(
        new ProductoVista("7791234567890", "Aerosol Negro Mate", "$ 8.500", 6, 8, "Reposici\u00f3n"),
        new ProductoVista("7791234567891", "Marcador Paint Azul", "$ 4.200", 18, 5, "Disponible"),
        new ProductoVista("7791234567892", "Sketchbook A4", "$ 6.900", 3, 4, "Reposici\u00f3n")
    );
    private final ObservableList<ItemVenta> carrito = FXCollections.observableArrayList(new ItemVenta("Aerosol Negro Mate", 1, "$ 8.500"));

    @Override public void start(Stage stage) {
        BorderPane root = new BorderPane(); root.setTop(header());
        TabPane tabs = new TabPane(inventario(), puntoDeVenta()); tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        root.setCenter(tabs); root.setStyle("-fx-font-family:'Poppins','Segoe UI';-fx-background-color:#f4f7fb;-fx-accent:#2563eb;");
        stage.setScene(new Scene(root, 1366, 728)); stage.setTitle("Graffiti | Gesti\u00f3n de Stock"); stage.setMinWidth(1100); stage.setMinHeight(650); stage.show();
    }
    private HBox header() {
        Label marca = new Label("GRAFFITI"); marca.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:white;");
        Label sub = new Label("GESTI\u00d3N DE STOCK  |  PUNTO DE COBRO"); sub.setStyle("-fx-font-size:10px;-fx-text-fill:#bfdbfe;-fx-font-weight:bold;");
        Label alerta = new Label("\u26a0  2 productos requieren reposici\u00f3n"); alerta.setStyle("-fx-font-weight:bold;-fx-text-fill:#fef3c7;-fx-background-color:#854d0e;-fx-background-radius:14;-fx-padding:7 12;");
        Region r = new Region(); HBox.setHgrow(r, Priority.ALWAYS);
        HBox box = new HBox(18,new VBox(marca,sub),r,alerta); box.setAlignment(Pos.CENTER_LEFT); box.setPadding(new Insets(14,28,14,28)); box.setStyle("-fx-background-color:linear-gradient(to right,#172554,#1e40af,#0f766e);"); return box;
    }
    private Tab inventario() {
        Label title = title("INVENTARIO DE PRODUCTOS", "Administre existencias, proveedores y alertas de reposici\u00f3n.");
        TableView<ProductoVista> tabla = new TableView<>(productos); tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabla.getColumns().addAll(col("C\u00f3digo de barras",p->p.codigo,145),col("Producto",p->p.nombre,200),col("Precio",p->p.precio,110),col("Stock",p->String.valueOf(p.stock),70),col("M\u00ednimo",p->String.valueOf(p.minimo),75),col("Estado",p->p.estado,105));
        tabla.setRowFactory(t -> new TableRow<>() { { selectedProperty().addListener((obs, before, selected) -> pintarFila()); } @Override protected void updateItem(ProductoVista producto, boolean empty) { super.updateItem(producto, empty); pintarFila(); } private void pintarFila() { if (isEmpty() || getItem() == null) setStyle(""); else if (getItem().stock <= getItem().minimo && isSelected()) setStyle("-fx-background-color:#f59e0b;-fx-text-fill:#431407;"); else if (getItem().stock <= getItem().minimo) setStyle("-fx-background-color:#fff7ed;"); else if (isSelected()) setStyle("-fx-background-color:#2563eb;-fx-text-fill:white;"); else setStyle(""); } });
        TextField buscar=field("Buscar por producto o c\u00f3digo..."); Button actualizar=button("Actualizar listado");
        HBox filtros=new HBox(10,buscar,actualizar);HBox.setHgrow(buscar,Priority.ALWAYS); VBox lista=new VBox(11,title,filtros,tabla);lista.setPadding(new Insets(18));VBox.setVgrow(tabla,Priority.ALWAYS);
        SplitPane split=new SplitPane(lista,formulario());split.setDividerPositions(.64);return new Tab("  Inventario  ",split);
    }
    private VBox formulario() {
        Label titulo=new Label("Nuevo / editar producto");titulo.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:#172554;");
        TextField codigo=field("C\u00f3digo de barras *"),nombre=field("Nombre del producto *"),proveedor=field("Proveedor"),descripcion=field("Descripci\u00f3n"),venta=field("Precio de venta *"),costo=field("Precio de costo"),actual=field("Stock actual *"),minimo=field("Umbral m\u00ednimo *"),path=field("Ruta de imagen (Path)");
        Label preview=new Label("VISTA PREVIA\nDE IMAGEN");preview.setAlignment(Pos.CENTER);preview.setPrefSize(170,76);preview.setStyle("-fx-background-color:#e0f2fe;-fx-border-color:#bae6fd;-fx-border-radius:8;-fx-background-radius:8;-fx-text-fill:#0369a1;-fx-font-size:10px;-fx-font-weight:bold;");
        Label help=new Label("Seleccione una imagen desde el equipo para asociarla al producto.");help.setWrapText(true);help.setStyle("-fx-font-size:11px;-fx-text-fill:#64748b;");
        Button guardar=accent("Guardar producto","#2563eb"), borrar=accent("Eliminar","#dc2626");
        VBox box=new VBox(9,titulo,section("DATOS GENERALES"),codigo,nombre,proveedor,descripcion,section("PRECIOS Y STOCK"),venta,costo,actual,minimo,section("IMAGEN"),path,new HBox(8,button("Elegir imagen"),button("Limpiar campos")),preview,help,new HBox(8,guardar,borrar));box.setPadding(new Insets(22));box.setPrefWidth(370);box.setStyle("-fx-background-color:white;-fx-border-color:#dbeafe;-fx-border-width:0 0 0 1;");return box;
    }
    private Tab puntoDeVenta() {
        Label encabezado=title("PUNTO DE COBRO", "Capture productos r\u00e1pidamente con el lector o el c\u00f3digo de barras.");
        TextField lector=field("Escanee o ingrese el c\u00f3digo de barras y presione Enter");lector.setStyle("-fx-font-size:16px;-fx-padding:12px;-fx-background-radius:8;-fx-border-radius:8;-fx-border-color:#93c5fd;");
        Label hid=new Label("LISTO  |  Lector USB HID conectado: el esc\u00e1ner funciona como un teclado.");hid.setStyle("-fx-text-fill:#047857;-fx-font-size:12px;-fx-font-weight:bold;-fx-background-color:#d1fae5;-fx-background-radius:6;-fx-padding:7 10;");
        TableView<ItemVenta> tabla=new TableView<>(carrito);tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);tabla.getColumns().addAll(colItem("Producto",i->i.nombre,380),colItem("Cantidad",i->String.valueOf(i.cantidad),100),colItem("P. unitario",i->i.precio,145),colItem("Subtotal",i->i.precio,145));
        Label total=new Label("TOTAL  $ 8.500");total.setStyle("-fx-font-size:27px;-fx-font-weight:bold;-fx-text-fill:#172554;");Button cobrar=accent("Cobrar e imprimir ticket","#0f766e");cobrar.setOnAction(e->ticket());
        Region r=new Region();HBox.setHgrow(r,Priority.ALWAYS);HBox footer=new HBox(10,button("Quitar item"),accent("Cancelar venta","#64748b"),r,total,cobrar);footer.setAlignment(Pos.CENTER_RIGHT);footer.setPadding(new Insets(14,0,0,0));VBox root=new VBox(11,encabezado,lector,hid,tabla,footer);root.setPadding(new Insets(24));VBox.setVgrow(tabla,Priority.ALWAYS);return new Tab("  Punto de cobro  ",root);
    }
    private Label title(String a,String b){Label l=new Label(a+"\n"+b);l.setStyle("-fx-font-size:20px;-fx-font-weight:bold;-fx-text-fill:#172554;");return l;}
    private Label section(String s){Label l=new Label(s);l.setStyle("-fx-font-size:10px;-fx-font-weight:bold;-fx-text-fill:#2563eb;-fx-padding:9 0 0 0;");return l;}
    private TextField field(String prompt){TextField f=new TextField();f.setPromptText(prompt);f.setStyle("-fx-background-radius:6;-fx-border-radius:6;-fx-border-color:#cbd5e1;-fx-padding:8 10;");return f;}
    private Button button(String text){Button b=new Button(text);b.setStyle("-fx-background-color:#ffffff;-fx-text-fill:#1e3a8a;-fx-font-weight:bold;-fx-border-color:#93c5fd;-fx-border-radius:6;-fx-background-radius:6;-fx-padding:8 13;-fx-cursor:hand;");b.setOnMouseEntered(e->b.setStyle("-fx-background-color:#eff6ff;-fx-text-fill:#1d4ed8;-fx-font-weight:bold;-fx-border-color:#2563eb;-fx-border-radius:6;-fx-background-radius:6;-fx-padding:8 13;-fx-cursor:hand;"));b.setOnMouseExited(e->b.setStyle("-fx-background-color:#ffffff;-fx-text-fill:#1e3a8a;-fx-font-weight:bold;-fx-border-color:#93c5fd;-fx-border-radius:6;-fx-background-radius:6;-fx-padding:8 13;-fx-cursor:hand;"));return b;}
    private Button accent(String text,String color){Button b=button(text);b.setStyle("-fx-background-color:"+color+";-fx-text-fill:white;-fx-font-weight:bold;-fx-background-radius:6;-fx-padding:9 15;-fx-cursor:hand;");return b;}
    private TableColumn<ProductoVista,String> col(String text,java.util.function.Function<ProductoVista,String> value,double width){TableColumn<ProductoVista,String> c=new TableColumn<>(text);c.setCellValueFactory(v->new SimpleStringProperty(value.apply(v.getValue())));c.setPrefWidth(width);return c;}
    private TableColumn<ItemVenta,String> colItem(String text,java.util.function.Function<ItemVenta,String> value,double width){TableColumn<ItemVenta,String> c=new TableColumn<>(text);c.setCellValueFactory(v->new SimpleStringProperty(value.apply(v.getValue())));c.setPrefWidth(width);return c;}
    private void ticket(){Alert a=new Alert(Alert.AlertType.INFORMATION,"GRAFFITI\nTICKET INTERNO NO FISCAL\n\nVenta #000124\nAerosol Negro Mate   $ 8.500\n--------------------------------\nTOTAL: $ 8.500\n\nGracias por su compra.",ButtonType.OK);a.setTitle("Comprobante");a.setHeaderText("Venta registrada");a.showAndWait();}
    private static class ProductoVista{final String codigo,nombre,precio,estado;final int stock,minimo;ProductoVista(String c,String n,String p,int s,int m,String e){codigo=c;nombre=n;precio=p;stock=s;minimo=m;estado=e;}}
    private static class ItemVenta{final String nombre,precio;final int cantidad;ItemVenta(String n,int c,String p){nombre=n;cantidad=c;precio=p;}}
    public static void main(String[] args){launch(args);}
}
