package com.bussin.desktop.ui.flow;

public class BookingFlowState {

    private CommuterTrip selectedTrip;
    private String selectedSeat;

    private String passengerName;
    private String passengerPhone;
    private String passengerEmail;

    private String bookingId;

    public void reset() {
        selectedTrip = null;
        selectedSeat = null;
        passengerName = null;
        passengerPhone = null;
        passengerEmail = null;
        bookingId = null;
    }

    public boolean hasTrip() {
        return selectedTrip != null;
    }

    public boolean hasSeat() {
        return selectedSeat != null && !selectedSeat.isBlank();
    }

    public boolean hasPassengerInformation() {
        return passengerName != null
                && !passengerName.isBlank()
                && passengerPhone != null
                && !passengerPhone.isBlank()
                && passengerEmail != null
                && !passengerEmail.isBlank();
    }

    public CommuterTrip getSelectedTrip() {
        return selectedTrip;
    }

    public void setSelectedTrip(CommuterTrip selectedTrip) {
        this.selectedTrip = selectedTrip;
    }

    public String getSelectedSeat() {
        return selectedSeat;
    }

    public void setSelectedSeat(String selectedSeat) {
        this.selectedSeat = selectedSeat;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerPhone() {
        return passengerPhone;
    }

    public void setPassengerPhone(String passengerPhone) {
        this.passengerPhone = passengerPhone;
    }

    public String getPassengerEmail() {
        return passengerEmail;
    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }
}