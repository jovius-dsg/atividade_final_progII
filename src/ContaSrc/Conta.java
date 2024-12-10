package ContaSrc;

import ContaSrc.IConta;
import com.google.gson.*;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Conta implements IConta {

    private static final int AGENCIA_PADRAO = 1;
    private final double LIMITE = 5000;
    private static int SEQUENCIAL = 1;
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private final int agencia;
    private final int numero;
    private double saldo;
    private String tipo;
    private final Cliente cliente;
    private String historico = "";
    private final List<Movimentacao> movimentacoes = new ArrayList<>();

    public Conta(Cliente cliente) {
        this.tipo = this.getClass().getSimpleName();
        this.agencia = Conta.AGENCIA_PADRAO;
        this.numero = SEQUENCIAL++;
        this.cliente = cliente;
    }

    @Override
    public void sacar(double valor) {
        if (saldo < valor || valor > LIMITE) {
            throw new RuntimeException("Operação inválida.");
        }
        saldo -= valor;
        registrarMovimentacao("Saque", valor);
    }

    @Override
    public void depositar(double valor) {
        saldo += valor;
        registrarMovimentacao("Depósito", valor);
    }

    @Override
    public void transferir(double valor, Conta contaDestino) {
        if (saldo < valor || valor > LIMITE || contaDestino.getNumero() == this.numero) {
            throw new RuntimeException("Operação inválida.");
        }

        // Atualizar os saldos diretamente
        saldo -= valor;
        contaDestino.saldo += valor;

        // Registrar a transferência apenas uma vez
        registrarMovimentacao(String.format("Transferência para conta %d", contaDestino.getNumero()), valor);
        contaDestino.registrarMovimentacao(String.format("Transferência recebida de conta %d", this.numero), valor);
    }

    @Override
    public abstract void criarPdf(String conteudo);

    @Override
    public abstract void imprimirExtrato();

    @Override
    public String tipo() {
        return this.getClass().getSimpleName();
    }

    public void registrarMovimentacao(String tipo, double valor, boolean restaurando) {
        Date data = new Date();
        movimentacoes.add(new Movimentacao(tipo, valor, data));

        if (!restaurando) {
            historico += "\n" + formatoData.format(data) + ": " + tipo + " de R$" + valor;
        }
    }

    // Sobrecarga para operações normais
    public void registrarMovimentacao(String tipo, double valor) {
        Date data = new Date();
        movimentacoes.add(new Movimentacao(tipo, valor, data));
        historico += String.format("\n%s: %s de R$%.2f", formatoData.format(data), tipo, valor);
    }

    public boolean isWithinPeriod(String inicio, String fim) {
        try {
            Date dataInicio = formatoData.parse(inicio);
            Date dataFim = formatoData.parse(fim);

            return movimentacoes.stream().anyMatch(m -> {
                Date dataMov = m.getData();
                return !dataMov.before(dataInicio) && !dataMov.after(dataFim);
            });
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getNumeroMovimentacoes() {
        return movimentacoes.size();
    }

    public int getAgencia() {
        return agencia;
    }

    public int getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getTipo() {
        return tipo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String nome() {
        return this.cliente.getNome();
    }

    public String imprimirInfos() {
        return "Titular: " + this.cliente.getNome()
                + "\nCPF: " + this.cliente.getCPF()
                + "\nAgência: " + this.agencia
                + "\nNúmero: " + this.numero
                + "\nTipo: " + tipo()
                + String.format("\nSaldo: %.2f", this.saldo);
    }

    public String imprimirHistorico() {
        return "\n===================" + historico + "\n===================";
    }

    // Classe interna para movimentação
    private static class Movimentacao {

        private final String tipo;
        private final double valor;
        private final Date data;

        public Movimentacao(String tipo, double valor, Date data) {
            this.tipo = tipo;
            this.valor = valor;
            this.data = data;
        }

        public Date getData() {
            return data;
        }
    }

    public static class DateAdapter implements JsonSerializer<Date>, JsonDeserializer<Date> {

        private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.forLanguageTag("pt-BR"));

        @Override
        public JsonElement serialize(Date src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(formatoData.format(src));
        }

        @Override
        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            try {
                return formatoData.parse(json.getAsString());
            } catch (ParseException e) {
                throw new JsonParseException("Erro ao deserializar a data: o formato esperado é 'dd/MM/yyyy HH:mm:ss'. Data fornecida: " + json.getAsString(), e);
            }
        }
    }

    public static class SimpleDateFormatAdapter implements JsonSerializer<SimpleDateFormat>, JsonDeserializer<SimpleDateFormat> {

        @Override
        public JsonElement serialize(SimpleDateFormat src, Type typeOfSrc, JsonSerializationContext context) {
            // Serializa o padrão do SimpleDateFormat como uma string
            return new JsonPrimitive(src.toPattern());
        }

        @Override
        public SimpleDateFormat deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // Desserializa a string de volta para um SimpleDateFormat
            return new SimpleDateFormat(json.getAsString());
        }
    }

    // Função para formatar o tipo da conta antes de salvar
    private static String formatarTipoConta(Conta conta) {
        if (conta instanceof ContaCorrente) {
            return "Corrente";
        } else if (conta instanceof ContaPoupanca) {
            return "Poupança";
        }
        return "Desconhecido";
    }

    public static class ContaAdapter implements JsonSerializer<Conta>, JsonDeserializer<Conta> {

        @Override
        public JsonElement serialize(Conta src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();

            // Serializar os campos básicos
            jsonObject.addProperty("agencia", src.getAgencia());
            jsonObject.addProperty("numero", src.getNumero());
            jsonObject.addProperty("saldo", src.getSaldo()); // Certifique-se de incluir o saldo aqui

            // Serializar o tipo e o cliente
            jsonObject.addProperty("tipo", formatarTipoConta(src));
            jsonObject.add("cliente", context.serialize(src.getCliente()));

            // Serializar o histórico
            jsonObject.addProperty("historico", src.imprimirHistorico());

            // Serializar movimentações
            JsonArray movimentacoesArray = new JsonArray();
            for (Movimentacao mov : src.movimentacoes) {
                JsonObject movimentacaoObject = new JsonObject();
                movimentacaoObject.addProperty("tipo", mov.tipo);
                movimentacaoObject.addProperty("valor", mov.valor);
                movimentacaoObject.add("data", context.serialize(mov.data));
                movimentacoesArray.add(movimentacaoObject);
            }
            jsonObject.add("movimentacoes", movimentacoesArray);

            return jsonObject;
        }

        @Override
        public Conta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();

            // Deserializar os campos básicos
            double saldo = jsonObject.get("saldo").getAsDouble(); // Recuperar o saldo
            Cliente cliente = context.deserialize(jsonObject.get("cliente"), Cliente.class);
            String historico = jsonObject.get("historico").getAsString();

            // Verificar o tipo de conta
            String tipo = jsonObject.get("tipo").getAsString();
            Conta conta;
            if ("Corrente".equals(tipo)) {
                conta = new ContaCorrente(cliente);
            } else if ("Poupança".equals(tipo)) {
                conta = new ContaPoupanca(cliente);
            } else {
                throw new JsonParseException("Tipo de conta inválido: " + tipo);
            }

            // Configurar o saldo e histórico
            conta.saldo = saldo; // Aplicar o saldo carregado do JSON
            conta.historico = historico;

            // Deserializar movimentações
            JsonArray movimentacoesArray = jsonObject.getAsJsonArray("movimentacoes");
            for (JsonElement element : movimentacoesArray) {
                JsonObject movObject = element.getAsJsonObject();
                String tipoMov = movObject.get("tipo").getAsString();
                double valor = movObject.get("valor").getAsDouble();
                Date data = context.deserialize(movObject.get("data"), Date.class);

                conta.movimentacoes.add(new Movimentacao(tipoMov, valor, data));
            }

            return conta;
        }

    }

}
