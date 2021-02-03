package com.company.chata;

import android.graphics.PostProcessor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.company.chata.databinding.FragmentChatBinding;
import com.company.chata.databinding.FragmentSignInBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private NavController nav;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private List<Mensaje> chat = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return (binding = FragmentChatBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nav = Navigation.findNavController(view);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        binding.enviar.setOnClickListener(v -> {
            String texto = binding.mensaje.getText().toString();
            String fecha = LocalDateTime.now().toString();
            String autor = mAuth.getCurrentUser().getEmail();

            mDb.collection("mensajes").add(new Mensaje(texto, fecha, autor));
        });

        mDb.collection("mensajes").addSnapshotListener((value, error) -> {
            chat.clear();
            value.forEach(document -> {
                chat.add(new Mensaje(
                    document.getString("autor"),
                    document.getString("fecha"),
                    document.getString("mensaje")));
            });
        });
    }
}