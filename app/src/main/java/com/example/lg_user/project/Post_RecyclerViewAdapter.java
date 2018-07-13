package com.example.lg_user.project;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.lg_user.project.chat.MessageActivity;
import com.example.lg_user.project.model.PostModel;
import com.example.lg_user.project.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;



public class Post_RecyclerViewAdapter extends RecyclerView.Adapter<Post_RecyclerViewAdapter.ViewHolder>{

    List<String> items;

    ArrayList<PostModel> subjectvalues = new ArrayList<PostModel>();
    Context context;
    View view1;
    ViewHolder viewHolder1;

    public String name;
    public String room_key;
    public int peoplenum;

    private UserModel destinationUserModel;
    private String destinatonUid;




    public ArrayList<PostModel> getSubjectvalues() {
        return subjectvalues;
    }

    public void setSubjectvalues(ArrayList<PostModel> subjectvalues) {
        this.subjectvalues = subjectvalues;
    }

    public Post_RecyclerViewAdapter(Context context1, ArrayList<PostModel> SubjectValues1){

        subjectvalues = SubjectValues1;
        context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView Post_Title;
        public TextView Post_User;
        public TextView Post_People_Max;
        public TextView Post_Date;
        public TextView Post_Region;
        public TextView Post_Tag;
        public TextView Post_Token;
        public TextView Post_Content;
        public TextView Post_BoardValue;


        public RelativeLayout Post_Relativelayout;



        public ViewHolder(View v){
            super(v);
            Post_Title = (TextView)v.findViewById(R.id.Post_Title_id);
            Post_User = (TextView)v.findViewById(R.id.Post_User_Id);
            Post_People_Max = (TextView)v.findViewById(R.id.Post_People_Max_Id);
            Post_Date = (TextView)v.findViewById(R.id.Post_Date_Id);
            Post_Region = (TextView)v.findViewById(R.id.Post_Region_Id);
            Post_Tag = (TextView)v.findViewById(R.id.Post_Tag_Id);

//            Post_Content = (TextView)v.findViewById(R.id.Post_Content_Id);
//            Post_BoardValue = (TextView)v.findViewById(R.id.Post_BoardValue_Id);
//            Post_Token = (TextView)v.findViewById(R.id.Post_Token_Id);

            Post_Relativelayout = (RelativeLayout)v.findViewById(R.id.item_post_layout_id);
        }
    }

    @Override
    public Post_RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        view1 = LayoutInflater.from(context).inflate(R.layout.item_post,parent,false);
        viewHolder1 = new ViewHolder(view1);



        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){

        final PostModel rank_base = subjectvalues.get(position);


        String Post_Title_String = rank_base.posttitle;

//        String Post_User_String = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        final String Post_User_String = rank_base.uid;
        //////////////////////////////////
        FirebaseDatabase.getInstance().getReference().child("users").orderByChild("uid").equalTo(Post_User_String).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.v("태그 포지션" + position, dataSnapshot.getKey() + " , " + dataSnapshot.getValue().toString());
                for (DataSnapshot messageSnapshot: dataSnapshot.getChildren()) {
                    Map<String, Object> map = (Map<String, Object>) messageSnapshot.getValue();
                    if(Post_User_String.equals((String)map.get("uid"))){
                        name = (String)map.get("userName");
                        break;
                    }
                }
                holder.Post_User.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
///////////////////////////////////////////////////////
        final String Post_People_Max_String = String.valueOf(rank_base.postpeople_max);//post_model.PostPeople_Max;
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("posts")
                .orderByChild("timestamp")
                .equalTo(rank_base.timestamp)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            room_key = childSnapshot.getKey();
                        }
                        Log.v("태그: ", " if시작");
                        if(room_key == null)
                        {
                        }
                        else {
                            Log.v("태그: ", " 룸키 있음." + room_key);
                            FirebaseDatabase.getInstance().getReference().child("posts").child(room_key).child("chatrooms").child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                                        peoplenum++;
                                    }
                                    Log.v("태그: ", "if끝, peoplenum=" + peoplenum);
                                    holder.Post_People_Max.setText("참가 인원: " + String.valueOf(peoplenum) + "/" + Post_People_Max_String);
                                    peoplenum = 0; //peoplenum 값이 데이터가 많을수록 누적되어서 초기화해주는 것.
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


///////////////////////////////////////////////////////////////////
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd");
        Date date = new Date(rank_base.timestamp);
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String Post_Date_String = simpleDateFormat.format(date);
//        String Post_Date_String = "" + rank_base.timestamp;
        String Post_Region_String = rank_base.postregion;
        String Post_Tag_String= rank_base.posttag;

//        String Post_Token_String = rank_base.postpushtoken;
        String Post_BoardValue_String = rank_base.boardvalue;
        String Post_Content_String = rank_base.postcontent;

//        Log.v("리사이클러뷰 태그", Post_Title_String + ", " + Post_User_String + ", "+ Post_People_Max_String +", " + Post_Date_String +  ", " +Post_Region_String+ ", " + Post_Tag_String+ ", " + Post_Token_String + ", " + Post_BoardValue_String + ", " + Post_Content_String);

        holder.Post_Title.setText(Post_Title_String);
//        holder.Post_User.setText(Post_User_String);

//        holder.Post_People_Max.setText(Post_People_Max_String);
        holder.Post_Date.setText(Post_Date_String);
        holder.Post_Region.setText(Post_Region_String);
        holder.Post_Tag.setText(Post_Tag_String);
//        holder.Post_Token.setText(Post_Token_String);
//        holder.Post_BoardValue.setText(Post_BoardValue_String);
//        holder.Post_Content.setText(Post_Content_String);


        holder.Post_Relativelayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                Toast.makeText(context,rank_base.posttitle, Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context,PostContentActivity.class);
                intent.putExtra("PostTitle", rank_base.posttitle);
                intent.putExtra("PostUser", rank_base.uid);
                intent.putExtra("PostPeopleMax", rank_base.postpeople_max);
                intent.putExtra("PostDate", rank_base.timestamp);
                intent.putExtra("PostRegion", rank_base.postregion);
                intent.putExtra("PostTag", rank_base.posttag);
                intent.putExtra("PostContent", rank_base.postcontent);
                intent.putExtra("PostBoardValue", rank_base.boardvalue);


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
//                ((Activity)context).finish();

            }
        });




    }

    @Override
    public int getItemCount(){
        return subjectvalues.size();
    }
}
