package aldyputra.hackernews.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import aldyputra.hackernews.api.Comment;
import aldyputra.hackernews.api.Item;
import aldyputra.hackernews.repository.HackerNewsRepository;

public class ItemViewModel extends ViewModel {

    public HashMap<Long, List<Comment>> collapsedParentComments; //id comment on which was clicked Collapse and List of its children
    public HashMap<Long, Long> collapsedChildren; //id comment and id of the collapsed parent indexable in collapsedParentComments
    public LinkedList<Comment> commentsList; // TODO Su internet suggeriscono per gli adapter di usare arraylist con fast random access invece di linked list, dato che l'adapter chiama get() tante volte per ciascun oggetto
//    public MultiLevelData multiLevelData;
    private HackerNewsRepository repository;
    public long lastCommentsRefreshTime = 0L;
    public boolean commentsFound = false;
    public Item item;

    public ItemViewModel() {
        repository = HackerNewsRepository.getInstance();
//        multiLevelData = new MultiLevelData();
        commentsList = new LinkedList<>();
//        collapsedParentComments = new HashMap<>();
//        collapsedChildren = new HashMap<>();
    }

    public LiveData<? extends Item> getItem() {
        if(item != null)
            return repository.getItem(item.getId());
        return null;
    }

    public LiveData<List<Comment>> getComments(List<Long> ids) { return repository.getComments(ids); }

    public LiveData<Comment> getComment(long id) { return repository.getComment(id); }

}
