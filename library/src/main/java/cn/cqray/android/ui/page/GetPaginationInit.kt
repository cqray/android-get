package cn.cqray.android.ui.page

import cn.cqray.android.app.GetBaseInit

class GetPaginationInit : GetBaseInit() {

    /** 默认页码起始值 **/
    var pageNum = 1

    /** 默认页码大小 **/
    var pageSize = 20

    /** 使用SetBusy作为首次加载 **/
    var busyFirst = true
}