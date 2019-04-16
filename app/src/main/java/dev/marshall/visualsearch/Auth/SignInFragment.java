package dev.marshall.visualsearch.Auth;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import cn.pedant.SweetAlert.SweetAlertDialog;
import dev.marshall.visualsearch.R;


public class SignInFragment extends BottomSheetDialogFragment {

  private TextInputEditText textemail,textpassword;
  private TextInputLayout InputPass,Inputemail;
  private MaterialButton register,login,reset;
  private FirebaseAuth mAuth;
  private String Email,Pass;
    ContentLoadingProgressBar wait;

  public SignInFragment() {
    // Required empty public constructor
  }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
        mAuth=FirebaseAuth.getInstance();
    }

    @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_sign_in, container, false);

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
      textemail=view.findViewById(R.id.input_email);
      textpassword=view.findViewById(R.id.input_password);
      Inputemail=view.findViewById(R.id.textInputemail);
      InputPass=view.findViewById(R.id.textInputpassword);
      login=view.findViewById(R.id.sign_in_button);
      register=view.findViewById(R.id.sign_up_button);
      reset=view.findViewById(R.id.btn_reset_password);
      wait=view.findViewById(R.id.wait);


      reset.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              ResetPasswordFragment resetPasswordFragment=new ResetPasswordFragment();
              resetPasswordFragment.show(getChildFragmentManager(),resetPasswordFragment.getTag());
              //dismiss();
          }
      });

      register.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              SignUpFragment signUpFragment=new SignUpFragment();
              signUpFragment.show(getChildFragmentManager(),signUpFragment.getTag());
              //dismiss();
          }
      });

      login.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              Email=textemail.getText().toString();
              Pass=textpassword.getText().toString();
              if (TextUtils.isEmpty(Pass) &&TextUtils.isEmpty(Email)) {
                  Inputemail.setError(getString(R.string.error_input));
                  InputPass.setError(getString(R.string.error_password));
              } else {
                  Inputemail.setError(null);
                  InputPass.setError(null); // Clear the error
                  if (Pass.length()>=8)
                      loginUser(Email,Pass);
                  else
                      InputPass.setError(getString(R.string.error_password));
              }


          }
      });


  }

    private void loginUser(String email, String pass) {
        register.setEnabled(false);
        textemail.setEnabled(false);
        textpassword.setEnabled(false);
        login.setEnabled(false);
        wait.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = mAuth.getCurrentUser();

                            if (user != null && user.isEmailVerified()) {
                                dismiss();
                                wait.setVisibility(View.INVISIBLE);
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                                        .setTitleText("Success!")
                                        .setContentText("Your now logged in!")
                                        .show();
                            }else{
                                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Please verify email address!")
                                        .show();
                                wait.setVisibility(View.INVISIBLE);
                                login.setEnabled(true);
                            }

                        } else {
                            // If sign in fails, display a message to the user.sudo apt upgrade
                            new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                                    .setTitleText("Oops...")
                                    .setContentText("Sign in failed!")
                                    .show();
                            wait.setVisibility(View.INVISIBLE);
                            textemail.setEnabled(true);
                            textpassword.setEnabled(true);
                            login.setEnabled(true);
                            register.setEnabled(true);
                        }

                    }
                });
    }
}