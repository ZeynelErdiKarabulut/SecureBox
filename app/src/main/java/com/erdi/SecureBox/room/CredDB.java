package com.erdi.SecureBox.room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.erdi.SecureBox.models.ViyCred;

@Database(entities = {ViyCred.class}, version = 5)
public abstract class CredDB extends RoomDatabase {

    private static CredDB instance;

    public abstract CredDao viyCredDao();

    public static synchronized CredDB getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    CredDB.class, "CredsDB")
                    .setJournalMode(JournalMode.TRUNCATE)
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private CredDao credDao;

        private PopulateDbAsyncTask(CredDB db) {
            credDao = db.viyCredDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Do anything
            return null;
        }
    }
}