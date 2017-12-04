package com.huantansheng.easyphotos.models.puzzle.straight;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import com.huantansheng.easyphotos.models.puzzle.Line;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * @author wupanjie
 */
class StraightLine implements Line {
  private PointF start;
  private PointF end;

  private PointF previousStart = new PointF();
  private PointF previousEnd = new PointF();

  public Line.Direction direction = Direction.HORIZONTAL;

  StraightLine attachLineStart;
  StraightLine attachLineEnd;

  private Line upperLine;
  private Line lowerLine;

  private RectF bounds = new RectF();

  StraightLine(PointF start, PointF end) {
    this.start = start;
    this.end = end;

    if (start.x == end.x) {
      direction = Line.Direction.VERTICAL;
    } else if (start.y == end.y) {
      direction = Line.Direction.HORIZONTAL;
    } else {
      Log.d("StraightLine", "StraightLine: current only support two direction");
    }
  }

  @Override
  public float length() {
    return (float) Math.sqrt(Math.pow(end.x - start.x, 2) + Math.pow(end.y - start.y, 2));
  }

  @Override
  public PointF startPoint() {
    return start;
  }

  @Override
  public PointF endPoint() {
    return end;
  }

  @Override
  public Line lowerLine() {
    return lowerLine;
  }

  @Override
  public Line upperLine() {
    return upperLine;
  }

  @Override
  public Line attachStartLine() {
    return attachLineStart;
  }

  @Override
  public Line attachEndLine() {
    return attachLineEnd;
  }

  @Override
  public void setLowerLine(Line lowerLine) {
    this.lowerLine = lowerLine;
  }

  @Override
  public void setUpperLine(Line upperLine) {
    this.upperLine = upperLine;
  }

  void setAttachLineStart(StraightLine attachLineStart) {
    this.attachLineStart = attachLineStart;
  }

  void setAttachLineEnd(StraightLine attachLineEnd) {
    this.attachLineEnd = attachLineEnd;
  }

  @Override
  public Direction direction() {
    return direction;
  }

  @Override
  public float slope() {
    return direction == Direction.HORIZONTAL ? 0 : Float.MAX_VALUE;
  }

  @Override
  public boolean contains(float x, float y, float extra) {
    if (direction == Line.Direction.HORIZONTAL) {
      bounds.left = start.x;
      bounds.right = end.x;
      bounds.top = start.y - extra / 2;
      bounds.bottom = start.y + extra / 2;
    } else if (direction == Line.Direction.VERTICAL) {
      bounds.top = start.y;
      bounds.bottom = end.y;
      bounds.left = start.x - extra / 2;
      bounds.right = start.x + extra / 2;
    }

    return bounds.contains(x, y);
  }

  @Override
  public void prepareMove() {
    previousStart.set(start);
    previousEnd.set(end);
  }

  @Override
  public boolean move(float offset, float extra) {
    if (direction == Line.Direction.HORIZONTAL) {
      if (previousStart.y + offset < lowerLine.maxY() + extra
          || previousStart.y + offset > upperLine.minY() - extra
          || previousEnd.y + offset < lowerLine.maxY() + extra
          || previousEnd.y + offset > upperLine.minY() - extra) {
        return false;
      }

      start.y = previousStart.y + offset;
      end.y = previousEnd.y + offset;
    } else {
      if (previousStart.x + offset < lowerLine.maxX() + extra
          || previousStart.x + offset > upperLine.minX() - extra
          || previousEnd.x + offset < lowerLine.maxX() + extra
          || previousEnd.x + offset > upperLine.minX() - extra) {
        return false;
      }

      start.x = previousStart.x + offset;
      end.x = previousEnd.x + offset;
    }

    return true;
  }

  @Override
  public void update(float layoutWidth, float layoutHeight) {
    if (direction == Line.Direction.HORIZONTAL) {
      if (attachLineStart != null) {
        start.x = attachLineStart.getPosition();
      }
      if (attachLineEnd != null) {
        end.x = attachLineEnd.getPosition();
      }
    } else if (direction == Line.Direction.VERTICAL) {
      if (attachLineStart != null) {
        start.y = attachLineStart.getPosition();
      }
      if (attachLineEnd != null) {
        end.y = attachLineEnd.getPosition();
      }
    }
  }

  public float getPosition() {
    if (direction == Line.Direction.HORIZONTAL) {
      return start.y;
    } else {
      return start.x;
    }
  }

  @Override
  public float minX() {
    return min(start.x, end.x);
  }

  @Override
  public float maxX() {
    return max(start.x, end.x);
  }

  @Override
  public float minY() {
    return min(start.y, end.y);
  }

  @Override
  public float maxY() {
    return max(start.y, end.y);
  }

  @Override
  public void offset(float x, float y) {
    start.offset(x, y);
    end.offset(x, y);
  }

  @Override
  public String toString() {
    return "start --> " + start.toString() + ",end --> " + end.toString();
  }
}
