package com.coolweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.R;
import com.coolweather.db.CoolWeatherDB;
import com.coolweather.model.City;
import com.coolweather.model.County;
import com.coolweather.model.Province;
import com.coolweather.util.HttpCallbackListener;
import com.coolweather.util.HttpUtil;
import com.coolweather.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaActivity extends Activity {

    //判断级别
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;//适配器
    private List<String> dataList=new ArrayList<>();//填充的列表
    private CoolWeatherDB coolWeatherDB;
    
    private int currentLevel;//当前选中的级别
    
    private Province selectedProvince;//选中的省份
    private City selectedCity;//选中的城市
    private County selectedCounty;//选中的县城
    
    private List<Province> provinceList;//省列表
    private List<City> cityList;//城市列表
    private List<County> countyList;//县列表

    private ProgressDialog progressDialog;

    
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sp= PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean("city_selected",false)){
            Intent intent=new Intent(this,WeatherActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);//无标题，全屏
        setContentView(R.layout.choose_area);

        listView= (ListView) findViewById(R.id.list_view);
        titleText= (TextView) findViewById(R.id.title_text);

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);//得到适配器对象
        listView.setAdapter(adapter);
        
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();//查找选中省份下的城市
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();//查找选中城市下的县城
                }else if(currentLevel==LEVEL_COUNTY){
                    String countyCode=countyList.get(position).getCountyCode();
                    Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
                    intent.putExtra("county_code",countyCode);
                    startActivity(intent);
                    finish();
                }
            }
        });
        queryProvinces();//加载省级数据
    }

    /**
     * 查询全国所有的省份，优先从数据库中查找，如果没有再到服务器上查询
     */
    private void queryProvinces() {
        provinceList=coolWeatherDB.loadProvince();//从数据库中将数据加载出来
        if(provinceList.size()>0){
            dataList.clear();//清空
            for(Province province : provinceList){
                dataList.add(province.getProvinceName());//将省份名填充到列表中
            }
            adapter.notifyDataSetChanged();//更新数据
            listView.setSelection(0);//将列表移动到指定位置
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;//将级别定义成省份
        }else{
            queryFromServer(null,"province");//从服务器中查询省份
        }
    }

    /**
     * 查询选中省份的城市，优先从数据库中查找，如果没有再到服务器上查询
     */
    private void queryCities() {
        cityList=coolWeatherDB.loadCities(selectedProvince.getId());//从数据库中将选中省份的城市加载出来
        if(cityList.size()>0){
            dataList.clear();//清空
            for(City city : cityList){
                dataList.add(city.getCityName());//填充
            }
            adapter.notifyDataSetChanged();//更新
            listView.setSelection(0);//将列表移动到指定位置
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;//将级别定义成城市
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");//从服务器中查询城市
        }
    }

    /**
     * 查询选中城市内所有的县，优先从数据库中查找，如果没有再到服务器上查询
     */
    private void queryCounties(){
        countyList=coolWeatherDB.loadCounties(selectedCity.getId());//从数据库中将选中城市内的县加载出来从数据库中将选中省份的城市加载出来
        if(countyList.size()>0){
            dataList.clear();//清空
            for(County county : countyList){
                dataList.add(county.getCountyName());//填充
            }
            adapter.notifyDataSetChanged();//更新
            listView.setSelection(0);//将列表移动到指定位置
            titleText.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;//将级别定义成县城
        }else {
            queryFromServer(selectedCity.getCityCode(),"county");//从服务器中查询城市
        }
    }

    /**
     * 根据传入的代号和类型从服务器上查询省市县数据
     * @param code 代码
     * @param type 省市县类别
     */
    private void queryFromServer(final String code, final String type) {

        String address;

        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else{
            address="http://www.weather.com.cn/data/list3/city.xml";
        }

        showProgressDialog();//显示进度对话框
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result=false;
                if("province".equals(type)){
                    result= Utility.handleProvincesResponse(coolWeatherDB,response);
                }else if("city".equals(type)){
                    result=Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());
                }else if("county".equals(type)){
                    result=Utility.handleCountiesResponse(coolWeatherDB,response,selectedCity.getId());
                }

                if(result){
                    //通过runOnUiThread()方法回去主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();//关闭进度对话框
                            if("province".equals(type)){
                                queryProvinces();//牵扯到了UI操作，所有借助runOnUiThread()方法回去主线程处理逻辑
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread()方法回去主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 显示进度对话框
     */
    private void showProgressDialog() {
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载......");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();//显示
    }

    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog() {
        if(null!=progressDialog){
            progressDialog.dismiss();
        }
    }

    /**
     * 捕获Back按键，根据当前的级别来判断，此时应该返回市列表、省列表、还是直接退出
     */
    @Override
    public void onBackPressed(){
         if(currentLevel==LEVEL_COUNTY){
             queryCities();
         }else if(currentLevel==LEVEL_CITY){
             queryProvinces();
         }else{
             finish();
         }
    }

}
