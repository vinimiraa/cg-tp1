package com.puc.cg.algorithms.raster;

import lombok.Getter;

import java.awt.image.BufferedImage;

@Getter
public class Framebuffer {
    private final BufferedImage image;

    public Framebuffer(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        clear(0xFFFFFF);
    }

    public void setPixel(int x, int y, int rgb) {
        if (x < 0 || y < 0 || x >= image.getWidth() || y >= image.getHeight()) {
            return;
        }
        image.setRGB(x, y, rgb);
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
