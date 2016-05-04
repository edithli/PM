package com.example.myapp.ui.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import com.example.myapp.R;
import com.example.myapp.logic.User;
import com.example.myapp.ui.OnFragmentRefreshListener;
import com.example.myapp.util.CommonFunction;
import com.example.myapp.util.Constants;

/**
 * Created by lss on 2016/3/1.
 */
public class AddWebsiteDialogFragment extends AddContentDialogFragment {
    @Override
    protected void positiveClickListener(View view) {
        EditText domainET = (EditText) view.findViewById(R.id.add_domain_input);
        EditText pwdET = (EditText)view.findViewById(R.id.add_webpwd_input);
        EditText nickET = (EditText) view.findViewById(R.id.add_username_input);
        if (CommonFunction.emptyEditText(domainET, R.string.hint_domain_input)) return;
        if (CommonFunction.emptyEditText(nickET, R.string.input_username)) return;
        if (CommonFunction.emptyEditText(pwdET, R.string.hint_webpwd_input)) return;
        String domain = domainET.getText().toString();
        String nickname = nickET.getText().toString();
        String pwd = pwdET.getText().toString();
        Log.d("AddWebsite", "domain: " + domain);
        User user = User.getInstance(null, null);
        user.addWebsite(domain, nickname, pwd);
//        dismiss();
        mFragRefreshListener.onRefreshFragment(Constants.WEBSITE_FRAGMENT);
    }

    @Override
    protected int getLayout() {
        return R.layout.add_website;
    }

    @Override
    protected int getKeyID() {
        return R.id.add_domain_input;
    }
//    OnFragmentRefreshListener mFragRefreshListener;
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setCancelable(false);
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
////        Dialog dialog = super.onCreateDialog(savedInstanceState);
////        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
////        return dialog;
//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = getActivity().getLayoutInflater();
//        final View view = inflater.inflate(R.layout.add_website, null);
//        builder
//                .setView(view)
//                .setPositiveButton(R.string.finished, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        EditText domainET = (EditText) view.findViewById(R.id.add_domain_input);
//                        EditText pwdET = (EditText)view.findViewById(R.id.add_webpwd_input);
//                        if (CommonFunction.emptyEditText(domainET, R.string.hint_domain_input)) return;
//                        if (CommonFunction.emptyEditText(pwdET, R.string.hint_webpwd_input)) return;
//                        String domain = domainET.getText().toString();
//                        String pwd = pwdET.getText().toString();
//                        Log.d("AddWebsite", "domain: " + domain);
//                        User user = User.getInstance(null, null);
//                        user.addWebsite(domain, pwd);
////                        user.storeWebsites();
//                        dismiss();
//                        mFragRefreshListener.onRefreshFragment(Constants.WEBSITE_FRAGMENT);
//                        Log.d("test", "refresh done");
//                    }
//                })
//                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        AddWebsiteDialogFragment.this.getDialog().cancel();
//                    }
//                });
//        AlertDialog dialog = builder.create();
//        dialog.setCanceledOnTouchOutside(false);
//        return dialog;
//    }
//
////    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
////        return inflater.inflate(R.layout.add_website, container, false);
////    }
}
