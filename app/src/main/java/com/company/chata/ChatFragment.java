package com.company.chata;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.company.chata.databinding.FragmentChatBinding;
import com.company.chata.databinding.ViewholderMensajeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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

        ChatAdapter chatAdapter = new ChatAdapter();
        binding.chat.setAdapter(chatAdapter);

        binding.enviar.setOnClickListener(v -> {
            String texto = binding.mensaje.getText().toString();
            String fecha = LocalDateTime.now().toString();
            String autor = mAuth.getCurrentUser().getEmail();

            mDb.collection("mensajes").add(new Mensaje(texto, fecha, autor));

            binding.mensaje.setText("");
        });

        mDb.collection("mensajes").orderBy("fecha").addSnapshotListener((value, error) -> {
            chat.clear();
            value.forEach(document -> {
                chat.add(new Mensaje(
                    document.getString("mensaje"),
                    document.getString("fecha"),
                    document.getString("autor")));
            });

            chatAdapter.notifyDataSetChanged();
            binding.chat.scrollToPosition(chat.size()-1);
        });
    }

    class ChatAdapter extends RecyclerView.Adapter<MensajeViewHolder>{

        @NonNull
        @Override
        public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MensajeViewHolder(ViewholderMensajeBinding.inflate(getLayoutInflater(), parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {

            Mensaje mensaje = chat.get(position);

            holder.binding.nombre.setText(mensaje.autor);
            holder.binding.mensaje.setText(mensaje.mensaje);
            holder.binding.fecha.setText(mensaje.fecha);

            if(mensaje.autor.equals(mAuth.getCurrentUser().getEmail())){
                holder.binding.root.setGravity(Gravity.END);
            } else {
                holder.binding.root.setGravity(Gravity.START);
            }
        }

        @Override
        public int getItemCount() {
            return chat.size();
        }
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder{
        ViewholderMensajeBinding binding;
        public MensajeViewHolder(@NonNull ViewholderMensajeBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}