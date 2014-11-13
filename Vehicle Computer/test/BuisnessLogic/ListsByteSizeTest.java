package BuisnessLogic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


/**
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on 08-11-2014
 * Contributors: 
 */
public class ListsByteSizeTest {

    private static final int MAX_LIST_SIZE = 800;
    
    
    public static void main(String[] args) throws IOException {
        PassengerList passList = new PassengerList();
        TicketList tickList = new TicketList();
        ArrayList<Integer> passengers = new ArrayList<>();
        ArrayList<Ticket> tickets = new ArrayList<>();
        
        // generate data and populate lists
        for (int i = 1; i != MAX_LIST_SIZE; ++i) {
            passengers.add(i);
            
            int number = (i + 1);
            String time = "2014-10-31 10:06:24";
            int price = 2400;
            int startZone = 4;
            int custommerNumber = i;
            Ticket ticket = new Ticket();
            ticket.createTicket(number, time, price, startZone,
                                custommerNumber);
            tickets.add(ticket);
        }
        passList.setAllPassengers(passengers);
        tickList.setAllTickets(tickets);
        
        // find byte size
        ByteArrayOutputStream passBOS = new ByteArrayOutputStream();
        ByteArrayOutputStream tickBOS = new ByteArrayOutputStream();
        ObjectOutputStream passOOS = new ObjectOutputStream(passBOS);
        ObjectOutputStream tickOOS = new ObjectOutputStream(tickBOS);
        // PassengerList size
        passOOS.writeObject(passList);
        int passBuffSize = passBOS.size();
        // TicketList size
        tickOOS.writeObject(tickList);
        int tickBuffSize = tickBOS.size();
        
        System.out.println("PassengerList size: " + passBuffSize);
        System.out.println("TicketList size: " + tickBuffSize);
    }
}
