package com.hakagamesstudio.begreen.views.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.support.MyAppPrefsManager;
import com.hakagamesstudio.begreen.views.fragments.IntroFragment01;
import com.github.paolorotolo.appintro.AppIntro;

public class IntroActivity extends AppIntro {

    private MyAppPrefsManager myAppPrefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAppPrefsManager = new MyAppPrefsManager(this);

        addSlide(new IntroFragment01().newInstance(0));
        addSlide(new IntroFragment01().newInstance(1));
        addSlide(new IntroFragment01().newInstance(2));
        addSlide(new IntroFragment01().newInstance(3));
        addSlide(new IntroFragment01().newInstance(4));

        // Hide StatusBar
        showStatusBar(false);
        showSkipButton(true);
        setProgressButtonEnabled(true);

        //setBarColor(ContextCompat.getColor(IntroScreen.this, R.color.white));
        setBarColor(getResources().getColor(R.color.colorWhite));
        setSeparatorColor(getResources().getColor(R.color.colorPrimaryDark));
        //setSeparatorColor(ContextCompat.getColor(IntroScreen.this, R.color.colorPrimaryLight));

        setColorDoneText(getResources().getColor(R.color.colorPrimary));
        //setColorDoneText(ContextCompat.getColor(IntroScreen.this, R.color.colorPrimary));
        setColorSkipButton(getResources().getColor(R.color.colorPrimary));
        //setColorSkipButton(ContextCompat.getColor(IntroScreen.this, R.color.colorPrimary));
        setNextArrowColor(getResources().getColor(R.color.colorPrimary));
        //setNextArrowColor(ContextCompat.getColor(IntroScreen.this, R.color.colorPrimary));

        setIndicatorColor(getResources().getColor(R.color.colorPrimary),
                getResources().getColor(R.color.iconsLight));

    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        if (myAppPrefsManager.isFirstTimeLaunch()) {
            // Navigate to MainActivity
            startActivity(new Intent(this, Menu_NavDrawer_Acititty.class));
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }
        else {
            // Finish this Activity
            finish();
        }
    }

    //*********** Called when the Done Button pressed on IntroScreen ********//

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        if (myAppPrefsManager.isFirstTimeLaunch()) {
            // Navigate to MainActivity
            startActivity(new Intent(this, Menu_NavDrawer_Acititty.class));
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }
        else {
            // Finish this Activity
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (myAppPrefsManager.isFirstTimeLaunch()) {
            // Navigate to MainActivity
            startActivity(new Intent(this, Menu_NavDrawer_Acititty.class));
            finish();
            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
        }
        else {
            // Finish this Activity
            finish();
        }

    }
}