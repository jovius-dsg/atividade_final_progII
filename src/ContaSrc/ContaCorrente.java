package ContaSrc;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
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
        String caminho = "C:\\Users\\joaov\\Downloads\\Extrato_Conta_Poupanca" + "_" + nome + "_" + dataAtual + ".pdf";

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(caminho));
            document.open();

            // Formatação básica do PDF
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
            Font textoFont = new Font(Font.FontFamily.HELVETICA, 12);

            document.add(new Paragraph("Extrato Bancário", tituloFont));
            document.add(new Paragraph("\n"));
            document.add(new Paragraph(conteudo, textoFont));

            document.close();

            JOptionPane.showMessageDialog(null, "PDF gerado com sucesso!", "GNB", JOptionPane.INFORMATION_MESSAGE,
                    new ImageIcon("Caminho/Para/Icone.png"));

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(caminho));
            } else {
                JOptionPane.showMessageDialog(null, "O PDF foi criado, mas não pôde ser aberto automaticamente.", "GNB",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (DocumentException | IOException e) {
            JOptionPane.showMessageDialog(null, "Erro ao criar o PDF: " + e.getMessage(), "GNB", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void imprimirExtrato() {
        String mensagem = "=== Extrato Conta Corrente ===\n"
                + super.imprimirInfos()
                + super.imprimirHistorico();

        Object[] options = {"Fechar", "Criar PDF"};
        int escolha = JOptionPane.showOptionDialog(null,
                mensagem,
                "GNB",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                new ImageIcon("Caminho/Para/Icone.png"),
                options,
                options[0]);

        if (escolha == 1) {
            criarPdf(mensagem);
        }
    }

}
