package BuisnessLogic;

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
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import javax.swing.Timer;


/**
 * Thread extension class for handling communication with a
 * <code>VehicleComputer</code> in regards to producing a
 * <code>TicketList</code> out of a <code>PassengerList</code>.
 * <p>
 * This class is meant to be instantiated with each new session with a
 * vehicle computer client, and terminate itself after having completed its
 * service of the client, as dictated by a custom protocol.
 * <p>
 * @author Andreas Stensig Jensen, on Nov 7, 2014
 * Contributors:
 */
public class UDPPacketHandler extends Thread {

    private final int SEQ_NUM_INDEX = 0;
    private final int RESEND_TIMEOUT_MS = (1000 * 4);     // 4 seconds

    private boolean alive = true;
    private final int destPort;
    private final InetAddress destAddr;
    private byte[] bufferIn;
    private byte[] bufferOut;
    private Timer timer;
    private final DatagramSocket socket;
    private DatagramPacket packetIn;
    private DatagramPacket packetOut;
    private byte expSeqNum, currSeqNum;
    private JourneyManager journeyManager;


    /**
     * Proper constructor that incorporates RMI variables.
     * <p>
     * @param packet            initial <code>DatagramPacket</code> from client
     *                          wanted to
     *                          be serviced.
     * @param socketPort        the port number on which the handler shall open
     *                          a
     *                          <code>DatagramSocket</code>.
     * @param rmiHost           host name of the service running the RMI
     *                          registry.
     * @param rmiPort           port number of the service running the RMI
     *                          registry.
     * @param rmiJourneyManName name of the <code>JourneyManager</code>
     *                          implementation class in the RMI registry.
     * <p>
     * @throws RemoteException   if unable to locate RMI registry.
     * @throws NotBoundException if unable to locate implementation class in the
     *                           RMI registry.
     * @throws SocketException   if unable to open a <code>DatagramSocket</code>
     *                           on the specified port.
     */
    public UDPPacketHandler(DatagramPacket packet, int socketPort,
                            String rmiHost, int rmiPort,
                            String rmiJourneyManName) throws RemoteException,
                                                             NotBoundException,
                                                             SocketException {
        this.packetIn = packet;
        destPort = packet.getPort();
        destAddr = packet.getAddress();
        journeyManager = new JourneyManager(rmiHost, rmiPort, rmiJourneyManName);
        socket = new DatagramSocket(socketPort);
        expSeqNum = 1;
        timer = new Timer(RESEND_TIMEOUT_MS, new TimeoutListener());
    }

    /**
     * Test constructor that ignores RMI initialization. Use for
     * internal testing that doesn't concern itself with RMI.
     * <p>
     * @param packet     initial <code>DatagramPacket</code> from client wanted
     *                   to be serviced.
     * @param socketPort the port number on which the handler shall open a
     *                   <code>DatagramSocket</code>.
     * <p>
     * @throws SocketException if unable to open a <code>DatagramSocket</code>
     *                         on the specified port.
     */
    public UDPPacketHandler(DatagramPacket packet, int socketPort) throws
            SocketException {
        this.packetIn = packet;
        destPort = packet.getPort();
        destAddr = packet.getAddress();
        socket = new DatagramSocket(socketPort);
    }

    /**
     * Main flow of the the thread. Will continue to run, processing data
     * whenever a new <code>DatagramPacket</code> is available, until
     * <code>killThread()</code> has been called.
     */
    @Override
    public void run() {
        // Initial processing of datagram set in constructor.
        try {
            processDatagram();
        } catch (IOException ex) {
            /*
             If first datagram is dropped then client won't know the port of 
             the handler. Thus the handler serves no purpose as the client will 
             contant UDPTrafficManager again. Therefore, kill the handler. 
             */
            System.err.println("-- UDPPacketHandler --");
            System.err.println("I/O exception in initial datagram.");
            System.err.println("Killing thread.");
            killThread();
        }
        while (alive) {
            try {
                socket.receive(packetIn);
                timer.stop();
                processDatagram();
            } catch (IOException ex) {
                System.err.println("-- UDPPacketHandler --");
                System.err.println("I/O exception; datagram dropped.");
            }
        }
        System.out.println("Thread killed. . . ");
    }

