package aldyputra.hackernews.api;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.util.Objects;

public class Item implements Parcelable {

    public static final Parcelable.Creator<Item> CREATOR = new Parcelable.Creator<Item>() {

        @Override
        public Item createFromParcel(Parcel parcel) {
            return new Item(parcel);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    Item(){}

    Item(long id) {
        this.id = id;
    }

    public Item(Parcel in) {
        this.id = in.readLong();
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.isDeleted = in.readBoolean();
            this.isDead = in.readBoolean();
        }
        else {
            this.isDeleted = in.readByte() != 0;
            this.isDead = in.readByte() != 0;
        }
        this.type = Type.valueOf(in.readString());
        this.user = in.readString();
        this.time = in.readLong();
    }

    long id;

    @SerializedName("deleted")
    boolean isDeleted;

    Type type;

    @SerializedName("by")
    String user;

    long time;

    @SerializedName("dead")
    boolean isDead;

    public long getId(){
        return id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public Type getType() {
        return type;
    }

    public String getUser() {
        return user;
    }

    public long getTime() {
        return time;
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            dest.writeBoolean(isDeleted);
            dest.writeBoolean(isDead);
        } else {
            dest.writeByte((byte) (isDeleted ? 1 : 0));
            dest.writeByte((byte) (isDead ? 1 : 0));
        }
        dest.writeString(type.getType());
        dest.writeString(user);
        dest.writeLong(time);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @NonNull
    @Override
    public String toString() {
        return "Item Id: " + id + " by " + user;
    }
}
