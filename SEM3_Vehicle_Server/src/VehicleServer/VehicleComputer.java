package VehicleServer;


import ModelClasses.PassengerList;
import ModelClasses.TicketList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * The backbone - main class - of the systems running on the vehicle. Maintains
 * awareness of the customers on board the vehicle and their tickets, and
 * distributes ping, pong, and ticket-preview requests from the passengers to
 * other classes.
 * <p>
 * @author Andreas Stensig Jensen, on 01-11-2014
 * Contributors:
 */
public class VehicleComputer extends Thread implements ExternalVehicleSignals {

    private final String BACKUP_FILE_NAME = "vc_backup.txt";
    private final int QUEUE_SIZE = 20;
    private final int CORE_POOL_SIZE = 10;
    private final int MAX_POOL_SIZE = 20;

    /**
     * Time for an excessive thread to exist, in seconds, before being closed
     */
    private final int EXCESS_POOL_TIMEOUT = 30;

    private int currentZone = 1;
    private final int pongPort = 2223;
    private final int trafficManTargetPort = 2408;
    private PassengerList pingedPassengers;
    private PassengerList activePassengers;
    private TicketList tickets;
    private UDPUplinkHandler uplinkHandler;
    private UDPDownlinkHandler downlinkHandler;
    private UDPPingSender pingSender;


