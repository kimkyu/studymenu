package com.example.lg_user.project;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.lg_user.project.model.PostModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Chattingboard extends AppCompatActivity {

    public TextView BoardName;
    public EditText Region_Search;
    public String region_Search_String;
    public EditText Tag_Search;
    public String tag_Search_String;

    public Button Write_Button;

    String BoardValue_Intent = null;//학업, 면접, 자격증 등을 구별

    private SimpleDateFormat ChattingBoard_DateFormat = new SimpleDateFormat("yyyy.MM.dd");


    public static ArrayList<PostModel> Origins = new ArrayList<PostModel>();

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mConditionRef = mDatabase.child("posts").child("Timestamp");

    ArrayList<PostModel> list = new ArrayList<PostModel>(); //입력받은 데이터들을 리사이클러뷰에 출력하기 위한 변수.
    ArrayList<PostModel> search_List = new ArrayList<>();

    Context context;
    RecyclerView recyclerview_ranking;
    Post_RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView.LayoutManager recylerViewLayoutManager;


    //////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chattingboard);


        Log.v("Logtag", "게시판 진입");

        Intent Reintent = this.getIntent();
        BoardValue_Intent = Reintent.getStringExtra("BoardValue");
        if (BoardValue_Intent != null) {
            Log.v("태그", "가져온 게시판 분류값 = " + BoardValue_Intent.toString());
        } else {
            Log.e("태그", "가져온 게시판 분류값이 없습니다.");
        }

        BoardName = (TextView) findViewById(R.id.Chattingboardname_ID);
        // == 는 주소값을 비교하므로 사용할 수 없고 equals을 쓰자.
        if (BoardValue_Intent.toString().equals("study")) {
            BoardName.setText("일반 학업");
        } else if (BoardValue_Intent.toString().equals("license")) {
            BoardName.setText("자격증");
        } else if (BoardValue_Intent.toString().equals("interview")) {
            BoardName.setText("취업/면접");
        } else {
            Log.v("태그", "시발");
        }

        Tag_Search = (EditText) findViewById(R.id.Tag_Search_Id);
        Region_Search = (EditText) findViewById(R.id.Region_Search_Id);
        Region_Search.setHint(" ex)창원");
        if (BoardValue_Intent.toString().equals("study")) {
            Tag_Search.setHint(" ex)영어");
        } else if (BoardValue_Intent.toString().equals("license")) {
            Tag_Search.setHint("ex)정보통신기사");
        } else if (BoardValue_Intent.toString().equals("interview")) {
            Tag_Search.setHint("ex)삼성전자");
        }

        Write_Button = (Button) findViewById(R.id.Write_Button_Id);

        Write_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent WriteIntent = new Intent(Chattingboard.this, WriteActivity.class);
                WriteIntent.putExtra("BoardValue", BoardValue_Intent);
                Log.v("태그", "extra까진 이상 없나");
                startActivity(WriteIntent);
                finish();
            }
        });
        Post_View();


