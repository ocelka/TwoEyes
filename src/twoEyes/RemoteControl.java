/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twoEyes;

import java.util.HashMap;
import java.util.HashSet;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;

/**
 *
 * @author uzivatel
 */
public class RemoteControl extends Application implements NewCamcorderReceiver {
        /**
        * @param args the command line arguments
        */
    
        static int portSeq=50012;
        
        HashMap<String,UDPServer> cams=new HashMap<String,UDPServer>();
        
        Network network=new Network();
        
   //     UDPServer cam1=new UDPServer("192.168.24.36",50000);
//        UDPServer cam2=new UDPServer("192.168.24.37",50001);
        public static void main(String[] args) {
           Application.launch(args);
        }
        

        Pane paneDashboards=null;
        Pane mainVertical=null;
        
        @Override
        public void start(Stage primaryStage) {
            primaryStage.setTitle("Two Eyes - Panasonic camcorders remote control");
            mainVertical=new VBox(10);
            
            paneDashboards = new HBox(10);
            paneDashboards.setPadding(new Insets(5,5,5,5));
            paneDashboards.setMinHeight(500);

            Logger logger=new Logger();
            
            mainVertical.getChildren().addAll(paneDashboards,logger.asPane());
            Scene scene = new Scene(mainVertical);
      
            
            primaryStage.setScene(scene);
            primaryStage.setMinHeight(900);
            primaryStage.setMinWidth(1800);
            primaryStage.show();  
            primaryStage.setOnCloseRequest(event -> {   
                System.exit(0);
            });
            
       //     cam1.start();
      //      cam2.start();
            network.setNewCamcorderReceiver(this);
            network.start();
        }
      
        public boolean addCamcorder(String ip) {
            Platform.runLater(new Runnable() {
                 @Override public void run() {
                    UDPServer cam=new UDPServer(ip, portSeq++);
                    Dashboard dashboard=new Dashboard(999,2,3);
                    paneDashboards.getChildren().add(dashboard.asPane());
                    Logger.log(1,Logger.SOURCE.NETWORK,"new camcorder added");
                    cam.setReceiver(dashboard);
                    cam.start();
                 }
                 });
            return true;
        }
    
}
