package com.oushang.plugin

import java.text.SimpleDateFormat

class BuildVersionNameExtension {

    def canGenerateVersionNameType = [] // 需要动态生成 versionName 的构建类型

    def softwareStatus = "" // 软件状态

    def generateVersionName = {
        SimpleDateFormat sdf = new SimpleDateFormat("_yyyyMMddHH", Locale.getDefault());
        return softwareStatus + sdf.format(new Date())
    }
}