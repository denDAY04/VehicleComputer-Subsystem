package VehicleServer;

import BuisnessLogic.Ticket;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


/**
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on Nov 17, 2014
 * Contributors: 
 */
public class UDPDownlinkHandler extends Thread {

    private final int localPort = 2225;
    private final DatagramSocket socket;
    private final VehicleComputer parent;
    
    
    public UDPDownlinkHandler(VehicleComputer parent) throws NumberFormatException, SocketException {
        socket = new DatagramSocket(localPort);
        this.parent = parent;
    }
    
    @Override
    public void run() {
        DatagramPacket packetIn = new DatagramPacket(new byte[256], 256);
        while (true) {
            try {
                // Recieve packet and decode data
                socket.receive(packetIn);
                ByteArrayInputStream bis = new ByteArrayInputStream(packetIn.getData());
                ObjectInputStream ois = new ObjectInputStream(bis);
                String customerNum = (String) ois.readObject();
                
                // Get ticket, if any
                int cusNum = Integer.parseInt(customerNum);
                Ticket ticket = findTicket(cusNum);
                
                // Create reply
                
            } catch (IOException ex) {
                System.err.println("IO exception; could not recieve datagram."
                        + "\nDatagram dropped.");
            } catch (ClassNotFoundException ex) {
                System.err.println("Class exception; could not convert data."
                        + "\nDatagram dropped.");
            }
        }
    }
    
    /**
     * Search the <code>parent</code>'s list of tickets for a 
     * <code>Ticket</code> with the supplied customer number. 
     * @param customerNumber to search for on the tickets. 
     * @return the ticket if such is found, or NULL otherwise. 
     */
    private Ticket findTicket(int customerNumber) {
        return parent.getTickets().getTicket(customerNumber);
    }
    
}
