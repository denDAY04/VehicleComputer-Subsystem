package FrontEnd;

import BuisnessLogic.Ticket;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.swing.JApplet;
import javax.swing.JOptionPane;

/**
 *
 * @author Stensig
 */
public class PDAApplication extends JApplet {
    
    /**
     * Port number for the a handler listening for ping replies, which is always 
     * the same across all vehicles. 
     */
    private final int VC_UNICAST_PORT = 2223;
    /**
     * Do not tax the unicast handler on the <code>VehicleComputer</code> with 
     * requests for tickets. 
     */
    private final int VC_TICKET_REQUEST_PORT = 2225;
    /**
     * Host name for <code>VehicleComputer</code> is set when the application is 
     * pinged. It cannot know the address of the vehicle it is currently on until 
     * it has recieved a ping from it. 
     */
    protected InetAddress VC_HOST_ADDR;
    private final int APPLICATION_PORT = 2224;
    
    protected DatagramSocket socket;
    protected String customerNumber;
    protected GraphicalUserInterface gui;
    private PingHandler pingHandler;
    
    
    @Override
    public void init() {
        // Read customer number from file, open multicast socket, and run GUI
        try {
            fetchCustomerNumber();
            socket = new DatagramSocket(APPLICATION_PORT);
            gui = new GraphicalUserInterface(this);
            
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
    
    @Override
    public void start() {
        pingHandler = new PingHandler(this);
        pingHandler.start();
    }
    
    @Override
    public void destroy() {
        socket.close();
    }
    
    /**
     * Reads the customer number from a properties file in the directory of the 
     * application. 
     * @throws FileNotFoundException if the properties file does not exist.
     * @throws IOException if there were problems reading the properties file. 
     */
    private void fetchCustomerNumber() throws FileNotFoundException, IOException {
        //File file = new File("cu_prop.txt");
        String file = "cu_prop.txt";
        RandomAccessFile raf = new RandomAccessFile(file, "r");    
        customerNumber = raf.readLine();
        raf.close();
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
     * @throws IOException If the reply could not be read as a <code>Ticket</code>. 
     */
    public Ticket getTicket() throws IOException {
        // If no ping has been recived yet the application will not know the out-address
        if (VC_HOST_ADDR == null) {
            return null;
        }

        // Serialize the customer number and send request 
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(customerNumber);
        byte[] buffOur = bos.toByteArray();
        DatagramPacket packetOut = new DatagramPacket(buffOur, buffOur.length, VC_HOST_ADDR, VC_TICKET_REQUEST_PORT);
        socket.send(packetOut);
        
        // Get reply and check for valid ticket
        int arr_len = 150;      // Test gave serialized Ticket as 137 bytes
        DatagramPacket packetIn = new DatagramPacket(new byte[arr_len], arr_len);
        socket.receive(packetIn);
        
        ByteArrayInputStream bis = new ByteArrayInputStream(packetIn.getData());
        ObjectInputStream ois = new ObjectInputStream(bis);
        Ticket ticket = null;
        try {
            ticket = (Ticket) ois.readObject();
        } catch (ClassNotFoundException ex) {
            System.err.println("Application not read Ticket from Inputstream. ");
        }
        
        return ticket;
    }
    
}
