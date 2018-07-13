package com.example.lg_user.project.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.lg_user.project.Chattingboard;
import com.example.lg_user.project.LoginActivity;
import com.example.lg_user.project.MainActivity;
import com.example.lg_user.project.R;
import com.example.lg_user.project.SelectPurposeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class SelectPurposeFragment extends Fragment {
    private Button study;
    private Button license;
    private Button interview;

    private Button menu;




    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selectpurpose,container,false);



        study = (Button) view.findViewById(R.id.selectpurpos_button_study);
        license = (Button) view.findViewById(R.id.selectpurpos_button_license);
        interview = (Button) view.findViewById(R.id.selectpurpos_button_interview);





        //지금은 임시로 그냥 클릭시 대화방목록에 들어가게 해놨지만
        //나중에 목적별로 방 정리를 하려면 인텐트 시에 study, license 등에 해당하는 값을 보내야 한다.
        study.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Chattingboard.class);
                intent.putExtra("BoardValue", "study");
                Log.v("태그", "버튼 클릭.");
                startActivity(intent);
                getActivity().finish();

            }
        });
        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Chattingboard.class);
                intent.putExtra("BoardValue", "license");
                Log.v("태그", "버튼 클릭.");
                startActivity(intent);
                getActivity().finish();

            }
        });
        interview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Chattingboard.class);
                intent.putExtra("BoardValue", "interview");
                Log.v("태그", "버튼 클릭.");
                startActivity(intent);
                getActivity().finish();

            }
        });

        passPushTokenToServer();

//        Button debuging = (Button)findViewById(R.id.chatting_list_debuging_Id);
//        debuging.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SelectPurposeActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });

        return view;

    }

    void passPushTokenToServer(){

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("pushToken",token);

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(map);


    }
}
