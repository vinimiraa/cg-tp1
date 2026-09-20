package com.puc.cg.ui;

import com.puc.cg.commons.models.Point2D;

public interface DrawingTool {
    void onMousePressed(Point2D point, DrawingContext context);

    default void onMouseMoved(Point2D point, DrawingContext context) {
    }

    default void onCancel(DrawingContext context) {
        reset();
    }

    default void reset() {
    }
}
