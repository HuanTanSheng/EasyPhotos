package com.huantansheng.easyphotos.models.puzzle.slant;

import android.graphics.RectF;
import android.util.Pair;

import com.huantansheng.easyphotos.models.puzzle.Area;
import com.huantansheng.easyphotos.models.puzzle.Line;
import com.huantansheng.easyphotos.models.puzzle.PuzzleLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.huantansheng.easyphotos.models.puzzle.slant.SlantUtils.createLine;
import static com.huantansheng.easyphotos.models.puzzle.slant.SlantUtils.cutAreaCross;
import static com.huantansheng.easyphotos.models.puzzle.slant.SlantUtils.cutAreaWith;


/**
 * 斜线布局，外围区域为一矩形
 *
 * @author wupanjie
 */
public abstract class SlantPuzzleLayout implements PuzzleLayout {
  private RectF bounds;
  private SlantArea outerArea;

  private List<Line> outerLines = new ArrayList<>(4);
  private List<SlantArea> areas = new ArrayList<>();
  private List<Line> lines = new ArrayList<>();

  private float padding;
  private float radian;
  private int color;

  private Comparator<SlantArea> areaComparator = new SlantArea.AreaComparator();

  private ArrayList<Step> steps = new ArrayList<>();

  protected SlantPuzzleLayout() {

  }

  @Override
  public void setOuterBounds(RectF bounds) {
    reset();

    this.bounds = bounds;

    CrossoverPointF leftTop = new CrossoverPointF(bounds.left, bounds.top);
    CrossoverPointF rightTop = new CrossoverPointF(bounds.right, bounds.top);
    CrossoverPointF leftBottom = new CrossoverPointF(bounds.left, bounds.bottom);
    CrossoverPointF rightBottom = new CrossoverPointF(bounds.right, bounds.bottom);

    SlantLine lineLeft = new SlantLine(leftTop, leftBottom, Line.Direction.VERTICAL);
    SlantLine lineTop = new SlantLine(leftTop, rightTop, Line.Direction.HORIZONTAL);
    SlantLine lineRight = new SlantLine(rightTop, rightBottom, Line.Direction.VERTICAL);
    SlantLine lineBottom = new SlantLine(leftBottom, rightBottom, Line.Direction.HORIZONTAL);

    outerLines.clear();

    outerLines.add(lineLeft);
    outerLines.add(lineTop);
    outerLines.add(lineRight);
    outerLines.add(lineBottom);

    outerArea = new SlantArea();
    outerArea.lineLeft = lineLeft;
    outerArea.lineTop = lineTop;
    outerArea.lineRight = lineRight;
    outerArea.lineBottom = lineBottom;

    outerArea.updateCornerPoints();

    areas.clear();
    areas.add(outerArea);
  }

  public abstract void layout();

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
      if (l.direction() != line.direction()) {
        continue;
      }

      if (l.attachStartLine() != line.attachStartLine()
          || l.attachEndLine() != line.attachEndLine()) {
        continue;
      }

      if (l.direction() == Line.Direction.HORIZONTAL) {
        if (l.minY() > line.lowerLine().maxY() && l.maxY() < line.minY()) {
          line.setLowerLine(l);
        }
      } else {
        if (l.minX() > line.lowerLine().maxX() && l.maxX() < line.minX()) {
          line.setLowerLine(l);
        }
      }
    }
  }

  private void updateUpperLine(final Line line) {
    for (int i = 0; i < lines.size(); i++) {
      Line l = lines.get(i);
      if (l.direction() != line.direction()) {
        continue;
      }

      if (l.attachStartLine() != line.attachStartLine()
          || l.attachEndLine() != line.attachEndLine()) {
        continue;
      }

      if (l.direction() == Line.Direction.HORIZONTAL) {
        if (l.maxY() < line.upperLine().minY() && l.minY() > line.maxY()) {
          line.setUpperLine(l);
        }
      } else {
        if (l.maxX() < line.upperLine().minX() && l.minX() > line.maxX()) {
          line.setUpperLine(l);
        }
      }
    }
  }

  @Override
  public int getAreaCount() {
    return areas.size();
  }

  @Override
  public void reset() {
    lines.clear();
    areas.clear();
    areas.add(outerArea);
    steps.clear();
  }

  @Override
  public void update() {
    for (int i = 0; i < lines.size(); i++) {
      lines.get(i).update(width(), height());
    }

    for (int i = 0; i < areas.size(); i++) {
      areas.get(i).updateCornerPoints();
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

  private void sortAreas() {
    Collections.sort(areas, areaComparator);
  }

  @Override
  public List<Line> getOuterLines() {
    return outerLines;
  }

  @Override
  public Area getOuterArea() {
    return outerArea;
  }

  public List<SlantArea> getAreas() {
    return areas;
  }

  @Override
  public SlantArea getArea(int position) {
    return areas.get(position);
  }

  @Override
  public List<Line> getLines() {
    return lines;
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

    outerArea.updateCornerPoints();
    update();
  }

  @Override
  public float getPadding() {
    return padding;
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

  protected List<SlantArea> addLine(int position, Line.Direction direction, float ratio) {
    return addLine(position, direction, ratio, ratio);
  }

  protected List<SlantArea> addLine(int position, Line.Direction direction, float startRatio,
                                    float endRatio) {
    SlantArea area = areas.get(position);
    areas.remove(area);
    SlantLine line = createLine(area, direction, startRatio, endRatio);
    lines.add(line);

    List<SlantArea> increasedAreas = cutAreaWith(area, line);

    areas.addAll(increasedAreas);

    updateLineLimit();
    sortAreas();

    Step step = new Step();
    step.type = Step.ADD_LINE;
    step.direction = direction == Line.Direction.HORIZONTAL ? 0 : 1;
    step.position = position;
    steps.add(step);

    return increasedAreas;
  }

  protected void addCross(int position, float startRatio1, float endRatio1,
      float startRatio2, float endRatio2) {
    SlantArea area = areas.get(position);
    areas.remove(area);

    SlantLine horizontal = createLine(area, Line.Direction.HORIZONTAL, startRatio1, endRatio1);
    SlantLine vertical = createLine(area, Line.Direction.VERTICAL, startRatio2, endRatio2);
    lines.add(horizontal);
    lines.add(vertical);

    List<SlantArea> increasedAreas = cutAreaCross(area, horizontal, vertical);

    areas.addAll(increasedAreas);
    sortAreas();

    Step step = new Step();
    step.type = Step.ADD_CROSS;
    step.position = position;
    steps.add(step);
  }

  protected void cutArea(int position, int hSize, int vSize) {
    SlantArea area = areas.get(position);
    areas.remove(area);

    Pair<List<SlantLine>, List<SlantArea>> spilt =
        cutAreaWith(area, hSize, vSize);

    lines.addAll(spilt.first);
    areas.addAll(spilt.second);

    updateLineLimit();
    sortAreas();

    Step step = new Step();
    step.type = Step.CUT_EQUAL_PART_ONE;
    step.position = position;
    step.hSize = hSize;
    step.vSize = vSize;
    steps.add(step);
  }

  @Override
  public Info generateInfo() {
    Info info = new Info();
    info.type = Info.TYPE_SLANT;
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
