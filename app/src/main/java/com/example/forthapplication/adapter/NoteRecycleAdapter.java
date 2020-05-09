package com.example.forthapplication.adapter;

import android.graphics.Color;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.forthapplication.R;
import com.example.forthapplication.models.Note;
import com.example.forthapplication.presistence.NoteRepository;
import com.example.forthapplication.util.ItemTouchHelperAdapter;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class NoteRecycleAdapter extends RecyclerView.Adapter<NoteRecycleAdapter.ViewHolder> implements ItemTouchHelperAdapter {
    private static final String TAG = "NoteRecycleAdapter";

    private ArrayList<Note> mNotes = new ArrayList<>();
    private OnNoteListner mOnNoteListner;

    private ItemTouchHelper mTouchHelper;

    private NoteRepository mNoteRepository;

    private CoordinatorLayout mCoordinatorLayout;

    public NoteRecycleAdapter(ArrayList<Note> mNotes, OnNoteListner mOnNoteListner, NoteRepository mNoteRepository,CoordinatorLayout mCoordinatorLayout) {
        this.mNotes = mNotes;
        this.mOnNoteListner = mOnNoteListner;
        this.mNoteRepository = mNoteRepository;
        this.mCoordinatorLayout = mCoordinatorLayout;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_view,parent,false);
        return new ViewHolder(view,mOnNoteListner);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.timestamp.setText(mNotes.get(position).getTimestamp());
        holder.title.setText(mNotes.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }


    //Swip and Delete data
    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        Note fromNote = mNotes.get(fromPosition);
//        Note toNote = mNotes.get(toPosition);
//        int temp = fromNote.getId();
//        fromNote.setId(toNote.getId());
//        toNote.setId(temp);
        mNotes.remove(fromNote);
        mNotes.add(toPosition,fromNote);
//        mNoteRepository.updateNote(fromNote);
//        mNoteRepository.updateNote(toNote);
        notifyItemMoved(fromPosition,toPosition);
    }

    @Override
    public void onItemSwiped(int position) {
        final Note deleteNote = mNotes.get(position);
        final String name = deleteNote.getTitle();
        final int deleteIndex = position;

        deleteNote(deleteNote);

        Snackbar snackbar = Snackbar
                .make(mCoordinatorLayout, name + " Deleted from Notes! ",Snackbar.LENGTH_LONG);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreItem(deleteNote,deleteIndex);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }

    public void setTouchHelper(ItemTouchHelper touchHelper) {
        this.mTouchHelper = touchHelper;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener,
            View.OnTouchListener,
            GestureDetector.OnGestureListener
    {
        private TextView title,timestamp;
        OnNoteListner onNoteListner;
        GestureDetector mGestureDetector;

        public ViewHolder(@NonNull View itemView, OnNoteListner onNoteListner) {
            super(itemView);
            title = itemView.findViewById(R.id.note_title);
            timestamp = itemView.findViewById(R.id.note_time);
            this.onNoteListner = onNoteListner;

            mGestureDetector = new GestureDetector(itemView.getContext(),this);

//            itemView.setOnClickListener(this);
            itemView.setOnTouchListener(this);
        }
        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onDown(MotionEvent e) {
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            onNoteListner.onNoteClick(getAdapterPosition());
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            mTouchHelper.startDrag(this);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return true;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mGestureDetector.onTouchEvent(event);
            return true;
        }
    }

    public interface OnNoteListner {
        void onNoteClick(int position);
    }

    private void deleteNote(Note note) {
        mNotes.remove(note);
        notifyDataSetChanged();
        mNoteRepository.deleteNode(note);
    }

    public void restoreItem(Note note, int position) {
        mNotes.add(position, note);
        // notify item added by position
        notifyItemInserted(position);
        mNoteRepository.insertNote(note);
    }
}
