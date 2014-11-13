/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package BuisnessLogic;

import java.util.ArrayList;
import java.util.Arrays;
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
public class PassengerListTest {

    private PassengerList instance;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }


    /**
     * Method run before each test-method call, initializing field PassengerList
     * variable.
     */
    @Before
    public void setUp() {
        instance = new PassengerList();
        instance.addSinglePassenger(1);
        instance.addSinglePassenger(2);
        instance.addSinglePassenger(3);
        instance.addSinglePassenger(4);

    }

    /**
     * Method run after each test-method call, setting field PasengeList
     * variable to NULL.
     */
    @After
    public void tearDown() {
        instance = null;
    }

    /**
     * Test of getAllPassengers method, of class PassengerList.
     */
    @Test
    public void testGetAllPassengers() {
        System.out.println("getPassengers");
        ArrayList<Integer> expResult = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

        ArrayList<Integer> result = instance.getAllPassengers();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDuplicatePassengers method, of class PassengerList.
     */
    @Test
    public void testGetDuplicatePassengers() {
        System.out.println("getDuplicatePassengers");
        PassengerList other = new PassengerList();
        PassengerList expResult = new PassengerList();

        // Add test data to lists
        ArrayList<Integer> listOther = new ArrayList<>();
        listOther.add(1);
        listOther.add(3);
        listOther.add(8);
        other.setAllPassengers(listOther);
        ArrayList<Integer> expectedResult = new ArrayList<>();
        expectedResult.add(1);
        expectedResult.add(3);
        expResult.setAllPassengers(expectedResult);
        // Execute test
        PassengerList result = instance.getDuplicatePassengers(other);
        assertEquals(expResult, result);
    }

    /**
     * Test of setAllPassengers method, of class PassengerList.
     */
    @Test
    public void testSetAllPassengers() {
        System.out.println("setMultiplePassengers");
        ArrayList<Integer> passengers = new ArrayList<>();
        ArrayList<Integer> expResult = new ArrayList<>();
        passengers.add(9);
        expResult.add(9);
        instance.setAllPassengers(passengers);
        ArrayList<Integer> result = instance.getAllPassengers();
        assertEquals(expResult, result);
    }

    /**
     * Test of addSinglePassenger method, of class PassengerList.
     */
    @Test
    public void testAddSinglePassenger() {
        System.out.println("addSinglePassenger");
        // Override to only contain only one element
        ArrayList<Integer> passengers = new ArrayList<>();
        passengers.add(1);
        instance.setAllPassengers(passengers);
        // add second element and verify that there are two
        instance.addSinglePassenger(7);
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(1);
        expResult.add(7);

        ArrayList<Integer> result = instance.getAllPassengers();
        assertEquals(expResult, result);
    }

    /**
     * Test of size method, of class PassengerList.
     */
    @Test
    public void testGetPassengerAmount() {
        System.out.println("getPassengerAmount");
        int expResult = 4;
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class PassengerList. Is ignored due to auto
     * generated method for hash code calculation.
     */
    @Test
    @Ignore
    public void testHashCode() {
        System.out.println("hashCode");
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("This test is a prototype.");
    }

    /**
     * Test of equals method, of class PassengerList.
     */
    @Test
    public void testEquals() {
        System.out.println("equals");
        PassengerList passengers = new PassengerList();
        passengers.addSinglePassenger(4);
        passengers.addSinglePassenger(2);
        passengers.addSinglePassenger(1);
        passengers.addSinglePassenger(3);
        Object obj = passengers;
        boolean expResult = true;
        boolean result = instance.equals(obj);
        assertEquals(expResult, result);
    }

    /**
     * Test of size method, of class PassengerList.
     */
    @Test
    public void testSize() {
        System.out.println("size");

        int expResult = 4;
        int result = instance.size();
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class PassengerList.
     */
    @Test
    public void testIsempty() {
        System.out.println("isempty");
        
        boolean expResult = false;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of removeSinglePassenger method, of class PassengerList.
     */
    @Test
    public void testRemoveSinglePassenger() {
        System.out.println("removeSinglePassenger");
        
        ArrayList<Integer> expResult = new ArrayList<>();
        expResult.add(2);
        expResult.add(3);
        expResult.add(4);
        
        Integer customerNumber = 1;
        instance.removeSinglePassenger(customerNumber);
        ArrayList<Integer> result = instance.getAllPassengers();
        assertEquals(expResult, result);
    }

    /**
     * Test of isEmpty method, of class PassengerList.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        boolean expResult = false;
        boolean result = instance.isEmpty();
        assertEquals(expResult, result);
    }

    /**
     * Test of getIsInNewZone method, of class PassengerList.
     */
    @Test
    public void testGetIsInNewZone() {
        System.out.println("getIsInNewZone");
        boolean expResult = false;
        boolean result = instance.getIsInNewZone();
        assertEquals(expResult, result);
    }

    /**
     * Test of setIsInNewZone method, of class PassengerList.
     */
    @Test
    public void testSetIsInNewZone() {
        System.out.println("setIsInNewZone");
        instance.setIsInNewZone();
        boolean expResult = true;
        boolean result = instance.getIsInNewZone();
        assertEquals(expResult, result);
    }

}
