/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package musicplayerapp;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author brike
 */
public class MusicPlayerApp extends Application {

    /**
     * @param args the command line arguments
     */
        public static void main(String[] args) {
        launch(args);
    } 
    
     @Override
    public void start(Stage stage) {
        Parent root = null;
        try
        {
            root = FXMLLoader.load(getClass().getResource("MusicPlayerFXML.fxml"));
        }
        catch(Exception e)
        {
            System.out.println("Could not load fxml" +e.getMessage());
            e.printStackTrace();
            return;
        }
        Scene scene = new Scene(root, 300, 275);
    
        stage.setTitle("Music Player");
        stage.setScene(scene);
        stage.show();
    }

    
}
