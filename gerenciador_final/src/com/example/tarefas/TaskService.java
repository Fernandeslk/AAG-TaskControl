package com.example.tarefas;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TaskService {
    private final TaskRepository repo;
    private final AtomicInteger counter = new AtomicInteger(0);

    public TaskService(TaskRepository repo) {
        this.repo = repo;
        List<Task> all = repo.loadAll();
        int max = all.stream().mapToInt(Task::getId).max().orElse(0);
        counter.set(max);
    }

    public Task create(String title, String description, int priority, String dateDue) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Título obrigatório");
        if (priority < 1 || priority > 5) throw new IllegalArgumentException("Prioridade deve estar entre 1 e 5");
        int id = counter.incrementAndGet();
        Task t = new Task(id, title.trim(), description == null ? "" : description, priority, "Pendente", dateDue == null ? "" : dateDue);
        List<Task> all = repo.loadAll();
        all.add(t);
        repo.saveAll(all);
        return t;
    }

    public List<Task> listAll() {
        return repo.loadAll();
    }

    public Task findById(int id) {
        return repo.loadAll().stream().filter(t -> t.getId() == id).findFirst()
                .orElseThrow(() -> new NoSuchElementException("Tarefa não encontrada"));
    }

    public List<Task> search(String term) {
        if (term == null) return Collections.emptyList();
        String lower = term.toLowerCase();
        return repo.loadAll().stream()
                .filter(t -> t.getTitle().toLowerCase().contains(lower) ||
                        t.getDescription().toLowerCase().contains(lower))
                .collect(Collectors.toList());
    }

    public void update(Task t) {
        List<Task> all = repo.loadAll();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == t.getId()) {
                all.set(i, t);
                found = true;
                break;
            }
        }
        if (!found) throw new NoSuchElementException("Tarefa não encontrada");
        repo.saveAll(all);
    }

    public void delete(int id) {
        List<Task> all = repo.loadAll();
        boolean removed = all.removeIf(t -> t.getId() == id);
        if (!removed) throw new NoSuchElementException("Tarefa não encontrada");
        repo.saveAll(all);
    }

    public void exportCsv(String targetFilename) throws IOException {
        Path src = repo.getPath();
        Path dst = Paths.get(targetFilename);
        Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);
    }

    public static class LoginScreen extends JFrame {

        private JTextField usernameField;
        private JPasswordField passwordField;
        private TaskService service;

        // Cores da marca
        private static final Color PRIMARY = new Color(79, 70, 229);
        private static final Color PRIMARY_DARK = new Color(67, 56, 202);
        private static final Color SUCCESS = new Color(16, 185, 129);
        private static final Color BACKGROUND = new Color(249, 250, 251);
        private static final Color CARD = Color.WHITE;
        private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
        private static final Color TEXT_SECONDARY = new Color(107, 114, 128);

        public LoginScreen(TaskService service) {
            this.service = service;

            setTitle("AAG TaskControl - Login");
            setSize(1000, 650);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setResizable(false);

            // Layout principal dividido
            JPanel mainPanel = new JPanel(new GridLayout(1, 2, 0, 0));

            // Painel esquerdo - Branding
            mainPanel.add(createBrandingPanel());

            // Painel direito - Login
            mainPanel.add(createLoginPanel());

            add(mainPanel);
        }

        private JPanel createBrandingPanel() {
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    // Gradiente de fundo
                    GradientPaint gradient = new GradientPaint(
                            0, 0, PRIMARY,
                            0, getHeight(), PRIMARY_DARK
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());

                    // Círculos decorativos
                    g2d.setColor(new Color(255, 255, 255, 30));
                    g2d.fillOval(-50, -50, 200, 200);
                    g2d.fillOval(getWidth() - 150, getHeight() - 150, 200, 200);
                    g2d.fillOval(getWidth() / 2 - 100, getHeight() / 2 + 50, 150, 150);
                }
            };

            panel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.insets = new Insets(0, 0, 30, 0);

            // Logo personalizado
            JPanel logoPanel = createLogo();
            panel.add(logoPanel, gbc);

            // Nome da empresa
            gbc.gridy = 1;
            gbc.insets = new Insets(0, 0, 10, 0);
            JLabel companyName = new JLabel("AAG TaskControl");
            companyName.setFont(new Font("Segoe UI", Font.BOLD, 42));
            companyName.setForeground(Color.WHITE);
            panel.add(companyName, gbc);

            // Slogan
            gbc.gridy = 2;
            gbc.insets = new Insets(0, 0, 50, 0);
            JLabel slogan = new JLabel("Organize, Priorize, Conquiste");
            slogan.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            slogan.setForeground(new Color(255, 255, 255, 200));
            panel.add(slogan, gbc);

            // Features
            gbc.gridy = 3;
            gbc.insets = new Insets(0, 0, 0, 0);
            panel.add(createFeaturesList(), gbc);

            return panel;
        }

        private JPanel createLogo() {
            JPanel logoPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int size = 120;
                    int x = (getWidth() - size) / 2;
                    int y = 10;

                    // Círculo externo
                    g2d.setColor(new Color(255, 255, 255, 40));
                    g2d.fillOval(x - 10, y - 10, size + 20, size + 20);

                    // Círculo principal
                    GradientPaint gradient = new GradientPaint(
                            x, y, Color.WHITE,
                            x + size, y + size, new Color(200, 220, 255)
                    );
                    g2d.setPaint(gradient);
                    g2d.fillOval(x, y, size, size);

                    // Checkmark estilizado (símbolo de tarefa)
                    g2d.setColor(PRIMARY);
                    g2d.setStroke(new BasicStroke(8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                    int checkX = x + size / 2 - 25;
                    int checkY = y + size / 2;

                    // Desenha o check
                    g2d.drawLine(checkX, checkY, checkX + 15, checkY + 15);
                    g2d.drawLine(checkX + 15, checkY + 15, checkX + 35, checkY - 15);

                    // Linhas de lista (três linhas ao lado do check)
                    g2d.setStroke(new BasicStroke(4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    g2d.setColor(new Color(79, 70, 229, 150));

                    int listX = x + 30;
                    int listY = y + 30;
                    g2d.drawLine(listX, listY, listX + 60, listY);
                    g2d.drawLine(listX, listY + 15, listX + 50, listY + 15);
                    g2d.drawLine(listX, listY + 30, listX + 55, listY + 30);
                }
            };

            logoPanel.setPreferredSize(new Dimension(140, 140));
            logoPanel.setOpaque(false);
            return logoPanel;
        }

        private JPanel createFeaturesList() {
            JPanel features = new JPanel();
            features.setLayout(new BoxLayout(features, BoxLayout.Y_AXIS));
            features.setOpaque(false);

            String[] featureList = {
                    "✓  Gestão completa de tarefas",
                    "✓  Controle de prioridades",
                    "✓  Acompanhamento de status",
                    "✓  Exportação de relatórios"
            };

            for (String feature : featureList) {
                JLabel label = new JLabel(feature);
                label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                label.setForeground(new Color(255, 255, 255, 220));
                label.setBorder(new EmptyBorder(5, 0, 5, 0));
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                features.add(label);
            }

            return features;
        }

        private JPanel createLoginPanel() {
            JPanel panel = new JPanel(new GridBagLayout());
            panel.setBackground(BACKGROUND);
            panel.setBorder(new EmptyBorder(50, 60, 50, 60));

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.insets = new Insets(0, 0, 20, 0);

            // Card de login
            JPanel loginCard = new JPanel();
            loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));
            loginCard.setBackground(CARD);
            loginCard.setBorder(new CompoundBorder(
                    new LineBorder(new Color(229, 231, 235), 1, true),
                    new EmptyBorder(40, 40, 40, 40)
            ));

            // Título
            JLabel titleLabel = new JLabel("Bem-vindo de volta!");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
            titleLabel.setForeground(TEXT_PRIMARY);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginCard.add(titleLabel);

            loginCard.add(Box.createVerticalStrut(10));

            JLabel subtitleLabel = new JLabel("Entre com suas credenciais");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            subtitleLabel.setForeground(TEXT_SECONDARY);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            loginCard.add(subtitleLabel);

            loginCard.add(Box.createVerticalStrut(35));

            // Campo de usuário
            JLabel userLabel = new JLabel("Usuário");
            userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            userLabel.setForeground(TEXT_PRIMARY);
            userLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            loginCard.add(userLabel);

            loginCard.add(Box.createVerticalStrut(8));

            usernameField = new JTextField();
            usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            usernameField.setBorder(new CompoundBorder(
                    new LineBorder(new Color(209, 213, 219), 1, true),
                    new EmptyBorder(12, 15, 12, 15)
            ));
            usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
            loginCard.add(usernameField);

            loginCard.add(Box.createVerticalStrut(20));

            // Campo de senha
            JLabel passLabel = new JLabel("Senha");
            passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
            passLabel.setForeground(TEXT_PRIMARY);
            passLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            loginCard.add(passLabel);

            loginCard.add(Box.createVerticalStrut(8));

            passwordField = new JPasswordField();
            passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            passwordField.setBorder(new CompoundBorder(
                    new LineBorder(new Color(209, 213, 219), 1, true),
                    new EmptyBorder(12, 15, 12, 15)
            ));
            passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
            passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Enter para fazer login
            passwordField.addActionListener(e -> fazerLogin());

            loginCard.add(passwordField);

            loginCard.add(Box.createVerticalStrut(30));

            // Botão de login
            JButton loginButton = createStyledButton("Entrar", PRIMARY);
            loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            loginButton.addActionListener(e -> fazerLogin());
            loginCard.add(loginButton);

            loginCard.add(Box.createVerticalStrut(15));

            // Botão de cadastro
            JButton registerButton = createStyledButton("Criar nova conta", SUCCESS);
            registerButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            registerButton.addActionListener(e -> abrirCadastro());
            loginCard.add(registerButton);

            loginCard.add(Box.createVerticalStrut(20));

            // Link "Esqueci a senha" (opcional)
            JLabel forgotPassword = new JLabel("Esqueceu sua senha?");
            forgotPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            forgotPassword.setForeground(PRIMARY);
            forgotPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
            forgotPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
            forgotPassword.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    JOptionPane.showMessageDialog(LoginScreen.this,
                            "Entre em contato com o administrador do sistema.",
                            "Recuperação de Senha",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            });
            loginCard.add(forgotPassword);

            gbc.gridy = 0;
            panel.add(loginCard, gbc);

            // Footer
            gbc.gridy = 1;
            gbc.insets = new Insets(20, 0, 0, 0);
            JLabel footer = new JLabel("© 2024 AAG TaskControl. Todos os direitos reservados.");
            footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            footer.setForeground(TEXT_SECONDARY);
            footer.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(footer, gbc);

            return panel;
        }

        private JButton createStyledButton(String text, Color color) {
            JButton button = new JButton(text);
            button.setFont(new Font("Segoe UI", Font.BOLD, 14));
            button.setForeground(Color.WHITE);
            button.setBackground(color);
            button.setBorder(new EmptyBorder(14, 20, 14, 20));
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
            button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

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
                        "Por favor, preencha todos os campos.",
                        "Campos obrigatórios",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Aqui você implementaria a validação real com banco de dados
            // Por enquanto, vamos aceitar qualquer login para demonstração
            if (validarUsuario(username, password)) {
                // Login bem-sucedido
                this.dispose();
                SwingUtilities.invokeLater(() -> {
                    TaskManagerUI mainUI = new TaskManagerUI(service);
                    mainUI.setVisible(true);
                });
            } else {
                JOptionPane.showMessageDialog(this,
                        "Usuário ou senha incorretos.",
                        "Erro de autenticação",
                        JOptionPane.ERROR_MESSAGE);
                passwordField.setText("");
            }
        }

        private boolean validarUsuario(String username, String password) {
            // TEMPORÁRIO: Aceita qualquer usuário para demonstração
            // Você deve implementar a validação real aqui
            // Exemplo: consultar banco de dados, arquivo, etc.

            // Login de demonstração
            return !username.isEmpty() && !password.isEmpty();
        }

        private void abrirCadastro() {
            JDialog cadastroDialog = new JDialog(this, "Criar Nova Conta", true);
            cadastroDialog.setSize(450, 520);
            cadastroDialog.setLocationRelativeTo(this);
            cadastroDialog.setLayout(new BorderLayout());

            JPanel formPanel = new JPanel();
            formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
            formPanel.setBackground(CARD);
            formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

            // Título
            JLabel titleLabel = new JLabel("Criar Nova Conta");
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
            titleLabel.setForeground(TEXT_PRIMARY);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(titleLabel);

            formPanel.add(Box.createVerticalStrut(10));

            JLabel subtitleLabel = new JLabel("Preencha os dados abaixo");
            subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            subtitleLabel.setForeground(TEXT_SECONDARY);
            subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            formPanel.add(subtitleLabel);

            formPanel.add(Box.createVerticalStrut(25));

            // Nome completo
            addFormField(formPanel, "Nome Completo", new JTextField());

            formPanel.add(Box.createVerticalStrut(15));

            // Email
            addFormField(formPanel, "Email", new JTextField());

            formPanel.add(Box.createVerticalStrut(15));

            // Usuário
            JTextField newUsername = new JTextField();
            addFormField(formPanel, "Usuário", newUsername);

            formPanel.add(Box.createVerticalStrut(15));

            // Senha
            JPasswordField newPassword = new JPasswordField();
            addFormField(formPanel, "Senha", newPassword);

            formPanel.add(Box.createVerticalStrut(15));

            // Confirmar senha
            JPasswordField confirmPassword = new JPasswordField();
            addFormField(formPanel, "Confirmar Senha", confirmPassword);

            formPanel.add(Box.createVerticalStrut(25));

            // Botões
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
            buttonPanel.setOpaque(false);
            buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

            JButton cadastrarBtn = createStyledButton("Cadastrar", SUCCESS);
            cadastrarBtn.setMaximumSize(new Dimension(150, 45));
            cadastrarBtn.addActionListener(e -> {
                String senha = new String(newPassword.getPassword());
                String confirmSenha = new String(confirmPassword.getPassword());

                if (!senha.equals(confirmSenha)) {
                    JOptionPane.showMessageDialog(cadastroDialog,
                            "As senhas não coincidem!",
                            "Erro",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (newUsername.getText().trim().isEmpty() || senha.isEmpty()) {
                    JOptionPane.showMessageDialog(cadastroDialog,
                            "Preencha todos os campos!",
                            "Campos obrigatórios",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Aqui você salvaria o usuário no banco de dados
                JOptionPane.showMessageDialog(cadastroDialog,
                        "Conta criada com sucesso!\nFaça login para continuar.",
                        "Sucesso",
                        JOptionPane.INFORMATION_MESSAGE);
                cadastroDialog.dispose();
            });

            JButton cancelarBtn = createStyledButton("Cancelar", new Color(156, 163, 175));
            cancelarBtn.setMaximumSize(new Dimension(150, 45));
            cancelarBtn.addActionListener(e -> cadastroDialog.dispose());

            buttonPanel.add(cancelarBtn);
            buttonPanel.add(cadastrarBtn);
            formPanel.add(buttonPanel);

            cadastroDialog.add(formPanel, BorderLayout.CENTER);
            cadastroDialog.setVisible(true);
        }

        private void addFormField(JPanel panel, String labelText, JTextField field) {
            JLabel label = new JLabel(labelText);
            label.setFont(new Font("Segoe UI", Font.BOLD, 13));
            label.setForeground(TEXT_PRIMARY);
            label.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(label);

            panel.add(Box.createVerticalStrut(8));

            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            field.setBorder(new CompoundBorder(
                    new LineBorder(new Color(209, 213, 219), 1, true),
                    new EmptyBorder(10, 15, 10, 15)
            ));
            field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            field.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(field);
        }
    }
}
