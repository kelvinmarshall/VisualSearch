package dev.marshall.visualsearch;



import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.visualsearch.Auth.SignInFragment;
import dev.marshall.visualsearch.Database.Model.WishList;
import dev.marshall.visualsearch.adapter.WishListAdapter;
import dev.marshall.visualsearch.model.User;
import dev.marshall.visualsearch.utils.Utils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class AccountFragment extends Fragment {
    private TextView greetings,username,address,phone,itemwish,itemorder;
    private ImageView profile;

    private FirebaseUser user;
    private DatabaseReference Users;

    private List<WishList> wishListList=new ArrayList<>();
    private WishListAdapter wishListAdapter;
    private CompositeDisposable compositeDisposable;
    private User model;


    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Users= FirebaseDatabase.getInstance().getReference().child("Users");
        user= FirebaseAuth.getInstance().getCurrentUser();
        compositeDisposable=new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_account, container, false);

        return view;
    }

/** 
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        greetings=view.findViewById(R.id.greetings);
        username=view.findViewById(R.id.username);
        address=view.findViewById(R.id.shipping_address);
        phone=view.findViewById(R.id.phonenumber);
        itemwish=view.findViewById(R.id.wishlist_items);
        profile=view.findViewById(R.id.profile_image);
        itemorder=view.findViewById(R.id.orddr_items);
        ImageView edt_user = view.findViewById(R.id.edit_user);
        ImageView edt_addr = view.findViewById(R.id.edt_address);
        ImageView edt_phone = view.findViewById(R.id.edt_phone);

        //Get the time of day
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        String greetn=null;
        if(hour>= 12 && hour < 17){
            greetn = getString(R.string.afternoon);
        } else if(hour >= 17 && hour < 21){
            greetn = getString(R.string.evening);
        } else if(hour >= 21 && hour < 24){
            greetn = getString(R.string.night);
        } else {
            greetn = getString(R.string.morning);
        }

        greetings.setText(greetn);

        edt_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editUser();
            }
        });
        edt_addr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editAddress(user);
            }
        });
        edt_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhone(user);
            }
        });

        setvalues();
        compositeDisposable.add(Utils.wishListRepository.getFavItems()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<List<WishList>>() {
                    @Override
                    public void accept(List<WishList> wishLists) throws Exception {
                        wishListList=wishLists;
                        wishListAdapter=new WishListAdapter(getActivity(),wishLists);
                        itemwish.setText(wishListAdapter.getItemCount()+" Items");
                    }
                })
        );

    }
    */

    private void setvalues() {
        Users.child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                model =dataSnapshot.getValue(User.class);
                username.setText(model.getUsername());
                address.setText(model.getShipping_address());
                phone.setText(model.getPhonenumber());

                Glide.with(getActivity()).load(model.getImage()).placeholder(R.drawable.ic_user).into(profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void editUser() {
        AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
        dialog.setTitle("Username");

        View view=LayoutInflater.from(getActivity()).inflate(R.layout.edit_user,null);



        final TextInputEditText edit=view.findViewById(R.id.edt_user);
        final TextInputLayout inputLayout=view.findViewById(R.id.textInput);
        edit.setText(model.getUsername());

        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                HashMap<String,Object> map=new HashMap<>();
                map.put("username",edit.getText().toString());

                final SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#FEDBD0"));
                pDialog.setTitleText("Loading");
                pDialog.setCancelable(false);
                pDialog.show();
                Users.child(user.getUid()).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        new SweetAlertDialog(getActivity(),SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Success")
                                .setContentText("Username was added")
                                .show();
                        pDialog.dismissWithAnimation();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Error adding username!")
                                .show();
                    }
                });
            }
        });
        dialog.setView(view);
        dialog.setCancelable(false);
        dialog.show();
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

}