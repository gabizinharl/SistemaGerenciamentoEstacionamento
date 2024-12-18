import javax.swing.table.DefaultTableModel;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ParkingLot {
    private final double hourlyRate;
    private int capacity;  // Removido o final para permitir alteração
    private int currentTicketsCount;
    private double totalRevenue;
    private final Map<String, Ticket> tickets;

    public ParkingLot(double hourlyRate, int capacity) {
        this.hourlyRate = hourlyRate;
        this.capacity = capacity;
        this.tickets = new HashMap<>();
    }

    public Ticket generateTicket(String vehicleNumber, String vehicleDescription) {
        if (currentTicketsCount >= capacity) {
            return null; // Capacidade atingida
        }
        Ticket ticket = new Ticket(vehicleNumber, vehicleDescription, hourlyRate);
        tickets.put(ticket.getTicketId(), ticket);
        currentTicketsCount++;
        return ticket;
    }

    public boolean payTicket(String ticketId) {
        Ticket ticket = tickets.get(ticketId);
        if (ticket != null && !ticket.isPaid()) {
            double amountDue = ticket.calculatePayment();
            ticket.markAsPaid();
            totalRevenue += amountDue;
            currentTicketsCount--;
            return true;
        }
        return false;
    }

    public void exportToTxt(String filename, DefaultTableModel model) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("ID do Ticket, Número do Veículo, Descrição, Entrada, Saída, Pago\n");
            for (int i = 0; i < model.getRowCount(); i++) {
                writer.write(String.format("%s, %s, %s, %s, %s, %s\n",
                        model.getValueAt(i, 0),
                        model.getValueAt(i, 1),
                        model.getValueAt(i, 2),
                        model.getValueAt(i, 3),
                        model.getValueAt(i, 4),
                        model.getValueAt(i, 5)));
            }
        }
    }

    // Novo método para exportar tickets para um arquivo CSV
    public void exportTickets(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("ID do Ticket, Número do Veículo, Descrição, Entrada, Saída, Pago\n");
            for (Ticket ticket : tickets.values()) {
                writer.write(String.format("%s, %s, %s, %s, %s, %s\n",
                        ticket.getTicketId(),
                        ticket.getVehicleNumber(),
                        ticket.getVehicleDescription(),
                        ticket.getEntryTime(),
                        ticket.getExitTime() != null ? ticket.getExitTime() : "Em andamento",
                        ticket.isPaid() ? "Sim" : "Não"));
            }
        }
    }

    public int getAvailableSpots() {
        return capacity - currentTicketsCount;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    // Método para alterar a capacidade do estacionamento
    public void setCapacity(int newCapacity) {
        if (newCapacity >= currentTicketsCount) {
            this.capacity = newCapacity;
        } else {
            throw new IllegalArgumentException("A nova capacidade não pode ser menor que o número de veículos estacionados.");
        }
    }
}