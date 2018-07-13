package com.example.lg_user.project;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.example.lg_user.project.chat.GroupMessageActivity;
import com.example.lg_user.project.fragment.AccountFragment;
import com.example.lg_user.project.fragment.BlacklistFragment;
import com.example.lg_user.project.fragment.ChatFragment;
import com.example.lg_user.project.fragment.FriendFragment;
import com.example.lg_user.project.fragment.PeopleFragment;
import com.example.lg_user.project.fragment.SelectPurposeFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private Fragment firstFragment;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.mainactivity_bottomnavigationview);

        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new SelectPurposeFragment()).commit();
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            //네비게이션 xml은 res/menu/에 있음.
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {

                    case R.id.action_selectpurpose:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new SelectPurposeFragment()).commit();
                        return true;
                    case R.id.action_friend:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new FriendFragment()).commit();
                        return true;
                    case R.id.action_blacklist:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new BlacklistFragment()).commit();
                        return true;
                    case R.id.action_chatrooms:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new ChatFragment()).commit();
                        return true;
                    case R.id.action_account:
                        getFragmentManager().beginTransaction().replace(R.id.mainactivity_framelayout, new AccountFragment()).commit();
                        return true;
                }


                return false;
            }
        });



    }

    void passPushTokenToServer() {

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String, Object> map = new HashMap<>();

        map.put("pushToken", token);


        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);


    }

    public void setDefaultFragment() { /** * 화면에 보여지는 Fragment를 관리한다. * FragmentManager : Fragment를 바꾸거나 추가하는 객체 */
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction(); /** * R.id.container(activity_main.xml)에 띄우겠다. * 첫번째로 보여지는 Fragment는 firstFragment로 설정한다. */
        transaction.add(R.id.container, firstFragment); /** * Fragment의 변경사항을 반영시킨다. */
        transaction.commit();
    }


}
