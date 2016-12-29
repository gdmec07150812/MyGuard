package com.gdmec.jacky.myguard.mycalculator;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.gdmec.jacky.myguard.R;

public class MyCalculator extends Activity {

    private Button calculate;
    private EditText weight;
    private RadioButton man;
    private RadioButton woman;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_calculator);
        findViewById(R.id.rl_titlebar).setBackgroundColor(getResources().getColor(R.color.bright_red));
        ImageView mLeftImgv = (ImageView) findViewById(R.id.imgv_leftbtn);
        ((TextView) findViewById(R.id.tv_title)).setText("身高体重计算器");
        mLeftImgv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mLeftImgv.setImageResource(R.drawable.back);
        calculate = (Button) findViewById(R.id.calculate);
        weight = (EditText) findViewById(R.id.weight);
        man = (RadioButton) findViewById(R.id.man);
        woman = (RadioButton) findViewById(R.id.woman);
        result = (TextView) findViewById(R.id.result);
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerEvent();
    }

    private void registerEvent() {
        calculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!weight.getText().toString().equals("")) {
                    if (man.isChecked() || woman.isChecked()) {
                        Double weight1 = Double.parseDouble(weight.getText().toString());
                        StringBuffer sb = new StringBuffer();
                        sb.append("--------------结果-------------\n");
                        if (man.isChecked()) {
                            sb.append("男性标准身高：");
                            double result = evaluateHeight(weight1, "男");
                            sb.append((int) result + "厘米");
                        } else {
                            sb.append("女性标准身高：");
                            double result = evaluateHeight(weight1, "女");
                            sb.append((int) result + "厘米");
                        }
                        result.setText(sb.toString());
                    } else {
                        showMessage("请选择性别");
                    }
                } else {
                    showMessage("请输入身高");
                }
            }
        });
    }

    private void showMessage(String message) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle("系统信息");
        alert.setMessage(message);
        alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert.show();
    }

    private double evaluateHeight(double weight, String sex) {
        double height;
        if (sex == "男") {
            height = 170 - (62 - weight) / 0.6;
        } else {
            height = 158 - (52 - weight) / 0.5;
        }
        return height;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "退出");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
