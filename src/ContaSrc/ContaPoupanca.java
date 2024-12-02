package ContaSrc;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class ContaPoupanca extends Conta {

    public ContaPoupanca(Cliente cliente) {
        super(cliente);
    }

    @Override
    public String tipo() {
        return "Poupança";
    }

    @Override
    public void imprimirExtrato() {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\Documents\\NetBeansProjects\\GNBVersaoFinal\\src\\JFrame\\mensage-icon.png");
        JOptionPane.showMessageDialog(null, "=== Extrato Conta Poupança ===\n"
                + super.imprimirInfos()
                + super.imprimirHistorico(),
                "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
    }
}
