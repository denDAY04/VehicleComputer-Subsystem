package VehicleServer;

import BusinessLogic.PassengerList;
import BusinessLogic.TicketList;
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
import java.util.Arrays;
import javax.swing.Timer;


/**
 * Handler for UDP communication from vehicle to business logic backend. It
 * maintains information about the static port and address of an associated
 * <code>UDPTrafficManager</code>, and then has field variables for the
 * port and address that are dynamically assigned to it by the
 * <code>UDPTrafficManager</code> when it is given a separate handler thread on
 * the business logic backend.
 * <p>
 * @author Andreas Stensig Jensen, on 16-11-2014
 * Contributors:
 */
public class UDPUplinkHandler {

    /**
     * Test with serialized <code>TicketList<code> with 800 tickets was 21,833
     */
    private final int MAX_IN_BUFFER_SIZE = 21900;
    private final int SEQ_NUM_INDEX = 0;
    private final int TIMEOUT_DELAY_MS = (1000 * 4);

    private final VehicleComputer parent;
    private final int localPort;
    private final int trafficManPort;                   // Const port
    private final InetAddress trafficManAddr;           // Const addr 
    private int handlerPort;                            // Dynamic port
    private InetAddress handlerAddr;                    // Dynamic addr
    private final DatagramSocket socket;
    private DatagramPacket packetOut;
    private final Timer timer;
    private byte[] bufferOut;
    private byte[] bufferIn;
    private byte currSeqNum = 1;


    /**
     * Constructor, which also initializes the constant connection to a
     * <code>UDPTrafficManager</code>, its reply timeout-timer, and its own
     * <code>DatagramSocket</code>.
     * <p>
     * @param parent       the owner of this object.
     * @param localPort    port number for the object's own socket.
     * @param targetedPort constant port number of a
     *                     <code>UDPTrafficManager</code>.
     * @param targetedHost constant host name of a
     *                     <code>UDPTrafficManager</code>.
     * <p>
     * @throws NumberFormatException if the local port was not a number.
     * @throws UnknownHostException  if the host name for the
     *                               <code>UDPTrafficManager</code> could not be
     *                               translated to an address.
     * @throws SocketException       if the local socket could not be opened.
     */
    public UDPUplinkHandler(VehicleComputer parent, String localPort, 
            int targetedPort, String targetedHost)
            throws NumberFormatException, UnknownHostException,
                   SocketException {
        this.parent = parent;
        this.localPort = Integer.parseInt(localPort);
        trafficManPort = targetedPort;
        trafficManAddr = InetAddress.getByName(targetedHost);
        socket = new DatagramSocket(this.localPort);
        timer = new Timer(TIMEOUT_DELAY_MS, new TimeoutListener());
    }

    /**
     * Get tickets for the supplied passengers. This method communicates through
     * UDP to the <code>UDPTrafficManager</code> and
     * <code>UDPPacketHandler</code>.
     * <p>
     * @param passengers list of passengers to get the tickets for.
     * <p>
     * @return a <code>TicketList</code> with all the tickets, or NULL if an
     *         error occurred.
     * <p>
     * @throws IOException if an I/O error occurred in the Streams.
     */
    public TicketList getTicketList(PassengerList passengers) throws IOException {
        /*Serialize passenger list and send the request to TrafficManager*/
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(passengers);
        prepBufferOut(currSeqNum, bos.toByteArray());
        packetOut = new DatagramPacket(bufferOut, bufferOut.length,
                                       trafficManAddr, trafficManPort);
        sendDatagram();

        /*Get reply, store port and addr of Handler and deserialize reply*/
        bufferIn = new byte[MAX_IN_BUFFER_SIZE];
        DatagramPacket packetIn = new DatagramPacket(bufferIn, bufferIn.length);
        System.out.println("UplinkHandler: Waiting for Tickets reply.");
        socket.receive(packetIn);
        handlerAddr = packetIn.getAddress();
        handlerPort = packetIn.getPort();
        ObjectInputStream ois = extractData(packetIn);
        TicketList tickets = null;
        try {
            tickets = (TicketList) ois.readObject();
        } catch (ClassNotFoundException ex) {
            System.err.println("Error in reading ticket list from reply.");
            // Continue and send ack, return NULL to previous call-frame.
            // Let that frame handle reply to re-call this method.
        }

        /*Send ack, reset seq number, and stop timer*/
        bos = new ByteArrayOutputStream();
        oos = new ObjectOutputStream(bos);
        oos.writeObject("ack");
        prepBufferOut(++currSeqNum, bos.toByteArray());
        packetOut = new DatagramPacket(bufferOut, bufferOut.length, handlerAddr,
                                       handlerPort);
        System.out.println("UplinkHandler: sending ack.");
        sendDatagram();
        currSeqNum = 1;
        timer.stop();

        return tickets;
    }

