import { afterEach, describe, expect, it, vi } from "vitest";
import { cleanup, render, screen, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";

const { getEmployeeBookings, updateEmployeeBookingPaymentStatus } = vi.hoisted(() => ({
  getEmployeeBookings: vi.fn(),
  updateEmployeeBookingPaymentStatus: vi.fn(),
}));

vi.mock("../../services/adminBookingService", () => ({
  getEmployeeBookings,
  updateEmployeeBookingPaymentStatus,
}));

vi.mock("../../components/booking/ETicket", () => ({ default: () => null }));

import EmployeeBookings from "./EmployeeBookings";

afterEach(() => cleanup());

const booking = {
  id: 17,
  bookingReference: "BK-TEST-17",
  passengerName: "Test Passenger",
  passengerEmail: "passenger@example.com",
  passengerPhone: "09123456789",
  commuterName: "Test Passenger",
  commuterEmail: "passenger@example.com",
  routeIdentifier: "BAC-MNL",
  tripId: 9,
  scheduledDeparture: "2026-11-01T08:00:00",
  busPlateNumber: "ABC-1234",
  seatNumber: "1A",
  seatNumbers: ["1A"],
  seatCount: 1,
  status: "CONFIRMED",
  paymentStatus: "UNPAID",
  queueNumber: 1,
  queueStatus: "WAITING",
  guestBooking: false,
};

describe("Employee bookings payment controls", () => {
  it("saves a payment status change for a listed booking", async () => {
    const user = userEvent.setup();
    getEmployeeBookings.mockResolvedValue([booking]);
    updateEmployeeBookingPaymentStatus.mockResolvedValue({ ...booking, paymentStatus: "PAID" });

    render(<EmployeeBookings />);

    const paymentStatus = await screen.findByRole("combobox", {
      name: "Payment status for BK-TEST-17",
    });
    await user.selectOptions(paymentStatus, "PAID");

    await waitFor(() => expect(updateEmployeeBookingPaymentStatus).toHaveBeenCalledWith(17, "PAID"));
    await waitFor(() => expect(paymentStatus.value).toBe("PAID"));
  });
});
