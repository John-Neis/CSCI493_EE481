package com.bluetooth.php.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bluetooth.php.R;
import com.bluetooth.php.ui.actions.Sign;

import java.util.Collections;
import java.util.List;

public class AslSignAdapter extends RecyclerView.Adapter<AslViewHolder>{
    List<Sign> signList = Collections.emptyList();
    Context context;
    public AslSignAdapter(List<Sign> listData, Context context){
        this.signList = listData;
        this.context = context;
    }

    @NonNull
    @Override
    public AslViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.asl_sign_list_item,parent,false);
        AslViewHolder holder = new AslViewHolder(v);
        return holder;
    }

    @NonNull
    @Override
    public void onBindViewHolder(@NonNull AslViewHolder holder, int pos){
        Sign item = signList.get(pos);
        String item_text = context.getResources().getString(item.getStringResourceId());
        holder.asl_letter.setText(item_text);
        holder.asl_image.setImageResource(item.getImageResourceId());

    }
    @Override
    public int getItemCount(){
        return signList.size();
    }
}
