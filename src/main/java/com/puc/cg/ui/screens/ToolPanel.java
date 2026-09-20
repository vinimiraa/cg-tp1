package com.puc.cg.ui.screens;

import com.puc.cg.algorithms.clipping.ClipAlgorithm;
import com.puc.cg.algorithms.filling.Connectivity;
import com.puc.cg.algorithms.filling.FillMethod;
import com.puc.cg.algorithms.raster.LineAlgorithm;
import com.puc.cg.algorithms.transform.Matrix3;
import com.puc.cg.algorithms.transform.Transformations;
import com.puc.cg.commons.models.Point2D;
import com.puc.cg.commons.util.Dimensions;
import com.puc.cg.commons.util.Icons;
import com.puc.cg.commons.util.Palette;
import com.puc.cg.ui.Tool;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.function.IntFunction;

public class ToolPanel extends JPanel {
    private static final int TRANSLATION_RANGE = 200;
    private static final int ROTATION_RANGE = 180;
    private static final int SCALE_MIN = 1;
    private static final int SCALE_MAX = 50;
    private static final int SCALE_DEFAULT = 10;
    private static final double SCALE_DIVISOR = 10.0;

    public ToolPanel(DrawingPanel drawingPanel) {
        setLayout(new FlowLayout(FlowLayout.LEFT, Dimensions.GAP, Dimensions.GAP));

        JPanel[] groups = {
                buildAlgorithmsGroup(drawingPanel),
                buildToolsGroup(drawingPanel),
                buildTransformGroup(drawingPanel),
                buildImageGroup(drawingPanel),
                buildFillGroup(drawingPanel)
        };
        equalizeHeights(groups);
        for (JPanel group : groups) {
            add(group);
        }
    }

    private void equalizeHeights(JPanel[] groups) {
        int maxHeight = 0;
        for (JPanel group : groups) {
            maxHeight = Math.max(maxHeight, group.getPreferredSize().height);
        }
        for (JPanel group : groups) {
            Dimension size = group.getPreferredSize();
            group.setPreferredSize(new Dimension(size.width, maxHeight));
        }
    }

