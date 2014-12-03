import java.io.Serializable;
import java.util.Arrays;


/**
 * A custom hash table data structure for <code>Ticket</code> objects. The
 * size of the hash table is determined by the arbitrary limit of a maximum of
 * 800 expected Tickets in the data structure. Thus, for the sake of efficiency
 * calling <code>size</code> should never return more than 800, or the
 * expected number of probes per <code>add</code> and <code>getTicket</code>
 * methods will exceed and average of 2.5.
 * <p>
 * In general, the methods are expected O(1).
 * <p>
 * Note that this data structure does not offer methods for removing elements
 * from the table, since such actions should not be needed in the project.
 * <p>
 * @author Andreas Stensig Jensen, on Dec 2, 2014
 * Contributors:
 */
public class TicketHashTable implements Serializable {

    /**
     * Prime number closest to 1,067 which gives a load factor of approx. 0.75
     * with 800 tickets in the table, as arbitrarily chosen to be the limit of
     * passengers per vehicle computer.
     */
    private final int TABLE_SIZE = 1069;

    private final Ticket[] table = new Ticket[TABLE_SIZE];
    private int tableSize = 0;


    /**
     * Add a <code>Ticket</code> to the hash table.
     * <p>
     * @param t the ticket that is to be added.
     * <p>
     * @return true if and only if the ticket was successfully added, or false
     *         if it could not be added.
     */
    public boolean add(Ticket t) {
        int key = t.customerHashCode() % TABLE_SIZE;
        return insert(key, t);
    }

    /**
     * Merge another <code>TicketHashTable</code> with the current object.
     * <p>
     * @param other hash table to merge into the current one.
     * <p>
     * @return true if and only if all tickets from the other table was
     *         inserted successfully in the current one, or false if there were any
     *         tickets that could not be inserted.
     */
    public boolean mergeWith(TicketHashTable other) {
        boolean noneSkipped = true;
        Ticket[] otherTable = other.getRawTable();
        for (Ticket t : otherTable) {
            if (t != null) {
                if (!add(t)) {
                    noneSkipped = false;
                }
            }
        }
        return noneSkipped;
    }

    /**
     * Retrieve a <code>Ticket</code> with the given customer number. This will
     * copy over the ticket in a new object which is returned, and will not
     * remove it from the data structure.
     * <p>
     * @param customerNumber of the ticket wished found.
     * <p>
     * @return the found ticket, or NULL if it is not in the table.
     */
    public Ticket getTicketFor(int customerNumber) {
        int searchKey = customerNumber * 31 % TABLE_SIZE;
        int properKey = find(searchKey, customerNumber);
        if (properKey != -1) {
            return table[properKey];
        } else {
            return null;
        }
    }

    /**
     * Get the number of <code>Tickets</code> in the table excluding NULL
     * values.
     * <p>
     * @return number of elements in the table.
     */
    public int size() {
        return tableSize;
    }

    /**
     * Get the raw array of <code>Ticket</code> objects that constitutes the
     * table of this Hash table data structure. NOTE that the returned array
     * will have NULL values.
     * <p>
     * @return the array.
     */
    public Ticket[] getRawTable() {
        return table;
    }

    /**
     * Insert a <code>Ticket</code> into the table, first trying with the
     * generated key/index. If that index is already occupied open address
     * probing is used until no more collisions are found.
     * <p>
     * @param key   generated index the ticket should be inserted with.
     * @param value the ticket to be inserted
     * <p>
     * @return true if the ticket was successfully inserted (in any position),
     *         or false if it could not be inserted into the table.
     */
    private boolean insert(int key, Ticket value) {
        int originalKey = key;
        /*Look for first empty cell, but avoid infinite loop if none exists*/
        while (table[key] != null && (key + 1) != originalKey) {
            /*Wrap table at max index*/
            key = (key != TABLE_SIZE - 1) ? ++key : 0;
        }
        if (table[key] == null) {
            table[key] = value;
            ++tableSize;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Find a <code>Ticket</code> based on its customer number and return the
     * index of the Ticket in the table, or -1 if the target was not found.
     * <p>
     * @param key            the generated index where the target SHOULD be.
     * @param targetCustomer the customer number of the targeted ticket.
     * <p>
     * @return the true index of the target, if it was found in the table, or -1
     *         if it was not found.
     */
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

    /**
     * Auto-generated hash code method.
     * <p>
     * @return the generated hash code.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + this.TABLE_SIZE;
        hash = 59 * hash + Arrays.deepHashCode(this.table);
        hash = 59 * hash + this.tableSize;
        return hash;
    }

    /**
     * Test for equality with another object. Two <code>TicketHashTable</code>
     * objects are equal if and only if all their fields are equal.
     * <p>
     * @param obj object to test with for equality.
     * <p>
     * @return true if and only if the two objects are equal.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TicketHashTable other = (TicketHashTable) obj;
        if (this.TABLE_SIZE != other.TABLE_SIZE) {
            return false;
        }
        if (!Arrays.deepEquals(this.table, other.table)) {
            return false;
        }
        if (this.tableSize != other.tableSize) {
            return false;
        }
        return true;
    }
}
