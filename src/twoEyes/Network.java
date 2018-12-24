/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twoEyes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author uzivatel
 */
public class Network {
    
    Thread discoverThread;    
    HashSet<String> ips;
    NewCamcorderReceiver cam=null;
    
    public Network() {
        Runnable runnable = () -> {
            discover();   
        };     
        discoverThread=new Thread(runnable);        
        ips=new HashSet<String>();
    }
    
    public void start() {
        Logger.log(1,Logger.SOURCE.NETWORK,"DISCOVER NETWORK SERVER STARTING");
        discoverThread.start();
    }
    
    public void setNewCamcorderReceiver(NewCamcorderReceiver cam) {
       this.cam=cam;  
    }
    
    private void discover() {
        do {
            try {
                Set<String> ipsNew=findPanasonic();
                for (String ip:ipsNew) {
                   if (!ips.contains(ip)) {
                       if (cam!=null) {
                           if (cam.addCamcorder(ip)) {
                               ips.add(ip);
                           }
                       }
                   } 
                }
            } catch (Exception e) {
                e.printStackTrace();
            }                       
            try {
                Thread.sleep(2000); 
            } catch (Exception e) {
                e.printStackTrace();
            }           
        }  while (true);
    }
    
    public static Set<String> findPanasonic() throws SocketException, IOException {
        /* create byte arrays to hold our send and response data */
        byte[] sendData;
        byte[] receiveData = new byte[2048];

        /* our M-SEARCH data as a byte array */
//        String MSEARCH = "M-SEARCH * HTTP/1.1\nHost: 239.255.255.250:1900\nMan: \"ssdp:discover\"\nST: roku:ecp\n"; 
    //    String MSEARCH = "M-SEARCH * HTTP/1.1\r\nMX: 5\nHost: 239.255.255.250:1900\r\nMan: \"ssdp:discover\"\r\nST: urn:dial-multiscreen-org:service:dial:1\r\n\r\n";
//        String MSEARCH = "M-SEARCH * HTTP/1.1\nHost: 239.255.255.250:1900\nMan: \"ssdp:discover\"\nST: ssdp:all\n";
String MSEARCH ="M-SEARCH * HTTP/1.1\r\nHOST:255.255.255.255:1900\r\nMAN:\"ssdp:discover\"\r\nST:ssdp:all\r\nMX:1\r\n\r\n" ; 
sendData = MSEARCH.getBytes();

        /* create a packet from our data destined for 239.255.255.250:1900 */
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("239.255.255.250"), 1900);

        /* send packet to the socket we're creating */
        DatagramSocket clientSocket = new DatagramSocket();
        clientSocket.setSoTimeout(900);
        clientSocket.send(sendPacket);

        HashSet<String> ips=new HashSet<String>();
        while (true) {
            try {
                /* recieve response and store in our receivePacket */
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                clientSocket.receive(receivePacket);

                /* get the response as a string */
                String response = new String(receivePacket.getData());
                if (response.contains("Panasonic")&&response.contains("LOCATION")) {
                    final String prefix="LOCATION: http://";
                    final String suffix=":";
                    int i=response.indexOf(prefix);
                    int from=i+prefix.length();
                    int j=response.indexOf(suffix, from);
                    String ip=response.substring(from, j);
                    ips.add(ip);
                }

                /* print the response */
              //  System.out.println(response);
            }
            catch (SocketTimeoutException e) {  break; }
        }


        /* close the socket */
        clientSocket.close();
        return ips;
    }
    
}
