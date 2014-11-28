/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BusinessLogic;

import ModelClasses.Ticket;
import ModelClasses.TicketList;
import java.util.ArrayList;
import org.junit.After; 
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;


/**
 *
 * @author AndreasStensig
 */
public class TicketListTest {

    TicketList instance;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    /**
     * Method run before each test-method call, initializing field TicketList
     * variable.
     */
    @Before
    public void setUp() {
        int number = 10;
        String time = "2014-10-31 10:06:24";
        int price = 1500;
        int startZone = 4;
        int validZones = 2;
        int custommerNumber = 1;

        Ticket ticket = new Ticket();
        ticket.createTicket(number, time, price, startZone, validZones,
                                                            custommerNumber);
        instance = new TicketList();
        instance.addSingleTicket(ticket);
    }

    /**
     * Method run after each test-method call, setting field TicketList variable
     * to NULL.
     */
    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of setAllTickets method, of class TicketList.
     */
    @Test
    public void testSetAllTickets() {
        System.out.println("setAllTickets");

        int number = 10;
        String time = "2014-10-31 10:06:24";
        int price = 1500;
        int startZone = 4;
        int validZones = 2;
        int custommerNumber = 1;
        Ticket ticket = new Ticket();
        ticket.createTicket(number, time, price, startZone, validZones,
                                                            custommerNumber);

        int number2 = 11;
        String time2 = "2015-10-31 10:06:24";
        int price2 = 1501;
        int startZone2 = 5;
        int validZones2 = 2;
        int custommerNumber2 = 2;
        Ticket ticket2 = new Ticket();
        ticket2.createTicket(number2, time2, price2, startZone2, validZones2,
                             custommerNumber2);

        ArrayList<Ticket> tickets = new ArrayList<>();
        tickets.add(ticket);
        tickets.add(ticket2);

        instance.setAllTickets(tickets);
        assertEquals(tickets, instance.getAllTickets());
    }

    /**
     * Test of addSingleTicket method, of class TicketList.
     */
    @Test
    public void testAddSingleTicket() {
        System.out.println("addSingleTicket");

        int number2 = 11;
        String time2 = "2015-10-31 10:06:24";
        int price2 = 1501;
        int startZone2 = 5;
        int validZones2 = 2;
        int custommerNumber2 = 2;
        Ticket ticket2 = new Ticket();
        ticket2.createTicket(number2, time2, price2, startZone2, validZones2,
                             custommerNumber2);

        instance.addSingleTicket(ticket2);
        Ticket result = instance.getTicket(custommerNumber2);
        assertEquals(ticket2, result);
    }

    /**
     * Test of getTicket method, of class TicketList.
     */
    @Test
    public void testGetTicket() {
        System.out.println("getTicket");

        int number = 10;
        String time = "2014-10-31 10:06:24";
        int price = 1500;
        int startZone = 4;
        int validZones = 2;
        int custommerNumber = 1;
        Ticket expResult = new Ticket();
        expResult.createTicket(number, time, price, startZone, validZones,
                                                               custommerNumber);

        Ticket result = instance.getTicket(custommerNumber);
        assertEquals(expResult, result);
    }

    /**
     * Test of getAllTickets method, of class TicketList.
     */
    @Test
    public void testGetAllTickets() {
        System.out.println("getAllTickets");

        int number = 10;
        String time = "2014-10-31 10:06:24";
        int price = 1500;
        int startZone = 4;
        int validZones = 2;
        int custommerNumber = 1;
        Ticket ticket = new Ticket();
        ticket.createTicket(number, time, price, startZone, validZones,
                                                            custommerNumber);

        ArrayList<Ticket> expResult = new ArrayList<>();
        expResult.add(ticket);

        ArrayList<Ticket> result = instance.getAllTickets();
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class TicketList.
     * Is ignored due to method being auto generated in model class.
     */
    @Test
    @Ignore
    public void testHashCode() {
        System.out.println("hashCode");
        TicketList instance = new TicketList();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class TicketList.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");

        int number = 10;
        String time = "2014-10-31 10:06:24";
        int price = 1500;
        int startZone = 4;
        int validZones = 2;
        int custommerNumber = 1;

        Ticket ticket = new Ticket();
        ticket.createTicket(number, time, price, startZone, validZones,
                                                            custommerNumber);
        TicketList other = new TicketList();
        other.addSingleTicket(ticket);

        Object obj = other;
        boolean expResult = true;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
    }

    /**
     * Test of size method, of class TicketList.
     */
    @Test
    public void testSize() {
        System.out.println("size");

        int expResult = 1;
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class TicketList.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");

        boolean expResult = false;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of mergeWith method, of class TicketList.
     */
    @Test
    public void testMergeWith() {
        System.out.println("mergeWith");
        // New ticketlist 
        int number = 9;
        String time = "2014-11-31 10:06:24";
        int price = 150;
        int startZone = 2;
        int validZones = 2;
        int custommerNumber = 3;
        Ticket ticket = new Ticket();
        ticket.createTicket(number, time, price, startZone, validZones,
                                                            custommerNumber);
        TicketList other = new TicketList();
        other.addSingleTicket(ticket);
        
        
        // Original Ticket as part of expected result
        number = 10;
        time = "2014-10-31 10:06:24";
        price = 1500;
        startZone = 4;
        validZones = 2;
        custommerNumber = 1;
        Ticket ticket2 = new Ticket();
        ticket2.createTicket(number, time, price, startZone, validZones, 
                                                             custommerNumber);
        TicketList expResultList = new TicketList();
        expResultList.addSingleTicket(ticket);
        expResultList.addSingleTicket(ticket2);
        
        // Merge TIcketLists and evaluate result
        instance.mergeWith(other);
        boolean expResult = true;
        boolean result = instance.equals(expResultList);
        assertEquals(expResult, result);
    }

}
