package aldyputra.hackernews.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import aldyputra.hackernews.api.Item;
import aldyputra.hackernews.api.Story;
import aldyputra.hackernews.repository.HackerNewsRepository;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel {

    public List<Long> itemsIds;
    public List<Item> items;
    private HackerNewsRepository repository;
    public int argViewItems;
    public int lastItemLoadedIndex;
    public long lastIdsRefreshTime = 0L;

    public HomeViewModel() {
        repository = HackerNewsRepository.getInstance();
        itemsIds = new ArrayList<>();
        items = new ArrayList<>();
    }

    public LiveData<List<Long>> getItemsIds() {
        return repository.getTopStoriesIds();
    }

    public LiveData<? extends Item> getItem(long id) {
            return getStory(id);
    }

    public LiveData<List<? extends Item>> getItems(List<Long> ids) {
        return repository.getItems(ids);
    }

    private LiveData<Story> getStory(long id) {
        return repository.getStory(id);
    }

    public LiveData<List<Story>> getStories(List<Long> ids) {
        return repository.getStories(ids);
    }

}