package com.coolweather.model;

/**
 * Created by ASUS-PC on 2016/7/31.
 */
public class County {

    private int id;//自动生成的县城ID
    private String countyName;//县城名
    private String countyCode;//县城代码
    private int cityId;//关联城市的ID

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
