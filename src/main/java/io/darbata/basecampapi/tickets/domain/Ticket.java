package io.darbata.basecampapi.tickets.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Ticket {
    private TicketStatus status;
    private int number;
    private String description;
    private OffsetDateTime updatedAt;
    private OffsetDateTime createdAt;



    private String userIdAssignedTo;
    private UUID projectId;

    private Ticket() {}

    public String getUserIdAssignedTo() {
        return userIdAssignedTo;
    }

    public void assignTo(String userIdAssignedTo) {
        this.userIdAssignedTo = userIdAssignedTo;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public int getNumber() {
        return number;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public Ticket create(TicketStatus status, int number, String description, OffsetDateTime updatedAt,
                  OffsetDateTime createdAt, String userIdAssignedTo, UUID projectId) {
        Ticket ticket = new Ticket();
        ticket.status = status;
        ticket.number = number;
        ticket.description = description;
        ticket.updatedAt = updatedAt;
        ticket.createdAt = createdAt;
        ticket.userIdAssignedTo = userIdAssignedTo;
        ticket.projectId = projectId;
        return ticket;
    }

}