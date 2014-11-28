package BusinessLogic;

import ModelClasses.PassengerList;
import ModelClasses.Ticket;
import ModelClasses.TicketList;
import RMIInterfaces.JourneyManagerRMISkel;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


/**
 * Manager class for ongoing journeys in the sens that it maintains RMI
 * connectivity to the database backend, thus being able to both get existing
 * tickets and request new ones to be created in the database.
 * <p>
 * In essence, a handler class for both <code>PassengerList</code> and
 * <code>TicketList</code>, with the operations it perform on these.
 * <p>
 * @author Andreas Stensig Jensen on Oct 30, 2014
 * Contributors:
 */
public class JourneyManager {

    /** Stub for the JourneyManager RMI class on the DataBase backend */
    private final JourneyManagerRMISkel rmiJourneyMan;


    /**
     * Constructor that initializes the object's RMI connection and
     * JourneyManagerRMISkel reference.
     * <p>
     * @param rmiHost           host name of the RMI registry.
     * @param rmiPort           port number of the RMI registry.
     * @param rmiJourneyManName name of the JourneyManagerRMIImpl object in the
     *                          RMI Registry.
     * <p>
     * @throws RemoteException   if the registry could not be reached.
     * @throws NotBoundException if the name is not found in the RMI registry.
     */
    public JourneyManager(String rmiHost, int rmiPort, String rmiJourneyManName)
            throws RemoteException, NotBoundException {
        Registry reg = LocateRegistry.getRegistry(rmiHost, rmiPort);
        rmiJourneyMan = (JourneyManagerRMISkel) reg.lookup(rmiJourneyManName);
    }

    /**
     * Test constructor, bypassing the need for an RMI registry to be
     * present by supplying the implementation class directly
     * <p>
     * @param rmiImpl the implementation class of the RMI skeleton, which would
     *                normally be placed in a registry.
     */
    public JourneyManager(JourneyManagerRMISkel rmiImpl) {
        rmiJourneyMan = rmiImpl;
    }

    /**
     * Creates a <code>TicketList</code> of <code>Ticket</code> objects for all
     * passengers in the argument.
     * <p>
     * @param passengers list of the passengers for which to return tickets.
     * <p>
     * @return a <code>TicketList</code> object with all of the tickets for the
     *         passengers.
     */
    public TicketList generateTickets(PassengerList passengers) {
        TicketList reply = null;
        if (passengers == null) {
            return reply;
        }

        /*
         Get already-existing tickets for passengers and filter passengers 
         that are without a ticket. 
         */
        reply = checkTickets(passengers);
        PassengerList newPassengers = getNewPassengers(passengers, reply);

        /*Get missing tickets from server and merge with existing tickets*/
        try {
            TicketList newTickets = rmiJourneyMan.
                    createNewTickets(newPassengers);
            reply.mergeWith(newTickets);
        } catch (RemoteException ex) {
            System.err.println(
                    "JM: Could not generate new tickets. Returning NULL.");
            ex.printStackTrace();
        }

        return reply;
    }

    /**
     * Get a list of tickets that are already active for the supplied list of
     * passengers.
     * <p>
     * @param passengers the list of passengers on-board a vehicle.
     * <p>
     * @return a list of active tickets for the passengers, or NULL if an error
     *         occurred.
     */
    private TicketList checkTickets(PassengerList passengers) {
        try {
            return rmiJourneyMan.getExistingTickets(passengers);
        } catch (RemoteException ex) {
            System.err.println(
                    "JM: Error in getting existing tickets. Returning NULL.");
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a list of the passengers from the 1st argument who does not have 
     * a ticket in the list of tickets given as the 2nd argument.
     * <p>
     * @param passengers the list of passengers on-board the vehicle.
     * @param tickets    the list of currently active tickets.
     * <p>
     * @return a list of passengers that are missing tickets.
     */
    private PassengerList getNewPassengers(PassengerList passengers,
                                           TicketList tickets) {
        PassengerList newPassengers = passengers;
        ArrayList<Integer> passengersArr = passengers.getAllPassengers();

        /*
         If there is a match between a ticket's customer number and a 
         passenger in the list, remove said passenger from the new list. 
        */
        for (Ticket t : tickets.getAllTickets()) {
            int customer = t.getCustomerNumber();
            if (passengersArr.contains(customer)) {
                newPassengers.removeSinglePassenger(customer);
            }
        }

        return newPassengers;
    }

}
