package com.example.myapp.ui.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.example.myapp.R;
import com.example.myapp.logic.User;
import com.example.myapp.ui.OnFragmentRefreshListener;
import com.example.myapp.util.CommonFunction;
import com.example.myapp.util.Constants;

/**
 * Created by lss on 2016/3/4.
 */
public class AddNoteDialogFragment extends AddContentDialogFragment{
    @Override
    protected void positiveClickListener(View view) {
        EditText noteNameET = (EditText) view.findViewById(R.id.add_note_name_input);
        EditText noteContentET = (EditText)view.findViewById(R.id.add_note_content_input);
        if (CommonFunction.emptyEditText(noteNameET, R.string.hint_note_name_input)) return;
        if (CommonFunction.emptyEditText(noteContentET, R.string.hint_note_content_input)) return;
        String name = noteNameET.getText().toString();
        String content = noteContentET.getText().toString();
        Log.d("AddNote", "content: " + content);
//        if (content.contains("\n\r")){
//            Log.d("AddNote", "multiline content");
//            return;
//        }
        User user = User.getInstance(null, null);
        user.addNote(name, content);
//        dismiss();
        mFragRefreshListener.onRefreshFragment(Constants.NOTE_FRAGMENT);
    }

    @Override
    protected int getLayout() {
        return R.layout.add_notes;
    }

    @Override
    protected int getKeyID() {
        return R.id.add_note_name_input;
    }
//    private OnFragmentRefreshListener mFragRefreshListener;
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setCancelable(true);
//    }
//
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        try {
//            mFragRefreshListener = (OnFragmentRefreshListener) activity;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(activity.toString() + " must implement OnFragmentRefreshListener");
//        }
//    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        final View view = getActivity().getLayoutInflater().inflate(R.layout.add_notes, null);
//        return (new AlertDialog.Builder(getActivity()))
//                .setView(view)
//                .setPositiveButton(R.string.finished, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        EditText noteNameET = (EditText) view.findViewById(R.id.add_note_name_input);
//                        EditText noteContentET = (EditText)view.findViewById(R.id.add_note_content_input);
//                        if (CommonFunction.emptyEditText(noteNameET, R.string.hint_note_name_input)) return;
//                        if (CommonFunction.emptyEditText(noteContentET, R.string.hint_note_content_input)) return;
//                        String name = noteNameET.getText().toString();
//                        String content = noteContentET.getText().toString();
//                        Log.d("AddNote", "content: " + content);
//                        if (content.contains("\n\r") || content.contains("\n")){
//                            Log.d("AddNote", "multiline content");
//                            return;
//                        }
//                        User user = User.getInstance(null, null);
//                        user.addNote(name, content);
//                        dismiss();
//                        mFragRefreshListener.onRefreshFragment(Constants.NOTE_FRAGMENT);
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        AddNoteDialogFragment.this.getDialog().cancel();
//                    }
//                })
//                .create();
//    }
}