    /**
     * Fill the output buffer with the supplied sequence number and data.
     * <p>
     * @param seqNum sequence number to be placed in the buffer.
     * @param data   data to be placed in the buffer.
     */
    private void prepBufferOut(byte seqNum, byte[] data) {
        bufferOut = new byte[data.length + 1];
        
        int index = SEQ_NUM_INDEX;
        bufferOut[index] = seqNum;
        for (byte b : data) {
            bufferOut[++index] = b;
        }
    }

    /**
     * Extract the data from the current <code>DatagramPacket</code> field by
     * storing the sequence number in the currSeqNum field, and return an
     * <code>ObjectInputStream</code> with the data payload.
     * <p>
     * @param packet the datagram that has been received and from which
     *               data should be extracted.
     * <p>
     * @return the data payload in an <code>ObjectInputStream</code>.
     */
    private ObjectInputStream extractData(DatagramPacket packet) {
        bufferIn = packet.getData();
        currSeqNum = bufferIn[SEQ_NUM_INDEX];

        /*Copy serialized data into new array and load into input streams*/
        byte[] dataIn = Arrays.copyOfRange(bufferIn, (SEQ_NUM_INDEX + 1),
                                           bufferIn.length);
        ByteArrayInputStream bis = new ByteArrayInputStream(dataIn);
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(bis);
        } catch (IOException ex) {
            System.err.println("Could not extract data from datagram.");
            currSeqNum = -1;
        }
        return ois;
    }

    /**
     * Send the <code>DatagramPacket</code> field and start the timeout 
     * <code>Timer</code> for getting a reply.
     * <p>
     * If the datagram cannot be send the thread sleeps for 10 milliseconds 
     * before trying to resend. If resend is unsuccessful it repeats with a 10 
     * ms sleep before trying a second resend. If that too is unsuccessful the 
     * <code>systemRestartWarning</code> method in the 
     * <code>VehicleComputer</code> is called to prompt a restart in the system:
     * Not being able to get tickets is a fatal error.
     */
    private void sendDatagram() {
        try {
            socket.send(packetOut);
        } catch (IOException ex) {
            System.err.println("I/O exception in sending Datagram. ");
            ex.printStackTrace();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex1) {
            }
            /*First retry*/
            try {
                socket.send(packetOut);
            } catch (IOException ex1) {
                System.err.println("I/O exeption #2 in sending Datagram. ");
                ex1.printStackTrace();
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex2) {
                }
                /*Second retry*/
                try {
                    socket.send(packetOut);
                } catch (IOException ex2) {
                    System.err.println("I/O exeption #3 in sending Datagram. ");
                    parent.systemRestartWarning(ex2);
                }
            }
        }
        timer.start();
    }


    /**
     * Custom <code>ActionListener</code> for resending datagrams when a
     * <code>Timer</code> times out.
     */
    class TimeoutListener implements ActionListener {

        /**
         * Resend the current <code>DatagramPacket</code> stored in the field 
         * variable. 
         * <p>
         * @param e not used.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer.isRunning()) {
                System.out.println("Handler timeout. Resnding. . . ");
                sendDatagram();
            }
        }

    }
}
