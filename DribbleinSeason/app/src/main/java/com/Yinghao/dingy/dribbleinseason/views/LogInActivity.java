package com.Yinghao.dingy.dribbleinseason.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.Yinghao.dingy.dribbleinseason.DribbleAPI.DribbleException;
import com.Yinghao.dingy.dribbleinseason.DribbleAPI.DribbleUtils;
import com.Yinghao.dingy.dribbleinseason.R;
import com.facebook.drawee.backends.pipeline.Fresco;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;



public class LogInActivity extends AppCompatActivity {

    @BindView(R.id.season_image_button) ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_log_in);
        ButterKnife.bind(this);


        //try load token and user
        DribbleUtils.InitialToken(this);

        if(DribbleUtils.isLoggedin()){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }else{
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                //go to dribble's web to login
                public void onClick(View view) {
                   Intent intent = DribbleUtils.openAuthWeb(LogInActivity.this);
                    startActivityForResult(intent, DribbleUtils.REQ_CODE);
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int request_code, int resultCode, Intent data){
        super.onActivityResult(request_code, resultCode, data);

        if(request_code == DribbleUtils.REQ_CODE && resultCode == RESULT_OK){
            final String auth_code = data.getStringExtra(DribbleUtils.KEY_CODE);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        //get access token
                        String token = DribbleUtils.obtainTokenFromDribble(auth_code);
                        //store the user and token
                        DribbleUtils.login(LogInActivity.this, token);

                        Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    catch (IOException | DribbleException e){
                        e.printStackTrace();
                    }
                }
            }).start();

        }

    }
}
