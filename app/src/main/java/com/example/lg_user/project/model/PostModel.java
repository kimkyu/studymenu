package com.example.lg_user.project.model;

import java.io.Serializable;
import java.util.Comparator;

public class PostModel{

    public String uid; //게시글 작성자
    public String boardvalue; //게시판 종류
    public String posttitle; //게시글의 이름
    public String postcontent; //게시글의 내용
    public String posttag; //태그
    public String postregion; //만날 지역
    public int postpeople_max; //최대 인원수
    public long timestamp; //게시 시간

    public PostModel(){

    }

    public PostModel(String uid, String boardvalue, String posttitle, String postcontent, String posttag, String postregion, int postpeople_max, Long timestamp) {
        this.uid = uid;
        this.boardvalue = boardvalue;
        this.posttitle = posttitle;
        this.postcontent = postcontent;
        this.posttag = posttag;
        this.postregion = postregion;
        this.postpeople_max = postpeople_max;
        this.timestamp = timestamp;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }


    public String getBoardvalue() {
        return boardvalue;
    }

    public void setBoardvalue(String boardvalue) {
        this.boardvalue = boardvalue;
    }

    public String getPosttitle() {
        return posttitle;
    }

    public void setPosttitle(String posttitle) {
        this.posttitle = posttitle;
    }

    public String getPostcontent() {
        return postcontent;
    }

    public void setPostcontent(String postcontent) {
        this.postcontent = postcontent;
    }

    public String getPosttag() {
        return posttag;
    }

    public void setPosttag(String posttag) {
        this.posttag = posttag;
    }

    public String getPostregion() {
        return postregion;
    }

    public void setPostregion(String postregion) {
        this.postregion = postregion;
    }

    public int getPostpeople_max() {
        return postpeople_max;
    }

    public void setPostpeople_max(int postpeople_max) {
        this.postpeople_max = postpeople_max;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    public static Comparator<PostModel> Post_mulitplessort = new Comparator<PostModel>() {
        //times 순서로 정렬 후 같은 times 내에서는 playtimes 기준으로 정렬. 성공!
        //정렬 순서를 바꾸려면 1, -1 등을 반대로 바꿔주면 되지 않을까 싶다. 일단 현 상태는 오름차순.

        public int compare(PostModel s1, PostModel s2) {
            int ret = 0;

            long times1 = s1.getTimestamp();
            long times2 = s2.getTimestamp();
//            int playtimes1 = s1.getPlay_times();
//            int playtimes2 = s2.getPlay_times();

            if (times1 > times2) ret = -1;
            else if (times1 < times2 ) ret = 1;
//            if(times1 == times2){
//                if (playtimes1 > playtimes2) ret = 1;
//                else if(playtimes1 < playtimes2) ret = -1;
//            }


            return ret;
        }
    };
}
