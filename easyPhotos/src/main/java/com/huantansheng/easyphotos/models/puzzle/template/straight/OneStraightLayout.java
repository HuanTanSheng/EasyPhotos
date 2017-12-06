package com.huantansheng.easyphotos.models.puzzle.template.straight;

import com.huantansheng.easyphotos.models.puzzle.Line;

/**
 * @author wupanjie
 */
public class OneStraightLayout extends NumberStraightLayout {

  public OneStraightLayout(int theme) {
    super(theme);
  }

  @Override
  public int getThemeCount() {
    return 6;
  }

  @Override
  public void layout() {
    switch (theme) {
      case 0:
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        break;
      case 1:
        addLine(0, Line.Direction.VERTICAL, 1f / 2);
        break;
      case 2:
        addCross(0, 1f / 2);
        break;
      case 3:
        cutAreaEqualPart(0, 2, 1);
        break;
      case 4:
        cutAreaEqualPart(0, 1, 2);
        break;
      case 5:
        cutAreaEqualPart(0, 2, 2);
        break;
      default:
        addLine(0, Line.Direction.HORIZONTAL, 1f / 2);
        break;
    }
  }
}
