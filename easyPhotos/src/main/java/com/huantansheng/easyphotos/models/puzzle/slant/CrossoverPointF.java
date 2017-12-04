package com.huantansheng.easyphotos.models.puzzle.slant;

import android.graphics.PointF;

/**
 * 两条线的交点
 *
 * @author wupanjie
 */
class CrossoverPointF extends PointF {
  SlantLine horizontal;
  SlantLine vertical;

  CrossoverPointF() {

  }

  CrossoverPointF(float x, float y) {
    this.x = x;
    this.y = y;
  }

  CrossoverPointF(SlantLine horizontal, SlantLine vertical) {
    this.horizontal = horizontal;
    this.vertical = vertical;
  }

  void update() {
    if (horizontal == null || vertical == null){
      return;
    }
    SlantUtils.intersectionOfLines(this, horizontal, vertical);
  }
}
