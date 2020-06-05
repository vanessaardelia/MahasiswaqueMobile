package umn.ac.id.mahasiswaque;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

public class AboutmeActivity extends AppCompatActivity {
    private Switch darkmode;
    Session sharedpref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sharedpref = new Session(this);
        if(sharedpref.loadNightModeState()==true){
            setTheme(R.style.DarkTheme);
        }else setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutme);
        getSupportActionBar().setTitle("About Me");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        darkmode = findViewById(R.id.switch_darkmode);
        if(sharedpref.loadNightModeState()==true){
            darkmode.setChecked(true);
        }
        darkmode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    sharedpref.setNightModeState(true);
                    restartApp();
                }else{
                    sharedpref.setNightModeState(false);
                    restartApp();
                }
            }
        });
    }

    public void restartApp(){
        Intent i  = new Intent(getApplicationContext(), AboutmeActivity.class);
        startActivity(i);
        finish();
    }
}
