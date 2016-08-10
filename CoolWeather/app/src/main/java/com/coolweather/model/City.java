package com.coolweather.model;

/**
 * Created by ASUS-PC on 2016/7/31.
 */
public class City {

    private int id;//自动生成的城市ID
    private String cityName;//城市名
    private String cityCode;//城市代码
    private int provinceId;//关联省份的ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
