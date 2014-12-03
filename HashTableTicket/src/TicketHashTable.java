
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;




/**
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on Dec 2, 2014
 * Contributors: 
 */
public class TicketHashTable implements Serializable {

    /**Arbitrary prime that is above the expected maximum size that is 800*/
    private final int TABLE_SIZE = 1601;     
    
    private final Ticket[] table = new Ticket[TABLE_SIZE];
    private int tableSize = 0;
    
    public int collisions = 0;
   
    
    public boolean add(Ticket t) {
        int key = t.customerHash() % TABLE_SIZE;
        return insert(key, t);
    }
    
    public Ticket getTicketFor(int customerNumber) {
        int searchKey = customerNumber * 31 % TABLE_SIZE;
        int properKey = find(searchKey, customerNumber);
        if (properKey != -1) {
            return table[properKey];
        } else {
            return null;
        }
    }
    
    public int size() {
        return tableSize;
    }
    
    private boolean insert(int key, Ticket value) {
        int originalKey = key;
        /*Look for first empty cell, but avoid infinite loop if none exists*/
        while (table[key] != null && (key + 1) != originalKey) {
            /*Wrap table at max index*/
            key = (key != TABLE_SIZE - 1) ? ++key : 0;
            ++collisions;
        }
        if (table[key] == null) {
            table[key] = value;
            ++tableSize;
            return true;
        } else {
            return false;
        }
    }
    
    private int find(int key, int targetCustomer) {
        int originalKey = key;
        /*Look for first empty cell, but avoid infinite loop if none exists*/
        while (table[key] != null && (key + 1) != originalKey) {
            if (table[key].getCustomerNumber() == targetCustomer) {
                return key;
            } else {
                /*Wrap table at max index*/
                key = (key != TABLE_SIZE - 1) ? ++key : 0;
            }
        }
        /*Ticket not found. Return invalid index*/
        return -1;
    }
    
    
    public static void main(String[] args) throws IOException {
        long time1, time2;
        TicketHashTable tht = new TicketHashTable();
        TicketList tl = new TicketList();
        int cNum = 1337;
        Ticket[] arr = new Ticket[800];
        
        for (int i = 0; i != 800; ++i) {
            Ticket t = new Ticket();
            int cusNum =  i + (int)(Math.random() * 1000);
            t.createTicket(1, "2014-12-02 16:00:00", 2400, 1, 2, cusNum);
            arr[i] = t;
        }
        
        /* -- Add 800 Tickets -- */
        
        time1 = System.nanoTime();
        for (Ticket t : arr) {
            tht.add(t);
        }
        time2 = System.nanoTime();
        System.out.println("HASH Time for adding 800: " + (time2 - time1) + " ns");
        System.out.println("Number of collisions is " + tht.collisions);
        
        time1 = System.nanoTime();
        for (Ticket t : arr) {
            tl.addSingleTicket(t);
        }
        time2 = System.nanoTime();
        System.out.println("LIST Time for adding 800: " + (time2 - time1) + " ns");
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(tht);
        byte[] bArr = bos.toByteArray();
        System.out.println("Size of serialized object is " + bArr.length); 

//        /* -- Get Ticket for 1337 -- */
//        
//        time1 = System.nanoTime();
//        if (tht.getTicketFor(cNum) != null) {
//            System.out.println("FOUND TICKET!");
//        } else {
//            System.out.println("No ticket found. :(");
//        }
//        time2 = System.nanoTime();
//        
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
