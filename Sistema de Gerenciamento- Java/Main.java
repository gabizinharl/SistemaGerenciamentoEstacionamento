import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class Main {
    private static ParkingLot parkingLot = new ParkingLot(10.0, 50);
    private static JTable ticketTable;
    private static DefaultTableModel model;
    private static JLabel availableSpotsLabel;
    private static JLabel totalRevenueLabel;
    private static TableRowSorter<DefaultTableModel> sorter;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Sistema de Gerenciamento de Estacionamento");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 700);
        frame.setLocationRelativeTo(null);
        
        // Definindo o ícone da janela a partir de um recurso
        ImageIcon icon = new ImageIcon("logo.png"); // A imagem deve estar no mesmo diretório do Main.java
        frame.setIconImage(icon.getImage());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.RED); // Fundo vermelho
        JLabel titleLabel = new JLabel("Sistema de Gerenciamento de Estacionamento");
        titleLabel.setForeground(Color.BLACK); // Texto preto
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(titleLabel);
        
        JPanel mainPanel = createMainPanel();

        frame.setLayout(new BorderLayout());
        frame.add(titlePanel, BorderLayout.NORTH);
        frame.add(mainPanel, BorderLayout.CENTER);
        
        frame.setVisible(true);
    }

    private static JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel summaryPanel = createSummaryPanel();
        JPanel actionPanel = createActionPanel();
        JScrollPane scrollPane = createTablePanel();
        JPanel filterPanel = createFilterPanel();

        mainPanel.add(summaryPanel, BorderLayout.WEST);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(actionPanel, BorderLayout.EAST);
        mainPanel.add(filterPanel, BorderLayout.NORTH);

        return mainPanel;
    }

    private static JPanel createSummaryPanel() {
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Resumo"));

        availableSpotsLabel = new JLabel("Vagas Disponíveis: " + parkingLot.getAvailableSpots());
        totalRevenueLabel = new JLabel("Receita Total: R$ 0.00");
        availableSpotsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton changeCapacityButton = new JButton("Alterar Capacidade");
        changeCapacityButton.addActionListener(e -> openLoginWindow());

        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(availableSpotsLabel);
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(totalRevenueLabel);
        summaryPanel.add(Box.createVerticalStrut(10));
        summaryPanel.add(changeCapacityButton);

        return summaryPanel;
    }

    private static void openLoginWindow() {
        JFrame loginFrame = new JFrame("Login de Administração");
        loginFrame.setSize(300, 150);
        loginFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));

        JLabel userLabel = new JLabel("Usuário:");
        JTextField userField = new JTextField();
        JLabel passLabel = new JLabel("Senha:");
        JPasswordField passField = new JPasswordField();

        JButton loginButton = new JButton("Entrar");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = userField.getText();
                String password = new String(passField.getPassword());

                if ("admin".equals(username) && "1234".equals(password)) { // Simples validação de exemplo
                    loginFrame.dispose();
                    changeCapacity();
                } else {
                    JOptionPane.showMessageDialog(loginFrame, "Usuário ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginButton);

        loginFrame.add(panel);
        loginFrame.setVisible(true);
    }

    private static void changeCapacity() {
        String capacityStr = JOptionPane.showInputDialog("Digite a nova capacidade:");
        if (capacityStr != null) {
            try {
                int newCapacity = Integer.parseInt(capacityStr);
                parkingLot.setCapacity(newCapacity);
                updateSummaryLabels();
                JOptionPane.showMessageDialog(null, "Capacidade alterada com sucesso.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Por favor, insira um número válido.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static JScrollPane createTablePanel() {
        model = new DefaultTableModel(new Object[]{"ID do Ticket", "Número do Veículo", "Descrição", "Entrada", "Saída", "Pago"}, 0);
        ticketTable = new JTable(model);
        ticketTable.setFillsViewportHeight(true);
        sorter = new TableRowSorter<>(model);
        ticketTable.setRowSorter(sorter);
        return new JScrollPane(ticketTable);
    }

    private static JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Ações"));

        JLabel vehicleLabel = new JLabel("Número do Veículo:");
        JTextField vehicleField = new JTextField();

        JLabel descriptionLabel = new JLabel("Descrição do Veículo:");
        JTextField descriptionField = new JTextField();

        JButton generateButton = createGenerateButton(vehicleField, descriptionField);
        JButton payButton = createPayButton();
        JButton exportButton = createExportButton();

        actionPanel.add(vehicleLabel);
        actionPanel.add(vehicleField);
        actionPanel.add(descriptionLabel);
        actionPanel.add(descriptionField);
        actionPanel.add(generateButton);
        actionPanel.add(payButton);
        actionPanel.add(exportButton);

        return actionPanel;
    }

    private static JButton createGenerateButton(JTextField vehicleField, JTextField descriptionField) {
        JButton generateButton = new JButton("Gerar Ticket");
        generateButton.setBackground(new Color(60, 179, 113));
        generateButton.setForeground(Color.WHITE);

        generateButton.addActionListener(e -> {
            String vehicleNumber = vehicleField.getText();
            String vehicleDescription = descriptionField.getText();
            if (!vehicleNumber.isEmpty() && !vehicleDescription.isEmpty()) {
                Ticket newTicket = parkingLot.generateTicket(vehicleNumber, vehicleDescription);
                if (newTicket != null) {
                    model.addRow(new Object[]{
                        newTicket.getTicketId(),
                        newTicket.getVehicleNumber(),
                        newTicket.getVehicleDescription(),
                        newTicket.getEntryTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.of("America/Sao_Paulo"))),
                        "Ainda estacionado",
                        "Não"
                    });
                    vehicleField.setText("");
                    descriptionField.setText("");
                    updateSummaryLabels();
                    JOptionPane.showMessageDialog(null, "Ticket gerado com sucesso para o veículo " + vehicleNumber);
                } else {
                    JOptionPane.showMessageDialog(null, "Capacidade do estacionamento atingida.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Por favor, insira o número do veículo e a descrição.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return generateButton;
    }

    private static JButton createPayButton() {
        JButton payButton = new JButton("Pagar Ticket Selecionado");
        payButton.setBackground(new Color(255, 69, 0));
        payButton.setForeground(Color.WHITE);

        payButton.addActionListener(e -> {
            int selectedRow = ticketTable.getSelectedRow();
            if (selectedRow >= 0) {
                String ticketId = model.getValueAt(selectedRow, 0).toString();
                boolean success = parkingLot.payTicket(ticketId);
                if (success) {
                    model.setValueAt("Pago", selectedRow, 5);
                    model.setValueAt(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), selectedRow, 4);
                    updateSummaryLabels();
                    JOptionPane.showMessageDialog(null, "Pagamento realizado com sucesso.");
                } else {
                    JOptionPane.showMessageDialog(null, "Erro ao pagar o ticket.", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Selecione um ticket para pagar.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return payButton;
    }

    private static JButton createExportButton() {
        JButton exportButton = new JButton("Exportar Tickets");
        exportButton.setBackground(new Color(30, 144, 255));
        exportButton.setForeground(Color.WHITE);

        exportButton.addActionListener(e -> {
            try {
                parkingLot.exportTickets("tickets.csv");
                JOptionPane.showMessageDialog(null, "Tickets exportados com sucesso.");
            } catch (IOException ioException) {
                JOptionPane.showMessageDialog(null, "Erro ao exportar os tickets.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });

        return exportButton;
    }

    private static JPanel createFilterPanel() {
        JPanel filterPanel = new JPanel();
        filterPanel.setBorder(BorderFactory.createTitledBorder("Filtrar:"));
        JLabel filterLabel = new JLabel("Filtrar:");
        JTextField filterField = new JTextField(20);
        filterField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                String filterText = filterField.getText();
                sorter.setRowFilter(RowFilter.regexFilter(filterText));
            }
        });

        filterPanel.add(filterLabel);
        filterPanel.add(filterField);
        return filterPanel;
    }

    private static void updateSummaryLabels() {
        availableSpotsLabel.setText("Vagas Disponíveis: " + parkingLot.getAvailableSpots());
        totalRevenueLabel.setText("Receita Total: R$ " + String.format("%.2f", parkingLot.getTotalRevenue()));
    }
}