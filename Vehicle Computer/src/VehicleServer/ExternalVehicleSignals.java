/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package VehicleServer;

/**
 *
 * @author Stensig
 */
public interface ExternalVehicleSignals {
    /**
    * Expected to be triggered a few seconds after leaving a station.
    */
    public void leftStation();          
    
    /**
    * Expected to be triggered upon moving from one zone to another. 
    * @param zoneEntered the zone number of the new zone just entered.
    */
    public void zoneTransit(int zoneEntered);
}
