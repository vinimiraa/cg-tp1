package com.puc.cg.ui;

import com.puc.cg.algorithms.raster.Framebuffer;
import com.puc.cg.commons.model.Point2D;
import com.puc.cg.commons.model.Scene;

/**
 * O que uma DrawingTool enxerga do mundo à sua volta: o modelo (Scene), o
 * framebuffer (para feedback visual imediato) e como pedir um redesenho.
 * Quem implementa isso é o DrawingPanel; as ferramentas dependem só desta
 * interface, nunca do Swing diretamente.
 */
public interface DrawingContext {
    Scene getScene();

    Framebuffer getFramebuffer();

    /** Limpa o framebuffer e re-rasteriza tudo a partir da Scene (mais a pré-visualização, se houver). */
    void requestRedraw();

    /** Só repinta o framebuffer atual na tela (sem reconstruí-lo a partir da Scene). */
    void repaint();

    /**
     * Marca visualmente um clique pendente (uma cruz). É um desenho "fora do
     * modelo": some assim que requestRedraw() rodar de novo.
     */
    default void drawPendingMarker(Point2D point, int rgb) {
        Framebuffer framebuffer = getFramebuffer();
        int cx = (int) Math.round(point.getX());
        int cy = (int) Math.round(point.getY());
        for (int offset = -3; offset <= 3; offset++) {
            framebuffer.setPixel(cx + offset, cy, rgb);
            framebuffer.setPixel(cx, cy + offset, rgb);
        }
        repaint();
    }

    /** Pré-visualização de uma reta sendo desenhada (1º clique até a posição atual do mouse). */
    void setPreviewLine(Point2D start, Point2D end);

    /** Pré-visualização de um círculo sendo desenhado (centro fixo, raio até a posição atual do mouse). */
    void setPreviewCircle(Point2D center, int radius);

    /** Pré-visualização de um retângulo sendo desenhado (1º canto até a posição atual do mouse). */
    void setPreviewRect(Point2D corner1, Point2D corner2);

    /** Remove qualquer pré-visualização ativa e pede um redesenho. */
    void clearPreview();
}
