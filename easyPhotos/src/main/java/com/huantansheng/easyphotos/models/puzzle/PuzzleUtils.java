package com.huantansheng.easyphotos.models.puzzle;


import com.huantansheng.easyphotos.models.puzzle.template.slant.OneSlantLayout;
import com.huantansheng.easyphotos.models.puzzle.template.slant.SlantLayoutHelper;
import com.huantansheng.easyphotos.models.puzzle.template.slant.ThreeSlantLayout;
import com.huantansheng.easyphotos.models.puzzle.template.slant.TwoSlantLayout;
import com.huantansheng.easyphotos.models.puzzle.template.straight.EightStraightLayout;
import com.huantansheng.easyphotos.models.puzzle.template.straight.FiveStraightLayout;
import com.huantansheng.easyphotos.models.puzzle.template.straight.FourStraightLayout;
import com.huantansheng.easyphotos.models.puzzle.template.straight.NineStraightLayout;
import com.huantansheng.easyphotos.models.puzzle.template.straight.OneStraightLayout;
import com.huantansheng.easyphotos.models.puzzle.template.straight.SevenStraightLayout;
import com.huantansheng.easyphotos.models.puzzle.template.straight.SixStraightLayout;
import com.huantansheng.easyphotos.models.puzzle.template.straight.StraightLayoutHelper;
import com.huantansheng.easyphotos.models.puzzle.template.straight.ThreeStraightLayout;
import com.huantansheng.easyphotos.models.puzzle.template.straight.TwoStraightLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wupanjie
 */
public class PuzzleUtils {
  private static final String TAG = "PuzzleUtils";

  private PuzzleUtils() {
    //no instance
  }

  public static PuzzleLayout getPuzzleLayout(int type, int borderSize, int themeId) {
    if (type == 0) {
      switch (borderSize) {
        case 1:
          return new OneSlantLayout(themeId);
        case 2:
          return new TwoSlantLayout(themeId);
        case 3:
          return new ThreeSlantLayout(themeId);
        default:
          return new OneSlantLayout(themeId);
      }
    } else {
      switch (borderSize) {
        case 1:
          return new OneStraightLayout(themeId);
        case 2:
          return new TwoStraightLayout(themeId);
        case 3:
          return new ThreeStraightLayout(themeId);
        case 4:
          return new FourStraightLayout(themeId);
        case 5:
          return new FiveStraightLayout(themeId);
        case 6:
          return new SixStraightLayout(themeId);
        case 7:
          return new SevenStraightLayout(themeId);
        case 8:
          return new EightStraightLayout(themeId);
        case 9:
          return new NineStraightLayout(themeId);
        default:
          return new OneStraightLayout(themeId);
      }
    }
  }

  public static List<PuzzleLayout> getAllPuzzleLayouts() {
    List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
    //slant layout
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(2));
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(3));

    // straight layout
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(2));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(3));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(4));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(5));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(6));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(7));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(8));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(9));
    return puzzleLayouts;
  }

  public static List<PuzzleLayout> getPuzzleLayouts(int pieceCount) {
    List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
    puzzleLayouts.addAll(SlantLayoutHelper.getAllThemeLayout(pieceCount));
    puzzleLayouts.addAll(StraightLayoutHelper.getAllThemeLayout(pieceCount));
    return puzzleLayouts;
  }
}
