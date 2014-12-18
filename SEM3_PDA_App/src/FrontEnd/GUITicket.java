package FrontEnd;

import ModelClasses.Ticket;


/**
 * Ticket page <code>JPanel</code> for the customer PDA application GUI.
 * <p>
 * @author Andreas Stensig Jensen on Nov 5th, 2014
 * Contributors:
 */
public class GUITicket extends javax.swing.JPanel {

    private final GraphicalUserInterface parent;

    /**
     * Creates new form GUITicket
     * <p>
     * @param parent the parent container for this element.
     */
    public GUITicket(GraphicalUserInterface parent) {
        initComponents();
        this.parent = parent;
    }

    /**
     * Load the data from a <code>Ticket</code> into the GUI panel,
     * displaying the ticket.
     * <p>
     * @param ticket the <code>Ticket</code> with the information.
     */
    public void loadTicketData(Ticket ticket) {
        String cusNum = "" + ticket.getCustomerNumber();
        String time = ticket.getTimestamp();
        String price = "" + (ticket.getPrice() / 100.0);  // øre to dkk
        String startZone = "" + ticket.getStartZone();
        String tickNum = "" + ticket.getNumber();
        String zoneCount = "" + ticket.getZoneCount();
        fieldCusNum.setText(cusNum);
        fieldTime.setText(time);
        fieldPrice.setText(price);
        fieldStartZone.setText(startZone);
        fieldTickNum.setText(tickNum);
        fieldZoneCount.setText(zoneCount);
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

        btnBack = new javax.swing.JButton();
        labCusNum = new javax.swing.JLabel();
        fieldCusNum = new javax.swing.JTextField();
        labTime = new javax.swing.JLabel();
        fieldTime = new javax.swing.JTextField();
        labPrice = new javax.swing.JLabel();
        fieldPrice = new javax.swing.JTextField();
        labStartZone = new javax.swing.JLabel();
        fieldStartZone = new javax.swing.JTextField();
        labTickNum = new javax.swing.JLabel();
        fieldTickNum = new javax.swing.JTextField();
        labValidFor = new javax.swing.JLabel();
        fieldZoneCount = new javax.swing.JTextField();
        labZones = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(250, 400));

        btnBack.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        btnBack.setText("Back");
        btnBack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBackActionPerformed(evt);
            }
        });

        labCusNum.setText("Customer Number");

        fieldCusNum.setText(" ");
        fieldCusNum.setFocusable(false);

        labTime.setText("Time");

        fieldTime.setText(" ");
        fieldTime.setFocusable(false);

        labPrice.setText("Price (DKK)");

        fieldPrice.setFocusable(false);

        labStartZone.setText("Start zone");

        fieldStartZone.setFocusable(false);

        labTickNum.setText("Ticket Number");

        fieldTickNum.setFocusable(false);

        labValidFor.setText("Valid for ");

        fieldZoneCount.setText(" ");
        fieldZoneCount.setFocusable(false);

        labZones.setText("zones");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(fieldTime)
                    .addComponent(fieldCusNum)
                    .addComponent(fieldPrice)
                    .addComponent(fieldStartZone)
                    .addComponent(fieldTickNum)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(labCusNum)
                            .addComponent(labTime)
                            .addComponent(labPrice)
                            .addComponent(labStartZone)
                            .addComponent(labTickNum))
                        .addGap(0, 140, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fieldZoneCount, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(labZones))
                            .addComponent(labValidFor))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(labCusNum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldCusNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labTime)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldTime, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labPrice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labStartZone)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldStartZone, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(labTickNum)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fieldTickNum, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(labValidFor)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fieldZoneCount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(labZones)))
                    .addComponent(btnBack, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(77, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Switch back to the front page of the GUI. 
     * @param evt not used.
     */
    private void btnBackActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBackActionPerformed
        parent.switchPanel("Front");
    }//GEN-LAST:event_btnBackActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBack;
    private javax.swing.JTextField fieldCusNum;
    private javax.swing.JTextField fieldPrice;
    private javax.swing.JTextField fieldStartZone;
    private javax.swing.JTextField fieldTickNum;
    private javax.swing.JTextField fieldTime;
    private javax.swing.JTextField fieldZoneCount;
    private javax.swing.JLabel labCusNum;
    private javax.swing.JLabel labPrice;
    private javax.swing.JLabel labStartZone;
    private javax.swing.JLabel labTickNum;
    private javax.swing.JLabel labTime;
    private javax.swing.JLabel labValidFor;
    private javax.swing.JLabel labZones;
    // End of variables declaration//GEN-END:variables
}