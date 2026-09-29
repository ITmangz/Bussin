package com.bussin.desktop.ui.theme;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.*;
import javax.swing.*;

public final class BussinTheme {

        private BussinTheme() {
        }

        // ============================================================
        // BRAND PALETTE
        // ============================================================

        /**
         * Primary brand/action color.
         */
        public static final Color RED = Color.decode("#FF0000");

        /**
         * Darker supporting red.
         */
        public static final Color CRIMSON = Color.decode("#990000");

        /**
         * Main dark color.
         */
        public static final Color CHARCOAL = Color.decode("#121212");

        /**
         * Main application background.
         */
        public static final Color COOL_GRAY = Color.decode("#F3F4F6");

        /**
         * Muted text / border color.
         */
        public static final Color MUTED_SILVER = Color.decode("#9CA3AF");

        // ============================================================
        // SURFACES
        // ============================================================

        public static final Color BACKGROUND = COOL_GRAY;

        public static final Color SURFACE = Color.WHITE;

        public static final Color SURFACE_ALT = Color.decode("#F9FAFB");

        public static final Color SURFACE_DARK = CHARCOAL;

        public static final Color SURFACE_DARK_ALT = Color.decode("#1C1C1C");

        // ============================================================
        // BRAND STATES
        // ============================================================

        public static final Color PRIMARY = RED;

        public static final Color PRIMARY_HOVER = Color.decode("#CC0000");

        public static final Color PRIMARY_PRESSED = CRIMSON;

        public static final Color PRIMARY_LIGHT = Color.decode("#FFF1F1");

        // ============================================================
        // TEXT
        // ============================================================

        public static final Color TEXT_PRIMARY = CHARCOAL;

        public static final Color TEXT_SECONDARY = Color.decode("#4B5563");

        public static final Color TEXT_MUTED = MUTED_SILVER;

        public static final Color TEXT_ON_DARK = Color.WHITE;

        public static final Color TEXT_ON_PRIMARY = Color.WHITE;

        // ============================================================
        // BORDERS
        // ============================================================

        public static final Color BORDER = Color.decode("#E5E7EB");

        public static final Color BORDER_STRONG = Color.decode("#D1D5DB");

        public static final Color BORDER_DARK = Color.decode("#303030");

        // ============================================================
        // STATUS COLORS
        // ============================================================

        public static final Color SUCCESS = Color.decode("#15803D");

        public static final Color SUCCESS_LIGHT = Color.decode("#F0FDF4");

        public static final Color WARNING = Color.decode("#B45309");

        public static final Color WARNING_LIGHT = Color.decode("#FFFBEB");

        public static final Color DANGER = RED;

        public static final Color DANGER_LIGHT = Color.decode("#FFF1F1");

        public static final Color INFO = Color.decode("#374151");

        public static final Color INFO_LIGHT = Color.decode("#F3F4F6");

        // ============================================================
        // DIMENSIONS
        // ============================================================

        public static final int SIDEBAR_WIDTH = 238;

        public static final int TOP_BAR_HEIGHT = 68;

        public static final int PAGE_PADDING = 28;

        public static final int SMALL_RADIUS = 8;

        public static final int MEDIUM_RADIUS = 12;

        public static final int LARGE_RADIUS = 16;

        // ============================================================
        // SPACING
        // ============================================================

        public static final int SPACE_XS = 4;

        public static final int SPACE_SM = 8;

        public static final int SPACE_MD = 12;

        public static final int SPACE_LG = 16;

        public static final int SPACE_XL = 24;

        public static final int SPACE_XXL = 32;

        // ============================================================
        // TYPOGRAPHY
        // ============================================================

        private static final String FONT = "Segoe UI";

        public static final Font BRAND = new Font(
                        FONT,
                        Font.BOLD,
                        22);

        public static final Font PAGE_TITLE = new Font(
                        FONT,
                        Font.BOLD,
                        27);

        public static final Font PAGE_SUBTITLE = new Font(
                        FONT,
                        Font.PLAIN,
                        14);

        public static final Font SECTION_TITLE = new Font(
                        FONT,
                        Font.BOLD,
                        18);

        public static final Font CARD_TITLE = new Font(
                        FONT,
                        Font.BOLD,
                        15);

        public static final Font BODY = new Font(
                        FONT,
                        Font.PLAIN,
                        14);

        public static final Font BODY_MEDIUM = new Font(
                        FONT,
                        Font.PLAIN,
                        15);

        public static final Font SMALL = new Font(
                        FONT,
                        Font.PLAIN,
                        12);

        public static final Font SMALL_BOLD = new Font(
                        FONT,
                        Font.BOLD,
                        12);

        public static final Font BUTTON = new Font(
                        FONT,
                        Font.BOLD,
                        13);

        public static final Font STAT_VALUE = new Font(
                        FONT,
                        Font.BOLD,
                        25);

        // ============================================================
        // INITIALIZATION
        // ============================================================

        public static void initialize() {

                FlatLightLaf.setup();

                UIManager.put(
                                "Component.focusWidth",
                                0);

                UIManager.put(
                                "Component.arc",
                                MEDIUM_RADIUS);

                UIManager.put(
                                "Button.arc",
                                SMALL_RADIUS);

                UIManager.put(
                                "TextField.arc",
                                SMALL_RADIUS);

                UIManager.put(
                                "PasswordField.arc",
                                SMALL_RADIUS);

                UIManager.put(
                                "ComboBox.arc",
                                SMALL_RADIUS);

                UIManager.put(
                                "ScrollBar.width",
                                10);

                UIManager.put(
                                "Button.font",
                                BUTTON);

                UIManager.put(
                                "Label.font",
                                BODY);

                UIManager.put(
                                "TextField.font",
                                BODY);

                UIManager.put(
                                "PasswordField.font",
                                BODY);

                UIManager.put(
                                "TextArea.font",
                                BODY);

                UIManager.put(
                                "ComboBox.font",
                                BODY);

                UIManager.put(
                                "ScrollBar.thumbArc",
                                10);
        }
}