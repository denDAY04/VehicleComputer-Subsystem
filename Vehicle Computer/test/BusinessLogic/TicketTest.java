/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BusinessLogic;

import ModelClasses.Ticket;
import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 * @author AndreasStensig
 */
public class TicketTest {

    Ticket instance;


    /**
     * Method run before each test-method call, initializing field Ticket
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

        instance = new Ticket();
        instance.createTicket(number, time, price, startZone, validZones,
                                                              custommerNumber);
    }

    /**
     * Method run after each test-method call, setting field Ticket variable to
     * NULL.
     */
    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of createTicket method, of class Ticket.
     */
    @Test
    public void testCreateTicket() {
        System.out.println("createTicket");
        int number = 10;
        String time = "2014-10-31 10:06:24";
        int price = 1500;
        int startZone = 4;
        int validZones = 2;
        int custommerNumber = 1;
        Ticket expResutl = new Ticket();
        expResutl.createTicket(number, time, price, startZone, validZones,
                                                               custommerNumber);
        assertEquals(expResutl, instance);
    }

    /**
     * Test of getNumber method, of class Ticket.
     */
    @Test
    public void testGetNumber() {
        System.out.println("getNumber");
        int expResult = 10;
        int result = instance.getNumber();
        assertEquals(expResult, result);
    }

    /**
     * Test of getTimestamp method, of class Ticket.
     */
    @Test
    public void testGetTimestamp() {
        System.out.println("getTimestamp");
        String expResult = "2014-10-31 10:06:24";
        String result = instance.getTimestamp();
        assertEquals(expResult, result);
    }

    /**
     * Test of setTimestamp method, of class Ticket.
     */
    @Test
    public void testSetTimestamp() {
        System.out.println("setTimestamp");
        String timestamp = "2014-10-31 12:00:00";
        instance.setTimestamp(timestamp);
        String result = instance.getTimestamp();
        assertEquals(timestamp, result);
    }

    /**
     * Test of getPrice method, of class Ticket.
     */
    @Test
    public void testGetPrice() {
        System.out.println("getPrice");
        int expResult = 1500;
        int result = instance.getPrice();
        assertEquals(expResult, result);
    }

    /**
     * Test of setPrice method, of class Ticket.
     */
    @Test
    public void testSetPrice() {
        System.out.println("setPrice");
        int price = 60000;
        instance.setPrice(price);
        int result = instance.getPrice();
        assertEquals(price, result);
    }

    /**
     * Test of getStartZone method, of class Ticket.
     */
    @Test
    public void testGetStartZone() {
        System.out.println("getStartZone");
        int expResult = 4;
        int result = instance.getStartZone();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCustomerNumber method, of class Ticket.
     */
    @Test
    public void testGetCustomerNumber() {
        System.out.println("getCustomerNumber");
        int expResult = 1;
        int result = instance.getCustomerNumber();
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class Ticket.
     * Is ignored due to hashCode method being auto generated.
     */
    @Test
    @Ignore
    public void testHashCode() {
        System.out.println("hashCode");
        Ticket instance = new Ticket();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of equals method, of class Ticket.
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
        Ticket other = new Ticket();
        other.createTicket(number, time, price, startZone, validZones,
                                                           custommerNumber);

        Object obj = other;
        boolean expResult = true;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
    }

}
