package About;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.applicationprototype.R;

public class AboutTeamFragment extends Fragment {
    private ListView listView;
    String [] teamMemberNames = {"Sam Dressler","Jack Neis","Megan Larson",
                                    "Daryl Johnson","Jasmyn Loven"};

    int [] profilePics = {R.drawable.sam_profile_pic,R.drawable.jack_profile_pic,
            R.drawable.megan_profile_pic, R.drawable.daryl_profile_pic,
            R.drawable.jasmyn_profile_pic};

    String [] teamMemberRoles = {"Application Software","Embedded/Application Software","Electrical & Hardware",
    "Embedded Software", "Electrical & Hardware"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_team_fragment,container,false);
        /*Initialize the list view containing team members names, pictures, and roles*/
        init_list_view_items(view);

        return view;
    }

    private void init_list_view_items(View view) {
        listView = view.findViewById(R.id.team_members_lv);
        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setEnabled(false);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(this, TeamMemberBioActivity.class);
            }
        });
    }

    private class CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return profilePics.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int pos, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.team_member_list_item,null);
            TextView name = view.findViewById(R.id.team_member_name);
            TextView role = view.findViewById(R.id.team_member_role);
            ImageView profile_pic = view.findViewById(R.id.team_member_pic);

            name.setText(teamMemberNames[pos]);
            role.setText(teamMemberRoles[pos]);
            profile_pic.setImageResource(profilePics[pos]);

            return view;
        }
    }
}
