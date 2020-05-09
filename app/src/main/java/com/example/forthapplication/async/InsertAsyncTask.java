package com.example.forthapplication.async;

import android.os.AsyncTask;
import android.util.Log;

import com.example.forthapplication.models.Note;
import com.example.forthapplication.presistence.NoteDao;

public class InsertAsyncTask extends AsyncTask<Note,Void,Void> {

    private static final String TAG = "InsertAsyncTask";

    private NoteDao mNoteDao;
    public InsertAsyncTask(NoteDao noteDao) {
        mNoteDao = noteDao;
    }

    @Override
    protected Void doInBackground(Note... notes) {
        Log.d(TAG, "doInBackground: thread" + Thread.currentThread().getName());
        mNoteDao.insertNotes(notes);
        return null;
    }
}
