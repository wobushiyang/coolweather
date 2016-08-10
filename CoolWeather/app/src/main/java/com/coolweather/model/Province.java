package com.coolweather.model;

/**
 * Created by ASUS-PC on 2016/7/31.
 */
public class Province {

    private int id;//自动生成的ID
    private String provinceName;//省份名
    private String provinceCode;//省份代码

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