    /**
     * Kills the targeted thread, making it stop execution. If the thread is
     * currently processing a Datagram the thread will not stop execution until
     * after it is done with its current task.
     */
    private void killThread() {
        timer.stop();
        alive = false;
    }

    /**
     * Processes the data in the <code>DatagramPacket</code>.
     */
    private void processDatagram() throws IOException {
        System.out.println("Processing datagram");
        ObjectInputStream ois = extractData();

        switch (currSeqNum) {
            case 1:  // Request for service and tickets
                // If unexpected sequence number, just resend last packet
                if (currSeqNum != expSeqNum) {
                    System.out.println("Unexpected seq num; resending data.");
                    /*
                    sendReply() modify expSeqNum by +2 each time; calling it 
                    again without reverting the expected value would result
                    in an expected value of 5, which is not recognized.
                    */
                    expSeqNum -= 2;
                    sendReply();
                    return;
                }

                PassengerList passengers;
                try {
                    // Extract passenger list, and get ticket list from JourneyManager
                    passengers = (PassengerList) ois.readObject();
                    TicketList tickets = journeyManager.generateTickets(passengers);

                    // Serialize ticket list
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(bos);
                    oos.writeObject(tickets);
                    byte[] dataOut = bos.toByteArray();

                    // Send reply
                    prepBufferOut(++currSeqNum, dataOut);
                    sendReply();

                } catch (IOException | ClassNotFoundException ex) {
                    System.err.println("Could not process data in datagram.");
                    ex.printStackTrace();
                    System.err.println("Dropping datagram.");
                }
                break;

            case 3: // Ack for recieved ticket list
                // End of service; handler terminates itself
                System.out.println("Ack from client recieved");
                killThread();
                break;
                
            default:
                System.err.println("Unrecognized sequence number.");
                System.err.println("Dropping datagram.");
                timer.start();
                break;
        }
    }

    /**
     * Extract the data from the current <code>DatagramPacket</code> field by
     * storing the sequence number in the currSeqNum field, and return an
     * <code>ObjectInputStream</code> with the data payload. 
     * <p>
     * @return the data payload in an <code>ObjectInputStream</code>.
     */
    private ObjectInputStream extractData() {
        bufferIn = packetIn.getData();
        currSeqNum = bufferIn[SEQ_NUM_INDEX];

        byte[] dataIn = Arrays.copyOfRange(bufferIn, (SEQ_NUM_INDEX + 1),
                                           bufferIn.length);
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
     * Prepare the <code>DatagramPacket</code> field and send the reply to the
     * client specified by the destPort and destAddr fields. Also starts the 
     * timeout <code>Timer</code> for getting a reply. 
     * <p>
     * If the reply cannot be send the thread sleeps for 10 milliseconds before
     * trying to resend. If resend is unsuccessful the reply is dropped
     * (though the <code>DatagramPacket</code> field retains the reply). Thus
     * it does not try and resend again before either 1) its timeout is
     * triggered, or 2) the client resends its datagram.
     */
    private void sendReply() {
        System.out.println("Sending data");
        packetOut = new DatagramPacket(bufferOut, bufferOut.length, destAddr,
                                       destPort);

        try {
            socket.send(packetOut);
        } catch (IOException ex) {
            System.err.println("I/O exception in sending reply. ");
            try {
                Thread.sleep(10);
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
        
        // Start timeout timer for getting a reply and increment expSeq by 2
        timer.start();
        expSeqNum += 2;
    }


    /**
     * Custom <code>ActionListener</code> for resending replies when a
     * <code>Timer</code> times out.
     */
    class TimeoutListener implements ActionListener {

        /**
         * Timeout event is triggered by the <code>Timer</code>. Resend last
         * reply only if the timer is running, not if the event was triggered by
         * the timer being stopped.
         * <p>
         * @param e Not used.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if (timer.isRunning()) {
                System.out.println("Handler timeout. Resnding. . . ");
                sendReply();
            }
        }

    }

}
