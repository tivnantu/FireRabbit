package cn.tivnan.firerabbit;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BookmarkAdapter extends RecyclerView.Adapter<BookmarkAdapter.ViewHolder> {

    private List<Bookmark> mBookmarkList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookmarkImg;
        TextView bookmarkDesc;

        public ViewHolder(@NonNull @NotNull View itemView) {//传入RecycleerView子项最外层布局，通过fid获取布局中的实例
            super(itemView);
            bookmarkImg = (ImageView)itemView.findViewById(R.id.bookmark_item_img);
            bookmarkDesc = (TextView)itemView.findViewById(R.id.bookmark_item_desc);
        }
    }
    //Adapter的构造函数，获取需要展示的数据源
    public BookmarkAdapter(List<Bookmark> bookmarkList) {
        mBookmarkList = bookmarkList;
    }

    @NonNull
    @NotNull
    @Override
    //用于创建ViewHolder实例
    public BookmarkAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup viewGroup, int i) {
        //将bookmark_item布局加载进来
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bookmark_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    //对RecyclerView子项的数据进行赋值
    public void onBindViewHolder(@NonNull @NotNull BookmarkAdapter.ViewHolder viewHolder, int i) {
        Bookmark bookmark = mBookmarkList.get(i);
//        viewHolder.bookmarkImg.setImageResource(bookmark.getImgId());
        viewHolder.bookmarkDesc.setText(bookmark.getDesc());
    }

    @Override
    public int getItemCount() {
        return mBookmarkList.size();
    }

}
