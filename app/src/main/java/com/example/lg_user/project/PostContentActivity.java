package com.example.lg_user.project;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lg_user.project.chat.GroupMessageActivity;
import com.example.lg_user.project.model.ChatModel;
import com.example.lg_user.project.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;


public class PostContentActivity extends AppCompatActivity {

    String postTitle_intent;
    String postUser_intent;
    int postPeople_Max_intent;
    long postDate_intent;
    String postRegion_intent;
    String postTag_intent;
    String postContent_intent;
    String postBoardValue_intent;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");


    ChatModel chatModel = new ChatModel();
    String room_key;
    String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
    String user_getkey;

    int peoplenum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_content);

        Intent Reintent = getIntent();
        postTitle_intent = Reintent.getStringExtra("PostTitle");
        postUser_intent = Reintent.getStringExtra("PostUser");
        postPeople_Max_intent = Reintent.getIntExtra("PostPeopleMax", 0);

        postDate_intent = Reintent.getLongExtra("PostDate", 0);
        Date date = new Date(postDate_intent);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String Post_Date_String = simpleDateFormat.format(date);

        postRegion_intent = Reintent.getStringExtra("PostRegion");
        postTag_intent = Reintent.getStringExtra("PostTag");
        postContent_intent = Reintent.getStringExtra("PostContent");
        postBoardValue_intent = Reintent.getStringExtra("PostBoardValue");
        //https://stackoverflow.com/questions/38202763/how-to-know-the-random-id-given-to-a-firebase-data 파이어베이스 데이터 불러오기


        final TextView postTitle = (findViewById(R.id.Post_Content_Title_Id));
        final TextView postUser = (findViewById(R.id.Post_Content_User_Id));
        final TextView postPeople_Max = (findViewById(R.id.Post_Content_People_Max_Id));
        TextView postDate = (findViewById(R.id.Post_Content_Date_Id));
        TextView postRegion = (findViewById(R.id.Post_Content_Region_Id));
        TextView postTag = (findViewById(R.id.Post_Content_Tag_Id));
        TextView postContent = (findViewById(R.id.Post_Content_Contents_Id));

        postTitle.setText(postTitle_intent);

        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("uid").equalTo(postUser_intent).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) messageSnapshot.getValue();
                    if (postUser_intent.equals((String) map.get("uid"))) {
                        postUser.setText((String) map.get("userName"));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
//        postUser.setText(postUser_intent);
        postDate.setText(Post_Date_String);

        postContent.setText(postContent_intent);
        postTag.setText("태그: " + postTag_intent);
        postRegion.setText("지역: " + postRegion_intent);


        final long postmake_Time = postDate_intent;
        Log.v("태그 시작: ", " 시작");
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("posts")
                .orderByChild("timestamp")
                .equalTo(postmake_Time)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            room_key = childSnapshot.getKey();
                        }
                        if (room_key == null) {
                        } else {
                            FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        peoplenum++;
                                    }

                                    postPeople_Max.setText("참가 인원: " + String.valueOf(peoplenum) + "/" + postPeople_Max_intent);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


        Button chatting_in_button = findViewById(R.id.Post_Content_ChattingButton_Id);
        chatting_in_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /////////////////////////////채팅 참가하기 누를시 입장 전 미리 방을 생성하고 거기에 입장하는 형태로.///////////////////////////////////////////////////////////
                chatModel.users.clear(); //나중에 3명이상 채팅 떄 문제 생기면 삭제하자.

