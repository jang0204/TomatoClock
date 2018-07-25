package com.customappbar.tomatoclock;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class FirstPage extends AppCompatActivity {
    ConstraintLayout title,des;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);

        imageView = findViewById(R.id.imageView);
        title = findViewById(R.id.title);
        des = findViewById(R.id.des);

        Animation zoom = AnimationUtils.loadAnimation(FirstPage.this,R.anim.zoom);
        Animation fadein = AnimationUtils.loadAnimation(FirstPage.this,R.anim.fade_in);
        title.setAnimation(zoom);
        des.setAnimation(fadein);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FirstPage.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }
}

