package com.example.forthapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toolbar;

import com.example.forthapplication.adapter.NoteRecycleAdapter;
import com.example.forthapplication.models.Note;
import com.example.forthapplication.presistence.NoteRepository;
import com.example.forthapplication.util.MyItemTouchHelper;
import com.example.forthapplication.util.VerticalSpacingDecorator;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        NoteRecycleAdapter.OnNoteListner,
        View.OnClickListener
{
    private static final String TAG = "MainActivity";

    private RecyclerView mRecyclerView;

    private ArrayList<Note> mNotes = new ArrayList<>();
    private NoteRecycleAdapter mNoteRecycleAdapter;

    private NoteRepository mNoteRepository;
    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle_view);

        findViewById(R.id.fab).setOnClickListener(this);

        mNoteRepository = new NoteRepository(this);
        mCoordinatorLayout = findViewById(R.id.cordinator_layout);

        initRecyclerView();
        retrieveNotes();
//        insertFackNotes();

        Log.d(TAG, "onCreate: thread: "+Thread.currentThread().getName());

        setSupportActionBar((androidx.appcompat.widget.Toolbar)findViewById(R.id.note_toolbar));
        setTitle("Notes");

    }

    private void retrieveNotes() {
        mNoteRepository.retrieveNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                Log.d(TAG, "onChanged: change retrieved notes");
                if(mNotes.size()>0) {
                    mNotes.clear();
                }
                if(notes != null) {
                    mNotes.addAll(notes);
                }
                mNoteRecycleAdapter.notifyDataSetChanged();
            }
        });
    }
//    private void insertFackNotes() {
//        for (int i=1; i<=15 ;i++) {
//            Note note = new Note();
//            note.setTitle("Title # :" + i);
//            note.setContent("Content # :" + i);
//            note.setTimestamp("Jan 2020");
//            mNotes.add(note);
//        }
//        mNoteRecycleAdapter.notifyDataSetChanged();
//    }
    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        VerticalSpacingDecorator itemDecorator = new VerticalSpacingDecorator(10);
//        new ItemTouchHelper(itTouchHelperCallback).attachToRecyclerView(mRecyclerView);
        mRecyclerView.addItemDecoration(itemDecorator);
        mNoteRecycleAdapter = new NoteRecycleAdapter(mNotes,this,mNoteRepository,mCoordinatorLayout);

        ItemTouchHelper.Callback callback = new MyItemTouchHelper(mNoteRecycleAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        mNoteRecycleAdapter.setTouchHelper(itemTouchHelper);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.setAdapter(mNoteRecycleAdapter);
    }

    @Override
    public void onNoteClick(int position) {
//        mNotes.get(position);
        Log.d(TAG, "onNoteClick: clicked "+ position);
        Intent intent = new Intent(this,ContentActivity.class);
        intent.putExtra("object",mNotes.get(position));
        Log.d(TAG, "onNoteClick: "+mNotes.get(position).getTitle()+" "+mNotes.get(position).getContent());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this,ContentActivity.class);
        startActivity(intent);
    }

//    private void deleteNote(Note note) {
//        mNotes.remove(note);
//        mNoteRecycleAdapter.notifyDataSetChanged();
//
//        mNoteRepository.deleteNode(note);
//    }

//    private ItemTouchHelper.SimpleCallback itTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
//        @Override
//        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            return false;
//        }
//
//        @Override
//        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//            deleteNote(mNotes.get(viewHolder.getAdapterPosition()));
//        }
//    };
}
