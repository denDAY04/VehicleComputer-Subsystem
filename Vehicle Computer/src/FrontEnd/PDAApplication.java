package FrontEnd;

import BuisnessLogic.Ticket;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.DatagramSocket;
import javax.swing.JApplet;
import javax.swing.JOptionPane;

/**
 *
 * @author Stensig
 */
public class PDAApplication extends JApplet {
    
    protected DatagramSocket socket;
    protected String customerNumber;
    protected GraphicalUserInterface gui;
    private PingHandler pingHandler;
    
    
    @Override
    public void init() {
        // Read customer number from file, open multicast socket, and run GUI
        try {
            fetchCustomerNumber();
            socket = new DatagramSocket();
            gui = new GraphicalUserInterface(this);
            
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    getContentPane().add(gui);
                }
            });
        } catch (Exception ex) {
            applicationError();
        }        
    }
    
    @Override
    public void start() {
        pingHandler = new PingHandler(this);
        pingHandler.start();
    }
    
    @Override
    public void destroy() {
        
    }
    
    /**
     * Reads the customer number from a properties file in the directory of the 
     * application. 
     * @throws FileNotFoundException if the properties file does not exist.
     * @throws IOException if there were problems reading the properties file. 
     */
    private void fetchCustomerNumber() throws FileNotFoundException, IOException {
        File file = new File("cu_prop.txt");
        RandomAccessFile raf = new RandomAccessFile(file, "r");    
        customerNumber = raf.readLine();
    }
    
    /**
     * Displays an <code>JOptionPane</code> informing the user that something 
     * critically went wrong, and that the application must be restarted. When 
     * the message is closed the program exits with error code -1. 
     */
    public void applicationError() {
        String title = "Application error";
        String message = "An error occured. \n Please restart the application.";
        JOptionPane.showMessageDialog(this, message, title, JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }
    
    
    public Ticket getTicket() {
        // TO DO!
        
        return null;
    }
}
