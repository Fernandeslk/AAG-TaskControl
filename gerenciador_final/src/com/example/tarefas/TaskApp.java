package com.example.tarefas;

import java.util.List;
import java.util.Scanner;

public class TaskApp {
    public static void main(String[] args) {
        TaskRepository repo = new TaskRepository("tarefas.csv");
        TaskService service = new TaskService(repo);
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("=== GERENCIADOR DE TAREFAS ===");
            System.out.println("1) Listar tarefas");
            System.out.println("2) Nova tarefa");
            System.out.println("3) Editar tarefa");
            System.out.println("4) Excluir tarefa");
            System.out.println("5) Filtrar / Buscar");
            System.out.println("6) Exportar CSV");
            System.out.println("0) Sair");
            System.out.print("Escolha: ");
            String opt = sc.nextLine().trim();
            try {
                switch (opt) {
                    case "1": listar(service); break;
                    case "2": criar(service, sc); break;
                    case "3": editar(service, sc); break;
                    case "4": excluir(service, sc); break;
                    case "5": filtrar(service, sc); break;
                    case "6": exportar(service); break;
                    case "0": System.out.println("Saindo..."); sc.close(); return;
                    default: System.out.println("Opção inválida");
                }
            } catch (Exception e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
    }

    private static void listar(TaskService s) {
        List<Task> all = s.listAll();
        if (all.isEmpty()) { System.out.println("Nenhuma tarefa cadastrada."); return; }
        for (Task t : all) {
            System.out.printf("%d | %s | %s | P:%d | Prevista: %s%n",
                    t.getId(), t.getTitle(), t.getStatus(), t.getPriority(), t.getDateDue());
        }
    }

    private static void criar(TaskService s, Scanner sc) {
        System.out.print("Título: "); String ti = sc.nextLine();
        System.out.print("Descrição: "); String de = sc.nextLine();
        System.out.print("Prioridade (1-5): "); int pr = Integer.parseInt(sc.nextLine());
        System.out.print("Data prevista (YYYY-MM-DD) ou vazio: "); String d = sc.nextLine().trim();
        s.create(ti, de, pr, d);
        System.out.println("Tarefa criada.");
    }

    private static void editar(TaskService s, Scanner sc) {
        System.out.print("ID da tarefa: "); int id = Integer.parseInt(sc.nextLine());
        Task t = s.findById(id);
        System.out.print("Novo título (vazio = manter): "); String ti = sc.nextLine(); if(!ti.isBlank()) t.setTitle(ti);
        System.out.print("Nova descrição (vazio = manter): "); String de = sc.nextLine(); if(!de.isBlank()) t.setDescription(de);
        System.out.print("Nova prioridade (vazio = manter): "); String pr = sc.nextLine(); if(!pr.isBlank()) t.setPriority(Integer.parseInt(pr));
        System.out.print("Novo status (vazio = manter): "); String st = sc.nextLine(); if(!st.isBlank()) t.setStatus(st);
        System.out.print("Nova data prevista (YYYY-MM-DD) ou vazio: "); String d = sc.nextLine(); if(!d.isBlank()) t.setDateDue(d);
        s.update(t);
        System.out.println("Tarefa atualizada.");
    }

    private static void excluir(TaskService s, Scanner sc) {
        System.out.print("ID: "); int id = Integer.parseInt(sc.nextLine());
        System.out.print("Confirmar exclusão? (s/N): "); String c = sc.nextLine().trim().toLowerCase();
        if (c.equals("s")) { s.delete(id); System.out.println("Excluído."); } else System.out.println("Cancelado.");
    }

    private static void filtrar(TaskService s, Scanner sc) {
        System.out.print("Buscar por título: ");
        String q = sc.nextLine().trim();
        s.search(q).forEach(t -> System.out.println(t.getId() + " | " + t.getTitle() + " | " + t.getStatus()));
    }

    private static void exportar(TaskService s) {
        try {
            s.exportCsv("tarefas_export.csv");
            System.out.println("Exportado para tarefas_export.csv");
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }
}