    /**
     * Constructor. Will try and read passenger and tickets from a local
     * file, if such exists.
     * <p>
     * @param startZone      the zone in which the vehicle is placed at the time
     *                       of program startup.
     * @param uplinkPort     the port number for the Uplink handler to the
     *                       business logic backend.
     * @param trafficManAddr the host name address for the
     *                       <code>UDPTrafficManager</code> to which this system
     *                       must communicate.
     */
    public VehicleComputer(String startZone, String uplinkPort,
                           String trafficManAddr) {
        try {
            currentZone = Integer.parseInt(startZone);
            /*Load passengers and tickets from backup, or manual initialize*/
            if (!readBackup()) {
                activePassengers = null;
                tickets = new TicketList();
            }
            pingedPassengers = new PassengerList(currentZone);
            uplinkHandler = new UDPUplinkHandler(this, uplinkPort,
                                                 trafficManTargetPort,
                                                 trafficManAddr);
            downlinkHandler = new UDPDownlinkHandler(this);
        } catch (NumberFormatException | UnknownHostException |
                 SocketException ex) {
            System.err.println("Fatal error in VehicleComputer setup.");
            ex.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Non-terminating <code>run()</code> method that maintains a
     * <code>ThreadPool</code> with worker threads for handling ping replies
     * from client PDAs on-board the vehicle.
     */
    @Override
    public void run() {
        downlinkHandler.start();

        /*Create a thead pool with a blocking queue*/
        BlockingQueue<Runnable> blockQueue
                = new ArrayBlockingQueue<>(QUEUE_SIZE);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                                                             MAX_POOL_SIZE,
                                                             EXCESS_POOL_TIMEOUT,
                                                             TimeUnit.SECONDS,
                                                             blockQueue);
        executor.setRejectedExecutionHandler(new RejectPongExecuteHandler());

        /*Open socket for pongs and prestart executor threads*/
        DatagramSocket pongSocket = null;
        try {
            pongSocket = new DatagramSocket(pongPort);
        } catch (SocketException ex) {
            System.err.println("Could not open pong socket. Restart required.");
            ex.printStackTrace();
            System.exit(-1);
        }
        executor.prestartAllCoreThreads();

        /*Listen for pongs and distribute them to executor threads*/
        while (true) {
            DatagramPacket packetIn = new DatagramPacket(new byte[256], 256);
            try {
                pongSocket.receive(packetIn);
                executor.execute(new UDPPongHandler(this, packetIn));
            } catch (IOException ex) {
                System.err.println("Could not receive pong packet; dropping.");
                ex.printStackTrace();
            }
        }
    }

    /**
     * Interface method implementation to an external signal (or simulation)
     * indicating that the vehicle has left the station/stop. This initiate
     * pinging of <code>PDAApplication</code> clients in the vicinity of the
     * vehicle.
     */
    @Override
    public void leftStation() {
        System.out.println("VC: Left station");
        try {
            pingSender = new UDPPingSender(this);
            pingSender.start();
        } catch (SocketException ex) {
            System.err.println(
                    "Fatal error in VehicleComputer ping initiation.");
            ex.printStackTrace();
            System.exit(-1);
        }

    }

    /**
     * Interface method implementation to an external signal (or simulation)
     * indicating that the vehicle has transitioned from one zone to another.
     * <p>
     * @param zoneEntered the zone number of the newly entered zone.
     */
    @Override
    public void zoneTransit(int zoneEntered) {
        System.out.println("VC: Zone transit into " + zoneEntered);
        currentZone = zoneEntered;
        
        /*
         If pings have been performed, change zone of active list and 
         request new tickets. Else set zone of pinged list and initate pinging,
         which terminates in getting tickets. 
        */
        if (activePassengers != null) {
            activePassengers.setZone(currentZone);
            requestTickets();
        } else {
            System.out.println("VC: missing pings. . . pinging.");
            pingedPassengers.setZone(currentZone);
            leftStation();
        }
    }

    /**
     * Returns a deep-copy of the <code>TicketList</code> field object.
     * <p>
     * @return a copy of the ticket list.
     */
    public TicketList getTickets() {
        return new TicketList(tickets);
    }

    /**
     * Compare the list of pinged passengers with the list of active passengers.
     * The result overrides the list of active passengers, thus keeping that
     * list updated, while the list of pinged passengers is cleared.
     * <p>
     * Test with 800 passengers show an execution time at around seven to eight
     * milliseconds, which is acceptable for being used in-between pings going
     * out with an interval of about a second.
     */
    public void filterPassengers() {
        if (activePassengers == null) {
            /*Copy pinged list into active and reset pinged list*/
            System.out.println("First passenger filter.");
            activePassengers = new PassengerList(pingedPassengers);
            pingedPassengers = new PassengerList(currentZone);
        } else {
            System.out.println("Subsequent passenger filter.");
            /*Store duplicates in active, and reset pinged*/
            activePassengers = pingedPassengers.getDuplicatePassengers(
                    activePassengers);
            pingedPassengers = new PassengerList(currentZone);
        }
    }

    /**
     * Request tickets through the <code>UDPUplinkHandler</code>. Should the
     * result be unsuccessful in getting getting a <code>TicketList</code> the
     * method will retry up to five times before commencing a system-reboot
     * request; being unable to get tickets for its passengers is a fatal error.
     * <p>
     * The tickets are stored in a field variable.
     */
    public void requestTickets() {
        TicketList newTickets = null;
        try {
            /*Try up to five times*/
            for (int i = 0; i != 5 && newTickets == null; ++i) {
                newTickets = uplinkHandler.getTicketList(activePassengers);
            }
        } catch (IOException ex) {
            try {
                /*Retry*/
                newTickets = uplinkHandler.getTicketList(activePassengers);
            } catch (IOException ex1) {
                /*If still not successful, request a restart of the system*/
                systemRestartWarning(ex1);
            }
        }

        /*If tickets are still NULL, request a restart of the system*/
        if (newTickets == null) {
            systemRestartWarning(new NullPointerException());
        }

        tickets = newTickets;
    }

    /**
     * Attempts to store the passengers and tickets fields in a local backup
     * file named by the <code>BACKUP_FILE_NAME</code> field. Afterwards, it
     * exits the system with an output of the stack trace of the cause of the
     * fatal error.
     * <p>
     * @param cause the exception that caused the fatal error.
     */
    public void systemRestartWarning(Exception cause) {
        System.err.println("A fatal error has occoured.");
        cause.printStackTrace();
        try {
            /*Serialize passengers and tickets*/
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(activePassengers);
            oos.flush();
            oos.writeObject(tickets);
            byte[] data = bos.toByteArray();

            /*Write to file*/
            RandomAccessFile raf = new RandomAccessFile(BACKUP_FILE_NAME, "rw");
            raf.write(data);
            raf.close();
            System.out.println("Data saved in backup: " + BACKUP_FILE_NAME);

        } catch (Exception ex) {
            System.err.println("Could not back up data. ");
        }
        System.err.println("Restart of the system is required.");
        System.exit(-1);
    }

    /**
     * Read the <code>PassengerList</code> and <code>TicketList</code> from
     * a backup file, if such exist. Returns the result of trying to re-create
     * data from the file as a boolean.
     * <p>
     * @return true if and only if both of the lists could be read from an
     *         existing file; false otherwise.
     */
    private boolean readBackup() {
        File backup = new File(BACKUP_FILE_NAME);
        if (!backup.exists()) {
            return false;
        }

        try {
            /*Read backup file*/
            RandomAccessFile raf = new RandomAccessFile(backup, "r");
            /*Actual size for 800 passengers and tickets: 21900+8300 = 30200*/
            byte[] buff = new byte[31000];
            raf.read(buff);

            /*Deserialize objects and load into fields*/
            ByteArrayInputStream bis = new ByteArrayInputStream(buff);
            ObjectInputStream ois = new ObjectInputStream(bis);
            activePassengers = (PassengerList) ois.readObject();
            tickets = (TicketList) ois.readObject();
        } catch (FileNotFoundException ex) {
            System.err.println("Could not create file reader.");
            ex.printStackTrace();
            return false;
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println("Could not read file, or could not read data");
            ex.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Add a customer/passenger to the list of pinged passengers.
     * <p>
     * @param CustomerNumber customer number of the passenger.
     */
    public synchronized void addToPassengers(int CustomerNumber) {
        pingedPassengers.addSinglePassenger(CustomerNumber);
    }


    /**
     * Custom <code>RejectedExecutionHandler</code> for handling rejected
     * <code>Runnable</code> tasks for a <code>ThreadPool</code>.
     * <p>
     * This implementation pauses the attempt for 10 milliseconds before trying
     * to execute it again.
     */
    class RejectPongExecuteHandler implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            System.err.println("Rejected from queue. 10 ms sleep");
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
            }
            System.err.println("Retrying");
            executor.execute(r);
        }

    }


