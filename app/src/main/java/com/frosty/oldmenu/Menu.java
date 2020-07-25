package com.frosty.oldmenu;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.text.Html;
import android.text.Spanned;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

import static android.view.ViewGroup.LayoutParams.FILL_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class Menu extends Service {
    private WindowManager mWindowManager;
    private View mFloatingView;
    private RelativeLayout mRootContainer, mCollapsed, ButtonLayout;
    private LinearLayout SwitchList, MenuLayout, TitleLayout;
    public ImageView image;
    private WindowManager.LayoutParams params;
    public ScrollView Scroll;
    public TextView TitleText;

    public native String Title();
    public native String WebViewText();
    public native String closeButton();
    public native String Icon();
    private native String[] getFeatureList();
    public native void changeToggle(int feature);
    public native void Load(Menu menu);


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        LoadService(this);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            public void run() {
                Menu.this.Thread();
                handler.postDelayed(this, 1000);
            }
        });
    }


    private int dp2px(int dp) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public void Thread() {
        if (this.mFloatingView == null) {
            return;
        }
        if (isAppBackground()) {
            this.mFloatingView.setVisibility(View.INVISIBLE);
        } else {
            this.mFloatingView.setVisibility(View.VISIBLE);
        }
    }

    public void LoadService(Context context) {
        try {
            FrameLayout rootFrame = new FrameLayout(context);
            mRootContainer = new RelativeLayout(context);
            mCollapsed = new RelativeLayout(context);
            MenuLayout = new LinearLayout(context);
            SwitchList = new LinearLayout(context);
            TitleLayout = new LinearLayout(context);
            Scroll = new ScrollView(context);
            image = new ImageView(context);
            ButtonLayout = new RelativeLayout(context);
            TitleText = new TextView(context);
            /*******************************************************************/

            FrameLayout.LayoutParams flayoutParams = new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            rootFrame.setLayoutParams(flayoutParams);
            mRootContainer.setLayoutParams(new FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));


            mCollapsed.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            mCollapsed.setVisibility(View.VISIBLE);


            image.setLayoutParams(new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            int dimension = 60;
            int dimensionInDp = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dimension, getResources().getDisplayMetrics());
            image.getLayoutParams().height = dimensionInDp;
            image.getLayoutParams().width = dimensionInDp;
            image.requestLayout();
            image.setScaleType(ImageView.ScaleType.FIT_XY);
            byte[] arrayOfByte1 = Base64.decode(Icon(), 0);
            Bitmap bitmap1 = BitmapFactory.decodeByteArray(arrayOfByte1, 0, arrayOfByte1.length);
            image.setImageBitmap(bitmap1);
            ((ViewGroup.MarginLayoutParams) this.image.getLayoutParams()).topMargin = this.dp2px(10);


            MenuLayout.setVisibility(View.GONE);
            GradientDrawable bg = new GradientDrawable();
            setColor(bg,Color.BLACK);
            setCornerRadius(bg,20);
            setStroke(bg,6, Color.RED);
            MenuLayout.setBackground(bg);
            MenuLayout.setPadding(8,8,8,8);
            MenuLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams params_mMenu = new LinearLayout.LayoutParams(dp2px(300), dp2px(300));
            MenuLayout.setLayoutParams(params_mMenu);


            Scroll.setLayoutParams(new LinearLayout.LayoutParams(FILL_PARENT, dp2px(155)));
            SwitchList.setLayoutParams(new LinearLayout.LayoutParams(FILL_PARENT, FILL_PARENT));
            SwitchList.setOrientation(LinearLayout.VERTICAL);
            SwitchList.setPadding(8, 8, 8, 8);


            TitleLayout.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT));
            TitleLayout.setGravity(Gravity.CENTER);
            TitleLayout.setOrientation(LinearLayout.VERTICAL);
            TitleText.setText(Title());
            TitleText.setTextColor(Color.WHITE);
            TitleText.setGravity(Gravity.CENTER);
            TitleText.setShadowLayer(20,0,0, Color.WHITE);
            TitleText.setTextSize(14f);
            TitleText.setTypeface(TitleText.getTypeface(),Typeface.BOLD);
            WebView webView = new WebView(context);
            webView.setBackgroundColor(Color.TRANSPARENT);
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);
            webView.loadData("<html><head><style>body{color: red;font-weight:bold;font-family:Courier, monospace;}</style></head><body><marquee class=\"GeneratedMarquee\" direction=\"left\" scrollamount=\"4\" behavior=\"scroll\">" + WebViewText() + "</marquee></body></html>", "text/html", "utf-8");
            LinearLayout.LayoutParams title_Layout = new LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            webView.setLayoutParams(title_Layout);
            TitleLayout.addView(TitleText);
            TitleLayout.addView(webView);


            ButtonLayout.setLayoutParams(new RelativeLayout.LayoutParams(-1, dp2px(65)));
            ButtonLayout.setPadding(10, 10, 10, 10);
            ButtonLayout.setGravity(Gravity.CENTER);
            GradientDrawable closebg = new GradientDrawable();
            setColor(closebg,Color.TRANSPARENT);
            setStroke(closebg,4, Color.RED);
            setCornerRadius(closebg,10);
            Button CloseButton = new Button(this);
            CloseButton.setBackground(closebg);
            CloseButton.setText(closeButton());
            CloseButton.setTextColor(Color.WHITE);
            CloseButton.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            CloseButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {

                    mCollapsed.setVisibility(View.VISIBLE);
                    MenuLayout.setVisibility(View.GONE);
                }
            });
            ButtonLayout.addView(CloseButton);

            /*******************************************************************/

            rootFrame.addView(mRootContainer);
            mRootContainer.addView(mCollapsed);
            mRootContainer.addView(MenuLayout);

            mCollapsed.addView(image);
            MenuLayout.addView(TitleLayout);
            MenuLayout.addView(Scroll);
            Scroll.addView(SwitchList);
            MenuLayout.addView(ButtonLayout);
            /*******************************************************************/

            mFloatingView = rootFrame;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                params = new WindowManager.LayoutParams(
                        WRAP_CONTENT,
                        WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            } else {
                params = new WindowManager.LayoutParams(
                        WRAP_CONTENT,
                        WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }

            params.gravity = Gravity.CENTER | Gravity.CENTER;
            params.x = 0;
            params.y = 0;

            mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            mWindowManager.addView(mFloatingView, params);

            final View collapsedView = mCollapsed;
            final View expandedView = MenuLayout;

            mFloatingView.setOnTouchListener(onTouchListener());
            image.setOnTouchListener(onTouchListener());
            initMenuButton(collapsedView, expandedView);
            Functions();
            Load(this);
        } catch (Exception ex) {
            Toast.makeText(context, ex.toString(), Toast.LENGTH_LONG).show();
        }
    }


    private View.OnTouchListener onTouchListener() {
        return new View.OnTouchListener() {
            final View collapsedView = mCollapsed;

            final View expandedView = MenuLayout;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        initialX = params.x;
                        initialY = params.y;

                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_UP:
                        int Xdiff = (int) (event.getRawX() - initialTouchX);
                        int Ydiff = (int) (event.getRawY() - initialTouchY);

                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                collapsedView.setVisibility(View.GONE);
                                expandedView.setVisibility(View.VISIBLE);

                            }
                        }
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        mWindowManager.updateViewLayout(mFloatingView, params);
                        return true;
                }
                return false;
            }
        };
    }

    private void initMenuButton(final View collapsedView, final View expandedView) {
        image.setOnClickListener(new ImageView.OnClickListener() {
            @Override
            public void onClick(View view) {
                collapsedView.setVisibility(View.GONE);
                expandedView.setVisibility(View.VISIBLE);

            }
        });

    }

    private void Functions()
    {
        final String[] listFT = getFeatureList();
        for (int i2 = 0; i2 < listFT.length; i2++) {
            final int l2 = i2;
            addSwitch(listFT[i2], new SW() {
                @Override
                public void OnWrite(boolean isChecked) {
                    if (isChecked) {

                        changeToggle(l2);
                    } else {

                        changeToggle(l2);
                    }
                }
            });
        }
    }

    private void addSwitch(String name, final SW listner) {
        final Switch sw = new Switch(this);
        sw.setText(name);
        sw.setTextColor(Color.WHITE);
        sw.setPadding(10, 3, 3, 3);
        sw.setTextSize(15.0f);
        sw.setTypeface(sw.getTypeface(), Typeface.NORMAL);
        sw.getThumbDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        sw.getTrackDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                listner.OnWrite(isChecked);
                if(isChecked) {
                    sw.getThumbDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                    sw.getTrackDrawable().setColorFilter(Color.GREEN, PorterDuff.Mode.MULTIPLY);
                } else {
                    sw.getThumbDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                    sw.getTrackDrawable().setColorFilter(Color.RED, PorterDuff.Mode.MULTIPLY);
                }
            }
        });
        SwitchList.addView(sw);
    }

    public void setColor(GradientDrawable gradientDrawable, int color){
        gradientDrawable.setColor(color);
    }

    public void setStroke(GradientDrawable gradientDrawable, int width, int color){
        gradientDrawable.setStroke(width,color);
    }

    public void setCornerRadius(GradientDrawable gradientDrawable, float radius){
        gradientDrawable.setCornerRadius(radius);
    }

    public boolean isAppBackground() {
        ActivityManager.RunningAppProcessInfo runningAppProcessInfo = new ActivityManager.RunningAppProcessInfo();
        ActivityManager.getMyMemoryState(runningAppProcessInfo);
        return runningAppProcessInfo.importance != 100;
    }

    private boolean isViewCollapsed() {
        return mFloatingView == null || mCollapsed.getVisibility() == View.VISIBLE;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        stopSelf();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingView != null){
            mWindowManager.removeView(mFloatingView);
        }
    }


    private interface SW {
        void OnWrite(boolean z);
    }

}