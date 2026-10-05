package com.bussin.bussin_api.dto;

public class DashboardResponse {

    private long totalUsers;
    private long totalEmployees;
    private long totalAdmins;
    private long totalBuses;
    private long activeBuses;
    private long totalRoutes;
    private long activeRoutes;
    private long totalTrips;
    private long todaysTrips;
    private long totalBookings;
    private long todaysBookings;
    private long totalQueueEntries;
    private long waitingQueueEntries;
    private long confirmedBookings;
    private long cancelledBookings;

    public DashboardResponse(
            long totalUsers,
            long totalEmployees,
            long totalAdmins,
            long totalBuses,
            long activeBuses,
            long totalRoutes,
            long activeRoutes,
            long totalTrips,
            long todaysTrips,
            long totalBookings,
            long todaysBookings,
            long totalQueueEntries,
            long waitingQueueEntries,
            long confirmedBookings,
            long cancelledBookings) {
        this.totalUsers = totalUsers;
        this.totalEmployees = totalEmployees;
        this.totalAdmins = totalAdmins;
        this.totalBuses = totalBuses;
        this.activeBuses = activeBuses;
        this.totalRoutes = totalRoutes;
        this.activeRoutes = activeRoutes;
        this.totalTrips = totalTrips;
        this.todaysTrips = todaysTrips;
        this.totalBookings = totalBookings;
        this.todaysBookings = todaysBookings;
        this.totalQueueEntries = totalQueueEntries;
        this.waitingQueueEntries = waitingQueueEntries;
        this.confirmedBookings = confirmedBookings;
        this.cancelledBookings = cancelledBookings;
    }

    public long getTotalUsers() { return totalUsers; }
    public long getTotalEmployees() { return totalEmployees; }
    public long getTotalAdmins() { return totalAdmins; }
    public long getTotalBuses() { return totalBuses; }
    public long getActiveBuses() { return activeBuses; }
    public long getTotalRoutes() { return totalRoutes; }
    public long getActiveRoutes() { return activeRoutes; }
    public long getTotalTrips() { return totalTrips; }
    public long getTodaysTrips() { return todaysTrips; }
    public long getTotalBookings() { return totalBookings; }
    public long getTodaysBookings() { return todaysBookings; }
    public long getTotalQueueEntries() { return totalQueueEntries; }
    public long getWaitingQueueEntries() { return waitingQueueEntries; }
    public long getConfirmedBookings() { return confirmedBookings; }
    public long getCancelledBookings() { return cancelledBookings; }
}
