package JFrame;

import javax.swing.JOptionPane;
import Crud.CRUD;
import ContaSrc.Cliente;
import javax.swing.ImageIcon;

public class CriarConta extends javax.swing.JFrame {

    /**
     * Creates new form CriarConta
     */
    public CriarConta() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        CPF = new javax.swing.JFormattedTextField();
        name = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        tipo = new javax.swing.JComboBox<>();
        jButton2 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Criar Conta");
        setMinimumSize(new java.awt.Dimension(640, 360));
        getContentPane().setLayout(null);

        jLabel5.setFont(new java.awt.Font("Montserrat", 1, 10)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(153, 153, 153));
        jLabel5.setText("v.1.0.0.0");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(40, 310, 180, 13);

        jLabel1.setForeground(new java.awt.Color(0, 102, 153));
        jLabel1.setText("Informe o CPF do cliente:");
        getContentPane().add(jLabel1);
        jLabel1.setBounds(230, 170, 150, 16);

        jLabel2.setForeground(new java.awt.Color(0, 102, 153));
        jLabel2.setText("Informe o nome do cliente:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(230, 120, 150, 16);

        CPF.setForeground(new java.awt.Color(0, 102, 153));
        try {
            CPF.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.MaskFormatter("###.###.###-##")));
        } catch (java.text.ParseException ex) {
            ex.printStackTrace();
        }
        CPF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        CPF.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        CPF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CPFActionPerformed(evt);
            }
        });
        getContentPane().add(CPF);
        CPF.setBounds(230, 190, 180, 22);

        name.setForeground(new java.awt.Color(0, 102, 153));
        getContentPane().add(name);
        name.setBounds(230, 140, 180, 22);

        jButton1.setFont(new java.awt.Font("Montserrat", 0, 12)); // NOI18N
        jButton1.setForeground(new java.awt.Color(0, 102, 153));
        jButton1.setText("Criar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton1);
        jButton1.setBounds(240, 290, 70, 30);

        jLabel4.setForeground(new java.awt.Color(0, 102, 153));
        jLabel4.setText("Escolha o tipo de conta:");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(230, 220, 130, 16);

        tipo.setForeground(new java.awt.Color(0, 102, 153));
        tipo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Corrente", "Poupança" }));
        getContentPane().add(tipo);
        tipo.setBounds(230, 240, 180, 22);

        jButton2.setFont(new java.awt.Font("Montserrat Medium", 1, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(0, 102, 153));
        jButton2.setText("Sair");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        getContentPane().add(jButton2);
        jButton2.setBounds(330, 290, 70, 30);

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/JFrame/x1.png"))); // NOI18N
        jLabel3.setText("jLabel3");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(0, 0, 640, 360);

        setSize(new java.awt.Dimension(656, 398));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void CPFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_CPFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_CPFActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        
        String nome = name.getText();
        String cpf = CPF.getText();
        String Tipo = (String) tipo.getSelectedItem();
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\OneDrive\\Documentos\\NetBeansProjects\\Sistema-Bancario-Simples-Java-main\\src\\JFrame\\mensage-icon.png");

        if (cpf.trim().replace(".", "").replace("-", "").length() != 11) {

            JOptionPane.showMessageDialog(null, "Digite um CPF válido.", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);

        } else if (nome.trim().length() < 3) {
            
            JOptionPane.showMessageDialog(null, "Digite um nome com pelo menos 3 caracteres.", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
            
        } else {
            Cliente cliente = new Cliente(nome, cpf);
            CRUD.criarConta(cliente, Tipo);
            name.setText(null);
            CPF.setText("");
            
            JOptionPane.showMessageDialog(null, "Conta Criada!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
        }
//        String TipodaConta = Tipo;
//        Cliente modeloCliente = new Cliente();
//        modeloCliente.setNome(nome);
//        modeloCliente.setCPF(cpf);
//
//        AtualizarConta atualizarConta = new AtualizarConta();
//        atualizarConta.exportarCliente(modeloCliente, TipodaConta);

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
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
            java.util.logging.Logger.getLogger(CriarConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(CriarConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(CriarConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(CriarConta.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new CriarConta().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JFormattedTextField CPF;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField name;
    private javax.swing.JComboBox<String> tipo;
    // End of variables declaration//GEN-END:variables
}
