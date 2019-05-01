package com.hakagamesstudio.begreen.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.databases.User_Cart_DB;
import com.hakagamesstudio.begreen.databases.User_Info_DB;
import com.hakagamesstudio.begreen.pojos.cart_model.CartProduct;
import com.hakagamesstudio.begreen.pojos.pages_model.PagesData;
import com.hakagamesstudio.begreen.pojos.pages_model.PagesDetails;
import com.hakagamesstudio.begreen.pojos.user_model.UserDetails;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.ConstantValues;
import com.hakagamesstudio.begreen.support.MyAppPrefsManager;
import com.hakagamesstudio.begreen.views.fragments.AboutFragment;
import com.hakagamesstudio.begreen.views.fragments.CatalogueFragment;
import com.hakagamesstudio.begreen.views.fragments.CreditCardsFragment;
import com.hakagamesstudio.begreen.views.fragments.HistoryShopFragment;
import com.hakagamesstudio.begreen.views.fragments.MyCartFragment;
import com.hakagamesstudio.begreen.views.fragments.OffersFragment;
import com.hakagamesstudio.begreen.views.fragments.OrchardsFragment;
import com.hakagamesstudio.begreen.views.fragments.PerfilUserFragment;
import com.hakagamesstudio.begreen.views.fragments.SingleProductFragment;
import com.hakagamesstudio.begreen.views.fragments.WishListFragment;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Menu_NavDrawer_Acititty extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, MyCartFragment.OnFragmentInteractionListener,
        SingleProductFragment.OnSingleProductListener, PerfilUserFragment.ProfileCallback, WishListFragment.OnWhishListListener {

    public static BottomNavigationView mBottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.contraintContentMain)
    ConstraintLayout mContentOrchardsOptions;

    public static Button imgProducts;
    public static Button imgMap;
    public static ImageView imgHome;

    private SharedPreferences sharedPreferences;
    private MyAppPrefsManager myAppPrefsManager;
    private Fragment fragment = null;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    public static ActionBarDrawerToggle toggle;
    private User_Cart_DB user_cart_db = new User_Cart_DB();
    public static RecyclerView mRecyclerCatalogueProduct;
    public static LinearLayout mFrameCatalogueProduct;
    private CallBackRetrofit mCall = ApiUtils.getAPIService();

    public LinearLayout llTerminosyCondiciones;
    private DrawerLayout drawer;

    private ImageView photoUser;
    private TextView txtSubscribe_Nav;
    private TextView txtWelcome_Nav;
    private TextView txtNameUser_Nav;
    private TextView text;
    private View badge;
    private BottomNavigationItemView itemView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_nav_drawer);
        ButterKnife.bind(this);

        imgMap = findViewById(R.id.imgMap);
        imgProducts = findViewById(R.id.imgProducts);
        imgHome = findViewById(R.id.imgHome);

        mBottomNavigationView = findViewById(R.id.navigationView);
        mRecyclerCatalogueProduct = findViewById(R.id.recyclerProductsCatalogue);
        mFrameCatalogueProduct = findViewById(R.id.frameContentToolbar);
        llTerminosyCondiciones = findViewById(R.id.llTermsAndConditions);
        mFrameCatalogueProduct.bringToFront();
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        mBottomNavigationView.bringToFront();

        myAppPrefsManager = new MyAppPrefsManager(this);
        myAppPrefsManager.setFirstTimeLaunch(false);

        drawer = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        NavigationMenuView navMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        navMenuView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        initBottomView();

        toggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.btn_perfil_3, getTheme());
        toggle.setHomeAsUpIndicator(drawable);
        toggle.setToolbarNavigationClickListener(v -> {
            if (!drawer.isDrawerOpen(Gravity.RIGHT) && !drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.openDrawer(Gravity.LEFT);
            } else if (drawer.isDrawerOpen(Gravity.LEFT)) {
                drawer.closeDrawer(Gravity.LEFT);
            } else if (drawer.isDrawerOpen(Gravity.RIGHT)) {
                drawer.closeDrawer(Gravity.RIGHT);
            }
        });

        Fragment fragment = new CatalogueFragment().newInstance();
        fragmentManager.beginTransaction().replace(R.id.contentLayout, fragment).commit();

        myAppPrefsManager = new MyAppPrefsManager(Menu_NavDrawer_Acititty.this);
        sharedPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        setupExpandableDrawerHeader(headerView);

        llTerminosyCondiciones.setOnClickListener(v -> {
            mContentOrchardsOptions.setVisibility(View.GONE);
            drawer.closeDrawer(GravityCompat.START);
            MaterialDialog progressDialog = BeGreenSupport.getInstance().createProgressDialog( "Estamos obteniendo las políticas de privacidad", this);
            progressDialog.show();
            Call<PagesData> call = mCall.getStaticPages(1);
            call.enqueue(new Callback<PagesData>() {
                @Override
                public void onResponse(Call<PagesData> call, Response<PagesData> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){
                            if (response.body().getSuccess().equalsIgnoreCase("1")) {
                                for (int i = 0; i < response.body().getPagesData().size(); i++) {
                                    PagesDetails page = response.body().getPagesData().get(i);
                                    if (page.getSlug().equalsIgnoreCase("privacy-policy")) {
                                        ConstantValues.PRIVACY_POLICY = page.getDescription();
                                        progressDialog.dismiss();
                                        fragmentManager.beginTransaction().replace(R.id.contentLayout, AboutFragment.newInstance("openPolicy")).commit();
                                    }
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<PagesData> call, Throwable t) {

                }
            });


        });

        if (myAppPrefsManager.isUserLoggedIn()) {
            photoUser.setOnClickListener(v -> {
                mContentOrchardsOptions.setVisibility(View.GONE);
                drawer.closeDrawer(GravityCompat.START);
                fragmentManager.beginTransaction().replace(R.id.contentLayout, PerfilUserFragment.newInstance()).commit();
            });
        }

        itemView = mBottomNavigationView.findViewById(R.id.action_compras);
        badge = LayoutInflater.from(this).inflate(R.layout.menu_counter, mBottomNavigationView, false);
        text = badge.findViewById(R.id.badge_text_view);

        if (myAppPrefsManager.isUserLoggedIn()) {
            List<CartProduct> cartItemsList = user_cart_db.getCartItems();
            if (0 < cartItemsList.size())
                onFragmentInteraction(String.valueOf(cartItemsList.size()));
        } else onFragmentInteraction("0");

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_history:
                fragment = HistoryShopFragment.newInstance("", "");
                //fragment = WithOutProductsFragment.newInstance(R.mipmap.agregar_pedido, "Aún no tienes pedidos ve al catálogo para hacer una compra");
                mContentOrchardsOptions.setVisibility(View.GONE);
                break;

            case R.id.nav_favorite:
                fragment = WishListFragment.newInstance();
                BeGreenSupport.getInstance().setmContentrButtonsOrchards(mContentOrchardsOptions);
                break;

            case R.id.nav_payment:
                fragment = CreditCardsFragment.newInstance();
                mContentOrchardsOptions.setVisibility(View.GONE);
                break;

            case R.id.nav_tutorial:
                startActivity(new Intent(Menu_NavDrawer_Acititty.this, IntroActivity.class));
                break;

            case R.id.nav_about:
                fragment = AboutFragment.newInstance("");
                mContentOrchardsOptions.setVisibility(View.GONE);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        if (fragment != null)
            fragmentManager.beginTransaction().replace(R.id.contentLayout, fragment).commit();
        return false;
    }

    public void initBottomView() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.menu_catalogo:
                    fragment = new CatalogueFragment().newInstance();
                    mContentOrchardsOptions.setVisibility(View.GONE);
                    break;

                case R.id.action_huertas:
                    fragment = new OrchardsFragment().newInstance();
                    BeGreenSupport.getInstance().setmContentrButtonsOrchards(mContentOrchardsOptions);
                    mContentOrchardsOptions.setVisibility(View.GONE);
                    break;

                case R.id.action_compras:
                    mContentOrchardsOptions.setVisibility(View.GONE);
                    fragment = new MyCartFragment().newInstance();
                    break;

                case R.id.action_ofertas:
                    mContentOrchardsOptions.setVisibility(View.GONE);
                    fragment = new OffersFragment().newInstance();
                    break;
            }
            if (fragment != null)
                fragmentManager.beginTransaction().replace(R.id.contentLayout, fragment).commit();
            return true;
        });
    }

    public void setupExpandableDrawerHeader(View headerView) {
        photoUser = headerView.findViewById(R.id.imgPhotoUser_Nav);
        txtSubscribe_Nav = headerView.findViewById(R.id.txtSubscribe_Nav);
        txtWelcome_Nav = headerView.findViewById(R.id.txtWelcomeUser);
        txtNameUser_Nav = headerView.findViewById(R.id.txtNameUser_Nav);
        navHeaderInit();
    }

    public void navHeaderInit() {
// Check if the User is Authenticated
        if (myAppPrefsManager.isUserLoggedIn()) {
            // Check User's Info from SharedPreferences
            txtSubscribe_Nav.setVisibility(View.GONE);
            if (sharedPreferences.getString("userEmail", null) != null) {

                // Get User's Info from Local Database User_Info_DB
                User_Info_DB userInfoDB = new User_Info_DB();
                UserDetails userInfo = userInfoDB.getUserData(sharedPreferences.getString("userID", null));

                // Set User's Name, Email and Photo
                txtNameUser_Nav.setText(userInfo.getCustomersFirstname().concat(" ").concat(userInfo.getCustomersLastname()));
                String url = ConstantValues.ECOMMERCE_URL + userInfo.getCustomersPicture();

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .error(R.drawable.profile)
                        .placeholder(R.drawable.profile)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH);

                Glide.with(this)
                        .load(url)
                        .apply(options)
                        .into(photoUser);

            } else {
                // Set Default Name, Email and Photo
                photoUser.setImageResource(R.drawable.profile);
                txtSubscribe_Nav.setVisibility(View.VISIBLE);
                txtNameUser_Nav.setVisibility(View.GONE);
                txtWelcome_Nav.setVisibility(View.GONE);
            }

        } else {
            // Set Default Name, Email and Photo
            photoUser.setImageResource(R.drawable.profile);
            txtSubscribe_Nav.setVisibility(View.VISIBLE);
            txtNameUser_Nav.setVisibility(View.GONE);
            txtWelcome_Nav.setVisibility(View.GONE);
        }

        txtSubscribe_Nav.setOnClickListener(v -> startActivity(new Intent(Menu_NavDrawer_Acititty.this, LoginActivity.class)));
    }

    @Override
    public void onFragmentInteraction(String value) {
        addBadge(value);
    }

    @Override
    public void onAddBadge(String value) {
        addBadge(value);
    }

    public void addBadge(String value) {
        boolean containProducts = !value.equals("0");
        if (containProducts) {
            if (Integer.parseInt(value) > 99) text.setText("99+");
            else text.setText(value);
            itemView.removeView(badge);
            itemView.addView(badge);
        } else {
            itemView.removeView(badge);
            itemView.addView(badge);
            itemView.removeViewAt(2);
        }
    }

    @Override
    public void onChangeName(String value) {
        txtNameUser_Nav.setText(value);
    }

}
