/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VehicleServer;

import BuisnessLogic.*;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 *
 * @author Stensig
 */
public class VehicleComputer extends Thread implements ExternalVehicleSignals{
    
    private int currenZone = 0;
    private PassengerList passengerList = null;
    private TicketList tickets = null;
    private UDPComHandler udpHandler;
    
    // -- store ticketlist in local file for backup?
    
    public VehicleComputer(String trafficManPort, String trafficManAddr) {
        try {
            udpHandler = new UDPComHandler(trafficManPort, trafficManAddr);
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
        // calculate new price for each ticket in list 
    }
    
    
    public static void main(String[] args) {
        VehicleComputer vc = new VehicleComputer(args[0], args[1]);
        vc.start();
    }
}
