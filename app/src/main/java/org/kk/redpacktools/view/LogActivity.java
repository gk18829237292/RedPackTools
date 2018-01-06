package org.kk.redpacktools.view;

import android.app.Activity;
import android.arch.core.executor.TaskExecutor;
import android.os.Bundle;
import android.widget.TextView;

import org.kk.redpacktools.App;
import org.kk.redpacktools.R;
import org.kk.redpacktools.db.entities.RedPackLog;
import org.w3c.dom.Text;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class LogActivity extends BaseActivity {

    @BindView(R.id.tv_totalMoney)
    TextView tv_totalMoney;
    @BindView(R.id.tv_totalCount)
    TextView tv_totalCount;

    @Override
    void initView() {
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);
    }

    @Override
    void initData() {

        Flowable.fromCallable(()->
            App.getDB().redPackDao().queryAll()
        ).subscribeOn(Schedulers.io()).observeOn(Schedulers.single()).subscribe((logs)->{
            tv_totalCount.setText("总次数："+ logs.size());
            double sum = 0;
            for(RedPackLog log:logs) sum+=log.money;
            tv_totalMoney.setText(String.format("总金额：%.2f",sum));

        });

    }
}
