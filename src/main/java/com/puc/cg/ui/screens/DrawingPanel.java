package com.puc.cg.ui.screens;

import com.puc.cg.algorithms.clipping.ClipWindowAlgorithm;
import com.puc.cg.algorithms.clipping.impl.CohenSutherland;
import com.puc.cg.algorithms.clipping.impl.LiangBarsky;
import com.puc.cg.algorithms.raster.CircleRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.Framebuffer;
import com.puc.cg.algorithms.raster.LineRasterizerAlgorithm;
import com.puc.cg.algorithms.raster.impl.CircleRasterizerBresenhamImpl;
import com.puc.cg.algorithms.raster.impl.LineRasterizerBresenhamImpl;
import com.puc.cg.algorithms.raster.impl.LineRasterizerDDAImpl;
import com.puc.cg.commons.enums.ClipAlgorithm;
import com.puc.cg.commons.enums.LineAlgorithm;
import com.puc.cg.commons.model.Circle;
import com.puc.cg.commons.model.LineSegment;
import com.puc.cg.commons.model.Point2D;
import com.puc.cg.commons.model.Scene;
import com.puc.cg.commons.model.Window;
import com.puc.cg.commons.util.Palette;
import com.puc.cg.ui.DrawingContext;
import com.puc.cg.ui.DrawingTool;
import com.puc.cg.ui.Tool;
import com.puc.cg.ui.tools.CircleTool;
import com.puc.cg.ui.tools.ClipWindowTool;
import com.puc.cg.ui.tools.LineTool;
import lombok.Getter;

import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.EnumMap;
import java.util.Map;

/**
 * Área de desenho: mantém o framebuffer, o modelo (Scene) e a visualização
 * (pan/zoom), e implementa DrawingContext para que as DrawingTool não
 * dependam do Swing. Cada clique de mouse é despachado (já convertido de
 * coordenada de tela para coordenada do modelo) para a ferramenta atual;
 * PAN_ZOOM é tratado aqui mesmo, porque mexe na visualização, não na Scene.
 */
@Getter
public class DrawingPanel extends JPanel implements DrawingContext {
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 728;
    private static final double MIN_ZOOM = 0.2;
    private static final double MAX_ZOOM = 8.0;
    private static final double ZOOM_STEP = 1.1;

    private final Scene scene = new Scene();
    private final Framebuffer framebuffer = new Framebuffer(WIDTH, HEIGHT);

    private final LineRasterizerAlgorithm ddaRasterizer = new LineRasterizerDDAImpl();
    private final LineRasterizerAlgorithm bresenhamRasterizer = new LineRasterizerBresenhamImpl();
    private final CircleRasterizerAlgorithm circleRasterizer = new CircleRasterizerBresenhamImpl();

    private final ClipWindowAlgorithm cohenSutherlandClipper = new CohenSutherland();
    private final ClipWindowAlgorithm liangBarskyClipper = new LiangBarsky();

    private final Map<Tool, DrawingTool> tools = new EnumMap<>(Tool.class);

    private Tool currentTool = Tool.PAN_ZOOM;
    private LineAlgorithm lineAlgorithm = LineAlgorithm.DDA;
    private ClipAlgorithm clipAlgorithm = ClipAlgorithm.COHEN_SUTHERLAND;

    private LineSegment previewLine;
    private Circle previewCircle;
    private Window previewRect;

    private double zoom = 1.0;
    private double panX = 0;
    private double panY = 0;
    private Point dragAnchorScreen;

