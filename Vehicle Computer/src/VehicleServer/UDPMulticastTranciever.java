/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VehicleServer;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

/**
 *
 * @author Stensig
 */
public class UDPMulticastTranciever extends Thread {
    private final int PING_RECIEVE_TIMEOUT = 5000;        // in milliseconds
    private final int localPort = 24408;
    private final int clientPort = 24409;
    private final String multicastAddr = "239.0.1.139";
    private DatagramSocket socket = null;
    private ArrayList<String> passengers = null;
    
    @Override
    public void run() {
        try {
            socket = new DatagramSocket(localPort);
            // Set timeout on socket for ping recive. 
            socket.setSoTimeout(PING_RECIEVE_TIMEOUT);
        } catch(SocketException se) {
            System.err.println("--Fatal error in setting socket connection.");
            se.printStackTrace();
            System.exit(-1);
        }
    }
    
    public void pingPassengerDevices() throws UnknownHostException, IOException {
        byte[] bufOut = new byte[256];    
        String packetPyaload = "ping";
        
        // Send pulticast ping
        bufOut = packetPyaload.getBytes();
        InetAddress group = InetAddress.getByName(multicastAddr);
        DatagramPacket packet = new DatagramPacket(bufOut, bufOut.length, group, clientPort);
        socket.send(packet);
        // Gather replies from the multicast ping
        collectPingReplies();
    }
    
    private void collectPingReplies() {
        byte[] bufIn = new byte[256];
        DatagramPacket packetIn = new DatagramPacket(bufIn, bufIn.length);
        String replyPayload;
        
        try {
            while (true) {
                socket.receive(packetIn);
                replyPayload = new String(packetIn.getData(), 0, packetIn.getLength());
                passengers.add(replyPayload);
            }
        } catch(SocketTimeoutException te) {
            // Escape sequence. No more ping replies from passangers.
            // Five seconds since last reply. 
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public ArrayList<String> getPassangerDeviceNumbers() {
        return passengers;
    }
}
