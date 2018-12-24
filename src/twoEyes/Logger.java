/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twoEyes;

import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

/**
 *
 * @author uzivatel
 */
public class Logger {
    
    public enum SOURCE {UDPSERVER,NETWORK};
    private static final long startTime=System.currentTimeMillis();
    
    private final Pane pane;
    private static TextArea textArea;
     
    public Logger() {
        this.pane=new Pane();
        textArea = new TextArea();
        textArea.setMaxHeight(40);
        textArea.setMinWidth(800);
        this.pane.getChildren().add(textArea);
    } 
     
    public Pane asPane() {
        return pane ;
    }     
    
    
    public static void log(int level,SOURCE source,String text) {
        long now=(System.currentTimeMillis()-startTime);
        textArea.appendText(String.valueOf(now)+"\t"+String.valueOf(level)+"\t"+source.name()+"\t"+text+"\n");
    }
    
}
