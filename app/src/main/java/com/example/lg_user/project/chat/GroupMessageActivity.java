package com.example.lg_user.project.chat;

import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.lg_user.project.LoginActivity;
import com.example.lg_user.project.MainActivity;
import com.example.lg_user.project.PostContentActivity;
import com.example.lg_user.project.R;
import com.example.lg_user.project.SelectPurposeActivity;
import com.example.lg_user.project.SoftKeyboardDectectorView;
import com.example.lg_user.project.model.ChatModel;
import com.example.lg_user.project.model.NotificationModel;
import com.example.lg_user.project.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.security.acl.Group;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.lg_user.project.R.id.groupMessageActivity_recyclerview;
import static com.example.lg_user.project.R.id.time;

public class GroupMessageActivity extends AppCompatActivity {
    Map<String, UserModel> users = new HashMap<>();
    String destinationRoom;
    String uid;
    EditText editText;
    String enterTime;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;

    private RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm");
    List<ChatModel.Comment> comments = new ArrayList<>();

    int peopleCounter = 0;

    Button chatRooms_menu_button;

    LinearLayout linearLayout;
    TextView chatroom_Title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_message);
        destinationRoom = getIntent().getStringExtra("destinationRoom");
        Log.v("태그:그룹메시지의 데스티네이션룸", destinationRoom);//chatrooms 바로 아래의 푸쉬값들
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

  //      getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN); //에딧텍스트 클릭으로 입력화면 출력시 나머지 화면을 위로 밀려가게 만들어줌.
 //       getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED);
  //    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

