package BusinessLogic;

import java.io.Serializable;
import java.util.Objects;


/**
 * Model class encapsulating relevant data constituting a ticket, such as price,
 * start zone, time etc.
 * <p>
 * @author Andreas Stensig Jensen on Oct 30, 2014
 * Contributors:
 */
public class Ticket implements Serializable {

    /*Timestamp format: YYYY-MM-DD hh:mm:ss in 24h mode.*/
    private static final String TIME_FORMAT = "201[4-9]\\-[0-1][0-9]\\-[0-3]"
            + "[0-9]\\s[0-2][0-9]:[0-5][0-9]:[0-5][0-9]";
    
    private int number;
    private String timestamp;
    private int price;
    private int startZone;
    private int customerNumber;


    /**
     * Empty default constructor. After creating the object the data must be
     * supplied through the <code>createTicket</code> method.
     */
    public Ticket() {
        /*Empty default constructor.*/
    }

    /**
     * Method for instantiating the data encapsulated in a <code>Ticket</code>.
     * <p>
     * @param ticketNumber   must be greater than 0.
     * @param timestamp      must follow the format: YYYY-MM-DD hh:mm:ss in 24h
     *                       mode.
     * @param price          must be greater than 0.
     * @param startZone      must be within range: 1-5
     * @param customerNumber must be greater than 0.
     * <p>
     * @throws IllegalArgumentException if any of the restrictions above are not
     *                                  met.
     */
    public void createTicket(int ticketNumber, String timestamp, int price,
                             int startZone, int customerNumber) throws
            IllegalArgumentException {
        this.setNumber(ticketNumber);
        this.setTimestamp(timestamp);
        this.setPrice(price);
        this.setStartZone(startZone);
        this.setCustomerNumber(customerNumber);
    }

    /**
     * Get ticket's unique number.
     * <p>
     * @return the ticket number.
     */
    public int getNumber() {
        return number;
    }

    /**
     * Set ticket's unique number.
     * <p>
     * @param number of the ticket; unique.
     * <p>
     * @throws IllegalArgumentException if the number is negative.
     */
    private void setNumber(int number) throws IllegalArgumentException {
        if (number < 1) {
            throw new IllegalArgumentException("Ticket number must be positive.");
        } else {
            this.number = number;
        }
    }

    /**
     * Get ticket's timestamp
     * <p>
     * @return the ticket's timestamp
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Set ticket's timestamp.
     * <p>
     * @param timestamp must follow the format: YYYY-MM-DD hh:mm:ss in 24h mode.
     * <p>
     * @throws IllegalArgumentException if the timestamp is formated wrongly.
     */
    public void setTimestamp(String timestamp) throws IllegalArgumentException {
        if (timestamp.matches(TIME_FORMAT)) {
            this.timestamp = timestamp;
        } else {
            throw new IllegalArgumentException("Invalid timestamp format.");
        }
    }

    /**
     * Get ticket's price.
     * <p>
     * @return the ticket's price.
     */
    public int getPrice() {
        return price;
    }

    /**
     * Set ticket's price.
     * <p>
     * @param price of the ticket.
     * <p>
     * @throws IllegalArgumentException if the price is negative or zero.
     */
    public void setPrice(int price) throws IllegalArgumentException {
        if (price <= 0) {
            throw new IllegalArgumentException(
                    "Ticket must have a positive price.");
        }
        this.price = price;
    }

    /**
     * Get ticket's start zone.
     * <p>
     * @return the ticket's start zone.
     */
    public int getStartZone() {
        return startZone;
    }

    /**
     * Set ticket's start zone, represented by the zone's number.
     * Currently supported zone number range: 1-5
     * <p>
     * @param startZone of the ticket.
     * <p>
     * @throws IllegalArgumentException if the zone number is out of range.
     */
    private void setStartZone(int startZone) throws IllegalArgumentException {
        if (startZone <= 0 || startZone > 5) {
            throw new IllegalArgumentException("Invalid start zone number.");
        }
        this.startZone = startZone;
    }

    /**
     * Get ticket's customer number.
     * <p>
     * @return the ticket's customer number.
     */
    public int getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Set ticket's customer number.
     * <p>
     * @param customerNumber of the ticket.
     * <p>
     * @throws IllegalArgumentException if the customer number is negative or
     *                                  zero.
     */
    private void setCustomerNumber(int customerNumber) throws
            IllegalArgumentException {
        if (customerNumber <= 0) {
            throw new IllegalArgumentException(
                    "Customer number must be positive.");
        } else {
            this.customerNumber = customerNumber;
        }
    }

    /**
     * Auto generated hash code method.
     * <p>
     * @return the has code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + this.number;
        hash = 53 * hash + Objects.hashCode(this.timestamp);
        hash = 53 * hash + this.price;
        hash = 53 * hash + this.startZone;
        hash = 53 * hash + this.customerNumber;
        return hash;
    }

    /**
     * Equals override.
     * Two <code>Ticket</code> objects are equal if and only if all of their 
     * field variables are equal.
     * <p>
     * @param obj other <code>Ticket</code> object for testing for equality.
     * <p>
     * @return true if the two objects are equal; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ticket other = (Ticket) obj;
        if (this.number != other.number) {
            return false;
        }
        if (this.timestamp.equals(other.timestamp) == false) {
            return false;
        }
        if (this.price != other.price) {
            return false;
        }
        if (this.startZone != other.startZone) {
            return false;
        }
        if (this.customerNumber != other.customerNumber) {
            return false;
        }
        return true;
    }

}
