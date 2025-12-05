package com.example.tarefas;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class SimpleLoginScreen extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private TaskService service;

    // Constante de versão do software
    public static final String APP_VERSION = "v1.0.0";

    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color PRIMARY_DARK = new Color(67, 56, 202);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color BACKGROUND = new Color(249, 250, 251);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);

    public SimpleLoginScreen(TaskService service) {
        this.service = service;

        setTitle("AAG TaskControl - Login");
        setSize(450, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        // Painel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND);
        mainPanel.setBorder(new EmptyBorder(40, 50, 40, 50));

        JPanel headerPanel = createHeader();
        headerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(headerPanel);

        mainPanel.add(Box.createVerticalStrut(30));

        JPanel loginCard = createLoginCard();
        loginCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(loginCard);

        mainPanel.add(Box.createVerticalStrut(20));

        // Footer com versão
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BoxLayout(footerPanel, BoxLayout.Y_AXIS));
        footerPanel.setOpaque(false);
        footerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel footer = new JLabel("2024 AAG TaskControl");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(TEXT_SECONDARY);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(footer);

        footerPanel.add(Box.createVerticalStrut(4));

        JLabel versionLabel = new JLabel("Versão " + APP_VERSION);
        versionLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        versionLabel.setForeground(PRIMARY);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        footerPanel.add(versionLabel);

        mainPanel.add(footerPanel);

        add(mainPanel);
    }

    private JPanel createHeader() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(5000, 150));

        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 80;
                int x = (getWidth() - size) / 2;
                int y = 10;

                GradientPaint gradient = new GradientPaint(
                        x, y, PRIMARY,
                        x + size, y + size, PRIMARY_DARK
                );
                g2d.setPaint(gradient);
                g2d.fillOval(x, y, size, size);

                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                int checkX = x + size / 2 - 15;
                int checkY = y + size / 2;
                g2d.drawLine(checkX, checkY, checkX + 10, checkY + 10);
                g2d.drawLine(checkX + 10, checkY + 10, checkX + 25, checkY - 10);
            }
        };
        logoPanel.setPreferredSize(new Dimension(100, 100));
        logoPanel.setOpaque(false);
        logoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(logoPanel);

        headerPanel.add(Box.createVerticalStrut(15));

        JLabel title = new JLabel("AAG TaskControl");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(title);

        JLabel subtitle = new JLabel("Sistema de Gerenciamento de Tarefas");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(TEXT_SECONDARY);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(subtitle);

        return headerPanel;
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(CARD);
        card.setBorder(new CompoundBorder(
                new LineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(30, 35, 30, 35)
        ));

        card.setMaximumSize(new Dimension(350, 420));
        card.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel cardTitle = new JLabel("Entrar no Sistema");
        cardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        cardTitle.setForeground(TEXT_PRIMARY);
        cardTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(cardTitle);

        card.add(Box.createVerticalStrut(25));

        JLabel userLabel = new JLabel("Usuario");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        userLabel.setForeground(TEXT_PRIMARY);
        userLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(userLabel);

        card.add(Box.createVerticalStrut(8));

        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setMaximumSize(new Dimension(5000, 45));
        usernameField.setHorizontalAlignment(JTextField.CENTER);
        usernameField.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));
        card.add(usernameField);

        card.add(Box.createVerticalStrut(18));

        JLabel passLabel = new JLabel("Senha");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        passLabel.setForeground(TEXT_PRIMARY);
        passLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(passLabel);

        card.add(Box.createVerticalStrut(8));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setMaximumSize(new Dimension(5000, 45));
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        passwordField.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1),
                new EmptyBorder(12, 15, 12, 15)
        ));
        passwordField.addActionListener(e -> fazerLogin());
        card.add(passwordField);

        card.add(Box.createVerticalStrut(25));

        JButton loginBtn = createStyledButton("Entrar", PRIMARY);
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> fazerLogin());
        card.add(loginBtn);

        card.add(Box.createVerticalStrut(12));

        JButton cadastroBtn = createStyledButton("Criar Conta", SUCCESS);
        cadastroBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        cadastroBtn.addActionListener(e -> abrirCadastro());
        card.add(cadastroBtn);

        return card;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setBorder(new EmptyBorder(12, 20, 12, 20));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(5000, 48));

        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });

        return button;
    }

    private void fazerLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor, preencha usuario e senha.",
                    "Campos Obrigatorios",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Passa a versão para a tela principal
        this.dispose();
        SwingUtilities.invokeLater(() -> {
            TaskManagerUI mainUI = new TaskManagerUI(service);
            mainUI.setVisible(true);
        });
    }

    private void abrirCadastro() {
        JDialog dialog = new JDialog(this, "Criar Nova Conta", true);
        dialog.setSize(420, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD);
        panel.setBorder(new EmptyBorder(30, 35, 30, 35));

        JLabel title = new JLabel("Criar Nova Conta");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(PRIMARY);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        panel.add(Box.createVerticalStrut(25));

        // Nome
        JLabel l1 = new JLabel("Nome");
        l1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(l1);
        panel.add(Box.createVerticalStrut(6));
        JTextField f1 = new JTextField();
        f1.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f1.setBorder(new CompoundBorder(new LineBorder(new Color(209, 213, 219), 1), new EmptyBorder(10, 15, 10, 15)));
        f1.setMaximumSize(new Dimension(5000, 42));
        f1.setHorizontalAlignment(JTextField.CENTER);
        panel.add(f1);
        panel.add(Box.createVerticalStrut(12));

        // Usuario
        JLabel l2 = new JLabel("Usuario");
        l2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l2.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(l2);
        panel.add(Box.createVerticalStrut(6));
        JTextField f2 = new JTextField();
        f2.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f2.setBorder(new CompoundBorder(new LineBorder(new Color(209, 213, 219), 1), new EmptyBorder(10, 15, 10, 15)));
        f2.setMaximumSize(new Dimension(5000, 42));
        f2.setHorizontalAlignment(JTextField.CENTER);
        panel.add(f2);
        panel.add(Box.createVerticalStrut(12));

        // Email
        JLabel l3 = new JLabel("Email");
        l3.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l3.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(l3);
        panel.add(Box.createVerticalStrut(6));
        JTextField f3 = new JTextField();
        f3.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f3.setBorder(new CompoundBorder(new LineBorder(new Color(209, 213, 219), 1), new EmptyBorder(10, 15, 10, 15)));
        f3.setMaximumSize(new Dimension(5000, 42));
        f3.setHorizontalAlignment(JTextField.CENTER);
        panel.add(f3);
        panel.add(Box.createVerticalStrut(12));

        // Senha
        JLabel l4 = new JLabel("Senha");
        l4.setFont(new Font("Segoe UI", Font.BOLD, 13));
        l4.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(l4);
        panel.add(Box.createVerticalStrut(6));
        JPasswordField f4 = new JPasswordField();
        f4.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        f4.setBorder(new CompoundBorder(new LineBorder(new Color(209, 213, 219), 1), new EmptyBorder(10, 15, 10, 15)));
        f4.setMaximumSize(new Dimension(5000, 42));
        f4.setHorizontalAlignment(JTextField.CENTER);
        panel.add(f4);
        panel.add(Box.createVerticalStrut(20));

        // Botões
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(5000, 50));

        JButton cancelBtn = createStyledButton("Cancelar", new Color(156, 163, 175));
        cancelBtn.setPreferredSize(new Dimension(130, 42));
        cancelBtn.addActionListener(e -> dialog.dispose());

        JButton cadastrarBtn = createStyledButton("Cadastrar", SUCCESS);
        cadastrarBtn.setPreferredSize(new Dimension(130, 42));

        cadastrarBtn.addActionListener(e -> {
            String nome = f1.getText().trim();
            String user = f2.getText().trim();
            String email = f3.getText().trim();
            String senha = new String(f4.getPassword());

            if (nome.isEmpty() || user.isEmpty() || email.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Preencha todos os campos!",
                        "Atenção",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!email.contains("@")) {
                JOptionPane.showMessageDialog(dialog,
                        "Email inválido!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (user.length() < 3 || senha.length() < 6) {
                JOptionPane.showMessageDialog(dialog,
                        "Usuário mínimo 3 caracteres e Senha mínimo 6!",
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(dialog,
                    "Conta criada com sucesso!\nUsuario: " + user,
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            usernameField.setText(user);
            passwordField.requestFocus();

            dialog.dispose();
        });

        btnPanel.add(cancelBtn);
        btnPanel.add(cadastrarBtn);
        panel.add(btnPanel);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addField(JPanel panel, JLabel label, JComponent field) {
        label.setFont(new Font("Segoe UI", Font.BOLD, 13));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        panel.add(label);
        panel.add(Box.createVerticalStrut(6));
        panel.add(field);
        panel.add(Box.createVerticalStrut(12));
    }
}