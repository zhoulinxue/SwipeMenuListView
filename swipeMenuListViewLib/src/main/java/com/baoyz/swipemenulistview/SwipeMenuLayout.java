package com.baoyz.swipemenulistview;

import android.content.Context;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector.OnGestureListener;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * 
 * @author baoyz
 * @date 2014-8-23
 * 
 */
public class SwipeMenuLayout extends FrameLayout {

	// 我们自定义布局的 view的id;
	private static final int CONTENT_VIEW_ID = 1;
	// 滑动出来显示的linearlayout的Id;
	private static final int MENU_VIEW_ID = 2;
	// view关闭时的状态
	private static final int STATE_CLOSE = 0;
	// view打开时的状态
	private static final int STATE_OPEN = 1;
	// 滑动的方向
	private int mSwipeDirection;
	// 我们自己写在xml里的ItemView(即没有滑动时的显示在界面上的View)
	private View mContentView;
	// 当侧滑以后显示出来的那一部分xiew.
	private SwipeMenuView mMenuView;
	// 手指按下的位置
	private int mDownX;
	// 默认状态为关闭状态
	private int state = STATE_CLOSE;
	// 一脸迷茫的样子 据说是 GestureDetector类的替代者。
	// 好吧 新版手势识别。 用他来识别手势 可以获得回调监听。
	private GestureDetectorCompat mGestureDetector;
	// 手势回调监听。
	private OnGestureListener mGestureListener;
	// 是否是在 Fling
	private boolean isFling;
	// 最小Fling 距离
	private int MIN_FLING = dp2px(15);
	// 最大X方向速度
	private int MAX_VELOCITYX = -dp2px(500);
	// 滚动控制生成器(一BU脸JB迷DONG茫)
	private ScrollerCompat mOpenScroller;
	// 滚动控制生成器(一BU脸JB迷DONG茫)
	private ScrollerCompat mCloseScroller;
	// 滑动的基础距离
	private int mBaseX;
	// item 的position
	private int position;
	// 滚动规则插入器(比如先快后慢 或者匀速滚动之类的)
	private Interpolator mCloseInterpolator;
	// 滚动规则插入器
	private Interpolator mOpenInterpolator;
	// 是否可以 侧滑(一脸嫌弃 感觉有点多余)
	private boolean mSwipEnable = true;

	public SwipeMenuLayout(View contentView, SwipeMenuView menuView) {
		this(contentView, menuView, null, null);
	}

	public SwipeMenuLayout(View contentView, SwipeMenuView menuView,
			Interpolator closeInterpolator, Interpolator openInterpolator) {
		super(contentView.getContext());
		mCloseInterpolator = closeInterpolator;
		mOpenInterpolator = openInterpolator;
		mContentView = contentView;
		mMenuView = menuView;
		mMenuView.setLayout(this);
		init();
	}

	// private SwipeMenuLayout(Context context, AttributeSet attrs, int
	// defStyle) {
	// super(context, attrs, defStyle);
	// }

	private SwipeMenuLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	private SwipeMenuLayout(Context context) {
		super(context);
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
		mMenuView.setPosition(position);
	}

	public void setSwipeDirection(int swipeDirection) {
		mSwipeDirection = swipeDirection;
	}

