package BusinessLogic;

import ModelClasses.PassengerList;
import ModelClasses.TicketList;
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
public class UDPDatagramHandler extends Thread {

    private final int SEQ_NUM_INDEX = 0;
    /**Time frame in which to expect a reply before resend last datagram*/
    private final int RESEND_TIMEOUT_MS = (1000 * 4);     

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
     *                          wanting to be serviced.
     * @param socketPort        the port number on which the handler shall open
     *                          a <code>DatagramSocket</code>.
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
    public UDPDatagramHandler(DatagramPacket packet, int socketPort,
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
    public UDPDatagramHandler(DatagramPacket packet, int socketPort) throws
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
        /*Initial processing of datagram set in constructor.*/
        try {
            processDatagram();
        } catch (IOException ex) {
            /*
             If first datagram is dropped then client won't know the port of 
             the handler. Thus the handler serves no purpose as the client will 
             contact UDPTrafficManager again. Therefore, kill the handler. 
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
        System.out.println("PacketHandler killed.");
    }

    /**
     * Kill the thread, making it stop execution. If the thread is
     * currently processing a Datagram the thread will not stop execution until
     * after it is done with its current task.
     */
    private void killThread() {
        timer.stop();
        alive = false;
    }

    /**
     * Process the data in the <code>DatagramPacket</code>.
     */
    private void processDatagram() throws IOException {
        System.out.println("PacketHandler: Processing datagram");
        ObjectInputStream ois = extractData();

        switch (currSeqNum) {
            /*Request for service and tickets*/
            case 1:  
                if (currSeqNum != expSeqNum) {
                    System.err.println("Unexpected seq num; resending data.");
                    /*
                     sendReply() modifies expSeqNum by +2 each time; calling it 
                     again without reverting the expected value would result
                     in a logical error.
                     */
                    expSeqNum -= 2;
                    sendReply();
                    return;
                }

                PassengerList passengers;
                try {
                    passengers = (PassengerList) ois.readObject();
                    TicketList tickets = journeyManager.generateTickets(
                            passengers);

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

            /*Ack for recieved ticket list*/
            case 3:
                System.out.println("Ack from client recieved");
                killThread();
                break;

            default:
                System.err.println("Unrecognized sequence number.");
                System.err.println("Dropping datagram.");
                break;
        }
    }

    /**
     * Extract the data from the current <code>DatagramPacket</code> field by
     * storing the sequence number in the <code>currSeqNum</code> field, and 
     * return an <code>ObjectInputStream</code> with the data payload.
     * <p>
     * @return the data payload in an <code>ObjectInputStream</code>.
     */
    private ObjectInputStream extractData() {
        bufferIn = packetIn.getData();
        currSeqNum = bufferIn[SEQ_NUM_INDEX];

        byte[] dataIn = Arrays.copyOfRange(bufferIn, (SEQ_NUM_INDEX + 1),
                                           bufferIn.length);

        ByteArrayInputStream bis = new ByteArrayInputStream(dataIn);
        try {
            ObjectInputStream ois = new ObjectInputStream(bis);
            return ois;
        } catch (IOException ex) {
            System.err.println("Could not extract data from datagram.");
            ex.printStackTrace();
            currSeqNum = -1;
            return null;
        }
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
     * client specified by the <code>destPort</code> and <code>destAddr</code> 
     * fields. Also starts the timeout <code>Timer</code> for getting a reply.
     * <p>
     * If the reply cannot be send the thread sleeps for 10 milliseconds before
     * trying to resend. If resend is unsuccessful it repeats with a 10 ms sleep
     * before trying a second resend. If that too is unsuccessful the handler 
     * is deemed useless and is killed, since the client won't know the address
     * of the handler and will thus contact <code>UDPTrafficManager</code>
     * again, spawning a new handler. 
     */
    private void sendReply() {
        packetOut = new DatagramPacket(bufferOut, bufferOut.length, destAddr,
                                       destPort);
        try {
            System.out.println(
                    "Sending data to " + packetOut.getSocketAddress());
            socket.send(packetOut);
        } catch (IOException ex) {
            System.err.println("I/O exception in sending reply. ");
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex1) {
            }
            /*First retry*/
            try {
                System.err.println("Retrying. . . ");
                socket.send(packetOut);
            } catch (IOException ex1) {
                System.err.println("I/O exception #2 in sending reply. ");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException ex2) {
                }
                /*Second retry*/
                try {
                    System.err.println("Retrying. . . ");
                    socket.send(packetOut);
                } catch (IOException ex2) {
                    System.err.println("I/O exeption #3 in sending reply. ");
                    System.err.println("PacketHandler unusable. "
                            + "Killing thread.");
                    killThread();
                    return;
                }
            }
        }
        System.out.println("Send successful.");
        expSeqNum += 2;
        timer.start();
    }


    /**
     * Custom <code>ActionListener</code> for resending replies when a
     * <code>Timer</code> times out. Only allows three timeouts to occur before
     * killing handler thread.
     * <p>
     * This is implemented in order to avoid excessive spawning of handler 
     * threads from <code>UDPTrafficManager</code>. If this timeout occurs 
     * because the reply never reaches the client then the client will contact 
     * the TrafficManager again, in which case a new thread will be spawned. 
     * Therefore this thread must at some time decide that it should give up and 
     * terminate itself. 
     * <p>
     * If the timeout occurs due to an ack that is lost on the client's side, 
     * the impact of terminating this thread is void, since the reply reached
     * the client and it has its tickets; the ack would, in any case, simply 
     * terminate the thread. 
     */
    class TimeoutListener implements ActionListener {

        private final int TIMEOUT_LIMIT = 3;
        private int timeouts = 0;
        
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
                if (++timeouts != TIMEOUT_LIMIT) {
                    System.out.println("Handler timeout. Resnding. . . ");
                    sendReply();
                } else {
                    System.err.println("Three timeouts reached."
                            + " Killing thread.");
                    killThread();
                }
            }
        }

    }

}
