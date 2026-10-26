package com.example.ehitedium;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;

public class DummyRenderView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private boolean showBox = true;
    private boolean showChams = true;
    private boolean showText = true;
    private boolean nightMode = false;
    private int chamsColor = Color.rgb(0, 255, 120);
    private int chamsAlpha = 145;

    public DummyRenderView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public void setShowBox(boolean value) { showBox = value; invalidate(); }
    public void setShowChams(boolean value) { showChams = value; invalidate(); }
    public void setShowText(boolean value) { showText = value; invalidate(); }
    public void setNightMode(boolean value) { nightMode = value; invalidate(); }
    public void setChamsColor(int value) { chamsColor = value; invalidate(); }
    public void setChamsAlpha(int value) { chamsAlpha = Math.max(0, Math.min(255, value)); invalidate(); }

    public boolean isShowBox() { return showBox; }
    public boolean isShowChams() { return showChams; }
    public boolean isShowText() { return showText; }
    public boolean isNightMode() { return nightMode; }
    public int getChamsColor() { return chamsColor; }
    public int getChamsAlpha() { return chamsAlpha; }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float w = getWidth();
        float h = getHeight();

        canvas.drawColor(nightMode ? Color.rgb(15, 20, 34) : Color.rgb(183, 218, 235));

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(nightMode ? Color.rgb(30, 38, 53) : Color.rgb(105, 150, 110));
        canvas.drawRect(0, h * 0.68f, w, h, paint);

        paint.setColor(nightMode ? Color.rgb(52, 60, 75) : Color.rgb(142, 160, 151));
        canvas.drawRect(w * 0.12f, h * 0.60f, w * 0.88f, h * 0.68f, paint);

        drawDummy(canvas, w * 0.33f, h * 0.50f, Math.min(w, h) * 0.20f, "TEST-01");
        drawDummy(canvas, w * 0.68f, h * 0.43f, Math.min(w, h) * 0.16f, "TEST-02");
    }

    private void drawDummy(Canvas canvas, float cx, float top, float size, String label) {
        float headR = size * 0.19f;
        float bodyTop = top + headR * 2.35f;
        float bodyBottom = top + size * 1.35f;
        float bodyHalf = size * 0.23f;
        float left = cx - bodyHalf;
        float right = cx + bodyHalf;
        float headCy = top + headR;
        float legBottom = bodyBottom + size * 0.48f;

        // Draw simple stylized test mannequin, not a game model.
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(40, 50, 64));
        canvas.drawCircle(cx, headCy, headR, paint);
        RectF torso = new RectF(left, bodyTop, right, bodyBottom);
        canvas.drawRoundRect(torso, size * 0.08f, size * 0.08f, paint);
        canvas.drawRoundRect(new RectF(left - size * 0.12f, bodyTop + size * 0.02f,
                left + size * 0.01f, bodyBottom - size * 0.02f), size * 0.05f, size * 0.05f, paint);
        canvas.drawRoundRect(new RectF(right - size * 0.01f, bodyTop + size * 0.02f,
                right + size * 0.12f, bodyBottom - size * 0.02f), size * 0.05f, size * 0.05f, paint);
        canvas.drawRoundRect(new RectF(cx - size * 0.20f, bodyBottom - size * 0.02f,
                cx - size * 0.04f, legBottom), size * 0.04f, size * 0.04f, paint);
        canvas.drawRoundRect(new RectF(cx + size * 0.04f, bodyBottom - size * 0.02f,
                cx + size * 0.20f, legBottom), size * 0.04f, size * 0.04f, paint);

        if (showChams) {
            paint.setColor((chamsAlpha << 24) | (chamsColor & 0x00FFFFFF));
            canvas.drawCircle(cx, headCy, headR * 0.92f, paint);
            canvas.drawRoundRect(torso, size * 0.08f, size * 0.08f, paint);
            canvas.drawRoundRect(new RectF(left - size * 0.12f, bodyTop + size * 0.02f,
                    left + size * 0.01f, bodyBottom - size * 0.02f), size * 0.05f, size * 0.05f, paint);
            canvas.drawRoundRect(new RectF(right - size * 0.01f, bodyTop + size * 0.02f,
                    right + size * 0.12f, bodyBottom - size * 0.02f), size * 0.05f, size * 0.05f, paint);
            canvas.drawRoundRect(new RectF(cx - size * 0.20f, bodyBottom - size * 0.02f,
                    cx - size * 0.04f, legBottom), size * 0.04f, size * 0.04f, paint);
            canvas.drawRoundRect(new RectF(cx + size * 0.04f, bodyBottom - size * 0.02f,
                    cx + size * 0.20f, legBottom), size * 0.04f, size * 0.04f, paint);
        }

        if (showBox) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(2f, size * 0.025f));
            paint.setColor(Color.WHITE);
            canvas.drawRect(cx - size * 0.40f, top - size * 0.10f,
                    cx + size * 0.40f, legBottom + size * 0.08f, paint);
            paint.setStyle(Paint.Style.FILL);
        }

        if (showText) {
            paint.setStyle(Paint.Style.FILL);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(Math.max(12f, size * 0.16f));
            paint.setColor(Color.WHITE);
            canvas.drawText(label, cx, top - size * 0.16f, paint);
        }
    }
}
