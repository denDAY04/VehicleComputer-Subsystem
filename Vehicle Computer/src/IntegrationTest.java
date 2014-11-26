

import Database.JourneyManagerRMISkel;
import java.io.IOException;
import java.net.DatagramSocket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import BusinessLogic.PassengerList;
import BusinessLogic.UDPTrafficManager;

/**
 * Integration testing of the different classes' abilities to work together to
 * solve a task.
 * <p>
 * @author Andreas Stensig Jensen, on Nov 11, 2014
 * Contributors:
 */
public class IntegrationTest extends Thread {

    private static DatagramSocket socket;
    private static Registry registry;

    @Override
    public void run() {
        // Start UDPTrafficManager's main()
        String rmiHost = "localhost";
        String rmiPort = "5708";
        String rmiImplName = "jmImpl";
        String[] tmArgs = {rmiHost, rmiPort, rmiImplName};
        UDPTrafficManager.main(tmArgs);        
    }

    /**
     * Generate a short list of passengers for testing.
     * <p>
     * @return a list of passengers with customer numbers 3, 4, and 6.
     */
    public static PassengerList generatePassList() {
        PassengerList passengers = new PassengerList(5);
        int[] customerNumbers = {3, 4, 6};
        for (int i : customerNumbers) {
            passengers.addSinglePassenger(i);
        }
        return passengers;
    }

    /**
     * Opens an RMI registry for testing.
     * <p>
     * @param port the port on which to create the registry.
     * @param implClass the implementation class implementing the
     * <code>JourneyManagerRMISkel</code> interface, to be put in the registry.
     * @param className the name that should be bound to the 
     * <code>impleClass</code> in the registry. Used for looking up the class.
     * <p>
     * @throws RemoteException if the registry could not be created, or the 
     * implementation class not bound. 
     */
    private static void openRMIRegistry(int port, JourneyManagerRMISkel implClass, String className) throws RemoteException {
        registry = LocateRegistry.createRegistry(port);
        registry.rebind(className, implClass);
    }
    
    
    /**
     * Test integration of the individual models to perform a complete task of 
     * generating a <code>TicketList</code> out of a <code>PassengerList</code>.
     * This request for service is done by three clients virtually at the same 
     * time (through separate threads) in order to test concurrency in the 
     * system. 
     * @param args N/A
     * @throws IOException If an I/O exception occurs. 
     */
    public static void main(String[] args) throws  IOException {        
        socket = new DatagramSocket();

        // Create RMI registry 
        int port = 5708;
        JourneyManagerRMITestImpl rmiImpl = new JourneyManagerRMITestImpl();
        String name = "jmImpl";
        openRMIRegistry(port, rmiImpl, name);        
        
        // Run UDPTrafficManager in seperate thread through this <code>Class</code>
        IntegrationTest test = new IntegrationTest();
        test.start();
    }

}
