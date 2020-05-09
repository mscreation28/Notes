package com.example.forthapplication.presistence;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.forthapplication.async.DeleteAsyncTask;
import com.example.forthapplication.async.InsertAsyncTask;
import com.example.forthapplication.async.UpdateAsyncTask;
import com.example.forthapplication.models.Note;

import java.util.List;

public class NoteRepository {

    private NoteDatabase mNoteDatabase;

    public NoteRepository(Context context) {
        mNoteDatabase = NoteDatabase.getInstance(context);
    }

    public void insertNote(Note note) {
        new InsertAsyncTask(mNoteDatabase.getNoteDao()).execute(note);
    }

    public void updateNote(Note note) {
        new UpdateAsyncTask(mNoteDatabase.getNoteDao()).execute(note);
    }

    public LiveData<List<Note>> retrieveNotes() {
        return mNoteDatabase.getNoteDao().getNotes();
    }

    public void deleteNode(Note note) {
        new DeleteAsyncTask(mNoteDatabase.getNoteDao()).execute(note);
    }
}
