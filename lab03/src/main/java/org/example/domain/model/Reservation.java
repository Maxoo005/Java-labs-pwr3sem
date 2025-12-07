package org.example.domain.model;

import java.time.*;


//opis pojedynczej rezerwaci w salonie fryzjerskim
public class Reservation {
    private Long id;
    private long shopId;
    private String serviceName;
    private Long clientId;         // może być null
    private Long employeeId;       // może być null
    private LocalDate date;        // YYYY-MM-DD
    private LocalTime time;        // HH:mm
    private ReservationStatus status;
    private Instant createdAtUtc;
    private Instant updatedAtUtc;
    private int priceCents;

    //pełny konstrultor
    public Reservation(Long id, long shopId, String serviceName, Long clientId, Long employeeId,
                       LocalDate date, LocalTime time, ReservationStatus status,
                       Instant createdAtUtc, Instant updatedAtUtc, int priceCents) {
        this.id = id;
        this.shopId = shopId;
        this.serviceName = serviceName;
        this.clientId = clientId;
        this.employeeId = employeeId;
        this.date = date;
        this.time = time;
        this.status = status;
        this.createdAtUtc = createdAtUtc;
        this.updatedAtUtc = updatedAtUtc;
        this.priceCents = priceCents;
    }

    //get i set
    public Long getId() {
        return id;
    }
    public long getShopId() {
        return shopId;
    }
    public String getServiceName() {
        return serviceName;
    }
    public Long getClientId() {
        return clientId;
    }
    public Long getEmployeeId() {
        return employeeId;
    }
    public LocalDate getDate() {
        return date;
    }
    public LocalTime getTime() {
        return time;
    }
    public ReservationStatus getStatus() {
        return status;
    }
    public Instant getCreatedAtUtc() {
        return createdAtUtc;
    }
    public Instant getUpdatedAtUtc() {
        return updatedAtUtc;
    }
    public int getPriceCents() {
        return priceCents;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    public void setUpdatedAtUtc(Instant updatedAtUtc) {
        this.updatedAtUtc = updatedAtUtc;
    }

    @Override public String toString() {
        return "Reservation{id=%s, shop=%d, date=%s %s, service='%s', price=%d, status=%s, client=%s, emp=%s}"
                .formatted(id, shopId, date, time, serviceName, priceCents, status, clientId, employeeId);
    }
}
