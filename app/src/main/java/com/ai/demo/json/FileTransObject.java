package com.ai.demo.json;

import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * 将传输的内容封装成对象
 */

public class FileTransObject {

    private String status;
    private JSONObject content;
    private String method;
    private String appKey;
    private String strFace = "faces";
    private String strCard = "idcards";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }


    public JSONObject getContent() {
        return content;
    }


    public void setContent(List<String> listCard, List<String> listFace) {
        JSONObject jsonContent = new JSONObject();
        jsonContent.put(strCard, listCard);
        jsonContent.put(strFace, listFace);
        this.content = jsonContent;
    }


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }


    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String javaObjectToJsonString() {
        return "";

    }


}
