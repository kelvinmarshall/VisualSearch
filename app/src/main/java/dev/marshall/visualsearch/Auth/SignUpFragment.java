package dev.marshall.visualsearch.Auth;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.visualsearch.R;
import dev.marshall.visualsearch.model.User;


public class SignUpFragment extends BottomSheetDialogFragment {
    private TextInputEditText textemail,textpassword,textusername,confirm;
    private TextInputLayout InputPass,Inputemail,Inputusername,Inputconfirm;
    private MaterialButton register,login;
    private FirebaseAuth mAuth;
    private DatabaseReference users;
    ContentLoadingProgressBar wait;
    String Email,Pass,Username,Confirm;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        mAuth=FirebaseAuth.getInstance();
        users= FirebaseDatabase.getInstance().getReference().child("Users");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textemail=view.findViewById(R.id.input_email);
        textpassword=view.findViewById(R.id.input_password);
        InputPass=view.findViewById(R.id.textInputpassword);
        Inputemail=view.findViewById(R.id.textInputemail);
        Inputusername=view.findViewById(R.id.textInputusername);
        Inputconfirm=view.findViewById(R.id.textInputconfirmpass);
        textusername=view.findViewById(R.id.input_username);
        confirm=view.findViewById(R.id.input_confirm_password);
        login=view.findViewById(R.id.sign_in_button);
        register=view.findViewById(R.id.sign_up_button);
        wait=view.findViewById(R.id.wait);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignInFragment signInFragment=new SignInFragment();
                signInFragment.show(getChildFragmentManager(),signInFragment.getTag());
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Email=textemail.getText().toString();
                Pass=textpassword.getText().toString();
                Username=textusername.getText().toString();
                Confirm=confirm.getText().toString();

                if (TextUtils.isEmpty(Pass)&&TextUtils.isEmpty(Email)
                        &&TextUtils.isEmpty(Username)&& TextUtils.isEmpty(Confirm)) {
                    InputPass.setError(getString(R.string.error_password));
                    Inputemail.setError(getString(R.string.error_input));
                    Inputusername.setError(getString(R.string.error_input));
                    Inputconfirm.setError(getString(R.string.error_input));

                } else {
                    InputPass.setError(null);
                    Inputemail.setError(null);
                    Inputusername.setError(null);
                    Inputconfirm.setError(null); // Clear the error

                    if (TextUtils.equals(Pass,Confirm)&&Pass.length()>=8){
                        createUser(Email,Pass,Username);

                    }
                    else{
                        Inputconfirm.setError(getString(R.string.error_confirm));
                        InputPass.setError(getString(R.string.error_password));
                    }

                }

            }
        });
    }

    private void createUser(String email, String pass, final String username) {

        textemail.setEnabled(false);
        textpassword.setEnabled(false);
        textusername.setEnabled(false);
        confirm.setEnabled(false);
        login.setEnabled(false);
        register.setEnabled(false);
        wait.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            final FirebaseUser user = mAuth.getCurrentUser();
                            if (user!=null) {
                                User model_user = new User(username, "", "", "");
                                users.child(user.getUid()).setValue(model_user);

                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Success!")
                                                    .setContentText("User created and verification email sent!")
                                                    .show();
                                            mAuth.signOut();

                                            dismiss();
                                        } else {
                                            new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                                    .setTitleText("Error!")
                                                    .setContentText("Sending Verification E-mail Failed!")
                                                    .show();
                                        }

                                    }
                                });
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Sign up failed!")
                                    .show();
                            textemail.setEnabled(true);
                            textpassword.setEnabled(true);
                            textusername.setEnabled(true);
                            confirm.setEnabled(true);
                            login.setEnabled(true);
                            register.setEnabled(true);

                        }
                    }
                });

    }

}