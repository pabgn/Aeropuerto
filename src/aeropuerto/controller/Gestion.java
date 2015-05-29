/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aeropuerto.controller;

import aeropuerto.Main;
import application.data.Data;
import application.model.Airport;
import application.model.AirportFlights;
import application.model.Flight;
import application.model.FlightsHistory;
import javafx.scene.input.MouseEvent;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 *
 * @author toespar
 */
public class Gestion extends Controlador {
    @FXML
    private TableView tablaAeropuertos;
    @FXML
    private PieChart topAeropuertos;
    @FXML
    private LineChart vuelosPorDia;
    @FXML
    private TableColumn<AirportFlights, String> aeropuertosTabla;
    @FXML
    private TableColumn<AirportFlights, String> codigo;
    @FXML
    private TableColumn<AirportFlights, String> vuelosTotales;
    @FXML
    private TableColumn<AirportFlights, String> nacional;
    @FXML
    private TableColumn<AirportFlights, String> internacional;
    @FXML
    private TableColumn<AirportFlights, String> vuelosDia;
    @FXML
    private TableColumn<AirportFlights, String> retrasoMedio;
    @FXML
    private CategoryAxis vuelosPorDiaCategoria;
    @FXML
    private Slider slider;
    
    private Tooltip tooltip;
    List<String> dias_s;
    DecimalFormat df = new DecimalFormat("#.##");
    ObservableList<PieChart.Data> pieChartData;
    int sliderVal=6;
    public void ready(){
        //Guardamos los dias en los que hay vuelos
        List<LocalDate> dias = this.app.data.getDates();
        dias_s = new ArrayList<>();
        for(LocalDate d:dias){
            dias_s.add(d.toString());
        }
        ObservableList<String> dias_s_o = (ObservableList<String>) FXCollections.observableList(dias_s);
        
        //Cargamos los aeropuertos
        List<Airport> aeropuertos = this.app.data.getAirportList();
        List<AirportFlights> infoAeropuerto = new ArrayList<>();
        int[] vuelosNacionales = new int[dias.size()];
        int[] vuelosInternacionales = new int[dias.size()];
        int[] vuelosTotales = new int[dias.size()];
        
        //Obtenemos info de cada aeropuertos y los vuelos nacionales e internacionales totales por día
        
        for(Airport a:aeropuertos){
            infoAeropuerto.add(this.app.data.getAirportFlights(a));
            int i=0;
            for(LocalDate l:dias){
                AirportFlights t = this.app.data.getAirportFlights(a, l);
                try{
                    if(t != null){
                        vuelosTotales[i] += t.getNumFlights();
                        vuelosNacionales[i] += t.getNumNationalFlights();
                        vuelosInternacionales[i] += t.getNumInternationalFlights();
                    }
                    
                } catch(Exception e){
                }
                i++;
            }
        }
        ObservableList<AirportFlights> infoAeropuerto_o = (ObservableList<AirportFlights>) FXCollections.observableList(infoAeropuerto);
        
        tablaAeropuertos.setItems(infoAeropuerto_o);
        
      
        
        //Vuelos nacionales e internacionales totales
        vuelosPorDiaCategoria.setCategories(dias_s_o);
        XYChart.Series<String, Integer> series2 = createDataSeries(vuelosNacionales, dias_s);
        series2.setName("Nacionales");
        XYChart.Series<String, Integer> series3 = createDataSeries(vuelosInternacionales, dias_s);
        series3.setName("Internacionales");
        XYChart.Series<String, Integer> series4 = createDataSeries(vuelosTotales, dias_s);
        series4.setName("Totales");
        vuelosPorDia.getData().add(series2);
        vuelosPorDia.getData().add(series3);
        vuelosPorDia.getData().add(series4);
        
        //Obtenemos topAeropuertos 
        List<Airport> topA = new ArrayList<Airport>();
        List<Airport> a = aeropuertos;
        while(!a.isEmpty()){
            Airport m = a.get(0);
            for(int i=0; i<a.size(); i++){
                int x = this.app.data.getAirportFlights(m).getNumFlights();
                int y = this.app.data.getAirportFlights(a.get(i)).getNumFlights();
                if(x-y<=0) m= a.get(i);
            }
            topA.add(m);
            a.remove(m);
        }
        
        //Pintamos en un pie chart los n top aeropuertos --> Por defecto n=6
        pieChartData = FXCollections.observableArrayList();
        for (int j=0; j<6; j++){
            Airport t = topA.get(j);
            pieChartData.add(new PieChart.Data(t.getName(), this.app.data.getAirportFlights(t).getNumFlights()));
        }
        topAeropuertos.setData(pieChartData);
        //Si slider se modifica, repintamos pie chart
        slider.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
            if(new_val.intValue() < sliderVal) {
                pieChartData.remove(new_val.intValue(), sliderVal);
                sliderVal=new_val.intValue();
            }
            
            if(new_val.intValue() > old_val.intValue()){
                for(int j = sliderVal; j< new_val.intValue(); j++){
                    Airport t = topA.get(j);
                    pieChartData.add(new PieChart.Data(t.getName(), this.app.data.getAirportFlights(t).getNumFlights()));
                    
                }
                sliderVal = new_val.intValue();
                for (final PieChart.Data data : topAeropuertos.getData()) {
                    Tooltip.install(data.getNode(),tooltip);
                    applyMouseEvents(data);
                }
            }
        }); 
        
        //Efecto en cada pie del PieChart topAeropuertos
        tooltip = new Tooltip("");
        tooltip.setStyle("-fx-font: 14 arial;  -fx-font-smoothing-type: lcd;");// -fx-text-fill:black; -fx-background-color: linear-gradient(#e2ecfe, #99bcfd);");
        for (final PieChart.Data data : topAeropuertos.getData()) {
            Tooltip.install(data.getNode(),tooltip);
            applyMouseEvents(data);
        }
        Main app = this.app;
        //Click en cada fila
       
        
        tablaAeropuertos.setRowFactory( tv -> {
            TableRow<AirportFlights> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    AirportFlights rowData = row.getItem();
                    Aeropuerto b = (Aeropuerto) app.abrirVentana("Aeropuerto", "Aeropuerto");
                    b.search = rowData.getAirport().getCode();
                    b.ready();
                }
            });
            return row ;
        });
    }
    
    //Método para detectar el ratón en cada pie
    private void applyMouseEvents(final PieChart.Data data) {
        final Node node = data.getNode();
        
        node.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                node.setEffect(new Glow());
                String styleString = "-fx-border-color: white; -fx-border-width: 3; -fx-border-style: dashed;";
                node.setStyle(styleString);
                tooltip.setText(data.getName());
            }
        });

        node.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent arg0) {
                node.setEffect(null);
                node.setStyle("");
            }
        });
    }
       
    private XYChart.Series<String, Integer> createDataSeries(int[] counter, List<String>y) {
        XYChart.Series<String,Integer> series = new XYChart.Series<String,Integer>();
        for (int i = 0; i < counter.length; i++) {
            if(i<=y.size()-1){
                XYChart.Data<String, Integer> data = new XYChart.Data<String,Integer>(y.get(i), counter[i]);
                series.getData().add(data);
            }
        }
        return series;
    }
    
    @FXML
    private void initialize(){
        aeropuertosTabla.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAirport().getName()));
        codigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAirport().getCode()));
        vuelosTotales.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumFlights()+""));
        nacional.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumNationalFlights()+""));
        internacional.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumInternationalFlights()+""));
        vuelosDia.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(df.format(cellData.getValue().getNumFlights()/(double)dias_s.size()))));
        retrasoMedio.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(df.format(cellData.getValue().getDelay()))));
    }
}
