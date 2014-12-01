package BusinessLogic;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;


/**
 * Manager for incoming service requests from <code>VehicleComputer</code>
 * clients. This class captures the UDP request and instantiates a new instance
 * of of the <code>UDPDatagramHandler</code> class. This class is then passed
 * the request for service and will service the client in a separate thread.
 * <p>
 * NOTE that this class does not check the incoming UDP datagrams for proper
 * request syntax as dictated by a custom protocol. Instead, this is left for
 * the designated handler of the request.
 * <p>
 * Furthermore, this class contains the main method for starting this part of the
 * business logic back-end.
 * <p>
 * @author Andreas Stensig Jensen, on Nov 7, 2014
 * Contributors:
 */
public class UDPTrafficManager {

    /**
     * Size for a <code>byte</code> buffer tested to hold 800 passengers in a
     * serialized <code>PassengerList</code>. Test showed a size of 8207 bytes.
     * Actual size is 8,300 for good measure.
     */
    public static final int BUFFER_IN_SIZE = 8300;

    /**
     * Size for a <code>byte</code> buffer tested to hold 800 tickets in a
     * serialized <code>TicketList</code>. Test showed a size of 21,833 bytes.
     * Actual size is 21,900 for good measure.
     */
    public static final int BUFFER_OUT_SIZE = 22000;

    private DatagramSocket socket;
    private String rmiHost, rmiJournayManagerName;
    private int rmiPort;
    private final int localPort = 2408;
    private int udpHandlerPort = 2409;

    /**
     * Open the UDP <code>DatagramSocket</code> on a specified port.
     */
    private void openUDPSocket() {
        try {
            socket = new DatagramSocket(localPort);  
        } catch (NumberFormatException | SocketException ex) {
            System.err.println("Fatal error in UPDTrafficManager.");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Assigns the RMI properties to field variables. This method MUST be called
     * prior to the <code>distributeDatagram</code> method.
     * <p>
     * @param RMIHost           host name of the RMI registry location.
     * @param RMIPort           port number of the RMI registry location.
     * @param RMIJouenryManName name of the <code>JourneyManager</code>
     *                          implementation class in the RMI registry.
     */
    private void setRMIpropperties(String RMIHost, String RMIPort, String RMIJouenryManName) {
        try {
            rmiHost = RMIHost;
            rmiPort = Integer.parseInt(RMIPort);
            rmiJournayManagerName = RMIJouenryManName;
        } catch (NumberFormatException ex) {
            System.err.println("Fatal error in UPDTrafficManager.");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Distributes a <code>DatagramPacket</code> to a new handler thread.
     * The new handler will then take over communication with the client through
     * its own <code>DatagramSocker</code>.
     * <p>
     * The <code>setRMIPropperties</code> method MUST be called prior to this
     * method.
     * <p>
     * @param packet the <code>DatagramPacket</code> received from a client that
     *               wishes to be serviced.
     */
    private void distributeDatagram(DatagramPacket packet) {
        /*
         Do not offer port numbers greater than 2,409 + 2,000. With an arbitrary
         restiction of no more than 1,000 vehicle computers per TrafficManager, 
         we can expect to be able to reuse port numbers after an extra 1,000 
         port numbers.
         */
        if (udpHandlerPort >= 4409) {
            udpHandlerPort = 2409;
        }

        /*
         Instantiate new handler for the DatagramPacket, start handler thread, 
         and increment UDP socket port counter.
         */
        try {
            UDPDatagramHandler handler = 
                   new UDPDatagramHandler(packet, udpHandlerPort, rmiHost,
                                           rmiPort, rmiJournayManagerName);
            handler.start();
            ++udpHandlerPort;
        } catch (SocketException | NotBoundException |RemoteException ex) {
            System.err.println("-- UDPTRafficManager --");
            System.err.println("Handler exception; datagram dropped.");
            ex.printStackTrace();
            System.err.println("Continue listening for new datagram.");
        }
    }

    /**
     * Main method for starting <code>UDPTrafficManger</code>,
     * <code>UDPDatagramHandler</code>, and <code>JourneyManager</code>.
     * <p>
     * @param args
     * <ul>
     * <li>0 : host name for the RMI registry server.
     * <li>1 : port number for the RMI registry server.
     * <li>2 : name of the <code>JourneyManager</code> implementation class in
     * the RMI registry.
     * </ul>
     * <p>
     */
    public static void main(String[] args) {
        UDPTrafficManager manager = new UDPTrafficManager();
        manager.openUDPSocket();
        manager.setRMIpropperties(args[0], args[1], args[2]);

        DatagramPacket packet;
        while (true) {
            /*Wait for new DatagramPacket and distribute it to a new handler.*/
            try {
                System.out.println("TM: Waiting for packet. . . ");
                packet = new DatagramPacket(new byte[BUFFER_IN_SIZE],
                                            BUFFER_IN_SIZE);
                manager.socket.receive(packet);
                System.out.println("TM: Packet received from " 
                        + packet.getSocketAddress());
                manager.distributeDatagram(packet);
            } catch (IOException ex) {
                System.err.println("-- UDPTrafficManager --");
                System.err.println("I/O exception; datagram dropped.");
                System.err.println("Continue listening for next datagram. ");
            }
        }
    }

}
