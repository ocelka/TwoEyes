/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package twoEyes;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 *
 * @author uzivatel
 */
public class UDPServer {
    int port;
    String ip;
    UDPServerReceiver receiver;
    Thread threadUDP;
    Thread threadClock;
    long lastUDPTimestamp=0;
    
    public UDPServer(String ip,int port) {
        this.ip=ip;
        this.port=port;
        Runnable runnableUDP = () -> {
            serverUDP();   
        };     
        Runnable runnableClock = () -> {
            serverClock();   
        };     
        threadUDP=new Thread(runnableUDP);
        threadClock=new Thread(runnableClock);
        receiver=null;
    }
    
    public void start() {
        System.out.println("UPD SERVER "+port);
        threadUDP.start();
        threadClock.start();
        System.out.println("STARTED");
    }
    
    
    public void setReceiver(UDPServerReceiver receiver) {
        this.receiver=receiver;
    }   
    
    private void serverClock() {
        long lastRefreshConnection=0;
        boolean conOk=false;
        do {
            long now=System.currentTimeMillis();
            long refreshInterval=conOk?5000:1500;
            if ((now-lastRefreshConnection)>refreshInterval) {
                try {
                    lastRefreshConnection=now;
                    conOk=HTTPClient.refreshConnection(ip);
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (conOk&&(now-lastUDPTimestamp)>300) {//more then 
                try {
                    System.out.println("RESTART STREAM");
                    HTTPClient.startStream(ip, port);
                }  catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500); 
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while(true);
    }
    
    private void serverUDP()  {
        DatagramSocket serverSocket=null;
        do {
            try {
                serverSocket=new DatagramSocket(port);
            } catch (Exception ex) {
                ex.printStackTrace();
                try {Thread.sleep(1000);} catch(Exception x){}
            }
        } while(serverSocket==null);
        byte[] receiveData = new byte[50000];
        long index=0;
        long jpgCount=0;
        long indexJpgStart=0;
        long indexJpgEnd=0;
        long lastJpgTimeStamp=0;
        int packetCount=0;
        while(true) { 
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivePacket);
                long t1=System.currentTimeMillis();
                lastUDPTimestamp=System.currentTimeMillis();
                packetCount++;
                if (lastUDPTimestamp<=lastJpgTimeStamp+50) { //max 25 frame per sec 
                  //  System.out.println("IGNORE");
                    continue;  
                } 
                byte data[]= receivePacket.getData();
                InetAddress IPAddress = receivePacket.getAddress();
                int port = receivePacket.getPort();
                int recLen=receivePacket.getLength();

                int thisJpgStart=0;
                int thisJpgEnd=0;
                for (int i=0;i<recLen;i++) {
                    index++;
                    if ((data[i]==(byte)0xff) &&(i<(recLen-1) && data[i+1]==(byte)0xd8 )) {
                      indexJpgStart=index;
                      jpgCount++;
                      thisJpgStart=i;
                      lastJpgTimeStamp=System.currentTimeMillis();
                      long t2=System.currentTimeMillis();
                      System.out.println("JPEG("+jpgCount+"){"+packetCount+"} start at "+i + "<"+(t2-t1)+">");
                    }
                    if ((data[i]==(byte)0xff) &&(i<(recLen-1) && data[i+1]==(byte)0xd9 )) {
                      indexJpgEnd=index+1;
                      thisJpgEnd=i;
                      long t3=System.currentTimeMillis();
                      System.out.println("JPEG("+jpgCount+"){"+packetCount+"} end at "+i+ " length="+ ((indexJpgEnd-indexJpgStart)+2)+"<"+(t3-t1)+">");
                    }
                }  
                if (thisJpgStart>=0 &&thisJpgEnd>=0 && thisJpgEnd-thisJpgStart>100) {
                    byte b[]=new byte[thisJpgEnd+1-thisJpgStart];
                    for (int j=0;j<b.length;j++) {
                        b[j]=data[j+thisJpgStart];
                    }
                    if (receiver!=null) {
                        receiver.setImageBytes(b);
                        long t4=System.currentTimeMillis();
                        System.out.println("PRINTED "+(t4-t1));
                    }
                }           
            } catch (Exception ex) {
                    ex.printStackTrace();
            }
        }
    }   
}
