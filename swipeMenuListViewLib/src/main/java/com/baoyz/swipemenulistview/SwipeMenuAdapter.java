package com.baoyz.swipemenulistview;

import java.util.List;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import com.baoyz.swipemenulistview.SwipeMenuView.OnSwipeItemClickListener;

/**
 * 
 * @author baoyz
 * @date 2014-8-24
 * 
 */
public class SwipeMenuAdapter implements WrapperListAdapter,
		OnSwipeItemClickListener {

    private ListAdapter mAdapter;
    private Context mContext;
    private SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener;

    public SwipeMenuAdapter(Context context, ListAdapter adapter) {
        mAdapter = adapter;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mAdapter.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mAdapter.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mAdapter.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SwipeMenuLayout layout = null;
        if (convertView == null) {
        	//获取我们自己定义的item 的布局。
            View contentView = mAdapter.getView(position, convertView, parent);
            //生成一个新的menu 对象。
            SwipeMenu menu = new SwipeMenu(mContext);
            //设置数据的对象的类型  依靠这个字段来 生成menu的类型  
            //demo里面 的DifferentMenuActivity 这个类里就用到这了这个参数。
            menu.setViewType(getItemViewType(position));
            //生成Item的方法  在本类中又一个 实现。这个其实是需要自己实现的  以便自己定义 自己Item 按钮的 属性（背景，颜色 字体大小之类的）。
            createMenu(menu);
            //生成新的ViewGroup 只是在我们的item 布局上添加了 createMenu(menu) 中生成的menu。该（SwipeMenuView）继承自Linearlayout
            //最终展示在界面上的Item布局 就是这个。（我们滑动的时候展示出来的布局）
            SwipeMenuView menuView = new SwipeMenuView(menu,
                    (SwipeMenuListView) parent);
            //给展示到界面上的Item添加事件
            menuView.setOnSwipeItemClickListener(this);
            //获取SwipeMenuListView(这个对象 是我们写在界面里面的SwipeMenuListView)对象
            //强转是为了  使用该类中自定义的 插入器(即listView.getCloseInterpolator())。
            SwipeMenuListView listView = (SwipeMenuListView) parent;
            //最终展示在界面上的item 上下 两层 表面上是我们 自己定义的布局 ，当我们滑动item的时候 就会展示 它右侧的 SwipeMenuView。
            //而SwipeMenuView 本生是一个继承linearLayout 所以我们可以 随意添加侧滑出来 按钮的个数
            //这个个数在SwipeMenu 中的List<SwipeMenuItem> mItems 的Size 就是数目。
            layout = new SwipeMenuLayout(contentView, menuView,
                    listView.getCloseInterpolator(),
                    listView.getOpenInterpolator());
            //记录Item的posion。
            layout.setPosition(position);
        } else {
            layout = (SwipeMenuLayout) convertView;
            //当view 处于打开状态时 滑动到 不可见时关闭它。
            layout.closeMenu();
            //时刻都要记录位置。
            layout.setPosition(position);
            //生成一个View 单好像没有用过这个view  (我也迷糊)
            View view = mAdapter.getView(position, layout.getContentView(),
                    parent);
        }
        //这个是最新版本才加入的东西   由于我很早之前用过这个开源觉得不错 所以最近闲的无事才准备写个注释 这个应该是新代码(之前没有)。
        //看样子是 现在支持 手动设置是否可 滑动。  
        if (mAdapter instanceof BaseSwipListAdapter) {
            boolean swipEnable = (((BaseSwipListAdapter) mAdapter).getSwipEnableByPosition(position));
            layout.setSwipEnable(swipEnable);
        }
        return layout;
    }
    //自定义 按钮数量 的重要方法 五星级的 重要方法(我们在生成按钮的 时候会用这个 menu 去menu.addMenuItem(item))
    //当然 我们看到的是SwipeMenuCreator中的create(SwipeMenu menu) 里面的menu　 其实是一个menu.
    public void createMenu(SwipeMenu menu) {
        // Test Code
        SwipeMenuItem item = new SwipeMenuItem(mContext);
        item.setTitle("Item 1");
        item.setBackground(new ColorDrawable(Color.GRAY));
        item.setWidth(300);
        menu.addMenuItem(item);

        item = new SwipeMenuItem(mContext);
        item.setTitle("Item 2");
        item.setBackground(new ColorDrawable(Color.RED));
        item.setWidth(300);
        menu.addMenuItem(item);
    }
    // SwipeMenuView item 点击事件。即 按钮的 点击事件 一边我们都会实现的
    //比如说   我们注册了2个按钮   一个删除      一个 备注   那么他们的 点击事件 就在这里面写
    //index 依次为createMenu(SwipeMenu menu)  中menu 的addMenuItem 的顺序。
    @Override
    public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
    	
        if (onMenuItemClickListener != null) {
        	//view.getPosition() 是当前item的行数 ，index 为该行第几个 按钮。 menu为这些个 按钮的属性。
            onMenuItemClickListener.onMenuItemClick(view.getPosition(), menu,
                    index);
        }
    }
    //为按钮事件赋值
    public void setOnSwipeItemClickListener(
            SwipeMenuListView.OnMenuItemClickListener onMenuItemClickListener) {
        this.onMenuItemClickListener = onMenuItemClickListener;
    }
    //这是个类是WrapperListAdapter的实现类 在WrapperListAdapter只是一个接口 没有实现
    //所以必须 实现以下 的方法  所以移花接木到我们自己的mAdapter上了。 
    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mAdapter.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mAdapter.unregisterDataSetObserver(observer);
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mAdapter.isEnabled(position);
    }

    @Override
    public boolean hasStableIds() {
        return mAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return mAdapter.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mAdapter.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mAdapter.isEmpty();
    }

    @Override
    public ListAdapter getWrappedAdapter() {
        return mAdapter;
    }

}
