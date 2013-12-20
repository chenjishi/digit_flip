package com.chenjishi.digitflip;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chenjishi on 13-12-20.
 */
public class DigitFlipView extends View {
    //here you can control the speed of animation
    private final static int MAX_FLIP_COUNT = 80;
    private final static int DEFAULT_DIGIT_COUNT = 5;

    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Bitmap bitmap;
    private float[] offset;
    private float[] initY;

    private float digitW;
    private float digitH;

    private int count;
    private int timeCount;

    private boolean isDown = false;

    public DigitFlipView(Context context) {
        super(context);
        init();
    }

    public DigitFlipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        if (isInEditMode()) return;

        paint.setAntiAlias(true);
        paint.setColor(0xFF00FF00);
        paint.setTextSize(44.0f);
        paint.setTextAlign(Paint.Align.CENTER);

        digitW = paint.measureText("8");

        Rect rect = new Rect();
        paint.getTextBounds("8", 0, 1, rect);

        digitH = Math.abs(rect.top) + Math.abs(rect.bottom);
        //add some gap for digit
        digitH += 4.0f;

        bitmap = Bitmap.createBitmap((int) digitW, (int) (digitH * 10 * 3), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap);

        for (int i = 0; i < 30; i++) {
            int d;
            if (i < 10) {
                d = i;
            } else if (i < 20) {
                d = i - 10;
            } else {
                d = i - 20;
            }

            float x = digitW / 2.0f;
            float y = ((digitH / 2.0f) - (paint.descent() + paint.ascent()) / 2.0f) + digitH * i;
            canvas.drawText(d + "", x, y, paint);
        }
    }

    public void setNumber(int n1, int n2) {
        String num1 = String.valueOf(n1);
        String num2 = String.valueOf(n2);

        int len1 = num1.length();
        int len2 = num2.length();

        int len;
        int[] arr1;
        int[] arr2;
        if (len1 < len2) {
            len = Math.max(len1, len2);

            arr1 = new int[len];
            arr2 = new int[len];
            int diff = Math.max(len1, len2) - Math.min(len1, len2);
            for (int i = 0; i < len; i++) {
                arr1[i] = i < diff ? 0 : getInt(num1.charAt(i - diff));
                arr2[i] = getInt(num2.charAt(i));
            }
        } else if (len1 == len2) {
            len = len2;
            arr1 = new int[len];
            arr2 = new int[len];
            for (int i = 0; i < len; i++) {
                arr1[i] = getInt(num1.charAt(i));
                arr2[i] = getInt(num2.charAt(i));
            }
        } else {
            len = Math.min(len1, len2);
            arr1 = new int[len];
            arr2 = new int[len];

            int diff = Math.max(len1, len2) - Math.min(len1, len2);
            for (int i = 0; i < len; i++) {
                arr1[i] = getInt(num1.charAt(i + diff));
                arr2[i] = getInt(num2.charAt(i));
            }
        }

        count = len;
        offset = new float[len];
        initY = new float[len];
        timeCount = 0;

        for (int i = 0; i < len; i++) {
            int d1 = arr1[i];
            int d2 = arr2[i];

            initY[i] = -(10 + d1) * digitH;

            if (n1 > n2) {
                isDown = true;
                if (d1 < d2) {
                    offset[i] = (d1 + 1) * digitH + (9 - d2) * digitH;
                } else if (d1 == d2) {
                    offset[i] = 0;
                } else {
                    offset[i] = (d2 - d1) * digitH;
                }
            } else if (n1 == n2) {
                offset[i] = 0;
            } else {
                isDown = false;
                if (d1 < d2) {
                    offset[i] = (d2 - d1) * digitH;
                } else if (d1 == d2) {
                    offset[i] = 0;
                } else {
                    offset[i] = (9 - d1) * digitH + (d2 + 1) * digitH;
                }
            }
        }

        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        setLayoutParams(layoutParams);

        invalidate();
    }

    private int getInt(char c) {
        return Character.getNumericValue(c);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(0xFFFF0000);
        if (count == 0) return;

        for (int i = 0; i < count; i++) {
            float diff = Math.abs(offset[i]) / MAX_FLIP_COUNT;
            float x = i * digitW;
            float y = initY[i] + (isDown ? diff : -diff) * timeCount;
            canvas.drawBitmap(bitmap, x, y, paint);
        }

        timeCount++;
        if (timeCount <= MAX_FLIP_COUNT) {
            invalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
    }

    private int measureLong(int measureSpec) {
        int width;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (MeasureSpec.EXACTLY == specMode) {
            width = specSize;
        } else {
            width = (int) (getPaddingLeft() + getPaddingRight() + digitW * (count == 0 ? DEFAULT_DIGIT_COUNT : count));
            if (MeasureSpec.AT_MOST == specMode) {
                width = Math.min(width, specSize);
            }
        }

        return width;
    }

    private int measureShort(int measureSpec) {
        int height;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (MeasureSpec.EXACTLY == specMode) {
            height = specSize;
        } else {
            height = (int) (getPaddingTop() + getPaddingBottom() + digitH);
            if (MeasureSpec.AT_MOST == specMode) {
                height = Math.min(height, specSize);
            }
        }

        return height;
    }
}
