/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VehicleServer;

import BuisnessLogic.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Stensig
 */
public class VehicleComputer extends Thread implements ExternalVehicleSignals{
    
    private final String BACKUP_FILE_NAME = "vc_backup.txt";
    
    private int currenZone = 1;
    private PassengerList passengers = null;
    private TicketList tickets = null;
    private UDPUplinkHandler uplinkHandler;
    
    
    public VehicleComputer(String startZone, String uplinkPort, String trafficManPort, String trafficManAddr) {
        try {
            currenZone = Integer.parseInt(startZone);
            uplinkHandler = new UDPUplinkHandler(uplinkPort, trafficManPort, trafficManAddr);
        } catch (NumberFormatException | UnknownHostException |
                SocketException ex) {
            System.err.println("Fatal error in VehicleComputer setup.");
            ex.printStackTrace();
            System.exit(-1);
        }
    }
    
    @Override
    public void run() {

    }
    
    @Override
    public void leftStation() {
        
    }    

    @Override
    public void zoneTransit(int zoneEntered) {
        currenZone = zoneEntered;
        passengers.setZone(currenZone);
        tickets = requestTickets();
    }
    
    /**
     * Returns a full-copy of the <code>TicketList</code> field object. 
     * @return a copy of the ticket list.
     */
    public TicketList getTickets() {
        return new TicketList(tickets);
    }
    
    private TicketList requestTickets() {
        TicketList newTickets = null;
        try {   
            // Try three times
            for (int i = 0; i != 3 && newTickets == null; ++i) {
                newTickets = uplinkHandler.getTicketList(passengers);
            }
        } catch (IOException ex) {
            try {
                // Retry
                newTickets = uplinkHandler.getTicketList(passengers);
            } catch (IOException ex1) {
                // If still not successful, request a restart of the system
                systemRestartWarning(ex1);
            }
        }
        
        // If tickets are still NULL, request a restart of the system
        if (newTickets == null) {
            systemRestartWarning(new NullPointerException());
        }
        
        return tickets;
    }
    
    /**
     * Attempts to store the passengers and tickets fields in a local backup 
     * file by the name "vc_backup.txt". Afterwards it exits the system with an
     * output of the stack trace of the cause of the fatal error. 
     * @param cause the exception that caused the fatal error.
     */
    private void systemRestartWarning(Exception cause) {
        try {
            // Serialize passengers and tickets
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(passengers);
            oos.flush();
            oos.writeObject(tickets);
            byte[] data = bos.toByteArray();
            
            // Write to file
            RandomAccessFile raf = new RandomAccessFile(BACKUP_FILE_NAME, "rw");
            raf.write(data);
            raf.close();
            System.err.println("Data saved in backup vc_data.txt");
            
        } catch (Exception ex) {
            System.err.println("Could not back up data. ");
        }      
        System.err.println("A fatal error has occoured.");
        cause.printStackTrace();
        System.err.println("Restart of the system is required.");
        System.exit(-1);
    }
    
    /**
     * Read the <code>PassengerList</code> and <code>TicketList</code> from 
     * a backup file, if such exist - by the name "vc_backup.txt". Returns the 
     * result of trying to re-create data from the file. 
     * @return true if and only if both of the lists could be read from an 
     * existing file; false otherwise. 
     */
    private boolean readBackup() {
        File backup = new File(BACKUP_FILE_NAME);
        if (!backup.exists()) {
            return false;
        }
        
        try {
            // Read backup file
            RandomAccessFile raf = new RandomAccessFile(backup, "r");
            byte[] buff = new byte[31000]; // Actual size for 800 passengers and tickets: 21900+8300 = 30200
            raf.read(buff);

            // Deserialize objects and load into fields
            ByteArrayInputStream bis = new ByteArrayInputStream(buff);
            ObjectInputStream ois = new ObjectInputStream(bis);
            passengers = (PassengerList) ois.readObject();
            tickets = (TicketList) ois.readObject();
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException | ClassNotFoundException ex) {
            return false;
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        VehicleComputer vc = new VehicleComputer(args[0], args[1], args[2], args[3]);
        vc.start();
    }
}
