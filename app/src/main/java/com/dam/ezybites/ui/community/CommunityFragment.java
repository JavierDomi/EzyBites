package com.dam.ezybites.ui.community;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dam.ezybites.R;

public class CommunityFragment extends Fragment {

    private CommunityViewModel mViewModel;

    public static CommunityFragment newInstance() {
        return new CommunityFragment();
    }

    @RequiresApi(api = Build.VERSION_CODES.S)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_community, container, false);

        CardView cardBlur = view.findViewById(R.id.lol);

        RenderEffect blurEffect = RenderEffect.createBlurEffect(10f, 10f, Shader.TileMode.CLAMP);
        cardBlur.setRenderEffect(blurEffect);

        TextView textView = view.findViewById(R.id.textView); // Asegúrate de usar el ID correcto de tu TextView

// Texto completo
        String text = "Recetas by Jorge Tesch";

// Crear un SpannableString
        SpannableString spannableString = new SpannableString(text);

// Subrayar solo "Jorge Tesch"
        int start = text.indexOf("Jorge Tesch");
        int end = start + "Jorge Tesch".length();
        spannableString.setSpan(new UnderlineSpan(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

// Establecer el texto en el TextView
        textView.setText(spannableString);


        return view;
    }
}