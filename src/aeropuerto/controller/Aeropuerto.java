package aeropuerto.controller;

import application.model.Airport;
import application.model.AirportFlights;
import application.model.Flight;
import application.model.Flight.FlightDomain;
import application.model.Flight.FlightType;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
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
    @FXML
    private CategoryAxis vuelosPorHoraCategorias;
    @FXML
    private LineChart vuelosPorHora;
    @FXML
    private DatePicker date;
    
    List<String> compañias_s;
    public void ready(){
     //Lista de horas para la gráfica
        List<String> horas = new ArrayList<>();
        for(int h=0;h<=23;h++){
            horas.add(h+"h");
        }
        ObservableList<String> horas_o = FXCollections.observableArrayList(horas);

     //Cargamos los vuelos
        
     //Salidas
     try{
     List<Flight> flights = new ArrayList<>(this.app.data.getAirportFlights(aeropuerto, date.getValue()).getDepartures().getFlights().values());
     ObservableList<Flight> flights_o = (ObservableList<Flight>)FXCollections.observableArrayList(flights);  
        tablaVuelosSalidas.setItems(flights_o);
        //Obtenemos la lista de compañías de salidas y el numero de vuelos asociados
     int max=3;
     compañias_s = new ArrayList<>();
     int[] flightsCounter = new int[max];
     int[] flightsCounter2 = new int[horas.size()]; //Contador para el numero de vuelos por hora nacionales
     int[] flightsCounter3 = new int[horas.size()]; //Contador para el numero de vuelos por hora internacionales
     
     for(Flight f:flights){
         if(compañias_s.size()<max){
         if(!compañias_s.contains(f.getCompany())){
             compañias_s.add(f.getCompany());
         }
         }
         try{ flightsCounter[compañias_s.indexOf(f.getCompany())]++;} catch(Exception e){}
         if(f.getFlightDomain()==FlightDomain.INTERNATIONAL){flightsCounter2[f.getDateTime().getHour()]++;}
         if(f.getFlightDomain()==FlightDomain.NATIONAL){flightsCounter3[f.getDateTime().getHour()]++;}

     }
     ObservableList<String> compañias_s_o = FXCollections.observableArrayList(compañias_s);
     vuelosPorCompañiaCategorias.setCategories(compañias_s_o);
     XYChart.Series<String, Integer> series = createDataSeries(flightsCounter, compañias_s);
     if(vuelosPorCompañia.getData().size()>0){
        vuelosPorCompañia.getData().remove(0);
     }
     vuelosPorCompañia.getData().add(series);
     vuelosPorCompañia.setLegendVisible(false);
     
     //Vuelos por horas
     vuelosPorHoraCategorias.setCategories(horas_o);
     XYChart.Series<String, Integer> series2 = createDataSeries(flightsCounter2, horas);
     XYChart.Series<String, Integer> series3 = createDataSeries(flightsCounter3, horas);

     vuelosPorHora.getData().add(series2);
     vuelosPorHora.getData().add(series3);
     
     }catch(Exception e){
        tablaVuelosSalidas.setItems(null);
        
        
     }
     //Llegadas
     try{
     List<Flight> flights_l = new ArrayList<>(this.app.data.getAirportFlights(aeropuerto, date.getValue()).getArrivals().getFlights().values());
     ObservableList<Flight> flights_o_l = (ObservableList<Flight>)FXCollections.observableArrayList(flights_l);  
     tablaVuelosLlegadas.setItems(flights_o_l); 
     }catch(Exception e){
         tablaVuelosLlegadas.setItems(null);
     }
     
     
     
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
    private void onDateChanged(Event e){
        ready();
    }
    @FXML
    private void initialize() {
         //date.setValue(LocalDate.now());
        date.setValue(LocalDate.of(2015, 3, 9));

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