package ModelClasses;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Model class containing an ArrayList of passengers on board a vehicle,
 * represented by their customer number.
 * <p>
 * @author Andreas Stensig Jensen on Oct 30, 2014
 * Contributors:
 * Jonas Grum-Schwensen
 */
public class PassengerList implements Serializable {

    /**The arbitrary maximum number of passengers on-board a single vehicle.*/
    private final int MAX_SIZE = 800;

    /**Passengers on-board the vehicle.*/
    private ArrayList<Integer> passengers;

    /**The zone that the passengers of the list are in.*/
    private int zone;


    /**
     * Constructor.
     * <p>
     * @param zone the zone number of the zone the passengers are in.
     */
    public PassengerList(int zone) {
        passengers = new ArrayList<>(MAX_SIZE);
        this.zone = zone;
    }

    /**
     * Copy constructor.
     * <p>
     * @param other <code>PassengerList</code> to copy from.
     */
    public PassengerList(PassengerList other) {
        passengers = other.passengers;
    }

    /**
     * Get all the passengers in the list.
     * <p>
     * @return all the passengers in the list.
     */
    public ArrayList<Integer> getAllPassengers() {
        return passengers;
    }

    /**
     * Get the amount of passengers in the list.
     * <p>
     * @return the amount of passengers in the list.
     */
    public int size() {
        return passengers.size();
    }

    /**
     * See if list contains no passengers.
     * <p>
     * @return true if no tickets are stored; false otherwise.
     */
    public boolean isEmpty() {
        return passengers.isEmpty();
    }

    /**
     * Get the zone number of the passenger list.
     * <p>
     * @return the zone number
     */
    public int getZone() {
        return zone;
    }

    /**
     * Set the zone number.
     * <p>
     * @param zone the new zone number.
     */
    public void setZone(int zone) {
        this.zone = zone;
    }

    /**
     * Set all the passengers in the passenger list. Note that this overrides
     * any existing list.
     * <p>
     * @param passengers list of all passengers in the new list.
     * <p>
     * @throws IllegalArgumentException if argument is NULL.
     */
    public void setAllPassengers(ArrayList<Integer> passengers) throws
            IllegalArgumentException {
        if (passengers == null) {
            throw new IllegalArgumentException("Argument cannot be NULL.");
        }

        this.passengers = passengers;
    }

    /**
     * Add a single passenger to the passenger list.
     * <p>
     * @param customerNumber of the passenger to add.
     * <p>
     * @throws IllegalArgumentException if argument is an empty string.
     */
    public void addSinglePassenger(Integer customerNumber) throws
            IllegalArgumentException {
        if (customerNumber <= 0) {
            throw new IllegalArgumentException(
                    "Customer number must be greater than 0.");
        }
        passengers.add(customerNumber);
    }

    /**
     * Remove a passenger from the list.
     * <p>
     * @param customerNumber customer number of the passenger to be removed.
     */
    public void removeSinglePassenger(Integer customerNumber) {
        passengers.remove(customerNumber);
    }

    /**
     * Get all duplicate passengers between two <code>PassengerLists</code>, as 
     * a new <code>PassengerList</code>.
     * <p>
     * @param  other list of passengers to compare with. 
     * <p>
     * @return a new <code>PassengerList</code> containing all duplicates 
     *         between the two lists.
     * <p>
     * @throws IllegalArgumentException if the supplied argument was NULL.
     */
    public PassengerList getDuplicatePassengers(PassengerList other) throws
            IllegalArgumentException {
        if (other == null) {
            throw new IllegalArgumentException("Supplied argument was NULL.");
        }

        PassengerList duplicates = new PassengerList(zone);
        ArrayList<Integer> otherPassengers = other.getAllPassengers();

        for (Integer pass : passengers) {
            if (otherPassengers.contains(pass)) {
                duplicates.addSinglePassenger(pass);
            }
        }
        return duplicates;
    }

    /**
     * Auto generated hasCode override.
     * <p>
     * @return the hash code.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + Objects.hashCode(this.passengers);
        return hash;
    }

    /**
     * Modified equals method. Two lists are equal only if they contain
     * the same elements. Neither of the lists may contain unique elements. The
     * sequence in which the elements are placed is ignored.
     * <p>
     * @param obj other <code>PassengerList</code> to test for equality.
     * <p>
     * @return true if an only if the two lists are equal; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PassengerList other = (PassengerList) obj;

        /* 
         If the two lists are not of equal size, there are unique elements in 
         one of the lists, and thus they are not equal.
         */
        if (other.size() != passengers.size()) {
            return false;
        }

        /*Ensure that there are no unique elements in one of the lists.*/
        ArrayList<Integer> otherPass = other.getAllPassengers();
        for (Integer thisPass : passengers) {
            if (otherPass.contains(thisPass) == false) {
                return false;
            }
        }
        return true;
    }

}
