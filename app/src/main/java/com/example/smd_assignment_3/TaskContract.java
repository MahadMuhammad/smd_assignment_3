package com.example.smd_assignment_3;

import android.provider.BaseColumns;

public final class TaskContract {

    private TaskContract() {}

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_DATETIME = "datetime";
        public static final String COLUMN_STATUS = "status";
    }

    public static class NotificationEntry implements BaseColumns {
        public static final String TABLE_NAME = "notifications";
        public static final String COLUMN_MESSAGE = "message";
        public static final String COLUMN_DATETIME = "datetime";
    }
}
