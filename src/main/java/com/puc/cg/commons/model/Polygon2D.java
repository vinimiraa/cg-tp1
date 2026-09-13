package com.puc.cg.commons.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Polygon2D {
    private final List<Point2D> vertices = new ArrayList<>();

    public void addVertex(Point2D point) {
        vertices.add(point);
    }

}
