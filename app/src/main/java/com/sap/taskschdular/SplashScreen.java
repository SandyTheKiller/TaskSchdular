package com.sap.taskschdular;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sap.taskschdular.Session.Session;
import com.sap.taskschdular.Session.SessionConstents;

public class SplashScreen extends AppCompatActivity {
    private CountDownTimer mCountDownTimer;
   // private TextView mTxtAppName;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);

     //   mTxtAppName= (TextView) findViewById(R.id.txt_app_name);
        mImageView=(ImageView)findViewById(R.id.img_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountDownTime();
    }

    private void startCountDownTime(){
        mCountDownTimer = new CountDownTimer( 1000, 1000 ) {
            @Override
            public void onTick ( long millisUntilFinished ) {

            }

            @Override
            public void onFinish () {
                mCountDownTimer.cancel ();


                TranslateAnimation animation = new TranslateAnimation( 0, 0, 0, - mImageView.getY () - mImageView.getY () );
                //animation.setInterpolator ( SplashScreenActivity.this,R.anim.icon_anim_fade_out );
                animation.setDuration ( 400 );
                animation.setFillAfter ( true );

                animation.setAnimationListener ( new MyAnimationListener () );
                mImageView.startAnimation ( animation );
            }
        }.start ();
    }

    private class MyAnimationListener implements Animation.AnimationListener {

        @Override
        public void onAnimationEnd ( Animation animation ) {
            // mImageView.setAlpha ( 0.0f );
            mImageView.setVisibility ( View.INVISIBLE );
            Intent it = new Intent();
            it.setClass ( SplashScreen.this, Activity_Login.class );
            if(Session.getInstance().getValue(SplashScreen.this, SessionConstents.KEY_USER_ID).equalsIgnoreCase("")){
                it.setClass ( SplashScreen.this, Activity_Login.class );
            }else{
                it.setClass ( SplashScreen.this, AddConsinmentsActivity.class );
            }

            startActivity ( it );
            finish ();
        }

        @Override
        public void onAnimationRepeat ( Animation animation ) {
        }

        @Override
        public void onAnimationStart ( Animation animation ) {
        }
    }
}
