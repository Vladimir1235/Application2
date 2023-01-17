package dev.vvasiliev.audio.service.state;

import android.os.Parcel;
import android.os.Parcelable;

public enum AudioServiceState implements Parcelable {
    PLAYING, STOPPED, NOT_CREATED, UNKNOWN;

    AudioServiceState() {
    }

    AudioServiceState(Parcel in) {
    }

    public AudioServiceState readFromParcel(Parcel parcel) {
        return CREATOR.createFromParcel(parcel);
    }

    public static final Creator<AudioServiceState> CREATOR = new Creator<AudioServiceState>() {
        @Override
        public AudioServiceState createFromParcel(Parcel in) {
            switch (in.readString()) {
                case "PLAYING":
                    return PLAYING;
                case "STOPPED":
                    return STOPPED;
                case "NOT_CREATED":
                    return NOT_CREATED;
                case "UNKNOWN":
                    return UNKNOWN;
            }
            return NOT_CREATED;
        }

        @Override
        public AudioServiceState[] newArray(int size) {
            return new AudioServiceState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        switch (this) {
            case PLAYING:
                parcel.writeString("PLAYING");
            case STOPPED:
                parcel.writeString("STOPPED");
            case NOT_CREATED:
                parcel.writeString("NOT_CREATED");
            case UNKNOWN:
                parcel.writeString("UNKNOWN");
        }
    }
}
