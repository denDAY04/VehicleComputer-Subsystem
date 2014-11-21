package BusinessLogic;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 *
 * @author Stensig
 */
public interface JourneyManagerRMISkel extends Remote{
    TicketList getExistingTickets(PassengerList passengers) throws RemoteException;
    TicketList createNewTickets(PassengerList passengers) throws RemoteException;
}