//                chatModel.users.put(postUser_intent, true); //이건 나중에 채팅방 안에 있는 사람 목록 받아와서 죄다 추가 후에 방 키값이랑 비교하는 형태로 해야할 듯.
                chatModel.users.put(myUid, true);

                String postfirst_Uid = postUser_intent;

                Log.v("태그 끝: ", " 여기까지 성공.." + room_key); //이유는 모르겠지만 두 번째 시행 이후에서야 room_key에 제대로 값이 들어온다.
                //if문을 써서 room_key가 null일 떄의 동작을 지정하면 되지 않을까.

                if (room_key != null) {
                    Log.v("태그1: ", " 여기까지 성공..");
                    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    DatabaseReference userNameRef = rootRef.child("posts").child(room_key).child("chatrooms").child("users");
                    ValueEventListener eventListener = new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ////////////////////////////////////////////////////////////////////////////////
                            Date PostDate = new Date();
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
                            final long Timestamp = PostDate.getTime();

                            Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();

                            ///////////////////////////////////////////////////////////////////////////////
                            if (!dataSnapshot.exists()) {//채팅방이 존재하지 않는 신생방이라면 새로 채팅방을 만듬
                                Toast.makeText(PostContentActivity.this, "새로 채팅방이 신설되었습니다.", Toast.LENGTH_SHORT).show();
//                                ////////////////////////////////////////////////////
//                                FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").setValue(chatModel);
                                Map<String, Long> entermessage = new HashMap<>();
//                                entermessage.clear();
//                                entermessage.put("message",(String)map.get("userName") + " 님이 참가하셨습니다.");
//                                entermessage.put("entertime", Timestamp);
//                                entermessage.put("uid", "SystemMessage");
//                                FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("comments").push().setValue(entermessage);
//                                FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("users").child(myUid).child("entertime").setValue(Timestamp);
//                                entermessage.clear();
//
//                                ////////////////////////////////////////////
                                //create new user
                                FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("users").child(myUid).child("entertime").setValue(Timestamp);

                                        Intent intent = new Intent(PostContentActivity.this, GroupMessageActivity.class);
                                        intent.putExtra("destinationRoom", room_key);
                                        ActivityOptions activityOptions = null;
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                            activityOptions = ActivityOptions.makeCustomAnimation(PostContentActivity.this, R.anim.fromright, R.anim.toleft);
                                            startActivity(intent, activityOptions.toBundle());
                                        }
                                    }
                                });
//
                            } else if (dataSnapshot.exists()) {//이미 존재하는 채팅방에 들어갈 떄 내용을 싸그리 날리는 일이 없도록 수정중. 완료.
                                Log.v("태그2: ", "데이터스냅샷:" + String.valueOf(dataSnapshot));
                                Log.v("태그2: ", "방이 없다고 생각하고 싹 날리는 건가?");
                                chatModel.users.clear();
                                chatModel.users.put(myUid, true);

                                ////////////////
                                DatabaseReference mDatabase2 = FirebaseDatabase.getInstance().getReference(); //채팅방이 만들어졌을 때의 유저수를 구하기 가져오기 위해 사용
                                mDatabase2.child("posts").child(room_key).child("chatrooms").child("users")
//                                                    .orderByChild("timestamp")
//                                                    .equalTo(postmake_Time)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                                    user_getkey = childSnapshot.getKey();
//                                                                Log.v("태그2, 타임 스탬프: ", String.valueOf(postmake_Time));
                                                    Log.v("태그2, 키값 받아오기: ", user_getkey);
                                                    chatModel.users.put(user_getkey, true);
                                                    if (user_getkey.equals(myUid)) {
                                                        break; //대충 채팅방 유저 목록에 자기가 있는지 검사하고 있으면 바로 종료.
                                                    }
                                                } // for문 끝

                                                Log.v("태그3, if에 진입.: ", user_getkey);
                                                if (user_getkey.equals(myUid)) {//일단 이 부분은 이미 채팅방이 이미 만들어져 있고 해당 채팅방에 사용자의 id인 myUid가 이미 입력되어있을 때 적용
                                                    Intent intent = new Intent(PostContentActivity.this, GroupMessageActivity.class);
                                                    intent.putExtra("destinationRoom", room_key);
                                                    ActivityOptions activityOptions = null;
                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                        activityOptions = ActivityOptions.makeCustomAnimation(PostContentActivity.this, R.anim.fromright, R.anim.toleft);
                                                        startActivity(intent, activityOptions.toBundle());
                                                    }
                                                } else {//채팅방은 만들어져 있지만 해당 채팅방에 사용자의 id가 없을 때 적용. 현재 사용자의 id를 채팅방에 추가함. 즉 있던 채팅방에 참가가 되는 것.
                                                    if (peoplenum >= postPeople_Max_intent) {
                                                        Toast.makeText(PostContentActivity.this, "이미 인원이 최대치인 방입니다.", Toast.LENGTH_SHORT).show();
                                                    } else if (peoplenum < postPeople_Max_intent) {
                                                        Log.v("태그3, else if에 진입.: ", user_getkey);
                                                        Date PostDate = new Date();
                                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
                                                        final long Timestamp = PostDate.getTime();


                                                        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("uid").equalTo(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
                                                            @Override
                                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                                                                    Map<String, Object> map = (Map<String, Object>) messageSnapshot.getValue();
                                                                    if (myUid.equals((String) map.get("uid"))) {
//                                                                        postUser.setText((String)map.get("userName"));
                                                                        Map<String, Object> entermessage = new HashMap<>();
                                                                        entermessage.clear();
                                                                        entermessage.put("message", (String) map.get("userName") + " 님이 참가하셨습니다.");
                                                                        entermessage.put("timestamp", Timestamp);
                                                                        entermessage.put("uid", "SystemMessage");
                                                                        FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("comments").push().setValue(entermessage);
                                                                        entermessage.clear();
                                                                        entermessage.put("entertime", Timestamp);
//                                                                        FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("users").child(myUid).child("entertime").setValue(Timestamp);
                                                                        FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("users").child(myUid).updateChildren(entermessage).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                Intent intent = new Intent(PostContentActivity.this, GroupMessageActivity.class);
                                                                                intent.putExtra("destinationRoom", room_key);
                                                                                ActivityOptions activityOptions = null;
                                                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                                                                    activityOptions = ActivityOptions.makeCustomAnimation(PostContentActivity.this, R.anim.fromright, R.anim.toleft);
                                                                                    startActivity(intent, activityOptions.toBundle());
                                                                                }
                                                                            }
                                                                        });

                                                                    }
                                                                }
                                                            }

                                                            @Override
                                                            public void onCancelled(DatabaseError databaseError) {
                                                            }
                                                        });
                       //                                 FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("users").setValue(chatModel.users);

