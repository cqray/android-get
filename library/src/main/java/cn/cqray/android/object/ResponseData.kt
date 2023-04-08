package cn.cqray.android.`object`

import com.google.gson.annotations.SerializedName

import java.io.Serializable
import java.util.ArrayList
import java.util.regex.Pattern

/**
 * 网络请求响应体
 * @author Cqray
 */
class ResponseData<T> : Serializable {

    @SerializedName(value = "code", alternate = ["ret", "error_code"])
    var code: String? = null

    @SerializedName(value = "message", alternate = ["msg", "info", "reason"])
    var message: String? = null

    @SerializedName(value = "data", alternate = ["result", "value"])
    var data: T? = null

    /** [code]整型值 **/
    val codeAsInt: Int
        get() {
            val regex = "\\D"
            val pattern = Pattern.compile(regex)
            val matcher = pattern.matcher(code!!)
            val number = matcher.replaceAll("").trim()
            return number.toInt()
        }

    //        return Starter.getInstance()
//                .getStarterStrategy()
//                .getResponseDataSucceedCode()
//                .contains(code);
    val isSucceed: Boolean get() = true

    //        return Starter.getInstance()
//                .getStarterStrategy()
//                .getResponseDataSucceedCode()
//                .contains(code);
    companion object {

        @JvmStatic
        fun <T> success(data: T, message: String?): ResponseData<T> {
            //List<String> codes = Starter.getInstance().getStarterStrategy().getResponseDataSucceedCode();
            val codes: List<String> = ArrayList()
            val responseData = ResponseData<T>()
            responseData.data = data
            responseData.message = message
            responseData.code = if (codes.isNotEmpty()) codes[0] else "200"
            return responseData
        }

        @JvmStatic
        fun <T> fail(message: String?): ResponseData<T> {
            return fail(null, message)
        }

        @JvmStatic
        fun <T> fail(code: Int, message: String?): ResponseData<T> {
            return fail(code.toString(), message)
        }

        @JvmStatic
        fun <T> fail(code: String?, message: String?): ResponseData<T> {
            //String failCode = Starter.getInstance().getStarterStrategy().getResponseDataFailCode();
            val failCode = ""
            val responseData = ResponseData<T>()
            responseData.message = message
            responseData.code = code ?: failCode
            return responseData
        }
    }
}