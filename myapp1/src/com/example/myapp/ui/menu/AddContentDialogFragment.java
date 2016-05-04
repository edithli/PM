package com.example.myapp.ui.menu;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import com.example.myapp.R;
import com.example.myapp.ui.OnFragmentRefreshListener;
import com.example.myapp.util.Constants;

/**
 * Created by lss on 2016/3/6.
 */
public abstract class AddContentDialogFragment extends DialogFragment {

    protected OnFragmentRefreshListener mFragRefreshListener;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mFragRefreshListener = (OnFragmentRefreshListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentRefreshListener");
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final View view = getActivity().getLayoutInflater().inflate(getLayout(), null);
        Bundle args = getArguments();
        String defaultKey;
        if (args != null && (defaultKey = args.getString(Constants.DEFAULT_TEXT)) != null){
            EditText et = (EditText) view.findViewById(getKeyID());
            et.setText(defaultKey);
        }
        return (new AlertDialog.Builder(getActivity()))
                .setView(view)
                .setPositiveButton(R.string.finished, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        positiveClickListener(view);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddContentDialogFragment.this.getDialog().cancel();
                    }
                }).create();
    }

    protected abstract void positiveClickListener(View view);
    protected abstract @LayoutRes int getLayout();
    protected abstract @IdRes int getKeyID();
}
