package com.graffiti.view;
import com.graffiti.dao.*;
import com.graffiti.dao.impl.*;
import com.graffiti.model.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class PuntoDeVentaView extends Tab {
 private final ProductoDAO productos=new ProductoDAOImpl();
 private final VentaDAO ventas=new VentaDAOImpl();
 private final ObservableList<Fila> carrito=FXCollections.observableArrayList();
 private final TableView<Fila> tabla=new TableView<>(carrito);
 private final TextField busqueda=new TextField();
 private final ResumenCobroView resumen=new ResumenCobroView();
 private List<Producto> catalogo=List.of();
 private final javafx.stage.Popup sugerencias=new javafx.stage.Popup();
 private final VBox listaSugerencias=new VBox(2);
 public PuntoDeVentaView(){
  super("  Punto de cobro  ");setClosable(false);
  BorderPane root=new BorderPane();root.setPadding(new Insets(22));root.setStyle("-fx-background-color:#182230;");
  Label title=new Label("Punto de cobro");title.setStyle("-fx-font-size:24px;-fx-font-weight:bold;-fx-text-fill:#f1f5f9;");
  root.setTop(new VBox(5,title,new Label("Escanee el código de barras o busque un producto para iniciar la venta.")));
  SplitPane area=new SplitPane(panel(),resumen);area.setDividerPositions(.70);root.setCenter(area);BorderPane.setMargin(area,new Insets(18,0,0,0));
  setContent(root);resumen.setAcciones(this::cobrar,this::limpiar);
 }
 private VBox panel(){
  busqueda.setPromptText("Código de barras o nombre del producto");busqueda.setPrefHeight(40);
  Button add=new Button("Agregar producto");add.setOnAction(e->agregar());busqueda.setOnAction(e->agregar());
  HBox search=new HBox(10,busqueda,add);search.setAlignment(Pos.CENTER_LEFT);HBox.setHgrow(busqueda,Priority.ALWAYS);
  tabla.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
  tabla.setPlaceholder(new Label("El detalle de la venta aparecerá aquí."));
  tabla.getColumns().addAll(col("Código",f->f.p.getCodigoBarra()),col("Producto",f->f.p.getNombre()),col("Cant.",f->""+f.cantidad),col("Precio unitario",f->money(f.p.getPrecioVenta())),col("Subtotal",f->money(f.subtotal())));
  Button quitar=new Button("Quitar seleccionado");quitar.setOnAction(e->{Fila f=tabla.getSelectionModel().getSelectedItem();if(f!=null){carrito.remove(f);total();}});
  VBox box=new VBox(14,search,new Label("Detalle de la venta"),tabla,quitar,new Label("El lector de barras puede escribir directamente en el campo de búsqueda."));
  box.setPadding(new Insets(18));box.setStyle(estiloTarjeta());VBox.setVgrow(tabla,Priority.ALWAYS);return box;
 }
 private TableColumn<Fila,String> col(String n,java.util.function.Function<Fila,String> val){TableColumn<Fila,String> c=new TableColumn<>(n);c.setCellValueFactory(x->new SimpleStringProperty(val.apply(x.getValue())));return c;}
 private void agregar(){
  String q=busqueda.getText().trim();if(q.isEmpty())return;
  Producto p=productos.buscarPorCodigoBarra(q);
  if(p==null||!Boolean.TRUE.equals(p.getActivo()))p=productos.listarTodos().stream().filter(x->x.getNombre()!=null&&x.getNombre().toLowerCase().contains(q.toLowerCase())).findFirst().orElse(null);
  if(p==null||!Boolean.TRUE.equals(p.getActivo())){alert(Alert.AlertType.WARNING,"Producto no encontrado","No existe un producto activo con ese código o nombre.");return;}
  final int id=p.getIdProducto();Fila f=carrito.stream().filter(x->x.p.getIdProducto().equals(id)).findFirst().orElse(null);
  if(p.getStockActual()==null||(f==null?0:f.cantidad)>=p.getStockActual()){alert(Alert.AlertType.WARNING,"Stock insuficiente","No hay unidades disponibles para agregar.");return;}
  if(f==null)carrito.add(new Fila(p));else f.cantidad++;tabla.refresh();total();busqueda.clear();busqueda.requestFocus();
 }
 private void cobrar(){
  if(carrito.isEmpty()){alert(Alert.AlertType.WARNING,"Carrito vacío","Agregue al menos un producto antes de cobrar.");return;}
  if(resumen.getIdVendedor()==null){alert(Alert.AlertType.ERROR,"Vendedor requerido","No hay un vendedor activo para asociar a la venta.");return;}
  if(!resumen.importeRecibidoSuficiente()){alert(Alert.AlertType.WARNING,"Importe inválido","Revise el importe recibido; debe cubrir el total.");return;}
  Venta v=new Venta();v.setFechaHora(LocalDateTime.now());v.setIdUsuario(resumen.getIdVendedor());
  List<DetalleVenta> ds=new ArrayList<>();for(Fila f:carrito){DetalleVenta d=new DetalleVenta();d.setIdProducto(f.p.getIdProducto());d.setCantidad(f.cantidad);d.setPrecioUnitario(f.p.getPrecioVenta());ds.add(d);}
  if(ventas.registrarVenta(v,ds)){alert(Alert.AlertType.INFORMATION,"Venta registrada","La venta #"+v.getIdVenta()+" se cobró correctamente.");limpiar();}
  else alert(Alert.AlertType.ERROR,"No se pudo registrar","La venta no se guardó. Verifique el stock y la conexión con la base de datos.");
 }
 private void limpiar(){carrito.clear();tabla.refresh();total();resumen.limpiar();busqueda.clear();}
 private void total(){resumen.actualizarImportes(carrito.stream().map(Fila::subtotal).reduce(BigDecimal.ZERO,BigDecimal::add));}
 private void alert(Alert.AlertType t,String title,String msg){Alert a=new Alert(t);a.setTitle(title);a.setHeaderText(null);a.setContentText(msg);a.showAndWait();}
 private static String money(BigDecimal n){return "$ "+(n==null?"0.00":n.toPlainString());}
 static String estiloTarjeta(){return "-fx-background-color:#172131;-fx-background-radius:10;-fx-border-color:#334155;-fx-border-radius:10;";}
 private static class Fila{final Producto p;int cantidad=1;Fila(Producto p){this.p=p;}BigDecimal subtotal(){return p.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad));}}
}
