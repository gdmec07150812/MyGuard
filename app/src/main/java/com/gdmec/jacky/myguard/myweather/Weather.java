package com.gdmec.jacky.myguard.myweather;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.gdmec.jacky.myguard.R;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class Weather extends Activity {
    private Weather seft;
    private Map<String, List<String>> cityMap;
    private Spinner province_spinner;
    private Spinner city_spinner;
    private AlertDialog choose_dialog;
    private LinearLayout choose_layout;
    private ImageButton settingBtn;
    private ImageButton refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_activity_main);
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.orange));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("天气中心");
        mLeftImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLeftImgv.setImageResource(R.drawable.back);
        seft = this;
        initProvinces();
        initChooseDialog();
        settingBtn = (ImageButton) this.findViewById(R.id.setting);
        refreshBtn = (ImageButton) this.findViewById(R.id.refresh);
        settingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choose_dialog.show();
            }
        });
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String cityName = ((TextView) seft.findViewById(R.id.city)).getText().toString();
                new GetWeatherInfoTask(seft).execute(cityName);
            }
        });
        new GetWeatherInfoTask(this).execute("广州");
    }

    private void initChooseDialog() {
        choose_dialog = new AlertDialog.Builder(seft).setTitle("选择城市")
                .setPositiveButton("确定", new ChooseCityListenner())
                .setNegativeButton("取消", null).create();
        LayoutInflater inflater = LayoutInflater.from(this);
        choose_layout = (LinearLayout) inflater.inflate(R.layout.choose, null);
        province_spinner = (Spinner) choose_layout.findViewById(R.id.province_spinner);
        ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this,
                R.layout.simple_list_item, new ArrayList<String>(
                cityMap.keySet()));
        province_spinner.setAdapter(provinceAdapter);
        city_spinner = (Spinner) choose_layout.findViewById(R.id.city_spinner);
        province_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String province_name = province_spinner.getSelectedItem().toString();
                ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(
                        seft, R.layout.simple_list_item, cityMap.get(province_name));
                city_spinner.setAdapter(cityAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        choose_dialog.setView(choose_layout);
    }

    private void initProvinces() {
        AssetManager assetManager = getAssets();
        SaxHandler handler = new SaxHandler();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open("City.xml");
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            parser.parse(inputStream, handler);
            cityMap = handler.getCityMap();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class ChooseCityListenner implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            String city_name = city_spinner.getSelectedItem().toString();
            TextView cityName = (TextView) seft.findViewById(R.id.city);
            cityName.setText(city_name);
            new GetWeatherInfoTask(seft).execute(city_name);
        }
    }

}
