package VehicleServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


/**
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on Nov 18, 2014
 * Contributors: 
 */
public class UDPPingSender extends Thread {
    
    private final int INTERVAL = 1000;      // 1 sec
    private final DatagramSocket socket;
    private final int localPort = 2222;
    private final int targetPort = 2224;
    private final String multicastAddr = "239.0.1.139";
    private final VehicleComputer parent;
    
    public UDPPingSender(VehicleComputer parent) throws SocketException {
        this.parent = parent;
        socket = new DatagramSocket(localPort);
    }
    
    @Override
    public void run() {
        int drops = 0;
        int passes = 0;
        // Do five successful pings
        while (passes != 5) {
            try {
                ping();
                ++passes;
            } catch (IOException ex) {
                System.err.println("Ping unsuccessful; dropped.");
                // Allow only three to be dropped before system restart request
                if (++drops == 3) {
                    parent.systemRestartWarning(ex);
                }
            }
            try {
                Thread.sleep(INTERVAL);
            } catch (InterruptedException ex) {
                System.out.println("Ping-sleep was interrupted.");
            }
        }        
        System.out.println("Five pings passed. Sender terminating.");
    }
    
    private void ping() throws IOException {
        String msg = "ping";
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(msg);
        
        byte[] buffer = bos.toByteArray();
        InetAddress addr = InetAddress.getByName(multicastAddr);
        DatagramPacket packetOut = new DatagramPacket(buffer, buffer.length, addr, targetPort);
        System.out.println("Sending ping");
        socket.send(packetOut);
    }
    
}
