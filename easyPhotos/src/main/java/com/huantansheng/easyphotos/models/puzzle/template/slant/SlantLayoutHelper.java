package com.huantansheng.easyphotos.models.puzzle.template.slant;



import com.huantansheng.easyphotos.models.puzzle.PuzzleLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wupanjie
 */
public class SlantLayoutHelper {
  private SlantLayoutHelper() {

  }

  public static List<PuzzleLayout> getAllThemeLayout(int pieceCount) {
    List<PuzzleLayout> puzzleLayouts = new ArrayList<>();
    switch (pieceCount) {
      case 1:
        for (int i = 0; i < 4; i++) {
          puzzleLayouts.add(new OneSlantLayout(i));
        }
        break;
      case 2:
        for (int i = 0; i < 2; i++) {
          puzzleLayouts.add(new TwoSlantLayout(i));
        }
        break;
      case 3:
        for (int i = 0; i < 6; i++) {
          puzzleLayouts.add(new ThreeSlantLayout(i));
        }
        break;
      //case 4:
      //  for (int i = 0; i < 8; i++) {
      //    puzzleLayouts.add(new FourStraightLayout(i));
      //  }
      //  break;
      //case 5:
      //  for (int i = 0; i < 17; i++) {
      //    puzzleLayouts.add(new FiveStraightLayout(i));
      //  }
      //  break;
      //case 6:
      //  for (int i = 0; i < 12; i++) {
      //    puzzleLayouts.add(new SixStraightLayout(i));
      //  }
      //  break;
      //case 7:
      //  for (int i = 0; i < 9; i++) {
      //    puzzleLayouts.add(new SevenStraightLayout(i));
      //  }
      //  break;
      //case 8:
      //  for (int i = 0; i < 11; i++) {
      //    puzzleLayouts.add(new EightStraightLayout(i));
      //  }
      //  break;
      //case 9:
      //  for (int i = 0; i < 8; i++) {
      //    puzzleLayouts.add(new NineStraightLayout(i));
      //  }
      //  break;
    }

    return puzzleLayouts;
  }
}
