package FrontEnd;

import java.lang.reflect.InvocationTargetException;
import javax.swing.JApplet;

/**
 *
 * @author Stensig
 */
public class PDAApplications extends JApplet{
    @Override
    public void init() {
        // Add GUI to applet. 
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    getContentPane().add(new GraphicalUserInterface());
                }
            });
        } catch (InterruptedException | InvocationTargetException ex) {
            System.err.println("Critical error in creating PDA GUI.");
            ex.printStackTrace();
            System.exit(-1);
        }
    }
    
    @Override
    public void start() {
        
    }
    
    @Override
    public void destroy() {
        
    }
}
