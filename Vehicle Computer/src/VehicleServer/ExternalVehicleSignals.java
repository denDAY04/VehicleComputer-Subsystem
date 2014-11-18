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
