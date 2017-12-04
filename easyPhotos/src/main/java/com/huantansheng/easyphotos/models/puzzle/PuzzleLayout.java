package com.huantansheng.easyphotos.models.puzzle;

import android.graphics.RectF;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wupanjie
 */
public interface PuzzleLayout {
  void setOuterBounds(RectF bounds);

  void layout();

  int getAreaCount();

  List<Line> getOuterLines();

  List<Line> getLines();

  Area getOuterArea();

  void update();

  void reset();

  Area getArea(int position);

  float width();

  float height();

  void setPadding(float padding);

  float getPadding();

  float getRadian();

  void setRadian(float radian);

  Info generateInfo();

  void setColor(int color);

  int getColor();

  class Info {
    public static final int TYPE_STRAIGHT = 0;
    public static final int TYPE_SLANT = 1;

    public int type;
    public ArrayList<Step> steps;
    public ArrayList<LineInfo> lineInfos;
    public float padding;
    public float radian;
    public int color;
  }

  class Step {
    public static final int ADD_LINE = 0;
    public static final int ADD_CROSS = 1;
    public static final int CUT_EQUAL_PART_ONE = 2;
    public static final int CUT_EQUAL_PART_TWO = 3;
    public static final int CUT_SPIRAL = 4;

    public int type;
    public int direction;
    public int position;
    public int part;
    public int hSize;
    public int vSize;
  }

  class LineInfo {
    public float startX;
    public float startY;
    public float endX;
    public float endY;

    public LineInfo(Line line){
      startX = line.startPoint().x;
      startY = line.startPoint().y;
      endX = line.endPoint().x;
      endY = line.endPoint().y;
    }
  }
}
