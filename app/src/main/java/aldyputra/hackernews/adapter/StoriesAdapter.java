package aldyputra.hackernews.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import aldyputra.hackernews.R;
import aldyputra.hackernews.Utils;
import aldyputra.hackernews.api.Item;
import aldyputra.hackernews.api.Story;
import aldyputra.hackernews.databinding.FragmentHomeArticlesListItemBinding;
import aldyputra.hackernews.fragment.HomeFragment;
import aldyputra.hackernews.fragment.StoryFragment;

public class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

    private int argViewStories;
    private List<? extends Item> stories;
    private Context context;

    public StoriesAdapter(List<? extends Item> stories, int argViewStories) {
        if (stories == null)
            this.stories = new ArrayList<>();
        else
            this.stories = stories;
        this.argViewStories = argViewStories;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(
                FragmentHomeArticlesListItemBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false),
                onStoryClickListener, argViewStories);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mItem = stories.get(position);
        String titleText = "", numCommentsText = "", urlText = "", pointsText = "", userText = "", timeText = "";
        if(holder.mItem instanceof Story) {
            Story mStory = (Story) holder.mItem;
            titleText = mStory.getTitle();
            numCommentsText = Integer.toString(mStory.getDescendants());
            if(mStory.getUrl() == null || mStory.getUrl().isEmpty())
                urlText = context.getString(R.string.item_base_url);
            else {
                Uri uri = Uri.parse(mStory.getUrl());
                urlText = uri.getHost();
            }
            pointsText = Integer.toString(mStory.getScore());
            userText = mStory.getUser();
            timeText = Utils.getAbbreviatedTimeSpan(mStory.getTime());
        }
        holder.mTitle.setText(titleText);
        holder.mNumComments.setText(numCommentsText);
        holder.mUrl.setText(urlText);
        holder.mPoints.setText(pointsText);
        holder.mUser.setText(userText);
        holder.mTime.setText(timeText);
    }

    @Override
    public int getItemCount() {
        if (stories != null)
            return stories.size();
        return 0;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        context = recyclerView.getContext();
    }

    private OnItemClickListener onStoryClickListener = new OnItemClickListener() {

        @Override
        public void onItemContentClick(View view, Item item) {
            switch (item.getType()) {
                case STORY_TYPE:
                    NavController navController = Navigation.findNavController(view);
                    Bundle args = new Bundle();
                    Story mStory = (Story) item;
                    args.putParcelable(StoryFragment.ARG_ITEM, mStory);
                    HomeFragment.navigateToStory(navController, argViewStories, args);
                    break;
                case POLL_TYPE:
                case POLLOPT_TYPE:
                default: break;
            }
        }

        @Override
        public void onItemCommentsClick(View view, Item item) {
            NavController navController = Navigation.findNavController(view);
            Bundle args = new Bundle();
            Story mStory = (Story) item;
            args.putParcelable(StoryFragment.ARG_ITEM, mStory);
            HomeFragment.navigateToStory(navController, argViewStories, args);
        }

        @Override
        public void onItemMenuClick(MenuItem menuItem, View view, Item item) {
            onItemContentClick(view, item);
        }

    };


    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            PopupMenu.OnMenuItemClickListener{

        final FragmentHomeArticlesListItemBinding mBinding;
        final ConstraintLayout mCommentsLayout;
        final TextView mTitle;
        final TextView mNumComments;
        final TextView mUrl;
        final TextView mPoints;
        final TextView mUser;
        final TextView mTime;
        final View mView;

        int argViewStories;
        Item mItem;
        private OnItemClickListener mListener;

        ViewHolder(@NonNull FragmentHomeArticlesListItemBinding binding, OnItemClickListener listener, int argViewStories) {
            super(binding.getRoot());
            mListener = listener;
            mBinding = binding;
            mView = binding.getRoot();
            this.argViewStories = argViewStories;
            mCommentsLayout = binding.articleCommentsLayout;
            mTitle = binding.articleTitle;
            mNumComments = binding.articleNumComments;
            mCommentsLayout.setOnClickListener(view -> {
                if(mListener != null)
                    mListener.onItemCommentsClick(view, mItem);
            });
            mUrl = binding.articleUrl;
            mPoints = binding.articlePoints;
            mUser = binding.articleUser;
            mTime = binding.articleTime;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null)
                mListener.onItemContentClick(v, mItem);
        }


        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener == null)
                return false;
            mListener.onItemMenuClick(item, mView, mItem);
            return true;
        }
    }

    private interface OnItemClickListener {

        void onItemContentClick(View view, Item item);

        void onItemCommentsClick(View view, Item item);

        void onItemMenuClick(MenuItem menuItem, View view, Item item);

    }
}
