package com.company.chata;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.chata.databinding.FragmentChatBinding;
import com.company.chata.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;


public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private NavController nav;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentChatBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nav = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();
    }
}