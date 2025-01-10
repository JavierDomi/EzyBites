package com.dam.ezybites.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.dam.ezybites.R; //esto no se si hace falta, lo he puesto para referenciar por ID
import com.dam.ezybites.databinding.FragmentHomeBinding;
import com.dam.ezybites.ui.home.innerFragments.home_amigos;
import com.dam.ezybites.ui.home.innerFragments.home_comunidades;
import com.dam.ezybites.ui.home.innerFragments.home_para_ti;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private int currentIndex = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //Navegacion de Fragmentos internos a HOME
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        TextView homeParaTi = root.findViewById(R.id.homeParaTi);
        TextView homeAmigos = root.findViewById(R.id.homeAmigos);
        TextView homeComunidades = root.findViewById(R.id.homeComunidades);

        int activeColor = ContextCompat.getColor(getContext(), R.color.white);
        int inactiveColor = ContextCompat.getColor(getContext(), R.color.grey);

        homeParaTi.setTextColor(activeColor);
        homeAmigos.setTextColor(inactiveColor);
        homeComunidades.setTextColor(inactiveColor);

        fragmentManager.beginTransaction()
                .replace(R.id.fragmentHomeInner, home_para_ti.class, null)
                .setReorderingAllowed(true)
                .commit();

        Map<Integer, Integer> buttonIndices = new HashMap<>();
        buttonIndices.put(R.id.homeParaTi, 0);
        buttonIndices.put(R.id.homeAmigos, 1);
        buttonIndices.put(R.id.homeComunidades, 2);

        // Configura los listeners de los botones
        View.OnClickListener buttonClickListener = v -> {
            int newIndex = buttonIndices.get(v.getId());
            if (newIndex != currentIndex) {
                Class<? extends Fragment> newFragment;
                switch (newIndex) {
                    case 0:
                        newFragment = home_para_ti.class;
                        break;
                    case 1:
                        newFragment = home_amigos.class;
                        break;
                    case 2:
                        newFragment = home_comunidades.class;
                        break;
                    default:
                        return;
                }

                // Determina la dirección de la transición
                int enterAnim = (newIndex > currentIndex) ? R.anim.slide_in_right : R.anim.slide_in_left;
                int exitAnim = (newIndex > currentIndex) ? R.anim.slide_out_left : R.anim.slide_out_right;

                // Realiza la transacción con animaciones
                fragmentManager.beginTransaction()
                        .setCustomAnimations(enterAnim, exitAnim, enterAnim, exitAnim)
                        .replace(R.id.fragmentHomeInner, newFragment, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name")
                        .commit();

                homeParaTi.setTextColor(newIndex == 0 ? activeColor : inactiveColor);
                homeAmigos.setTextColor(newIndex == 1 ? activeColor : inactiveColor);
                homeComunidades.setTextColor(newIndex == 2 ? activeColor : inactiveColor);

                // Actualiza el índice actual
                currentIndex = newIndex;
            }
        };

        // Asigna el listener a cada botón
        homeParaTi.setOnClickListener(buttonClickListener);
        homeAmigos.setOnClickListener(buttonClickListener);
        homeComunidades.setOnClickListener(buttonClickListener);

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}