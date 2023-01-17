package dev.vvasiliev.audio.service.data;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

public class SongMetadata implements Parcelable {
    public long id;
    public Uri uri;
    public String title;
    public String artist;

    public SongMetadata(long id, Uri uri, String title, String artist) {
        this.id = id;
        this.uri = uri;
        this.title = title;
        this.artist = artist;
    }

    protected SongMetadata(Parcel in) {
        id = in.readLong();
        uri = in.readParcelable(Uri.class.getClassLoader());
        title = in.readString();
        artist = in.readString();
    }

    public static final Creator<SongMetadata> CREATOR = new Creator<SongMetadata>() {
        @Override
        public SongMetadata createFromParcel(Parcel in) {
            return new SongMetadata(in);
        }

        @Override
        public SongMetadata[] newArray(int size) {
            return new SongMetadata[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeParcelable(uri, i);
        parcel.writeString(title);
        parcel.writeString(artist);
    }
}