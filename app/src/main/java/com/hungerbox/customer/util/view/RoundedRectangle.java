package com.hungerbox.customer.util.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

import com.takusemba.spotlight.shape.Shape;

public class RoundedRectangle implements Shape {

  private float height;
  private float width;
  private float radius;

  public RoundedRectangle(float height, float width, float radius) {
    this.height = height;
    this.width = width;
    this.radius = radius;
  }

  @Override public void draw(Canvas canvas, PointF point, float value, Paint paint) {
    float halfWidth = width / 2 * value;
    float halfHeight = height / 2 * value;
//    float left = point.x - halfWidth;
    float left = point.x;
//    float top = point.y - halfHeight;
    float top = point.y;
//    float right = point.x + halfWidth;
    float right = point.x + width;
//    float bottom = point.y + halfHeight;
    float bottom = point.y + height;
    RectF rect = new RectF(left, top, right, bottom);
    canvas.drawRoundRect(rect, radius, radius, paint);
  }

  @Override
  public int getHeight() {
    return (int) height;
  }

  @Override
  public int getWidth() {
    return (int) width;
  }
}