package com.example.myapp.ui.mainframe;

import android.os.Bundle;
import com.example.myapp.ui.menu.AddCardDialogFragment;
import com.example.myapp.util.Constants;
import com.example.myapp.logic.Content;

import java.util.Map;

/**
 * Created by lss on 2016/3/2.
 */
public class CardFragment extends ContentFragment {

    @Override
    protected Map<String, Content> getMap() {
        return user.getCardMap();
    }

    @Override
    protected void editItem(int position) {
        AddCardDialogFragment cardDialog = new AddCardDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.DEFAULT_TEXT, itemList.get(position));
        cardDialog.setArguments(args);
        cardDialog.show(getFragmentManager(), Constants.ADD_CARD_FRAG_TAG);
    }

    @Override
    protected void removeItem(int position) {
        user.removeCard(itemList.get(position));
        mFragRefreshListener.onRefreshFragment(Constants.CARD_FRAGMENT);
    }

    @Override
    protected Bundle displayItemArgs(Content content) {
        Bundle args = new Bundle();
        args.putInt(Constants.TYPE, Constants.CARD_FRAGMENT);
        args.putString(Constants.CARD_NUMBER, content.getKey());
        args.putString(Constants.PASSWORD, content.getValue());
        return args;
    }
//    private Map<String, String> cardMap;
//    private List<String> cardList;
//    private OnItemSelectedListener mCallBack;
//
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null)
//            return;
//        Bundle args = getArguments();
//        String username = args.getString(Constants.USERNAME);
//        User user = User.getInstance(username, null);
//        cardMap = user.getCardMap();
//        cardList = new ArrayList<>(cardMap.keySet());
//        int layout = Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB ?
//                android.R.layout.simple_list_item_activated_1 :
//                android.R.layout.simple_list_item_1;
//        setListAdapter(new ArrayAdapter<>(getActivity(), layout, cardList));
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        Activity activity;
//        if (context instanceof  Activity){
//            activity = (Activity) context;
//            try {
//                mCallBack = (OnItemSelectedListener) activity;
//            } catch (ClassCastException e) {
//                throw new ClassCastException(activity.toString() + " must implement OnItemSelectedListener");
//            }
//        }
//    }
//
//    public void onStart() {
//        super.onStart();
//        if (getFragmentManager().findFragmentById(R.id.homepageFrame) != null)
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//    }
//
//    public void onListItemClick(ListView l, View v, int position, long id){
////        getListView().setItemChecked(position, true);
//        String pwd = cardMap.get(cardList.get(position));
//        mCallBack.onItemSelected(pwd);
//    }
}
