package com.faizmalkani.floatingactionbutton;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.view.ViewHelper;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;

public class Fab extends View {
	Context _context;
	Paint mButtonPaint, mDrawablePaint;
	Bitmap mBitmap;
	int mScreenHeight;
	float currentY;;
	boolean mHidden = false;
	private Display display;

	public Fab(Context context, AttributeSet attributeSet) {
		super(context, attributeSet);
		_context = context;
		init(Color.WHITE);
	}

	@SuppressLint("NewApi")
	public Fab(Context context) {
		super(context);
		_context = context;
		init(Color.WHITE);
	}

	public void setFabColor(int fabColor) {
		init(fabColor);
	}

	public void setFabDrawable(Drawable fabDrawable) {
		Drawable myDrawable = fabDrawable;
		mBitmap = ((BitmapDrawable) myDrawable).getBitmap();
		invalidate();
	}

	@SuppressLint("NewApi")
	public void init(int fabColor) {
		setWillNotDraw(false);
		try {
			this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
		} catch (NoSuchMethodError e2) {
			// http://stackoverflow.com/questions/16990588/setlayertype-substitute-for-android-2-3-3
			try {
				Method setLayerTypeMethod = this.getClass().getMethod(
						"setLayerType", new Class[] { int.class, Paint.class });
				if (setLayerTypeMethod != null)
					setLayerTypeMethod.invoke(this, new Object[] {
							LAYER_TYPE_SOFTWARE, null });
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
		}

		mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mButtonPaint.setColor(fabColor);
		mButtonPaint.setStyle(Paint.Style.FILL);
		mButtonPaint
				.setShadowLayer(10.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));
		mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		invalidate();

		WindowManager mWindowManager = (WindowManager) _context
				.getSystemService(Context.WINDOW_SERVICE);
		display = mWindowManager.getDefaultDisplay();
		Point size = getSize();
		mScreenHeight = size.y;
	}

	// http://stackoverflow.com/questions/10439033/getsize-not-supported-on-older-android-os-versions-getwidth-getheight-d
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	@SuppressWarnings("deprecation")
	protected Point getSize() {
		final Point point = new Point();

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			display.getSize(point);
		} else {
			point.x = display.getWidth();
			point.y = display.getHeight();
		}
		return point;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		setClickable(true);
		canvas.drawCircle(getWidth() / 2, getHeight() / 2,
				(float) (getWidth() / 2.6), mButtonPaint);
		canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2,
				(getHeight() - mBitmap.getHeight()) / 2, mDrawablePaint);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			ViewHelper.setAlpha(this, 1.0f);
		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			ViewHelper.setAlpha(this, 0.6f);
		}
		return super.onTouchEvent(event);
	}

	public int dpToPx(int dp) {
		DisplayMetrics displayMetrics = getContext().getResources()
				.getDisplayMetrics();
		int px = Math.round(dp
				* (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
		return px;
	}

	public void hideFab() {
		try {
			if (mHidden == false) {
				currentY = ViewHelper.getY(this);
				ObjectAnimator mHideAnimation = ObjectAnimator.ofFloat(this,
						"Y", mScreenHeight);
				mHideAnimation.setInterpolator(new AccelerateInterpolator());
				mHideAnimation.start();
			}
		} catch (Exception e) {
			currentY = ViewHelper.getY(this);
			Animation animation = new TranslateAnimation(0.0f, 0.0f, 0.0f,
					currentY);
			animation.setDuration(1000);
			animation.setFillAfter(true);
			startAnimation(animation);
			setVisibility(View.GONE);
		}
		mHidden = true;
	}

	public void showFab() {
		try {
			if (mHidden == true) {
				ObjectAnimator mShowAnimation = ObjectAnimator.ofFloat(this,
						"Y", currentY);
				mShowAnimation.setInterpolator(new DecelerateInterpolator());
				mShowAnimation.start();

			}
		} catch (Exception e) {
			setVisibility(View.VISIBLE);
			currentY = ViewHelper.getY(this);
			Animation animation = new TranslateAnimation(0.0f, 0.0f, currentY,
					0.0f);
			animation.setDuration(500);
			this.startAnimation(animation);
		}
		mHidden = false;
	}
}