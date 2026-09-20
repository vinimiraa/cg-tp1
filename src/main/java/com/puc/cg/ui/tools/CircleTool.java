package com.puc.cg.ui.tools;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.impl.Circle;
import com.puc.cg.ui.DrawingContext;
import com.puc.cg.ui.DrawingTool;

/**
 * Ferramenta "Círculo": o 1º clique marca o centro e já liga a
 * pré-visualização (raio acompanha o mouse); o 2º clique fecha o círculo —
 * o raio é a distância entre os dois cliques. Botão direito cancela.
 */
public class CircleTool implements DrawingTool {

    private Point2D pendingCenter;

    @Override
    public void onMousePressed(Point2D point, DrawingContext context) {
        if (pendingCenter == null) {
            pendingCenter = point;
            context.setPreviewCircle(point, 0);
            return;
        }

        context.getScene().getShapes().add(new Circle(pendingCenter, radiusTo(point)));
        pendingCenter = null;
        context.clearPreview();
    }

    @Override
    public void onMouseMoved(Point2D point, DrawingContext context) {
        if (pendingCenter != null) {
            context.setPreviewCircle(pendingCenter, radiusTo(point));
        }
    }

    @Override
    public void onCancel(DrawingContext context) {
        pendingCenter = null;
        context.clearPreview();
    }

    @Override
    public void reset() {
        pendingCenter = null;
    }

    private int radiusTo(Point2D point) {
        return (int) Math.round(Math.hypot(
                point.x() - pendingCenter.x(),
                point.y() - pendingCenter.y()));
    }
}