    public DrawingPanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        tools.put(Tool.ADD_LINE, new LineTool());
        tools.put(Tool.ADD_CIRCLE, new CircleTool());
        tools.put(Tool.DEFINE_CLIP_WINDOW, new ClipWindowTool());
        // Polígono e seleção por região retangular ainda são exigidos pelo
        // enunciado (Seção 2 do PDF) — voltam quando essas fases forem
        // implementadas; por ora só ficam de fora do toolbar.

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMousePressed(e);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragAnchorScreen = null;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouseDragged(e);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                DrawingTool tool = tools.get(currentTool);
                if (tool != null) {
                    tool.onMouseMoved(toModelPoint(e), DrawingPanel.this);
                }
            }
        });

        addMouseWheelListener(this::handleMouseWheel);
    }

    private void handleMousePressed(MouseEvent e) {
        // Botão do meio: arrasta a visualização em qualquer ferramenta, sem
        // mexer no clique pendente de uma reta/círculo/janela em andamento.
        if (e.getButton() == MouseEvent.BUTTON2) {
            dragAnchorScreen = e.getPoint();
            return;
        }

        if (currentTool == Tool.PAN_ZOOM) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                dragAnchorScreen = e.getPoint();
            }
            return;
        }

        DrawingTool tool = tools.get(currentTool);
        if (tool == null) {
            return;
        }

        if (e.getButton() == MouseEvent.BUTTON3) {
            tool.onCancel(this);
        } else {
            tool.onMousePressed(toModelPoint(e), this);
        }
    }

    private void handleMouseDragged(MouseEvent e) {
        if (dragAnchorScreen != null) {
            panX += e.getX() - dragAnchorScreen.x;
            panY += e.getY() - dragAnchorScreen.y;
            dragAnchorScreen = e.getPoint();
            repaint();
        }
    }

    private void handleMouseWheel(MouseWheelEvent e) {
        double factor = Math.pow(ZOOM_STEP, -e.getPreciseWheelRotation());
        double newZoom = Math.max(MIN_ZOOM, Math.min(MAX_ZOOM, zoom * factor));

        double modelX = (e.getX() - panX) / zoom;
        double modelY = (e.getY() - panY) / zoom;

        zoom = newZoom;
        panX = e.getX() - modelX * zoom;
        panY = e.getY() - modelY * zoom;

        repaint();
    }

    private Point2D toModelPoint(MouseEvent e) {
        return new Point2D((e.getX() - panX) / zoom, (e.getY() - panY) / zoom);
    }

    public void setCurrentTool(Tool tool) {
        DrawingTool previous = tools.get(currentTool);
        if (previous != null) {
            previous.reset();
        }
        dragAnchorScreen = null;
        this.currentTool = tool;
    }

    public void setLineAlgorithm(LineAlgorithm algorithm) {
        this.lineAlgorithm = algorithm;
    }

    public void setClipAlgorithm(ClipAlgorithm algorithm) {
        this.clipAlgorithm = algorithm;
    }

    public void clearScene() {
        scene.clear();
        DrawingTool tool = tools.get(currentTool);
        if (tool != null) {
            tool.reset();
        }
        clearPreview();
    }

    private LineRasterizerAlgorithm currentLineRasterizer() {
        return lineAlgorithm == LineAlgorithm.DDA ? ddaRasterizer : bresenhamRasterizer;
    }

    private ClipWindowAlgorithm currentClipAlgorithm() {
        return clipAlgorithm == ClipAlgorithm.COHEN_SUTHERLAND ? cohenSutherlandClipper : liangBarskyClipper;
    }

    @Override
    public void setPreviewLine(Point2D start, Point2D end) {
        previewLine = new LineSegment(start, end);
        previewCircle = null;
        previewRect = null;
        requestRedraw();
    }

    @Override
    public void setPreviewCircle(Point2D center, int radius) {
        previewCircle = new Circle(center, radius);
        previewLine = null;
        previewRect = null;
        requestRedraw();
    }

    @Override
    public void setPreviewRect(Point2D corner1, Point2D corner2) {
        double xMin = Math.min(corner1.getX(), corner2.getX());
        double xMax = Math.max(corner1.getX(), corner2.getX());
        double yMin = Math.min(corner1.getY(), corner2.getY());
        double yMax = Math.max(corner1.getY(), corner2.getY());

        previewRect = new Window(xMin, yMin, xMax, yMax);
        previewLine = null;
        previewCircle = null;
        requestRedraw();
    }

    @Override
    public void clearPreview() {
        previewLine = null;
        previewCircle = null;
        previewRect = null;
        requestRedraw();
    }

    @Override
    public void requestRedraw() {
        framebuffer.clear(Palette.WHITE);

        Window clipWindow = scene.getClipWindow();
        for (LineSegment line : scene.getLines()) {
            LineSegment toDraw = clipWindow == null ? line : currentClipAlgorithm().clip(line, clipWindow);
            if (toDraw != null) {
                currentLineRasterizer().draw(framebuffer, toDraw.start(), toDraw.end(), Palette.BLACK);
            }
        }

        for (Circle circle : scene.getCircles()) {
            circleRasterizer.draw(framebuffer, circle.center(), circle.radius(), Palette.BLACK);
        }

        if (clipWindow != null) {
            drawWindowOutline(clipWindow, Palette.BLUE);
        }

        drawActivePreview();

        repaint();
    }

    private void drawActivePreview() {
        if (previewLine != null) {
            currentLineRasterizer().draw(framebuffer, previewLine.start(), previewLine.end(), Palette.RED);
            drawPendingMarker(previewLine.start(), Palette.RED);
        }
        if (previewCircle != null) {
            circleRasterizer.draw(framebuffer, previewCircle.center(), previewCircle.radius(), Palette.RED);
            drawPendingMarker(previewCircle.center(), Palette.RED);
        }
        if (previewRect != null) {
            drawWindowOutline(previewRect, Palette.RED);
            drawPendingMarker(new Point2D(previewRect.getXMin(), previewRect.getYMin()), Palette.RED);
        }
    }

    private void drawWindowOutline(Window window, int rgb) {
        int xMin = (int) Math.round(window.getXMin());
        int xMax = (int) Math.round(window.getXMax());
        int yMin = (int) Math.round(window.getYMin());
        int yMax = (int) Math.round(window.getYMax());

        for (int x = xMin; x <= xMax; x++) {
            framebuffer.setPixel(x, yMin, rgb);
            framebuffer.setPixel(x, yMax, rgb);
        }
        for (int y = yMin; y <= yMax; y++) {
            framebuffer.setPixel(xMin, y, rgb);
            framebuffer.setPixel(xMax, y, rgb);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.translate(panX, panY);
        g2.scale(zoom, zoom);
        g2.drawImage(framebuffer.getImage(), 0, 0, null);
    }
}
