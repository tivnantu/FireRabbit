package cn.tivnan.firerabbit.view;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import androidx.viewpager.widget.PagerAdapter;

import com.github.chrisbanes.photoview.PhotoView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import cn.tivnan.firerabbit.R;
////import uk.co.senab.photoview.PhotoView;
//import com.github.chrisbanes.photoview.PhotoView;


public class ViewPagerActivity extends Activity {
    private ViewPager mViewPager;
    private View rootView;
    private PhotoView image;
    private TextView indicator;
    private Bundle bundle;
    ArrayList<String> listimg;
    private int index;
    private int count;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);
        mViewPager = (HackViewPager) findViewById(R.id.view_pager);
        setContentView(mViewPager);
        bundle = getIntent().getBundleExtra("bundle");
        listimg = bundle.getStringArrayList("list_image");
        index = bundle.getInt("index");
        count = listimg.size();
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(index);
    }

    class SamplePagerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return count;
        }
        @Override
        //此处用到PhotoView、ImageLoader均为第三方类库的类
        public View instantiateItem(ViewGroup container, int position) {
            rootView = View.inflate(ViewPagerActivity.this, R.layout.viewpager_item, null);
            image = (PhotoView) rootView.findViewById(R.id.photoview);
            indicator = (TextView) rootView.findViewById(R.id.indicator);
            ImageLoader imageloader=ImageLoader.getInstance();
            imageloader.init(ImageLoaderConfiguration.createDefault(ViewPagerActivity.this));
            imageloader.displayImage(listimg.get(position), image);
            CharSequence text = getString(R.string.viewpager_indicator, position+1, count);
            indicator.setText(text);
            container.addView(rootView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return rootView;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }
}