package com.erdi.SecureBox.repos;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.erdi.SecureBox.models.ViyCred;
import com.erdi.SecureBox.room.CredDB;
import com.erdi.SecureBox.room.CredDao;

import java.util.List;

public class CredsRepository {
    private CredDao credDao;
    private LiveData<List<ViyCred>> allCreds, mailCreds, socialCreds, wifiCreds;

    public CredsRepository(Application application) {
        CredDB database = CredDB.getInstance(application);
        credDao = database.viyCredDao();
        allCreds = credDao.getAllCreds();
        mailCreds = credDao.getAllMails();
        socialCreds = credDao.getAllSocial();
        wifiCreds = credDao.getAllWifi();
    }

    public void insert(ViyCred viyCred) {
        new InsertNoteAsyncTask(credDao).execute(viyCred);
    }

    public void update(ViyCred viyCred) {
        new UpdateNoteAsyncTask(credDao).execute(viyCred);
    }

    public void delete(ViyCred viyCred) {
        new DeleteNoteAsyncTask(credDao).execute(viyCred);
    }

    public void deleteAllNotes() {
        new DeleteAllNotesAsyncTask(credDao).execute();
    }

    public LiveData<List<ViyCred>> getAllNotes() {
        return allCreds;
    }

    public LiveData<List<ViyCred>> getAllMails() {
        return mailCreds;
    }

    public LiveData<List<ViyCred>> getAllWifi() {
        return wifiCreds;
    }

    public LiveData<List<ViyCred>> getAllSocial() {
        return socialCreds;
    }

    private static class InsertNoteAsyncTask extends AsyncTask<ViyCred, Void, Void> {
        private CredDao credDao;

        private InsertNoteAsyncTask(CredDao credDao) {
            this.credDao = credDao;
        }

        @Override
        protected Void doInBackground(ViyCred... viyCreds) {
            credDao.insert(viyCreds[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<ViyCred, Void, Void> {
        private CredDao credDao;

        private UpdateNoteAsyncTask(CredDao credDao) {
            this.credDao = credDao;
        }

        @Override
        protected Void doInBackground(ViyCred... viyCreds) {
            credDao.update(viyCreds[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<ViyCred, Void, Void> {
        private CredDao credDao;

        private DeleteNoteAsyncTask(CredDao credDao) {
            this.credDao = credDao;
        }

        @Override
        protected Void doInBackground(ViyCred... viyCreds) {
            credDao.delete(viyCreds[0]);
            return null;
        }
    }

     private static class DeleteAllNotesAsyncTask extends AsyncTask<Void, Void, Void> {
         private CredDao credDao;

         private DeleteAllNotesAsyncTask(CredDao credDao) {
             this.credDao = credDao;
         }

         @Override
         protected Void doInBackground(Void... voids) {
             credDao.deleteAllNotes();
             return null;
        }
    }
}
