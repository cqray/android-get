package cn.cqray.android.util

import java.util.regex.Matcher
import java.util.regex.Pattern

object StrUtils {

    fun parseNum(str: String):String {
        val regex = "[^0-9]"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(str)
        return matcher.replaceAll("").trim()
    }

    fun toInt(str: String): Int {
        val regex = "[^0-9]"
        val pattern = Pattern.compile(regex)
        val matcher = pattern.matcher(str)
        val number = matcher.replaceAll("").trim()
        return number.toInt()
    }
}