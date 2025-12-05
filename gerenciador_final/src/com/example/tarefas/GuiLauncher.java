package com.example.tarefas;

public class GuiLauncher {
    public static void main(String[] args){
        TaskRepository repo = new TaskRepository("tarefas.csv");
        TaskService service = new TaskService(repo);

        javax.swing.SwingUtilities.invokeLater(() -> {
            // Abre a nova tela de login simples
            SimpleLoginScreen loginScreen = new SimpleLoginScreen(service);
            loginScreen.setVisible(true);
        });
    }
}