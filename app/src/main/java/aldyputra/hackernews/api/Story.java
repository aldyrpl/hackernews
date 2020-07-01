package aldyputra.hackernews.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Story extends Item {

    public static final Parcelable.Creator<Story> CREATOR = new Parcelable.Creator<Story>(){

        @Override
        public Story createFromParcel(Parcel parcel) {
            return new Story(parcel);
        }

        @Override
        public Story[] newArray(int size) {
            return new Story[size];
        }
    };

    public Story(){}

    public Story(Parcel in) {
        super(in);
        this.descendants = in.readInt();
        this.kids = new long[in.readInt()];
        in.readLongArray(kids);
        this.score = in.readInt();
        this.title = in.readString();
        this.url = in.readString();
        this.text = in.readString();
    }

    int descendants;

    long[] kids;

    int score;

    String title;

    String url;

    String text;

    public int getDescendants() {
        return descendants;
    }

    public long[] getKids() {
        return kids;
    }

    public int getScore() {
        return score;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getText() {
        return text;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(descendants);
        dest.writeInt(kids.length);
        dest.writeLongArray(kids);
        dest.writeInt(score);
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(text);
    }
}
