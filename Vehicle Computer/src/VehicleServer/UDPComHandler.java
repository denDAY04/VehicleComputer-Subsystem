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
import java.util.Arrays;
import javax.swing.Timer;


/**
 * Traffic handler for UDP communication from vehicle to business logic backend.
 * 
 * @author Andreas Stensig Jensen, on 16-11-2014
 * Contributors: 
 */
public class UDPComHandler {

    private final int SEQ_NUM_INDEX = 0;
    private final int TIMEOUT_DELAY = (1000 * 4);       // 4 seconds
    
    private final int localPort = 2226;
    private final int trafficManPort;       // Const port to TrafficManager
    private final InetAddress trafficManAddr;   // Const addr to TrafficManager
    private int handlerPort;                // Dynamic post to service handler
    private InetAddress handlerAddr;        // Dynamic addr to service handler
    private final DatagramSocket socket;
    private DatagramPacket packetOut;
    private final Timer timer;
    private byte[] bufferOut;
    private byte[] bufferIn;
    private byte currSeqNum = 1;

    
    public UDPComHandler(String targetedPort, String targetedHost) 
            throws NumberFormatException, UnknownHostException, SocketException {
        trafficManPort = Integer.parseInt(targetedPort);
        trafficManAddr = InetAddress.getByName(targetedHost);
        InetAddress host = InetAddress.getLocalHost();
        socket = new DatagramSocket(localPort, host);
        timer = new Timer(TIMEOUT_DELAY, new TimeoutListener());
    }
    
    public TicketList getTicketList(PassengerList passengers) throws IOException {
        // Serialize passenger list and send the request to TrafficManager
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(passengers);
        prepBufferOut(currSeqNum, bos.toByteArray());
        packetOut = new DatagramPacket(bufferOut, bufferOut.length, trafficManAddr, trafficManPort);
        sendDatagram();
        
        // Get reply, store port and addr of Handler and deserialize reply 
        bufferIn = new byte[21900]; // Test with 800 Tickets = 21,833 Byte
        DatagramPacket packetIn = new DatagramPacket(bufferIn, bufferIn.length);
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
        
        // Send ack
        oos.reset();
        bos.reset();
        oos.writeObject("ack");
        prepBufferOut(++currSeqNum, bos.toByteArray());
        packetOut = new DatagramPacket(bufferOut, bufferOut.length, handlerAddr, handlerPort);
        sendDatagram();
        
        // Reset sequence number and stop timer
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
     * @param packet the datagram that has been recieved and from which 
     * data should be extracted.
     * @return the data payload in an <code>ObjectInputStream</code>.
     */
    private ObjectInputStream extractData(DatagramPacket packet) {
        bufferIn = packet.getData();
        currSeqNum = bufferIn[SEQ_NUM_INDEX];

        byte[] dataIn = Arrays.copyOfRange(bufferIn, (SEQ_NUM_INDEX + 1), bufferIn.length);
        ByteArrayInputStream bis = new ByteArrayInputStream(dataIn);
        ObjectInputStream ois= null;
        try {
            ois = new ObjectInputStream(bis);
        } catch (IOException ex) {
            System.err.println("Could not extract data from datagram.");
            currSeqNum = -1;
        }
        return ois;
    }
    
    /**
     * Send the <code>DatagramPacket</code> packetOut field on the socket. Also 
     * starts the timeout <code>Timer</code> for getting a reply. 
     * <p>
     * If it cannot be send the thread sleeps for 10 milliseconds before
     * trying to resend. If resend is unsuccessful the effort is dropped
     * (though the <code>DatagramPacket</code> field retains the reply). Thus
     * it does not try and resend again before either 1) its timeout is
     * triggered, or 2) the client resends its datagram.
     */
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
        
        // Start timeout timer for getting a reply
        timer.start();
    }
    
    
    class TimeoutListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer.isRunning()) {
                System.out.println("Handler timeout. Resnding. . . ");
                sendDatagram();
            }
        }
        
    }
}
