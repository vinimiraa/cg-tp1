package com.puc.cg.ui.tools;

import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Window;
import com.puc.cg.ui.DrawingContext;
import com.puc.cg.ui.DrawingTool;

/**
 * Ferramenta "Definir janela de recorte": o 1º clique marca um canto e já
 * liga a pré-visualização (retângulo acompanha o mouse); o 2º clique marca
 * o canto oposto. Botão direito cancela.
 */
public class ClipWindowTool implements DrawingTool {

    private Point2D pendingCorner;

    @Override
    public void onMousePressed(Point2D point, DrawingContext context) {
        if (pendingCorner == null) {
            pendingCorner = point;
            context.setPreviewRect(point, point);
            return;
        }

        double xMin = Math.min(pendingCorner.x(), point.x());
        double xMax = Math.max(pendingCorner.x(), point.x());
        double yMin = Math.min(pendingCorner.y(), point.y());
        double yMax = Math.max(pendingCorner.y(), point.y());

        context.getScene().setClipWindow(new Window(xMin, yMin, xMax, yMax));
        pendingCorner = null;
        context.clearPreview();
    }

    @Override
    public void onMouseMoved(Point2D point, DrawingContext context) {
        if (pendingCorner != null) {
            context.setPreviewRect(pendingCorner, point);
        }
    }

    @Override
    public void onCancel(DrawingContext context) {
        pendingCorner = null;
        context.clearPreview();
    }

    @Override
    public void reset() {
        pendingCorner = null;
    }
}
