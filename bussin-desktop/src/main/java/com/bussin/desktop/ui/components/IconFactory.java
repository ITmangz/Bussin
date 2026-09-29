package com.bussin.desktop.ui.components;

import com.bussin.desktop.ui.theme.BussinTheme;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.swing.*;

public final class IconFactory {

    private IconFactory() {
    }

    // ================================================================
    // CREATE ICON
    // ================================================================

    public static Icon create(
            String name,
            int size,
            Color color) {

        String svg = getSvg(
                name,
                color);

        try {

            FlatSVGIcon icon = new FlatSVGIcon(
                    new ByteArrayInputStream(
                            svg.getBytes(
                                    StandardCharsets.UTF_8)));

            return icon.derive(
                    size,
                    size);

        } catch (IOException exception) {

            throw new IllegalStateException(
                    "Unable to create BUSSIN icon: " + name,
                    exception);
        }
    }

    public static Icon create(
            String name,
            int size) {

        return create(
                name,
                size,
                BussinTheme.TEXT_PRIMARY);
    }

    // ================================================================
    // SVG GENERATOR
    // ================================================================

    private static String getSvg(
            String name,
            Color color) {

        String hex = String.format(
                "#%02X%02X%02X",
                color.getRed(),
                color.getGreen(),
                color.getBlue());

        String path;

        switch (name) {

            // --------------------------------------------------------
            // DASHBOARD
            // --------------------------------------------------------

            case "dashboard" -> path = """
                    <rect x="3" y="3"
                          width="7" height="7"
                          rx="1"/>
                    <rect x="14" y="3"
                          width="7" height="7"
                          rx="1"/>
                    <rect x="3" y="14"
                          width="7" height="7"
                          rx="1"/>
                    <rect x="14" y="14"
                          width="7" height="7"
                          rx="1"/>
                    """;

            // --------------------------------------------------------
            // QUEUE
            // --------------------------------------------------------

            case "queue" -> path = """
                    <path d="M4 6h16"/>
                    <path d="M4 12h16"/>
                    <path d="M4 18h16"/>
                    <circle cx="7" cy="6" r="1"/>
                    <circle cx="7" cy="12" r="1"/>
                    <circle cx="7" cy="18" r="1"/>
                    """;

            // --------------------------------------------------------
            // BOOKING
            // --------------------------------------------------------

            case "booking" -> path = """
                    <rect x="4" y="3"
                          width="16"
                          height="18"
                          rx="2"/>
                    <path d="M8 3v4"/>
                    <path d="M16 3v4"/>
                    <path d="M4 9h16"/>
                    <path d="M8 13h2"/>
                    <path d="M14 13h2"/>
                    <path d="M8 17h2"/>
                    <path d="M14 17h2"/>
                    """;

            // --------------------------------------------------------
            // BUS
            // --------------------------------------------------------

            case "bus" -> path = """
                    <path d="M5 17V7
                             c0-2 2-3 7-3
                             s7 1 7 3v10"/>
                    <path d="M3 17h18"/>
                    <path d="M5 17v3"/>
                    <path d="M19 17v3"/>
                    <path d="M7 20h2"/>
                    <path d="M15 20h2"/>
                    <path d="M5 10h14"/>
                    <circle cx="8" cy="15" r="1"/>
                    <circle cx="16" cy="15" r="1"/>
                    """;

            // --------------------------------------------------------
            // ROUTE
            // --------------------------------------------------------

            case "route" -> path = """
                    <circle cx="6" cy="18" r="3"/>
                    <circle cx="18" cy="6" r="3"/>
                    <path d="M8.5 16.5L15.5 9.5"/>
                    """;

            // --------------------------------------------------------
            // TRIP
            // --------------------------------------------------------

            case "trip" -> path = """
                    <circle cx="12" cy="12" r="9"/>
                    <path d="M12 7v5l3 2"/>
                    """;

            // --------------------------------------------------------
            // EMPLOYEE
            // --------------------------------------------------------

            case "employee" -> path = """
                    <circle cx="12" cy="8" r="4"/>
                    <path d="M4 21
                             c0-4 3-6 8-6
                             s8 2 8 6"/>
                    """;

            // --------------------------------------------------------
            // REPORT
            // --------------------------------------------------------

            case "report" -> path = """
                    <path d="M4 19V5"/>
                    <path d="M4 19h16"/>
                    <path d="M7 16v-5"/>
                    <path d="M12 16V8"/>
                    <path d="M17 16V5"/>
                    """;

            // --------------------------------------------------------
            // NOTIFICATION
            // --------------------------------------------------------

            case "notification" -> path = """
                    <path d="M18 8
                             a6 6 0 0 0-12 0
                             c0 7-3 7-3 9
                             h18
                             c0-2-3-2-3-9"/>
                    <path d="M10 21h4"/>
                    """;

            // --------------------------------------------------------
            // PLUS
            // --------------------------------------------------------

            case "plus" -> path = """
                    <path d="M12 5v14"/>
                    <path d="M5 12h14"/>
                    """;

            // --------------------------------------------------------
            // ARROW RIGHT
            // --------------------------------------------------------

            case "arrow-right" -> path = """
                    <path d="M5 12h14"/>
                    <path d="m13 6 6 6-6 6"/>
                    """;

            // --------------------------------------------------------
            // USERS
            // --------------------------------------------------------

            case "users" -> path = """
                    <circle cx="9" cy="8" r="3"/>
                    <path d="M3 20
                             c0-4 2-6 6-6
                             s6 2 6 6"/>
                    <path d="M16 11
                             c3 0 5 2 5 5"/>
                    <path d="M16 5
                             c2 0 3 1 3 3"/>
                    """;

            // --------------------------------------------------------
            // SEAT
            // --------------------------------------------------------

            case "seat" -> path = """
                    <path d="M7 4v8"/>
                    <path d="M7 12h8
                             c2 0 3 1 3 3v5"/>
                    <path d="M7 12v8"/>
                    <path d="M4 20h16"/>
                    <path d="M7 4h5
                             c2 0 3 1 3 3v2"/>
                    """;

            // --------------------------------------------------------
            // DEFAULT
            // --------------------------------------------------------

            default -> path = """
                    <circle cx="12" cy="12" r="9"/>
                    <path d="M12 8v8"/>
                    <path d="M8 12h8"/>
                    """;
        }

        return """
                <svg xmlns="http://www.w3.org/2000/svg"
                     width="24"
                     height="24"
                     viewBox="0 0 24 24"
                     fill="none"
                     stroke="%s"
                     stroke-width="2"
                     stroke-linecap="round"
                     stroke-linejoin="round">

                    %s

                </svg>
                """.formatted(
                hex,
                path);
    }
}