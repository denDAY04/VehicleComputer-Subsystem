package VehicleServer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on Nov 18, 2014
 * Contributors: 
 */
public class UDPPongHandler implements Runnable {
    
    private final VehicleComputer parent;
    private final DatagramPacket packet;
    private final DatagramSocket socket;
    
    
    public UDPPongHandler(VehicleComputer parent, DatagramPacket packet) throws SocketException {
        this.parent = parent;
        this.packet = packet;
        socket = new DatagramSocket();
    }
    
    @Override
    public void run() {
        try {
            // Deserialize reply and store it in VehicleComputer
            ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
            ObjectInputStream ois = new ObjectInputStream(bis);
            String passenger = (String) ois.readObject();
            parent.addToPassengers(Integer.parseInt(passenger));
            
            // Send ack back
            String ack = "ack";
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(ack);
            byte[] buffer = bos.toByteArray();
            InetAddress addr = packet.getAddress();
            int port = packet.getPort();
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length, addr, port);
            socket.send(reply);
        } catch (IOException ex) {
            System.err.println("IO exception in reading pong; dropped.");
        } catch (ClassNotFoundException | NumberFormatException ex) {
            System.err.println("Invalid payload of pong; dropped.");
        }
    }

}
