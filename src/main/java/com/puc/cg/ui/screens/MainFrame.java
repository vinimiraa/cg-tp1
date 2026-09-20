package com.puc.cg.ui.screens;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.WindowConstants;
import java.awt.BorderLayout;

public class MainFrame extends JFrame {
    public MainFrame() {
        super("TP1 Algoritmos - Computação Gráfica");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        DrawingPanel drawingPanel = new DrawingPanel();
        ToolPanel toolPanel = new ToolPanel(drawingPanel);
        JLabel statusBar = new JLabel();
        drawingPanel.setStatusLabel(statusBar);

        setLayout(new BorderLayout());
        add(toolPanel, BorderLayout.NORTH);
        add(drawingPanel, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }
}
