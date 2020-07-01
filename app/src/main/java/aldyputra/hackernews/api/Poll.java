package aldyputra.hackernews.api;

import android.os.Parcel;
import android.os.Parcelable;

public class Poll extends Item {

    public static final Parcelable.Creator<Poll> CREATOR = new Parcelable.Creator<Poll>() {

        @Override
        public Poll createFromParcel(Parcel parcel) {
            return new Poll(parcel);
        }

        @Override
        public Poll[] newArray(int size) {
            return new Poll[size];
        }
    };

    public Poll(Parcel in) {
        super(in);
        this.descendants = in.readInt();
        this.kids = new long[in.readInt()];
        in.readLongArray(kids);
        this.score = in.readInt();
        this.parts = new long[in.readInt()];
        in.readLongArray(parts);
        this.text = in.readString();
        this.title = in.readString();
    }

    int descendants;

    long[] kids;

    int score;

    long[] parts;

    String text;

    String title;

    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(descendants);
        dest.writeInt(kids.length);
        dest.writeLongArray(kids);
        dest.writeInt(score);
        dest.writeInt(parts.length);
        dest.writeLongArray(parts);
        dest.writeString(text);
        dest.writeString(title);
    }
}
