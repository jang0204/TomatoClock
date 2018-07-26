package com.customappbar.tomatoclock;

import android.animation.ValueAnimator;
import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import com.blankj.utilcode.util.SPUtils;

public class TomatoView extends View {
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);//宣告線圈ID並使邊緣平滑
    private Paint timePaint = new Paint(Paint.ANTI_ALIAS_FLAG);//宣告時間ID並使邊緣平滑
    private int mColor = Color.parseColor("#FFB7DD");//自訂倒數圓顏色
    private int centerX;//置中X軸點
    private int centerY;//置中Y軸點
    private int radius;//半徑
    private RectF mRectF = new RectF();// 宣告一個圓的ID
    private float sweepVelocity = 0;// 掃描速度(掃過的區域)
    public static final float START_ANGLE = -90;//圓的進度條
    public String textTime = "00:00";//預設時間
    private Vibrator vibrator;//震動
    private CountTime timer;
    private SoundPool soundPool;
    private int soundID,streamID;
    public  Boolean is_getVibrator , is_getAudio = false;//震動，音效的布林值

    public String setTime(String currentValue) {//currentValue裡的值轉換成 textTime的值
        if (currentValue.equals("5")) {
            textTime = "05:00";
        } else if (currentValue.equals("10")) {
            textTime = "10:00";
        } else if (currentValue.equals("15")) {
            textTime = "15:00";
        } else if (currentValue.equals("20")) {
            textTime = "20:00";
        } else if (currentValue.equals("25")) {
            textTime = "25:00";
        }
        return currentValue;
    }

    public TomatoView(Context context) {//自訂View裡需要建立三個建構方法
        super(context); //代碼構造
    }
    public TomatoView(Context context, @Nullable AttributeSet attrs ) {
        super(context, attrs); //xml構造
        vibrator = (Vibrator)context.getSystemService(Service.VIBRATOR_SERVICE);//震動服務
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 5);
        soundID = soundPool.load(context, R.raw.sound1, 1);//音效服務
    }
    public TomatoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr); //xml+style構造
    }
    public static float dpToPixel(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return dp * metrics.density;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {//測量圓
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        centerX = width / 2;//值2為置中
        centerY = height / 2;//影響圓的高度,值越大越偏高
        radius = (int) dpToPixel(120);//影響圓的大小
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {//onDraw畫圓
        super.onDraw(canvas);
        mRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        //紅色圓
        canvas.save();
        mPaint.setColor(Color.RED);//線圈顏色
        mPaint.setStyle(Paint.Style.STROKE);//設置畫筆的樣式，STORKE:描邊  FILL_AND_STORE:描邊並填充
        mPaint.setStrokeWidth(dpToPixel(15));//線圈粗細
        canvas.drawCircle(centerX, centerY, radius, mPaint);//繪製圓形
        canvas.restore();//復原
        //桃色圆
        canvas.save();
        mPaint.setColor(mColor);//顏色
        canvas.drawArc(mRectF, START_ANGLE, 360 * sweepVelocity, false, mPaint);//畫圓弧
        canvas.restore();//復原
        //時間顯示
        canvas.save();
        timePaint.setColor(Color.BLACK); //字體黑色
        timePaint.setStyle(Paint.Style.FILL);
        timePaint.setTextSize(dpToPixel(40)); //字體大小
        canvas.drawText(textTime, centerX - timePaint.measureText(textTime) / 2,//這段會顯示textTime
                centerY - (timePaint.ascent() + timePaint.descent()) / 2, timePaint);
        canvas.restore();//復原
    }

    public void setallAction() {//音效及震動時間
        is_getVibrator = SPUtils.getInstance().getBoolean("isVibrator",false);//抓取setting的布林值
        is_getAudio = SPUtils.getInstance().getBoolean("isAudio",false);
       if (is_getVibrator == true){
           Log.i("getVibrator=",is_getVibrator.toString());
        vibrator.vibrate(new long[]{500,1000,2000,1000,2000,1000},0);//震動
       }
       if (is_getAudio == true){
           Log.i("getAudio=",is_getAudio.toString());
           streamID = soundPool.play(soundID, 1, 1, 0, -1, 1f);//音效
       }
    }
    public void stopSound(){//結束音效
        soundPool.stop(streamID);
    }
    public void stopVibrator(){//結束震動
          vibrator.cancel();
    }
    private String formatCountdownTime(int countdownTime) {//將倒數時間格式化，從秒數變成分鐘數
        StringBuilder sb = new StringBuilder();
        int minute = countdownTime / 60;//分鐘
        int second = countdownTime - 60 * minute;//秒數
        if (minute < 10) {
            sb.append("0" + minute + ":");
        } else {
            sb.append(minute + ":");
        }
        if (second < 10) {
            sb.append("0" + second);
        } else {
            sb.append(second);
        }
        return sb.toString();
    }

    public class CountTime extends CountDownTimer {//另外做了一個class來統一倒數時間
        public CountTime(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }
        @Override
        public void onTick(long millisUntilFinished) {//onTick -倒數時做的事情
            int countdownTime = ((int) millisUntilFinished-1000)/1000;//因CountDownTimer內部系統，需-1000才可歸 0
            textTime = formatCountdownTime(countdownTime);
            invalidate();
        }
        @Override
        public void onFinish() {//onFinish-倒數結束時的事情
            sweepVelocity = 0;//結束後還原紅圈狀態
            setallAction();//震動及音效
            invalidate();
        }
    }

    public void restCountDownTimer(){//五分鐘休息時間倒數
        //圓弧圈減少,要用到動畫ValueAnimator
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f); //可以傳入多個float的值，傳入0-->1.0f的值
        valueAnimator.setDuration(300000); //設定動畫時間
        valueAnimator.setInterpolator(new LinearInterpolator()); //設置插值器
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            //通過addUpdateListener設置ValueAnimator.AnimatorUpdateListener對動畫的值進行監聽
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                sweepVelocity = (float) animation.getAnimatedValue(); //取得動畫值，計算灰色圓弧跑過的區域
                mColor = Color.parseColor("#FFB7DD"); //設置粉桃色
                invalidate(); }
        });
        valueAnimator.start();
        timer = new CountTime(300000 + 1500, 1000);//總時間，間隔幾毫秒跳下一毫秒
        timer.start();//倒數方法統一在 CountTime裡
    }

    public void start() { //開始倒數計時
        if (textTime.equals("05:00") ) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
            valueAnimator.setDuration(300000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sweepVelocity = (float) animation.getAnimatedValue();
                    mColor = Color.parseColor("#FFB7DD");
                    invalidate(); }
            });
            valueAnimator.start();
            timer = new CountTime(300000 + 1500, 1000);
            timer.start();
        }else if(textTime.equals("10:00")){
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
            valueAnimator.setDuration(600000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sweepVelocity = (float) animation.getAnimatedValue();
                    mColor = Color.parseColor("#FFB7DD");
                    invalidate(); }
            });
            valueAnimator.start();
            timer = new CountTime(600000 + 1500, 1000);
            timer.start();
        }else if(textTime.equals("15:00")) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
            valueAnimator.setDuration(900000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sweepVelocity = (float) animation.getAnimatedValue();
                    mColor = Color.parseColor("#FFB7DD");
                    invalidate(); }
            });
            valueAnimator.start();
            timer = new CountTime(900000 + 1500, 1000);
            timer.start();
        }else if(textTime.equals("20:00")) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
            valueAnimator.setDuration(1200000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sweepVelocity = (float) animation.getAnimatedValue();
                    mColor = Color.parseColor("#FFB7DD");
                    invalidate(); }
            });
            valueAnimator.start();
            timer = new CountTime(1200000 + 1500, 1000);
            timer.start();
        }else if(textTime.equals("25:00")) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
            valueAnimator.setDuration(1500000);
            valueAnimator.setInterpolator(new LinearInterpolator());
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    sweepVelocity = (float) animation.getAnimatedValue();
                    mColor = Color.parseColor("#FFB7DD");
                    invalidate(); }
            });
            valueAnimator.start();
            timer = new CountTime(1500000 + 1500, 1000);
            timer.start();
        }
    }
}