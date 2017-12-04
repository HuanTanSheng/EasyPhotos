package com.huantansheng.easyphotos.models.puzzle.straight;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Pair;

import com.huantansheng.easyphotos.models.puzzle.Area;
import com.huantansheng.easyphotos.models.puzzle.Line;
import com.huantansheng.easyphotos.models.puzzle.PuzzleLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.huantansheng.easyphotos.models.puzzle.straight.StraightUtils.createLine;
import static com.huantansheng.easyphotos.models.puzzle.straight.StraightUtils.cutAreaCross;
import static com.huantansheng.easyphotos.models.puzzle.straight.StraightUtils.cutAreaSpiral;


/**
 * @author wupanjie
 */
public abstract class StraightPuzzleLayout implements PuzzleLayout {
  private RectF bounds;
  private StraightArea outerArea;

  private List<StraightArea> areas = new ArrayList<>();
  private List<Line> lines = new ArrayList<>();
  private List<Line> outerLines = new ArrayList<>(4);

  private float padding;
  private float radian;
  private int color;

  private Comparator<StraightArea> areaComparator = new StraightArea.AreaComparator();

  private ArrayList<Step> steps = new ArrayList<>();

  protected StraightPuzzleLayout() {

  }

  @Override
  public void setOuterBounds(RectF bounds) {
    reset();

    this.bounds = bounds;

    PointF one = new PointF(bounds.left, bounds.top);
    PointF two = new PointF(bounds.right, bounds.top);
    PointF three = new PointF(bounds.left, bounds.bottom);
    PointF four = new PointF(bounds.right, bounds.bottom);

    StraightLine lineLeft = new StraightLine(one, three);
    StraightLine lineTop = new StraightLine(one, two);
    StraightLine lineRight = new StraightLine(two, four);
    StraightLine lineBottom = new StraightLine(three, four);

    outerLines.clear();

    outerLines.add(lineLeft);
    outerLines.add(lineTop);
    outerLines.add(lineRight);
    outerLines.add(lineBottom);

    outerArea = new StraightArea();
    outerArea.lineLeft = lineLeft;
    outerArea.lineTop = lineTop;
    outerArea.lineRight = lineRight;
    outerArea.lineBottom = lineBottom;

    areas.clear();
    areas.add(outerArea);
  }

  @Override
  public abstract void layout();

  @Override
  public int getAreaCount() {
    return areas.size();
  }

  @Override
  public List<Line> getOuterLines() {
    return outerLines;
  }

  @Override
  public List<Line> getLines() {
    return lines;
  }

  @Override
  public void update() {
    for (Line line : lines) {
      line.update(width(), height());
    }
  }

  @Override
  public float width() {
    return outerArea == null ? 0 : outerArea.width();
  }

  @Override
  public float height() {
    return outerArea == null ? 0 : outerArea.height();
  }

  @Override
  public void reset() {
    lines.clear();
    areas.clear();
    areas.add(outerArea);
    steps.clear();
  }

  @Override
  public Area getArea(int position) {
    return areas.get(position);
  }

  @Override

  public StraightArea getOuterArea() {
    return outerArea;
  }

  @Override
  public void setPadding(float padding) {
    this.padding = padding;

    for (Area area : areas) {
      area.setPadding(padding);
    }

    outerArea.lineLeft.startPoint().set(bounds.left + padding, bounds.top + padding);
    outerArea.lineLeft.endPoint().set(bounds.left + padding, bounds.bottom - padding);

    outerArea.lineRight.startPoint().set(bounds.right - padding, bounds.top + padding);
    outerArea.lineRight.endPoint().set(bounds.right - padding, bounds.bottom - padding);

    update();
  }

  @Override
  public float getPadding() {
    return padding;
  }

  protected void addLine(int position, Line.Direction direction, float ratio) {
    StraightArea area = areas.get(position);
    addLine(area, direction, ratio);

    Step step = new Step();
    step.type = Step.ADD_LINE;
    step.direction = direction == Line.Direction.HORIZONTAL ? 0 : 1;
    step.position = position;
    steps.add(step);
  }

  private List<StraightArea> addLine(StraightArea area, Line.Direction direction, float ratio) {
    areas.remove(area);
    StraightLine line = createLine(area, direction, ratio);
    lines.add(line);

    List<StraightArea> increasedArea = StraightUtils.cutArea(area, line);
    areas.addAll(increasedArea);

    updateLineLimit();
    sortAreas();

    return increasedArea;
  }

  protected void cutAreaEqualPart(int position, int part, Line.Direction direction) {
    StraightArea temp = areas.get(position);
    for (int i = part; i > 1; i--) {
      temp = addLine(temp, direction, (float) (i - 1) / i).get(0);
    }

    Step step = new Step();
    step.type = Step.CUT_EQUAL_PART_TWO;
    step.part = part;
    step.position = position;
    step.direction = direction == Line.Direction.HORIZONTAL ? 0 : 1;
    steps.add(step);
  }

