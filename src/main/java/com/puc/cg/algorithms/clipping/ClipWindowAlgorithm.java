package com.puc.cg.algorithms.clipping;

import com.puc.cg.commons.model.Window;
import com.puc.cg.commons.model.LineSegment;

public interface ClipWindowAlgorithm {
    LineSegment clip(LineSegment line, Window window);
}
