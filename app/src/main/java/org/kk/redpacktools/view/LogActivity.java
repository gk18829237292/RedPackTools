package org.kk.redpacktools.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.kk.redpacktools.App;
import org.kk.redpacktools.R;
import org.kk.redpacktools.db.entities.RedPackLog;
import org.kk.redpacktools.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class LogActivity extends BaseActivity {

    @BindView(R.id.tv_totalMoney)
    TextView tv_totalMoney;
    @BindView(R.id.tv_totalCount)
    TextView tv_totalCount;

    @BindView(R.id.list_logs)
    RecyclerView list_logs;

    Adapter adapter;
    private List<RedPackLog> mylogs = new ArrayList<>();

    @Override
    void initView() {
        setContentView(R.layout.activity_log);
        ButterKnife.bind(this);

        list_logs.setAdapter(adapter = new Adapter(mContext, mylogs));
        list_logs.setLayoutManager(new LinearLayoutManager(mContext));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Flowable.fromCallable(() ->
                App.getDB().redPackDao().queryAll()
        ).subscribeOn(Schedulers.io()).observeOn(Schedulers.single()).subscribe((logs) -> {
            tv_totalCount.setText("总次数：" + logs.size());
            double sum = 0;
            for (RedPackLog log : logs) sum += log.money;
            tv_totalMoney.setText(String.format("总金额：%.2f", sum));

            mylogs.clear();
            mylogs.addAll(logs);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    void initData() {

    }

    class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

        private Context mContext;
        private List<RedPackLog> redPackLogs;

        public Adapter(Context context, List<RedPackLog> redPackLogs) {
            this.mContext = context;
            this.redPackLogs = redPackLogs;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.item_log, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RedPackLog redPackLog = redPackLogs.get(position);
            holder.tv_msg.setText(String.format("%d. 收到来自 %s 的红包：%.2f ", position + 1, redPackLog.name, redPackLog.money));
            holder.tv_time.setText(TimeUtils.getDescriptionTimeFromTimestamp(redPackLog.time));
        }

        @Override
        public int getItemCount() {
            return redPackLogs.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.tv_msg)
            TextView tv_msg;
            @BindView(R.id.tv_time)
            TextView tv_time;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

}
