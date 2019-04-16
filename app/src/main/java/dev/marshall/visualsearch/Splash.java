package dev.marshall.visualsearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {
    private ImageView imageView;
    private TextView textView;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        imageView= findViewById(R.id.imageView);
        textView= findViewById(R.id.logo);
        Animation anim = AnimationUtils.loadAnimation(this,R.anim.splash_animation);
        imageView.startAnimation(anim);
        textView.startAnimation(anim);
        Thread timer = new Thread(){
            public void run(){
                try {
                    sleep(5000);
                }
                catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    starthomepage();
                    finish();
                }
            }
        };
        timer.start();



    }

    private void starthomepage() {


            startActivity(new Intent(this, MainActivity.class));
            finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        user= FirebaseAuth.getInstance().getCurrentUser();
    }
}