	private void init() {
		setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		mGestureListener = new SimpleOnGestureListener() {
			@Override
			public boolean onDown(MotionEvent e) {
				isFling = false;
				return true;
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2,
					float velocityX, float velocityY) {
				// TODO
				if (Math.abs(e1.getX() - e2.getX()) > MIN_FLING
						&& velocityX < MAX_VELOCITYX) {
					isFling = true;
				}
				// Log.i("byz", MAX_VELOCITYX + ", velocityX = " + velocityX);
				return super.onFling(e1, e2, velocityX, velocityY);
			}
		};
		mGestureDetector = new GestureDetectorCompat(getContext(),
				mGestureListener);

		// mScroller = ScrollerCompat.create(getContext(), new
		// BounceInterpolator());
		if (mCloseInterpolator != null) {
			mCloseScroller = ScrollerCompat.create(getContext(),
					mCloseInterpolator);
		} else {
			mCloseScroller = ScrollerCompat.create(getContext());
		}
		if (mOpenInterpolator != null) {
			mOpenScroller = ScrollerCompat.create(getContext(),
					mOpenInterpolator);
		} else {
			mOpenScroller = ScrollerCompat.create(getContext());
		}

		LayoutParams contentParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		mContentView.setLayoutParams(contentParams);
		if (mContentView.getId() < 1) {
			mContentView.setId(CONTENT_VIEW_ID);
		}

		mMenuView.setId(MENU_VIEW_ID);
		mMenuView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		addView(mContentView);
		addView(mMenuView);

		// if (mContentView.getBackground() == null) {
		// mContentView.setBackgroundColor(Color.WHITE);
		// }

		// in android 2.x, MenuView height is MATCH_PARENT is not work.
		// getViewTreeObserver().addOnGlobalLayoutListener(
		// new OnGlobalLayoutListener() {
		// @Override
		// public void onGlobalLayout() {
		// setMenuHeight(mContentView.getHeight());
		// // getViewTreeObserver()
		// // .removeGlobalOnLayoutListener(this);
		// }
		// });

	}

	@Override
	protected void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	public boolean onSwipe(MotionEvent event) {
		// 用了手势管理器 去为isFling 赋值。
		mGestureDetector.onTouchEvent(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = (int) event.getX();
			isFling = false;
			break;
		case MotionEvent.ACTION_MOVE:
			// Log.i("byz", "downX = " + mDownX + ", moveX = " + event.getX());
			int dis = (int) (mDownX - event.getX());
			if (state == STATE_OPEN) {
				// 手势滑动的距离 在滑动时是无限调用这个方法 因此 是个累加 过程
				dis += mMenuView.getWidth() * mSwipeDirection;
				;
			}
			// 跟着手去移动VIEW
			swipe(dis);
			break;
		case MotionEvent.ACTION_UP:
			if ((isFling || Math.abs(mDownX - event.getX()) > (mMenuView
					.getWidth() / 2))
					&& Math.signum(mDownX - event.getX()) == mSwipeDirection) {
				// 当同一个方向滑动距离大于一般宽度时就 自动打开。isFling 在这里就体现价值了 要不会和swipe(dis) 方法冲突
				// open
				smoothOpenMenu();
			} else {
				// 否则 所有的触摸都直接关闭
				// close
				smoothCloseMenu();
				// 然而不消耗这次手势(一脸懵逼) 
				return false;
			}
			break;
		}
		//除了关闭的时候 其他时候都消耗本次手势(一脸懵逼);
		return true;
	}

	// 判断是否 是打开状态
	public boolean isOpen() {
		return state == STATE_OPEN;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}

