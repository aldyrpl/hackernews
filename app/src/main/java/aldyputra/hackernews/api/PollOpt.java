package aldyputra.hackernews.api;

import android.os.Parcel;
import android.os.Parcelable;

public class PollOpt extends Item {

    public static final Parcelable.Creator<PollOpt> CREATOR = new Parcelable.Creator<PollOpt>() {

        @Override
        public PollOpt createFromParcel(Parcel parcel) {
            return new PollOpt(parcel);
        }

        @Override
        public PollOpt[] newArray(int size) {
            return new PollOpt[size];
        }
    };

    public PollOpt(Parcel in) {
        super(in);
        this.score = in.readInt();
        this.text = in.readString();
        this.poll = in.readLong();
    }

    int score;

    String text;

    long poll;

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(score);
        dest.writeString(text);
        dest.writeLong(poll);
    }
}
