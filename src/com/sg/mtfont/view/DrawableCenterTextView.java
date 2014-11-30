package com.sg.mtfont.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class DrawableCenterTextView extends TextView {

	public DrawableCenterTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	protected void onDraw(Canvas canvas) {
		int totalWidth = getWidth();
		float textWidth = getPaint().measureText(getText().toString());
		int drawablePadding = getCompoundDrawablePadding();
		Drawable[] drawables = getCompoundDrawables();
		int drawableWidth = 0;
		if (drawables[0] != null) {
			drawableWidth = getCompoundDrawables()[0].getIntrinsicWidth();
		}
		float paddingLeft = (totalWidth - (textWidth + drawablePadding + drawableWidth)) / 2.0f;
		setPadding((int) paddingLeft, 0, 0, 0);

		super.onDraw(canvas);
	}
}
