package com.puc.cg.ui.screens;

import com.puc.cg.algorithms.clipping.ClipAlgorithm;
import com.puc.cg.algorithms.filling.Connectivity;
import com.puc.cg.algorithms.filling.FillMethod;
import com.puc.cg.algorithms.raster.LineAlgorithm;
import com.puc.cg.algorithms.transform.Matrix3;
import com.puc.cg.commons.models.FillAction;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.util.Framebuffer;
import com.puc.cg.commons.util.Palette;
import com.puc.cg.commons.util.Scene;
import com.puc.cg.ui.DrawingContext;
import com.puc.cg.ui.DrawingTool;
import com.puc.cg.ui.PreviewState;
import com.puc.cg.ui.SceneRenderer;
import com.puc.cg.ui.Selection;
import com.puc.cg.ui.Tool;
import com.puc.cg.ui.Viewport;
import com.puc.cg.ui.tools.CircleTool;
import com.puc.cg.ui.tools.ClipWindowTool;
import com.puc.cg.ui.tools.FillTool;
import com.puc.cg.ui.tools.LineTool;
import com.puc.cg.ui.tools.PolygonTool;
import com.puc.cg.ui.tools.SelectionTool;
import lombok.Getter;
import lombok.Setter;

import javax.swing.AbstractAction;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DrawingPanel extends JPanel implements DrawingContext {
    private static final int INITIAL_WIDTH = 1400;
    private static final int INITIAL_HEIGHT = 800;
    private static final int MIN_WIDTH = 300;
    private static final int MIN_HEIGHT = 200;
    private static final int GRID_SPACING = 50;
    private static final int ORIGIN_MARKER_RADIUS = 4;
    private static final double PIXEL_GRID_MIN_ZOOM = 4.0;
    private static final int MIN_LABEL_SPACING_PX = 28;
    private static final int[] LABEL_STEPS = {1, 2, 5, 10, 20, 50, 100, 200, 500, 1000};

    @Getter
    private final Scene scene = new Scene();
    private final Selection selection = new Selection();
    private final PreviewState preview = new PreviewState();
    private final Viewport viewport = new Viewport();
    private final SceneRenderer renderer = new SceneRenderer();
    private final Map<Tool, DrawingTool> tools = new EnumMap<>(Tool.class);
    private Framebuffer framebuffer = new Framebuffer(INITIAL_WIDTH, INITIAL_HEIGHT);
    private Tool currentTool = Tool.PAN_ZOOM;

    @Setter
    private LineAlgorithm lineAlgorithm = LineAlgorithm.DDA;
    @Setter
    private ClipAlgorithm clipAlgorithm = ClipAlgorithm.COHEN_SUTHERLAND;
    @Setter
    private FillMethod fillMethod = FillMethod.FLOOD_FILL;
    @Setter
    private int fillColor = Palette.RED;
    @Setter
    private Connectivity connectivity = Connectivity.FOUR;

    private Point dragAnchorScreen;
    private JLabel statusLabel;

    public DrawingPanel() {
        setPreferredSize(new Dimension(INITIAL_WIDTH, INITIAL_HEIGHT));
        setMinimumSize(new Dimension(MIN_WIDTH, MIN_HEIGHT));
        updateCursor();

        tools.put(Tool.ADD_LINE, new LineTool());
        tools.put(Tool.ADD_CIRCLE, new CircleTool());
        tools.put(Tool.DEFINE_CLIP_WINDOW, new ClipWindowTool());
        tools.put(Tool.ADD_POLYGON, new PolygonTool());
        tools.put(Tool.SELECT_RECT, new SelectionTool());
        tools.put(Tool.FILL, new FillTool());

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeFramebuffer();
            }
        });

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
                Point2D modelPoint = toModelPoint(e);
                updateStatus(modelPoint.x(), modelPoint.y());
                DrawingTool tool = tools.get(currentTool);
                if (tool != null) {
                    tool.onMouseMoved(modelPoint, DrawingPanel.this);
                }
            }
        });

        addMouseWheelListener(this::handleMouseWheel);

        KeyStroke undoKey = KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK);
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(undoKey, "undo");
        getActionMap().put("undo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }
        });
    }

    public void undo() {
        if (scene.undo()) {
            selection.clear();
            DrawingTool tool = tools.get(currentTool);
            if (tool != null) {
                tool.reset();
            }
            clearPreview();
        }
    }

    private void resizeFramebuffer() {
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());
        if (framebuffer.getWidth() == w && framebuffer.getHeight() == h) {
            return;
        }
        framebuffer = new Framebuffer(w, h);
        requestRedraw();
    }

    private void handleMousePressed(MouseEvent e) {
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
            viewport.panBy(e.getX() - dragAnchorScreen.x, e.getY() - dragAnchorScreen.y);
            dragAnchorScreen = e.getPoint();
            repaint();
        }
        Point2D modelPoint = toModelPoint(e);
        updateStatus(modelPoint.x(), modelPoint.y());
    }

    private void handleMouseWheel(MouseWheelEvent e) {
        viewport.zoomAt(e.getX(), e.getY(), e.getPreciseWheelRotation(), framebuffer.getWidth(), framebuffer.getHeight());
        Point2D modelPoint = toModelPoint(e);
        updateStatus(modelPoint.x(), modelPoint.y());
        repaint();
    }

    private Point2D toModelPoint(MouseEvent e) {
        return viewport.toModelPoint(e.getX(), e.getY(), framebuffer.getWidth(), framebuffer.getHeight());
    }

    public void setCurrentTool(Tool tool) {
        DrawingTool previous = tools.get(currentTool);
        if (previous != null) {
            previous.reset();
        }
        dragAnchorScreen = null;
        this.currentTool = tool;
        updateCursor();
    }

    private void updateCursor() {
        int cursorType = currentTool == Tool.PAN_ZOOM ? Cursor.HAND_CURSOR : Cursor.CROSSHAIR_CURSOR;
        setCursor(Cursor.getPredefinedCursor(cursorType));
    }

    public void setStatusLabel(JLabel statusLabel) {
        this.statusLabel = statusLabel;
        updateStatus(0, 0);
    }

    private void updateStatus(double modelX, double modelY) {
        if (statusLabel != null) {
            statusLabel.setText(String.format("  (%.0f, %.0f)    Zoom: %.0f%%", modelX, modelY, viewport.getZoom() * 100));
        }
    }

    public void clearScene() {
        scene.pushHistory();
        scene.clear();
        selection.clear();
        DrawingTool tool = tools.get(currentTool);
        if (tool != null) {
            tool.reset();
        }
        clearPreview();
    }

    public Point2D getSelectionCenter() {
        return selection.center();
    }

    public void applyTransform(Matrix3 matrix) {
        selection.applyTransform(matrix, scene);
        requestRedraw();
    }

    @Override
    public void setPreviewLine(Point2D start, Point2D end) {
        preview.setLine(start, end);
        requestRedraw();
    }

    @Override
    public void setPreviewCircle(Point2D center, int radius) {
        preview.setCircle(center, radius);
        requestRedraw();
    }

    @Override
    public void setPreviewRect(Point2D corner1, Point2D corner2) {
        preview.setRect(corner1, corner2);
        requestRedraw();
    }

    @Override
    public void setPreviewPolygon(List<Point2D> vertices, Point2D current) {
        preview.setPolygon(vertices, current);
        requestRedraw();
    }

    @Override
    public void clearPreview() {
        preview.clear();
        requestRedraw();
    }

    @Override
    public void setSelection(List<Shape> shapes) {
        selection.set(shapes);
        requestRedraw();
    }

    @Override
    public void applyFill(Point2D seed) {
        int seedX = (int) Math.floor(seed.x());
        int seedY = (int) Math.floor(seed.y());
        int refColor = fillMethod == FillMethod.BOUNDARY_FILL ? Palette.BLACK : framebuffer.getPixel(seedX, seedY);
        scene.pushHistory();
        scene.getFills().add(new FillAction(seed, fillMethod, fillColor, refColor, connectivity));
        requestRedraw();
    }

    @Override
    public void requestRedraw() {
        renderer.render(framebuffer, scene, selection, preview, lineAlgorithm, clipAlgorithm);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        Graphics2D scaled = (Graphics2D) g2.create();
        scaled.translate(viewport.getPanX(), viewport.getPanY());
        scaled.scale(viewport.getZoom(), viewport.getZoom());
        scaled.drawImage(framebuffer.getImage(), 0, 0, null);
        scaled.dispose();

        drawGridOverlay(g2);
    }

    private void drawGridOverlay(Graphics2D g2) {
        int width = framebuffer.getWidth();
        int height = framebuffer.getHeight();
        g2.setStroke(new BasicStroke(1f));

        if (viewport.getZoom() >= PIXEL_GRID_MIN_ZOOM) {
            drawGridLines(g2, width, height, 1, Palette.PIXEL_GRID);
        }
        drawGridLines(g2, width, height, GRID_SPACING, Palette.GRID_LINE);
        drawAxes(g2, width, height);
        drawOriginMarker(g2, width, height);
        drawAxisLabels(g2, width, height);
    }

    private void drawGridLines(Graphics2D g2, int width, int height, int spacing, int color) {
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        g2.setColor(new Color(color));

        for (int modelX = -halfWidth; modelX <= halfWidth; modelX += spacing) {
            if (modelX == 0) {
                continue;
            }
            int screenX = (int) Math.round(viewport.toScreenX(modelX, width));
            g2.drawLine(screenX, 0, screenX, getHeight());
        }
        for (int modelY = -halfHeight; modelY <= halfHeight; modelY += spacing) {
            if (modelY == 0) {
                continue;
            }
            int screenY = (int) Math.round(viewport.toScreenY(modelY, height));
            g2.drawLine(0, screenY, getWidth(), screenY);
        }
    }

    private void drawAxes(Graphics2D g2, int width, int height) {
        int screenX = (int) Math.round(viewport.toScreenX(0, width));
        int screenY = (int) Math.round(viewport.toScreenY(0, height));
        g2.setColor(new Color(Palette.GRID_AXIS));
        g2.drawLine(screenX, 0, screenX, getHeight());
        g2.drawLine(0, screenY, getWidth(), screenY);
    }

    private void drawOriginMarker(Graphics2D g2, int width, int height) {
        int screenX = (int) Math.round(viewport.toScreenX(0, width));
        int screenY = (int) Math.round(viewport.toScreenY(0, height));
        g2.setColor(new Color(Palette.ORIGIN_HIGHLIGHT));
        g2.fillOval(screenX - ORIGIN_MARKER_RADIUS, screenY - ORIGIN_MARKER_RADIUS,
                ORIGIN_MARKER_RADIUS * 2, ORIGIN_MARKER_RADIUS * 2);
    }

    private void drawAxisLabels(Graphics2D g2, int width, int height) {
        int step = labelStep();
        if (step == 0) {
            return;
        }

        int halfWidth = width / 2;
        int halfHeight = height / 2;
        g2.setFont(g2.getFont().deriveFont(11f));
        FontMetrics metrics = g2.getFontMetrics();

        for (int modelX = -halfWidth; modelX <= halfWidth; modelX += step) {
            int screenX = (int) Math.round(viewport.toScreenX(modelX, width));
            if (screenX >= 0 && screenX <= getWidth()) {
                drawLabel(g2, metrics, String.valueOf(modelX), screenX + 2, 2);
            }
        }
        for (int modelY = -halfHeight; modelY <= halfHeight; modelY += step) {
            int screenY = (int) Math.round(viewport.toScreenY(modelY, height));
            if (screenY >= 0 && screenY <= getHeight()) {
                drawLabel(g2, metrics, String.valueOf(modelY), 2, screenY + 2);
            }
        }
    }

    private void drawLabel(Graphics2D g2, FontMetrics metrics, String text, int x, int y) {
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getAscent();
        g2.setColor(Color.WHITE);
        g2.fillRect(x, y, textWidth + 2, textHeight + 2);
        g2.setColor(Color.DARK_GRAY);
        g2.drawString(text, x + 1, y + textHeight);
    }

    private int labelStep() {
        double zoom = viewport.getZoom();
        for (int step : LABEL_STEPS) {
            if (step * zoom >= MIN_LABEL_SPACING_PX) {
                return step;
            }
        }
        return 0;
    }
}
