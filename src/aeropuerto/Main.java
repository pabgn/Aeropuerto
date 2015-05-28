/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aeropuerto;

import aeropuerto.controller.Aeropuerto;
import aeropuerto.controller.Controlador;
import aeropuerto.controller.Gestion;
import application.data.Data;
import java.io.IOException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 *
 * @author pablo
 */
public class Main extends Application {
    public Data data;
    @Override
    public void start(Stage primaryStage) {
            data = Data.getInstance();
            Gestion g=(Gestion) abrirVentana("Gestion", "Gestión aeropuerto");
            g.ready();
                  
    }
    
    public Controlador abrirVentana(String viewName, String title){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/aeropuerto/view/"+viewName+".fxml"));
        Parent root = null;
        Stage stage = new Stage();
        try {
            root = (Parent) loader.load();
        } catch (IOException ex) {}
        Controlador controller = loader.getController();
        controller.app=this;
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(title);
        stage.show();
        controller.stage=stage;
        return controller;
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
