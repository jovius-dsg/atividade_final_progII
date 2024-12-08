package Crud;

import ContaSrc.ContaPoupanca;
import ContaSrc.Conta;
import ContaSrc.ContaCorrente;
import ContaSrc.Cliente;
import ContaSrc.Conta.ContaAdapter;
import ContaSrc.Conta.DateAdapter;
import ContaSrc.Conta.SimpleDateFormatAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.HeadlessException;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CRUD {

    public static List<Conta> listaContas = new ArrayList<>();
    private static final String CAMINHO_ARQUIVO = "src/data/contas.json";

    // Método para garantir que o arquivo existe, se não, criar um arquivo vazio
    public static void garantirArquivoExistente() {
        File pasta = new File("src/data");

        // Verifica se a pasta 'data' existe, se não, cria
        if (!pasta.exists()) {
            boolean pastaCriada = pasta.mkdirs();
            if (pastaCriada) {
                System.out.println("Pasta 'data' criada com sucesso.");
            } else {
                System.out.println("Erro ao criar a pasta 'data'.");
            }
        }

        File arquivo = new File(CAMINHO_ARQUIVO);

        // Verifica se o arquivo já existe
        if (!arquivo.exists()) {
            try {
                // Se o arquivo não existe, cria um novo com um conteúdo inicial vazio
                arquivo.createNewFile();
                // Inicializa o arquivo com uma lista vazia de contas (em formato JSON)
                try (FileWriter writer = new FileWriter(arquivo)) {
                    writer.write("[]"); // Escreve um array JSON vazio
                }
                System.out.println("Arquivo 'contas.json' criado com sucesso.");
            } catch (IOException e) {
                System.out.println("Erro ao criar o arquivo: " + e.getMessage());
            }
        } else {
            System.out.println("Arquivo 'contas.json' já existe.");
        }
    }
    
    public static void formatarTiposContasNoJson() {
        // Tentar ler o arquivo JSON existente
        try (FileReader reader = new FileReader(CAMINHO_ARQUIVO)) {
            // Ler o conteúdo do JSON como uma lista de objetos genéricos
            JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

            // Percorrer todas as contas no JSON
            for (JsonElement element : jsonArray) {
                JsonObject jsonObject = element.getAsJsonObject();

                // Verificar o tipo da conta e alterar para "Corrente" ou "Poupança"
                String tipo = jsonObject.get("tipo").getAsString();
                if ("ContaCorrente".equals(tipo)) {
                    jsonObject.addProperty("tipo", "Corrente");
                } else if ("ContaPoupanca".equals(tipo)) {
                    jsonObject.addProperty("tipo", "Poupança");
                }
            }

            // Salvar o JSON modificado de volta no arquivo
            try (FileWriter writer = new FileWriter(CAMINHO_ARQUIVO)) {
                // Usar Gson para escrever o JSON formatado
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(jsonArray, writer);
            }

        } catch (IOException | JsonSyntaxException e) {
            // Exibe uma mensagem de erro se algo der errado
            JOptionPane.showMessageDialog(null, "Erro ao carregar ou salvar os dados: " + e.getMessage(), "GNB", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Método para salvar as contas no arquivo JSON
    public static void salvarContasJson() {
        // Cria o Gson com adaptadores personalizados
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Conta.class, new ContaAdapter())
                .registerTypeAdapter(SimpleDateFormat.class, new SimpleDateFormatAdapter())
                .setPrettyPrinting() // Para saída JSON formatada
                .create();

        try (FileWriter writer = new FileWriter(CAMINHO_ARQUIVO)) {
            gson.toJson(listaContas, writer);  // O Gson irá salvar o tipo corretamente
        } catch (IOException e) {
            // Exibe uma mensagem de erro caso ocorra um problema na gravação
            JOptionPane.showMessageDialog(null, "Erro ao salvar os dados: " + e.getMessage(), "GNB", JOptionPane.ERROR_MESSAGE);
        }

        // Imprimir o JSON salvo para verificação
        String jsonSalvo = gson.toJson(listaContas);
        if (jsonSalvo != null) {
            System.out.println("JSON salvo:\n" + jsonSalvo);
        } else {
            System.out.println("Nenhuma conta salva.");
        }
        
        formatarTiposContasNoJson();
    }

    public static void carregarContasJson() {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Date.class, new DateAdapter())
                .registerTypeAdapter(Conta.class, new ContaAdapter()) // O adaptador de Conta já cuida da conversão do tipo
                .create();

        Type tipoListaContas = new TypeToken<ArrayList<Conta>>() {
        }.getType();

        try (FileReader reader = new FileReader(CAMINHO_ARQUIVO)) {
            listaContas = gson.fromJson(reader, tipoListaContas);
            if (listaContas == null) {
                listaContas = new ArrayList<>();
            }
        } catch (JsonSyntaxException e) {
            // Caso o JSON esteja mal formatado
            JOptionPane.showMessageDialog(null, "Erro no formato do arquivo JSON: " + e.getMessage(), "GNB", JOptionPane.ERROR_MESSAGE);
            listaContas = new ArrayList<>();
        } catch (IOException e) {
            // Caso o arquivo não possa ser lido
            JOptionPane.showMessageDialog(null, "Erro ao carregar os dados: " + e.getMessage(), "GNB", JOptionPane.ERROR_MESSAGE);
            listaContas = new ArrayList<>();
        }
    }

    // Método para mostrar mensagens de erro centralizado
    private static void mostrarMensagemErro(String mensagem) {
        // Caminho relativo à pasta de recursos
        ImageIcon icon = new ImageIcon(CRUD.class.getResource("/JFrame/mensage-icon.png"));
        JOptionPane.showMessageDialog(null, mensagem, "GNB", JOptionPane.ERROR_MESSAGE, icon);
    }

    public static boolean criarConta(Cliente cliente, String tipo) {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\OneDrive\\Documentos\\NetBeansProjects\\Sistema-Bancario-Simples-Java-main\\src\\JFrame\\mensage-icon.png");

        try {
            Conta conta;
            Cliente clienteExistente;
            String cpfExistente;
            String tipoExistente;

            // Verifica se o cliente já tem uma conta do tipo especificado
            for (Conta contaExistente : listaContas) {
                clienteExistente = contaExistente.getCliente();
                cpfExistente = clienteExistente.getCPF();
                tipoExistente = contaExistente.tipo();
                if (cpfExistente.equals(cliente.getCPF()) && tipoExistente.equals(tipo)) {
                    JOptionPane.showMessageDialog(null, "Esse Cliente já possui conta " + tipo + " no Banco!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                    JOptionPane.showMessageDialog(null, contaExistente.imprimirInfos(), "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                    return false;
                }
            }

            // Criação da conta com o tipo
            if ("Corrente".equals(tipo)) {
                conta = new ContaCorrente(cliente);  // Atribui automaticamente o tipo "Corrente"
            } else {
                conta = new ContaPoupanca(cliente);  // Atribui automaticamente o tipo "Poupança"
            }

            listaContas.add(conta);
            JOptionPane.showMessageDialog(null, conta.imprimirInfos(), "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
            salvarContasJson(); // Salva a lista após adicionar uma conta

        } catch (Exception e) {
            return false;
        }

        return true;
    }

    public static boolean deletarContaPorNumero(int numero) {
        ImageIcon icon = new ImageIcon("C:\\Users\\joaov\\OneDrive\\Documentos\\NetBeansProjects\\Sistema-Bancario-Simples-Java-main\\src\\JFrame\\mensage-icon.png");

        try {
            for (Conta conta : listaContas) {
                int c = listaContas.indexOf(conta);
                if (conta.getNumero() == numero) {
                    int escolha = JOptionPane.showConfirmDialog(null, "Tem certeza que deseja excluir essa conta?\n " + conta.imprimirInfos());
                    if (escolha == 0) {
                        listaContas.remove(conta);
                        JOptionPane.showMessageDialog(null, "Conta deletada!", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
                        salvarContasJson(); // Salva a lista após remover uma conta
                        break;
                    } else {
                        JOptionPane.showMessageDialog(null, "Operação Cancelada.", "GNB", JOptionPane.INFORMATION_MESSAGE, icon);
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

}
