import { afterEach, describe, expect, it, vi } from "vitest";
import { cleanup, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

const { getAllBookings, updateAdminBookingPaymentStatus, updateBookingStatus } = vi.hoisted(() => ({
  getAllBookings: vi.fn(),
  updateAdminBookingPaymentStatus: vi.fn(),
  updateBookingStatus: vi.fn(),
}));

vi.mock("../../services/adminBookingService", () => ({
  getAllBookings,
  updateAdminBookingPaymentStatus,
  updateBookingStatus,
}));

vi.mock("../../components/booking/ETicket", () => ({ default: () => null }));

import Bookings from "./Bookings";

afterEach(() => cleanup());

const booking = {
  id: 17,
  bookingReference: "BK-TEST-17",
  passengerName: "Test Passenger",
  passengerEmail: "passenger@example.com",
  passengerPhone: "09123456789",
  commuterName: "Test Passenger",
  commuterEmail: "passenger@example.com",
  origin: "Bacolod",
  destination: "Manila",
  routeIdentifier: "BAC-MNL",
  tripId: 9,
  scheduledDeparture: "2026-11-01T08:00:00",
  scheduledArrival: "2026-11-01T18:00:00",
  busPlateNumber: "ABC-1234",
  seatNumber: "1A",
  seatNumbers: ["1A"],
  fare: 1000,
  status: "CONFIRMED",
  paymentStatus: "UNPAID",
  queueNumber: 1,
  queueStatus: "WAITING",
  guestBooking: false,
  createdAt: "2026-10-07T08:00:00",
};

describe("Admin bookings payment controls", () => {
  it("saves a payment status change and updates the booking details", async () => {
    const user = userEvent.setup();
    getAllBookings.mockResolvedValue([booking]);
    updateAdminBookingPaymentStatus.mockResolvedValue({ ...booking, paymentStatus: "PAID" });

    render(<Bookings />);

    await user.click(await screen.findByRole("button", { name: /details/i }));
    const paymentStatus = screen.getByRole("combobox", { name: "Payment status" });
    await user.selectOptions(paymentStatus, "PAID");

    await waitFor(() => expect(updateAdminBookingPaymentStatus).toHaveBeenCalledWith(17, "PAID"));
    await waitFor(() => expect(paymentStatus.value).toBe("PAID"));
  });
});
