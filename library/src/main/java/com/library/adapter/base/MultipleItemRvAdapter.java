package com.library.adapter.base;

import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.library.adapter.base.provider.BaseItemProvider;
import com.library.adapter.base.util.MultiTypeDelegate;
import com.library.adapter.base.util.ProviderDelegate;
import java.util.List;

/**
 * https://github.com/chaychan
 *
 * @author ChayChan
 * @description: When there are multiple entries, avoid too much business logic in convert(),Put the logic of each item in the corresponding ItemProvider
 * 当有多种条目的时候，避免在convert()中做太多的业务逻辑，把逻辑放在对应的ItemProvider中
 * @date 2018/3/21  9:55
 */

public abstract class MultipleItemRvAdapter<T> extends BaseQuickAdapter<T> {

    private SparseArray<BaseItemProvider> mItemProviders;
    protected ProviderDelegate mProviderDelegate;
    private MultiTypeDelegate<T> mMultiTypeDelegate;

    public MultipleItemRvAdapter(@Nullable List<T> data) {
        super(data);
    }

    /**
     * 用于adapter构造函数完成参数的赋值后调用
     * Called after the assignment of the argument to the adapter constructor
     */
    public void finishInitialize() {
        mProviderDelegate = new ProviderDelegate();

        setMultiTypeDelegate(new MultiTypeDelegate<T>() {
            @Override
            protected int getItemType(T t) {
                return getViewType(t);
            }
        });

        registerItemProvider();

        mItemProviders = mProviderDelegate.getItemProviders();

        for (int i = 0; i < mItemProviders.size(); i++) {
            int key = mItemProviders.keyAt(i);
            BaseItemProvider provider = mItemProviders.get(key);
            provider.mData = mData;
            getMultiTypeDelegate().registerItemType(key, provider.layout());
        }
    }

    protected abstract int getViewType(T t);

    public abstract void registerItemProvider();

    @Override
    protected void bindViewClickListener(ViewHolder viewHolder) {
        if (viewHolder == null) {
            return;
        }

        bindClick(viewHolder);

        super.bindViewClickListener(viewHolder);
    }

    @Override
    protected ViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        if (getMultiTypeDelegate() == null) {
            throw new IllegalStateException("please use setMultiTypeDelegate first!");
        }
        int layoutId = getMultiTypeDelegate().getLayoutId(viewType);
        return createBaseViewHolder(parent, layoutId);
    }

    @Override
    protected int getDefItemViewType(int position) {
        if (getMultiTypeDelegate() == null) {
            throw new IllegalStateException("please use setMultiTypeDelegate first!");
        }
        return getMultiTypeDelegate().getDefItemViewType(mData, position);
    }

    @Override
    protected void convert(@NonNull ViewHolder helper, T item) {
        int itemViewType = helper.getItemViewType();
        BaseItemProvider provider = mItemProviders.get(itemViewType);

        provider.mContext = helper.itemView.getContext();

        int position = helper.getLayoutPosition() - getHeaderCount();
        provider.convert(helper, item, position);
    }

    @Override
    protected void convertPayloads(@NonNull ViewHolder helper, T item, @NonNull List<Object> payloads) {
        int itemViewType = helper.getItemViewType();
        BaseItemProvider provider = mItemProviders.get(itemViewType);

        int position = helper.getLayoutPosition() - getHeaderCount();
        provider.convertPayloads(helper, item, position, payloads);
    }

    private void bindClick(final ViewHolder helper) {
        OnItemClickListener clickListener = getOnItemClickListener();
        OnItemLongClickListener longClickListener = getOnItemLongClickListener();

        if (clickListener != null && longClickListener != null) {
            //如果已经设置了子条目点击监听和子条目长按监听
            // If you have set up a sub-entry click monitor and sub-entries long press listen
            return;
        }

        if (clickListener == null) {
            //如果没有设置点击监听，则回调给itemProvider
            //Callback to itemProvider if no click listener is set
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = helper.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) {
                        return;
                    }
                    position -= getHeaderCount();

                    int itemViewType = helper.getItemViewType();
                    BaseItemProvider provider = mItemProviders.get(itemViewType);

                    provider.onClick(helper, mData.get(position), position);
                }
            });
        }

        if (longClickListener == null) {
            //如果没有设置长按监听，则回调给itemProvider
            // If you do not set a long press listener, callback to the itemProvider
            helper.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = helper.getAdapterPosition();
                    if (position == RecyclerView.NO_POSITION) {
                        return false;
                    }
                    position -= getHeaderCount();

                    int itemViewType = helper.getItemViewType();
                    BaseItemProvider provider = mItemProviders.get(itemViewType);
                    return provider.onLongClick(helper, mData.get(position), position);
                }
            });
        }
    }

    public void setMultiTypeDelegate(MultiTypeDelegate<T> multiTypeDelegate) {
        mMultiTypeDelegate = multiTypeDelegate;
    }

    public MultiTypeDelegate<T> getMultiTypeDelegate() {
        return mMultiTypeDelegate;
    }

}
