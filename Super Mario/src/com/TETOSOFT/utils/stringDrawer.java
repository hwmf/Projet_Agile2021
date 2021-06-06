package com.TETOSOFT.utils;

import java.awt.*;

public  class stringDrawer {
    public static void drawCenteredString(Graphics2D g, String text, Rectangle rect, Font font,Color color) {
        // Get the FontMetrics
        FontMetrics metrics = g.getFontMetrics(font);
        // Determine the X coordinate for the text
        int x = rect.x + (rect.width - metrics.stringWidth(text)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = rect.y + ((rect.height - metrics.getHeight()) / 2) + metrics.getAscent();
        // Set the font
        g.setFont(font);
        // Set the rectangle Color
        g.setColor(color);
        // Draw the Rect
        g.draw(rect);
        // Set the text Color
        g.setColor(Color.RED);
        // Draw the String
        g.drawString(text, x, y);
    }
}
