package store.singto.singtostore;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import store.singto.singtostore.CartTab.CartTabFragment;
import store.singto.singtostore.MeTab.MeTabFragment;
import store.singto.singtostore.ProductTab.ProductTabFragment;
import store.singto.singtostore.StoryTab.StoryTabFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment storyFragment, productFragment, cartFragment, meFragment;

    private BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setElevation(0);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_bar);
        bottomNavigationView.inflateMenu(R.menu.menu);
        setFragment(0);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {


                switch (item.getItemId()) {
                    case R.id.story_Tab:
                        setFragment(0);
                        break;
                    case R.id.product_Tab:
                        setFragment(1);
                        break;
                    case R.id.cart_Tab:
                        setFragment(2);
                        break;
                    case R.id.me_Tab:
                        setFragment(3);
                        break;
                }

                return true;
            }
        });
    }

    private void setFragment(int index){
        FragmentManager fragmentManager = getFragmentManager();
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
