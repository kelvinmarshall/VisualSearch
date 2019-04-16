package dev.marshall.visualsearch.Auth;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import dev.marshall.visualsearch.R;


public class ResetPasswordFragment extends BottomSheetDialogFragment implements View.OnClickListener {

  private TextInputEditText textemail;
  TextInputLayout Inputemail;
  private MaterialButton reset;
  private FirebaseAuth mAuth;


  public ResetPasswordFragment() {
    // Required empty public constructor
  }
  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    mAuth= FirebaseAuth.getInstance();
  }



  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_reset_password, container, false);

    return view;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    textemail=view.findViewById(R.id.input_email);
    Inputemail=view.findViewById(R.id.textInputemail);
    reset=view.findViewById(R.id.reset_button);

    reset.setOnClickListener(this);
  }

  @Override
  public void onClick(View v) {
    int id=v.getId();

    if (id==R.id.reset_button){
      if (TextUtils.isEmpty(textemail.getText())){
        Inputemail.setError(getString(R.string.error_input));
      }
    }
  }
}