import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class Ticket {
    private final String ticketId;
    private final String vehicleNumber;
    private final String vehicleDescription;
    private final LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private boolean isPaid;
    private final double hourlyRate;

    public Ticket(String vehicleNumber, String vehicleDescription, double hourlyRate) {
        this.ticketId = UUID.randomUUID().toString();
        this.vehicleNumber = vehicleNumber;
        this.vehicleDescription = vehicleDescription;
        this.entryTime = LocalDateTime.now();
        this.hourlyRate = hourlyRate;
        this.isPaid = false;
    }

    public String getTicketId() {
        return ticketId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getVehicleDescription() {
        return vehicleDescription;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public String getFormattedEntryTime() {
        return entryTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public double calculatePayment() {
        if (exitTime == null) {
            exitTime = LocalDateTime.now();
        }
        long hours = Duration.between(entryTime, exitTime).toHours();
        return (hours == 0 ? 1 : hours) * hourlyRate;
    }

    public void markAsPaid() {
        this.isPaid = true;
        this.exitTime = LocalDateTime.now();
    }
}
