/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VehicleServer;

import BuisnessLogic.*;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stensig
 */
public class VehicleComputer extends Thread implements ExternalVehicleSignals{
    private int currenZone = 0;
    private PassengerList passengerList = null;
    private TicketList tickets = null;
    
    // -- store ticketlist in local file for backup?
    
    @Override
    public void run() {

    }
    
    @Override
    public void leftStation() {
        
    }    

    @Override
    public void zoneTransit(int zoneEntered) {
        // calculate new price for each ticket in list 
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    class pingTimer extends Thread {
        ArrayList<String>[] pingSeq;
        int PING_INTERVAL = 3000;        // in milliseconds
        
        @Override
        public void run() {
            for (int i = 0; i < 3; ++i) {
                pingSeq[i] = udpTran.getPassangerDeviceNumbers();
                try {
                    wait(PING_INTERVAL);
                } catch (InterruptedException ex) {
                    Logger.getLogger(VehicleComputer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static void main(String[] args) {
        VehicleComputer vc = new VehicleComputer();
        vc.start();
    }
}
