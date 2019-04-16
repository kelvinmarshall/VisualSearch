package dev.marshall.visualsearch;



import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.visualsearch.Auth.SignInFragment;
import dev.marshall.visualsearch.Database.Model.Cart;
import dev.marshall.visualsearch.adapter.CartAdapter;
import dev.marshall.visualsearch.api.ApiClient;
import dev.marshall.visualsearch.api.model.AccessToken;
import dev.marshall.visualsearch.api.model.STKPush;
import dev.marshall.visualsearch.model.User;
import dev.marshall.visualsearch.utils.NotificationUtils;
import dev.marshall.visualsearch.utils.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static dev.marshall.visualsearch.utils.Utils.BUSINESS_SHORT_CODE;
import static dev.marshall.visualsearch.utils.Utils.CALLBACKURL;
import static dev.marshall.visualsearch.utils.Utils.PARTYB;
import static dev.marshall.visualsearch.utils.Utils.PASSKEY;
import static dev.marshall.visualsearch.utils.Utils.PUSH_NOTIFICATION;
import static dev.marshall.visualsearch.utils.Utils.REGISTRATION_COMPLETE;
import static dev.marshall.visualsearch.utils.Utils.TOPIC_GLOBAL;
import static dev.marshall.visualsearch.utils.Utils.TRANSACTION_TYPE;


public class CheckoutFragment extends Fragment {
    private TextView shipping_address,phonenumber,price_total;
    private ImageView edit_address,edit_phone;
    private ContentLoadingProgressBar wait;
    private RecyclerView recycler_cart;
    private MaterialButton place_order;
    private SweetAlertDialog mProgressDialog;
    private Double total;

    private List<Cart> cartArrayList=new ArrayList<>();
    private CartAdapter cartAdapter;
    private CompositeDisposable compositeDisposable;

    private DatabaseReference Users;
    private FirebaseUser muser;
    private User model;

    private String mFireBaseRegId;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    private ApiClient mApiClient;




    public CheckoutFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Users= FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_checkout, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compositeDisposable=new CompositeDisposable();

        muser=FirebaseAuth.getInstance().getCurrentUser();
        mApiClient = new ApiClient();
        mApiClient.setIsDebug(true); //Set True to enable logging, false to disable.
        recycler_cart=view.findViewById(R.id.recycler_cart);
        recycler_cart.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler_cart.setHasFixedSize(true);
        mProgressDialog = new SweetAlertDialog(getActivity(),SweetAlertDialog.PROGRESS_TYPE);
        shipping_address=view.findViewById(R.id.shipping_address);
        phonenumber=view.findViewById(R.id.phonenumber);
        price_total=view.findViewById(R.id.total_price);
        edit_address=view.findViewById(R.id.edit_address);
        edit_phone=view.findViewById(R.id.edit_phonenumber);
        wait=view.findViewById(R.id.wait);
        place_order=view.findViewById(R.id.place_order);

        getAccessToken();

