package com.example.lg_user.project;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SelectPurposeActivity extends AppCompatActivity {

    private Button study;
    private Button license;
    private Button interview;

    private Button menu;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_purpose);

        firebaseAuth = FirebaseAuth.getInstance();

        study = (Button) findViewById(R.id.selectpurpos_button_study);
        license = (Button) findViewById(R.id.selectpurpos_button_license);
        interview = (Button) findViewById(R.id.selectpurpos_button_interview);

        menu = (Button) findViewById(R.id.Logout_Button_Id);



        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(SelectPurposeActivity.this);
                alert_confirm.setMessage("로그아웃 하시겠습니까?").setCancelable(false).setPositiveButton("아니요",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).setNegativeButton("네",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(SelectPurposeActivity.this, LoginActivity.class);
                                firebaseAuth.signOut();
                                startActivity(intent);
                                finish();
                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });

        //지금은 임시로 그냥 클릭시 대화방목록에 들어가게 해놨지만
        //나중에 목적별로 방 정리를 하려면 인텐트 시에 study, license 등에 해당하는 값을 보내야 한다.
        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectPurposeActivity.this, Chattingboard.class);
                intent.putExtra("BoardValue", "study");
                Log.v("태그", "버튼 클릭.");
                startActivity(intent);
                finish();
            }
        });
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectPurposeActivity.this, Chattingboard.class);
                intent.putExtra("BoardValue", "license");
                Log.v("태그", "버튼 클릭.");
                startActivity(intent);
                finish();
            }
        });
        interview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SelectPurposeActivity.this, Chattingboard.class);
                intent.putExtra("BoardValue", "interview");
                Log.v("태그", "버튼 클릭.");
                startActivity(intent);
                finish();
            }
        });

        passPushTokenToServer();

        Button debuging = (Button)findViewById(R.id.chatting_list_debuging_Id);
        debuging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectPurposeActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    void passPushTokenToServer(){

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);


    }
}
