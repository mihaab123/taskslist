package com.mihailovalex.taskslist_room.data;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "groups")
public final class Group {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "entryid")
    private final Long mId;

    @Nullable
    @ColumnInfo(name = "title")
    private final String mTitle;

    @Ignore
    public Group(Long mId, @Nullable String mTitle) {
        this.mId = mId;
        this.mTitle = mTitle;
    }
    public Long getId() {
        return mId;
    }

    @Nullable
    public String getTitle() {
        return mTitle;
    }
}
