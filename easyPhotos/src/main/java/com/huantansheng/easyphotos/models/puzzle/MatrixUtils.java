package com.huantansheng.easyphotos.models.puzzle;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import java.util.Arrays;

import static java.lang.Math.round;

/**
 * some useful matrix operation methods
 *
 * @author wupanjie
 */
public class MatrixUtils {
  private MatrixUtils() {
    //no instance
  }

  private static final float[] sMatrixValues = new float[9];
  private static final Matrix sTempMatrix = new Matrix();

  /**
   * This method calculates scale value for given Matrix object.
   */
  public static float getMatrixScale(Matrix matrix) {
    return (float) Math.sqrt(Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X), 2) + Math.pow(
        getMatrixValue(matrix, Matrix.MSKEW_Y), 2));
  }

  /**
   * This method calculates rotation angle for given Matrix object.
   */
  public static float getMatrixAngle(Matrix matrix) {
    return (float) -(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X),
        getMatrixValue(matrix, Matrix.MSCALE_X)) * (180 / Math.PI));
  }

  public static float getMatrixValue(Matrix matrix, int valueIndex) {
    matrix.getValues(sMatrixValues);
    return sMatrixValues[valueIndex];
  }

  public static float getMinMatrixScale(PuzzlePiece piece) {
    if (piece != null) {

      sTempMatrix.reset();
      sTempMatrix.setRotate(-piece.getMatrixAngle());

      float[] unrotatedCropBoundsCorners = getCornersFromRect(piece.getArea().getAreaRect());

      sTempMatrix.mapPoints(unrotatedCropBoundsCorners);

      RectF unrotatedCropRect = trapToRect(unrotatedCropBoundsCorners);

      return Math.max(unrotatedCropRect.width() / piece.getWidth(),
          unrotatedCropRect.height() / piece.getHeight());
    }

    return 1f;
  }

  //判断剪裁框是否在图片内
  static boolean judgeIsImageContainsBorder(PuzzlePiece piece, float rotateDegrees) {
    sTempMatrix.reset();
    sTempMatrix.setRotate(-rotateDegrees);
    float[] unrotatedWrapperCorner = new float[8];
    float[] unrotateBorderCorner = new float[8];
    sTempMatrix.mapPoints(unrotatedWrapperCorner, piece.getCurrentDrawablePoints());
    sTempMatrix.mapPoints(unrotateBorderCorner, getCornersFromRect(piece.getArea().getAreaRect()));

    return trapToRect(unrotatedWrapperCorner).contains(trapToRect(unrotateBorderCorner));
  }

  static float[] calculateImageIndents(PuzzlePiece piece) {
    if (piece == null) return new float[] { 0, 0, 0, 0, 0, 0, 0, 0 };

    sTempMatrix.reset();
    sTempMatrix.setRotate(-piece.getMatrixAngle());

    final float[] currentImageCorners = piece.getCurrentDrawablePoints();
    final float[] unrotatedImageCorners =
        Arrays.copyOf(currentImageCorners, currentImageCorners.length);
    final float[] unrotatedCropBoundsCorners = getCornersFromRect(piece.getArea().getAreaRect());

    sTempMatrix.mapPoints(unrotatedImageCorners);
    sTempMatrix.mapPoints(unrotatedCropBoundsCorners);

    RectF unrotatedImageRect = trapToRect(unrotatedImageCorners);
    RectF unrotatedCropRect = trapToRect(unrotatedCropBoundsCorners);

    float deltaLeft = unrotatedImageRect.left - unrotatedCropRect.left;
    float deltaTop = unrotatedImageRect.top - unrotatedCropRect.top;
    float deltaRight = unrotatedImageRect.right - unrotatedCropRect.right;
    float deltaBottom = unrotatedImageRect.bottom - unrotatedCropRect.bottom;

    float indents[] = new float[4];

    indents[0] = (deltaLeft > 0) ? deltaLeft : 0;
    indents[1] = (deltaTop > 0) ? deltaTop : 0;
    indents[2] = (deltaRight < 0) ? deltaRight : 0;
    indents[3] = (deltaBottom < 0) ? deltaBottom : 0;

    sTempMatrix.reset();
    sTempMatrix.setRotate(piece.getMatrixAngle());
    sTempMatrix.mapPoints(indents);

    return indents;
  }

  //计算包含给出点的最小矩形
  public static RectF trapToRect(float[] array) {
    RectF r = new RectF(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
        Float.NEGATIVE_INFINITY);
    for (int i = 1; i < array.length; i += 2) {
      float x = round(array[i - 1] * 10) / 10.f;
      float y = round(array[i] * 10) / 10.f;
      r.left = (x < r.left) ? x : r.left;
      r.top = (y < r.top) ? y : r.top;
      r.right = (x > r.right) ? x : r.right;
      r.bottom = (y > r.bottom) ? y : r.bottom;
    }
    r.sort();
    return r;
  }

  public static float[] getCornersFromRect(RectF r) {
    return new float[] {
        r.left, r.top, r.right, r.top, r.right, r.bottom, r.left, r.bottom
    };
  }

  public static Matrix generateMatrix(PuzzlePiece piece, float extra) {
    return generateMatrix(piece.getArea(), piece.getDrawable(), extra);
  }

  public static Matrix generateMatrix(Area area, Drawable drawable, float extraSize) {
    return generateCenterCropMatrix(area, drawable.getIntrinsicWidth(),
        drawable.getIntrinsicHeight(), extraSize);
  }

  private static Matrix generateCenterCropMatrix(Area area, int width, int height,
                                                 float extraSize) {
    final RectF rectF = area.getAreaRect();

    Matrix matrix = new Matrix();

    float offsetX = rectF.centerX() - width / 2;
    float offsetY = rectF.centerY() - height / 2;

    matrix.postTranslate(offsetX, offsetY);

    float scale;

    if (width * rectF.height() > rectF.width() * height) {
      scale = (rectF.height() + extraSize) / height;
    } else {
      scale = (rectF.width() + extraSize) / width;
    }

    matrix.postScale(scale, scale, rectF.centerX(), rectF.centerY());

    return matrix;
  }
}
