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

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Post_Modify extends AppCompatActivity {

    public String BoardValue = null;
    public String posttitle;
    public String postcontent;
    public int postpeoplemax;
    public String postregion;
    public String posttag;
    public String postuseruid;

    public int postcurrentpeople_num;
    public String postkey;
    public long postdate;


    public Button Modify_Complete_Button;

    DatabaseReference WriteDatabase = FirebaseDatabase.getInstance().getReference();

    public EditText Title_Write_Modify;
    public EditText Content_Write_Modify;
    public EditText Region_Write_Modify;
    public EditText Tag_Write_Modify;
    public EditText People_Max_Write_Modify;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);

        Intent Reintent = this.getIntent();
        BoardValue = Reintent.getStringExtra("PostBoardValue");
        posttitle = Reintent.getStringExtra("PostTitle");
        postcontent = Reintent.getStringExtra("PostContent");
        postpeoplemax = Reintent.getIntExtra("PostPeopleMax", 0);
        postregion = Reintent.getStringExtra("PostRegion");
        posttag = Reintent.getStringExtra("PostTag");
        postuseruid = Reintent.getStringExtra("PostUid");
        postdate = Reintent.getLongExtra("PostDate", 0);

        postcurrentpeople_num = Reintent.getIntExtra("PostCurrentPeople", 0);

        postkey = Reintent.getStringExtra("PostKey");

        Log.v("인텐트2", BoardValue);
        Log.v("인텐트2", posttitle);
        Log.v("인텐트2", postcontent);
        Log.v("인텐트2", String.valueOf(postpeoplemax));
        Log.v("인텐트2", postregion);
        Log.v("인텐트2", posttag);

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


        Title_Write_Modify = (EditText)findViewById(R.id.Title_Write_Modify_Id);
        Title_Write_Modify.setText(posttitle);
        Content_Write_Modify = (EditText)findViewById(R.id.Contents_Write_Modify_Id);
        Content_Write_Modify.setText(postcontent);
        Region_Write_Modify = (EditText)findViewById(R.id.Region_Write_Modify_Id);
        Region_Write_Modify.setText(postregion);
        Tag_Write_Modify = (EditText)findViewById(R.id.Tag_Write_Modify_Id);
        Tag_Write_Modify.setText(posttag);
        People_Max_Write_Modify = (EditText)findViewById(R.id.People_Max_Write_Modify_Id);
        People_Max_Write_Modify.setText(String.valueOf(postpeoplemax));

        TextView boardname = (TextView)findViewById(R.id.Write_Board_Name_Id);
        boardname.setText("게시글 수정");//이상하게 xml에서 변경이 안되서 일단 여기서 수정.


        Modify_Complete_Button = (Button)findViewById(R.id.Modify_Complete_Button_Id);
        Modify_Complete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //getText().toString() == null 또는 '' 같은 식의 if 조건문은 작동을 안 한다.
                if(Title_Write_Modify.length() == 0 )
                {
                    Toast.makeText(Post_Modify.this, "글 제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Content_Write_Modify.length() == 0){
                    Toast.makeText(Post_Modify.this, "글 내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(People_Max_Write_Modify.length() == 0){
                    Toast.makeText(Post_Modify.this, "최대 인원을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Region_Write_Modify.length() == 0){
                    Toast.makeText(Post_Modify.this, "만날 지역을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(Tag_Write_Modify.length() == 0){
                    Toast.makeText(Post_Modify.this, "태그를 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
                else if(postcurrentpeople_num > Integer.parseInt(People_Max_Write_Modify.getText().toString())){
                    Toast.makeText(Post_Modify.this, "이미 참가중인 인원이 수정하려는 최대치 인원수보다 많습니다.", Toast.LENGTH_SHORT).show();
                }

                else {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    String Title_Write_v = Title_Write_Modify.getText().toString();
                    String Content_Write_v = Content_Write_Modify.getText().toString();
                    String Region_write_v = Region_Write_Modify.getText().toString();
                    String Tag_Write_v = Tag_Write_Modify.getText().toString();
                    int People_Max_Write_v = Integer.parseInt(People_Max_Write_Modify.getText().toString());


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
                    Intent modifyIntent = new Intent(Post_Modify.this, PostContentActivity.class);

                    modifyIntent.putExtra("PostTitle", Title_Write_v); //수정된 내용
                    modifyIntent.putExtra("PostUser", postuseruid);
                    modifyIntent.putExtra("PostPeopleMax", People_Max_Write_v);
                    modifyIntent.putExtra("PostDate", postdate);
                    modifyIntent.putExtra("PostRegion", Region_write_v);
                    modifyIntent.putExtra("PostTag", Tag_Write_v);
                    modifyIntent.putExtra("PostContent", Content_Write_v);
                    modifyIntent.putExtra("PostBoardValue", BoardValue);



//                    modifyIntent.putExtra("PostCurrentPeople", peoplenum);

                    modifyIntent.putExtra("PostKey", postkey);
                    Log.v("태그", "extra까진 이상 없나");
                    startActivity(modifyIntent);
                    finish();
                }
            }
        });
    }


    //("posts").child(uid).setVBalue.... 대충 이 상태면 글 추가 없이 글 수정이 되더라.
    public void WritePost(String uid, String BoardValue, String PostTitle, String PostContent, String PostTag, String PostRegion, int PostPeople_Max, Long Timestamp){
        PostModel postwritemodel = new PostModel(uid, BoardValue, PostTitle, PostContent, PostTag, PostRegion, PostPeople_Max, Timestamp);
        FirebaseDatabase.getInstance().getReference().child("posts").child(postkey).child("postcontent").setValue(postwritemodel.postcontent);
        FirebaseDatabase.getInstance().getReference().child("posts").child(postkey).child("postpeople_max").setValue(postwritemodel.postpeople_max);
        FirebaseDatabase.getInstance().getReference().child("posts").child(postkey).child("postregion").setValue(postwritemodel.postregion);
        FirebaseDatabase.getInstance().getReference().child("posts").child(postkey).child("posttag").setValue(postwritemodel.posttag);
        FirebaseDatabase.getInstance().getReference().child("posts").child(postkey).child("posttitle").setValue(postwritemodel.posttitle);
    }

    public void onBackPressed()//스마트폰의 뒤로가기 버튼 클릭시 이벤트
    {
        Intent WriteIntent = new Intent(Post_Modify.this, Chattingboard.class);
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
