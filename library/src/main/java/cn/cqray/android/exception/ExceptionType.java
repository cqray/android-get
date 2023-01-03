package cn.cqray.android.exception;

/**
 * @author Cqray
 */
public enum ExceptionType {
    /** 布局为NULL **/
    CONTENT_VIEW_NULL("ContentView为空。", "未设置界面，便进行对界面的相关操作。"),
    /** 不支持Header **/
    HEADER_UNSUPPORTED("不支持Header。", "因为使用的是setNativeContentView()方法布局，所以不支持Header相关操作。"),
    /** 不支持Footer **/
    FOOTER_UNSUPPORTED("不支持Footer。", "因为使用的是setNativeContentView()方法布局，所以不支持Footer相关操作。"),
    /** 方法调用时机出错 **/
    STARTER_ILLEGAL_STATE("方法调用时机出错。", "请在super.onCreating()之后调用loadRootFragment()、start()、popTo()、pop()等方法。"),


    /** 布局为NULL **/
    //STATE_UNSUPPORTED("不支持。", "未设置界面，便进行对界面的相关操作。"),
    ;
    final String mIntro;
    final String mDesc;

    ExceptionType(String intro, String desc) {
        mIntro = intro;
        mDesc = desc;
    }
}
