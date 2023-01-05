package cn.cqray.android.exc;

import java.io.Serializable;

/**
 * @author Admin
 */
public class StarterException extends RuntimeException implements Serializable {

//    private String mMessage;

    public StarterException(String message) {
        //mMessage = message;
        super(message);
    }

//    @Override
//    public String getMessage() {
//        return mMessage;
//    }
}
