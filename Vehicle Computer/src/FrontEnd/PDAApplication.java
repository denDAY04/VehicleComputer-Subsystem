package FrontEnd;

import BuisnessLogic.Ticket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 *
 * @author Stensig
 */
public class PDAApplication extends JFrame {
    
    /**
     * Do not tax the unicast handler on the <code>VehicleComputer</code> with 
     * requests for tickets. 
     */
    private final int VCTicketRequestPort = 2225;
    /**
     * The socket for fetching a ticket needs a timeout, in the case that it 
     * does not receive a reply from the vehicle computer. If not, the GUI
     * would freeze and lock up the application. 
     */
    private final int SOCKET_TIMEOUT = (5 * 1000);     // 10 sec
    /**
     * Host name for <code>VehicleComputer</code> is set when the application is 
     * pinged. It cannot know the address of the vehicle it is currently on 
     * until it has received a ping from it. 
     */
    protected InetAddress VCHostAddr;
    private final int localPort = 2220;
    protected DatagramSocket socket;
    protected String customerNumber;
    protected GraphicalUserInterface gui;
    private PingHandler pingHandler;
    
    
    public void init() {
        // Read customer number from file, open multicast socket, and run GUI
        try {
            readCustomerNumber();
            socket = new DatagramSocket(localPort);
            socket.setSoTimeout(SOCKET_TIMEOUT);
            gui = new GraphicalUserInterface(this);
            pingHandler = new PingHandler(this);
            pingHandler.start();
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    getContentPane().add(gui);
                }
            });
        } catch (IOException | InterruptedException | InvocationTargetException ex) {
            applicationError(ex);
        }       
            
    }
    
    /**
     * Reads the customer number from a properties file in the directory of the 
     * application, if such exists. Otherwise, the user is prompted for an input
     * which is stored in the file.
     * @throws FileNotFoundException if the properties file does not exist.
     * @throws IOException if there were problems reading the properties file. 
     */
    private void readCustomerNumber() throws FileNotFoundException, IOException {
        String name = "cu_prop.txt";
        File file = new File(name);
        
        if (file.exists()) {
            RandomAccessFile raf = new RandomAccessFile(file, "r");    
            customerNumber = raf.readLine();
            raf.close();
        } else {
            // Get customer number from user
            String input = "";
            while (!input.matches("[0-9]+")) {    // must only be digits
                input = JOptionPane.showInputDialog(this, "Please supply your customer number:");
                if (input == null) {
                    System.err.println("User canceled action. \nClosing app.");
                    System.exit(-1);
                }
            }
            // Store and write to file
            customerNumber = input;
            RandomAccessFile raf = new RandomAccessFile(file, "rw");
            for (byte b : customerNumber.getBytes()) {
                raf.writeByte(b);
            }
        }
    }
    
    /**
     * Displays an <code>JOptionPane</code> informing the user that something 
     * critically went wrong, and that the application must be restarted. When 
     * the message is closed the program exits with error code -1. 
     * @param ex optional parameter for the exception invoking the application
     * error. Can be NULL.
     */
    public void applicationError(Exception ex) {
        if (ex != null) {
            ex.printStackTrace();
        }
        String title = "Application error";
        String message = "An error occured. \n Please restart the application.";
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }
    
    /**
     * Get the <code>Ticket</code> from the <code>VehicleComputer</code>. If the
     * vehicle is not able to find a valid ticket, this method call return NULL.
     * @return the ticket for the application's customer number, or NULL if no
     * valid ticket was found or the application has not yet been pinged, and 
     * thus does not know what address to contact. 
     * @throws IOException If the reply could not be read as a 
     * <code>Ticket</code>. 
     */
    public Ticket getTicket() throws IOException {
        // If no ping has been recived yet the application will not know the out-address
        if (VCHostAddr == null) {
            System.err.println("No ping as been recieved yet. Cannot request ticket.");
            return null;
        }

        // Serialize the customer number and send request 
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(customerNumber);
        byte[] buffOur = bos.toByteArray();
        DatagramPacket packetOut = new DatagramPacket(buffOur, buffOur.length, VCHostAddr, VCTicketRequestPort);
        socket.send(packetOut);
        
        // Get reply and check for valid ticket
        int arrLen = 254;      // Serialized Ticket + header, about 160 bytes
        DatagramPacket packetIn = new DatagramPacket(new byte[arrLen], arrLen);
        System.out.println("Waiting for reply from VC.");
        socket.receive(packetIn);
        System.out.println("Reply received.");
        Ticket ticket = null;
        byte[] dataBuff = packetIn.getData();
        
        // Only deserialize if a ticket was returned
        if (dataBuff.length > 1) {
            ByteArrayInputStream bis = new ByteArrayInputStream(dataBuff);
            ObjectInputStream ois = new ObjectInputStream(bis);
            try {
                ticket = (Ticket) ois.readObject();
            } catch (ClassNotFoundException ex) {
                System.err.println("Application not read Ticket from Inputstream. ");
            }
        }
        return ticket;
    }
    
    
    /**
     * Main method for running the application.
     * @param args Not used. 
     */
    public static void main(String[] args) {
        PDAApplication app = new PDAApplication();
        app.init();
        app.setSize(260, 410);
        app.setVisible(true);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
