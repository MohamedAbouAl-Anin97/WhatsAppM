package com.example.whatsappm.interfaces;

import com.example.whatsappm.models.chat.Chats;

import java.util.List;

public interface OnReadChatCallBack {

    void onReadSuccess(List<Chats> list);
    void onFailed();



}