//
        linearLayout = (LinearLayout)findViewById(R.id.LinearLayout_Id); //키보드가 등장했을 때를 알아채는 클래스를 작성해서 사용
        final SoftKeyboardDectectorView softKeyboardDecector = new SoftKeyboardDectectorView(this);
        addContentView(softKeyboardDecector, new FrameLayout.LayoutParams(-1, -1));
        softKeyboardDecector.setOnShownKeyboard(new SoftKeyboardDectectorView.OnShownKeyboardListener() {
            @Override
            public void onShowSoftKeyboard() {
                //키보드 등장할 때
//                linearLayout.setVisibility(View.INVISIBLE);
                recyclerView.scrollToPosition(comments.size() - 1);
            }
        });
        softKeyboardDecector.setOnHiddenKeyboard(new SoftKeyboardDectectorView.OnHiddenKeyboardListener() {
            @Override
            public void onHiddenSoftKeyboard() {
                // 키보드 사라질 때
//                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        chatroom_Title = (TextView)findViewById(R.id.Chatrooms_Title_Id);
        FirebaseDatabase.getInstance().getReference().child("posts").orderByChild("posttitle").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) messageSnapshot.getValue();
                    if (destinationRoom.equals(messageSnapshot.getKey())) {
                        chatroom_Title.setText((String) map.get("posttitle"));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        chatRooms_menu_button = (Button)findViewById(R.id.Chatrooms_MenuButton_Id);//클릭시 메뉴화면 출력
        chatRooms_menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                PopupMenu chatrooms_menu = new PopupMenu(GroupMessageActivity.this, chatRooms_menu_button);
                chatrooms_menu.getMenuInflater().inflate(R.menu.chatrooms_menu, chatrooms_menu.getMenu());

                chatrooms_menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getTitle().equals("대화방 나가기")) {
                            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(GroupMessageActivity.this);
                            alert_confirm.setMessage("방에서 나가시겠습니까?\n이후 방에 재입장하시더라도 그 이전의 메시지는 볼 수 없습니다.").setCancelable(false).setPositiveButton("아니요",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            return;
                                        }
                                    }).setNegativeButton("네",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("users").child(uid).removeValue();

                                            Intent intent = new Intent(GroupMessageActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                            AlertDialog alert = alert_confirm.create();
                            alert.show();
                        }
                        return true;
                    }
                });
                chatrooms_menu.show();
            }
        });


        FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("users").child(uid).child("entertime").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                    enterTime = String.valueOf(dataSnapshot.getValue());
                    Log.v("시간 태그", enterTime);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        }); //사용자가 해당 대화방에 참여를 시작한 시간. 나중에 이 시간보다 이전에 나온 메시지는 출력되지 않도록 할 예정.

        editText = (EditText) findViewById(R.id.groupMessageActivity_editText);



        FirebaseDatabase.getInstance().getReference().child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot item : dataSnapshot.getChildren()) {
                    users.put(item.getKey(), item.getValue(UserModel.class));
                }
                init();
                recyclerView = (RecyclerView) findViewById(R.id.groupMessageActivity_recyclerview);
                recyclerView.setAdapter(new GroupMessageRecyclerViewAdapter());
                recyclerView.setLayoutManager(new LinearLayoutManager(GroupMessageActivity.this));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    void init() {
        Button button = (Button) findViewById(R.id.groupMessageActivity_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel.Comment comment = new ChatModel.Comment();
                comment.uid = uid;
                comment.message = editText.getText().toString();
                comment.timestamp = ServerValue.TIMESTAMP;

                if (comment.message.equals("")) {
                } else {
                    FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Map<String, Boolean> map = (Map<String, Boolean>) dataSnapshot.getValue();

                                    for (String item : map.keySet()) {

                                        if (item.equals(uid)) {//자기자신에게는 메시지 알람을 보낼필요가 없으니 무시
                                            Log.v("알림 태그, 통과했네?", uid + " : " + item);
                                            continue;
                                        }
                                        else {
                                            sendGcm(users.get(item).pushToken);
                                            Log.v("알림 태그, 못 통과?", uid + " : " + item);
                                        }
                                    }
                                    editText.setText("");
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    });
                }
            }
        });

    }

    void sendGcm(String pushToken) {

        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = pushToken;
        notificationModel.notification.title = userName;
        notificationModel.notification.text = editText.getText().toString();
        notificationModel.data.title = userName;
        notificationModel.data.text = editText.getText().toString();


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"), gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AIzaSyCh05Z1fDyhcQCrb095Ge5_r5BwxCS5Ick")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });


    }

    class GroupMessageRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public GroupMessageRecyclerViewAdapter() {
            getMessageList();
        }

        void getMessageList() {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("comments");
            valueEventListener = databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    comments.clear();
                    Map<String, Object> readUsersMap = new HashMap<>();
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
//                        Map<String, Object> map = (Map<String, Object>) item.getValue();
                        Map<String, Long> map = (Map<String, Long>) item.getValue();

                        Long timestamp = map.get("timestamp");
                        Log.v("롱 스트링 엔터타임", String.valueOf(Long.parseLong(enterTime)));
//                        Log.v("롱 스트링 타임스탬프", String.valueOf(Long.parseLong(timestamp)));
                        Log.v("롱 스트링 타임스탬프", String.valueOf(timestamp));
                        if (Long.parseLong(enterTime) <= timestamp) //유저가 방에 입장한 후의 메시지들만 저장.
                        {
                            String key = item.getKey();
                            ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                            ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                            comment_motify.readUsers.put(uid, true);

                            readUsersMap.put(key, comment_motify);
                            comments.add(comment_origin);
                        }
//                        String key = item.getKey();
//                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
//                        ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
//                        comment_motify.readUsers.put(uid, true);
//
//                        readUsersMap.put(key, comment_motify);
//                        comments.add(comment_origin);

                    }
                    if (comments.size() == 0) {
                        return;
                    } else if (!comments.get(comments.size() - 1).readUsers.containsKey(uid)) {
                        FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("comments")
                                .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size() - 1);
                            }
                        });
                    } else {
                        notifyDataSetChanged();
                        recyclerView.scrollToPosition(comments.size() - 1);
                    }
                    //메세지가 갱신
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);


            return new GroupMessageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            GroupMessageViewHolder messageViewHolder = ((GroupMessageViewHolder) holder);


            if (comments.get(position).uid.equals("SystemMessage")) {//다른 사람의 참가 등의 메시지.

                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.systemmessage_bar);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(10);
                messageViewHolder.linearLayout_main.setGravity(Gravity.CENTER);
                messageViewHolder.textView_timestamp.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_readCounter_right.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_readCounter_left.setVisibility(View.INVISIBLE);
