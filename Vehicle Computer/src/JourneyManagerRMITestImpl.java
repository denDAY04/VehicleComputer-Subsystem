

import RMIInterfaces.JourneyManagerRMISkel;
import ModelClasses.PassengerList;
import ModelClasses.Ticket;
import ModelClasses.TicketList;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


/**
 * Generic stand-in class for the RMI implementation class of the RMI
 * skeleton in the registry.
 * 
 * @author Andreas Stensig Jensen, on 04-11-2014
 * Contributors: 
 */
public class JourneyManagerRMITestImpl extends UnicastRemoteObject implements JourneyManagerRMISkel {

    TicketList tickets = null;

    public JourneyManagerRMITestImpl() throws RemoteException{
        // Create tickets to put into TicketList
        int[] numbers = {4, 5};
        String[] times = {"2014-10-31 00:00:01", "2014-10-31 00:00:02"};
        int[] prices = {1400, 1000};
        int[] startZones = {1, 2};
        int[] validZones = {2, 2};
        int[] customerNumbers = {3, 4};

        Ticket ticket1 = new Ticket();
        Ticket ticket2 = new Ticket();
        ticket1.createTicket(numbers[0], times[0], prices[0], startZones[0],
                             validZones[0],customerNumbers[0]);
        ticket2.createTicket(numbers[1], times[1], prices[1], startZones[1],
                             validZones[1], customerNumbers[1]);
        Ticket ticket3 = new Ticket();
        ticket3.createTicket(6, "2014-11-16 00:00:03",1200, 1, 2, 1337);
        
        tickets = new TicketList();
        tickets.addSingleTicket(ticket1);
        tickets.addSingleTicket(ticket2);
        tickets.addSingleTicket(ticket3);

    }

    /**
     * Generically returns a TicketList with two tickets in it.
     * <p>
     * @param passengers
     * <p>
     * @return
     * <p>
     * @throws RemoteException
     */
    @Override
    public TicketList getExistingTickets(PassengerList passengers) throws
            RemoteException {
        simulateDelay();
        return tickets;
    }

    /**
     * Generically returns a TicketList with one ticket in it.
     * <p>
     * @param passengers
     * <p>
     * @return
     * <p>
     * @throws RemoteException
     */
    @Override
    public TicketList createNewTickets(PassengerList passengers) throws
            RemoteException {
        int number = 11;
        String time = "2014-10-31 10:10:10";
        int price = 2200;
        int startZone = 5;
        int zoneCount = 2;
        int custommerNumber = 6;

        Ticket ticket = new Ticket();
        ticket.createTicket(number, time, price, startZone, zoneCount,
                            custommerNumber);
        TicketList ticketList = new TicketList();
        ticketList.addSingleTicket(ticket);
        simulateDelay();
        return ticketList;
    }

    /**
     * Simulation of some measure of delay which would be present in the real
     * implementation with RMI accessing a database in the end system.
     * <p>
     * Delay should be around 4 to 6 ms. 
     */
    private void simulateDelay() {
        System.out.println("Delay simulation");
        for (int i = 0; i != 10000000; ++i) {
            // Do nothing. 
        }
    }
    
}
