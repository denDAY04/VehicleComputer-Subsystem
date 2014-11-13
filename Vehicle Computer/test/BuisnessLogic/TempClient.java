package BuisnessLogic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Stand-in class for <code>VehicleComputer</code> clients in order to execute
 * <code>IntegreationTest</code> class.
 * 
 * @author Andreas Stensig Jensen, on Nov 11, 2014
 * Contributors: 
 */
public class TempClient extends Thread {
    PassengerList passengers;
    DatagramSocket socket;
    byte seqNum = 1;

    public TempClient() throws SocketException {
        passengers = IntegrationTest.generatePassList();
        socket = new DatagramSocket();
    }

    @Override
    public void run() {
        try {
            // Serialize passenger list
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(passengers);

            // Prepare and send datagram
            byte[] buffOut = bos.toByteArray();
            byte[] dataOut = new byte[buffOut.length + 1];
            int index = 0;
            dataOut[index++] = seqNum;
            for (byte b : buffOut) {
                dataOut[index++] = b;
            }
            InetAddress host = InetAddress.getLocalHost();
            int port = 2408;
            DatagramPacket packet = new DatagramPacket(dataOut, dataOut.length, host, port);
            socket.send(packet);
            
            // Recive response
            byte[] buffIn = new byte[UDPTrafficManager.BUFFER_OUT_SIZE];
            DatagramPacket response = new DatagramPacket(buffIn, buffIn.length);
            socket.receive(response);
            
            // Read it
            byte[] dataIn = response.getData();
            byte reciSeqNum = dataIn[0];
            ByteArrayInputStream bis = new ByteArrayInputStream(dataIn, 1, dataIn.length - 1);
            ObjectInputStream ois = new ObjectInputStream(bis);
            TicketList tickets = (TicketList) ois.readObject();
            
            // Print it
            for (Ticket t : tickets.getAllTickets()) {
                System.out.println("Client: Ticket nr. " + t.getNumber());
            }
            
            // Generate reply
            byte[] replyArr = {++reciSeqNum};
            packet = new DatagramPacket(replyArr, replyArr.length, response.getAddress(), response.getPort());
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(IntegrationTest.class.getName()).
                    log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TempClient.class.getName()).log(Level.SEVERE, null,
                                                             ex);
        } 
    }
}
