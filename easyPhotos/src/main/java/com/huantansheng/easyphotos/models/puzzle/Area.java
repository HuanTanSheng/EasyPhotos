package com.huantansheng.easyphotos.models.puzzle;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import java.util.List;

/**
 * @author wupanjie
 */
public interface Area {
  float left();

  float top();

  float right();

  float bottom();

  float centerX();

  float centerY();

  float width();

  float height();

  PointF getCenterPoint();

  boolean contains(PointF point);

  boolean contains(float x, float y);

  boolean contains(Line line);

  Path getAreaPath();

  RectF getAreaRect();

  List<Line> getLines();

  PointF[] getHandleBarPoints(Line line);

  float radian();

  void setRadian(float radian);

  float getPaddingLeft();

  float getPaddingTop();

  float getPaddingRight();

  float getPaddingBottom();

  void setPadding(float padding);

  void setPadding(float paddingLeft, float paddingTop, float paddingRight, float paddingBottom);
}





