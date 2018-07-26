package com.customappbar.tomatoclock;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;

public class MainActivity extends AppCompatActivity {
    private String currentTime;
    public long sleepTime;
    Handler handler;
    TomatoView clockView;
    ImageButton imgbtn,imgbtn2;

      @Override  //呼叫menu功能鍵
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.features_meun,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {//跳轉到setting頁面
        switch (item.getItemId()){
            case R.id.setting_item:
                Intent intent = new Intent(MainActivity.this,setting.class);
                startActivity(intent);
                break;
            case R.id.list_item:
                Intent i = new Intent(MainActivity.this,Description.class);
                startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clockView =(TomatoView) findViewById(R.id.clockView);//綁定
        clockView.setOnClickListener(new View.OnClickListener() {//監聽
            @Override
            public void onClick(View v) {
                final Dialog d = new Dialog(MainActivity.this);//選擇時間對話框(使用NumberPicker)
                d.setContentView(R.layout.activity_time_pick);
                d.setCanceledOnTouchOutside(false); //按對話框以外的地方不起作用。按返回鍵起作用
                Button b1 = (Button) d.findViewById(R.id.button1);//確定按鈕
                Button b2 = (Button) d.findViewById(R.id.button2);//取消按鈕
                final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
                np.setMaxValue(4); // max value 4  最大參數
                np.setMinValue(0);   // min value 0  最小參數
                final String[] minutes = {"5", "10", "15", "20", "25"};
                np.setDisplayedValues(minutes);
                np.setWrapSelectorWheel(false);

                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String currentValue = minutes[np.getValue()];//抓取的陣列位置轉換成值放進 CurrentValue
                        currentTime = clockView.setTime(currentValue);//把currentValue放進clockView.setTime
                        clockView.invalidate();
                        switch (currentTime){//currentTime用於執行倒數後同步執行睡眠時間，使時間不延遲進而產生Dialog
                            case "5":
                                sleepTime = 300000;
                                break;
                            case"10":
                                sleepTime = 600000;
                                break;
                            case"15":
                                sleepTime = 900000;
                                break;
                            case"20":
                                sleepTime = 1200000;
                                break;
                            case "25":
                                sleepTime = 1500000;
                                break;
                        }
                        d.dismiss();// 關閉視窗
                        //因為建立tomatoView時已把值產生上去了,因此造成currentValue帶不進去,
                        // 所以加上invalidate(),讓它重畫一次TomatoView的onDraw,才帶得出currentValue
                        //Toast.makeText(MainActivity.this, "顯示:" + currentValue, Toast.LENGTH_LONG).show();
                    }
                });
                b2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        d.dismiss(); // 關閉視窗
                    }
                });
                d.show();
            }
        });
        imgbtn=(ImageButton)findViewById(R.id.imgBtn);
        imgbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("0.0",clockView.textTime);
                if (!clockView.textTime.equals("00:00") ) {
                clockView.start();
                    handler = new Handler(new Handler.Callback() {
                        @Override
                        public boolean handleMessage(Message msg) {
                            if (msg.what == 1) {//判斷為1時產生Dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                                        .setTitle("時間到囉!")
                                        .setMessage("休息一下吧!")
                                        .setPositiveButton("休息", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                clockView.stopVibrator();
                                                clockView.stopSound();
                                                clockView.restCountDownTimer();
                                            }
                                        })
                                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                clockView.stopVibrator();
                                                clockView.stopSound();
                                            }
                                        });
                                builder.setCancelable(false);//按對話框以外的地方不起作用。按返回鍵起作用
                                builder.show();
                            }
                            return false;
                        }
                    });
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {//睡眠用於倒數同步化,使而產生Dialog
                                Thread.sleep(sleepTime);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } finally {
                                Message msg = new Message();
                                msg.what = 1;
                                handler.sendMessage(msg);
                            }
                        }
                    }).start();
                }
            }
        });
        imgbtn2=(ImageButton)findViewById(R.id.imgBtn2);
        imgbtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clockView.stopSound();//結束音效
                clockView.stopVibrator();//結束震動
            }
        });
    }
//    public static Boolean getMa_Vibrator(){ //設一個讓TomatoView抓取的方法
//        Log.i("getMa_Vibrator()=",Ma_Vibrator.toString());
//          return Ma_Vibrator;
//    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) { // 攔截返回鍵
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("離開視窗")
                    .setMessage("確定要結束應用程式嗎?")
                    //.setIcon(R.drawable.tomato_icon)
                    .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);
                            android.os.Process.killProcess(android.os.Process.myPid());//關閉應用程式
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
           }
        return true;
    }
}
