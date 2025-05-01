package com.example.smd_assignment_3;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "taskmanager.db";
    private static final int DATABASE_VERSION = 1;

    private static final String SQL_CREATE_TASKS_TABLE =
            "CREATE TABLE " + TaskContract.TaskEntry.TABLE_NAME + " (" +
                    TaskContract.TaskEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskContract.TaskEntry.COLUMN_TITLE + " TEXT," +
                    TaskContract.TaskEntry.COLUMN_DESCRIPTION + " TEXT," +
                    TaskContract.TaskEntry.COLUMN_DATETIME + " INTEGER," +
                    TaskContract.TaskEntry.COLUMN_STATUS + " TEXT)";

    private static final String SQL_CREATE_NOTIFICATIONS_TABLE =
            "CREATE TABLE " + TaskContract.NotificationEntry.TABLE_NAME + " (" +
                    TaskContract.NotificationEntry._ID + " INTEGER PRIMARY KEY," +
                    TaskContract.NotificationEntry.COLUMN_MESSAGE + " TEXT," +
                    TaskContract.NotificationEntry.COLUMN_DATETIME + " INTEGER)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TASKS_TABLE);
        db.execSQL(SQL_CREATE_NOTIFICATIONS_TABLE);

        // Add some dummy notifications
        ContentValues values = new ContentValues();
        values.put(TaskContract.NotificationEntry.COLUMN_MESSAGE, "Welcome to Task Manager!");
        values.put(TaskContract.NotificationEntry.COLUMN_DATETIME, System.currentTimeMillis());
        db.insert(TaskContract.NotificationEntry.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(TaskContract.NotificationEntry.COLUMN_MESSAGE, "You have created your first task!");
        values.put(TaskContract.NotificationEntry.COLUMN_DATETIME, System.currentTimeMillis() - 86400000); // 1 day ago
        db.insert(TaskContract.NotificationEntry.TABLE_NAME, null, values);

        values = new ContentValues();
        values.put(TaskContract.NotificationEntry.COLUMN_MESSAGE, "Don't forget to complete your pending tasks");
        values.put(TaskContract.NotificationEntry.COLUMN_DATETIME, System.currentTimeMillis() - 172800000); // 2 days ago
        db.insert(TaskContract.NotificationEntry.TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.TaskEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TaskContract.NotificationEntry.TABLE_NAME);
        onCreate(db);
    }

    // Task CRUD Operations
    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COLUMN_TITLE, task.getTitle());
        values.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskContract.TaskEntry.COLUMN_DATETIME, task.getDatetime());
        values.put(TaskContract.TaskEntry.COLUMN_STATUS, task.getStatus());

        return db.insert(TaskContract.TaskEntry.TABLE_NAME, null, values);
    }

    public List<Task> getFutureTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        long currentTime = System.currentTimeMillis();
        String selection = TaskContract.TaskEntry.COLUMN_DATETIME + " > ?";
        String[] selectionArgs = {String.valueOf(currentTime)};
        String sortOrder = TaskContract.TaskEntry.COLUMN_DATETIME + " ASC";

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID)));
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DESCRIPTION)));
                task.setDatetime(cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DATETIME)));
                task.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_STATUS)));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return taskList;
    }

    public List<Task> getPastTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        long currentTime = System.currentTimeMillis();
        String selection = TaskContract.TaskEntry.COLUMN_DATETIME + " <= ?";
        String[] selectionArgs = {String.valueOf(currentTime)};
        String sortOrder = TaskContract.TaskEntry.COLUMN_DATETIME + " DESC"; // Latest first

        Cursor cursor = db.query(
                TaskContract.TaskEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task();
                task.setId(cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry._ID)));
                task.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_TITLE)));
                task.setDescription(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DESCRIPTION)));
                task.setDatetime(cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_DATETIME)));
                task.setStatus(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.TaskEntry.COLUMN_STATUS)));
                taskList.add(task);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return taskList;
    }

    // Notification Operations
    public List<Notification> getAllNotifications() {
        List<Notification> notificationList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String sortOrder = TaskContract.NotificationEntry.COLUMN_DATETIME + " DESC"; // Latest first

        Cursor cursor = db.query(
                TaskContract.NotificationEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        );

        if (cursor.moveToFirst()) {
            do {
                Notification notification = new Notification();
                notification.setId(cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.NotificationEntry._ID)));
                notification.setMessage(cursor.getString(cursor.getColumnIndexOrThrow(TaskContract.NotificationEntry.COLUMN_MESSAGE)));
                notification.setDatetime(cursor.getLong(cursor.getColumnIndexOrThrow(TaskContract.NotificationEntry.COLUMN_DATETIME)));
                notificationList.add(notification);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return notificationList;
    }

    public long addNotification(Notification notification) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskContract.NotificationEntry.COLUMN_MESSAGE, notification.getMessage());
        values.put(TaskContract.NotificationEntry.COLUMN_DATETIME, notification.getDatetime());

        return db.insert(TaskContract.NotificationEntry.TABLE_NAME, null, values);
    }
}
