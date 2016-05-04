package com.example.myapp.ui.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import com.example.myapp.R;
import com.example.myapp.logic.User;
import com.example.myapp.util.CommonFunction;
import com.example.myapp.util.Constants;

/**
 * Created by lss on 2016/3/3.
 */
public class AddCardDialogFragment extends AddContentDialogFragment{
    @Override
    protected void positiveClickListener(View view) {
        EditText nickET = (EditText) view.findViewById(R.id.add_card_name_input);
        EditText idET = (EditText)view.findViewById(R.id.add_card_number_input);
        EditText pwdET = (EditText)view.findViewById(R.id.add_card_pwd_input);
        if (CommonFunction.emptyEditText(nickET, R.string.hint_card_name_input)) return;
        if (CommonFunction.emptyEditText(idET, R.string.hint_card_number_input)) return;
        if (CommonFunction.emptyEditText(pwdET, R.string.hint_card_pwd_input)) return;
        String cardID = idET.getText().toString();
        String pwd = pwdET.getText().toString();
        String nickname = nickET.getText().toString();
        User user = User.getInstance(null, null);
        user.addCard(nickname, cardID, pwd);
//        dismiss();
        mFragRefreshListener.onRefreshFragment(Constants.CARD_FRAGMENT);
    }

    @Override
    protected int getLayout() {
        return R.layout.add_card;
    }

    @Override
    protected int getKeyID() {
        return R.id.add_card_name_input;
    }
//    private OnFragmentRefreshListener mFragRefreshListener;
//
//    public void onCreate(Bundle savedInstanceState) {
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
//    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        final View view = getActivity().getLayoutInflater().inflate(R.layout.add_card, null);
//        return (new AlertDialog.Builder(getActivity()))
//                .setView(view)
//                .setPositiveButton(R.string.finished, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        EditText idET = (EditText)view.findViewById(R.id.add_card_number_input);
//                        EditText pwdET = (EditText)view.findViewById(R.id.add_card_pwd_input);
//                        if (CommonFunction.emptyEditText(idET, R.string.hint_card_number_input)) return;
//                        if (CommonFunction.emptyEditText(pwdET, R.string.hint_card_pwd_input)) return;
//                        String cardID = idET.getText().toString();
//                        String pwd = pwdET.getText().toString();
//                        User user = User.getInstance(null, null);
//                        user.addCard(cardID, pwd);
////                        user.storeCard();
//                        dismiss();
//                        mFragRefreshListener.onRefreshFragment(Constants.CARD_FRAGMENT);
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        AddCardDialogFragment.this.getDialog().cancel();
//                    }
//                }).create();
//    }



}
