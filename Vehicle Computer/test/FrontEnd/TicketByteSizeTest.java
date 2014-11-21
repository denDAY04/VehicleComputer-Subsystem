package FrontEnd;

import BusinessLogic.Ticket;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;


/**
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on 16-11-2014
 * Contributors: 
 */
public class TicketByteSizeTest {

    public static void main(String[] args) throws IOException {
        int number = 774154;
        String time = "2014-10-31 10:06:24";
        int price = 2400;
        int startZone = 4;
        int custommerNumber = 2450;
        Ticket ticket = new Ticket();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(ticket);
        byte[] arr = bos.toByteArray();
        
        System.out.println("Serialized ticket size: " + arr.length);
    }
}