//                setReadCounter(position,messageViewHolder.textView_readCounter_right);
            } else if (comments.get(position).uid.equals(uid)) {//내가보낸 메세지
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.rightbubble);
                messageViewHolder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_message.setTextSize(12);
                messageViewHolder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(position, messageViewHolder.textView_readCounter_right);
                messageViewHolder.textView_readCounter_left.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_timestamp.setVisibility(View.VISIBLE);
            }
            //상대방이 보낸 메세지
            else {

                Glide.with(holder.itemView.getContext())
                        .load(users.get(comments.get(position).uid).profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHolder.imageView_profile);
                messageViewHolder.textview_name.setText(users.get(comments.get(position).uid).userName);
                messageViewHolder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHolder.textView_message.setBackgroundResource(R.drawable.leftbubble);
                messageViewHolder.textView_message.setText(comments.get(position).message);
                messageViewHolder.textView_message.setTextSize(12);
                messageViewHolder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(position, messageViewHolder.textView_readCounter_left);
                messageViewHolder.textView_readCounter_right.setVisibility(View.INVISIBLE);
                messageViewHolder.textView_timestamp.setVisibility(View.VISIBLE);

            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            messageViewHolder.textView_timestamp.setText(time);

        }

        void setReadCounter(final int position, final TextView textView) {
            if (peopleCounter == 0) {
                FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Map<String, Boolean> users = (Map<String, Boolean>) dataSnapshot.getValue();//읽은 사람들 수 세기
                        peopleCounter = users.size();
                        int count = peopleCounter - comments.get(position).readUsers.size();
                        if (count > 0) {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText(String.valueOf(count));
                        } else if (comments.get(position).uid.equals("SystemMessage")) {
                            textView.setVisibility(View.INVISIBLE);
                        } else {
                            textView.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                int count = peopleCounter - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));
                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        private class GroupMessageViewHolder extends RecyclerView.ViewHolder {

            public TextView textView_message;
            public TextView textview_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_timestamp;
            public TextView textView_readCounter_left;
            public TextView textView_readCounter_right;

            public GroupMessageViewHolder(View view) {
                super(view);
                textView_message = (TextView) view.findViewById(R.id.messageItem_textView_message);
                textview_name = (TextView) view.findViewById(R.id.messageItem_textview_name);
                imageView_profile = (ImageView) view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_destination = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_destination);
                linearLayout_main = (LinearLayout) view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timestamp = (TextView) view.findViewById(R.id.messageItem_textview_timestamp);
                textView_readCounter_left = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_left);
                textView_readCounter_right = (TextView) view.findViewById(R.id.messageItem_textview_readCounter_right);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatrooms_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.Chatrooms_Exit_Id:
                final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                AlertDialog.Builder alert_confirm = new AlertDialog.Builder(GroupMessageActivity.this);
                alert_confirm.setMessage("방에서 나가시겠습니까?\n이후 방에 재입장하시더라도 그 이전의 메시지는 볼 수 없습니다.").setCancelable(false).setPositiveButton("아니요",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).setNegativeButton("네",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                                Date PostDate = new Date();
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
                                final long Timestamp = PostDate.getTime();
                                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

                                FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("users").child(uid).removeValue(); //채팅방 참여 유저목록에서 제거


                                Map<String, Object> entermessage = new HashMap<>();
                                entermessage.clear();
                                entermessage.put("message", " 님이 퇴장하셨습니다.");
                                entermessage.put("timestamp", Timestamp);
                                entermessage.put("uid", "SystemMessage");
                                FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("comments").push().setValue(entermessage);


                                Log.v("왜 안들어가", String.valueOf(entermessage.get("Message")));
                                FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("comments").push().setValue("uid", "SystemMessage");
                                FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("comments").push().setValue("timestamp", Timestamp);
                                FirebaseDatabase.getInstance().getReference().child("posts").child(destinationRoom).child("chatrooms").child("comments").push().setValue("message", "퇴장했습니다.").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Intent intent = new Intent(GroupMessageActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }
                        });
                AlertDialog alert = alert_confirm.create();
                alert.show();
        }
        return true;
    }
}
