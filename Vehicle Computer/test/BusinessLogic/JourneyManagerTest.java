package BusinessLogic;

import java.rmi.RemoteException;



/**
 * Custom test class for JourneyManager.
 * <p>
 * @author Andreas Stensig Jensen, on 04-11-2014
 * Contributors:
 */
public class JourneyManagerTest {


    public static void main(String[] args) throws RemoteException {
        // Instansiate test classes with bypassing of RMI registry
        JourneyManagerRMITestImpl rmiImpl = new JourneyManagerRMITestImpl();
        JourneyManager journeyMan = new JourneyManager(rmiImpl);
        // Test-passenger list
        int[] customerNumbers = {3, 4, 6};
        PassengerList passengers = new PassengerList(5);
        passengers.addSinglePassenger(customerNumbers[0]);
        passengers.addSinglePassenger(customerNumbers[1]);
        passengers.addSinglePassenger(customerNumbers[2]);

        // Run test and ensure that three tickets are returned, as expected
        TicketList tickets = journeyMan.generateTickets(passengers);
        if (tickets.size() == 3) {
            if (tickets.getTicket(customerNumbers[0]) != null) {
                System.out.println("Success for customer " + customerNumbers[0]);
            }
            if (tickets.getTicket(customerNumbers[1]) != null) {
                System.out.println("Success for customer " + customerNumbers[1]);
            }
            if (tickets.getTicket(customerNumbers[2]) != null) {
                System.out.println("Success for customer " + customerNumbers[2]);
            }
        } else {
            System.err.println("Error, there should be three tickets.");
        }
    }

}
