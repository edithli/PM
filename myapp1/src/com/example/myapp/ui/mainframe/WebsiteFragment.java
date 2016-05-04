package com.example.myapp.ui.mainframe;

import android.os.Bundle;
import com.example.myapp.ui.menu.AddWebsiteDialogFragment;
import com.example.myapp.util.Constants;
import com.example.myapp.logic.Content;

import java.util.Map;

/**
 * Created by lss on 2016/2/24.
 */
public class WebsiteFragment extends ContentFragment {
    @Override
    protected Map<String, Content> getMap() {
        return user.getWebsiteMap();
    }

    @Override
    protected void editItem(int position) {
        AddWebsiteDialogFragment webDialog = new AddWebsiteDialogFragment();
        Bundle args = new Bundle();
        args.putString(Constants.DEFAULT_TEXT, itemList.get(position));
        webDialog.setArguments(args);
        webDialog.show(getFragmentManager(), Constants.ADD_WEBSITE_FRAG_TAG);
    }

    @Override
    protected void removeItem(int position) {
        user.removeWebsite(itemList.get(position));
        // refresh fragment
        mFragRefreshListener.onRefreshFragment(Constants.WEBSITE_FRAGMENT);
    }

    @Override
    protected Bundle displayItemArgs(Content content) {
        Bundle args = new Bundle();
        args.putInt(Constants.TYPE, Constants.WEBSITE_FRAGMENT);
        args.putString(Constants.NICKNAME, content.getKey());
        args.putString(Constants.PASSWORD, content.getValue());
        return args;
    }
//    OnItemSelectedListener mCallBack;
//
//    private Map<String, String> websiteMap;
//    private List<String> websiteList;
//
//    public void onCreate(Bundle savedInstanceState){
//        super.onCreate(savedInstanceState);
//
//        if (savedInstanceState != null)
//            return;
//        Bundle args = getArguments();
//        User user = User.getInstance(args.getString(Constants.USERNAME), null);
//        websiteMap = user.getWebsiteMap();
//        websiteList = new ArrayList<>(websiteMap.keySet());
//
//        int layout = Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB ?
//                android.R.layout.simple_list_item_activated_1 :
//                android.R.layout.simple_list_item_1;
//        setListAdapter(new ArrayAdapter<>(getActivity(), layout, websiteList));
//    }
//
//    public void onStart(){
//        super.onStart();
//        if (getFragmentManager().findFragmentById(R.id.homepageFrame) != null)
//            getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//    }
//
//    public void onAttach(Context context){
//        super.onAttach(context);
//        Activity activity;
//        if (context instanceof Activity) {
//            activity = (Activity) context;
//            try {
//                mCallBack = (OnItemSelectedListener) activity;
//            }catch (ClassCastException e){
//                throw new ClassCastException(activity.toString() + " should implement OnWebsiteSelectedListener!");
//            }
//        }
//    }
//
//    public void onListItemClick(ListView l, View v, int position, long id) {
//        getListView().setItemChecked(position, false);
//        String pwd = websiteMap.get(websiteList.get(position));
//        mCallBack.onItemSelected(pwd);
//    }

}
