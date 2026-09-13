package com.puc.cg.commons.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Scene {
    private final List<Point2D> points = new ArrayList<>();
    private final List<LineSegment> lines = new ArrayList<>();
    private final List<Polygon2D> polygons = new ArrayList<>();
    private final List<Circle> circles = new ArrayList<>();

    @Setter
    private Window clipWindow;

    public void clear() {
        points.clear();
        lines.clear();
        polygons.clear();
        circles.clear();
        clipWindow = null;
    }
}
