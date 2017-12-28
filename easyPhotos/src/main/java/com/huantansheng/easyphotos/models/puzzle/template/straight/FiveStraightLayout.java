package com.huantansheng.easyphotos.models.puzzle.template.straight;

import com.huantansheng.easyphotos.models.puzzle.Line;

/**
 * @author wupanjie
 */
public class FiveStraightLayout extends NumberStraightLayout {

  public FiveStraightLayout(int theme) {
    super(theme);
  }

  @Override
  public int getThemeCount() {
    return 15;
  }

  @Override
  public void layout() {
    switch (theme) {
      case 0:
        addLine(0, Line.Direction.HORIZONTAL, 2f / 5);
        addLine(0, Line.Direction.VERTICAL, 1f / 2);
        cutAreaEqualPart(2, 3, Line.Direction.VERTICAL);
        break;
      case 1:
        addLine(0, Line.Direction.HORIZONTAL, 3f / 5);
        cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
        addLine(3, Line.Direction.VERTICAL, 1f / 2);
        break;
      case 2:
        addLine(0, Line.Direction.VERTICAL, 2f / 5);
        cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
        addLine(1, Line.Direction.HORIZONTAL, 1f / 2);
        break;
      case 3:
        addLine(0, Line.Direction.VERTICAL, 2f / 5);
        cutAreaEqualPart(1, 3, Line.Direction.HORIZONTAL);
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        break;
      case 4:
        addLine(0, Line.Direction.HORIZONTAL, 3f / 4);
        cutAreaEqualPart(1, 4, Line.Direction.VERTICAL);
        break;
      case 5:
        addLine(0, Line.Direction.HORIZONTAL, 1f / 4);
        cutAreaEqualPart(0, 4, Line.Direction.VERTICAL);
        break;
      case 6:
        addLine(0, Line.Direction.VERTICAL, 3f / 4);
        cutAreaEqualPart(1, 4, Line.Direction.HORIZONTAL);
        break;
      case 7:
        addLine(0, Line.Direction.VERTICAL, 1f / 4);
        cutAreaEqualPart(0, 4, Line.Direction.HORIZONTAL);
        break;
      case 8:
        addLine(0, Line.Direction.HORIZONTAL, 1f / 4);
        addLine(1, Line.Direction.HORIZONTAL, 2f / 3);
        addLine(0, Line.Direction.VERTICAL, 1f / 2);
        addLine(3, Line.Direction.VERTICAL, 1f / 2);
        break;
      case 9:
        addLine(0, Line.Direction.VERTICAL, 1f / 4);
        addLine(1, Line.Direction.VERTICAL, 2f / 3);
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        addLine(2, Line.Direction.HORIZONTAL, 1f / 2);
        break;
      case 10:
        addCross(0, 1f / 3);
        addLine(2, Line.Direction.HORIZONTAL, 1f / 2);
        break;
      case 11:
        addCross(0, 2f / 3);
        addLine(1, Line.Direction.HORIZONTAL, 1f / 2);
        break;
      case 12:
        addCross(0, 1f / 3, 2f / 3);
        addLine(3, Line.Direction.HORIZONTAL, 1f / 2);
        break;
      case 13:
        addCross(0, 2f / 3, 1f / 3);
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        break;
      case 14:
        cutSpiral(0);
        break;
      default:
        cutAreaEqualPart(0, 5, Line.Direction.HORIZONTAL);
        break;
    }
  }
}
