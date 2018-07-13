package com.example.lg_user.project.model;

import org.w3c.dom.Comment;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LG-USER on 2018-04-04.
 */

public class ChatModel {
    public Map<String,Boolean> users = new HashMap<>(); //채팅방의 유저들
    public Map<String,Comment> comments = new HashMap<>();//채팅방의 대화내용

    public static class Comment {
        public String uid;
        public String message;
        public Object timestamp;


        public Map<String, Object> readUsers = new HashMap<>();
    }

//    public static Comparator<ChatModel> Post_mulitplessort = new Comparator<ChatModel>() {
//        //times 순서로 정렬 후 같은 times 내에서는 playtimes 기준으로 정렬. 성공!
//        //정렬 순서를 바꾸려면 1, -1 등을 반대로 바꿔주면 되지 않을까 싶다. 일단 현 상태는 오름차순.
//
//        public int compare(ChatModel s1, ChatModel s2) {
//            int ret = 0;
//
//            long times1 = s1.getTimestamp();
//            long times2 = s2.getTimestamp();
////            int playtimes1 = s1.getPlay_times();
////            int playtimes2 = s2.getPlay_times();
//
//            if (times1 > times2) ret = -1;
//            else if (times1 < times2 ) ret = 1;
////            if(times1 == times2){
////                if (playtimes1 > playtimes2) ret = 1;
////                else if(playtimes1 < playtimes2) ret = -1;
////            }
//
//
//            return ret;
//        }
//    };
}
