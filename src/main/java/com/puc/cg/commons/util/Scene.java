package com.puc.cg.commons.util;

import com.puc.cg.commons.models.FillAction;
import com.puc.cg.commons.models.Shape;
import com.puc.cg.commons.models.Window;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

@Getter
public class Scene {
    private final List<Shape> shapes = new ArrayList<>();
    private final List<FillAction> fills = new ArrayList<>();
    private final Deque<Snapshot> history = new ArrayDeque<>();

    @Setter
    private Window clipWindow;

    public void clear() {
        shapes.clear();
        fills.clear();
        clipWindow = null;
    }

    public void pushHistory() {
        history.push(new Snapshot(new ArrayList<>(shapes), new ArrayList<>(fills), clipWindow));
    }

    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        Snapshot previous = history.pop();
        shapes.clear();
        shapes.addAll(previous.shapes());
        fills.clear();
        fills.addAll(previous.fills());
        clipWindow = previous.clipWindow();
        return true;
    }

    private record Snapshot(List<Shape> shapes, List<FillAction> fills, Window clipWindow) {
    }
}
