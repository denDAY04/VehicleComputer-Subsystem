package VehicleServer;

import BuisnessLogic.Ticket;
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
 * Servicing thread class for <code>PDAApplication</code> objects that requests
 * their ticket from the <code>VehicleComputer</code> <code>TicketList</code>
 * field.
 * 
 * @author Andreas Stensig Jensen, on Nov 17, 2014
 * Contributors: 
 */
public class UDPDownlinkHandler extends Thread {

    private final int localPort = 2225;
    private final DatagramSocket socket;
    private final VehicleComputer parent;
    
    
    /**
     * Constructor.
     * @param parent the <code>VehicleComputer</code> that owns this object. 
     * @throws SocketException if the UDP socket could not be opened.
     */
    public UDPDownlinkHandler(VehicleComputer parent) throws SocketException {
        socket = new DatagramSocket(localPort);
        this.parent = parent;
    }
    
    /**
     * Always listen for new <code>DatagramPackets</code>. Service them by 
     * getting the requested ticket from the <code>parent</code>, if there 
     * are any. 
     */
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
                
                // Create reply and send it
                byte[] bufferOut;
                if (ticket != null) {
                    // Serialize ticket
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(ticket);
                    bufferOut = bos.toByteArray();
                } else {
                    // Send datagram with only 1, empty byte 
                    bufferOut = new byte[1];
                }
                // Send reply
                InetAddress replyAddr = packetIn.getAddress();
                int replyPort = packetIn.getPort();
                DatagramPacket packetOut = new DatagramPacket(bufferOut, bufferOut.length, replyAddr,replyPort);
                socket.send(packetOut);
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
