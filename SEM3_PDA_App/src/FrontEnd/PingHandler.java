package FrontEnd;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import javax.swing.JOptionPane;
import javax.swing.Timer;


/**
 * Separate thread for processing and handling pings from a
 * <code>VehicleComputer</code>.
 * <p>
 * @author Andreas Stensig Jensen, on 15-11-2014
 * Contributors:
 */
class PingHandler extends Thread {

    /**
     * Time limit for indicating that the PDA is no longer on board a vehicle,
     * by not having been pinged in this interval. Given in milliseconds, the 
     * current value is 1 minute for testing purposes.
     */
    private final int PINGTIMER_TIMEOUT = (1000 * 60 * 1);
    /**
     * Time that the device should not answer to pings when the user has 
     * manually disable it. Is in milliseconds. 
     */
    private final int STATUSTIMER_REACTIVATE = (1000 * 60 * 1);

    private final int multicastPort = 2224;
    private final int singlecastReplyPort = 2223;
    private final String multicastAddr = "239.0.1.139";
    private MulticastSocket multiSocket;
    private int missedPings;
    private final DatagramPacket packetIn = new DatagramPacket(new byte[128],
                                                               128);
    private final PDAApplication parent;
    private final Timer pingTimer;
    private Timer statusTimer;
    private boolean answerPings = true;


    /**
     * Constructor for initializing reference to the Application using this
     * handler.
     * <p>
     * @param parent the <code>PDAApplication</code> using the handler.
     */
    public PingHandler(PDAApplication parent) {
        this.parent = parent;
        pingTimer = new Timer(PINGTIMER_TIMEOUT, new PingTimerListener());
    }

    /**
     * Main flow of the ping handler. If it initializes and opens its socket 
     * for multicasting without problems it won't terminate until the 
     * <code>PDAApplication</code> does.  
     */
    @Override
    public void run() {
        try {
            /*Open socket and add it to multicast group*/
            multiSocket = new MulticastSocket(multicastPort);
            InetAddress group = InetAddress.getByName(multicastAddr);
            multiSocket.joinGroup(group);
        } catch (IOException ex) {
            System.err.println("Cannot open multicast socket.");
            parent.applicationError(ex);
        }

        while (true) {
            try {
                System.out.println("PingHandler: waiting for ping . . .");
                multiSocket.receive(packetIn);
                System.out.println("PingHandler: datagram recieved.");
                String payload = getData(packetIn);

                switch (payload) {
                    case "inactive": 
                        System.out.println("Handler is inactive.");
                        break;
                    
                    case "ping":
                        /*Set host reply address in parent*/
                        parent.VCHostAddr = packetIn.getAddress();
                        /*Send pong with customer number*/
                        byte[] reply = generateReplyBuffer();
                        InetAddress replyAddr = packetIn.getAddress();
                        DatagramPacket packetOut = 
                                new DatagramPacket(reply, reply.length, 
                                        replyAddr, singlecastReplyPort);
                        System.out.println("PingHandler: Sending pong");
                        multiSocket.send(packetOut);
                        break;

                    case "ack":
                        System.out.println("PingHandler: Ack received");
                        missedPings = 0;
                        /*Show ping in GUI and start timer for receiving next*/
                        parent.gui.enablePingLabel(true);
                        pingTimer.restart();
                        break;

                    default:
                        System.err.println("Ping payload not recognized. "
                                + "\nDropping ping.");
                        break;
                }
            } catch (IOException ex) {
                /*Disable gui ping lable and increment lost-ping counter*/
                parent.gui.enablePingLabel(false);
                if (++missedPings >= 3) {
                    String title = "Missed pings";
                    String message = "Something went wrong. "
                            + "\nIf you cannot access a ticket "
                            + "\nin 10 seconds, visit \nwww.1415.dk";
                    JOptionPane.showMessageDialog(parent, message, title,
                                                  JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    /**
     * Set the status of the ping handler, whether it should reply to pings 
     * or not. If set to false, the handler will not reply to pings for the time
     * specified in the <code>STATUSTIMER_REACTIVATE</code> field. 
     * <p>
     * @param status of the handler for answering pings. 
     */
    public void shouldPong(boolean status) {
        answerPings = status;
        if (!status) {
            System.out.println("Ping handler deactivated.");
            statusTimer = new Timer(STATUSTIMER_REACTIVATE,
                    new StatusTimerListener());
            statusTimer.start();
        } else {
            System.out.println("Ping handler activated.");
            statusTimer.stop();
            parent.gui.showEnabledJourneying();
        }
    }
    
    /**
     * Get the data from the <code>DatagramPacket</code> ping.
     * <p>
     * @return the data from the packet as a String. 
     * <p>
     * @throws IOException if the <code>InputStream</code>
     */
    private String getData(DatagramPacket packet) throws IOException {
        if (!answerPings) {
            return "inactive";
        }
        
        byte[] dataBytes = packet.getData();
        ByteArrayInputStream bis = new ByteArrayInputStream(dataBytes);
        ObjectInputStream ois = new ObjectInputStream(bis);

        String data = "";
        try {
            data = (String) ois.readObject();
        } catch (ClassNotFoundException ex) {
            System.err.println("Ping data payload was not a string. "
                    + "\nDropping ping.");
        } finally {
            ois.close();
            bis.close();
        }
        return data;
    }

    /**
     * Generates a <code>byte</code> array that contains the serialized
     * customer number field from the <code>PDAApplication</code>.
     * <p>
     * @return the populated byte array.
     * <p>
     * @throws IOException if an I/O error occurs.
     */
    private byte[] generateReplyBuffer() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(parent.customerNumber);
        byte[] reply = bos.toByteArray();
        oos.close();
        bos.close();
        return reply;
    }


    /**
     * <code>Timer ActionListener</code> that is triggered when a timeout for
     * an expected ping has occurred, signaling that the device is no longer
     * in a public transportation vehicle.
     */
    class PingTimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            parent.gui.enablePingLabel(false);
            pingTimer.stop();
        }

    }
    
    /**
     * <code>Timer ActionListener</code> that is triggered when a given time has
     * passed after the user has ended a journey, as specified in the 
     * <code>STATUSTIMER_REACTIVATE</code> field. 
     * <p>
     * This event will trigger the status of the handler to be active again.
     */
    class StatusTimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Deactivation period passed. Enabling handler.");
            shouldPong(true);
        }

    }
}
