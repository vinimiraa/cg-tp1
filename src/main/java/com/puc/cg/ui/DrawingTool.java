package com.puc.cg.ui;

import com.puc.cg.commons.model.Point2D;

/**
 * Uma ferramenta do toolbar (Reta, Círculo, janela de recorte...). Cada
 * ferramenta guarda seu próprio estado de interação pendente — o
 * DrawingPanel não sabe nada sobre esse estado, só despacha os eventos de
 * mouse para a ferramenta atual.
 */
public interface DrawingTool {
    void onMousePressed(Point2D point, DrawingContext context);

    /** O mouse se moveu sem clicar; usado para pré-visualização (ex.: a reta sendo desenhada). */
    default void onMouseMoved(Point2D point, DrawingContext context) {
    }

    /** Botão direito do mouse: cancela o clique pendente desta ferramenta. */
    default void onCancel(DrawingContext context) {
        reset();
    }

    /** Descarta qualquer clique pendente. Chamado ao trocar de ferramenta, cancelar ou limpar a cena. */
    default void reset() {
    }
}