    /**
     * Main method for running the <code>VehicleComputer</code> and its
     * associated classes in the Vehicle server.
     * <p>
     * Also reads inputs from the console in order to control simulation
     * of external vehicle sighnals, i.e. entering a new zone, or leaving 
     * station. 
     * <p>
     * @param args
     * <ul>
     * <li>0 : Start zone for the vehicle
     * <li>1 : Port number for socket uplinking to
     * <code>UDPTrafficManager</code>
     * <li>2 : IPv6 literal address or host name for location of
     * <code>UDPTrafficManager</code>.
     * </ul>
     */
    public static void main(String[] args) {
        VehicleComputer vc = new VehicleComputer(args[0], args[1], args[2]);
        vc.start();

        /*Input for external signal simulation*/
        Scanner cin = new Scanner(System.in);
        System.out.println("  External signal-interface. Inputs can be:");
        System.out.println("   leftstation");
        System.out.println("   zonetransit <zone number .. 1 through 5>");
        System.out.println("   quit");
        while (true) {
            switch (cin.nextLine()) {
                case "leftstation":
                    vc.leftStation();
                    break;

                case "zonetransit 1":
                    vc.zoneTransit(1);
                    break;

                case "zonetransit 2":
                    vc.zoneTransit(2);
                    break;

                case "zonetransit 3":
                    vc.zoneTransit(3);
                    break;

                case "zonetransit 4":
                    vc.zoneTransit(4);
                    break;

                case "zonetransit 5":
                    vc.zoneTransit(5);
                    break;

                case "quit":
                    System.exit(1);
                    break;

                default:
                    System.out.println("  Invalid input.");
                    System.out.println("  Can be:");
                    System.out.println("   leftstation");
                    System.out.println("   zonetransit <zone number .. 1 "
                            + "through 5>");
                    System.out.println("   quit");
                    break;
            }
        }
    }
}
