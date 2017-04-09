package net.muxi.huashiapp.ui.webview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.muxi.huashiapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by december on 17/4/7.
 */

public class ShareAdapter extends RecyclerView.Adapter<ShareAdapter.MyShareViewHolder> {


    private List<Integer> mpics;
    private List<String> mdesc;

    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(View view, int position);
    }

    public ShareAdapter(List<Integer> mpics, List<String> mdesc) {
        this.mpics = mpics;
        this.mdesc = mdesc;
    }


    @Override
    public MyShareViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_share, parent, false);
        return new MyShareViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyShareViewHolder holder, int position) {
        holder.mShareImage.setImageResource(mpics.get(position));
        holder.mShareWay.setText(mdesc.get(position));
        if (position < 6){
            if (position > 2){
                holder.mDivider.setVisibility(View.VISIBLE);
            }
        }
            holder.mItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.OnItemClick(v, position);
                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return mdesc.size();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public class MyShareViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_layout)
        RelativeLayout mItemLayout;

        @BindView(R.id.divider)
        View mDivider;

        @BindView(R.id.share_image)
        ImageView mShareImage;
        @BindView(R.id.share_way)
        TextView mShareWay;


        public MyShareViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }
    }
}
