package com.example.frontend_profilservice;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CurvedBottomView extends View {
    private Path mPath;
    private Paint mPaint;
    private int activeIndex = -1; // -1 means none selected
    
    // Configuration
    private int curveRadius;
    private int curveDepth;

    public CurvedBottomView(Context context) {
        super(context);
        init(context);
    }
    
    public CurvedBottomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }
    
    public CurvedBottomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }
    
    private void init(Context context) {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setColor(Color.parseColor("#DFDFDF")); // Matches user request
        mPaint.setAntiAlias(true);
        setBackgroundColor(Color.TRANSPARENT);

        // Convert dp to px
        float density = context.getResources().getDisplayMetrics().density;
        curveRadius = (int) (68 * density); // Slightly wider than button (48dp + padding)
        curveDepth = (int) (34 * density);  // Depth to accomodate circle
    }
    
    public void setActiveIndex(int index) {
        this.activeIndex = index;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int w = getWidth();
        int h = getHeight();
        
        float cornerRadius = getContext().getResources().getDisplayMetrics().density * 10;

        mPath.reset();
        
        if (activeIndex == -1) {
            // Rounded rectangle without scoop
            mPath.addRoundRect(0, 0, w, h, cornerRadius, cornerRadius, Path.Direction.CW);
            canvas.drawPath(mPath, mPaint);
            return;
        }

        // Start drawing path with rounded corners
        // Top-Left Corner
        mPath.moveTo(0, cornerRadius);
        mPath.quadTo(0, 0, cornerRadius, 0);
        
        // Calculate center of the active item
        int itemWidth = w / 3;
        int centerX = (itemWidth * activeIndex) + (itemWidth / 2);
        
        // Line to scoop start
        mPath.lineTo(centerX - curveRadius, 0);
        
        // Draw the curve (Scoop)
        // Left side of scoop
        mPath.cubicTo(
            centerX - curveRadius + (curveRadius / 2), 0,
            centerX - (curveRadius / 2), curveDepth,
            centerX, curveDepth
        );
        
        // Right side of scoop
        mPath.cubicTo(
            centerX + (curveRadius / 2), curveDepth,
            centerX + curveRadius - (curveRadius / 2), 0,
            centerX + curveRadius, 0
        );
        
        // Line to Top-Right Corner start
        mPath.lineTo(w - cornerRadius, 0);
        
        // Top-Right Corner
        mPath.quadTo(w, 0, w, cornerRadius);
        
        // Right side
        mPath.lineTo(w, h - cornerRadius);
        
        // Bottom-Right Corner
        mPath.quadTo(w, h, w - cornerRadius, h);
        
        // Bottom side
        mPath.lineTo(cornerRadius, h);
        
        // Bottom-Left Corner
        mPath.quadTo(0, h, 0, h - cornerRadius);
        
        // close
        mPath.close();
        
        canvas.drawPath(mPath, mPaint);
    }
}
