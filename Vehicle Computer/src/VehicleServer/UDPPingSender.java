package VehicleServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * External thread for sending pings on UDP multicast to PDA devices on-board
 * the vehicle.
 * <p>
 * @author Andreas Stensig Jensen, on Nov 18, 2014
 * Contributors:
 */
public class UDPPingSender extends Thread {

    private final int INTERVAL_MS = 1000;
    private final DatagramSocket socket;
    private final int localPort = 2222;
    private final int targetPort = 2224;
    private final String multicastAddr = "239.0.1.139";
    private final VehicleComputer parent;


    /**
     * Constructor. Sets the reference for the <code>VehicleComputer</code> that
     * owns this object, and opens its socket.
     * <p>
     * @param parent
     * <p>
     * @throws SocketException
     */
    public UDPPingSender(VehicleComputer parent) throws SocketException {
        this.parent = parent;
        socket = new DatagramSocket(localPort);
    }

    /**
     * Main flow of the thread. Sends pings on the multicast address specified
     * in the <code>multicastAddr</code> field, with the interval specified in
     * the <code>INTERVAL_MS</CODE> field. It will run until either five
     * successful pings have been transmitted, or three pings have been dropped.
     * In the case of the latter, it will call the
     * <code>systemRestartWarning</code> method in the parent.
     * <p>
     * When five pings have passed the parent will be used to getting tickets
     * for the passengers that have replied to the pings, after which this
     * thread terminates.
     */
    @Override
    public void run() {
        int drops = 0;
        int passes = 0;

        while (passes != 5) {
            try {
                ping();
                ++passes;
            } catch (IOException ex) {
                System.err.println("Ping unsuccessful and dropped.");
                if (++drops == 3) {
                    parent.systemRestartWarning(ex);
                    return;
                }
            }
            try {
                /*
                 Sleep for an interval, allowing clients to reply, before 
                 logging the replies in parent. 
                 */
                Thread.sleep(INTERVAL_MS);
                parent.filterPassengers();
            } catch (InterruptedException ex) {
            }
        }
        System.out.println("Five pings passed. Requesting tickets");
        parent.requestTickets();
        System.out.println(" Sender terminating.");
        socket.close();
    }

    /**
     * Serialize ping message and send it as multicast on the socket.
     * <p>
     * @throws IOException if an I/O error occurred in either the serialization
     *                     or the sending of the ping.S
     */
    private void ping() throws IOException {
        String msg = "ping";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);

        byte[] buffer = bos.toByteArray();
        InetAddress addr = InetAddress.getByName(multicastAddr);
        DatagramPacket packetOut = new DatagramPacket(buffer, buffer.length,
                                                      addr, targetPort);
        System.out.println("Sending ping");
        socket.send(packetOut);
    }

}
