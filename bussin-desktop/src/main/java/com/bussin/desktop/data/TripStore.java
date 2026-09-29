package com.bussin.desktop.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.bussin.desktop.ui.flow.CommuterTrip;

public final class TripStore {

    private static final List<CommuterTrip> TRIPS = new ArrayList<>();

    static {
        seed();
    }

    private TripStore() {
    }

    private static void seed() {
        if (!TRIPS.isEmpty()) {
            return;
        }

        CommuterTrip trip102 = new CommuterTrip(
                "TR-102",
                "10:00 AM",
                "12:30 PM",
                "Manila",
                "Batangas",
                "BUS-102",
                450,
                45,
                3);

        trip102.reserveSeat("03A");
        trip102.reserveSeat("05C");
        trip102.reserveSeat("12A");

        CommuterTrip trip114 = new CommuterTrip(
                "TR-114",
                "10:30 AM",
                "1:45 PM",
                "Manila",
                "Lucena",
                "BUS-114",
                520,
                45,
                3);

        trip114.reserveSeat("08A");
        trip114.reserveSeat("08B");
        trip114.reserveSeat("10A");

        CommuterTrip trip121 = new CommuterTrip(
                "TR-121",
                "11:00 AM",
                "1:30 PM",
                "Manila",
                "Nasugbu",
                "BUS-121",
                380,
                45,
                3);

        trip121.reserveSeat("14D");
        trip121.reserveSeat("22A");
        trip121.reserveSeat("22B");

        CommuterTrip trip125 = new CommuterTrip(
                "TR-125",
                "1:00 PM",
                "8:30 PM",
                "Manila",
                "Naga",
                "BUS-125",
                720,
                49,
                0);

        TRIPS.add(trip102);
        TRIPS.add(trip114);
        TRIPS.add(trip121);
        TRIPS.add(trip125);
    }

    public static List<CommuterTrip> getAll() {
        return Collections.unmodifiableList(TRIPS);
    }

    public static CommuterTrip findById(String tripId) {
        if (tripId == null) {
            return null;
        }

        for (CommuterTrip trip : TRIPS) {
            if (trip.getTripId().equalsIgnoreCase(tripId)) {
                return trip;
            }
        }

        return null;
    }

    public static List<CommuterTrip> search(
            String origin,
            String destination) {

        List<CommuterTrip> results = new ArrayList<>();

        String originValue = origin == null ? "" : origin.trim().toLowerCase();
        String destinationValue = destination == null ? "" : destination.trim().toLowerCase();

        for (CommuterTrip trip : TRIPS) {

            boolean originMatches = originValue.isEmpty()
                    || trip.getOrigin().toLowerCase().contains(originValue);

            boolean destinationMatches = destinationValue.isEmpty()
                    || trip.getDestination().toLowerCase().contains(destinationValue);

            if (originMatches && destinationMatches) {
                results.add(trip);
            }
        }

        return results;
    }

    public static boolean reserveSeat(String tripId, String seat) {

        CommuterTrip trip = findById(tripId);

        if (trip == null) {
            return false;
        }

        return trip.reserveSeat(seat);
    }

    public static boolean isSeatAvailable(String tripId, String seat) {

        CommuterTrip trip = findById(tripId);

        if (trip == null) {
            return false;
        }

        return trip.isSeatAvailable(seat);
    }
}