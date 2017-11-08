package com.edwin.androidskelton.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.edwin.androidskelton.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Provides UI for the view with List.
 */
public class ListContentFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(
                R.layout.recycler_view, container, false);

        ContentAdapter adapter = new ContentAdapter(recyclerView.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return recyclerView;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_avatar)
        ImageView listAvatar;
        @BindView(R.id.list_title)
        TextView listTitle;
        @BindView(R.id.list_desc)
        TextView listDesc;

        public ViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.item_list, parent, false));

            //// butterknife
            ButterKnife.bind(this, itemView);
            //listAvatar = (ImageView) itemView.findViewById(R.id.list_avatar);
            //listTitle = (TextView) itemView.findViewById(R.id.list_title);
            //listDesc = (TextView) itemView.findViewById(R.id.list_desc);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra(DetailActivity.EXTRA_POSITION, getAdapterPosition());
                    context.startActivity(intent);
                }
            });
        }
    }

    /**
     * Adapter to display recycler view.
     */
    public static class ContentAdapter extends RecyclerView.Adapter<ViewHolder> {
        // Set numbers of List in RecyclerView.
        private static final int LENGTH = 18;
        private final String[] mPlaces;
        private final String[] mPlaceDesc;
        private final Drawable[] mPlaceAvators;

        public ContentAdapter(Context context) {
            Resources resources = context.getResources();
            mPlaces = resources.getStringArray(R.array.places);
            mPlaceDesc = resources.getStringArray(R.array.place_desc);
            TypedArray a = resources.obtainTypedArray(R.array.place_avator);
            mPlaceAvators = new Drawable[a.length()];
            for (int i = 0; i < mPlaceAvators.length; i++) {
                mPlaceAvators[i] = a.getDrawable(i);
            }
            a.recycle();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()), parent);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.listAvatar.setImageDrawable(mPlaceAvators[position % mPlaceAvators.length]);
            holder.listTitle.setText(mPlaces[position % mPlaces.length]);
            holder.listDesc.setText(mPlaceDesc[position % mPlaceDesc.length]);
        }

        @Override
        public int getItemCount() {
            return LENGTH;
        }
    }
}
