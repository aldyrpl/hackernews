package aldyputra.hackernews.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import java.lang.ref.WeakReference;

import aldyputra.hackernews.Utils;
import aldyputra.hackernews.api.Item;
import aldyputra.hackernews.api.Story;

public class StoryFragment extends BaseItemFragment {

    public static StoryFragment newInstance(Story story) {

        Bundle args = new Bundle();
        args.putParcelable(ARG_ITEM, story);
        StoryFragment fragment = new StoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = super.onCreateView(inflater, container, savedInstanceState);

        itemText.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setVisibility(View.VISIBLE);

        if(itemViewModel.item != null && itemViewModel.item instanceof Story) {
            Story story = (Story) itemViewModel.item;
            startObservingComments(story.getKids());
            titleText.setText(story.getTitle());
            Uri uri = Uri.parse(story.getUrl());
            urlText.setText(uri.getHost());
            pointsText.setText(String.valueOf(story.getScore()));
            numCommentsText.setText(String.valueOf(story.getDescendants()));
        }

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        WeakReference<Context> ref = new WeakReference<>(getContext());
        if(itemViewModel.item == null || !(itemViewModel.item instanceof Story))
            return super.onOptionsItemSelected(item);
        String hnUrl = Utils.toHackerNewsUrl(itemViewModel.item.getId());
        return super.onOptionsItemSelected(item);

    }

    @Override
    protected Observer<Item> getItemObserver(boolean refreshComments) {
        return new Observer<Item>() {
            @Override
            public void onChanged(Item item) {
                if(item instanceof Story) {
                    Story story = (Story) item;
                    if(story.getKids() == null || story.getKids().length == 0) {
                        itemViewModel.commentsFound = false;
                        showTextNoComments();
                    }
                    if(refreshComments) {
                        int size = itemViewModel.commentsList.size();
                        itemViewModel.commentsList.clear();
//                        int size = itemViewModel.multiLevelData.itemsSize();
//                        itemViewModel.multiLevelData.clear();
                        if(adapter != null)
                            adapter.notifyItemRangeRemoved(0, size);
                        startObservingComments(story.getKids());
                    }
                }
            }
        };
    }

    @Override
    public void onRefresh() {
        observeItem(true);
    }

}
