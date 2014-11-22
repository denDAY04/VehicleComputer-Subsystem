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
 * Separate thread for handling received replies (pongs) from pings. This is 
 * class is meant for handing a single pong, and exists only in the span of time
 * it takes to process the pong. 
 * <p>
 * Unlike the multicast 
 * <code>UDPPingSender</code>, which exists in an entire ping-session of five 
 * successful pings, this class is singlecast and simply handles the packet 
 * given in its constructor. This allows for multithreaded servicing of pongs 
 * when many clients may respond to a multicast ping in rapid succession. 
 * 
 * @author Andreas Stensig Jensen, on Nov 10, 2014
 * Contributors: 
 */
public class UDPPongHandler implements Runnable {
    
    private final VehicleComputer parent;
    private final DatagramPacket packet;
    private final DatagramSocket socket;
    
    
    /**
     * Constructor with the pong-packet that needs to be processed.
     * @param parent the <code>VehicleComputer</code> that owns this object. 
     * @param packet <code>DatagramPacket</code> pong received from a PDA 
     * device.
     * @throws SocketException if the respond-socket cannot be opened. 
     */
    public UDPPongHandler(VehicleComputer parent, DatagramPacket packet) 
            throws SocketException {
        this.parent = parent;
        this.packet = packet;
        socket = new DatagramSocket();
    }
    
    /**
     * Main flow of the thread. It retrieves the customer number from the pong
     * datagram, stores it in the <code>parents</code> list of passengers, and 
     * sends and ack back to the sender of the pong. 
     * <p>
     * Lastly, the thread terminates. 
     */
    @Override
    public void run() {
        try {
            ByteArrayInputStream bis = 
                    new ByteArrayInputStream(packet.getData());
            ObjectInputStream ois = new ObjectInputStream(bis);
            String passenger = (String) ois.readObject();
            parent.addToPassengers(Integer.parseInt(passenger));
            
            String ack = "ack";
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(ack);
            
            byte[] buffer = bos.toByteArray();
            InetAddress addr = packet.getAddress();
            int port = packet.getPort();
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length, 
                    addr, port);
            socket.send(reply);
        } catch (IOException ex) {
            System.err.println("IO exception in reading pong; dropped.");
        } catch (ClassNotFoundException | NumberFormatException ex) {
            System.err.println("Invalid payload of pong; dropped.");
        }
    }

}
