package aldyputra.hackernews.api;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;
import io.reactivex.Observable;

public interface HackerNewsApi {

    String BASE_URL = "https://hacker-news.firebaseio.com/v0/";
    String HACKER_NEWS_BASE_URL = "https://news.ycombinator.com/item?id=";

    int MAX_TOP_STORIES_ITEMS = 500;

    @GET("topstories.json")
    Observable<List<Long>> getTopStories();

    @GET("showstories.json")
    Observable<List<Long>> getShowStories();

    @GET("item/{id}.json")
    Observable<Story> getStory(@Path("id") long id);

    @GET("item/{id}.json")
    Observable<Comment> getComment(@Path("id") long id);

    @GET("item/{id}.json")
    Observable<Item> getItem(@Path("id") long id);

    @GET("item/{id}.json")
    Observable<ResponseBody> getCommentBody(@Path("id") long id);

}
