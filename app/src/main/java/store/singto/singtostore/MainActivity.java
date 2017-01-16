package store.singto.singtostore;

import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

import store.singto.singtostore.CartTab.CartTabFragment;
import store.singto.singtostore.MeTab.MeTabFragment;
import store.singto.singtostore.ProductTab.ProductTabFragment;
import store.singto.singtostore.StoryTab.StoryTabFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment storyFragment, productFragment, cartFragment, meFragment;
    private AHBottomNavigation bottomBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);


        bottomBar = (AHBottomNavigation) findViewById(R.id.bottomBar);
        AHBottomNavigationItem story = new AHBottomNavigationItem(R.string.storyTab, R.drawable.ic_color_lens_white_24dp, R.color.colorPrimary);
        AHBottomNavigationItem prd = new AHBottomNavigationItem(R.string.prdTab, R.drawable.ic_local_offer_white_24dp, R.color.colorPrimary);
        AHBottomNavigationItem cart = new AHBottomNavigationItem(R.string.cartTab, R.drawable.ic_shopping_cart_white_24dp, R.color.colorPrimary);
        AHBottomNavigationItem me = new AHBottomNavigationItem(R.string.meTab, R.drawable.ic_account_box_white_24dp, R.color.colorPrimary);

        bottomBar.addItem(story);
        bottomBar.addItem(prd);
        bottomBar.addItem(cart);
        bottomBar.addItem(me);

        bottomBar.setAccentColor(Color.parseColor("#FF3845"));
        bottomBar.setInactiveColor(Color.parseColor("#747474"));

        bottomBar.setTitleState(AHBottomNavigation.TitleState.ALWAYS_SHOW);
        bottomBar.setCurrentItem(0);
        bottomBar.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                switch(position){
                    case 0:
                        setFragment(0);
                        break;
                    case 1:
                        setFragment(1);
                        break;
                    case 2:
                        setFragment(2);
                        break;
                    case 3:
                        setFragment(3);
                        break;
                }
                return true;
            }
        });
        setFragment(0);
    }

    private void setFragment(int index){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        hideAllFragment(transaction);
        switch (index) {
            case 0:
                if (storyFragment == null) {
                    storyFragment = new StoryTabFragment();
                    transaction.add(R.id.main_container, storyFragment);
                } else {
                    transaction.show(storyFragment);
                }
                break;
            case 1:
                if (productFragment == null) {
                    productFragment = new ProductTabFragment();
                    transaction.add(R.id.main_container, productFragment);
                } else {
                    transaction.show(productFragment);
                }
                break;
            case 2:
                if (cartFragment == null) {
                    cartFragment = new CartTabFragment();
                    transaction.add(R.id.main_container, cartFragment);
                } else {
                    transaction.show(cartFragment);
                }
                break;
            case 3:
                if (meFragment == null) {
                    meFragment = new MeTabFragment();
                    transaction.add(R.id.main_container, meFragment);
                } else {
                    transaction.show(meFragment);
                }
                break;
            default:
                break;
        }
        transaction.commit();
    }

    private void hideAllFragment(FragmentTransaction transaction){
        if (storyFragment != null) {
            transaction.hide(storyFragment);
        }
        if (productFragment != null) {
            transaction.hide(productFragment);
        }
        if (cartFragment != null) {
            transaction.hide(cartFragment);
        }
        if (meFragment != null) {
            transaction.hide(meFragment);
        }

    }
}
