package com.huantansheng.easyphotos.utils.bitmap.face;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * 脸部信息
 * Created by huan on 2017/11/17.
 */

public class FaceInformation {

    //两眼距离的中心点
    public PointF midEyesPoint;

    //两眼眼球距离
    public float eyesDistance;

    //左眼矩形
    public RectF leftEsyRect;

    //右眼矩形
    public RectF rightEsyRect;

    //鼻子矩形
    public RectF noseRect;

    //嘴矩形
    public RectF mouthRect;

    //面部矩形
    public RectF faceRect;
}