/////////////////////////// 게시글 검색 기능 구현 중. 완료.///////////////////////////////////////////////////
        Button searchButton = (Button) findViewById(R.id.Search_Button_Id);
        View.OnClickListener search = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tag_Search_String = Tag_Search.getText().toString();
                region_Search_String = Region_Search.getText().toString();
                search_List.clear();

                Collections.sort(list, PostModel.Post_mulitplessort);
                Log.v("리스트뷰 내용", String.valueOf(list.size()));
                if (tag_Search_String.equals("") && region_Search_String.equals("")) {
                    //둘 다 비어 있으면 전체글 갱신.
                    Log.v("태그5", "둘 다 비어있음");
                    Intent refresh = new Intent(Chattingboard.this, Chattingboard.class);
                    refresh.putExtra("BoardValue", BoardValue_Intent);
                    startActivity(refresh);
                    finish();
                } else if (region_Search_String.equals("")) { //태그만 입력되어 있을 때
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).posttag.contains(tag_Search_String)) {
                            search_List.add(list.get(i));
                        }
                        Log.v("리스트뷰 내용", String.valueOf(list.get(i).posttag));
                    }
                    Collections.sort(search_List, PostModel.Post_mulitplessort);
                } else if (tag_Search_String.equals("")) { //지역만 입력되어 있을 때
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).postregion.contains(region_Search_String)) {
                            search_List.add(list.get(i));
                        }
                        Log.v("리스트뷰 내용", String.valueOf(list.get(i).postregion));
                    }
                } else if (!tag_Search_String.equals("") && !region_Search_String.equals("")) { //지역과 태그 둘 다 입력되어 있을 때
                    for (int i = 0; i < list.size(); i++) {
                        if (list.get(i).postregion.contains(region_Search_String) && list.get(i).posttag.contains(tag_Search_String)) {
                            search_List.add(list.get(i));
                        }
                        Log.v("리스트뷰 내용", String.valueOf(list.get(i).postregion) + " / " + String.valueOf(list.get(i).posttag));
                    }
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////////
                context = getApplicationContext();
                recyclerview_ranking = (RecyclerView) findViewById(R.id.Post_RecyclerView_Id);
                recylerViewLayoutManager = new LinearLayoutManager(getBaseContext());

                recyclerview_ranking.setLayoutManager(recylerViewLayoutManager);
                recyclerViewAdapter = new Post_RecyclerViewAdapter(context, search_List);
                recyclerview_ranking.setAdapter(recyclerViewAdapter);

                final DatabaseReference testing = FirebaseDatabase.getInstance().getReference("posts");
                testing.orderByChild("Timestamp").addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {


                        ///////정렬 테스트 중 //////
                        Collections.sort(search_List, PostModel.Post_mulitplessort); //정렬. PostModel 함수 참조. 지금은 timestamp 역순으로 정렬중.

                        ///////////정렬 테스트 끝//////
                        recyclerViewAdapter.notifyDataSetChanged();
                        Origins.clear();
                        Origins.addAll(recyclerViewAdapter.getSubjectvalues());
                        //원래 시작때는 3구 4구 구별 없이 모든 데이터를 뿜어내서 랭킹 화면을 들어가자마자 구별할 수 있도록(기본 3구) 아래 코드를 만든건데
                        final Handler mhandler = new Handler();
                        TimerTask tt = new TimerTask() {
                            @Override
                            public void run() {
                                mhandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayList<PostModel> arrayList = recyclerViewAdapter.getSubjectvalues();

                                        ArrayList<PostModel> arrayList_match = new ArrayList<>();

                                        for (int k = 0; k < arrayList.size(); k++) {
                                            if (arrayList.get(k).boardvalue == null) { //제일 첫번쨰 글이 오류나는 현상. 그냥 잡데이터 넣어서 땜빵해야겠따.
                                                continue;
                                            } else if (arrayList.get(k).boardvalue.equals(BoardValue_Intent)) { //학업, 자격증, 면접 등 게시판과 일치하는 글만 보여줌
                                                arrayList_match.add(arrayList.get(k));
                                            }

//                                    arrayList_match.add(arrayList.get(k));
                                        }
                                        recyclerViewAdapter.setSubjectvalues(arrayList_match);
                                        String tmp = "";
                                        recyclerViewAdapter.notifyDataSetChanged();


                                    }
                                });
                            }
                        };
                        Timer timer = new Timer();
                        timer.schedule(tt, 0000);
                        Log.d("태그", "여기도 null?: " + dataSnapshot.getKey() + " , " + dataSnapshot.getValue().toString());
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }; //검색기능 구현 내용
        searchButton.setOnClickListener(search);

    }

    public void listsetup() //화면 초기화를 위해 사용
    {
        recyclerview_ranking.setLayoutManager(recylerViewLayoutManager);
        recyclerViewAdapter = new Post_RecyclerViewAdapter(context, Origins); //최초 이벤트시에 받아놓은 타입 구별없는 원본이 저장되었던 데이터(Origins)를 받아옴.
        recyclerview_ranking.setAdapter(recyclerViewAdapter);


        final DatabaseReference testing = FirebaseDatabase.getInstance().getReference("posts");
        testing.orderByChild("Timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                PostModel ranktest = dataSnapshot.getValue(PostModel.class);

                recyclerViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////

    /////////////////////////////////////////////////////////////////

    public void Post_View()
    {
        context = getApplicationContext();
        recyclerview_ranking = (RecyclerView) findViewById(R.id.Post_RecyclerView_Id);
        recylerViewLayoutManager = new LinearLayoutManager(getBaseContext());

        recyclerview_ranking.setLayoutManager(recylerViewLayoutManager);
        recyclerViewAdapter = new Post_RecyclerViewAdapter(context, list);
        recyclerview_ranking.setAdapter(recyclerViewAdapter);

        final DatabaseReference testing = FirebaseDatabase.getInstance().getReference("posts");
        testing.orderByChild("Timestamp").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Log.d("_test", dataSnapshot.getKey() + " , " + dataSnapshot.getValue().toString());

                PostModel ranktest = dataSnapshot.getValue(PostModel.class);

                if (ranktest == null) {
                    Log.d("_teststart", "null: " + dataSnapshot.getKey() + " , " + dataSnapshot.getValue().toString());
                    return;
                }

                list.add(ranktest);//time순서로 정렬된 자료들을 list에 저장.
                Log.d("_testend", "null: " + dataSnapshot.getKey() + " , " + dataSnapshot.getValue().toString());


                ///////정렬 테스트 중 //////

                Collections.sort(list, PostModel.Post_mulitplessort); //정렬. PostModel 함수 참조. 지금은 timestamp 역순으로 정렬중.


                ///////////정렬 테스트 끝//////
                recyclerViewAdapter.notifyDataSetChanged();
                Origins.clear();
                Origins.addAll(recyclerViewAdapter.getSubjectvalues());
                //원래 시작때는 3구 4구 구별 없이 모든 데이터를 뿜어내서 랭킹 화면을 들어가자마자 구별할 수 있도록(기본 3구) 아래 코드를 만든건데
                //지금 생각해도 대체 왜 handler와 timetask를 이용해야 제대로 출력이 된건지 알 수가 없다. 이것들을 사용하지 않으면 아예 어떤 데이터도 안 뜬다.
                //이 바로 위 코드들이 통합 데이터 출력인데도 말이다...
                final Handler mhandler = new Handler();
                TimerTask tt = new TimerTask() {
                    @Override
                    public void run() {
                        mhandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listsetup();
                                ArrayList<PostModel> arrayList = recyclerViewAdapter.getSubjectvalues();

                                ArrayList<PostModel> arrayList_match = new ArrayList<>();

                                for (int k = 0; k < arrayList.size(); k++) {
                                    if (arrayList.get(k).boardvalue == null) { //제일 첫번쨰 글이 오류나는 현상. 그냥 잡데이터 넣어서 땜빵해야겠따.
                                        continue;
                                    } else if (arrayList.get(k).boardvalue.equals(BoardValue_Intent)) { //학업, 자격증, 면접 등 게시판과 일치하는 글만 보여줌
                                        arrayList_match.add(arrayList.get(k));
                                    }

//                                    arrayList_match.add(arrayList.get(k));
                                }
                                recyclerViewAdapter.setSubjectvalues(arrayList_match);
                                String tmp = "";
                                recyclerViewAdapter.notifyDataSetChanged();


                            }
                        });
                    }
                };
                Timer timer = new Timer();
                timer.schedule(tt, 0000);
                Log.d("태그", "여기도 null?: " + dataSnapshot.getKey() + " , " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void onBackPressed()//스마트폰의 뒤로가기 버튼 클릭시 이벤트
    {
        Intent WriteIntent = new Intent(Chattingboard.this, MainActivity.class);
        startActivity(WriteIntent);
        finish();
    }


}

