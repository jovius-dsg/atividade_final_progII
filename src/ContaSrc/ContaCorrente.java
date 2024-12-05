package ContaSrc;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    public void criarPdf(String conteudo) {
        String nome = super.nome();
        String dataAtual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\OneDrive\\Documentos\\NetBeansProjects\\Sistema-Bancario-Simples-Java-main\\src\\JFrame\\mensage-icon.png");
        String caminho = "C:\\Users\\joaov\\Downloads\\Extrato_Conta_Poupanca" + "_" + nome + "_" + dataAtual + ".pdf";

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(caminho));

            document.open();

            document.add(new Paragraph(conteudo));

            document.close();

            JOptionPane.showMessageDialog(null, "PDF gerado com sucesso!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);

            File pdfFile = new File(caminho);
            if (pdfFile.exists()) {
                Desktop.getDesktop().open(pdfFile);
            } else {
                JOptionPane.showMessageDialog(null, "Não foi possível encontrar o arquivo PDF.", "GNB", JOptionPane.ERROR_MESSAGE);
            }

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Erro ao criar o PDF: " + e.getMessage(), "GNB", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void imprimirExtrato() {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\Documents\\NetBeansProjects\\GNBVersaoFinal\\src\\JFrame\\mensage-icon.png");

        String mensagem = "=== Extrato Conta Corrente ===\n"
                + super.imprimirInfos()
                + super.imprimirHistorico();

        Object[] options = {"Fechar", "Criar PDF"};

        int escolha = JOptionPane.showOptionDialog(null,
                mensagem,
                "GNB",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                icon,
                options,
                options[0]);

        if (escolha == 1) {
            criarPdf(mensagem);
        }
    }

}
