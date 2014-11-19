package BuisnessLogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Model class containing an ArrayList of passengers on board a vehicle,
 * represented by their customer number.
 * <p>
 * @author Andreas Stensig Jensen on Oct 30, 2014
 * Contributors:
 * N/A
 */
public class PassengerList implements Serializable {

    // Passengers onboard a vehicle, represented through their customer number.
    private ArrayList<Integer> passengers;
    // The zone that the passengers of the list are in 
    private int zone;


    /**
     * Constructor.
     * @param zone the zone number of the zone the passengers are in. 
     */
    public PassengerList(int zone) {
        passengers = new ArrayList<>();
        this.zone = zone;
    }
    
    /**
     * Copy constructor.
     * @param other passengerlist to copy from.
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
     * @return the zone number
     */
    public int getZone() {
        return zone;
    }
    
    /**
     * Set the zone number.
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
        if (passengers.contains(customerNumber)) {
            passengers.remove(customerNumber);
        }
    }

    /**
     * Get all duplicate passengers between two PassengerLists, as a new
     * PassengerList.
     * <p>
     * @param other other PassengerList.
     * <p>
     * @return a new PassengerList containing all duplicates between the two
     *         Lists.
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
     * Modified equals method. Two PassengerList are only equal if they contain
     * the same strings. Neither of the lists must contain unique elements. The
     * sequence in which the strings are placed is ignored.
     * <p>
     * @param obj other PassengerList to test for equality
     * <p>
     * @return true if an only if the two PassengerList are equal; false
     *         otherwise.
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

        // Ensure that there are no unique strings in one of the lists. 
        ArrayList<Integer> otherPass = other.getAllPassengers();
        for (Integer thisPass : passengers) {
            if (otherPass.contains(thisPass) == false) {
                return false;
            }
        }
        return true;
    }

}
