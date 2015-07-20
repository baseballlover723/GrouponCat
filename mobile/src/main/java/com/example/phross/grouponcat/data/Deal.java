package com.example.phross.grouponcat.data;

import java.util.List;

/**
 * Created by phross on 7/20/15.
 */
public class Deal {
    static final String ID = "id";
    static final String LAST_SEEN = "lastSeen";
    public static final String TABLE = "deals";
    public static final String CREATE = "CREATE TABLE " + TABLE + "(" + ID + " TEXT PRIMARY KEY NOT NULL, " + LAST_SEEN + " INTEGER)";
    public String merchantName;
    public String announcementTitle;
    public String dealUrl;
    public String title;
    public Boolean isSoldOut;
    public String shortAnnouncementTitle;
    public String id;
    public List<Option> options;
    public String address;
    public float distance;
}
