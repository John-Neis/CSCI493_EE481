package com.bluetooth.php.util;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bluetooth.php.R;
import com.bluetooth.php.ui.actions.ActionSelectASLSignFragment;

public class AslViewHolder extends RecyclerView.ViewHolder {
    TextView asl_letter;
    ImageView asl_image;
    AslViewHolder(View itemView){
        super(itemView);
        asl_letter = (TextView) itemView.findViewById(R.id.asl_sign_title);
        asl_image = (ImageView) itemView.findViewById(R.id.asl_image);
        itemView.setOnClickListener(this::onClick);
    }

    public void onClick(View view){
        int pos = getLayoutPosition();
        Log.d(Constants.TAG, "Item clicked : " + asl_letter.getText().toString());
        ActionSelectASLSignFragment.sendAslCommand(asl_letter.getText().toString());
    }
}
