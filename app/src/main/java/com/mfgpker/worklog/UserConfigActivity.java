package com.mfgpker.worklog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.mfgpker.worklog.databinding.ActivityUserConfigBinding;
import com.mfgpker.worklog.firebase.FirebaseUtils;

public class UserConfigActivity extends AppCompatActivity {

    private FirebaseUtils firebase = new FirebaseUtils();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUserConfigBinding binding = ActivityUserConfigBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        makeToolbar();

        if(firebase.validUser()) {
            finish();
        }

        if(MainActivity.config != null) {
            binding.userconfigCompany.setText(MainActivity.config.currentCompany);
        }

        binding.userconfigSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // save info
                if(MainActivity.config != null) {
                    MainActivity.config.currentCompany = binding.userconfigCompany.getText().toString();

                    if(!MainActivity.config.companies.contains(MainActivity.config.currentCompany)) {
                        MainActivity.config.companies.add(MainActivity.config.currentCompany);
                    }

                    firebase.setCurrentUserConfig(MainActivity.config);


                }

                finish();
            }
        });

    }

    private void makeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        // Display icon in the toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);

    }

    // Menu icons are inflated just as they were with actionbar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
}