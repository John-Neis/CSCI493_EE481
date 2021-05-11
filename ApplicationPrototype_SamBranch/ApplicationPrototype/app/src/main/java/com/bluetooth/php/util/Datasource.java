package com.bluetooth.php.util;

import com.bluetooth.php.R;
import com.bluetooth.php.ui.actions.Sign;

import java.util.ArrayList;
import java.util.List;

public class Datasource {
    private static List<Sign> signs = new ArrayList<Sign>();
    public static List<Sign> loadSigns(){
        signs.clear();
        signs.add(new Sign(R.string.A_label, R.drawable.a_image));
        signs.add(new Sign(R.string.B_label, R.drawable.b_image));
        signs.add(new Sign(R.string.C_label, R.drawable.c_image));
        signs.add(new Sign(R.string.D_label, R.drawable.d_image));
        signs.add(new Sign(R.string.E_label, R.drawable.e_image));
        signs.add(new Sign(R.string.F_label, R.drawable.f_image));
        signs.add(new Sign(R.string.G_label, R.drawable.g_image));
        signs.add(new Sign(R.string.H_label, R.drawable.h_image));
        signs.add(new Sign(R.string.I_label, R.drawable.i_image));
        signs.add(new Sign(R.string.J_label, R.drawable.j_image));
        signs.add(new Sign(R.string.K_label, R.drawable.k_image));
        signs.add(new Sign(R.string.L_label, R.drawable.l_image));
        signs.add(new Sign(R.string.M_label, R.drawable.m_image));
        signs.add(new Sign(R.string.N_label, R.drawable.n_image));
        signs.add(new Sign(R.string.O_label, R.drawable.o_image));
        signs.add(new Sign(R.string.P_label, R.drawable.p_image));
        signs.add(new Sign(R.string.Q_label, R.drawable.q_image));
        signs.add(new Sign(R.string.R_label, R.drawable.r_image));
        signs.add(new Sign(R.string.S_label, R.drawable.s_image));
        signs.add(new Sign(R.string.T_label, R.drawable.t_image));
        signs.add(new Sign(R.string.U_label, R.drawable.u_image));
        signs.add(new Sign(R.string.V_label, R.drawable.v_image));
        signs.add(new Sign(R.string.W_label, R.drawable.w_image));
        signs.add(new Sign(R.string.X_label, R.drawable.w_image));
        signs.add(new Sign(R.string.Y_label, R.drawable.y_image));
        signs.add(new Sign(R.string.Z_label, R.drawable.z_image));
        return signs;
    }
    public static String getGripCommand(String grip){
        String command = "";
        switch(grip){
            case "openHand":
                    command = "251 2 1000 1000 2 100";
                break;
            case "closedHand":
                    command = "251 2 2000 2000 2 100";
                break;
        }
        return command;
    }
    public static String getAslCommand(String letter){
        String command = "";
        switch(letter){
            case "A":
                command = "251 2 2000 2000 2 100";
                break;
            case "B":
                command = "251 2 1000 1000 2 100";
                break;
            case "C":
                command = "251 2 1500 1500 2 100";
                break;
            case "D":
                command = "004";
                break;
            case "E":
                command = "005";
                break;
            case "F":
                command = "006";
                break;
            case "G":
                command = "007";
                break;
            case "H":
                command = "008";
                break;
            case "I":
                command = "009";
                break;
            case "J":
                command = "010";
                break;
            case "K":
                command = "011";
                break;
            case "L":
                command = "012";
                break;
            case "M":
                command = "251 2 2000 2000 2 100";
                break;
            case "N":
                command = "251 2 2000 2000 2 100";
                break;
            case "O":
                command = "251 2 2000 2000 2 100";
                break;
            case "P":
                command = "016";
                break;
            case "Q":
                command = "017";
                break;
            case "R":
                command = "018";
                break;
            case "S":
                command = "019";
                break;
            case "T":
                command = "020";
                break;
            case "U":
                command = "021";
                break;
            case "V":
                command = "251 2 2000 2000 2 100";
                break;
            case "W":
                command = "251 2 2000 1000 2 100";
                break;
            case "X":
                command = "024";
                break;
            case "Y":
                command = "025";
                break;
            case "Z":
                command = "026";
                break;
        }
        return command;
    }
}
