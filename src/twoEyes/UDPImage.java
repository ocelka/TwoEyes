/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twoEyes;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.imageio.ImageIO;

/**
 *
 * @author uzivatel
 */
public class UDPImage extends ImageView implements UDPServerReceiver {
    
    public UDPImage() {
        super();
        setFitHeight(360);
        setFitWidth(640);
    }
    
    @Override
    public void setImageBytes(byte data[]) {
        try {
            Image i=new Image(new ByteArrayInputStream(data));
            setImage(i);                     
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }
}