        loadItems();

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(TOPIC_GLOBAL);
                    getFirebaseRegId();

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    NotificationUtils.createNotification(getContext(), message);
                    showResultDialog(message);
                }
            }
        };

        getFirebaseRegId();

        place_order.setOnClickListener(v -> placeOrder(phonenumber.getText().toString()));

        edit_address.setOnClickListener(v -> editAddress(muser));
        edit_phone.setOnClickListener(v -> editPhone(muser));

        if (muser!=null){
            Users.child(muser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    model=dataSnapshot.getValue(User.class);

                    assert model != null;
                    shipping_address.setText(model.getShipping_address());
                    phonenumber.setText(model.getPhonenumber());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

    }

    private void placeOrder(String number) {
        performSTKPush(number);
    }

    private void editPhone(final FirebaseUser muser) {

        if (muser !=null){
            AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
            dialog.setTitle("PhoneNumber");

            View view=LayoutInflater.from(getActivity()).inflate(R.layout.edit_phone,null);

            final TextInputEditText edit=view.findViewById(R.id.edt_phone);
            final TextInputLayout inputLayout=view.findViewById(R.id.textInput);

            edit.setText(model.getPhonenumber());

            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    HashMap<String,Object> map=new HashMap<String, Object>();
                    map.put("phonenumber",edit.getText().toString());
                    final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#FEDBD0"));
                    pDialog.setTitleText("Loading");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    Users.child(muser.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pDialog.dismissWithAnimation();
                            new SweetAlertDialog(getActivity(),SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success")
                                    .setContentText("Phonenumber was added")
                                    .show();
                            dialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Error adding phone!")
                                    .show();
                        }
                    });
                }
            });

            dialog.setView(view);
            dialog.show();
        }
        else {
            SignInFragment signInFragment=new SignInFragment();
            signInFragment.show(getChildFragmentManager(),signInFragment.getTag());
        }

    }

    private void editAddress(final FirebaseUser muser) {
        if (muser !=null){
            AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
            builder.setTitle("Shipping address");

            View view=LayoutInflater.from(getActivity()).inflate(R.layout.edit_address,null);



            final TextInputEditText edit=view.findViewById(R.id.edt_address);
            final TextInputLayout inputLayout=view.findViewById(R.id.textInput);

            edit.setText(model.getShipping_address());
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(final DialogInterface dialog, int which) {
                    HashMap<String,Object> map=new HashMap<>();
                    map.put("shipping_address",edit.getText().toString());
                    final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#FEDBD0"));
                    pDialog.setTitleText("Loading");
                    pDialog.setCancelable(false);
                    pDialog.show();

                    Users.child(muser.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            pDialog.dismissWithAnimation();
                            new SweetAlertDialog(getActivity(),SweetAlertDialog.SUCCESS_TYPE)
                                    .setTitleText("Success")
                                    .setContentText("Shipping address was added")
                                    .show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Error adding address!")
                                    .show();
                        }
                    });
                }
            });
            builder.setView(view);
            builder.setCancelable(false);
            builder.show();


        }
        else {
            SignInFragment signInFragment=new SignInFragment();
            signInFragment.show(getChildFragmentManager(),signInFragment.getTag());
        }
    }

    private void loadItems() {
        compositeDisposable.add(Utils.cartRepository.getCartItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<Cart>>() {
                    @Override
                    public void accept(List<Cart> carts) throws Exception {
                        displayItems(carts);
                    }
                })
        );
    }

    private void displayItems(List<Cart> carts) {

        cartArrayList=carts;
        cartAdapter=new CartAdapter(getActivity(),carts);
        recycler_cart.setAdapter(cartAdapter);

        Double adouble=0.1*Utils.cartRepository.sumPrice();
        Double shipping=0.1*Utils.cartRepository.sumPrice();
        total=shipping+adouble+Utils.cartRepository.sumPrice();

        price_total.setText("KES "+new DecimalFormat("#.##").format(total));
    }

    private void getAccessToken() {
        mApiClient.setGetAccessToken(true);
        mApiClient.mpesaService().getAccessToken().enqueue(new Callback<AccessToken>() {
            @Override
            public void onResponse(@NonNull Call<AccessToken> call, @NonNull Response<AccessToken> response) {

                if (response.isSuccessful()) {
                    mApiClient.setAuthToken(response.body().accessToken);
                }
            }

            @Override
            public void onFailure(@NonNull Call<AccessToken> call, @NonNull Throwable t) {

            }
        });
    }
    private void performSTKPush(String phone_number) {
        mProgressDialog.getProgressHelper().setBarColor(Color.parseColor("#FEDBD0"));
        mProgressDialog.setTitleText(getString(R.string.title_wait));
        mProgressDialog.setContentText(getString(R.string.dialog_message_processing));
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        String timestamp = Utils.getTimestamp();
        STKPush stkPush = new STKPush(
                BUSINESS_SHORT_CODE,
                Utils.getPassword(BUSINESS_SHORT_CODE, PASSKEY, timestamp),
                timestamp,
                TRANSACTION_TYPE,
                new DecimalFormat("#").format(total),
                Utils.sanitizePhoneNumber(phone_number),
                PARTYB,
                Utils.sanitizePhoneNumber(phone_number),
                CALLBACKURL + mFireBaseRegId,
                "test", //The account reference
                "test"  //The transaction description
        );

        mApiClient.setGetAccessToken(false);

        mApiClient.mpesaService().sendPush(stkPush).enqueue(new Callback<STKPush>() {
            @Override
            public void onResponse(@NonNull Call<STKPush> call, @NonNull Response<STKPush> response) {
                mProgressDialog.dismiss();
                try {
                    if (response.isSuccessful()) {
                        Log.d("post submitted to. %s", String.valueOf(response.body()));
                    } else {
                        Log.e("Response %s", response.errorBody().string());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<STKPush> call, @NonNull Throwable t) {
                mProgressDialog.dismiss();
            }
        });
    }
    private void showResultDialog(String result) {
        new SweetAlertDialog(Objects.requireNonNull(getActivity()), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText(getString(R.string.title_success))
                .setContentText(getString(R.string.dialog_message_success))
                .setConfirmClickListener(SweetAlertDialog::dismissWithAnimation)
                .show();
    }

    private void getFirebaseRegId() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( getActivity(),  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {

                mFireBaseRegId= instanceIdResult.getToken();

            }
        });
    }


    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onDestroy();
    }
    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadItems();
        // register GCM registration complete receiver

        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity())).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(Objects.requireNonNull(getContext()));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

}