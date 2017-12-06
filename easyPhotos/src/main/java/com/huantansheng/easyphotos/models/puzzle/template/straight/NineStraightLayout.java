package com.huantansheng.easyphotos.models.puzzle.template.straight;

import com.huantansheng.easyphotos.models.puzzle.Line;

/**
 * @author wupanjie
 */
public class NineStraightLayout extends NumberStraightLayout {
  public NineStraightLayout(int theme) {
    super(theme);
  }

  @Override
  public int getThemeCount() {
    return 8;
  }

  @Override
  public void layout() {
    switch (theme) {
      case 0:
        cutAreaEqualPart(0, 2, 2);
        break;
      case 1:
        addLine(0, Line.Direction.VERTICAL, 3f / 4);
        addLine(0, Line.Direction.VERTICAL, 1f / 3);
        cutAreaEqualPart(2, 4, Line.Direction.HORIZONTAL);
        cutAreaEqualPart(0, 4, Line.Direction.HORIZONTAL);
        break;
      case 2:
        addLine(0, Line.Direction.HORIZONTAL, 3f / 4);
        addLine(0, Line.Direction.HORIZONTAL, 1f / 3);
        cutAreaEqualPart(2, 4, Line.Direction.VERTICAL);
        cutAreaEqualPart(0, 4, Line.Direction.VERTICAL);
        break;
      case 3:
        addLine(0, Line.Direction.HORIZONTAL, 3f / 4);
        addLine(0, Line.Direction.HORIZONTAL, 1f / 3);
        cutAreaEqualPart(2, 3, Line.Direction.VERTICAL);
        addLine(1, Line.Direction.VERTICAL, 3f / 4);
        addLine(1, Line.Direction.VERTICAL, 1f / 3);
        cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
        break;
      case 4:
        addLine(0, Line.Direction.VERTICAL, 3f / 4);
        addLine(0, Line.Direction.VERTICAL, 1f / 3);
        cutAreaEqualPart(2, 3, Line.Direction.HORIZONTAL);
        addLine(1, Line.Direction.HORIZONTAL, 3f / 4);
        addLine(1, Line.Direction.HORIZONTAL, 1f / 3);
        cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
        break;
      case 5:
        cutAreaEqualPart(0, 3, Line.Direction.VERTICAL);
        addLine(2, Line.Direction.HORIZONTAL, 3f / 4);
        addLine(2, Line.Direction.HORIZONTAL, 1f / 3);
        cutAreaEqualPart(1, 3, Line.Direction.HORIZONTAL);
        addLine(0, Line.Direction.HORIZONTAL, 3f / 4);
        addLine(0, Line.Direction.HORIZONTAL, 1f / 3);
        break;
      case 6:
        cutAreaEqualPart(0, 3, Line.Direction.HORIZONTAL);
        addLine(2, Line.Direction.VERTICAL, 3f / 4);
        addLine(2, Line.Direction.VERTICAL, 1f / 3);
        cutAreaEqualPart(1, 3, Line.Direction.VERTICAL);
        addLine(0, Line.Direction.VERTICAL, 3f / 4);
        addLine(0, Line.Direction.VERTICAL, 1f / 3);
        break;
      case 7:
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        cutAreaEqualPart(1, 1, 3);
        break;
      default:
        break;
    }
  }
}
