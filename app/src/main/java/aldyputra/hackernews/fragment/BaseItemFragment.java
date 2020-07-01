package aldyputra.hackernews.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import aldyputra.hackernews.MainActivity;
import aldyputra.hackernews.R;
import aldyputra.hackernews.Utils;
import aldyputra.hackernews.adapter.MultiCommentsAdapter;
import aldyputra.hackernews.api.Comment;
import aldyputra.hackernews.api.Item;
import aldyputra.hackernews.databinding.FragmentItemBinding;
import aldyputra.hackernews.viewmodel.ItemViewModel;

public abstract class BaseItemFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public static final String ARG_ITEM = "argument_item";
    public static String favorite_title = "";
    public static boolean isFavorite = false;

    private TextView userText, timeText;
    protected TextView titleText, urlText, pointsText, numCommentsText, itemText;
    protected LinearLayout noCommentsLayout;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView recyclerView;
    protected MultiCommentsAdapter adapter;
    protected ItemViewModel itemViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        itemViewModel = new ViewModelProvider(this).get(ItemViewModel.class);


        Bundle args = getArguments();
        if(savedInstanceState != null)
            args = savedInstanceState;
        if(args != null) {
            itemViewModel.item = args.getParcelable(ARG_ITEM);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentItemBinding binding = FragmentItemBinding.inflate(inflater, container, false);
        swipeRefreshLayout = binding.itemSwipeRefresh;
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.redA200);
        recyclerView = binding.itemCommentsList;

        itemText = binding.itemText;
        titleText = binding.itemTitle;
        urlText = binding.itemUrl;
        userText = binding.itemUser;
        timeText = binding.itemTime;
        pointsText = binding.itemPoints;
        numCommentsText = binding.itemNumComments;
        noCommentsLayout = binding.itemNocommentsLayout;

        userText.setText(itemViewModel.item.getUser());
        timeText.setText(Utils.getAbbreviatedTimeSpan(itemViewModel.item.getTime()));

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        adapter = new CommentsAdapter(itemViewModel.commentsList,
//                itemViewModel.collapsedParentComments, itemViewModel.collapsedChildren);
        adapter = new MultiCommentsAdapter(itemViewModel.commentsList);
//        adapter = new MultiCommentsAdapter(itemViewModel.multiLevelData);
        recyclerView.setAdapter(adapter);
        observeItem(false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(ARG_ITEM, itemViewModel.item);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_item_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        WeakReference<Context> ref = new WeakReference<Context>(getContext());
        if(itemViewModel.item == null)
            return super.onOptionsItemSelected(item);
        String hnUrl = Utils.toHackerNewsUrl(itemViewModel.item.getId());
        switch (id) {
            case R.id.action_item_menu_favorite:
                favorite_title = titleText.getText().toString();
                isFavorite = true;
                Toast.makeText(getActivity(), "Success add to favorite.",
                        Toast.LENGTH_LONG).show();
                return true;
            case R.id.action_articles_refresh:
                swipeRefreshLayout.setRefreshing(true);
                observeItem(true);
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        adapter.dispose();
        super.onDestroy();
    }

    protected void observeItem(final boolean refreshComments) {
        long currentTime = System.currentTimeMillis();
        if(currentTime - itemViewModel.lastCommentsRefreshTime > Utils.CACHE_EXPIRATION) {
            itemViewModel.commentsFound = true;
            showTextNoComments();
            itemViewModel.lastCommentsRefreshTime = currentTime;
            itemViewModel.getItem().observe(getViewLifecycleOwner(), getItemObserver(refreshComments));
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    protected abstract Observer<Item> getItemObserver(final boolean refreshComments);

    protected void startObservingComments(long[] kids) {
        if(kids == null || kids.length == 0)
            return;
        observeComments(LongStream.of(kids).boxed().collect(Collectors.toList()));
        if(swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    protected void observeComments(List<Long> ids) {
        itemViewModel.getComments(ids).observe(getViewLifecycleOwner(), comments -> {
            List<Long> newKidsIds = new ArrayList<>();
            for(Comment comment : comments) {
                if(comment == null)
                    return;

                if(adapter != null)
                    adapter.addItem(comment);
                long[] kids = comment.getKids();
                if(kids != null) {
                    newKidsIds.addAll(LongStream.of(kids).boxed().collect(Collectors.toList()));
                }
            }
            if(newKidsIds.size() > 0)
                observeComments(newKidsIds);
        });
    }

    protected void showTextNoComments() {
        if(!itemViewModel.commentsFound)
            noCommentsLayout.setVisibility(View.VISIBLE);
        else
            noCommentsLayout.setVisibility(View.INVISIBLE);
    }


}
