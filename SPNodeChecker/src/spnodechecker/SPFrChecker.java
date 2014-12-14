/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spnodechecker;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import spnodechecker.client.SPAddress;
import spnodechecker.client.SPAddressHolder;

/**
 *
 * @author suaongmattroi
 */
public class SPFrChecker extends javax.swing.JFrame implements ListDataListener {

    private final SPAddressHolder holder;
    private final SPChecker checker;
    
    private String uid;
    private String sid;
    private String signed_request;
    
    private SpFrCheckerModel model;
    
    public SPFrChecker() {
        setTitle("Node Checker");
        initComponents();
        
        model = new SpFrCheckerModel();
        model.addListDataListener(this);
        
        holder = new SPAddressHolder();
        holder.loadAddress();
        
        checker = new SPChecker();
        
        result.setModel(new DefaultListModel());
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        testReq = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        btnCheck = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        result = new javax.swing.JList();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Test User");

        btnCheck.setText("Check");
        btnCheck.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCheckActionPerformed(evt);
            }
        });

        jScrollPane2.setViewportView(result);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnCheck)
                    .addComponent(jLabel1))
                .addGap(19, 19, 19)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(testReq)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE))
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(testReq, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnCheck)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private boolean getParams()
    {
        if(testReq.getText().equals(""))
            return false;
        
        this.uid = null;
        this.sid = null;
        this.signed_request = null;
        
        String req = testReq.getText();
        
        int uidIndex = req.indexOf("&sign_user");
        int nameIndex = req.indexOf("&username");
        this.uid = req.substring(uidIndex + 11, nameIndex);
        
        int sidIndex = req.indexOf("&session_id");
        int signRequestIndex = req.indexOf("&signed_request");
        this.sid = req.substring(sidIndex + 12, signRequestIndex);
        
        int codeIndex = req.indexOf("&code");
        this.signed_request = req.substring(signRequestIndex + 16, codeIndex);
        
        return (uid != null && sid != null && signed_request != null);
    }
    
    private void btnCheckActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCheckActionPerformed
        model.clearAll();
        List<SPAddress> addresses = holder.getAddresses();
        try {
            Map<String, Object> data = new HashMap();
            checker.check(model, addresses, data);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(SPFrChecker.class.getName()).log(Level.SEVERE, null, ex);
        }
//        if(getParams())
//        {
//            Map<String, Object> data = new HashMap();
//            data.put("uid", uid);
//            data.put("signed_request", signed_request);
//            data.put("sid", sid);
//            
//            List<SPAddress> addresses = holder.getAddresses();
//            try {
//                checker.check(model, addresses, data);
//            } catch (InvocationTargetException ex) {
//                Logger.getLogger(SPFrChecker.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
    }//GEN-LAST:event_btnCheckActionPerformed

    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SPFrChecker.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SPFrChecker.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SPFrChecker.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SPFrChecker.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SPFrChecker().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCheck;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JList result;
    private javax.swing.JTextField testReq;
    // End of variables declaration//GEN-END:variables

    @Override
    public void intervalAdded(ListDataEvent e) {
        SpFrCheckerModel addressModel = (SpFrCheckerModel) e.getSource();
        DefaultListModel listModel = (DefaultListModel) result.getModel();
        for (int i = e.getIndex0(); i < e.getIndex1(); ++i) {
            listModel.addElement(addressModel.getElementAt(i));
        }
        result.setModel(listModel);
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        SpFrCheckerModel addressModel = (SpFrCheckerModel) e.getSource();
        DefaultListModel listModel = (DefaultListModel) result.getModel();
        for (int i = e.getIndex0(); i < e.getIndex1(); ++i) {
            listModel.removeElement(addressModel.getElementAt(i));
        }
        result.setModel(listModel);
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        
    }
}
