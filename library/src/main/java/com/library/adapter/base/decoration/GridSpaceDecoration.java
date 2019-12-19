package com.library.adapter.base.decoration;

import android.graphics.Rect;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.State;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.library.adapter.base.BaseQuickAdapter;

/**
 * @author MBP
 * @date 2019-12-19
 */

public class GridSpaceDecoration extends RecyclerView.ItemDecoration{

    private int halfHorizontalSpace,halfVerticalSpace;
    private boolean mDrawAtHeader = false;
    private boolean mDrawAtFooter = false;


    public GridSpaceDecoration(int itemSpace) {
        this.halfHorizontalSpace = itemSpace / 2;
        this.halfVerticalSpace = this.halfHorizontalSpace;
    }

    public GridSpaceDecoration(int horizontalSpace,int verticalSpace) {
        this.halfHorizontalSpace = horizontalSpace / 2;
        this.halfVerticalSpace = verticalSpace / 2;
    }

    public void setDrawAtHeader(final boolean drawAtHeader) {
        mDrawAtHeader = drawAtHeader;
    }

    public void setDrawAtFooter(final boolean drawAtFooter) {
        mDrawAtFooter = drawAtFooter;
    }

    @Override
    public void getItemOffsets(@NonNull final Rect outRect, @NonNull final View view,
            @NonNull final RecyclerView parent,
            @NonNull final State state) {

        if(parent.getAdapter() == null)
            return;

        int position = parent.getChildAdapterPosition(view);

        int headerCount = 0,footerCount = 0;
        if (parent.getAdapter() instanceof BaseQuickAdapter){
            headerCount = ((BaseQuickAdapter) parent.getAdapter()).getHeaderCount();
            footerCount = ((BaseQuickAdapter) parent.getAdapter()).getFooterCount();
        }

        //头部不绘制
        if (position < headerCount && !mDrawAtHeader)
            return;

        //尾部不绘制
        if (position >= parent.getAdapter().getItemCount() - footerCount && !mDrawAtFooter)
            return;


        int spanCount = 0;

        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof StaggeredGridLayoutManager){
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }else if (layoutManager instanceof GridLayoutManager){
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }

        outRect.left = halfHorizontalSpace;
        outRect.right = halfHorizontalSpace;

        if(position >= spanCount){
            outRect.top = halfVerticalSpace;
        }

        int itemCount = parent.getAdapter().getItemCount();
        int remainder = itemCount % spanCount;
        remainder = (remainder == 0) ? spanCount : remainder;

        if(position < itemCount - remainder){
            outRect.bottom = halfVerticalSpace;
        }



    }
}
