package com.library.adapter.base.decoration;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.library.adapter.base.BaseQuickAdapter;

/**
 * @author MBP
 * @date 2019-11-27
 */
public class LineDecoration extends RecyclerView.ItemDecoration{
    private ColorDrawable mColorDrawable;
    private int mHeight;
    private int mPaddingLeft;
    private int mPaddingRight;
    private boolean mDrawAtLast = true;
    private boolean mDrawAtHeader = false;
    private boolean mDrawAtFooter = false;

    public LineDecoration(int color, int height) {
        this.mColorDrawable = new ColorDrawable(color);
        this.mHeight = height;
    }
    public LineDecoration(int color, int height, int paddingLeft, int paddingRight) {
        this.mColorDrawable = new ColorDrawable(color);
        this.mHeight = height;
        this.mPaddingLeft = paddingLeft;
        this.mPaddingRight = paddingRight;
    }

    public void setDrawAtLast(final boolean drawAtLast) {
        mDrawAtLast = drawAtLast;
    }

    public void setDrawAtHeader(final boolean drawAtHeader) {
        mDrawAtHeader = drawAtHeader;
    }

    public void setDrawAtFooter(final boolean drawAtFooter) {
        mDrawAtFooter = drawAtFooter;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, RecyclerView parent, @NonNull RecyclerView.State state) {

        if(parent.getAdapter() == null)
            return;

        int position = parent.getChildAdapterPosition(view);
        int orientation = 0;
        int headerCount = 0,footerCount = 0;
        if (parent.getAdapter() instanceof BaseQuickAdapter){
            headerCount = ((BaseQuickAdapter) parent.getAdapter()).getHeaderCount();
            footerCount = ((BaseQuickAdapter) parent.getAdapter()).getFooterCount();
        }

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager){
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        }else if (layoutManager instanceof GridLayoutManager){
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
        }else if (layoutManager instanceof LinearLayoutManager){
            orientation = ((LinearLayoutManager) layoutManager).getOrientation();
        }

        //头部不绘制
        if (position < headerCount && !mDrawAtHeader)
            return;

        //尾部不绘制
        if (position >= parent.getAdapter().getItemCount() - footerCount && !mDrawAtFooter)
            return;

        //最后一项不绘制
        if(position == parent.getAdapter().getItemCount() - footerCount - 1 && !mDrawAtLast)
            return;

        if (orientation == OrientationHelper.VERTICAL) {
            outRect.bottom = mHeight;
        } else {
            outRect.right = mHeight;
        }
    }

    public void onDrawOver(@NonNull Canvas c, RecyclerView parent, @NonNull RecyclerView.State state) {

        if (parent.getAdapter() == null){
            return;
        }

        int orientation = 0;
        int headerCount = 0,dataCount;

        if (parent.getAdapter() instanceof BaseQuickAdapter){
            headerCount = ((BaseQuickAdapter) parent.getAdapter()).getHeaderCount();
            dataCount = ((BaseQuickAdapter) parent.getAdapter()).getData().size();
        }else {
            dataCount = parent.getAdapter().getItemCount();
        }
        int dataStartPosition = headerCount;
        int dataEndPosition = headerCount + dataCount - 1;


        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager){
            orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
        }else if (layoutManager instanceof GridLayoutManager){
            orientation = ((GridLayoutManager) layoutManager).getOrientation();
        }else if (layoutManager instanceof LinearLayoutManager){
            orientation = ((LinearLayoutManager) layoutManager).getOrientation();
        }
        int start,end;
        if (orientation == OrientationHelper.VERTICAL){
            start = parent.getPaddingLeft() + mPaddingLeft;
            end = parent.getWidth() - parent.getPaddingRight() - mPaddingRight;
        }else {
            start = parent.getPaddingTop() + mPaddingLeft;
            end = parent.getHeight() - parent.getPaddingBottom() - mPaddingRight;
        }

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            int position = parent.getChildAdapterPosition(child);


            //头部不绘制
            if (position < dataStartPosition && !mDrawAtHeader)
                continue;

            //尾部不绘制
            if (position > dataEndPosition && !mDrawAtFooter)
                continue;

            //最后一项不绘制
            if(position == dataEndPosition && !mDrawAtLast)
                continue;

            if (orientation == OrientationHelper.VERTICAL) {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int top = child.getBottom() + params.bottomMargin;
                int bottom = top + mHeight;
                mColorDrawable.setBounds(start, top, end, bottom);
                mColorDrawable.draw(c);
            } else {
                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
                int left = child.getRight() + params.rightMargin;
                int right = left + mHeight;
                mColorDrawable.setBounds(left, start, right, end);
                mColorDrawable.draw(c);
            }
        }
    }
}