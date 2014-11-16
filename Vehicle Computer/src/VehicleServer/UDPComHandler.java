package VehicleServer;

import BuisnessLogic.PassengerList;
import BuisnessLogic.TicketList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javax.swing.Timer;


/**
 * Traffic handler for UDP communication from vehicle to business logic backend.
 * 
 * @author Andreas Stensig Jensen, on 16-11-2014
 * Contributors: 
 */
public class UDPComHandler {

    private final int TIMEOUT_DELAY = (1000 * 4);       // 4 seconds
    
    private final int localPort = 2226;
    private final int targetPort;
    private final String targetHost;
    private final DatagramSocket socket;
    private DatagramPacket packetOut;
    private Timer timer;
    
    public UDPComHandler(String targetedPort, String targetedHost) 
            throws NumberFormatException, UnknownHostException, SocketException {
        targetPort = Integer.parseInt(targetedPort);
        targetHost = targetedHost;
        InetAddress host = InetAddress.getLocalHost();
        socket = new DatagramSocket(localPort, host);
        timer = new Timer(TIMEOUT_DELAY, new TimeoutListener());
    }
    
    public TicketList getTicketList(PassengerList passengers) throws IOException, ClassNotFoundException {
        // Serialize passenger list and send the request
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(passengers);
        byte[] buffOut = bos.toByteArray();
        InetAddress addr = InetAddress.getByName(targetHost);
        packetOut = new DatagramPacket(buffOut, buffOut.length, 
                addr, localPort);
        sendDatagram();
        
        // Get reply deserialize it and return it.
        byte[] buffIn = new byte[21900]; // Test with 800 Tickets = 21,833 B
        DatagramPacket packetIn = new DatagramPacket(buffIn, buffIn.length);
        socket.receive(packetIn);
        ByteArrayInputStream bis = new ByteArrayInputStream(packetIn.getData());
        ObjectInputStream ois = new ObjectInputStream(bis);
        TicketList tickets = (TicketList) ois.readObject();
        return tickets;
    }
    
    private void sendDatagram() {
        try {
            socket.send(packetOut);
        } catch (IOException ex) {
            System.err.println("I/O exception in sending reply. ");
            try {
                this.wait(10);
            } catch (InterruptedException ex1) {
                // Do nothing
            }
            try {
                // Retry
                socket.send(packetOut);
            } catch (IOException ex1) {
                System.err.println("I/O exeption #2 in sending reply. ");
                System.err.println("Dropping reply.");
                return;
            }
        }
    }
    
    class TimeoutListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            
        }
        
    }
}
