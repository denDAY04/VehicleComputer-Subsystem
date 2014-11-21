package BusinessLogic;

import java.util.ArrayList;


/**
 * Test constructed in order to simulate searching through a list of threads
 * named with IP addresses, the list being at most 1,000 elements long.
 * This was used to determine whether it would be viable to keep track of active
 * handler threads, the <code>UDPPacketHandler</code> instances. It is a legacy
 * test, since its results were negative and the design was later changed to
 * communication be direct between client and handler thread, eliminating the
 * need for keeping track of active handlers.
 * <p>
 * @author Andreas Stensig Jensen, on Nov 10, 2014
 * Contributors:
 */
public class IPThreadNameSearchTest {

    private String name = "128.138.43.102";


    public String name() {
        return name;
    }

    public static void main(String[] args) {
        ArrayList<IPThreadNameSearchTest> arrList = new ArrayList<>();
        long timeBegin, timeEnd;

        for (int i = 0; i != 1000; ++i) {
            arrList.add(new IPThreadNameSearchTest());
        }

        String target = "128.138.43.103";
        timeBegin = System.nanoTime();
        for (IPThreadNameSearchTest e : arrList) {
            if (e.name().equals(target)) {
                System.out.println("Target found.");
                break;
            }
        }
        timeEnd = System.nanoTime();
        System.out.println("Time: " + (timeEnd - timeBegin) + " ns");

    }

}
