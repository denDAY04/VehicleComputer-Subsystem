package BusinessLogic;

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
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Concept- and initial testing of the <code>UDPTrafficManager</code> and
 * <code>UDPPacketHandler</code> classes' ability to handle multiple clients
 * requesting to be serviced at (arguably) the same time.
 * <p>
 * NOTE that this test was written with an dummy version of
 * <code>UDPPacketHandler</code>'s <code>processDatagram</code> method, which at
 * the time did not incorporate the custom application protocol. This test will
 * no longer pass, but did so in initial testing.
 * <p>
 * @author Andreas Stensig Jensen, on 08-11-2014
 * Contributors:
 */
public class UDPTMMultiThreadTest extends Thread {

    private int port;
    private String msg;
    private InetAddress addr;
    private DatagramSocket socket;


    public UDPTMMultiThreadTest(int port, String msg) throws
            UnknownHostException, SocketException {
        this.port = port;
        this.msg = msg;
        addr = InetAddress.getLocalHost();
        socket = new DatagramSocket();
    }

    @Override
    public void run() {
        try {
            ObjectOutputStream oos = null;
            try {
                DatagramPacket packet;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                oos = new ObjectOutputStream(bos);
                oos.writeObject(msg);
                byte[] arr = bos.toByteArray();
                packet = new DatagramPacket(arr, arr.length, addr, port);
                socket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(UDPTMMultiThreadTest.class.getName()).
                        log(Level.SEVERE, null, ex);
            } finally {
                try {
                    oos.close();
                } catch (IOException ex) {
                    Logger.getLogger(UDPTMMultiThreadTest.class.getName()).
                            log(Level.SEVERE, null, ex);
                }
            }

            byte[] inputArr = new byte[21900];
            DatagramPacket inPacket = new DatagramPacket(inputArr,
                                                         inputArr.length);
            socket.receive(inPacket);
            ByteArrayInputStream bis = new ByteArrayInputStream(inPacket.
                    getData());
            ObjectInputStream ois = new ObjectInputStream(bis);

            String response = (String) ois.readObject();
            System.err.println(response);
        } catch (IOException ex) {
            Logger.getLogger(UDPTMMultiThreadTest.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(UDPTMMultiThreadTest.class.getName()).
                    log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws UnknownHostException,
                                                  SocketException, IOException,
                                                  ClassNotFoundException {
        String message1 = "This is the test message1";
        String message2 = "This is the test message2";
        String message3 = "This is the test message3";
        String message4 = "This is the test message4";
        String message5 = "This is the test message5";
        int port = 2408;
        UDPTMMultiThreadTest test1 = new UDPTMMultiThreadTest(port, message1);
        UDPTMMultiThreadTest test2 = new UDPTMMultiThreadTest(port, message2);
        UDPTMMultiThreadTest test3 = new UDPTMMultiThreadTest(port, message3);
        UDPTMMultiThreadTest test4 = new UDPTMMultiThreadTest(port, message4);
        UDPTMMultiThreadTest test5 = new UDPTMMultiThreadTest(port, message5);

        test1.start();
        test2.start();
        test3.start();
        test4.start();
        test5.start();
    }


}
