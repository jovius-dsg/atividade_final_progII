package ContaSrc;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class ContaCorrente extends Conta {

    /*A Corrente poosui taxa na hora de sacar 
        e de transferir para outra conta do tipo corrente
     */
    private double taxaDeSaque = 0.30;
    private double taxaDeTransferencia = 0.87;

    public ContaCorrente(Cliente cliente) {
        super(cliente);
    }

    public void sacar(double valor) {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\Documents\\NetBeansProjects\\GNBVersaoFinal\\src\\JFrame\\mensage-icon.png");
        try {
            if (super.getSaldo() < valor + taxaDeSaque) {
                throw new ContaException("A operação não pode ser feita, o saldo não cobre a taxa de operação!");
            }
            super.sacar(valor + taxaDeSaque);
        } catch (ContaException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
        }
    }

    @Override
    public void transferir(double valor, Conta contaDestino) {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\Documents\\NetBeansProjects\\GNBVersaoFinal\\src\\JFrame\\mensage-icon.png");
        try {
            if (contaDestino.getNumero() == super.getNumero()) {
                throw new ContaException("O número informado é o desta conta");
            }
            if ("Corrente".equals(contaDestino.tipo())) {
                if (super.getSaldo() < valor + taxaDeSaque) {
                    throw new ContaException("A operação não pode ser feita, o saldo não cobre a taxa de operação!");
                }
                super.sacar(valor + taxaDeSaque);
                contaDestino.depositar(valor - taxaDeTransferencia);
            }
            super.transferir(valor, contaDestino);

        } catch (ContaException e) {
            JOptionPane.showMessageDialog(null, e.toString(), "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
        }

    }

    @Override
    public String tipo() {
        return "Corrente";
    }

    @Override
    public void imprimirExtrato() {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\Documents\\NetBeansProjects\\GNBVersaoFinal\\src\\JFrame\\mensage-icon.png");
        JOptionPane.showMessageDialog(null, "=== Extrato Conta Corrente ===\n"
                + super.imprimirInfos()
                + super.imprimirHistorico(),
                "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
    }

}
