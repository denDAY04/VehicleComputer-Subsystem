package VehicleServer;


/**
 * Interface for external signals coming from other systems of the vehicle.
 * <p>
 * @author Andreas Stensig Jensen on Oct 30, 2014
 * Contributors:
 */
public interface ExternalVehicleSignals {

    /**
     * Expected to be triggered a few seconds after leaving a station.
     */
    public void leftStation();

    /**
     * Expected to be triggered upon moving from one zone to another.
     * <p>
     * @param zoneEntered the zone number of the newly entered zone.
     */
    public void zoneTransit(int zoneEntered);
}
