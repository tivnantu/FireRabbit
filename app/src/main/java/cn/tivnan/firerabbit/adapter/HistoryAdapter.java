package cn.tivnan.firerabbit.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import cn.tivnan.firerabbit.R;
import cn.tivnan.firerabbit.entity.History;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private List<History> mHistoryList;
    private OnItemClickListener mOnItemClickListener;//item点击监听接口

    public HistoryAdapter(List<History> histotyList) {
        this.mHistoryList = histotyList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView historyName;
        private TextView historyUrl;
        private ImageView historyDelete;
        private View view;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            historyName = (TextView) itemView.findViewById(R.id.history_item_name);
            historyUrl = (TextView) itemView.findViewById(R.id.history_item_url);
            historyDelete = (ImageView) itemView.findViewById(R.id.history_item_delete);
            view = itemView;
        }
    }

    @NonNull
    @NotNull
    @Override
    public HistoryAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(@NonNull @NotNull HistoryAdapter.ViewHolder viewHolder, int position) {
        History history = mHistoryList.get(position);
        viewHolder.historyName.setText(history.getName());
        viewHolder.historyUrl.setText(history.getUrl());

        if (mOnItemClickListener != null) {
            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(v, position);
                }
            });
            viewHolder.view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    mOnItemClickListener.onLongClick(v, position);
                    return true;
                }
            });
            viewHolder.historyDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemDeleteClick(v, position);

                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return mHistoryList.size();
    }


    //设置item点击监听器
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onLongClick(View v, int pos);//长按历史记录时
        void onItemClick(View v, int pos);//点击历史纪录时
        void onItemDeleteClick(View v, int pos);//点击删除按钮时
    }
}
