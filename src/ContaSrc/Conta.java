package ContaSrc;

import ContaSrc.IConta;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class Conta implements IConta {

    private static final int AGENCIA_PADRAO = 1;
    private final double LIMITE = 5000;
    private static int SEQUENCIAL = 1;
    private final SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

    private final int agencia;
    private final int numero;
    private double saldo;
    private final Cliente cliente;
    private String historico = "";
    private final List<Movimentacao> movimentacoes = new ArrayList<>();

    public Conta(Cliente cliente) {
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
        this.sacar(valor);
        contaDestino.depositar(valor);
        registrarMovimentacao("Transferência", valor);
    }

    private void registrarMovimentacao(String tipo, double valor) {
        Date data = new Date();
        movimentacoes.add(new Movimentacao(tipo, valor, data));
        historico += "\n" + formatoData.format(data) + ": " + tipo + " de R$" + valor;
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
    
    @Override
    public abstract void criarPdf(String conteudo);
            
    @Override
    public abstract void imprimirExtrato();
    
  ;

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
}
