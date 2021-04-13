package wei.mark.standout;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import wei.mark.standout.constants.StandOutFlags;
import wei.mark.standout.ui.Window;

public abstract class StandOutWindow extends Service {
	static final String TAG = "StandOutWindow";

	public static final int DEFAULT_ID = 0;

	public static final int ONGOING_NOTIFICATION_ID = -1;

	public static final int DISREGARD_ID = -2;

	public static final String ACTION_SHOW = "SHOW";

	public static final String ACTION_RESTORE = "RESTORE";

	public static final String ACTION_CLOSE = "CLOSE";

	public static final String ACTION_CLOSE_ALL = "CLOSE_ALL";

	public static final String ACTION_SEND_DATA = "SEND_DATA";

	public static final String ACTION_HIDE = "HIDE";

	public static void show(Context context,
			Class<? extends StandOutWindow> cls, int id) {
		context.startService(getShowIntent(context, cls, id));
	}

	public static void hide(Context context,
			Class<? extends StandOutWindow> cls, int id) {
		context.startService(getHideIntent(context, cls, id));
	}

	public static void close(Context context,
			Class<? extends StandOutWindow> cls, int id) {
		context.startService(getCloseIntent(context, cls, id));
	}

	public static void closeAll(Context context,
			Class<? extends StandOutWindow> cls) {
		context.startService(getCloseAllIntent(context, cls));
	}

	public static void sendData(Context context,
			Class<? extends StandOutWindow> toCls, int toId, int requestCode,
			Bundle data, Class<? extends StandOutWindow> fromCls, int fromId) {
		context.startService(getSendDataIntent(context, toCls, toId,
				requestCode, data, fromCls, fromId));
	}

	public static Intent getShowIntent(Context context,
			Class<? extends StandOutWindow> cls, int id) {
		boolean cached = sWindowCache.isCached(id, cls);
		String action = cached ? ACTION_RESTORE : ACTION_SHOW;
		Uri uri = cached ? Uri.parse("standout://" + cls + '/' + id) : null;
		return new Intent(context, cls).putExtra("id", id).setAction(action)
				.setData(uri);
	}

	public static Intent getHideIntent(Context context,
			Class<? extends StandOutWindow> cls, int id) {
		return new Intent(context, cls).putExtra("id", id).setAction(
				ACTION_HIDE);
	}

	public static Intent getCloseIntent(Context context,
			Class<? extends StandOutWindow> cls, int id) {
		return new Intent(context, cls).putExtra("id", id).setAction(
				ACTION_CLOSE);
	}

	public static Intent getCloseAllIntent(Context context,
			Class<? extends StandOutWindow> cls) {
		return new Intent(context, cls).setAction(ACTION_CLOSE_ALL);
	}

	public static Intent getSendDataIntent(Context context,
			Class<? extends StandOutWindow> toCls, int toId, int requestCode,
			Bundle data, Class<? extends StandOutWindow> fromCls, int fromId) {
		return new Intent(context, toCls).putExtra("id", toId)
				.putExtra("requestCode", requestCode)
				.putExtra("wei.mark.standout.data", data)
				.putExtra("wei.mark.standout.fromCls", fromCls)
				.putExtra("fromId", fromId).setAction(ACTION_SEND_DATA);
	}

	// internal map of ids to shown/hidden views
	static WindowCache sWindowCache;
	static Window sFocusedWindow;

	// static constructors
	static {
		sWindowCache = new WindowCache();
		sFocusedWindow = null;
	}

	// internal system services
	WindowManager mWindowManager;
	private NotificationManager mNotificationManager;
	LayoutInflater mLayoutInflater;

