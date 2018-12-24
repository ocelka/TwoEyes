/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twoEyes;

import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/**
 *
 * @author uzivatel
 */
public class Dashboard implements UDPServerReceiver {

    private final Pane pane ;
    private final UDPImage udpImage;
    
    public Dashboard(int num1, int num2, int answer) {
        Label lblNum1 = new Label(Integer.toString(num1));
        Label lblNum2 = new Label(Integer.toString(num2));
        Label lblAnswer = new Label(Integer.toString(answer));

        HBox hboxCtrl = new HBox();
        hboxCtrl.getChildren().addAll(lblNum1, lblNum2, lblAnswer); 
        Pane ctrl=new Pane();
        ctrl.getChildren().add(hboxCtrl);
        
        ScrollBar zoomer = new ScrollBar();
        zoomer.setPrefHeight(30);
        
        this.udpImage=new UDPImage();
        
        
        VBox vbox = new VBox();     
        vbox.getChildren().addAll(ctrl,zoomer,udpImage);

        this.pane = new Pane();         
        this.pane.getChildren().add(vbox);
    }
    
    @Override
    public void setImageBytes(byte[] b) {
        udpImage.setImageBytes(b);
    }

    public Pane asPane() {
        return pane ;
    }

    
}
