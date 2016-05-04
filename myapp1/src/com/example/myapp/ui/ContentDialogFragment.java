package com.example.myapp.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.example.myapp.R;
import com.example.myapp.util.Constants;

/**
 * Created by lss on 2016/3/3.
 */
public class ContentDialogFragment extends DialogFragment {

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        Bundle args = getArguments();
        View view = getContentView(args);
//        final String content =  args.getString(Constants.CONTENT);
        final String content = args.getString(Constants.PASSWORD) == null ?
                args.getString(Constants.NOTE_CONTENT) :
                args.getString(Constants.PASSWORD);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.content_dialog_title)
                .setView(view)
//                .setMessage(content)
                .setNegativeButton(R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager cm = (ClipboardManager)getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        cm.setPrimaryClip(ClipData.newPlainText(Constants.CLIPBOARD_LABEL, content));
                        dismiss();
                    }
                })
                .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentDialogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }

    private View getContentView(Bundle args) {
        int type = args.getInt(Constants.TYPE);
        Activity activity = getActivity();
        View view = activity.getLayoutInflater().inflate(R.layout.content_dialog, null);
        TextView nicknameTV, nickname, pwdTV, pwd, cardnoTV, cardno, noteTV, note;
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.content_dialog);
        switch (type) {
            case Constants.WEBSITE_FRAGMENT:
                nicknameTV = new TextView(activity);
                nickname = new TextView(activity);
                pwdTV = new TextView(activity);
                pwd = new TextView(activity);
                nicknameTV.setText(R.string.nickname);
                nickname.setText(args.getString(Constants.NICKNAME));
                pwdTV.setText(R.string.password);
                pwd.setText(args.getString(Constants.PASSWORD));
                layout.addView(nicknameTV);
                layout.addView(nickname);
                layout.addView(pwdTV);
                layout.addView(pwd);
                break;
            case Constants.CARD_FRAGMENT:
                cardnoTV = new TextView(activity);
                cardno = new TextView(activity);
                pwdTV = new TextView(activity);
                pwd = new TextView(activity);
                cardnoTV.setText(R.string.card_number);
                cardno.setText(args.getString(Constants.CARD_NUMBER));
                pwdTV.setText(R.string.card_pwd);
                pwd.setText(args.getString(Constants.PASSWORD));
                layout.addView(cardnoTV);
                layout.addView(cardno);
                layout.addView(pwdTV);
                layout.addView(pwd);
                break;
            case Constants.NOTE_FRAGMENT:
                noteTV = new TextView(activity);
                note = new TextView(activity);
                noteTV.setText(R.string.note_content);
                note.setText(args.getString(Constants.NOTE_CONTENT));
                layout.addView(noteTV);
                layout.addView(note);
                break;
        }
        return view;
    }
}
