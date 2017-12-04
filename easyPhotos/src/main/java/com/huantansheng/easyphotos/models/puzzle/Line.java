package com.huantansheng.easyphotos.models.puzzle;

import android.graphics.PointF;

/**
 * @author wupanjie
 */
public interface Line {
  enum Direction {
    HORIZONTAL, VERTICAL
  }

  float length();

  PointF startPoint();

  PointF endPoint();

  Line lowerLine();

  Line upperLine();

  Line attachStartLine();

  Line attachEndLine();

  void setLowerLine(Line lowerLine);

  void setUpperLine(Line upperLine);

  Direction direction();

  float slope();

  boolean contains(float x, float y, float extra);

  void prepareMove();

  boolean move(float offset, float extra);

  void update(float layoutWidth, float layoutHeight);

  float minX();

  float maxX();

  float minY();

  float maxY();

  void offset(float x, float y);
}
