package com.puc.cg.commons.util;

import lombok.Getter;

import java.awt.image.BufferedImage;

@Getter
public class Framebuffer {
    private final BufferedImage image;

    public Framebuffer(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        clear(Palette.WHITE);
    }

    public void setPixel(int x, int y, int rgb) {
        int px = x + image.getWidth() / 2;
        int py = y + image.getHeight() / 2;
        if (px < 0 || py < 0 || px >= image.getWidth() || py >= image.getHeight()) {
            return;
        }
        image.setRGB(px, py, rgb);
    }

    public int getPixel(int x, int y) {
        int px = x + image.getWidth() / 2;
        int py = y + image.getHeight() / 2;
        return image.getRGB(px, py) & 0xFFFFFF;
    }

    public boolean isInBounds(int x, int y) {
        int px = x + image.getWidth() / 2;
        int py = y + image.getHeight() / 2;
        return px < 0 || py < 0 || px >= image.getWidth() || py >= image.getHeight();
    }

    public void clear(int rgb) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                image.setRGB(x, y, rgb);
            }
        }
    }

    public int getWidth() {
        return image.getWidth();
    }

    public int getHeight() {
        return image.getHeight();
    }

}
