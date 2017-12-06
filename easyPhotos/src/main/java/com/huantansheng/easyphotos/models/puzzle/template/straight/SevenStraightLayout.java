package com.huantansheng.easyphotos.models.puzzle.template.straight;

import com.huantansheng.easyphotos.models.puzzle.Line;

/**
 * @author wupanjie
 */
public class SevenStraightLayout extends NumberStraightLayout {
  public SevenStraightLayout(int theme) {
    super(theme);
  }

  @Override
  public int getThemeCount() {
    return 9;
  }

  @Override
  public void layout() {
    switch (theme) {
      case 0:
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        cutAreaEqualPart(1, 4, Line.Direction.VERTICAL);
        cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
        break;
      case 1:
        addLine(0, Line.Direction.VERTICAL, 1f / 2);
        cutAreaEqualPart(1, 4, Line.Direction.HORIZONTAL);
        cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
        break;
      case 2:
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        cutAreaEqualPart(1, 1, 2);
        break;
      case 3:
        addLine(0, Line.Direction.HORIZONTAL, 2f / 3);
        cutAreaEqualPart(1, 3, Line.Direction.VERTICAL);
        addCross(0, 1f / 2);
        break;
      case 4:
        cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
        cutAreaEqualPart(2, 3, Line.Direction.HORIZONTAL);
        cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
        break;
      case 5:
        addLine(0, Line.Direction.HORIZONTAL, 2f / 3);
        addLine(1, Line.Direction.VERTICAL, 3f / 4);
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        addLine(1, Line.Direction.VERTICAL, 2f / 5);
        cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
        break;
      case 6:
        addLine(0, Line.Direction.VERTICAL, 2f / 3);
        addLine(1, Line.Direction.HORIZONTAL, 3f / 4);
        addLine(0, Line.Direction.VERTICAL, 1f / 2);
        addLine(1, Line.Direction.HORIZONTAL, 2f / 5);
        cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
        break;
      case 7:
        addLine(0, Line.Direction.VERTICAL, 1f / 4);
        addLine(1, Line.Direction.VERTICAL, 2f / 3);
        addLine(2, Line.Direction.HORIZONTAL, 1f / 2);
        addLine(1, Line.Direction.HORIZONTAL, 3f / 4);
        addLine(1, Line.Direction.HORIZONTAL, 1f / 3);
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        break;
      case 8:
        addLine(0, Line.Direction.HORIZONTAL, 1f / 4);
        addLine(1, Line.Direction.HORIZONTAL, 2f / 3);
        cutAreaEqualPart(2, 3, Line.Direction.VERTICAL);
        cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
        break;
      default:
        break;
    }
  }
}
