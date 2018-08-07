package com.ai.demo.utils;

// 常量管理
public class ConstantManager {

    // requestCode 定义
    public static final int OPENCUSTOMCAMERA = 100; // 打开自定义相机
    public static final int OPENLOCALCAMERA = 101; // 打开本地相机
    public static final int OPENPHOTOS_FACE = 102; // 打开相册
    public static final int OPENPHOTOS_CARD = 103; // 打开相册

    // 使用自定义相机还是本地相机
    public static final int ISCARD = 0; // 身份证
    public static final int ISFACE = 1; // 人像

    // 上传字段
    public static final String UPLOADSTATUS = "upload";
    public static final String UPLOADMETHOD = "faceMatch";
    public static final String UPLOADAPPKEY = "123456";

    // 接收字段
    public static final String RECEIVESTATUS = "status";
    public static final String RECEIVEAPPKEY = "appKey";
    public static final String RECEIVEMETHOD = "method";
    public static final String RECEIVECONTENT = "content";
    public static final String RECEIVERESULT = "result";
    public static final String RECEIVEEDU = "edu";
    public static final String RECEIVECOSINE = "cosine";
    public static final String RECEIVEFACE = "face";
    public static final String RECEIVEID_FACE = "id_face";
    public static final String RECEIVEID_OCR_IMAGE = "OCR_IMG";

    public static final int OCR_IMAGE_HEIGHT = 38;

    public static final String RECEIVEID_OCR_IMAGE_HW = "OCR_IMG_HW";
    public static final String RECEIVEID_OCR_IMAGE_RESULT = "OCR_IMG_RESULT";
    public static final String RECEIVEFACE_HW = "face_hw";
    public static final String RECEIVEID_HW = "id_face_hw";


    // 提示语定义
    public static final String TOST_ADDPHOTO = "请添加完善图片"; // 请添加完善图片
    public static final String TOST_ADDTEXT = "请添加完善文本"; // 请添加完善图片
    public static final String TOST_UPLOADFALSE = "上传失败"; // 上传失败
    public static final String TOST_RESULTFALSE = "识别失败!"; // 返回结果解析错误-返回结果本身错误
    public static final String TOST_JSONPARSEFALSE = "解析返回结果错误"; // 返回结果解析错误-返回结果本身错误
    public static final String TOST_ADDPHOTOERROR = "图片格式错误"; // 返回结果解析错误-返回结果本身错误
    public static final String TOST_FACENULL = "未检测到人脸请重拍"; // 未检测到人脸请重拍


    // 人证对别结果判断
    public static final String RESULTSUCCESS = "一致,用户为同一人";
    public static final String RESULTFALSE = "不一致,用户不是同一人";


    public static final String OCR_IMG_FILENAME = "OCR_IMG.png";
    public static final String CARD_FACE_FILENAME = "CARD_FACE.png";
    public static final String FACE_FILENAME = "FACE.png";

}
