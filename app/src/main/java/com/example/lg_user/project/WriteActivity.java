package com.example.lg_user.project;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lg_user.project.model.PostModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WriteActivity extends AppCompatActivity {

    public String BoardValue = null;


    public Button Write_Complete_Button;

    DatabaseReference WriteDatabase = FirebaseDatabase.getInstance().getReference();

    public EditText Title_Write;
    public EditText Content_Write;
    public EditText Region_Write;
    public EditText Tag_Write;
    public EditText People_Max_Write;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        Intent Reintent = this.getIntent();
        BoardValue = Reintent.getStringExtra("BoardValue");



        TextView Kind_of_Board_content = (TextView)findViewById(R.id.Kind_of_Board_Content_Id);
        if (BoardValue.toString().equals("study")){
            Log.v("태그", "스터디");
            Kind_of_Board_content.setText("일반학업");
        }
        else if (BoardValue.toString().equals("license")){
            Log.v("태그", "자격증");
            Kind_of_Board_content.setText("자격증");
        }
        else if (BoardValue.toString().equals("interview")){
            Log.v("태그", "면접");
            Kind_of_Board_content.setText("취업/면접");
        }

        Title_Write = (EditText)findViewById(R.id.Title_Write_Modify_Id);
        Content_Write = (EditText)findViewById(R.id.Contents_Write_Modify_Id);
        Region_Write = (EditText)findViewById(R.id.Region_Write_Modify_Id);
        Tag_Write = (EditText)findViewById(R.id.Tag_Write_Modify_Id);
        People_Max_Write = (EditText)findViewById(R.id.People_Max_Write_Modify_Id);




        Write_Complete_Button = (Button)findViewById(R.id.Modify_Complete_Button_Id);
        Write_Complete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getText().toString() == null 또는 '' 같은 식의 if 조건문은 작동을 안 한다.
                if(Title_Write.length() == 0 )
                {
                    Toast.makeText(WriteActivity.this, "글 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Content_Write.length() == 0){
                    Toast.makeText(WriteActivity.this, "글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(People_Max_Write.length() == 0){
                    Toast.makeText(WriteActivity.this, "최대 인원을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Region_Write.length() == 0){
                    Toast.makeText(WriteActivity.this, "만날 지역을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Tag_Write.length() == 0){
                    Toast.makeText(WriteActivity.this, "태그를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }

                else {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String Title_Write_v = Title_Write.getText().toString();
                    String Content_Write_v = Content_Write.getText().toString();
                    String Region_write_v = Region_Write.getText().toString();
                    String Tag_Write_v = Tag_Write.getText().toString();
                    int People_Max_Write_v = Integer.parseInt(People_Max_Write.getText().toString());


//                    Object time = ServerValue.TIMESTAMP;
//                    long unixTime = (long) time;
//                    Date PostDate = new Date(unixTime);
                    Date PostDate = new Date();
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");

                    long Timestamp = PostDate.getTime();


//
//                    String PostPushToken = FirebaseInstanceId.getInstance().getToken();
//                    Log.v("태그: ", PostPushToken);

                    WritePost(uid, BoardValue, Title_Write_v, Content_Write_v, Tag_Write_v, Region_write_v, People_Max_Write_v, Timestamp);


                    String PostPushToken = FirebaseInstanceId.getInstance().getToken();
                    Intent WriteIntent = new Intent(WriteActivity.this, Chattingboard.class);
                    WriteIntent.putExtra("BoardValue", BoardValue);


                    Log.v("태그", "extra까진 이상 없나");
                    startActivity(WriteIntent);
                    finish();
                }
            }
        });
    }


//("posts").child(uid).setVBalue.... 대충 이 상태면 글 추가 없이 글 수정이 되더라.
    public void WritePost(String uid, String BoardValue, String PostTitle, String PostContent, String PostTag, String PostRegion, int PostPeople_Max, Long Timestamp){
        PostModel postwritemodel = new PostModel(uid, BoardValue, PostTitle, PostContent, PostTag, PostRegion, PostPeople_Max, Timestamp);
        FirebaseDatabase.getInstance().getReference().child("posts").push().setValue(postwritemodel).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

//                passPushTokenToServer();;//일단 글 목록 표기되게 하기 전까지는 임의로.
            }
        });
    }

    public void onBackPressed()//스마트폰의 뒤로가기 버튼 클릭시 이벤트
    {
        Intent WriteIntent = new Intent(WriteActivity.this, Chattingboard.class);
        WriteIntent.putExtra("BoardValue", BoardValue);
        Log.v("태그", "extra까진 이상 없나");
        startActivity(WriteIntent);
        finish();
    }
    void passPushTokenToServer(){

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> map = new HashMap<>();
        map.put("PostpushToken",token);
        Log.v("태그(uid token)", uid + "    " + token);
        FirebaseDatabase.getInstance().getReference().child("posts").child(uid).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                finish();//일단 글 목록 표기되게 하기 전까지는 임의로.
            }
        });
    }


}
