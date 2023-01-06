package cn.cqray.android.exc;

import java.io.Serializable;

/**
 * @author Admin
 */
public class GetException extends RuntimeException implements Serializable {

//    private String mMessage;

    private String intro;

    private String desc;

    public GetException(String message) {
        //mMessage = message;
        super(message);
    }

//    @Override
//    public String getMessage() {
//        return mMessage;
//    }
}
