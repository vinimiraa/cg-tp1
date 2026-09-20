package com.puc.cg.ui.tools;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.impl.LineSegment;
import com.puc.cg.ui.DrawingContext;
import com.puc.cg.ui.DrawingTool;

/**
 * Ferramenta "Reta": o 1º clique guarda o ponto inicial e já liga a
 * pré-visualização (que acompanha o mouse até o 2º clique); o 2º clique
 * fecha o segmento e adiciona na Scene; o botão direito cancela.
 */
public class LineTool implements DrawingTool {

    private Point2D pendingStart;

    @Override
    public void onMousePressed(Point2D point, DrawingContext context) {
        if (pendingStart == null) {
            pendingStart = point;
            context.setPreviewLine(point, point);
            return;
        }

        context.getScene().getShapes().add(new LineSegment(pendingStart, point));
        pendingStart = null;
        context.clearPreview();
    }

    @Override
    public void onMouseMoved(Point2D point, DrawingContext context) {
        if (pendingStart != null) {
            context.setPreviewLine(pendingStart, point);
        }
    }

    @Override
    public void onCancel(DrawingContext context) {
        pendingStart = null;
        context.clearPreview();
    }

    @Override
    public void reset() {
        pendingStart = null;
    }
}
