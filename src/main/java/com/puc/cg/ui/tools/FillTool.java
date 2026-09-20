package com.puc.cg.ui.tools;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.ui.DrawingContext;
import com.puc.cg.ui.DrawingTool;

public class FillTool implements DrawingTool {

    @Override
    public void onMousePressed(Point2D point, DrawingContext context) {
        context.applyFill(point);
    }
}