//                                                        FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("comments").push().child("message").setValue("abcde");
//                                                        FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("comments").push().child("timestamp").setValue(Timestamp);
//                                                        FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("comments").push().child("uid").setValue("SystemMessage");

//                                                        Intent intent = new Intent(PostContentActivity.this, GroupMessageActivity.class);
//                                                        intent.putExtra("destinationRoom", room_key);
//                                                        ActivityOptions activityOptions = null;
//                                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                                                            activityOptions = ActivityOptions.makeCustomAnimation(PostContentActivity.this, R.anim.fromright, R.anim.toleft);
//                                                            startActivity(intent, activityOptions.toBundle());
//                                                        }
                                                    }

                                                }
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    };
                    userNameRef.addListenerForSingleValueEvent(eventListener);
                    Log.v("태그999: ", " 여기까지 성공..");
                }


            }
        });

        ///////////////////게시글 수정 기능 작성중/////////////////////////////////
        Button Post_Modify_Button = (Button) findViewById(R.id.Post_Content_ModifyButton_Id);
        Post_Modify_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myUid.equals(postUser_intent)) {
                    Intent intent = new Intent(PostContentActivity.this, Post_Modify.class);
                    intent.putExtra("PostBoardValue", postBoardValue_intent);
                    intent.putExtra("PostTitle", postTitle_intent);
                    intent.putExtra("PostContent", postContent_intent);
                    intent.putExtra("PostPeopleMax", postPeople_Max_intent);
                    intent.putExtra("PostRegion", postRegion_intent);
                    intent.putExtra("PostTag", postTag_intent);
                    intent.putExtra("PostUid", postUser_intent);
                    intent.putExtra("PostCurrentPeople", peoplenum);

                    intent.putExtra("PostKey", room_key);
                    intent.putExtra("PostDate", postDate_intent);

                    Log.v("인텐트1", postBoardValue_intent);
                    Log.v("인텐트1", postTitle_intent);
                    Log.v("인텐트1", postContent_intent);
                    Log.v("인텐트1", String.valueOf(postPeople_Max_intent));
                    Log.v("인텐트1", postRegion_intent);
                    Log.v("인텐트1", postTag_intent);

                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(PostContentActivity.this, "게시자만 수정할 수 있습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //////////////////////////////////////////////////////////////////////////////
    }

    public void onBackPressed()//스마트폰의 뒤로가기 버튼 클릭시 이벤트
    {
        Intent WriteIntent = new Intent(PostContentActivity.this, Chattingboard.class);
        WriteIntent.putExtra("BoardValue", postBoardValue_intent);
        WriteIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //특정 액티비티를 호출할 떄 기존 리스트에 이미 액티비티가 호출된 상태라면 해당 액티비티를 새로 교체하고 그외의 액티비티를 모두 제거
        startActivity(WriteIntent);
        finish();
    }


}