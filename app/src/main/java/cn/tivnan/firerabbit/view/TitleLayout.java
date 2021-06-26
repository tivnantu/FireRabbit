package cn.tivnan.firerabbit.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import cn.tivnan.firerabbit.R;

public class TitleLayout extends LinearLayout {//可复用的标题栏

    public TitleLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.layout_titlebar, this);
        ImageView iv_backward = (ImageView) findViewById(R.id.iv_backward);
        TextView tv_title = (TextView) findViewById(R.id.tv_title);
        TextView tv_right = (TextView) findViewById(R.id.tv_right);

//与ItemGroup相同，通过obtainStyledAttributes方法获取到一个TypedArray对象，然后通过TypedArray对象就可以获取到相对应定义的属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TitleLayout);
        String title = typedArray.getString(R.styleable.TitleLayout_titlelayout_title);
        tv_title.setText(title);
        String right = typedArray.getString(R.styleable.TitleLayout_titlelayout_right);
        tv_right.setText(right);

        iv_backward.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)getContext()).finish();
            }
        });
    }
}
