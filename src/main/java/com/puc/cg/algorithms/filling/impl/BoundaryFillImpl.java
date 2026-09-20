package com.puc.cg.algorithms.filling.impl;

import com.puc.cg.algorithms.filling.Connectivity;
import com.puc.cg.algorithms.filling.FillAlgorithm;
import com.puc.cg.commons.util.Framebuffer;

import java.util.ArrayDeque;
import java.util.Deque;

public class BoundaryFillImpl implements FillAlgorithm {

    @Override
    public void fill(Framebuffer framebuffer, int x, int y, int fillColor, int boundaryColor, Connectivity connectivity) {
        if (fillColor == boundaryColor) {
            return;
        }

        Deque<int[]> pending = new ArrayDeque<>();
        pending.push(new int[]{x, y});

        while (!pending.isEmpty()) {
            int[] point = pending.pop();
            int px = point[0];
            int py = point[1];

            if (framebuffer.isInBounds(px, py)) {
                continue;
            }

            int current = framebuffer.getPixel(px, py);
            if (current == boundaryColor || current == fillColor) {
                continue;
            }

            framebuffer.setPixel(px, py, fillColor);

            for (int[] offset : connectivity.getOffsets()) {
                pending.push(new int[]{px + offset[0], py + offset[1]});
            }
        }
    }
}
