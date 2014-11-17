package BuisnessLogic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Model class containing an ArrayList of Ticket model objects.
 * <p>
 * @author Andreas Stensig Jensen on Oct 30, 2014
 * Contributors:
 * N/A
 */
public class TicketList implements Serializable {

    private ArrayList<Ticket> tickets;

    /**
     * Default constructor.
     */
    public TicketList() {
        tickets = new ArrayList<>();
    }
    
    /**
     * Copy constructor.
     * @param other the original object to copy.
     */
    public TicketList(TicketList other) {
        tickets = other.tickets;
    }

    /**
     * Set all tickets in the list. Note that this overrides existing tickets.
     * <p>
     * @param tickets ArrayList of all the tickets.
     */
    public void setAllTickets(ArrayList<Ticket> tickets) {
        this.tickets = tickets;
    }

    /**
     * Add a single ticket to the ticket list.
     * <p>
     * @param ticket to be added.
     * <p>
     * @throws IllegalArgumentException if the ticket reference is NULL.
     */
    public void addSingleTicket(Ticket ticket) throws IllegalArgumentException {
        if (ticket == null) {
            throw new IllegalArgumentException("Ticket cannot be NULL.");
        } else {
            tickets.add(ticket);
        }
    }

    /**
     * Combine the TicketList with another TicketList. Does not check for
     * duplicates.
     * <p>
     * @param other TicketList to be merged with the current object.
     */
    public void mergeWith(TicketList other) {
        tickets.addAll(other.getAllTickets());
    }

    /**
     * Get a ticket for a given customer number.
     * <p>
     * @param customerNumber of the desired ticket.
     * <p>
     * @return the ticket object if it exists in the list; NULL otherwise.
     * <p>
     * @throws IllegalArgumentException if customer number is negative or zero.
     */
    public Ticket getTicket(int customerNumber) throws IllegalArgumentException {
        if (customerNumber <= 0) {
            throw new IllegalArgumentException(
                    "Customer number must be positive.");
        }

        for (Ticket t : tickets) {
            if (t.getCustomerNumber() == customerNumber) {
                return t;
            }
        }

        return null;        // No valid ticket found
    }

    /**
     * Get all tickets in the list.
     * <p>
     * @return all the tickets in the list.
     */
    public ArrayList<Ticket> getAllTickets() {
        return tickets;
    }

    /**
     * Get amount of tickets in the list.
     * <p>
     * @return the amount of tickets in the list.
     */
    public int size() {
        return tickets.size();
    }

    /**
     * Test if list contains no tickets.
     * <p>
     * @return true if no tickets are stored; false otherwise.
     */
    public boolean isEmpty() {
        return tickets.isEmpty();
    }

    /**
     * Auto generated hash code override.
     * <p>
     * @return the new hash code.
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.tickets);
        return hash;
    }

    /**
     * Equals override.
     * Two TicketList objects are equal if and only if neither contains an
     * unique Ticket elements, e.i. every Ticket in one list also has to be in
     * the other.
     * <p>
     * @param obj other TicketList to compare with for equality.
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
        final TicketList other = (TicketList) obj;

        ArrayList<Ticket> otherTickets = other.getAllTickets();
        // If size is not the same, unique tickets must exist.
        if (otherTickets.size() != tickets.size()) {
            return false;
        }
        // Check for unique tickets.
        for (Ticket t : tickets) {
            if (otherTickets.contains(t) == false) {
                return false;
            }
        }
        return true;
    }


}
