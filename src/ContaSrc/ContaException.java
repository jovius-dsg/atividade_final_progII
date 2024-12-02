package ContaSrc;

public class ContaException extends Exception {

    private String mensagem;

    public ContaException(String mensagem) {
        this.mensagem = mensagem;
    }

    @Override
    public String toString() {
        return "<OPERAÇÃO CANCELADA>! " + mensagem;
    }
}
