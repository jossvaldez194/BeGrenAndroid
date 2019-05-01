package com.hakagamesstudio.begreen.views.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.hakagamesstudio.begreen.R;
import com.hakagamesstudio.begreen.databases.User_Info_DB;
import com.hakagamesstudio.begreen.pojos.address_model.AddressData;
import com.hakagamesstudio.begreen.pojos.address_model.AddressDetails;
import com.hakagamesstudio.begreen.pojos.user_model.UserData;
import com.hakagamesstudio.begreen.pojos.user_model.UserDetails;
import com.hakagamesstudio.begreen.retrofit.ApiUtils;
import com.hakagamesstudio.begreen.retrofit.CallBackRetrofit;
import com.hakagamesstudio.begreen.support.BeGreenSupport;
import com.hakagamesstudio.begreen.support.ConstantValues;
import com.hakagamesstudio.begreen.support.MyAppPrefsManager;
import com.hakagamesstudio.begreen.views.activities.Menu_NavDrawer_Acititty;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class PerfilUserFragment extends Fragment {

    @BindView(R.id.txtNameProfileUser)
    TextView txtNameUser;
    @BindView(R.id.txtAddressProfileUser)
    TextView txtAddressUser;
    @BindView(R.id.txtEMailProfileUser)
    TextView txtEmailUser;
    @BindView(R.id.btnCambiarContrasena)
    Button btnCambiarContra;
    @BindView(R.id.btnCerrarSesion)
    Button btnCerrarSesion;
    @BindView(R.id.imgEditNameAddressProfile)
    ImageView imgEditName;

    private Unbinder mUnbinder;
    private SharedPreferences sharedPreferences;
    private Context mContext;

    private UserDetails userInfo;
    private User_Info_DB userInfoDB = new User_Info_DB();
    private String customers_id;
    private CallBackRetrofit mCall;
    private List<AddressDetails> addressesList = new ArrayList<>();
    private MaterialDialog mProgress;

    private ProfileCallback mListener;

    public PerfilUserFragment() {

    }

    public static PerfilUserFragment newInstance() {
        PerfilUserFragment fragment = new PerfilUserFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) mContext = getContext();
        mCall = ApiUtils.getAPIService();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_perfil_user, container, false);
        mUnbinder = ButterKnife.bind(this, rootView);
        sharedPreferences = mContext.getSharedPreferences("UserInfo", MODE_PRIVATE);
        customers_id = mContext.getSharedPreferences("UserInfo", MODE_PRIVATE).getString("userID", "");
        boolean isFirst = mContext.getSharedPreferences("UserInfo", MODE_PRIVATE).getBoolean("isFirstOpenProfile", true);
        userInfo = userInfoDB.getUserData(customers_id);
        if (isFirst) {
            mProgress = BeGreenSupport.getInstance().createProgressDialog("Cargando datos", mContext);
            mProgress.show();
            addUserAddress();
        } else {
            txtNameUser.setText(userInfo.getCustomersFirstname().concat(" ").concat(userInfo.getCustomersLastname()));
            txtAddressUser.setText(mContext.getSharedPreferences("UserInfo", MODE_PRIVATE).getString("DireccionUser", ""));
            txtEmailUser.setText(userInfo.getCustomersEmailAddress());
        }

        if (getActivity() != null) {
            Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
            AutoCompleteTextView search = toolbar.findViewById(R.id.searchEditText);
            search.setVisibility(View.GONE);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            if (actionBar != null) {
                actionBar.setTitle("");
            }
        }

        btnCerrarSesion.setOnClickListener(v -> showDialogCloseSesion());

        btnCambiarContra.setOnClickListener(v -> showDialogDescription(false));
        imgEditName.setOnClickListener(v -> showDialogDescription(true));
        return rootView;
    }

    private void setDataView() {
        txtNameUser.setText(userInfo.getCustomersFirstname().concat(" ").concat(userInfo.getCustomersLastname()));
        txtAddressUser.setText(addressesList.get(0).getStreet());
        txtEmailUser.setText(userInfo.getCustomersEmailAddress());

        if (getActivity() != null) {
            SharedPreferences.Editor editor = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
            editor.putBoolean("isFirstOpenProfile", false);
            editor.apply();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void addUserAddress() {
        sharedPreferences = mContext.getSharedPreferences("UserInfo", MODE_PRIVATE);
        String name = sharedPreferences.getString("userName", "");
        String address = sharedPreferences.getString("DireccionUser", "");
        String customers_default_address_id = mContext.getSharedPreferences("UserInfo", MODE_PRIVATE).getString("userDefaultAddressID", "");

        Call<AddressData> call = mCall.addUserAddress(
                customers_id,
                name,
                "",
                address,
                "",
                "",
                "",
                "",
                customers_default_address_id);
        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(@NonNull Call<AddressData> call, @NonNull Response<AddressData> response) {
                if (response.body() != null) {
                    if (response.isSuccessful()) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            RequestAllAddresses();
                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            mProgress.dismiss();
                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            mProgress.dismiss();
                            Toast.makeText(mContext, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        mProgress.dismiss();
                        Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddressData> call, @NonNull Throwable throwable) {
                mProgress.dismiss();
                Toast.makeText(mContext, "NetworkCallFailure : " + throwable, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void MakeAddressDefault(String customerID, String addressID) {
        Call<AddressData> call = mCall.updateDefaultAddress(customerID, addressID);
        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(@NonNull Call<AddressData> call, @NonNull retrofit2.Response<AddressData> response) {
                mProgress.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1")) {
                        SharedPreferences.Editor editor = getActivity().getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                        editor.putString("userDefaultAddressID", addressID);
                        editor.apply();
                        setDataView();
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddressData> call, @NonNull Throwable t) {
                mProgress.dismiss();
                Toast.makeText(mContext, "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void RequestAllAddresses() {
        Call<AddressData> call = mCall.getAllAddress(customers_id);
        call.enqueue(new Callback<AddressData>() {
            @Override
            public void onResponse(@NonNull Call<AddressData> call, @NonNull retrofit2.Response<AddressData> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getSuccess().equalsIgnoreCase("1")) {
                            addAddresses(response.body());
                        } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                            mProgress.dismiss();
                            Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            mProgress.dismiss();
                            Toast.makeText(mContext, getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(getContext(), response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AddressData> call, @NonNull Throwable t) {
                mProgress.dismiss();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void addAddresses(AddressData addressData) {
        addressesList = addressData.getData();
        if (addressesList.size() == 1) {
            MakeAddressDefault(customers_id, String.valueOf(addressesList.get(0).getAddressId()));
        }
    }

    private void showDialogDescription(boolean fromName) {
        MaterialDialog dialog = new MaterialDialog.Builder(mContext)
                .customView(R.layout.custom_content_change_name, false)
                .canceledOnTouchOutside(false)
                .progressIndeterminateStyle(false)
                .show();

        Button btnSaveDes = dialog.getView().findViewById(R.id.btn_save_new_name);
        EditText editDes = dialog.getView().findViewById(R.id.editNameChangeProfile);
        EditText editOldPass = dialog.getView().findViewById(R.id.editOldPassword);
        ImageView imgClose = dialog.getView().findViewById(R.id.btnCloseDialogName);
        TextView txtTitle = dialog.getView().findViewById(R.id.textView13);

        if (fromName) {
            txtTitle.setText("Cambiar nombre");
            editDes.setHint("Nuevo nombre");
            btnSaveDes.setText("Cambiar nombre");
            editOldPass.setVisibility(View.GONE);
            editDes.setInputType(InputType.TYPE_CLASS_TEXT);

        } else {
            txtTitle.setText("Cambiar cotraseña");
            editDes.setHint("Nueva contraseña");
            btnSaveDes.setText("Cambiar contraseña");
            editOldPass.setVisibility(View.VISIBLE);
            editDes.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
            editOldPass.setInputType(InputType.TYPE_CLASS_TEXT |
                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }

        imgClose.setOnClickListener(v -> dialog.dismiss());

        btnSaveDes.setOnClickListener(v -> {
            if (!editDes.getText().toString().isEmpty()) {
                if (fromName) {
                    dialog.dismiss();
                    updateCustomerInfo(editDes.getText().toString(), userInfo.getCustomersPassword());
                } else {
                    if (editOldPass.getText().toString().equals(userInfo.getCustomersPassword())) {
                        dialog.dismiss();
                        updateCustomerInfo(userInfo.getCustomersFirstname(), editDes.getText().toString());
                    } else {
                        Toast.makeText(mContext, "Contraseña anterior no coincide", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (fromName) {
                    Toast.makeText(mContext, "No ha ingresado ningun nombre", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "No ha ingresado ninguna contraseña", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    private void updateCustomerInfo(String newName, String newPassword) {
        MaterialDialog mProgressDialog = BeGreenSupport.getInstance().createProgressDialog("Cambiando nombre de usuario", mContext);
        mProgressDialog.show();
        Call<UserData> call = mCall.updateCustomerInfo(
                customers_id,
                newName,
                "",
                "",
                "",
                "",
                "",
                newPassword);

        call.enqueue(new Callback<UserData>() {
            @Override
            public void onResponse(@NonNull Call<UserData> call, @NonNull retrofit2.Response<UserData> response) {
                mProgressDialog.dismiss();
                if (response.isSuccessful()) {
                    if (response.body().getSuccess().equalsIgnoreCase("1") && response.body().getData() != null) {
                        UserDetails userDetails = response.body().getData().get(0);

                        // Update in Local Databases as well
                        userInfoDB.updateUserData(userDetails);
                        userInfoDB.updateUserPassword(userDetails);

                        // Get the User's Info from the Local Databases User_Info_DB
                        userInfo = userInfoDB.getUserData(customers_id);

                        // Set the userName in SharedPreferences
                        SharedPreferences.Editor editor = mContext.getSharedPreferences("UserInfo", MODE_PRIVATE).edit();
                        editor.putString("userName", userDetails.getCustomersFirstname() + " " + userDetails.getCustomersLastname());
                        editor.apply();

                        mListener.onChangeName(newName);
                        txtNameUser.setText(newName);
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else if (response.body().getSuccess().equalsIgnoreCase("0")) {
                        // Unable to Update User's Info.
                        Toast.makeText(mContext, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        // Unable to get Success status
                        Toast.makeText(getContext(), getString(R.string.unexpected_response), Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(getContext(), "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UserData> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
                Toast.makeText(getContext(), "NetworkCallFailure : " + t, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showDialogCloseSesion() {
        new MaterialDialog.Builder(mContext)
                .title("Cerrar sesión")
                .content("Hola ".concat(userInfo.getCustomersFirstname()).concat(", ").concat("¿Deseas cerrar tu sesión actual?"))
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .progressIndeterminateStyle(false)
                .negativeText("Cancelar")
                .positiveText("Cerrar sesón")
                .positiveColor(mContext.getResources().getColor(R.color.colorPrimary))
                .negativeColor(mContext.getResources().getColor(R.color.colorPrimary))
                .onPositive((dialog, which) -> {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userID", "");
                    editor.apply();

                    MyAppPrefsManager myAppPrefsManager = new MyAppPrefsManager(mContext);
                    myAppPrefsManager.setUserLoggedIn(false);

                    ConstantValues.IS_USER_LOGGED_IN = myAppPrefsManager.isUserLoggedIn();
                    startActivity(new Intent(getContext(), Menu_NavDrawer_Acititty.class));
                    getActivity().finish();
                    getActivity().overridePendingTransition(R.anim.enter_from_right, R.anim.exit_out_right);
                })
                .onNegative((dialog, which) -> dialog.dismiss())
                .show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ProfileCallback) {
            mListener = (ProfileCallback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface ProfileCallback {
        void onChangeName(String value);
    }
}
