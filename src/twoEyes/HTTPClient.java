/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twoEyes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 *
 * @author uzivatel
 */
public class HTTPClient {
 
//https://github.com/peci1/lumix-link-desktop/blob/master/requests.html


    
    //https://www.journaldev.com/1194/java-xpath-example-tutorial
    //http://192.168.24.37/cam.cgi?mode=getinfo&type=curmenu
    
    public static boolean refreshConnection(String server) throws IOException {
        try {
            get(server,"mode","getinfo","type","allmenu");
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    
    public static void zoomWideNormal(String server) throws IOException {
        get(server,"camcmd","wide-normal");
    }

    public static void zoomTeleNormal(String server) throws IOException {
        get(server,"camcmd","tele-normal");
    }
    
    public static void zoomStop(String server) throws IOException {
        get(server,"camcmd","zoomstop");
    }
    
    public static void startStream(String server,int port) throws IOException {
        get(server,"mode","startstream","value",String.valueOf(port));
    }
    
    public static void get(String server, String p1,String v1) throws ProtocolException, IOException {   
        get(server,p1,v1,null,null);  
    }
    public static void get(String server, String p1,String v1, String p2, String v2) throws ProtocolException, IOException {
        StringBuilder sb=new StringBuilder();
        sb.append("http://").append(server).append("/cam.cgi?").append(p1).append('=').append(v1);
        if (v2!=null) {
            sb.append('&').append(p2).append('=').append(v2);
        }
        URL url = new URL(sb.toString());
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        try {
            con.setRequestMethod("GET"); 
            
            con.setConnectTimeout(1000);
            con.setReadTimeout(1000);
            int status = con.getResponseCode();
            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                System.out.println("OUTPUT:\n"+content.toString());
            }
        } finally {
            con.disconnect();  
        } 
    }


}
