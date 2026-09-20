package com.puc.cg.algorithms.filling;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Connectivity {
    FOUR(new int[][]{
            {1, 0}, {-1, 0}, {0, 1}, {0, -1}
    }),
    EIGHT(new int[][]{
            {1, 0}, {-1, 0}, {0, 1}, {0, -1},
            {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
    });

    private final int[][] offsets;
}
