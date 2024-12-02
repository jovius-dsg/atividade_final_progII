package Crud;

import ContaSrc.ContaPoupanca;
import ContaSrc.Conta;
import ContaSrc.ContaCorrente;
import ContaSrc.Cliente;
import java.awt.HeadlessException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class CRUD {

    public static List<Conta> listaContas = new ArrayList<Conta>();

    public static boolean criarConta(Cliente cliente, String tipo) {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\OneDrive\\Documentos\\NetBeansProjects\\Sistema-Bancario-Simples-Java-main\\src\\JFrame\\mensage-icon.png");

        try {
            Conta conta;
            Cliente clienteExistente;
            String cpfExistente;
            String tipoExistente;

            /*esse laço verifica se já existe uma conta com o mesmo CPF e do mesmo tipo
              se existir, ele avisa e exibe os dados da conta existente
              se não existir, ele cria a conta e exibe os dados da nova conta  
             */
            for (Conta contaExistente : listaContas) {
                clienteExistente = contaExistente.getCliente();
                cpfExistente = clienteExistente.getCPF();
                tipoExistente = contaExistente.tipo();
                if (cpfExistente.equals(cliente.getCPF())
                        && tipoExistente.equals(tipo)) {
                    JOptionPane.showMessageDialog(null, "Esse Cliente já possui conta " + tipo + " no Banco!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                    JOptionPane.showMessageDialog(null, contaExistente.imprimirInfos(), "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                    return false;
                }
            }

            //aqui ele verifica que tipo de conta deve ser criada, Corrente ou Poupança
            if ("Corrente".equals(tipo)) {
                conta = new ContaCorrente(cliente);
            } else {
                conta = new ContaPoupanca(cliente);
            }

            listaContas.add(conta);
            JOptionPane.showMessageDialog(null, conta.imprimirInfos(), "GNB", JOptionPane.INFORMATION_MESSAGE, icon);

        } catch (Exception e) {
            return false;

        }

        return true;
    }

    public static boolean deletarContaPorNumero(int numero) {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\OneDrive\\Documentos\\NetBeansProjects\\Sistema-Bancario-Simples-Java-main\\src\\JFrame\\mensage-icon.png");

        try {
            /*nesse laço ele procura a conta que deve ser deletada
              e, caso encontre, exibe os dados da conta e pede confirmação ao usuário
              caso não encontre, informa que a conta não foi encontrada
             */
            for (Conta conta : listaContas) {
                int c = listaContas.indexOf(conta);
                if (conta.getNumero() == numero) {
                    int escolha = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir essa conta?\n " + conta.imprimirInfos());
                    if (escolha == 0) {
                        listaContas.remove(conta);
                        JOptionPane.showMessageDialog(null, "Conta deletada!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Operação Cancelada", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                    }
                } else {
                    if (c == (listaContas.size() - 1)) {
                        JOptionPane.showMessageDialog(null, "Conta não encontrada!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                    }
                }

            }

        } catch (Exception e) {
            return false;

        }

        return false;
    }

    public static Conta acessarContaPorNumero(int numero) {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\OneDrive\\Documentos\\NetBeansProjects\\Sistema-Bancario-Simples-Java-main\\src\\JFrame\\mensage-icon.png");

        try {
            /*nesse laço ele procura a conta que possui o mesmo número informado
              caso encontre, ele retorna a conta
              caso não encontre, ele informa que a conta não foi encontrada
             */
            for (Conta conta : listaContas) {
                int c = listaContas.indexOf(conta);
                if (conta.getNumero() == numero) {
                    return conta;
                } else {
                    if (c == (listaContas.size() - 1)) {
                        JOptionPane.showMessageDialog(null, "Conta não encontrada!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                    }
                }

            }

        } catch (HeadlessException e) {
            return null;

        }
        return null;
    }

    public static List<Conta> listarContas() {

        return listaContas;

    }

    public static boolean alterarConta(int numero) {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\OneDrive\\Documentos\\NetBeansProjects\\Sistema-Bancario-Simples-Java-main\\src\\JFrame\\mensage-icon.png");

        try {
            for (Conta conta : listaContas) {
                if (conta.getNumero() == numero) {
                    // Exibe os dados atuais
                    JOptionPane.showMessageDialog(null, "Dados atuais da conta:\n" + conta.imprimirInfos(), "GNB", JOptionPane.INFORMATION_MESSAGE, icon);

                    // Solicita novos dados
                    String novoNome = JOptionPane.showInputDialog(null, "Digite o novo nome do cliente:", conta.getCliente().getNome());
                    if (novoNome == null || novoNome.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Nome inválido.", "GNB", JOptionPane.ERROR_MESSAGE, icon);
                        return false;
                    }

                    String novoCPF = JOptionPane.showInputDialog(null, "Digite o novo CPF do cliente:", conta.getCliente().getCPF());

                    String cpfLimpo = novoCPF.replaceAll("[^0-9]", "");

                    if (cpfLimpo.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "CPF não pode estar vazio.", "GNB", JOptionPane.ERROR_MESSAGE, icon);
                        return false;
                    }

                    if (cpfLimpo.length() != 11) {
                        JOptionPane.showMessageDialog(null, "CPF inválido. Deve conter exatamente 11 dígitos.", "GNB", JOptionPane.ERROR_MESSAGE, icon);
                        return false;
                    }

                    if (!cpfLimpo.matches("\\d{11}")) {
                        JOptionPane.showMessageDialog(null, "CPF inválido. Deve conter apenas números.", "GNB", JOptionPane.ERROR_MESSAGE, icon);
                        return false;
                    }

                    String novoTipo = JOptionPane.showInputDialog(null, "Digite o novo tipo da conta (Corrente ou Poupança):", conta.tipo());
                    if (novoTipo == null || (!novoTipo.equalsIgnoreCase("Corrente") && !novoTipo.equalsIgnoreCase("Poupança"))) {
                        JOptionPane.showMessageDialog(null, "Tipo inválido. Escolha 'Corrente' ou 'Poupança'.", "GNB", JOptionPane.ERROR_MESSAGE, icon);
                        return false;
                    }

                    // Atualiza os dados do cliente
                    conta.getCliente().setNome(novoNome);
                    conta.getCliente().setCPF(novoCPF);

                    // Atualiza ou converte o tipo da conta
                    if (!novoTipo.equalsIgnoreCase(conta.tipo())) {
                        Cliente clienteAtualizado = conta.getCliente();
                        listaContas.remove(conta);

                        if (novoTipo.equalsIgnoreCase("Corrente")) {
                            listaContas.add(new ContaCorrente(clienteAtualizado));
                        } else if (novoTipo.equalsIgnoreCase("Poupança")) {
                            listaContas.add(new ContaPoupanca(clienteAtualizado));
                        }
                    }

                    JOptionPane.showMessageDialog(null, "Conta alterada com sucesso!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                    return true;
                }
            }

            // Se a conta não for encontrada
            JOptionPane.showMessageDialog(null, "Conta não encontrada!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
            return false;

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Erro ao alterar a conta: " + e.getMessage(), "GNB", JOptionPane.ERROR_MESSAGE, icon);
            return false;
        }
    }

}
