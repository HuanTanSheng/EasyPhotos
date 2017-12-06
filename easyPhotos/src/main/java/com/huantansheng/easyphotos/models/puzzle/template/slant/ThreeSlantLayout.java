package com.huantansheng.easyphotos.models.puzzle.template.slant;

import com.huantansheng.easyphotos.models.puzzle.Line;

/**
 * @author wupanjie
 */

public class ThreeSlantLayout extends NumberSlantLayout {
  public ThreeSlantLayout(int theme) {
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
        addLine(0, Line.Direction.HORIZONTAL, 0.5f);
        addLine(0, Line.Direction.VERTICAL, 0.56f, 0.44f);
        break;
      case 1:
        addLine(0, Line.Direction.HORIZONTAL, 0.5f);
        addLine(1, Line.Direction.VERTICAL, 0.56f, 0.44f);
        break;
      case 2:
        addLine(0, Line.Direction.VERTICAL, 0.5f);
        addLine(0, Line.Direction.HORIZONTAL, 0.56f, 0.44f);
        break;
      case 3:
        addLine(0, Line.Direction.VERTICAL, 0.5f);
        addLine(1, Line.Direction.HORIZONTAL, 0.56f, 0.44f);
        break;
      case 4:
        addLine(0, Line.Direction.HORIZONTAL, 0.44f, 0.56f);
        addLine(0, Line.Direction.VERTICAL, 0.56f, 0.44f);
        break;
      case 5:
        addLine(0, Line.Direction.VERTICAL, 0.56f, 0.44f);
        addLine(1, Line.Direction.HORIZONTAL, 0.44f, 0.56f);
        break;
    }
  }
}
