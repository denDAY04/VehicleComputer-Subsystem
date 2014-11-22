package FrontEnd;

import BusinessLogic.Ticket;
import java.awt.CardLayout;
import java.io.IOException;
import javax.swing.JOptionPane;


/**
 * The backbone panel of the GUI for the PDA application.
 * <p>
 * @author Andreas Stensig Jensen on Nov 5th, 2014
 * Contributors:
 */
public class GraphicalUserInterface extends javax.swing.JPanel {

    private final CardLayout layout;
    private final GUITicket ticketPanel;
    private final GUIFrontpage frontPagePanel;
    private final PDAApplication parent;

    /**
     * Create new form GraphicalUserInterface.
     * <p>
     * @param parent the application using the GUI.
     */
    public GraphicalUserInterface(PDAApplication parent) {
        initComponents();
        layout = new CardLayout();
        this.setLayout(layout);
        this.parent = parent;

        frontPagePanel = new GUIFrontpage(this);
        ticketPanel = new GUITicket(this);
        this.add(frontPagePanel, "Front");
        this.add(ticketPanel, "Ticket");
    }

    /**
     * Switch to another panel in the <code>GraphicalUserInterface</code>.
     * <p>
     * @param target the panel to switch to: Either "Ticket" or "Front".
     */
    public void switchPanel(String target) {
        if (target.equals("Ticket")) {
            boolean error = false;
            try {
                Ticket ticket = parent.getTicket();
                if (ticket == null) {
                    error = true;
                } else {
                    ticketPanel.loadTicketData(ticket);
                }
            } catch (IOException ex) {
                System.err.println("I/O problems with reading ticket.");
                ex.printStackTrace();
                error = true;
            }
            
            if (error) {
                String title = "Ticket Error";
                String msg = "No ticket was found."
                        + "\nPlease try again in 10 seconds "
                        + "\nand if the problem persists"
                        + "\nsee www.1415.dk";
                JOptionPane.showMessageDialog(parent, msg, title,
                                              JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        layout.show(this, target);
    }

    /**
     * Enable the ping label on the Front page of the GUI, depending on the
     * parameter.
     * <p>
     * @param enable true if label should be shown, false otherwise.
     */
    public void enablePingLabel(boolean enable) {
        frontPagePanel.showPingLable(enable);
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(250, 400));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 250, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
