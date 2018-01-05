package org.kk.redpacktools.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import org.kk.redpacktools.utils.Logger;
import org.kk.redpacktools.utils.Spref;

public abstract class BaseActivity extends AppCompatActivity{

    Spref mSpref;
    Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        mSpref = new Spref(this);
        Logger.d("init view ");
        initView();

        initData();
    }

    abstract void initView();

    abstract void initData();
}