	private void swipe(int dis) {
		// 不让滑动的item 直接return掉。
		if (!mSwipEnable) {
			return;
		}
		// java.lang.Math.signum(double d)
		// 如果参数大于零返回1.0，如果参数小于零返回-1，如果参数为0，则返回signum函数的参数为零。
		if (Math.signum(dis) != mSwipeDirection) {
			// 当滑动换方向的时候调用。
			dis = 0;
		} else if (Math.abs(dis) > mMenuView.getWidth()) {
			// 上层view滑动距离的大于按钮的总宽度,不在增加view的移动宽度。
			dis = mMenuView.getWidth() * mSwipeDirection;
		}
		// 移动布局。
		mContentView.layout(-dis, mContentView.getTop(),
				mContentView.getWidth() - dis, getMeasuredHeight());
		// 向左滑动
		if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
			mMenuView.layout(mContentView.getWidth() - dis, mMenuView.getTop(),
					mContentView.getWidth() + mMenuView.getWidth() - dis,
					mMenuView.getBottom());
		} else {
			// 向右滑动
			mMenuView.layout(-mMenuView.getWidth() - dis, mMenuView.getTop(),
					-dis, mMenuView.getBottom());
		}
	}

	@Override
	public void computeScroll() {
		// computeScroll在父控件执行drawChild时，会调用这个方法
		if (state == STATE_OPEN) {
			// 如果view 在打开状态 又需要 重新绘制child 那么移动布局
			if (mOpenScroller.computeScrollOffset()) {
				swipe(mOpenScroller.getCurrX() * mSwipeDirection);
				// 刷新view
				postInvalidate();
			}
		} else {
			// 如果view 在关闭状态 又需要 重新绘制child 那么移动布局 移动的距离肯定是 小于mBaseX
			// 最小距离的(汗，一脸懵逼);
			if (mCloseScroller.computeScrollOffset()) {
				// 刷新view
				swipe((mBaseX - mCloseScroller.getCurrX()) * mSwipeDirection);
				postInvalidate();
			}
		}
	}

	public void smoothCloseMenu() {
		state = STATE_CLOSE;
		if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
			mBaseX = -mContentView.getLeft();
			mCloseScroller.startScroll(0, 0, mMenuView.getWidth(), 0, 350);
		} else {
			mBaseX = mMenuView.getRight();
			mCloseScroller.startScroll(0, 0, mMenuView.getWidth(), 0, 350);
		}
		postInvalidate();
	}

	public void smoothOpenMenu() {
		if (!mSwipEnable) {
			return;
		}
		state = STATE_OPEN;
		if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
			mOpenScroller.startScroll(-mContentView.getLeft(), 0,
					mMenuView.getWidth(), 0, 350);
		} else {
			mOpenScroller.startScroll(mContentView.getLeft(), 0,
					mMenuView.getWidth(), 0, 350);
		}
		postInvalidate();
	}

	public void closeMenu() {
		if (mCloseScroller.computeScrollOffset()) {
			mCloseScroller.abortAnimation();
		}
		if (state == STATE_OPEN) {
			state = STATE_CLOSE;
			swipe(0);
		}
	}

	public void openMenu() {
		if (!mSwipEnable) {
			return;
		}
		if (state == STATE_CLOSE) {
			state = STATE_OPEN;
			swipe(mMenuView.getWidth() * mSwipeDirection);
		}
	}

	public View getContentView() {
		return mContentView;
	}

	public SwipeMenuView getMenuView() {
		return mMenuView;
	}

	private int dp2px(int dp) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
				getContext().getResources().getDisplayMetrics());
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMenuView.measure(MeasureSpec.makeMeasureSpec(0,
				MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(
				getMeasuredHeight(), MeasureSpec.EXACTLY));
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		mContentView.layout(0, 0, getMeasuredWidth(),
				mContentView.getMeasuredHeight());
		if (mSwipeDirection == SwipeMenuListView.DIRECTION_LEFT) {
			mMenuView.layout(getMeasuredWidth(), 0, getMeasuredWidth()
					+ mMenuView.getMeasuredWidth(),
					mContentView.getMeasuredHeight());
		} else {
			mMenuView.layout(-mMenuView.getMeasuredWidth(), 0, 0,
					mContentView.getMeasuredHeight());
		}
	}

	public void setMenuHeight(int measuredHeight) {
		Log.i("byz", "pos = " + position + ", height = " + measuredHeight);
		LayoutParams params = (LayoutParams) mMenuView.getLayoutParams();
		if (params.height != measuredHeight) {
			params.height = measuredHeight;
			mMenuView.setLayoutParams(mMenuView.getLayoutParams());
		}
	}

	public void setSwipEnable(boolean swipEnable) {
		mSwipEnable = swipEnable;
	}

	public boolean getSwipEnable() {
		return mSwipEnable;
	}
}
