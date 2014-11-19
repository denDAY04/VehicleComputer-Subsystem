package BuisnessLogic;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;


/**
 *
 * @author Stensig
 */
public class JourneyManager {

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
     * Creates a TicketList of Ticket objects for all passengers in the
     * argument.
     * <p>
     * @param passengers list of the passengers for which to return tickets.
     * <p>
     * @return a TicketList object with all of the tickets for the passengers.
     */
    public TicketList generateTickets(PassengerList passengers) {                   // called from vehicle/UDP 
        TicketList reply = null;
        if (passengers == null) {
            return reply;
        }

        /*
         Get already-existing tickets for passengers and filter passengers 
         that are without a ticket. 
         */
        reply = checkTickets(passengers);
        PassengerList newPassengers = getNewPassengers(passengers,
                                                       reply);

        // Cet missing tickets from server and merge with existing tickets
        TicketList newTickets = null;
        try {
            newTickets = rmiJourneyMan.createNewTickets(newPassengers);
        } catch (RemoteException ex) {
            ex.printStackTrace();
        }
        reply.mergeWith(newTickets);
//        TicketList reply = new TicketList();
//        for (int i = 0; i != 799; ++i) {
//            int number = 4;
//            String time = "2014-10-31 00:00:01";
//            int price = 1200;
//            int startZone = 1;
//            int customerNumber = 3;
//            Ticket ticket = new Ticket();
//            ticket.createTicket(number, time, price, startZone, customerNumber);
//            reply.addSingleTicket(ticket);
//        }
//        int number = 4;
//        String time = "2014-10-31 00:00:01";
//        int price = 1200;
//        int startZone = 5;
//        int customerNumber = 1337;
//        Ticket ticket = new Ticket();
//        ticket.createTicket(number, time, price, startZone, customerNumber);
//        reply.addSingleTicket(ticket);      
//        System.out.println("JourneyMan: Generated large TicketList");
        
        
        return reply;
    }
    
    /**
     * Get a list of tickets that are already active for the supplied list of
     * passengers.
     * <p>
     * @param passengers the list of passengers onboard a vehicle.
     * <p>
     * @return a list of active tickets for the passengers, or NULL if an error
     *         occurred.
     */
    private TicketList checkTickets(PassengerList passengers) {
        try {
            return rmiJourneyMan.getExistingTickets(passengers);
        } catch (RemoteException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Returns a list of the passengers (PassengerList object) from the 1st
     * argument who does not have a ticket in the list of tickets given in the
     * 2nd argument.
     * <p>
     * @param passengers the list of passengers onboard the vehicle.
     * @param tickets    the list of currently active tickets.
     * <p>
     * @return a list of passengers that are missing tickets.
     */
    private PassengerList getNewPassengers(PassengerList passengers,
                                           TicketList tickets) {
        PassengerList newPassengers = passengers;
        ArrayList<Integer> passengersArr = passengers.getAllPassengers();

        for (Ticket t : tickets.getAllTickets()) {
            int customer = t.getCustomerNumber();
            /*
             If there is a match between a ticket's customer number and a 
             passenger in the list, remove said passenger from the new list. 
             */
            if (passengersArr.contains(customer)) {
                newPassengers.removeSinglePassenger(customer);
            }
        }

        return newPassengers;
    }

}
