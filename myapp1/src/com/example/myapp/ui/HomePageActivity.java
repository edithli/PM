package com.example.myapp.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.*;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.example.myapp.R;
import com.example.myapp.database.UserInfo;
import com.example.myapp.logic.Crypto;
import com.example.myapp.logic.User;
import com.example.myapp.ui.mainframe.CardFragment;
import com.example.myapp.ui.mainframe.NoteFragment;
import com.example.myapp.ui.mainframe.WebsiteFragment;
import com.example.myapp.ui.menu.AddCardDialogFragment;
import com.example.myapp.ui.menu.AddNoteDialogFragment;
import com.example.myapp.ui.menu.AddWebsiteDialogFragment;
import com.example.myapp.util.Constants;

/**
 * Created by lss on 2016/2/22.
 */
public class HomePageActivity extends FragmentActivity implements
        RadioGroup.OnCheckedChangeListener,
        OnFragmentRefreshListener,
        PopupMenu.OnMenuItemClickListener {

    private User user;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private RadioGroup rg;
    private Bundle args = new Bundle();
    private WebsiteFragment websiteFragment;
    private CardFragment cardFragment;
    private NoteFragment noteFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.homepage);

        if (findViewById(R.id.homepageFrame) != null) {
            if (savedInstanceState != null)
                return;
            Intent intent = getIntent();
            String username = intent.getStringExtra(Constants.USERNAME);
//            String password = intent.getStringExtra(Constants.PASSWORD);
//            intent.removeExtra(Constants.PASSWORD);
            args.putString(Constants.USERNAME, username);
            // @TODO: handle PASSWORD

            rg = (RadioGroup) findViewById(R.id.rg_navigator);
            rg.setOnCheckedChangeListener(this);
            RadioButton rbDefault = (RadioButton) findViewById(R.id.rb_website);
            rbDefault.setChecked(true);

            user = User.getInstance(username, getApplicationContext());
            TextView usernameTV = (TextView) findViewById(R.id.show_username);
            TextView checksumTV = (TextView) findViewById(R.id.show_checksum);
            usernameTV.setText("用户名：" + username);
            checksumTV.setText("校验词：" + user.getChecksum());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        user.store();
        user.release();
        Crypto.getInstance().release();
    }

    // for the add menu ImageButton
    public void showAddMenu(View view){
        PopupMenu addMenu = new PopupMenu(this, view);
        addMenu.setOnMenuItemClickListener(this);
        addMenu.inflate(R.menu.add_content);
        addMenu.show();
    }

    @Override
    // for add content finished - OnRefreshFragmentListener
    public void onRefreshFragment(int frag) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (frag) {
            case Constants.WEBSITE_FRAGMENT:
                if (websiteFragment != null)
                    ft.remove(websiteFragment);
                websiteFragment = new WebsiteFragment();
                websiteFragment.setArguments(args);
                ft.add(R.id.homepageFrame, websiteFragment);
                ft.commit();
                ((RadioButton)findViewById(R.id.rb_website)).setChecked(true);
                break;
            case Constants.CARD_FRAGMENT:
                if (cardFragment != null)
                    ft.remove(cardFragment);
                cardFragment = new CardFragment();
                cardFragment.setArguments(args);
                ft.add(R.id.homepageFrame, cardFragment);
                ft.commit();
                ((RadioButton)findViewById(R.id.rb_card)).setChecked(true);
                break;
            case Constants.NOTE_FRAGMENT:
                if (noteFragment != null)
                    ft.remove(noteFragment);
                noteFragment = new NoteFragment();
                noteFragment.setArguments(args);
                ft.add(R.id.homepageFrame, noteFragment);
                ft.commit();
                ((RadioButton)findViewById(R.id.rb_notes)).setChecked(true);
                break;
        }
    }

    @Override
    // for radio buttons - changing content fragments
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        hideAllFragment(ft);
        switch (checkedId) {
            case R.id.rb_website:
                if (websiteFragment == null){
                    websiteFragment = new WebsiteFragment();
                    websiteFragment.setArguments(args);
                    ft.add(R.id.homepageFrame, websiteFragment);
                }else ft.show(websiteFragment);
                break;
            case R.id.rb_card:
                if (cardFragment == null) {
                    cardFragment = new CardFragment();
                    cardFragment.setArguments(args);
                    ft.add(R.id.homepageFrame, cardFragment);
                }else ft.show(cardFragment);
                break;
            case R.id.rb_notes:
                if (noteFragment == null) {
                    noteFragment = new NoteFragment();
                    noteFragment.setArguments(args);
                    ft.add(R.id.homepageFrame, noteFragment);
                }else ft.show(noteFragment);
                break;
        }
        ft.commit();
    }

    private void hideAllFragment(FragmentTransaction ft) {
        if (websiteFragment != null) ft.hide(websiteFragment);
        if (cardFragment != null) ft.hide(cardFragment);
        if (noteFragment != null) ft.hide(noteFragment);
    }

    @Override
    // for add menu item clicked
    public boolean onMenuItemClick(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (id) {
            case R.id.add_website:
                AddWebsiteDialogFragment websiteDialogFragment = new AddWebsiteDialogFragment();
                Fragment prev = fragmentManager.findFragmentByTag(Constants.ADD_WEBSITE_FRAG_TAG);
                if (prev != null) {
                    transaction.remove(prev);
                    transaction.commit();
                }
                websiteDialogFragment.show(transaction, Constants.ADD_WEBSITE_FRAG_TAG);
                return true;
            case R.id.add_card:
                AddCardDialogFragment cardDialogFragment = new AddCardDialogFragment();
                Fragment cardPrev = fragmentManager.findFragmentByTag(Constants.ADD_CARD_FRAG_TAG);
                if (cardPrev != null) {
                    transaction.remove(cardPrev);
                    transaction.commit();
                }
                cardDialogFragment.show(transaction, Constants.ADD_CARD_FRAG_TAG);
                return true;
            case R.id.add_note:
                AddNoteDialogFragment noteDialogFragment = new AddNoteDialogFragment();
                Fragment notePrev = fragmentManager.findFragmentByTag(Constants.ADD_NOTE_FRAG_TAG);
                if (notePrev != null) {
                    transaction.remove(notePrev);
                    transaction.commit();
                }
                noteDialogFragment.show(transaction, Constants.ADD_NOTE_FRAG_TAG);
                return true;
            case R.id.remove_user:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(R.string.ensure_remove);
                builder.setTitle(R.string.remove);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ensure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserInfo userInfo = new UserInfo(getApplicationContext());
                        userInfo.deleteUserInfo(user.getUsername());
                        user.removeUser();
                        HomePageActivity.this.finish();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                return true;
        }
        return false;
    }
}
