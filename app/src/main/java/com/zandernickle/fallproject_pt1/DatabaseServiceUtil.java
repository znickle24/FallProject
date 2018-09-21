package com.zandernickle.fallproject_pt1;

import java.util.HashMap;

public class DatabaseServiceUtil {

    private int nextId = 1;
    private HashMap<Integer, User> database;

    public DatabaseServiceUtil() {
        database = new HashMap<>();
    }

    public void addUser(User user) {
        user.setId(nextId);
        database.put(nextId++, user);
    }

    public User getUser(int id) {
        return database.get(id);
    }


}
