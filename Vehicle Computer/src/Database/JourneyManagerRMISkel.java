package Database;



import BusinessLogic.PassengerList;
import BusinessLogic.TicketList;
import java.rmi.Remote;
import java.rmi.RemoteException;


/**
 * Stub interface for the RMI class-implementation maintained in the RMI
 * registry on the Database backend server.
 * <p>
 * @author Andreas Stensig Jensen on Oct 30, 2014
 * Contributors:
 * Jonas Grum-Schwensen
 */
public interface JourneyManagerRMISkel extends Remote {

    /**
     * Retrieve the existing-and-active tickets that may exist for the
     * passengers given in the argument.
     * <p>
     * @param passengers on-board the vehicle.
     * <p>
     * @return the existing tickets for the passengers
     * <p>
     * @throws RemoteException if an RMI exception occurs.
     */
    TicketList getExistingTickets(PassengerList passengers) throws
            RemoteException;

    /**
     * Create new tickets for the given passengers.
     * <p>
     * @param passengers on-board the vehicle.
     * <p>
     * @return the newly created tickets.
     * <p>
     * @throws RemoteException if an RMI exception occurs.
     */
    TicketList createNewTickets(PassengerList passengers) throws
            RemoteException;
}
