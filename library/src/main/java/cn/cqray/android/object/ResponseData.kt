package cn.cqray.android.`object`

import cn.cqray.android.Get
import com.google.gson.annotations.SerializedName

import java.io.Serializable
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

    /** 是否成功 **/
    val isSucceed: Boolean get() = Get.init.responseInit.successCodes.contains(code)

    companion object {

        @JvmStatic
        fun <T> success(data: T, message: String?): ResponseData<T> {
            val responseData = ResponseData<T>()
            responseData.data = data
            responseData.message = message
            responseData.code = Get.init.responseInit.successCodes.getOrNull(0)
            return responseData
        }

        @JvmStatic
        fun <T> fail(message: String?): ResponseData<T> {
            return fail(null, message)
        }

        @JvmStatic
        @JvmOverloads
        fun <T> fail(code: String?, message: String? = null): ResponseData<T> {
            val responseData = ResponseData<T>()
            responseData.message = message
            responseData.code = code
            return responseData
        }
    }
}