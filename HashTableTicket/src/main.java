
import java.io.IOException;


/**
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on 03-12-2014
 * Contributors: 
 */
public class main {

    public static void main(String[] args) throws IOException {
        long time1, time2;
        int NUM_OF_TICKETS = 800;
        TicketHashTable tht = new TicketHashTable();
//        TicketList tl = new TicketList();
//        int cNum = 1337;
        /*Create the Tickets */
        Ticket[] arr = new Ticket[NUM_OF_TICKETS];
        for (int i = 0; i != NUM_OF_TICKETS; ++i) {
            Ticket t = new Ticket();
            int cusNum =  i + (int)(Math.random() * 1000);
            t.createTicket(1, "2014-12-02 16:00:00", 2400, 1, 2, cusNum);
            arr[i] = t;
        }
        
        /* -- Merge testing -- */
        // Add half of tickets to one table, half to the other.
        TicketHashTable tht2 = new TicketHashTable();
        for (int i = 0; i != NUM_OF_TICKETS; ++i) {
            if (i < (NUM_OF_TICKETS / 2)) {
                tht.add(arr[i]);
            } else {
                tht2.add(arr[i]);
            }
        }
        boolean allSuccess = true;
        time1 = System.nanoTime();
        allSuccess = tht.mergeWith(tht2);
        time2 = System.nanoTime();
        if (allSuccess) {
            System.out.println("Merge was 100% successfull.");
        } else {
            System.out.println("Some tickets were skipped in the merge.");
        }
        System.out.println("Time for the merge was " + (time2 - time1) + " ns");
        System.out.println("Size after merge is " + tht.size());
        
        /* -- Add all Tickets -- */  
//        time1 = System.nanoTime();
//        for (Ticket t : arr) {
//            tht.add(t);
//        }
//        time2 = System.nanoTime();
//        System.out.println("HASH Time for adding all: " + (time2 - time1) + " ns");
//        System.out.println("Number of collisions is " + tht.collisions);
        
//        time1 = System.nanoTime();
//        for (Ticket t : arr) {
//            tl.addSingleTicket(t);
//        }
//        time2 = System.nanoTime();
//        System.out.println("LIST Time for adding 800: " + (time2 - time1) + " ns");
        
//        /* -- Serialized size test -- */
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(bos);
//        oos.writeObject(tht);
//        byte[] bArr = bos.toByteArray();
//        System.out.println("Size of serialized object is " + bArr.length); 

//        /* -- Get Ticket for 1337 -- */
//        time1 = System.nanoTime();
//        if (tht.getTicketFor(cNum) != null) {
//            System.out.println("FOUND TICKET!");
//        } else {
//            System.out.println("No ticket found. :(");
//        }
//        time2 = System.nanoTime();
//        System.out.println("HASH Time for finding the ticket: " + (time2 - time1) + " ns");
//        
//        time1 = System.nanoTime();
//        if (tl.getTicket(cNum) != null) {
//            System.out.println("FOUND TICKET!");
//        } else {
//            System.out.println("No ticket found. :(");
//        }
//        time2 = System.nanoTime();
//        System.out.println("LIST Time for finding the ticket: " + (time2 - time1) + " ns");
    }
}
