package com.puc.cg.algorithms.clipping;

import com.puc.cg.commons.models.Window;
import com.puc.cg.commons.models.impl.LineSegment;

public interface ClipWindowAlgorithm {
    LineSegment clip(LineSegment line, Window window);
}
