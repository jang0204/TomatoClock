package com.customappbar.tomatoclock;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;
import com.blankj.utilcode.util.SPUtils;

public class setting extends AppCompatActivity {

    Switch sw1, sw2;
    Boolean isVibrator,isAudio = false;
    private SharedPreferences sharedP1;
    private SharedPreferences sharedP2;
    private static final String data = "DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();
        initSet();
        readData();
    }

    public void initView() {
        sw1 = (Switch) findViewById(R.id.switch1);
        sw2 = (Switch) findViewById(R.id.switch2);
        sharedP1 = getSharedPreferences(data, MODE_PRIVATE);
        sharedP2 = getSharedPreferences(data, MODE_PRIVATE);
    }

    public void readData() {//讀取
        sw1.setChecked(sharedP1.getBoolean("sw1", false));
        sw2.setChecked(sharedP2.getBoolean("sw2", false));
    }

    public void initSet() {
        sw1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw1.isChecked()) {
                    isVibrator = true;
                    SPUtils.getInstance().put("isVibrator",true);//存到資料庫
                    sharedP1.edit().putBoolean("sw1", true).commit();//紀錄
                    Log.i("Vibrator1=",isVibrator.toString());
                   // Toast.makeText(setting.this, "開啟振動", Toast.LENGTH_SHORT).show();
                } else {
                    isVibrator = false;
                    SPUtils.getInstance().put("isVibrator",false);//存到資料庫
                    sharedP1.edit().putBoolean("sw1", false).commit();//紀錄
                    Log.i("Vibrator2=",isVibrator.toString());
                   // Toast.makeText(setting.this, "關閉振動", Toast.LENGTH_SHORT).show();
                }
            }
        });
        sw2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sw2.isChecked()) {
                    isAudio = true;
                    SPUtils.getInstance().put("isAudio",true);//存到資料庫
                    sharedP2.edit().putBoolean("sw2", true).commit();//紀錄
                    Log.i("Vibrator3=",isAudio.toString());
                   // Toast.makeText(setting.this, "開啟音效", Toast.LENGTH_SHORT).show();
                } else {
                    isAudio = false;
                    SPUtils.getInstance().put("isAudio",false);//存到資料庫
                    sharedP2.edit().putBoolean("sw2", false).commit();//紀錄
                    Log.i("Vibrator4=",isAudio.toString());
                   // Toast.makeText(setting.this, "關閉音效", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


