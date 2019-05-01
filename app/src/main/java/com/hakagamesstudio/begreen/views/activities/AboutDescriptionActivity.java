package com.hakagamesstudio.begreen.views.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.support.ConstantValues;

public class AboutDescriptionActivity extends AppCompatActivity {

    private String typeOpen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_description);

        String title = getIntent().getExtras().getString("title", "");
        typeOpen = getIntent().getExtras().getString("open", "");

        ImageButton dialog_button = findViewById(R.id.dialog_button);
        TextView dialog_title = findViewById(R.id.dialog_title);
        WebView dialog_webView = findViewById(R.id.dialog_webView);

        dialog_title.setText(title);
        String description12 = ConstantValues.PRIVACY_POLICY;
        String styleSheet12 = "<style> " +
                "body{background:#ffffff; margin:0; padding:0} " +
                "p{color:#757575;} " +
                "img{display:inline; height:auto; max-width:100%;}" +
                "</style>";

        dialog_webView.setHorizontalScrollBarEnabled(false);
        dialog_webView.loadDataWithBaseURL(null, styleSheet12 + description12, "text/html", "utf-8", null);

        dialog_button.setOnClickListener(v -> {
            if (typeOpen.equals("direct")){
                startActivity(new Intent(AboutDescriptionActivity.this, Menu_NavDrawer_Acititty.class));
                finish();
            }else{
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        if (typeOpen.equals("direct")){
            startActivity(new Intent(AboutDescriptionActivity.this, Menu_NavDrawer_Acititty.class));
            finish();
        }else{
            finish();
        }
    }
}
