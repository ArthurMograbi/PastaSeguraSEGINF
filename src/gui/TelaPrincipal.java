package gui;

import javax.swing.*;
import java.awt.*;

public class TelaPrincipal extends JFrame {

    public TelaPrincipal(String login, String grupo, String nome, int totalAcessos) {
        super("Cofre Digital - Tela Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLayout(new BorderLayout());

        // --- Cabeçalho ---
        JPanel cabecalho = new JPanel();
        cabecalho.setLayout(new GridLayout(3, 1));
        cabecalho.setBorder(BorderFactory.createTitledBorder("Cabeçalho"));
        cabecalho.add(new JLabel("Login: " + login));
        cabecalho.add(new JLabel("Grupo: " + grupo));
        cabecalho.add(new JLabel("Nome: " + nome));
        add(cabecalho, BorderLayout.NORTH);

        // --- Corpo 1 (Total de Acessos) ---
        JPanel corpo1 = new JPanel();
        corpo1.setBorder(BorderFactory.createTitledBorder("Corpo 1"));
        corpo1.add(new JLabel("Total de acessos do usuário: " + totalAcessos));
        add(corpo1, BorderLayout.CENTER);

        // --- Corpo 2 (Menu Principal) ---
        JPanel corpo2 = new JPanel();
        corpo2.setBorder(BorderFactory.createTitledBorder("Corpo 2"));
        corpo2.setLayout(new GridLayout(3, 1));

        JButton btnCadastrar = new JButton("1 – Cadastrar um novo usuário");
        JButton btnConsultar = new JButton("2 – Consultar pasta de arquivos secretos do usuário");
        JButton btnSair = new JButton("3 – Sair do Sistema");

        corpo2.add(btnCadastrar);
        corpo2.add(btnConsultar);
        corpo2.add(btnSair);

        add(corpo2, BorderLayout.SOUTH);

        setVisible(true);
    }

    public static void main(String[] args) {
        // Exemplo de uso (dados mockados):
        SwingUtilities.invokeLater(() -> 
            new TelaPrincipal(
                "admin@cofre.com", 
                "Administrador", 
                "João Silva", 
                10
            )
        );
    }
}