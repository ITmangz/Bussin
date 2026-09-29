package com.bussin.desktop.reports;

import com.bussin.desktop.ui.screens.ReportScreen.ReportData;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ReportExporter {

    private ReportExporter() {
    }

    // ============================================================
    // TXT
    // ============================================================

    public static void exportTXT(
            File file,
            String period,
            ReportData data) throws IOException {

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(buildTextReport(period, data));
        }
    }

    // ============================================================
    // CSV
    // ============================================================

    public static void exportCSV(
            File file,
            String period,
            ReportData data) throws IOException {

        try (FileWriter writer = new FileWriter(file)) {

            writer.write("BUSSIN REPORT\r\n");
            writer.write("Report Period," + csv(period) + "\r\n");
            writer.write("\r\n");

            writer.write("BOOKING OVERVIEW\r\n");
            writer.write("Metric,Value\r\n");
            writer.write("Total Bookings," + data.totalBookings + "\r\n");
            writer.write("Pending Bookings," + data.pendingBookings + "\r\n");
            writer.write("Confirmed Bookings," + data.confirmedBookings + "\r\n");
            writer.write("Completed Bookings," + data.completedBookings + "\r\n");
            writer.write("Cancelled Bookings," + data.cancelledBookings + "\r\n");
            writer.write("\r\n");

            writer.write("BUS STATUS\r\n");
            writer.write("Status,Count\r\n");
            writer.write("Available," + data.availableBuses + "\r\n");
            writer.write("On Trip," + data.onTripBuses + "\r\n");
            writer.write("Maintenance," + data.maintenanceBuses + "\r\n");
            writer.write("Inactive," + data.inactiveBuses + "\r\n");
            writer.write("\r\n");

            writer.write("ROUTE SUMMARY\r\n");
            writer.write("Metric,Value\r\n");
            writer.write("Active Routes," + data.activeRoutes + "\r\n");
            writer.write("Inactive Routes," + data.inactiveRoutes + "\r\n");
            writer.write("Total Distance (km)," + data.totalDistance + "\r\n");
            writer.write("Average Fare," + data.averageFare + "\r\n");
            writer.write("\r\n");

            writer.write("EMPLOYEE SUMMARY\r\n");
            writer.write("Metric,Value\r\n");
            writer.write("Total Employees," + data.totalEmployees + "\r\n");
            writer.write("Active Employees," + data.activeEmployees + "\r\n");
            writer.write("Inactive Employees," + data.inactiveEmployees + "\r\n");
            writer.write("Bus Drivers," + data.busDrivers + "\r\n");
            writer.write("Dispatchers," + data.dispatchers + "\r\n");
            writer.write("Ticketing Staff," + data.ticketingStaff + "\r\n");
            writer.write("Fleet Supervisors," + data.fleetSupervisors + "\r\n");
            writer.write("\r\n");

            writer.write("TRIP OVERVIEW\r\n");
            writer.write("Metric,Value\r\n");
            writer.write("Completed Trips," + data.completedTrips + "\r\n");
            writer.write("\r\n");
        }
    }

    // ============================================================
    // JSON
    // ============================================================

    public static void exportJSON(
            File file,
            String period,
            ReportData data) throws IOException {

        Map<String, Object> report = new LinkedHashMap<>();

        report.put("system", "BUSSIN");
        report.put("reportPeriod", period);

        Map<String, Object> overview = new LinkedHashMap<>();
        overview.put("totalBookings", data.totalBookings);
        overview.put("completedTrips", data.completedTrips);
        overview.put("activeBuses", data.activeBuses);
        overview.put("activeEmployees", data.activeEmployees);

        report.put("overview", overview);

        Map<String, Object> bookings = new LinkedHashMap<>();
        bookings.put("pending", data.pendingBookings);
        bookings.put("confirmed", data.confirmedBookings);
        bookings.put("completed", data.completedBookings);
        bookings.put("cancelled", data.cancelledBookings);

        report.put("bookings", bookings);

        Map<String, Object> buses = new LinkedHashMap<>();
        buses.put("available", data.availableBuses);
        buses.put("onTrip", data.onTripBuses);
        buses.put("maintenance", data.maintenanceBuses);
        buses.put("inactive", data.inactiveBuses);

        report.put("buses", buses);

        Map<String, Object> routes = new LinkedHashMap<>();
        routes.put("active", data.activeRoutes);
        routes.put("inactive", data.inactiveRoutes);
        routes.put("totalDistanceKm", data.totalDistance);
        routes.put("averageFare", data.averageFare);

        report.put("routes", routes);

        Map<String, Object> employees = new LinkedHashMap<>();
        employees.put("total", data.totalEmployees);
        employees.put("active", data.activeEmployees);
        employees.put("inactive", data.inactiveEmployees);
        employees.put("busDrivers", data.busDrivers);
        employees.put("dispatchers", data.dispatchers);
        employees.put("ticketingStaff", data.ticketingStaff);
        employees.put("fleetSupervisors", data.fleetSupervisors);

        report.put("employees", employees);

        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        mapper.writeValue(file, report);
    }

    // ============================================================
    // DOCX
    // ============================================================

    public static void exportDOCX(
            File file,
            String period,
            ReportData data) throws IOException {

        try (XWPFDocument document = new XWPFDocument()) {

            addHeading(document, "BUSSIN");
            addHeading(document, "Reports & Analytics");

            addParagraph(document, "Report Period: " + period);

            addHeading(document, "Overview");

            addMetric(document, "Total Bookings", data.totalBookings);
            addMetric(document, "Completed Trips", data.completedTrips);
            addMetric(document, "Active Buses", data.activeBuses);
            addMetric(document, "Active Employees", data.activeEmployees);

            addHeading(document, "Booking Overview");

            addMetric(document, "Pending Bookings", data.pendingBookings);
            addMetric(document, "Confirmed Bookings", data.confirmedBookings);
            addMetric(document, "Completed Bookings", data.completedBookings);
            addMetric(document, "Cancelled Bookings", data.cancelledBookings);

            addHeading(document, "Bus Status");

            addMetric(document, "Available", data.availableBuses);
            addMetric(document, "On Trip", data.onTripBuses);
            addMetric(document, "Maintenance", data.maintenanceBuses);
            addMetric(document, "Inactive", data.inactiveBuses);

            addHeading(document, "Route Summary");

            addMetric(document, "Active Routes", data.activeRoutes);
            addMetric(document, "Inactive Routes", data.inactiveRoutes);
            addMetric(document, "Total Distance (km)", data.totalDistance);
            addMetric(document, "Average Fare", data.averageFare);

            addHeading(document, "Employee Summary");

            addMetric(document, "Total Employees", data.totalEmployees);
            addMetric(document, "Active Employees", data.activeEmployees);
            addMetric(document, "Inactive Employees", data.inactiveEmployees);
            addMetric(document, "Bus Drivers", data.busDrivers);
            addMetric(document, "Dispatchers", data.dispatchers);
            addMetric(document, "Ticketing Staff", data.ticketingStaff);
            addMetric(document, "Fleet Supervisors", data.fleetSupervisors);

            addHeading(document, "Trip Overview");

            addMetric(document, "Completed Trips", data.completedTrips);

            document.write(java.nio.file.Files.newOutputStream(file.toPath()));
        }
    }

    private static void addHeading(
            XWPFDocument document,
            String text) {

        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        run.setText(text);
        run.setBold(true);
        run.setFontSize(16);
    }

    private static void addParagraph(
            XWPFDocument document,
            String text) {

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.createRun().setText(text);
    }

    private static void addMetric(
            XWPFDocument document,
            String name,
            int value) {

        XWPFParagraph paragraph = document.createParagraph();
        paragraph.createRun().setText(name + ": " + value);
    }

    // ============================================================
    // PDF
    // ============================================================

    public static void exportPDF(
            File file,
            String period,
            ReportData data) throws IOException {

        try (PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream content = new PDPageContentStream(document, page)) {

                float y = 750;

                content.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                        20);

                content.beginText();
                content.newLineAtOffset(50, y);
                content.showText("BUSSIN - Reports & Analytics");
                content.endText();

                y -= 35;

                content.setFont(
                        new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                        11);

                y = writePDFLine(content, "Report Period: " + period, y);

                y -= 15;

                y = writePDFHeading(content, "Overview", y);
                y = writePDFMetric(content, "Total Bookings", data.totalBookings, y);
                y = writePDFMetric(content, "Completed Trips", data.completedTrips, y);
                y = writePDFMetric(content, "Active Buses", data.activeBuses, y);
                y = writePDFMetric(content, "Active Employees", data.activeEmployees, y);

                y -= 15;

                y = writePDFHeading(content, "Booking Overview", y);
                y = writePDFMetric(content, "Pending Bookings", data.pendingBookings, y);
                y = writePDFMetric(content, "Confirmed Bookings", data.confirmedBookings, y);
                y = writePDFMetric(content, "Completed Bookings", data.completedBookings, y);
                y = writePDFMetric(content, "Cancelled Bookings", data.cancelledBookings, y);

                y -= 15;

                y = writePDFHeading(content, "Bus Status", y);
                y = writePDFMetric(content, "Available", data.availableBuses, y);
                y = writePDFMetric(content, "On Trip", data.onTripBuses, y);
                y = writePDFMetric(content, "Maintenance", data.maintenanceBuses, y);
                y = writePDFMetric(content, "Inactive", data.inactiveBuses, y);

                y -= 15;

                y = writePDFHeading(content, "Route Summary", y);
                y = writePDFMetric(content, "Active Routes", data.activeRoutes, y);
                y = writePDFMetric(content, "Inactive Routes", data.inactiveRoutes, y);
                y = writePDFMetric(content, "Total Distance (km)", data.totalDistance, y);
                y = writePDFMetric(content, "Average Fare", data.averageFare, y);

                y -= 15;

                y = writePDFHeading(content, "Employee Summary", y);
                y = writePDFMetric(content, "Total Employees", data.totalEmployees, y);
                y = writePDFMetric(content, "Active Employees", data.activeEmployees, y);
                y = writePDFMetric(content, "Inactive Employees", data.inactiveEmployees, y);
                y = writePDFMetric(content, "Bus Drivers", data.busDrivers, y);
                y = writePDFMetric(content, "Dispatchers", data.dispatchers, y);
                y = writePDFMetric(content, "Ticketing Staff", data.ticketingStaff, y);
                y = writePDFMetric(content, "Fleet Supervisors", data.fleetSupervisors, y);
            }

            document.save(file);
        }
    }

    private static float writePDFHeading(
            PDPageContentStream content,
            String text,
            float y) throws IOException {

        content.setFont(
                new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD),
                13);

        content.beginText();
        content.newLineAtOffset(50, y);
        content.showText(text);
        content.endText();

        return y - 20;
    }

    private static float writePDFMetric(
            PDPageContentStream content,
            String name,
            int value,
            float y) throws IOException {

        content.setFont(
                new PDType1Font(Standard14Fonts.FontName.HELVETICA),
                11);

        content.beginText();
        content.newLineAtOffset(65, y);
        content.showText(name + ": " + value);
        content.endText();

        return y - 16;
    }

    private static float writePDFLine(
            PDPageContentStream content,
            String text,
            float y) throws IOException {

        content.beginText();
        content.newLineAtOffset(50, y);
        content.showText(text);
        content.endText();

        return y - 18;
    }

    // ============================================================
    // Shared Text
    // ============================================================

    public static String buildTextReport(
            String period,
            ReportData data) {

        StringBuilder report = new StringBuilder();

        report.append("==================================================\n");
        report.append("BUSSIN - REPORTS & ANALYTICS\n");
        report.append("==================================================\n");
        report.append("Report Period: ").append(period).append("\n\n");

        report.append("OVERVIEW\n");
        report.append("--------------------------------------------------\n");
        report.append("Total Bookings: ").append(data.totalBookings).append("\n");
        report.append("Completed Trips: ").append(data.completedTrips).append("\n");
        report.append("Active Buses: ").append(data.activeBuses).append("\n");
        report.append("Active Employees: ").append(data.activeEmployees).append("\n\n");

        report.append("BOOKING OVERVIEW\n");
        report.append("--------------------------------------------------\n");
        report.append("Pending: ").append(data.pendingBookings).append("\n");
        report.append("Confirmed: ").append(data.confirmedBookings).append("\n");
        report.append("Completed: ").append(data.completedBookings).append("\n");
        report.append("Cancelled: ").append(data.cancelledBookings).append("\n\n");

        report.append("BUS STATUS\n");
        report.append("--------------------------------------------------\n");
        report.append("Available: ").append(data.availableBuses).append("\n");
        report.append("On Trip: ").append(data.onTripBuses).append("\n");
        report.append("Maintenance: ").append(data.maintenanceBuses).append("\n");
        report.append("Inactive: ").append(data.inactiveBuses).append("\n\n");

        report.append("ROUTE SUMMARY\n");
        report.append("--------------------------------------------------\n");
        report.append("Active Routes: ").append(data.activeRoutes).append("\n");
        report.append("Inactive Routes: ").append(data.inactiveRoutes).append("\n");
        report.append("Total Distance: ").append(data.totalDistance).append(" km\n");
        report.append("Average Fare: ").append(data.averageFare).append("\n\n");

        report.append("EMPLOYEE SUMMARY\n");
        report.append("--------------------------------------------------\n");
        report.append("Total Employees: ").append(data.totalEmployees).append("\n");
        report.append("Active Employees: ").append(data.activeEmployees).append("\n");
        report.append("Inactive Employees: ").append(data.inactiveEmployees).append("\n");
        report.append("Bus Drivers: ").append(data.busDrivers).append("\n");
        report.append("Dispatchers: ").append(data.dispatchers).append("\n");
        report.append("Ticketing Staff: ").append(data.ticketingStaff).append("\n");
        report.append("Fleet Supervisors: ").append(data.fleetSupervisors).append("\n\n");

        report.append("TRIP OVERVIEW\n");
        report.append("--------------------------------------------------\n");
        report.append("Completed Trips: ").append(data.completedTrips).append("\n");

        report.append("\n==================================================\n");

        return report.toString();
    }

    private static String csv(String value) {

        if (value == null) {
            return "";
        }

        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}