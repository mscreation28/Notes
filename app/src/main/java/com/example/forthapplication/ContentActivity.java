package com.example.forthapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forthapplication.models.Note;
import com.example.forthapplication.presistence.NoteRepository;
import com.example.forthapplication.util.LinedEditText;
import com.example.forthapplication.util.Utility;

public class ContentActivity extends AppCompatActivity implements View.OnTouchListener,
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener,
        View.OnClickListener,
        TextWatcher
{
    private static final String TAG = "ContentActivity";
    private static final int EDIT_MODE_ENABLED = 1;
    private static final int EDIT_MODE_DISABLED = 0;

    private LinedEditText mLinedEditText;
    private EditText mEditTitle;
    private TextView mViewTitle;

    private RelativeLayout mCheckcontainer,mBackArrowContainer;
    private ImageButton mCheckButton,mBackArrowButton;

    private boolean mIsNewNote;
    private Note mInitialNote;

    private GestureDetector mGestureDetector;
    private int mMode;

    private NoteRepository mNoteRepository;
    private Note mFinalNote;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_view);

        mLinedEditText = findViewById(R.id.notes_content);
        mEditTitle = findViewById(R.id.note_edit_title);
        mViewTitle = findViewById(R.id.note_text_title);

        mCheckcontainer = findViewById(R.id.check_container);
        mBackArrowContainer = findViewById(R.id.back_arrow);
        mCheckButton = findViewById(R.id.toolbar_check);
        mBackArrowButton = findViewById(R.id.toolbar_back);

        mNoteRepository = new NoteRepository(this);

        if (getIncomingIntent()) {
            //this is new Edit Mode
            setNewNoteProperties();
            enableEditMode();
        }
        else {
            //This is not new View Mode
            setNoteProperties();
            disableContentInteraction();
        }
        setListner();
    }

    private boolean getIncomingIntent() {
        if(getIntent().hasExtra("object")) {
            mInitialNote = getIntent().getParcelableExtra("object");
            mFinalNote = new Note();
            mFinalNote.setTitle(mInitialNote.getTitle());
            mFinalNote.setContent(mInitialNote.getContent());
            mFinalNote.setTimestamp(mInitialNote.getTimestamp());
            mFinalNote.setId(mInitialNote.getId());

            mMode = EDIT_MODE_DISABLED;
            mIsNewNote = false;
            return false;
        }
        mMode = EDIT_MODE_ENABLED;
        mIsNewNote = true;
        return true;
    }

    private void saveChanges() {
        if(mIsNewNote) {
            saveNewNote();
        }
        else {
            updateNote();
        }
    }

    private void saveNewNote() {
        mNoteRepository.insertNote(mFinalNote);
    }

    private void updateNote() {
        mNoteRepository.updateNote(mFinalNote);
    }

    private void setListner() {
        mLinedEditText.setOnTouchListener(this);
        mGestureDetector = new GestureDetector(this,this);
        mViewTitle.setOnClickListener(this);
        mCheckButton.setOnClickListener(this);
        mBackArrowButton.setOnClickListener(this);
        mEditTitle.addTextChangedListener(this);
    }

    private void setNoteProperties() {
        mInitialNote = getIntent().getParcelableExtra("object");
        Log.d(TAG, "setNoteProperties: "+mInitialNote.getTitle()+ " "+mInitialNote.getContent());
        mViewTitle.setText(mInitialNote.getTitle());
        mEditTitle.setText(mInitialNote.getTitle());
        mLinedEditText.setText(mInitialNote.getContent());
    }

    private void setNewNoteProperties() {
        mViewTitle.setText("Note Title");
        mEditTitle.setText("Note Title");

        mInitialNote = new Note();
        mFinalNote = new Note();
        mInitialNote.setTitle("Note Title");
        mFinalNote.setTitle("Note Title");
    }

    private void enableEditMode() {
        mBackArrowContainer.setVisibility(View.GONE);
        mCheckcontainer.setVisibility(View.VISIBLE);

        mViewTitle.setVisibility(View.GONE);
        mEditTitle.setVisibility(View.VISIBLE);

        mMode = EDIT_MODE_ENABLED;

        enableContentInteraction();
    }
    private void disableEditMode() {
        mBackArrowContainer.setVisibility(View.VISIBLE);
        mCheckcontainer.setVisibility(View.GONE);

        mViewTitle.setVisibility(View.VISIBLE);
        mEditTitle.setVisibility(View.GONE);

        mMode = EDIT_MODE_DISABLED;

        disableContentInteraction();

        String temp = mLinedEditText.getText().toString();
        temp = temp.replace("\n","");
        temp = temp.replace(" ","");
        if(temp.length()>0) {
            mFinalNote.setTitle(mEditTitle.getText().toString());
            mFinalNote.setContent(mLinedEditText.getText().toString());
            String timestamp = Utility.getCurrentTimeStamp();
            mFinalNote.setTimestamp(timestamp);

            if(!mFinalNote.getContent().equals(mInitialNote.getContent()) || !mFinalNote.getTitle().equals(mInitialNote.getTitle())) {
                saveChanges();
            }
        }

    }

    private void disableContentInteraction() {
        mLinedEditText.setKeyListener(null);
        mLinedEditText.setFocusable(false);
        mLinedEditText.setFocusableInTouchMode(false);
        mLinedEditText.setCursorVisible(false);
        mLinedEditText.clearFocus();
    }
    private void enableContentInteraction() {
        mLinedEditText.setKeyListener(new EditText(this).getKeyListener());
        mLinedEditText.setFocusable(true);
        mLinedEditText.setFocusableInTouchMode(true);
        mLinedEditText.setCursorVisible(true);
        mLinedEditText.requestFocus();
    }
    private void hideSoftKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if(view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(),0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
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
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.d(TAG, "onDoubleTap: Double tap");
        enableEditMode();
        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.toolbar_check:{
                hideSoftKeyboard();
                disableEditMode();
                break;
            }
            case R.id.note_text_title:{
                enableEditMode();
                mEditTitle.requestFocus();
                mEditTitle.setSelection(mEditTitle.length());
                break;
            }
            case R.id.toolbar_back:{
                finish();
                break;
            }
        }
    }

    @Override
    public void onBackPressed() {
        if(mMode == EDIT_MODE_ENABLED) {
            onClick(mCheckButton);
        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mode",mMode);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mMode = savedInstanceState.getInt("mode");
        if(mMode == EDIT_MODE_ENABLED) {
            enableEditMode();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mViewTitle.setText(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