	// internal state variables
	private boolean startedForeground;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mLayoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		startedForeground = false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// intent should be created with
		// getShowIntent(), getHideIntent(), getCloseIntent()
		if (intent != null) {
			String action = intent.getAction();
			int id = intent.getIntExtra("id", DEFAULT_ID);

			// this will interfere with getPersistentNotification()
			if (id == ONGOING_NOTIFICATION_ID) {
				throw new RuntimeException(
						"ID cannot equals StandOutWindow.ONGOING_NOTIFICATION_ID");
			}

			if (ACTION_SHOW.equals(action) || ACTION_RESTORE.equals(action)) {
				show(id);
			} else if (ACTION_HIDE.equals(action)) {
				hide(id);
			} else if (ACTION_CLOSE.equals(action)) {
				close(id);
			} else if (ACTION_CLOSE_ALL.equals(action)) {
				closeAll();
			} else if (ACTION_SEND_DATA.equals(action)) {
				if (!isExistingId(id) && id != DISREGARD_ID) {
					Log.w(TAG,
							"Sending data to non-existant window. If this is not intended, make sure toId is either an existing window's id or DISREGARD_ID.");
				}
				Bundle data = intent.getBundleExtra("wei.mark.standout.data");
				int requestCode = intent.getIntExtra("requestCode", 0);
				@SuppressWarnings("unchecked")
				Class<? extends StandOutWindow> fromCls = (Class<? extends StandOutWindow>) intent
						.getSerializableExtra("wei.mark.standout.fromCls");
				int fromId = intent.getIntExtra("fromId", DEFAULT_ID);
				onReceiveData(id, requestCode, data, fromCls, fromId);
			}
		} else {
			Log.w(TAG, "Tried to onStartCommand() with a null intent.");
		}

