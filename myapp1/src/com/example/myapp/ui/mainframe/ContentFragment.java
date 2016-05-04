package com.example.myapp.ui.mainframe;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.myapp.R;
import com.example.myapp.logic.User;
import com.example.myapp.ui.ContentDialogFragment;
import com.example.myapp.ui.OnFragmentRefreshListener;
import com.example.myapp.util.Constants;
import com.example.myapp.logic.Content;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by lss on 2016/3/6.
 */
public abstract class ContentFragment extends ListFragment{
    protected Map<String, Content> map;
    protected List<String> itemList;
    protected User user;
    protected OnFragmentRefreshListener mFragRefreshListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null)
            return;
        Bundle args = getArguments();
        user = User.getInstance(args.getString(Constants.USERNAME), null);
        map = getMap();
        itemList = new ArrayList<>(map.keySet());
        int layout = Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB ?
                android.R.layout.simple_list_item_activated_1:
                android.R.layout.simple_list_item_1;
        setListAdapter(new ArrayAdapter<>(getContext(), layout, itemList));
    }

    public void onAttach(Context context) {
        super.onAttach(context);
        Activity activity;
        if (context instanceof Activity) {
            activity = (Activity) context;
            try {
                mFragRefreshListener = (OnFragmentRefreshListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + " must implement OnFragmentRefreshListener");
            }
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForContextMenu(getListView());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getFragmentManager().findFragmentById(R.id.homepageFrame) != null)
            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        getListView().setItemChecked(position, false);
//        String content = map.get(itemList.get(position));
        Bundle args = displayItemArgs(map.get(itemList.get(position)));
        ContentDialogFragment pwdDialog = new ContentDialogFragment();
        pwdDialog.setArguments(args);
        pwdDialog.show(getFragmentManager(), Constants.PASSWORD_DIALOG_TAG);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.edit_content, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;
        Log.d("Content", "item info: " + position);
        switch (item.getItemId()) {
            case R.id.edit:
                editItem(position);
                return true;
            case R.id.remove:
                removeItem(position);
                return true;
        }
        return super.onContextItemSelected(item);
    }

    protected abstract Map<String, Content> getMap();
    protected abstract void editItem(int position);
    protected abstract void removeItem(int position);
    protected abstract Bundle displayItemArgs(Content content);
}
