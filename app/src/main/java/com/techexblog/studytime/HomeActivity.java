package com.techexblog.studytime;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import butterknife.InjectView;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    @InjectView(R.id.fab_button) Button _fabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        ButterKnife.inject(this);
//
//        _fabButton.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Intent newGroupIntent = new Intent(getApplicationContext(), NewGroupActivity.class);
//                startActivity(newGroupIntent);
//            }
//        });
    }

    public void createNewGroup(View v){
        Intent newGroupIntent = new Intent(getApplicationContext(), NewGroupActivity.class);
        startActivity(newGroupIntent);
    }
}
