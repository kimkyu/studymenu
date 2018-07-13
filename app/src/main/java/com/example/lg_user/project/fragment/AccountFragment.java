package com.example.lg_user.project.fragment;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.lg_user.project.LoginActivity;
import com.example.lg_user.project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class AccountFragment extends Fragment {

    Button logOut_Button;

    TextView comment_textView;
    TextView nickname_textView;
    ImageView userimage_imageView;

    String uid;
    String nickname;
    String email;
    String ImageURI;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_account, container, false);

        ///////////////////////////////////////////
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            nickname = user.getDisplayName();
            email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getToken() instead.
            String uid = user.getUid();
            Log.v("태그", "이름: " + nickname + ", 이메일: " + email + ", 사진주소: " + photoUrl + ", 유저푸시: " + uid);
        }
        ////////////////////////////////

        userimage_imageView = (ImageView) view.findViewById(R.id.UserImage_Id);

        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("profileImageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) messageSnapshot.getValue();
                    if (uid.equals(messageSnapshot.getKey())) {
                        ImageURI = (String) map.get("profileImageUrl");
                        Log.v("이미지 태그", ImageURI);

                        Glide.with(view) // 단일로 동작하는 액티비티가 아니라 한 레이아웃 속에 들어가는 프래그먼트라서 view.
                                .load(ImageURI)
                                .into(userimage_imageView);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        //////////////////////////////////////
        email = user.getEmail();
        final TextView useremail_textview = (TextView) view.findViewById(R.id.Email_Id);
        useremail_textview.setText(email);

        ///////////////////////////////////////////////////////////////
        nickname_textView = (TextView) view.findViewById(R.id.Nickname_Id);
        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("userName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) messageSnapshot.getValue();
                    if (uid.equals(messageSnapshot.getKey())) {
                        nickname_textView.setText((String) map.get("userName"));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        nickname_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nickname_change(view.getContext());
            }
        });
////////////////////////////////////////////////////////////////////////////////////////////
        comment_textView = (TextView) view.findViewById(R.id.Comment_Id);
        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("comment").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) messageSnapshot.getValue();
                    if (uid.equals(messageSnapshot.getKey())) {
                        comment_textView.setText((String) map.get("comment"));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        comment_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment_change(view.getContext());
            }
        });

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        firebaseAuth = FirebaseAuth.getInstance();
        logOut_Button = (Button) view.findViewById(R.id.Logout_Button_Id);
        logOut_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                android.support.v7.app.AlertDialog.Builder alert_confirm = new android.support.v7.app.AlertDialog.Builder(view.getContext());
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

                                Intent intent = new Intent(view.getContext(), LoginActivity.class);
                                firebaseAuth.signOut();
                                startActivity(intent);
                                getActivity().finish();

                            }
                        });
                android.support.v7.app.AlertDialog alert = alert_confirm.create();
                alert.show();
            }
        });


        return view;
    }

    void comment_change(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_comment, null);
        final EditText editText = (EditText) view.findViewById(R.id.commentDialog_edittext);
        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                comment_textView.setText(editText.getText().toString());
                Map<String, Object> stringObjectMap = new HashMap<>();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                stringObjectMap.put("comment", editText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(stringObjectMap);

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    void nickname_change(Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_comment, null);
        final EditText editText = (EditText) view.findViewById(R.id.commentDialog_edittext);
        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nickname_textView.setText(editText.getText().toString());
                Map<String, Object> stringObjectMap = new HashMap<>();
                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                stringObjectMap.put("userName", editText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("users").child(uid).updateChildren(stringObjectMap);

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        builder.show();
    }

    public void onStart() {
        super.onStart();


    }
}
