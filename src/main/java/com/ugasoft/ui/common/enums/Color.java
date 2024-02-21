package com.ugasoft.ui.common.enums;

public enum Color {

    C_255_0_0(255, 0, 0),         // Red
    C_255_165_0(255, 165, 0),     // Orange
    C_255_255_0(255, 255, 0),     // Yellow
    C_0_128_0(0, 128, 0),         // Green
    C_0_0_255(0, 0, 255),         // Blue
    C_75_0_130(75, 0, 130),       // Indigo
    C_238_130_238(238, 130, 238), // Violet
    C_128_0_128(128, 0, 128),     // Purple
    C_0_255_255(0, 255, 255),     // Aqua
    C_255_255_255(255, 255, 255), // White
    C_0_0_0(0, 0, 0),             // Black
    C_128_128_128(128, 128, 128), // Gray
    C_165_42_42(165, 42, 42),     // Brown
    C_255_192_203(255, 192, 203), // Pink
    C_255_255_240(255, 255, 240), // Ivory
    C_245_222_179(245, 222, 179), // Wheat
    C_173_216_230(173, 216, 230), // LightBlue
    C_0_185_255(0, 185, 255),
    C_54_83_116(54, 83, 116),
    C_215_215_215(215, 215, 215),
    C_199_199_199(199, 199, 199),
    C_0_99_228(0, 99, 228);


    private int red;
    private int green;
    private int blue;

    Color(int red, int green, int blue) {
        if (red < 0 || red > 255 || green < 0 || green > 255 || blue < 0 || blue > 255) {
            throw new IllegalArgumentException("Color values must be between 0 and 255");
        }

        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    public int getRed() {
        return this.red;
    }

    public int getGreen() {
        return this.green;
    }

    public int getBlue() {
        return this.blue;
    }

    public String getRgba() {
        return String.format("rgba(%s, %s, %s, 1)", red, green, blue);
    }

    public String getRgb() {
        return String.format("rgb(%s, %s, %s)", red, green, blue);
    }

    public String getHex() {
        return String.format("#%02x%02x%02x", this.red, this.green, this.blue);
    }

    public String getNumeric() {
        return String.format("%s, %s, %s", red, green, blue);
    }
}