		// the service is started in foreground in show()
		// so we don't expect Android to kill this service
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		// closes all windows
		closeAll();
	}

	public abstract String getAppName();

	public abstract int getAppIcon();

	public abstract void createAndAttachView(int id, FrameLayout frame);

	public abstract StandOutLayoutParams getParams(int id, Window window);

	public int getFlags(int id) {
		return 0;
	}

	public String getTitle(int id) {
		return getAppName();
	}

	public int getIcon(int id) {
		return getAppIcon();
	}

	public String getPersistentNotificationTitle(int id) {
		return getAppName();
	}

	public String getPersistentNotificationMessage(int id) {
		return "";
	}

	public Intent getPersistentNotificationIntent(int id) {
		return null;
	}

	public int getHiddenIcon() {
		return getAppIcon();
	}

	public String getHiddenNotificationTitle(int id) {
		return getAppName() + " Hidden";
	}

	public String getHiddenNotificationMessage(int id) {
		return "";
	}

	public Intent getHiddenNotificationIntent(int id) {
		return null;
	}

	public Notification getPersistentNotification(int id) {
		// basic notification stuff
		// http://developer.android.com/guide/topics/ui/notifiers/notifications.html
		int icon = getAppIcon();
		long when = System.currentTimeMillis();
		Context c = getApplicationContext();
		String contentTitle = getPersistentNotificationTitle(id);
		String tickerText = getPersistentNotificationMessage(id);

		// getPersistentNotification() is called for every new window
		// so we replace the old notification with a new one that has
		// a bigger id
		Intent notificationIntent = getPersistentNotificationIntent(id);

		PendingIntent contentIntent = null;

		if (notificationIntent != null) {
			contentIntent = PendingIntent.getService(this, 0,
					notificationIntent,
					// flag updates existing persistent notification
					PendingIntent.FLAG_UPDATE_CURRENT);
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
		builder.setContentTitle(contentTitle);
		builder.setContentText(tickerText);
		builder.setContentIntent(contentIntent);
		builder.setSmallIcon(icon);
		builder.setWhen(when);
		return builder.build();
	}

	public Notification getHiddenNotification(int id) {
		// same basics as getPersistentNotification()
		int icon = getHiddenIcon();
		long when = System.currentTimeMillis();
		Context c = getApplicationContext();
		String contentTitle = getHiddenNotificationTitle(id);
		String contentText = getHiddenNotificationMessage(id);
		String tickerText = String.format("%s: %s", contentTitle, contentText);

		// the difference here is we are providing the same id
		Intent notificationIntent = getHiddenNotificationIntent(id);

		PendingIntent contentIntent = null;

		if (notificationIntent != null) {
			contentIntent = PendingIntent.getService(this, 0,
					notificationIntent,
					// flag updates existing persistent notification
					PendingIntent.FLAG_UPDATE_CURRENT);
		}

		NotificationCompat.Builder builder = new NotificationCompat.Builder(c);
		builder.setContentTitle(contentTitle);
		builder.setContentText(tickerText);
		builder.setContentIntent(contentIntent);
		builder.setSmallIcon(icon);
		builder.setWhen(when);
		return builder.build();
	}

	public Animation getShowAnimation(int id) {
		return AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
	}

	public Animation getHideAnimation(int id) {
		return AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
	}

	public Animation getCloseAnimation(int id) {
		return AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
	}

	public int getThemeStyle() {
		return 0;
	}

	public PopupWindow getDropDown(final int id) {
		final List<DropDownListItem> items;

		List<DropDownListItem> dropDownListItems = getDropDownItems(id);
		if (dropDownListItems != null) {
			items = dropDownListItems;
		} else {
			items = new ArrayList<StandOutWindow.DropDownListItem>();
		}

		// add default drop down items
		items.add(new DropDownListItem(
				android.R.drawable.ic_menu_close_clear_cancel, "Quit "
				+ getAppName(), new Runnable() {

			@Override
			public void run() {
				closeAll();
			}
		}));

		// turn item list into views in PopupWindow
		LinearLayout list = new LinearLayout(this);
		list.setOrientation(LinearLayout.VERTICAL);

		final PopupWindow dropDown = new PopupWindow(list,
				StandOutLayoutParams.WRAP_CONTENT,
				StandOutLayoutParams.WRAP_CONTENT, true);

		for (final DropDownListItem item : items) {
			ViewGroup listItem = (ViewGroup) mLayoutInflater.inflate(
					R.layout.drop_down_list_item, null);
			list.addView(listItem);

			ImageView icon = (ImageView) listItem.findViewById(R.id.icon);
			icon.setImageResource(item.icon);

			TextView description = (TextView) listItem
					.findViewById(R.id.description);
			description.setText(item.description);

			listItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					item.action.run();
					dropDown.dismiss();
				}
			});
		}

		Drawable background = getResources().getDrawable(
				android.R.drawable.editbox_dropdown_dark_frame);
		dropDown.setBackgroundDrawable(background);
		return dropDown;
	}

	public List<DropDownListItem> getDropDownItems(int id) {
		return null;
	}

	public boolean onTouchBody(int id, Window window, View view,
			MotionEvent event) {
		return false;
	}

	public void onMove(int id, Window window, View view, MotionEvent event) {
	}

	public void onResize(int id, Window window, View view, MotionEvent event) {
	}

	public boolean onShow(int id, Window window) {
		return false;
	}

	public boolean onHide(int id, Window window) {
		return false;
	}

	public boolean onClose(int id, Window window) {
		return false;
	}

	public boolean onCloseAll() {
		return false;
	}

	public void onReceiveData(int id, int requestCode, Bundle data,
			Class<? extends StandOutWindow> fromCls, int fromId) {
	}

	public boolean onUpdate(int id, Window window, StandOutLayoutParams params) {
		return false;
	}

	public boolean onBringToFront(int id, Window window) {
		return false;
	}

	public boolean onFocusChange(int id, Window window, boolean focus) {
		return false;
	}

	public boolean onKeyEvent(int id, Window window, KeyEvent event) {
		return false;
	}

	public final synchronized Window show(int id) {
		// get the window corresponding to the id
		Window cachedWindow = getWindow(id);
		final Window window;

		// check cache first
		if (cachedWindow != null) {
			window = cachedWindow;
		} else {
			window = new Window(this, id);
		}

		// alert callbacks and cancel if instructed
		if (onShow(id, window)) {
			Log.d(TAG, "Window " + id + " show cancelled by implementation.");
			return null;
		}

		// focus an already shown window
		if (window.visibility == Window.VISIBILITY_VISIBLE) {
			Log.d(TAG, "Window " + id + " is already shown.");
			focus(id);
			return window;
		}

		window.visibility = Window.VISIBILITY_VISIBLE;

		// get animation
		Animation animation = getShowAnimation(id);

		// get the params corresponding to the id
		StandOutLayoutParams params = window.getLayoutParams();

		try {
			// add the view to the window manager
			mWindowManager.addView(window, params);

			// animate
			if (animation != null) {
				window.getChildAt(0).startAnimation(animation);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// add view to internal map
		sWindowCache.putCache(id, getClass(), window);

		// get the persistent notification
		Notification notification = getPersistentNotification(id);

		// show the notification
		if (notification != null) {
			notification.flags = notification.flags
					| Notification.FLAG_NO_CLEAR;

			// only show notification if not shown before
			if (!startedForeground) {
				// tell Android system to show notification
				startForeground(
						getClass().hashCode() + ONGOING_NOTIFICATION_ID,
						notification);
				startedForeground = true;
			} else {
				// update notification if shown before
				mNotificationManager.notify(getClass().hashCode()
						+ ONGOING_NOTIFICATION_ID, notification);
			}
		} else {
			// notification can only be null if it was provided before
			if (!startedForeground) {
				throw new RuntimeException("Your StandOutWindow service must"
						+ "provide a persistent notification."
						+ "The notification prevents Android"
						+ "from killing your service in low"
						+ "memory situations.");
			}
		}

		focus(id);

		return window;
	}

	public final synchronized void hide(int id) {
		// get the view corresponding to the id
		final Window window = getWindow(id);

		if (window == null) {
			throw new IllegalArgumentException("Tried to hide(" + id
					+ ") a null window.");
		}

		// alert callbacks and cancel if instructed
		if (onHide(id, window)) {
			Log.d(TAG, "Window " + id + " hide cancelled by implementation.");
			return;
		}

		// ignore if window is already hidden
		if (window.visibility == Window.VISIBILITY_GONE) {
			Log.d(TAG, "Window " + id + " is already hidden.");
		}

		// check if hide enabled
		if (Utils.isSet(window.flags, StandOutFlags.FLAG_WINDOW_HIDE_ENABLE)) {
			window.visibility = Window.VISIBILITY_TRANSITION;

			// get the hidden notification for this view
//			Notification notification = getHiddenNotification(id);

			// get animation
			Animation animation = getHideAnimation(id);

			try {
				// animate
				if (animation != null) {
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							// remove the window from the window manager
							mWindowManager.removeView(window);
							window.visibility = Window.VISIBILITY_GONE;
						}
					});
					window.getChildAt(0).startAnimation(animation);
				} else {
					// remove the window from the window manager
					mWindowManager.removeView(window);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// display the notification
//			notification.flags = notification.flags
//					| Notification.FLAG_NO_CLEAR
//					| Notification.FLAG_AUTO_CANCEL;
//
//			mNotificationManager.notify(getClass().hashCode() + id,
//					notification);

		} else {
			// if hide not enabled, close window
			close(id);
		}
	}

	public final synchronized void close(final int id) {
		// get the view corresponding to the id
		final Window window = getWindow(id);

		if (window == null) {
			throw new IllegalArgumentException("Tried to close(" + id
					+ ") a null window.");
		}

		if (window.visibility == Window.VISIBILITY_TRANSITION) {
			return;
		}

		// alert callbacks and cancel if instructed
		if (onClose(id, window)) {
			Log.w(TAG, "Window " + id + " close cancelled by implementation.");
			return;
		}

		// remove hidden notification
		mNotificationManager.cancel(getClass().hashCode() + id);

		unfocus(window);

		window.visibility = Window.VISIBILITY_TRANSITION;

		// get animation
		Animation animation = getCloseAnimation(id);

		// remove window
		try {
			// animate
			if (animation != null) {
				animation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// remove the window from the window manager
						mWindowManager.removeView(window);
						window.visibility = Window.VISIBILITY_GONE;

						// remove view from internal map
						sWindowCache.removeCache(id,
								StandOutWindow.this.getClass());

						// if we just released the last window, quit
						if (getExistingIds().size() == 0) {
							// tell Android to remove the persistent
							// notification
							// the Service will be shutdown by the system on low
							// memory
							startedForeground = false;
							stopForeground(true);
						}
					}
				});
				window.getChildAt(0).startAnimation(animation);
			} else {
				// remove the window from the window manager
				mWindowManager.removeView(window);

				// remove view from internal map
				sWindowCache.removeCache(id, getClass());

				// if we just released the last window, quit
				if (sWindowCache.getCacheSize(getClass()) == 0) {
					// tell Android to remove the persistent notification
					// the Service will be shutdown by the system on low memory
					startedForeground = false;
					stopForeground(true);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final synchronized void closeAll() {
		// alert callbacks and cancel if instructed
		if (onCloseAll()) {
			Log.w(TAG, "Windows close all cancelled by implementation.");
			return;
		}

		// add ids to temporary set to avoid concurrent modification
		LinkedList<Integer> ids = new LinkedList<Integer>();
		for (int id : getExistingIds()) {
			ids.add(id);
		}

		// close each window
		for (int id : ids) {
			close(id);
		}
	}

	public final void sendData(int fromId,
			Class<? extends StandOutWindow> toCls, int toId, int requestCode,
			Bundle data) {
		StandOutWindow.sendData(this, toCls, toId, requestCode, data,
				getClass(), fromId);
	}

	public final synchronized void bringToFront(int id) {
		Window window = getWindow(id);
		if (window == null) {
			throw new IllegalArgumentException("Tried to bringToFront(" + id
					+ ") a null window.");
		}

		if (window.visibility == Window.VISIBILITY_GONE) {
			throw new IllegalStateException("Tried to bringToFront(" + id
					+ ") a window that is not shown.");
		}

		if (window.visibility == Window.VISIBILITY_TRANSITION) {
			return;
		}

		// alert callbacks and cancel if instructed
		if (onBringToFront(id, window)) {
			Log.w(TAG, "Window " + id
					+ " bring to front cancelled by implementation.");
			return;
		}

		StandOutLayoutParams params = window.getLayoutParams();

		// remove from window manager then add back
		try {
			mWindowManager.removeView(window);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		try {
			mWindowManager.addView(window, params);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public final synchronized boolean focus(int id) {
		// check if that window is focusable
		final Window window = getWindow(id);
		if (window == null) {
			throw new IllegalArgumentException("Tried to focus(" + id
					+ ") a null window.");
		}

		if (!Utils.isSet(window.flags,
				StandOutFlags.FLAG_WINDOW_FOCUSABLE_DISABLE)) {
			// remove focus from previously focused window
			if (sFocusedWindow != null) {
				unfocus(sFocusedWindow);
			}

			return window.onFocus(true);
		}

		return false;
	}

	public final synchronized boolean unfocus(int id) {
		Window window = getWindow(id);
		return unfocus(window);
	}

	public final int getUniqueId() {
		int unique = DEFAULT_ID;
		for (int id : getExistingIds()) {
			unique = Math.max(unique, id + 1);
		}
		return unique;
	}

	public final boolean isExistingId(int id) {
		return sWindowCache.isCached(id, getClass());
	}

	public final Set<Integer> getExistingIds() {
		return sWindowCache.getCacheIds(getClass());
	}

	public final Window getWindow(int id) {
		return sWindowCache.getCache(id, getClass());
	}

	public final Window getFocusedWindow() {
		return sFocusedWindow;
	}

	public final void setFocusedWindow(Window window) {
		sFocusedWindow = window;
	}

	public final void setTitle(int id, String text) {
		Window window = getWindow(id);
		if (window != null) {
			View title = window.findViewById(R.id.title);
			if (title instanceof TextView) {
				((TextView) title).setText(text);
			}
		}
	}

	public final void setIcon(int id, int drawableRes) {
		Window window = getWindow(id);
		if (window != null) {
			View icon = window.findViewById(R.id.window_icon);
			if (icon instanceof ImageView) {
				((ImageView) icon).setImageResource(drawableRes);
			}
		}
	}

	public boolean onTouchHandleMove(int id, Window window, View view,
			MotionEvent event) {
		StandOutLayoutParams params = window.getLayoutParams();

		// how much you have to move in either direction in order for the
		// gesture to be a move and not tap

		int totalDeltaX = window.touchInfo.lastX - window.touchInfo.firstX;
		int totalDeltaY = window.touchInfo.lastY - window.touchInfo.firstY;

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				window.touchInfo.lastX = (int) event.getRawX();
				window.touchInfo.lastY = (int) event.getRawY();

				window.touchInfo.firstX = window.touchInfo.lastX;
				window.touchInfo.firstY = window.touchInfo.lastY;
				break;
			case MotionEvent.ACTION_MOVE:
				int deltaX = (int) event.getRawX() - window.touchInfo.lastX;
				int deltaY = (int) event.getRawY() - window.touchInfo.lastY;

				window.touchInfo.lastX = (int) event.getRawX();
				window.touchInfo.lastY = (int) event.getRawY();

				if (window.touchInfo.moving
						|| Math.abs(totalDeltaX) >= params.threshold
						|| Math.abs(totalDeltaY) >= params.threshold) {
					window.touchInfo.moving = true;

					// if window is moveable
					if (Utils.isSet(window.flags,
							StandOutFlags.FLAG_BODY_MOVE_ENABLE)) {

						// update the position of the window
						if (event.getPointerCount() == 1) {
							params.x += deltaX;
							params.y += deltaY;
						}

						window.edit().setPosition(params.x, params.y).commit();
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				window.touchInfo.moving = false;

				if (event.getPointerCount() == 1) {

					// bring to front on tap
					boolean tap = Math.abs(totalDeltaX) < params.threshold
							&& Math.abs(totalDeltaY) < params.threshold;
					if (tap
							&& Utils.isSet(
									window.flags,
									StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TAP)) {
						StandOutWindow.this.bringToFront(id);
					}
				}

				// bring to front on touch
				else if (Utils.isSet(window.flags,
						StandOutFlags.FLAG_WINDOW_BRING_TO_FRONT_ON_TOUCH)) {
					StandOutWindow.this.bringToFront(id);
				}

				break;
		}

		onMove(id, window, view, event);

		return true;
	}

	public boolean onTouchHandleResize(int id, Window window, View view,
			MotionEvent event) {
		StandOutLayoutParams params = window
				.getLayoutParams();

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				window.touchInfo.lastX = (int) event.getRawX();
				window.touchInfo.lastY = (int) event.getRawY();

				window.touchInfo.firstX = window.touchInfo.lastX;
				window.touchInfo.firstY = window.touchInfo.lastY;
				break;
			case MotionEvent.ACTION_MOVE:
				int deltaX = (int) event.getRawX() - window.touchInfo.lastX;
				int deltaY = (int) event.getRawY() - window.touchInfo.lastY;

				// update the size of the window
				params.width += deltaX;
				params.height += deltaY;

				// keep window between min/max width/height
				if (params.width >= params.minWidth
						&& params.width <= params.maxWidth) {
					window.touchInfo.lastX = (int) event.getRawX();
				}

				if (params.height >= params.minHeight
						&& params.height <= params.maxHeight) {
					window.touchInfo.lastY = (int) event.getRawY();
				}

				window.edit().setSize(params.width, params.height).commit();
				break;
			case MotionEvent.ACTION_UP:
				break;
		}

		onResize(id, window, view, event);

		return true;
	}

	public synchronized boolean unfocus(Window window) {
		if (window == null) {
			throw new IllegalArgumentException(
					"Tried to unfocus a null window.");
		}
		return window.onFocus(false);
	}

	public void updateViewLayout(int id, StandOutLayoutParams params) {
		Window window = getWindow(id);

		if (window == null) {
			throw new IllegalArgumentException("Tried to updateViewLayout("
					+ id + ") a null window.");
		}

		if (window.visibility == Window.VISIBILITY_GONE) {
			return;
		}

		if (window.visibility == Window.VISIBILITY_TRANSITION) {
			return;
		}

		// alert callbacks and cancel if instructed
		if (onUpdate(id, window, params)) {
			Log.w(TAG, "Window " + id + " update cancelled by implementation.");
			return;
		}

		try {
			window.setLayoutParams(params);
			mWindowManager.updateViewLayout(window, params);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * LayoutParams specific to floating StandOut windows.
	 * 
	 * @author Mark Wei <markwei@gmail.com>
	 * 
	 */
	public class StandOutLayoutParams extends WindowManager.LayoutParams {
		/**
		 * Special value for x position that represents the left of the screen.
		 */
		public static final int LEFT = 0;
		/**
		 * Special value for y position that represents the top of the screen.
		 */
		public static final int TOP = 0;
		/**
		 * Special value for x position that represents the right of the screen.
		 */
		public static final int RIGHT = Integer.MAX_VALUE;
		/**
		 * Special value for y position that represents the bottom of the
		 * screen.
		 */
		public static final int BOTTOM = Integer.MAX_VALUE;
		/**
		 * Special value for x or y position that represents the center of the
		 * screen.
		 */
		public static final int CENTER = Integer.MIN_VALUE;
		/**
		 * Special value for x or y position which requests that the system
		 * determine the position.
		 */
		public static final int AUTO_POSITION = Integer.MIN_VALUE + 1;

		/**
		 * The distance that distinguishes a tap from a drag.
		 */
		public int threshold;

		/**
		 * Optional constraints of the window.
		 */
		public int minWidth, minHeight, maxWidth, maxHeight;

		/**
		 * @param id
		 *            The id of the window.
		 */
		public StandOutLayoutParams(int id) {
			super(200, 200, TYPE_PHONE,
					StandOutLayoutParams.FLAG_NOT_TOUCH_MODAL
							| StandOutLayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
					PixelFormat.TRANSLUCENT);

			int windowFlags = getFlags(id);

			setFocusFlag(false);

			if (!Utils.isSet(windowFlags,
					StandOutFlags.FLAG_WINDOW_EDGE_LIMITS_ENABLE)) {
				// windows may be moved beyond edges
				flags |= FLAG_LAYOUT_NO_LIMITS;
			}

			x = getX(id, width);
			y = getY(id, height);

			gravity = Gravity.TOP | Gravity.LEFT;

			threshold = 10;
			minWidth = minHeight = 0;
			maxWidth = maxHeight = Integer.MAX_VALUE;
		}

		public StandOutLayoutParams(int id, int w, int h) {
			this(id);
			width = w;
			height = h;
		}

		public StandOutLayoutParams(int id, int w, int h, int xpos, int ypos) {
			this(id, w, h);

			if (xpos != AUTO_POSITION) {
				x = xpos;
			}
			if (ypos != AUTO_POSITION) {
				y = ypos;
			}

			Display display = mWindowManager.getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();

			if (x == RIGHT) {
				x = width - w;
			} else if (x == CENTER) {
				x = (width - w) / 2;
			}

			if (y == BOTTOM) {
				y = height - h;
			} else if (y == CENTER) {
				y = (height - h) / 2;
			}
		}

		public StandOutLayoutParams(int id, int w, int h, int xpos, int ypos,
				int minWidth, int minHeight) {
			this(id, w, h, xpos, ypos);

			this.minWidth = minWidth;
			this.minHeight = minHeight;
		}

		public StandOutLayoutParams(int id, int w, int h, int xpos, int ypos,
				int minWidth, int minHeight, int threshold) {
			this(id, w, h, xpos, ypos, minWidth, minHeight);

			this.threshold = threshold;
		}

		// helper to create cascading windows
		private int getX(int id, int width) {
			Display display = mWindowManager.getDefaultDisplay();
			int displayWidth = display.getWidth();

			int types = sWindowCache.size();

			int initialX = 100 * types;
			int variableX = 100 * id;
			int rawX = initialX + variableX;

			return rawX % (displayWidth - width);
		}

		// helper to create cascading windows
		private int getY(int id, int height) {
			Display display = mWindowManager.getDefaultDisplay();
			int displayWidth = display.getWidth();
			int displayHeight = display.getHeight();

			int types = sWindowCache.size();

			int initialY = 100 * types;
			int variableY = x + 200 * (100 * id) / (displayWidth - width);

			int rawY = initialY + variableY;

			return rawY % (displayHeight - height);
		}

		public void setFocusFlag(boolean focused) {
			if (focused) {
				flags = flags ^ StandOutLayoutParams.FLAG_NOT_FOCUSABLE;
			} else {
				flags = flags | StandOutLayoutParams.FLAG_NOT_FOCUSABLE;
			}
		}
	}

	protected static class DropDownListItem {
		public int icon;
		public String description;
		public Runnable action;

		public DropDownListItem(int icon, String description, Runnable action) {
			super();
			this.icon = icon;
			this.description = description;
			this.action = action;
		}

		@Override
		public String toString() {
			return description;
		}
	}
}
