package apps.jizzu.simpletodo.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import apps.jizzu.simpletodo.R;
import apps.jizzu.simpletodo.activity.MainActivity;

import static android.content.ContentValues.TAG;

/**
 * Adds the setEmptyView method for the RecyclerView.
 */
public class RecyclerViewEmptySupport extends RecyclerView {

    private View mEmptyView;
    private Context mContext;

    public void checkIfEmpty() {
        if (mEmptyView != null && getAdapter() != null && !MainActivity.mSearchViewIsOpen) {
            final boolean emptyViewVisible = getAdapter().getItemCount() == 0;
            mEmptyView.setVisibility(emptyViewVisible ? VISIBLE : GONE);
            setVisibility(emptyViewVisible ? GONE : VISIBLE);

            Log.d(TAG, "checkIfEmpty");

            if (emptyViewVisible) {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.empty_view_animation);
                anim.setStartOffset(300);
                anim.setDuration(300);
                mEmptyView.startAnimation(anim);
                Log.d(TAG, "checkIfEmpty: Start animation");
            }
        }
    }

    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            checkIfEmpty();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            checkIfEmpty();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            checkIfEmpty();
        }
    };

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        final Adapter oldAdapter = getAdapter();
        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }

        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
        checkIfEmpty();
    }

    public void setEmptyView(@Nullable View emptyView) {
        mEmptyView = emptyView;
        checkIfEmpty();
    }

    public RecyclerViewEmptySupport(Context context) {
        super(context);
        mContext = context;
    }

    public RecyclerViewEmptySupport(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RecyclerViewEmptySupport(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
