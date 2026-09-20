package com.puc.cg.commons.models;

import com.puc.cg.algorithms.filling.Connectivity;
import com.puc.cg.algorithms.filling.FillAlgorithm;

public record FillAction(
        Point2D seed,
        FillAlgorithm method,
        int fillColor,
        int refColor,
        Connectivity connectivity
) {
}
