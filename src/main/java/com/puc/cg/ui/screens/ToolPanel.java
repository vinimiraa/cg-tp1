package com.puc.cg.ui.screens;

import com.puc.cg.commons.enums.ClipAlgorithm;
import com.puc.cg.commons.enums.LineAlgorithm;
import com.puc.cg.ui.Tool;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import java.awt.FlowLayout;

public class ToolPanel extends JPanel {
    public ToolPanel(DrawingPanel drawingPanel) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        add(buildAlgorithmsSection(drawingPanel));
        add(new JSeparator());
        add(buildToolsSection(drawingPanel));
        add(new JSeparator());
        add(buildTransformSection());
    }

    private JPanel buildAlgorithmsSection(DrawingPanel drawingPanel) {
        JPanel section = new JPanel(new FlowLayout(FlowLayout.LEFT));

        section.add(new JLabel("Algoritmo de reta:"));
        JRadioButton dda = new JRadioButton("DDA", true);
        JRadioButton bresenhamLine = new JRadioButton("Bresenham");

        dda.addActionListener(e -> drawingPanel.setLineAlgorithm(LineAlgorithm.DDA));
        bresenhamLine.addActionListener(e -> drawingPanel.setLineAlgorithm(LineAlgorithm.BRESENHAM));

        ButtonGroup lineAlgorithmGroup = new ButtonGroup();
        lineAlgorithmGroup.add(dda);
        lineAlgorithmGroup.add(bresenhamLine);

        section.add(dda);
        section.add(bresenhamLine);

        section.add(new JLabel("\tAlgoritmo de recorte:"));

        JRadioButton cohenSutherland = new JRadioButton("Cohen-Sutherland", true);
        JRadioButton liangBarsky = new JRadioButton("Liang-Barsky");

        cohenSutherland.addActionListener(e -> drawingPanel.setClipAlgorithm(ClipAlgorithm.COHEN_SUTHERLAND));
        liangBarsky.addActionListener(e -> drawingPanel.setClipAlgorithm(ClipAlgorithm.LIANG_BARSKY));

        ButtonGroup clipAlgorithmGroup = new ButtonGroup();
        clipAlgorithmGroup.add(cohenSutherland);
        clipAlgorithmGroup.add(liangBarsky);


        section.add(cohenSutherland);
        section.add(liangBarsky);

        return section;
    }

    private JPanel buildToolsSection(DrawingPanel drawingPanel) {
        JPanel section = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton panZoomButton = new JButton("Mover/Zoom");
        panZoomButton.addActionListener(e -> drawingPanel.setCurrentTool(Tool.PAN_ZOOM));
        section.add(panZoomButton);

        JButton lineButton = new JButton("Reta");
        lineButton.addActionListener(e -> drawingPanel.setCurrentTool(Tool.ADD_LINE));
        section.add(lineButton);

        JButton circleButton = new JButton("Círculo");
        circleButton.addActionListener(e -> drawingPanel.setCurrentTool(Tool.ADD_CIRCLE));
        section.add(circleButton);

        JButton clipWindowButton = new JButton("Definir janela de recorte");
        clipWindowButton.addActionListener(e -> drawingPanel.setCurrentTool(Tool.DEFINE_CLIP_WINDOW));
        section.add(clipWindowButton);

        JButton clearButton = new JButton("Limpar tudo");
        clearButton.addActionListener(e -> drawingPanel.clearScene());
        section.add(clearButton);

        return section;
    }

    private JPanel buildTransformSection() {
        JPanel section = new JPanel(new FlowLayout(FlowLayout.LEFT));

        section.add(new JLabel("Translação (dx)"));
        section.add(new JSlider(-200, 200, 0));
        section.add(new JLabel("Translação (dy)"));
        section.add(new JSlider(-200, 200, 0));
        section.add(new JLabel("Rotação (graus)"));
        section.add(new JSlider(-180, 180, 0));
        section.add(new JLabel("Escala (sx, sy) x10"));
        section.add(new JSlider(1, 50, 10));
        section.add(new JButton("Refletir X"));
        section.add(new JButton("Refletir Y"));
        section.add(new JButton("Refletir XY"));
        section.add(new JButton("Aplicar transformação"));

        return section;
    }
}
