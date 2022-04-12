package com.oushang.iqiyi.entries;

/**
 * @Author: zeelang
 * @Description: 设置项
 * @Time: 2021/9/3 15:11
 * @Since: 1.0
 */
public class SettingEntry {

    private String title;

    private String tips;

    public SettingEntry(String title, String tips) {
        this.title = title;
        this.tips = tips;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }
}
