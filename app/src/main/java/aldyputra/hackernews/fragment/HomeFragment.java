package aldyputra.hackernews.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import aldyputra.hackernews.MainActivity;
import aldyputra.hackernews.Utils;
import aldyputra.hackernews.adapter.StoriesAdapter;
import aldyputra.hackernews.api.Item;
import aldyputra.hackernews.api.Type;
import aldyputra.hackernews.databinding.FragmentHomeBinding;
import aldyputra.hackernews.viewmodel.HomeViewModel;
import aldyputra.hackernews.R;
import aldyputra.hackernews.api.Story;

public class HomeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static final int TOP_STORIES_VIEW = 0;

    public static final String ARG_VIEW_STORIES = "arg_view_stories";

    private static final int NUM_LOAD_ITEMS = 20;

    private ProgressDialog pd;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private StoriesAdapter adapter;
    private HomeViewModel homeViewModel;

    public static LinearLayout favoriteLayout;
    public static TextView favoriteTitle;

    public static HomeFragment newInstance(int argViewStories) {
        Bundle args = new Bundle();
        args.putInt(ARG_VIEW_STORIES, argViewStories);
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        Bundle args = getArguments();
        if(savedInstanceState != null)
            args = savedInstanceState;
        if(args != null) {
            homeViewModel.argViewItems = args.getInt(ARG_VIEW_STORIES);
            homeViewModel.lastIdsRefreshTime = 0;
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        FragmentHomeBinding binding = FragmentHomeBinding.inflate(inflater, container, false);
        swipeRefreshLayout = binding.articlesSwipeRefresh;
        recyclerView = binding.articlesList;
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(R.color.redA200);
        recyclerView.addOnScrollListener(onScrollListener);
        favoriteLayout = binding.favoriteLayout;
        favoriteTitle = binding.favoriteTitle;
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        ShowFavorite();
    }

    public static void ShowFavorite(){
        if(BaseItemFragment.isFavorite == true) {
            favoriteLayout.setVisibility(View.VISIBLE);
            favoriteTitle.setText(BaseItemFragment.favorite_title);
        }else{
            favoriteLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(homeViewModel.items != null) {
            adapter = new StoriesAdapter(homeViewModel.items, homeViewModel.argViewItems);
            recyclerView.setAdapter(adapter);
        }
        showProgressDialog();
        refreshArticles();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ARG_VIEW_STORIES, homeViewModel.argViewItems);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_articles_refresh:
                swipeRefreshLayout.setRefreshing(true);
                refreshArticles();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {

        int scrollY = 0;

        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            int numIds = homeViewModel.itemsIds.size();
            if(newState == RecyclerView.SCROLL_STATE_IDLE && scrollY > 0 && numIds > 0) {
                int startIndex = homeViewModel.lastItemLoadedIndex + 1;
                int endIndex = Math.min(startIndex + NUM_LOAD_ITEMS, homeViewModel.itemsIds.size());
                observeItems(homeViewModel.itemsIds.subList(startIndex, endIndex));

                homeViewModel.lastItemLoadedIndex = endIndex - 1;
                preFetchUrls(startIndex, homeViewModel.lastItemLoadedIndex);
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            scrollY = dy;
        }
    };

    @Override
    public void onRefresh() {
        refreshArticles();
    }

    public void showProgressDialog(){
        pd = new ProgressDialog(MainActivity._this);
        pd.setMessage("Load News...");
        pd.show();
    }

    private void refreshArticles() {
        long currentTime = System.currentTimeMillis();
        if(currentTime - homeViewModel.lastIdsRefreshTime > Utils.CACHE_EXPIRATION) {
            homeViewModel.lastIdsRefreshTime = currentTime;
            homeViewModel.getItemsIds().observe(getViewLifecycleOwner(), new Observer<List<Long>>() {
                @Override
                public void onChanged(List<Long> ids) {
                    homeViewModel.itemsIds.clear();
                    int size = homeViewModel.items.size();
                    homeViewModel.items.clear();
                    if (adapter != null)
                        adapter.notifyItemRangeRemoved(0, size);
                    homeViewModel.itemsIds.addAll(ids);
                    homeViewModel.lastItemLoadedIndex = 0;
                    observeItems(ids.subList(0, NUM_LOAD_ITEMS));
                    homeViewModel.lastItemLoadedIndex += NUM_LOAD_ITEMS - 1;
                    preFetchUrls(0, homeViewModel.lastItemLoadedIndex);
                    swipeRefreshLayout.setRefreshing(false);
                    pd.dismiss();
                }
            });
        } else {
            swipeRefreshLayout.setRefreshing(false);
            pd.dismiss();
        }
    }

    private void observeItem(long id) {
        homeViewModel.getItem(id).observe(this, (item) -> {
                if(!homeViewModel.items.contains(item)) {
                    int pos = homeViewModel.items.size();
                    homeViewModel.items.add(item);
                    if (adapter != null)
                        adapter.notifyItemInserted(pos);
                }
        });
    }

    private void observeItems(List<Long> ids) {
        //homeViewModel.start2 = System.nanoTime();
        homeViewModel.getItems(ids).observe(this, (items -> {
            for(Item item : items) {
                if(!homeViewModel.items.contains(item)) {
                    int pos = homeViewModel.items.size();
                    homeViewModel.items.add(item);
                    if(adapter != null)
                        adapter.notifyItemInserted(pos);
                }
            }
        }));
    }

    private void preFetchUrls(int startIndex, int endIndex) {
        if(endIndex <= startIndex || startIndex >= homeViewModel.items.size() || endIndex >= homeViewModel.items.size())
            return;
        List<Uri> uris = new ArrayList<>(endIndex - startIndex + 1);
        for(int i = startIndex; i <= endIndex; i++) {
            Item item = homeViewModel.items.get(i);
            if(item.getType() == Type.STORY_TYPE && item instanceof Story) {
                Story story = (Story) item;
                uris.add(Uri.parse(story.getUrl()));
            }
        }
    }

    public static void navigateToStory(NavController navController, int currentArgViewStory, Bundle args) {
        int navId = 0;
        switch (currentArgViewStory) {
            case TOP_STORIES_VIEW: navId = R.id.action_nav_topstories_to_nav_story; break;
            default: return;
        }
        navController.navigate(navId, args);
    }
}