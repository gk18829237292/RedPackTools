package org.kk.redpacktools.view;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.accessibility.AccessibilityManager;
import android.widget.Switch;
import android.widget.TextView;

import org.kk.redpacktools.R;
import org.kk.redpacktools.utils.Logger;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {

    @BindView(R.id.sw_startService)
    Switch sw_startService;

    @BindView(R.id.tv_redPackLog)
    TextView tv_redPackLog;

    Intent settingIntent;


    @Override
    void initView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        //启动辅助服务的intent
        settingIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        sw_startService.setOnClickListener(l->{
            startActivity(settingIntent);
        });
    }

    @Override
    void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.d("onResume");
        if(isServiceStart()){
            sw_startService.setChecked(true);
            sw_startService.setText("服务已开启");
        }else{
            sw_startService.setChecked(false);
            sw_startService.setText("开启服务");
        }
    }

    private boolean isServiceStart(){
        boolean result = false;
        AccessibilityManager manager = (AccessibilityManager) getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> serviceInfos = manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC);
        for (AccessibilityServiceInfo serviceInfo:serviceInfos){
            if(serviceInfo.getId().equals(getPackageName()+"/org.kk.redpacktools.service.RedPackService")){
                result = true;
                break;
            }
        }
        return result;
    }



}