    private JPanel buildAlgorithmsGroup(DrawingPanel drawingPanel) {
        JPanel group = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.GAP, Dimensions.GAP));
        group.setBorder(groupBorder("algoritmos"));

        group.add(radioColumn("Rasterização",
                new RadioOption("DDA", true, e -> drawingPanel.setLineAlgorithm(LineAlgorithm.DDA)),
                new RadioOption("Bresenham", false, e -> drawingPanel.setLineAlgorithm(LineAlgorithm.BRESENHAM))
        ));

        group.add(radioColumn("Recorte",
                new RadioOption("Cohen-Sutherland", true, e -> drawingPanel.setClipAlgorithm(ClipAlgorithm.COHEN_SUTHERLAND)),
                new RadioOption("Liang-Barsky", false, e -> drawingPanel.setClipAlgorithm(ClipAlgorithm.LIANG_BARSKY))
        ));

        group.add(radioColumn("Circunferência",
                new RadioOption("Bresenham", true, null)
        ));

        group.add(radioColumn("Preenchimento",
                new RadioOption("Flood Fill", true, e -> drawingPanel.setFillMethod(FillMethod.FLOOD_FILL)),
                new RadioOption("Boundary Fill", false, e -> drawingPanel.setFillMethod(FillMethod.BOUNDARY_FILL))
        ));

        return group;
    }

    private JPanel buildToolsGroup(DrawingPanel drawingPanel) {
        JPanel group = new JPanel(new GridLayout(0, 2, Dimensions.GAP, Dimensions.GAP));
        group.setBorder(groupBorder("ferramentas"));
        ButtonGroup toolGroup = new ButtonGroup();

        addToolToggle(group, toolGroup, Icons.MOVE_ZOOM + " Mover/Zoom", Tool.PAN_ZOOM, drawingPanel, true);
        addToolToggle(group, toolGroup, Icons.LINE + " Reta", Tool.ADD_LINE, drawingPanel, false);
        addToolToggle(group, toolGroup, Icons.CIRCLE + " Círculo", Tool.ADD_CIRCLE, drawingPanel, false);
        addToolToggle(group, toolGroup, Icons.POLYGON + " Polígono", Tool.ADD_POLYGON, drawingPanel, false);
        addToolToggle(group, toolGroup, Icons.SELECT + " Selecionar", Tool.SELECT_RECT, drawingPanel, false);
        addToolToggle(group, toolGroup, Icons.CLIP + " Recorte", Tool.DEFINE_CLIP_WINDOW, drawingPanel, false);
        addToolToggle(group, toolGroup, Icons.FILL + " Preencher", Tool.FILL, drawingPanel, false);

        JButton clearButton = new JButton(Icons.CLEAR + " Limpar tudo");
        clearButton.addActionListener(e -> drawingPanel.clearScene());
        fixedSize(clearButton, Dimensions.BUTTON_WIDTH);
        group.add(clearButton);

        return group;
    }

    private JPanel buildTransformGroup(DrawingPanel drawingPanel) {
        JPanel group = new JPanel();
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setBorder(groupBorder("transformações"));

        JSlider dxSlider = new JSlider(-TRANSLATION_RANGE, TRANSLATION_RANGE, 0);
        JSlider dySlider = new JSlider(-TRANSLATION_RANGE, TRANSLATION_RANGE, 0);
        JSlider rotationSlider = new JSlider(-ROTATION_RANGE, ROTATION_RANGE, 0);
        JSlider scaleSlider = new JSlider(SCALE_MIN, SCALE_MAX, SCALE_DEFAULT);

        group.add(sliderRow(dxSlider, v -> "Translação X: " + v));
        group.add(sliderRow(dySlider, v -> "Translação Y: " + v));
        group.add(sliderRow(rotationSlider, v -> "Rotação: " + v + "°"));
        group.add(sliderRow(scaleSlider, v -> String.format("Escala: %.1fx", v / SCALE_DIVISOR)));

        JButton applyButton = new JButton("Aplicar transformação");
        applyButton.addActionListener(e -> {
            double scale = scaleSlider.getValue() / SCALE_DIVISOR;
            Matrix3 rotateAndScale = Transformations.rotation(rotationSlider.getValue())
                    .multiply(Transformations.scale(scale, scale));
            applyPivoted(drawingPanel, rotateAndScale);
            drawingPanel.applyTransform(Transformations.translation(dxSlider.getValue(), dySlider.getValue()));
        });
        fixedSize(applyButton, Dimensions.LABEL_WIDTH + Dimensions.GAP + Dimensions.SLIDER_WIDTH);
        group.add(applyButton);

        return group;
    }

    private JPanel buildImageGroup(DrawingPanel drawingPanel) {
        JPanel group = new JPanel(new GridLayout(0, 1, Dimensions.GAP, Dimensions.GAP));
        group.setBorder(groupBorder("imagem"));

        JButton flipHButton = new JButton(Icons.FLIP_HORIZONTAL + " Inverter Horizontal");
        flipHButton.addActionListener(e -> applyPivoted(drawingPanel, Transformations.reflectionY()));
        fixedSize(flipHButton, Dimensions.BUTTON_WIDTH);
        group.add(flipHButton);

        JButton flipVButton = new JButton(Icons.FLIP_VERTICAL + " Inverter Vertical");
        flipVButton.addActionListener(e -> applyPivoted(drawingPanel, Transformations.reflectionX()));
        fixedSize(flipVButton, Dimensions.BUTTON_WIDTH);
        group.add(flipVButton);

        JButton flipBothButton = new JButton("Inverter H+V");
        flipBothButton.addActionListener(e -> applyPivoted(drawingPanel, Transformations.reflectionXY()));
        fixedSize(flipBothButton, Dimensions.BUTTON_WIDTH);
        group.add(flipBothButton);

        return group;
    }

    private JPanel buildFillGroup(DrawingPanel drawingPanel) {
        JPanel group = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.GAP, Dimensions.GAP));
        group.setBorder(groupBorder("preenchimento"));

        group.add(radioColumn("Cor",
                new RadioOption("Preto", false, e -> drawingPanel.setFillColor(Palette.BLACK)),
                new RadioOption("Vermelho", true, e -> drawingPanel.setFillColor(Palette.RED)),
                new RadioOption("Verde", false, e -> drawingPanel.setFillColor(Palette.GREEN)),
                new RadioOption("Azul", false, e -> drawingPanel.setFillColor(Palette.BLUE))
        ));

        group.add(radioColumn("Conectividade",
                new RadioOption("4-conectado", true, e -> drawingPanel.setConnectivity(Connectivity.FOUR)),
                new RadioOption("8-conectado", false, e -> drawingPanel.setConnectivity(Connectivity.EIGHT))
        ));

        return group;
    }

    private void applyPivoted(DrawingPanel drawingPanel, Matrix3 rawMatrix) {
        Point2D center = drawingPanel.getSelectionCenter();
        if (center == null) {
            return;
        }
        Matrix3 matrix = Transformations.translation(center.x(), center.y())
                .multiply(rawMatrix)
                .multiply(Transformations.translation(-center.x(), -center.y()));
        drawingPanel.applyTransform(matrix);
    }

    private void addToolToggle(JPanel section, ButtonGroup group, String label, Tool tool, DrawingPanel drawingPanel, boolean selected) {
        JToggleButton button = new JToggleButton(label, selected);
        button.addActionListener(e -> drawingPanel.setCurrentTool(tool));
        fixedSize(button, Dimensions.BUTTON_WIDTH);
        group.add(button);
        section.add(button);
    }

    private JPanel radioColumn(String header, RadioOption... options) {
        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.add(new JLabel(header));

        ButtonGroup group = new ButtonGroup();
        for (RadioOption option : options) {
            JRadioButton radio = new JRadioButton(option.label(), option.selected());
            if (option.onSelect() != null) {
                radio.addActionListener(option.onSelect());
            }
            fixedHeight(radio);
            group.add(radio);
            column.add(radio);
        }
        return column;
    }

    private JPanel sliderRow(JSlider slider, IntFunction<String> textFor) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, Dimensions.GAP, Dimensions.GAP));
        JLabel label = new JLabel(textFor.apply(slider.getValue()));
        label.setPreferredSize(new Dimension(Dimensions.LABEL_WIDTH, Dimensions.ROW_HEIGHT));
        slider.setPreferredSize(new Dimension(Dimensions.SLIDER_WIDTH, Dimensions.ROW_HEIGHT));
        slider.addChangeListener(e -> label.setText(textFor.apply(slider.getValue())));
        row.add(label);
        row.add(slider);
        return row;
    }

    private void fixedSize(AbstractButton button, int width) {
        button.setPreferredSize(new Dimension(width, Dimensions.ROW_HEIGHT));
    }

    private void fixedHeight(JComponent component) {
        component.setPreferredSize(new Dimension(component.getPreferredSize().width, Dimensions.ROW_HEIGHT));
    }

    private TitledBorder groupBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), title);
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.BELOW_BOTTOM);
        return border;
    }

    private record RadioOption(String label, boolean selected, ActionListener onSelect) {
    }
}
