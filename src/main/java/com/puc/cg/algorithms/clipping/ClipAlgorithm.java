package com.puc.cg.algorithms.clipping;

import com.puc.cg.algorithms.clipping.impl.CohenSutherland;
import com.puc.cg.algorithms.clipping.impl.LiangBarsky;
import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.models.impl.LineSegment;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ClipAlgorithm implements ClipWindowAlgorithm {
    COHEN_SUTHERLAND(new CohenSutherland()),
    LIANG_BARSKY(new LiangBarsky());

    private final ClipWindowAlgorithm delegate;

    @Override
    public LineSegment clip(LineSegment line, Window window) {
        return delegate.clip(line, window);
    }
}
