package BusinessLogic;

import java.util.ArrayList;
import java.util.PriorityQueue;


/**
 * {Insert class description here}
 * 
 * @author Andreas Stensig Jensen, on Nov 21, 2014
 * Contributors: 
 */
public class PasListDatastructureTest {

    private static int SIZE = 800;
    private static ArrayList<Integer> arrList;
    private static PriorityQueue<Integer> queue;
    
    
    public static void main(String[] args) {
        long genArrLis, genQueue;
        
        generateArrayList();
        generateQueue();
        arrAddSingle(245);
        queueAddSingle(245);
        
    }
    
    
    public static void generateArrayList() {
        ArrayList<Integer> temp = new ArrayList<>(SIZE);
        long time1, time2;
        
        time1 = System.nanoTime();
        for (int i = 0; i != (SIZE - 1); ++i) {
            temp.add(i);
        }
        time2 = System.nanoTime();
        arrList = temp;
        System.out.println("Gen arr: " + (time2 - time1) + " ns");
    }
    
    public static void generateQueue() {
        PriorityQueue<Integer> temp = new PriorityQueue<>(SIZE);
        long time1, time2;
        
        time1 = System.nanoTime();
        for (int i = 0; i != (SIZE - 1); ++i) {
            temp.add(i);
        }
        time2 = System.nanoTime();
        queue = temp;
        System.out.println("Gen Queue: " + (time2 - time1) + " ns");
    }
    
    public static void arrAddSingle(int i) {
        long time1, time2;
        time1 = System.nanoTime();
        arrList.add(i);
        time2 = System.nanoTime();
        System.out.println("Arr add single: " + (time2 - time1) + " ns");
    }
    
    public static void queueAddSingle(int i) {
        long time1, time2;
        time1 = System.nanoTime();
        queue.add(i);
        time2 = System.nanoTime();
        System.out.println("Queue add single: " + (time2 - time1) + " ns");
    }
   
}
