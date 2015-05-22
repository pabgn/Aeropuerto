package aeropuerto.controller;

import application.model.Airport;
import application.model.AirportFlights;
import application.model.Flight;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class Aeropuerto extends Controlador {
    
    public Airport aeropuerto;
    private Flight vuelos_llegadas;
    private Flight vuelos_salidas;    
    @FXML
    private TableView tablaVuelosSalidas;
    @FXML
    private TableView tablaVuelosLlegadas;
    
    @FXML
    private TableColumn<Flight, String> s_nvuelo;
    @FXML
    private TableColumn<Flight, String> s_destino;
    @FXML
    private TableColumn<Flight, String> s_hora;
    @FXML
    private TableColumn<Flight, String> s_compañia;
    @FXML
    private TableColumn<Flight, String> s_estado; 

     @FXML
    private TableColumn<Flight, String> l_nvuelo;
    @FXML
    private TableColumn<Flight, String> l_destino;
    @FXML
    private TableColumn<Flight, String> l_hora;
    @FXML
    private TableColumn<Flight, String> l_compañia;
    @FXML
    private TableColumn<Flight, String> l_estado; 
    
    @FXML
    private BarChart vuelosPorCompañia;
    @FXML
    private CategoryAxis vuelosPorCompañiaCategorias;
    
    List<String> compañias_s;
    public void ready(){
     //Cargamos los vuelos
        
        //Salidas
     List<Flight> flights = new ArrayList<>(this.app.data.getAirportFlights(aeropuerto).getDepartures().getFlights().values());
     ObservableList<Flight> flights_o = (ObservableList<Flight>)FXCollections.observableArrayList(flights);  
     tablaVuelosSalidas.setItems(flights_o);
     
     //Llegadas
     List<Flight> flights_l = new ArrayList<>(this.app.data.getAirportFlights(aeropuerto).getArrivals().getFlights().values());
     ObservableList<Flight> flights_o_l = (ObservableList<Flight>)FXCollections.observableArrayList(flights_l);  
     tablaVuelosLlegadas.setItems(flights_o_l); 
     
     //Obtenemos la lista de compañías de salidas y el numero de vuelos asociados
     compañias_s = new ArrayList<>();
     int[] flightsCounter = new int[12];
     for(Flight f:flights){
         if(!compañias_s.contains(f.getCompany())){
             compañias_s.add(f.getCompany());
         }
         flightsCounter[compañias_s.indexOf(f.getCompany())]++;
     }
     ObservableList<String> compañias_s_o = FXCollections.observableArrayList(compañias_s);
     vuelosPorCompañiaCategorias.setCategories(compañias_s_o);
     XYChart.Series<String, Integer> series = createDataSeries(flightsCounter, compañias_s);
     vuelosPorCompañia.getData().add(series);
     vuelosPorCompañia.setLegendVisible(false);
    }
    
    
    private XYChart.Series<String, Integer> createDataSeries(int[] counter, List<String>y) {
        XYChart.Series<String,Integer> series = new XYChart.Series<String,Integer>();
        for (int i = 0; i < counter.length; i++) {
            if(i<=y.size()-1){
                XYChart.Data<String, Integer> monthData = new XYChart.Data<String,Integer>(y.get(i), counter[i]);
                series.getData().add(monthData);
            }
        }

        return series;
    }
    @FXML
    private void initialize() {
        //Configuramos cómo se renderizan las tablas y los gráficos
         s_nvuelo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlightNumber()));
         s_destino.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDestiny()));
         s_hora.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime().toString()));
         s_estado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlightStatus().getStatus().getMessage()));
         s_compañia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCompany()));

         l_nvuelo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlightNumber()));
         l_destino.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDestiny()));
         l_hora.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime().toString()));
         l_estado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFlightStatus().getStatus().getMessage()));
         l_compañia.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCompany()));
    }
}