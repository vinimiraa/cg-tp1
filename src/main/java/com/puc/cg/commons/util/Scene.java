package com.puc.cg.commons.util;

import com.puc.cg.commons.models.FillAction;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.models.Window;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Scene {
    private final List<Shape> shapes = new ArrayList<>();
    private final List<FillAction> fills = new ArrayList<>();

    @Setter
    private Window clipWindow;

    public void clear() {
        shapes.clear();
        fills.clear();
        clipWindow = null;
    }
}
