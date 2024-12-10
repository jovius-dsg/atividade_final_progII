package Crud;

import ContaSrc.ContaPoupanca;
import ContaSrc.Conta;
import ContaSrc.ContaCorrente;
import ContaSrc.Cliente;
import ContaSrc.Conta.ContaAdapter;
import ContaSrc.Conta.DateAdapter;
import ContaSrc.Conta.SimpleDateFormatAdapter;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class CRUD {

    public static List<Conta> listaContas = new ArrayList<>();
    private static final String CAMINHO_ARQUIVO = "src/data/contas.json";

    // Método para garantir que o arquivo existe
    public static void garantirArquivoExistente() {
        File pasta = new File("src/data");
        if (!pasta.exists() && pasta.mkdirs()) {
            System.out.println("Pasta 'data' criada com sucesso.");
        }

        File arquivo = new File(CAMINHO_ARQUIVO);
        if (!arquivo.exists()) {
            try {
                if (arquivo.createNewFile()) {
                    try (FileWriter writer = new FileWriter(arquivo)) {
                        writer.write("[]"); // Inicializa com uma lista vazia
                    }
                    System.out.println("Arquivo 'contas.json' criado com sucesso.");
                }
            } catch (IOException e) {
                System.out.println("Erro ao criar o arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("Arquivo 'contas.json' já existe.");
        }
    }

    // Centraliza o tratamento de mensagens de erro
    private static void mostrarMensagemErro(String mensagem) {
        // Caminho do ícone para exibição (ajuste o caminho conforme necessário)
        ImageIcon icon = new ImageIcon("src/JFrame/mensage-icon.png");
        JOptionPane.showMessageDialog(null, mensagem, "GNB", JOptionPane.ERROR_MESSAGE, icon);
    }

    // Atualiza o tipo de conta no JSON e formata datas
    public static void formatarTiposContasNoJson() {
        try (FileReader reader = new FileReader(CAMINHO_ARQUIVO)) {
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
            SimpleDateFormat formatoAntigo = new SimpleDateFormat("MMM dd, yyyy, hh:mm:ss a", Locale.ENGLISH);
            SimpleDateFormat formatoBrasileiro = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();
                String tipo = jsonObject.get("tipo").getAsString();
                if ("ContaCorrente".equals(tipo)) {
                    jsonObject.addProperty("tipo", "Corrente");
                } else if ("ContaPoupanca".equals(tipo)) {
                    jsonObject.addProperty("tipo", "Poupança");
                }

                JsonArray movimentacoes = jsonObject.getAsJsonArray("movimentacoes");
                for (JsonElement movimentacaoElement : movimentacoes) {
                    JsonObject movimentacaoObj = movimentacaoElement.getAsJsonObject();
                    String dataAntiga = movimentacaoObj.get("data").getAsString();
                    if (dataAntiga != null && !dataAntiga.trim().isEmpty()) {
                        try {
                            Date dataConvertida = formatoAntigo.parse(dataAntiga);
                            String dataNova = formatoBrasileiro.format(dataConvertida);
                            movimentacaoObj.addProperty("data", dataNova);
                        } catch (ParseException e) {
                            System.err.println("Erro ao converter data: " + dataAntiga);
                        }
                    }
                }
            }

            try (FileWriter writer = new FileWriter(CAMINHO_ARQUIVO)) {
                Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        .registerTypeAdapter(Date.class, new Conta.DateAdapter())
                        .create();
                gson.toJson(jsonArray, writer);
            }
        } catch (IOException | JsonSyntaxException e) {
            mostrarMensagemErro("Erro ao carregar ou salvar os dados: " + e.getMessage());
        }
    }

    // Salvar contas no arquivo JSON
    public static void salvarContasJson() {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(Conta.class, new ContaAdapter())
                .registerTypeAdapter(SimpleDateFormat.class, new SimpleDateFormatAdapter())
                .create();

        try (FileWriter writer = new FileWriter(CAMINHO_ARQUIVO)) {
            gson.toJson(listaContas, writer);
        } catch (IOException e) {
            mostrarMensagemErro("Erro ao salvar os dados: " + e.getMessage());
        }

        formatarTiposContasNoJson();
    }

    // Carregar contas do JSON
    public static void carregarContasJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateAdapter())
                .registerTypeAdapter(Conta.class, new ContaAdapter())
                .create();

        Type tipoListaContas = new TypeToken<ArrayList<Conta>>() {
        }.getType();

        try (FileReader reader = new FileReader(CAMINHO_ARQUIVO)) {
            listaContas = gson.fromJson(reader, tipoListaContas);
            if (listaContas == null) {
                listaContas = new ArrayList<>();
            }
        } catch (IOException | JsonSyntaxException e) {
            mostrarMensagemErro("Erro ao carregar os dados: " + e.getMessage());
            listaContas = new ArrayList<>();
        }
    }

    // Criar nova conta
    public static boolean criarConta(Cliente cliente, String tipo) {
        try {
            for (Conta contaExistente : listaContas) {
                if (contaExistente.getCliente().getCPF().equals(cliente.getCPF()) && contaExistente.tipo().equals(tipo)) {
                    mostrarMensagemErro("Cliente já possui conta " + tipo);
                    return false;
                }
            }

            Conta conta = "Corrente".equals(tipo) ? new ContaCorrente(cliente) : new ContaPoupanca(cliente);
            listaContas.add(conta);
            salvarContasJson();
            return true;
        } catch (Exception e) {
            mostrarMensagemErro("Erro ao criar conta: " + e.getMessage());
            return false;
        }
    }

    // Excluir conta por número
    public static boolean deletarContaPorNumero(int numero) {
        try {
            Iterator<Conta> iterator = listaContas.iterator();
            while (iterator.hasNext()) {
                Conta conta = iterator.next();
                if (conta.getNumero() == numero) {
                    int escolha = JOptionPane.showConfirmDialog(null, "Deseja excluir esta conta?\n" + conta.imprimirInfos());
                    if (escolha == JOptionPane.YES_OPTION) {
                        iterator.remove();
                        salvarContasJson();
                        return true;
                    }
                }
            }
            mostrarMensagemErro("Conta não encontrada!");
            return false;
        } catch (Exception e) {
            mostrarMensagemErro("Erro ao deletar conta: " + e.getMessage());
            return false;
        }
    }

    public static boolean alterarConta(int numero) {
        try {
            for (Conta conta : listaContas) {
                if (conta.getNumero() == numero) {
                    // Exibe dados atuais
                    mostrarMensagemErro("Dados atuais da conta:\n" + conta.imprimirInfos());

                    // Entrada de dados do cliente
                    String novoNome = JOptionPane.showInputDialog(null, "Digite o novo nome do cliente:", conta.getCliente().getNome());
                    if (novoNome == null || novoNome.trim().isEmpty()) {
                        mostrarMensagemErro("Nome inválido.");
                        return false;
                    }

                    String novoCPF = JOptionPane.showInputDialog(null, "Digite o novo CPF do cliente:", conta.getCliente().getCPF());

                    // Validações CPF
                    if (novoCPF.replaceAll("[^0-9]", "").length() != 11) {
                        mostrarMensagemErro("CPF inválido. Deve conter 11 dígitos numéricos.");
                        return false;
                    }

                    // Atualiza cliente
                    conta.getCliente().setNome(novoNome);
                    conta.getCliente().setCPF(novoCPF);

                    // Atualização do tipo da conta
                    String novoTipo = JOptionPane.showInputDialog(null, "Digite o novo tipo da conta (Corrente ou Poupança):", conta.tipo());
                    if (!"Corrente".equalsIgnoreCase(novoTipo) && !"Poupança".equalsIgnoreCase(novoTipo)) {
                        mostrarMensagemErro("Tipo inválido. Escolha 'Corrente' ou 'Poupança'.");
                        return false;
                    }

                    if (!novoTipo.equalsIgnoreCase(conta.tipo())) {
                        // Aqui podemos apenas alterar o tipo, sem remover a conta
                        if ("Corrente".equalsIgnoreCase(novoTipo)) {
                            conta = new ContaCorrente(conta.getCliente());
                        } else {
                            conta = new ContaPoupanca(conta.getCliente());
                        }
                    }

                    salvarContasJson(); // Salva a lista após a alteração
                    JOptionPane.showMessageDialog(null, "Conta alterada com sucesso!", "GNB", JOptionPane.INFORMATION_MESSAGE);
                    return true;
                }
            }

            mostrarMensagemErro("Conta não encontrada!");
            return false;

        } catch (Exception e) {
            mostrarMensagemErro("Erro ao alterar a conta: " + e.getMessage());
            return false;
        }
    }

    // Método para acessar uma conta pelo número
    public static Conta acessarContaPorNumero(int numero) {
        for (Conta conta : listaContas) {
            if (conta.getNumero() == numero) {
                return conta; // Retorna a conta encontrada
            }
        }
        // Se a conta não for encontrada, exibe uma mensagem e retorna null
        mostrarMensagemErro("Conta não encontrada!");
        return null;
    }

}
