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
}
