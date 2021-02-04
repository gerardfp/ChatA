package com.company.chata;

import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.company.chata.databinding.FragmentSignInBinding;
import com.company.chata.databinding.FragmentSignUpBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;

import java.util.UUID;


public class SignUpFragment extends Fragment {

    private SignUpViewModel vm;

    public static class SignUpViewModel extends ViewModel {
        Uri uri;
    }

    private FragmentSignUpBinding binding;
    private NavController nav;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentSignUpBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        vm = new ViewModelProvider(this).get(SignUpViewModel.class);
        nav = Navigation.findNavController(view);
        mAuth =  FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();


        binding.emailSignUp.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String password = binding.password.getText().toString();
            String name = binding.nombre.getText().toString();

            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {

                            storage.getReference("avatars/"+ UUID.randomUUID())
                                    .putFile(vm.uri)
                                    .continueWithTask(task2 -> task2.getResult().getStorage().getDownloadUrl())
                                    .addOnSuccessListener(url -> {

                                        mAuth.getCurrentUser()
                                                .updateProfile(
                                                        new UserProfileChangeRequest.Builder()
                                                                .setDisplayName(name)
                                                                .setPhotoUri(url)
                                                                .build());
                                    });

                            nav.navigate(R.id.action_signUpFragment_to_chatFragment);
                        } else {
                            Toast.makeText(requireContext(), task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        binding.foto.setOnClickListener(v -> {
            galeria.launch("image/*");
        });

        if(vm.uri != null) Glide.with(requireView()).load(vm.uri).into(binding.foto);
    }

    ActivityResultLauncher<String> galeria = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        vm.uri = uri;
        Glide.with(requireView()).load(uri).into(binding.foto);
    });
}