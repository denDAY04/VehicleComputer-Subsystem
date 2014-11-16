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
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on 15-11-2014
 * Contributors: 
 */
class PingHandler extends Thread {
        
    private final int MULTICAST_PORT = 24448;
    private final int SINGLECAST_REPLY_PORT = 24449;
    private final int PINGTIMER_TIMEOUT = (1000 * 60 * 10); // 10 minutes
    private MulticastSocket multiSocket;
    private int missedPings;
    private final DatagramPacket packetIn = new DatagramPacket(new byte[128], 128);
    private final PDAApplication parent;
    private final Timer pingTimer;

    
    /**
     * Constructor for initializing reference to the Application using this
     * handler. 
     * @param parent the <code>PDAApplication</code> using the handler.
     */
    public PingHandler(PDAApplication parent) {
        this.parent = parent;
        pingTimer = new Timer(PINGTIMER_TIMEOUT, new PingTimerListener());
    }

    @Override
    public void run() {
        try {
            multiSocket = new MulticastSocket(MULTICAST_PORT);
        } catch (IOException ex) {
            // If multicast socket fails, application must restart
            System.err.println("Cannot open multicast socket.");
            parent.applicationError(ex);
        }

        // Infinite loop to always listen for pings
        while (true) {
            try {
                // Read packet and verify its payload
                multiSocket.receive(packetIn);
                String payload = getData(packetIn);
                
                switch (payload) {
                    case "ping":
                        // Set host reply address in parent, for when requesting a ticket
                        parent.VC_HOST_ADDR = packetIn.getAddress();
                        // Generate reply
                        byte[] reply = generateReplyBuffer();
                        InetAddress replyAddr = packetIn.getAddress();
                        DatagramPacket packetOut = new DatagramPacket(reply, reply.length, replyAddr , SINGLECAST_REPLY_PORT);
                        parent.socket.send(packetOut);
                        break;
                        
                    case "ack":
                        missedPings = 0;
                        // Show in gui that the device has been pinged, and start timer
                        parent.gui.enablePingLabel(true);
                        pingTimer.restart();
                        break;
                        
                    default:
                        System.err.println("Ping payload not recognized. \nDropping ping.");
                        break;
                }              
            } catch (IOException ex) {
                // Disable gui ping lable and increment lost-ping counter
                parent.gui.enablePingLabel(false);
                if (++missedPings >= 3) {
                    String title = "Missed pings";
                    String message = "Something went wrong. "
                            + "\nIf you cannot access a ticket "
                            + "\nin 10 seconds, visit \nwww.1415.dk";
                    JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    /**
     * Get the data from the <code>DatagramPacket</code> ping. 
     * @return the data from the packet. 
     * @throws IOException if the <code>InputStream</code> 
     */
    private String getData(DatagramPacket packet) throws IOException {
        byte[] dataBytes = packet.getData();
        ByteArrayInputStream bis = new ByteArrayInputStream(dataBytes);
        ObjectInputStream ois = new ObjectInputStream(bis);
        
        String data = "";
        try {
            data = (String) ois.readObject();
        } catch (ClassNotFoundException ex) {
            System.err.println("Ping data payload was not a string. \nDropping ping.");
        } finally {
            ois.close();
            bis.close();
        }
        return data;
    }
    
    /**
     * Generates a <code>byte</code> array that contains the serialized 
     * customer number field from the <code>PDAApplication</code>. 
     * @return the byte array. 
     * @throws IOException if an I/O error occours. 
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
     * an expected ping has occoured, signaling that the device is no longer 
     * in a public transportation vehicle.
     */
    class PingTimerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            parent.gui.enablePingLabel(false);
        }
        
    }
}
