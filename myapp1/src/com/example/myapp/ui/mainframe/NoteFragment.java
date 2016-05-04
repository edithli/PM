package com.example.myapp.ui.mainframe;

import android.os.Bundle;
import com.example.myapp.ui.menu.AddNoteDialogFragment;
import com.example.myapp.util.Constants;
import com.example.myapp.logic.Content;

import java.util.Map;

/**
 * Created by lss on 2016/3/2.
 */
public class NoteFragment extends ContentFragment{
    @Override
    protected Map<String, Content> getMap() {
        return user.getNoteMap();
    }

    @Override
    protected void editItem(int position) {
        AddNoteDialogFragment noteDF = new AddNoteDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.DEFAULT_TEXT, itemList.get(position));
        noteDF.setArguments(args);
        noteDF.show(getFragmentManager(), Constants.ADD_NOTE_FRAG_TAG);
    }

    @Override
    protected void removeItem(int position) {
        user.removeNote(itemList.get(position));
        mFragRefreshListener.onRefreshFragment(Constants.NOTE_FRAGMENT);
    }

    @Override
    protected Bundle displayItemArgs(Content content) {
        Bundle args = new Bundle();
        args.putInt(Constants.TYPE, Constants.NOTE_FRAGMENT);
        args.putString(Constants.NOTE_CONTENT, content.getKey());
        return args;
    }

//    private Map<String, String> noteMap;
//    private List<String> noteList;
//    private OnItemSelectedListener mCallBack;
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null)
//            return;
//        Bundle args = getArguments();
//        String username = args.getString(Constants.USERNAME);
//        User user = User.getInstance(username, null);
//        noteMap = user.getNoteMap();
//        noteList = new ArrayList<>(noteMap.keySet());
//        int layout = Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB ?
//                android.R.layout.simple_list_item_activated_1:
//                android.R.layout.simple_list_item_1;
//        setListAdapter(new ArrayAdapter<>(getContext(), layout, noteList));
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof Activity) {
//            Activity activity = (Activity)context;
//            try{
//                mCallBack = (OnItemSelectedListener)activity;
//            }catch (ClassCastException e){
//                throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
//            }
//        }
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if (getFragmentManager().findFragmentById(R.id.homepageFrame) != null)
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//    }
//
//    @Override
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        getListView().setItemChecked(position, true);
//        String content = noteMap.get(noteList.get(position));
//        mCallBack.onItemSelected(content);
//    }
}