  protected void addCross(int position, float ratio) {
    addCross(position, ratio, ratio);
  }

  protected void addCross(int position, float horizontalRatio, float verticalRatio) {
    StraightArea area = areas.get(position);
    areas.remove(area);
    StraightLine horizontal = createLine(area, Line.Direction.HORIZONTAL, horizontalRatio);
    StraightLine vertical = createLine(area, Line.Direction.VERTICAL, verticalRatio);
    lines.add(horizontal);
    lines.add(vertical);

    List<StraightArea> newAreas = cutAreaCross(area, horizontal, vertical);
    areas.addAll(newAreas);

    updateLineLimit();
    sortAreas();

    Step step = new Step();
    step.type = Step.ADD_CROSS;
    step.position = position;
    steps.add(step);
  }

  protected void cutAreaEqualPart(int position, int hSize, int vSize) {
    StraightArea area = areas.get(position);
    areas.remove(area);
    Pair<List<StraightLine>, List<StraightArea>> increased =
        StraightUtils.cutArea(area, hSize, vSize);
    List<StraightLine> newLines = increased.first;
    List<StraightArea> newAreas = increased.second;

    lines.addAll(newLines);
    areas.addAll(newAreas);

    updateLineLimit();
    sortAreas();

    Step step = new Step();
    step.type = Step.CUT_EQUAL_PART_ONE;
    step.position = position;
    step.hSize = hSize;
    step.vSize = vSize;
    steps.add(step);
  }

  protected void cutSpiral(int position) {
    StraightArea area = areas.get(position);
    areas.remove(area);
    Pair<List<StraightLine>, List<StraightArea>> spilt = cutAreaSpiral(area);

    lines.addAll(spilt.first);
    areas.addAll(spilt.second);

    updateLineLimit();
    sortAreas();

    Step step = new Step();
    step.type = Step.CUT_SPIRAL;
    step.position = position;
    steps.add(step);
  }

  private void sortAreas() {
    Collections.sort(areas, areaComparator);
  }

  private void updateLineLimit() {
    for (int i = 0; i < lines.size(); i++) {
      Line line = lines.get(i);
      updateUpperLine(line);
      updateLowerLine(line);
    }
  }

  private void updateLowerLine(final Line line) {
    for (int i = 0; i < lines.size(); i++) {
      Line l = lines.get(i);
      if (l == line) {
        continue;
      }

      if (l.direction() != line.direction()) {
        continue;
      }

      if (l.direction() == Line.Direction.HORIZONTAL) {
        if (l.maxX() <= line.minX() || line.maxX() <= l.minX()) continue;
        if (l.minY() > line.lowerLine().maxY() && l.maxY() < line.minY()) {
          line.setLowerLine(l);
        }
      } else {
        if (l.maxY() <= line.minY() || line.maxY() <= l.minY()) continue;
        if (l.minX() > line.lowerLine().maxX() && l.maxX() < line.minX()) {
          line.setLowerLine(l);
        }
      }
    }
  }

  private void updateUpperLine(final Line line) {
    for (int i = 0; i < lines.size(); i++) {
      Line l = lines.get(i);
      if (l == line) {
        continue;
      }

      if (l.direction() != line.direction()) {
        continue;
      }

      if (l.direction() == Line.Direction.HORIZONTAL) {
        if (l.maxX() <= line.minX() || line.maxX() <= l.minX()) continue;
        if (l.maxY() < line.upperLine().minY() && l.minY() > line.maxY()) {
          line.setUpperLine(l);
        }
      } else {
        if (l.maxY() <= line.minY() || line.maxY() <= l.minY()) continue;
        if (l.maxX() < line.upperLine().minX() && l.minX() > line.maxX()) {
          line.setUpperLine(l);
        }
      }
    }
  }

  @Override
  public float getRadian() {
    return radian;
  }

  @Override
  public void setRadian(float radian) {
    this.radian = radian;
    for (Area area : areas) {
      area.setRadian(radian);
    }
  }

  @Override
  public int getColor() {
    return color;
  }

  @Override
  public void setColor(int color) {
    this.color = color;
  }

  @Override
  public Info generateInfo() {
    Info info = new Info();
    info.type = Info.TYPE_STRAIGHT;
    info.padding = padding;
    info.radian = radian;
    info.color = color;
    info.steps = steps;
    ArrayList<LineInfo> lineInfos = new ArrayList<>();
    for (Line line : lines) {
      LineInfo lineInfo = new LineInfo(line);
      lineInfos.add(lineInfo);
    }
    info.lineInfos = lineInfos;
    return info;
  }
}
