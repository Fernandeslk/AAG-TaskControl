package com.example.tarefas;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class TaskManagerUI extends JFrame {

    private final TaskService service;
    private DefaultTableModel model;
    private JTable table;
    private JLabel statsLabel;
    private JTextField searchField;

    // Paleta de cores moderna
    private static final Color PRIMARY = new Color(79, 70, 229);
    private static final Color PRIMARY_DARK = new Color(67, 56, 202);
    private static final Color SUCCESS = new Color(16, 185, 129);
    private static final Color DANGER = new Color(239, 68, 68);
    private static final Color WARNING = new Color(245, 158, 11);
    private static final Color INFO = new Color(59, 130, 246);
    private static final Color BACKGROUND = new Color(249, 250, 251);
    private static final Color CARD = Color.WHITE;
    private static final Color TEXT_PRIMARY = new Color(17, 24, 39);
    private static final Color TEXT_SECONDARY = new Color(107, 114, 128);

    public TaskManagerUI(TaskService service) {
        this.service = service;
        this.model = model;
        this.table = table;

        setTitle("Gerenciador de Tarefas Pro");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout(0, 0));

        add(createHeader(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH);

        load();
        updateStats();
    }

    public TaskManagerUI(TaskService service, TaskService service1) {
        this.service = service1;
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout(15, 15));
        header.setBackground(CARD);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 2, 0, new Color(229, 231, 235)),
                new EmptyBorder(20, 25, 20, 25)
        ));

        // Título e estatísticas
        JPanel leftPanel = new JPanel(new BorderLayout(10, 5));
        leftPanel.setOpaque(false);

        JLabel titleLabel = new JLabel("Gerenciador de Tarefas");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_PRIMARY);

        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        statsLabel.setForeground(TEXT_SECONDARY);

        leftPanel.add(titleLabel, BorderLayout.NORTH);
        leftPanel.add(statsLabel, BorderLayout.CENTER);

        // Barra de pesquisa
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(400, 42));

        searchField = new JTextField();
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        searchField.setBackground(Color.WHITE);

        // Listener para buscar ao digitar Enter
        searchField.addActionListener(e -> buscar());

        JButton searchBtn = createIconButton("Buscar", PRIMARY);
        searchBtn.addActionListener(e -> buscar());

        JButton clearBtn = createIconButton("Limpar", TEXT_SECONDARY);
        clearBtn.addActionListener(e -> {
            searchField.setText("");
            load();
        });

        searchPanel.add(searchField, BorderLayout.CENTER);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        btnPanel.setOpaque(false);
        btnPanel.add(searchBtn);
        btnPanel.add(clearBtn);
        searchPanel.add(btnPanel, BorderLayout.EAST);

        header.add(leftPanel, BorderLayout.WEST);
        header.add(searchPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(15, 15));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));

        // Toolbar com botões de ação
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        JButton novoBtn = createActionButton("Nova Tarefa", SUCCESS);
        JButton editarBtn = createActionButton("Editar", PRIMARY);
        JButton excluirBtn = createActionButton("Excluir", DANGER);
        JButton exportarBtn = createActionButton("Exportar CSV", INFO);
        JButton sairBtn = createActionButton("Sair", TEXT_SECONDARY);

        novoBtn.addActionListener(e -> novo());
        editarBtn.addActionListener(e -> editar());
        excluirBtn.addActionListener(e -> excluir());
        exportarBtn.addActionListener(e -> exportar());
        sairBtn.addActionListener(e -> System.exit(0));

        toolbar.add(novoBtn);
        toolbar.add(editarBtn);
        toolbar.add(excluirBtn);
        toolbar.add(exportarBtn);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(sairBtn);

        // Tabela moderna
        model = new DefaultTableModel(
                new Object[]{"ID", "Título", "Descrição", "Prioridade", "Status", "Data Prevista"}, 0
        ) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(model);
        table.setRowHeight(55);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(224, 231, 255));
        table.setSelectionForeground(TEXT_PRIMARY);

        // Header da tabela
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(243, 244, 246));
        header.setForeground(TEXT_SECONDARY);
        header.setBorder(new MatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        header.setPreferredSize(new Dimension(header.getWidth(), 45));

        // Larguras das colunas
        table.getColumnModel().getColumn(0).setPreferredWidth(60);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(320);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(140);
        table.getColumnModel().getColumn(5).setPreferredWidth(130);

        // Renderer customizado
        table.setDefaultRenderer(Object.class, new ModernTableCellRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new LineBorder(new Color(229, 231, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);

        centerPanel.add(toolbar, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(CARD);
        footer.setBorder(new CompoundBorder(
                new MatteBorder(2, 0, 0, 0, new Color(229, 231, 235)),
                new EmptyBorder(12, 25, 12, 25)
        ));

        JLabel footerLabel = new JLabel("Dica: Clique duas vezes em uma tarefa para editar rapidamente");
        footerLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        footerLabel.setForeground(TEXT_SECONDARY);

        JLabel versionLabel = new JLabel("Versão 2.0 Pro");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        versionLabel.setForeground(new Color(156, 163, 175));

        footer.add(footerLabel, BorderLayout.WEST);
        footer.add(versionLabel, BorderLayout.EAST);

        // Double click para editar
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && table.getSelectedRow() != -1) {
                    editar();
                }
            }
        });

        return footer;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setBorder(new EmptyBorder(12, 24, 12, 24));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efeito hover com animação
        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(color);
            }
        });

        return btn;
    }

    private JButton createIconButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setForeground(color);
        btn.setBackground(Color.WHITE);
        btn.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(8, 14, 8, 14)
        ));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(BACKGROUND);
                btn.setBorder(new CompoundBorder(
                        new LineBorder(color, 1, true),
                        new EmptyBorder(8, 14, 8, 14)
                ));
            }
            public void mouseExited(MouseEvent e) {
                btn.setBackground(Color.WHITE);
                btn.setBorder(new CompoundBorder(
                        new LineBorder(new Color(209, 213, 219), 1, true),
                        new EmptyBorder(8, 14, 8, 14)
                ));
            }
        });

        return btn;
    }

    private void updateStats() {
        List<Task> all = service.listAll();
        long pendentes = all.stream().filter(t -> "pendente".equalsIgnoreCase(t.getStatus())).count();
        long concluidas = all.stream().filter(t -> "concluída".equalsIgnoreCase(t.getStatus())).count();
        long andamento = all.stream().filter(t -> "em andamento".equalsIgnoreCase(t.getStatus())).count();

        statsLabel.setText(String.format(
                "<html>Total: <b>%d</b> tarefas  |  <span style='color:#10b981'>Concluídas: <b>%d</b></span>  |  " +
                        "<span style='color:#3b82f6'>Em Andamento: <b>%d</b></span>  |  <span style='color:#6b7280'>Pendentes: <b>%d</b></span></html>",
                all.size(), concluidas, andamento, pendentes
        ));
    }

    private void load() {
        model.setRowCount(0);
        List<Task> all = service.listAll();
        for (Task t : all) {
            model.addRow(new Object[]{
                    t.getId(),
                    t.getTitle(),
                    t.getDescription(),
                    t.getPriority(),
                    t.getStatus(),
                    t.getDateDue()
            });
        }
        updateStats();
    }

    private void novo() {
        JDialog dialog = new JDialog(this, "Nova Tarefa", true);
        dialog.setLayout(new BorderLayout(0, 0));
        dialog.setSize(550, 480);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        // Header do diálogo
        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(PRIMARY);
        dialogHeader.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel dialogTitle = new JLabel("Criar Nova Tarefa");
        dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dialogTitle.setForeground(Color.WHITE);
        dialogHeader.add(dialogTitle, BorderLayout.WEST);

        // Formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JTextField titulo = createStyledTextField();
        JTextArea descricao = createStyledTextArea();
        JComboBox<String> prioridade = createStyledComboBox(
                new String[]{"1 - Mínima", "2 - Baixa", "3 - Média", "4 - Alta", "5 - Urgente"}
        );
        prioridade.setSelectedIndex(2);

        JTextField data = createStyledTextField();
        data.setText(java.time.LocalDate.now().toString());

        addFormField(formPanel, gbc, 0, "Título da Tarefa:", titulo);
        addFormField(formPanel, gbc, 1, "Descrição:", new JScrollPane(descricao));
        addFormField(formPanel, gbc, 2, "Nível de Prioridade:", prioridade);
        addFormField(formPanel, gbc, 3, "Data Prevista (AAAA-MM-DD):", data);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        JButton salvar = createActionButton("Salvar Tarefa", SUCCESS);
        JButton cancelar = createActionButton("Cancelar", new Color(156, 163, 175));

        salvar.addActionListener(e -> {
            try {
                String prioText = (String) prioridade.getSelectedItem();
                int prioValue = Integer.parseInt(prioText.substring(0, 1));

                service.create(
                        titulo.getText(),
                        descricao.getText(),
                        prioValue,
                        data.getText()
                );
                load();
                dialog.dispose();
                showSuccessMessage("Tarefa criada com sucesso!");
            } catch (Exception ex) {
                showErrorMessage("Erro ao criar tarefa: " + ex.getMessage());
            }
        });

        cancelar.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelar);
        btnPanel.add(salvar);

        dialog.add(dialogHeader, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void editar() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showWarningMessage("Selecione uma tarefa para editar.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        Task t = service.findById(id);

        JDialog dialog = new JDialog(this, "Editar Tarefa #" + id, true);
        dialog.setLayout(new BorderLayout(0, 0));
        dialog.setSize(550, 520);
        dialog.setLocationRelativeTo(this);
        dialog.getContentPane().setBackground(Color.WHITE);

        // Header do diálogo
        JPanel dialogHeader = new JPanel(new BorderLayout());
        dialogHeader.setBackground(PRIMARY);
        dialogHeader.setBorder(new EmptyBorder(20, 25, 20, 25));

        JLabel dialogTitle = new JLabel("Editar Tarefa #" + id);
        dialogTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dialogTitle.setForeground(Color.WHITE);
        dialogHeader.add(dialogTitle, BorderLayout.WEST);

        // Formulário
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(25, 30, 25, 30));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);

        JTextField titulo = createStyledTextField();
        titulo.setText(t.getTitle());

        JTextArea descricao = createStyledTextArea();
        descricao.setText(t.getDescription());

        JComboBox<String> prioridade = createStyledComboBox(
                new String[]{"1 - Mínima", "2 - Baixa", "3 - Média", "4 - Alta", "5 - Urgente"}
        );
        prioridade.setSelectedIndex(t.getPriority() - 1);

        JComboBox<String> status = createStyledComboBox(
                new String[]{"pendente", "em andamento", "concluída"}
        );
        status.setSelectedItem(t.getStatus());

        // Listener para mudar prioridade quando marcar como concluída
        status.addActionListener(evt -> {
            String selectedStatus = (String) status.getSelectedItem();
            if ("concluída".equalsIgnoreCase(selectedStatus)) {
                prioridade.setSelectedIndex(0); // Define como 1 - Mínima
            }
        });

        JTextField data = createStyledTextField();
        data.setText(t.getDateDue());

        addFormField(formPanel, gbc, 0, "Título da Tarefa:", titulo);
        addFormField(formPanel, gbc, 1, "Descrição:", new JScrollPane(descricao));
        addFormField(formPanel, gbc, 2, "Nível de Prioridade:", prioridade);
        addFormField(formPanel, gbc, 3, "Status Atual:", status);
        addFormField(formPanel, gbc, 4, "Data Prevista:", data);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 20));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(229, 231, 235)));

        JButton salvar = createActionButton("Salvar Alterações", SUCCESS);
        JButton cancelar = createActionButton("Cancelar", new Color(156, 163, 175));

        salvar.addActionListener(e -> {
            try {
                String prioText = (String) prioridade.getSelectedItem();
                int prioValue = Integer.parseInt(prioText.substring(0, 1));

                t.setTitle(titulo.getText());
                t.setDescription(descricao.getText());
                t.setPriority(prioValue);
                t.setStatus((String) status.getSelectedItem());
                t.setDateDue(data.getText());
                service.update(t);
                load();
                dialog.dispose();
                showSuccessMessage("Tarefa atualizada com sucesso!");
            } catch (Exception ex) {
                showErrorMessage("Erro ao atualizar: " + ex.getMessage());
            }
        });

        cancelar.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelar);
        btnPanel.add(salvar);

        dialog.add(dialogHeader, BorderLayout.NORTH);
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void excluir() {
        int row = table.getSelectedRow();
        if (row == -1) {
            showWarningMessage("Selecione uma tarefa para excluir.");
            return;
        }

        int id = (int) model.getValueAt(row, 0);
        String titulo = (String) model.getValueAt(row, 1);

        int r = JOptionPane.showConfirmDialog(
                this,
                "<html><b>Tem certeza que deseja excluir esta tarefa?</b><br><br>" +
                        "Tarefa #" + id + ": " + titulo + "<br><br>" +
                        "<span style='color:#ef4444'>Esta ação não pode ser desfeita!</span></html>",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (r == JOptionPane.YES_OPTION) {
            service.delete(id);
            load();
            showSuccessMessage("Tarefa excluída com sucesso!");
        }
    }

    private void buscar() {
        String termo = searchField.getText().trim();
        if (termo.isEmpty()) {
            load();
            return;
        }

        List<Task> resultado = service.search(termo);
        model.setRowCount(0);

        for (Task t : resultado) {
            model.addRow(new Object[]{
                    t.getId(),
                    t.getTitle(),
                    t.getDescription(),
                    t.getPriority(),
                    t.getStatus(),
                    t.getDateDue()
            });
        }

        if (resultado.isEmpty()) {
            showWarningMessage("Nenhuma tarefa encontrada com: \"" + termo + "\"");
        }
    }

    private void exportar() {
        try {
            service.exportCsv("tarefas_export.csv");
            showSuccessMessage("<html><b>Arquivo exportado com sucesso!</b><br><br>Local: tarefas_export.csv</html>");
        } catch (Exception ex) {
            showErrorMessage("Erro ao exportar: " + ex.getMessage());
        }
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setBorder(new CompoundBorder(
                new LineBorder(new Color(209, 213, 219), 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));
        field.setBackground(Color.WHITE);
        return field;
    }

    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(3, 20);
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setBorder(new EmptyBorder(10, 15, 10, 15));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        return area;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        combo.setBackground(Color.WHITE);
        return combo;
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.gridwidth = 1;

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(TEXT_PRIMARY);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
    }

    private void showSuccessMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showErrorMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    private void showWarningMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    // Renderer personalizado para células da tabela
    class ModernTableCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(new EmptyBorder(12, 15, 12, 15));

            if (!isSelected) {
                setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                setForeground(TEXT_PRIMARY);
            } else {
                setBackground(new Color(224, 231, 255));
                setForeground(TEXT_PRIMARY);
            }

            // Coluna de Prioridade
            if (column == 3 && value != null) {
                int priority = (int) value;
                setText(getPriorityBadge(priority));
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 13));

                if (!isSelected) {
                    setBackground(getPriorityBackground(priority));
                    setForeground(getPriorityColor(priority));
                }
            }

            // Coluna de Status
            if (column == 4 && value != null) {
                String status = value.toString();
                setText(getStatusBadge(status));
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 13));

                if (!isSelected) {
                    setBackground(getStatusBackground(status));
                    setForeground(getStatusColor(status));
                }
            }

            // Coluna de Data
            if (column == 5) {
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.PLAIN, 13));
            }

            // Coluna ID
            if (column == 0) {
                setHorizontalAlignment(CENTER);
                setForeground(TEXT_SECONDARY);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
            }

            // Coluna Título
            if (column == 1) {
                setFont(new Font("Segoe UI", Font.BOLD, 14));
            }

            return c;
        }

        private String getPriorityBadge(int priority) {
            return switch (priority) {
                case 5 -> "URGENTE";
                case 4 -> "ALTA";
                case 3 -> "MÉDIA";
                case 2 -> "BAIXA";
                default -> "MÍNIMA";
            };
        }

        private Color getPriorityColor(int priority) {
            return switch (priority) {
                case 5 -> new Color(153, 27, 27);   // Vermelho escuro
                case 4 -> new Color(154, 52, 18);   // Laranja escuro
                case 3 -> new Color(133, 77, 14);   // Amarelo escuro
                case 2 -> new Color(21, 128, 61);   // Verde escuro
                default -> new Color(75, 85, 99);   // Cinza escuro
            };
        }

        private Color getPriorityBackground(int priority) {
            return switch (priority) {
                case 5 -> new Color(254, 226, 226); // Vermelho claro
                case 4 -> new Color(255, 237, 213); // Laranja claro
                case 3 -> new Color(254, 249, 195); // Amarelo claro
                case 2 -> new Color(220, 252, 231); // Verde claro
                default -> new Color(243, 244, 246); // Cinza claro
            };
        }

        private String getStatusBadge(String status) {
            return switch (status.toLowerCase()) {
                case "concluída" -> "CONCLUÍDA";
                case "em andamento" -> "EM ANDAMENTO";
                default -> "PENDENTE";
            };
        }

        private Color getStatusColor(String status) {
            return switch (status.toLowerCase()) {
                case "concluída" -> new Color(21, 128, 61);   // Verde escuro
                case "em andamento" -> new Color(29, 78, 216); // Azul escuro
                default -> new Color(75, 85, 99);             // Cinza escuro
            };
        }

        private Color getStatusBackground(String status) {
            return switch (status.toLowerCase()) {
                case "concluída" -> new Color(220, 252, 231); // Verde claro
                case "em andamento" -> new Color(219, 234, 254); // Azul claro
                default -> new Color(243, 244, 246);          // Cinza claro
            };
        }
    }
}