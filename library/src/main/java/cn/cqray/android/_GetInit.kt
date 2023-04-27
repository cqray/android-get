package cn.cqray.android

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 * [Get]框架无痛初始化，使用的是[ContentProvider]会自动初始化的特点。
 * @author Cqray
 */
@Suppress("ClassName")
internal class _GetInit : ContentProvider() {

    //========================================================
    //=================ContentProvider相关部分=================
    //========================================================

    override fun onCreate(): Boolean {
        // 初始化Application
        application = context!!.applicationContext as Application
        return false
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) = 0

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) = 0

    //========================================================
    //=====================Get框架初始化部分====================
    //========================================================

    companion object {
        /** [Application]实例 **/
        lateinit var application: Application
    }
}