package co.gladminds.bajajcvl.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import co.gladminds.bajajcvl.Common.Common;

public class Splashscreen extends Activity {

    Thread splashTread;
    ImageView imageView;
    Animation bottom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(co.gladminds.bajajcvl.R.layout.activity_splashscreen);
        imageView = (ImageView) findViewById(co.gladminds.bajajcvl.R.id.splash);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        bottom = AnimationUtils.loadAnimation(getApplicationContext(),
                co.gladminds.bajajcvl.R.anim.bottom_anim);
        imageView.startAnimation(bottom);

        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    if (Common.getPreferences(getApplicationContext(), "login").equalsIgnoreCase("true")) {
                        Intent intent = new Intent(Splashscreen.this, MainActivity.class);
                        startActivity(intent);
                        overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);
                        finish();
                    } else {
                        Intent intent = new Intent(Splashscreen.this, Login.class);
                        startActivity(intent);
                        overridePendingTransition(co.gladminds.bajajcvl.R.anim.enter, co.gladminds.bajajcvl.R.anim.exit);
                        finish();
                    }

                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    Splashscreen.this.finish();
                }

            }
        };
        splashTread.start();
    }

}