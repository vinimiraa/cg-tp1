package com.puc.cg.algorithms.filling.impl;

import com.puc.cg.algorithms.filling.Connectivity;
import com.puc.cg.algorithms.filling.FillAlgorithm;
import com.puc.cg.commons.util.Framebuffer;

import java.util.ArrayDeque;
import java.util.Deque;

public class FloodFillImpl implements FillAlgorithm {

    @Override
    public void fill(Framebuffer framebuffer, int x, int y, int fillColor, int oldColor, Connectivity connectivity) {
        if (fillColor == oldColor) {
            return;
        }

        Deque<int[]> pending = new ArrayDeque<>();
        pending.push(new int[]{x, y});

        while (!pending.isEmpty()) {
            int[] point = pending.pop();
            int px = point[0];
            int py = point[1];

            if (framebuffer.isInBounds(px, py) || framebuffer.getPixel(px, py) != oldColor) {
                continue;
            }

            framebuffer.setPixel(px, py, fillColor);

            for (int[] offset : connectivity.getOffsets()) {
                pending.push(new int[]{px + offset[0], py + offset[1]});
            }
        }
    }
}
