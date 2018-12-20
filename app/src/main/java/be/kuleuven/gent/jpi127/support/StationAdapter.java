package be.kuleuven.gent.jpi127.support;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.fragments.general.StationFragment;
import be.kuleuven.gent.jpi127.model.Station;

public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolderProjects> {

    private List<Station> stations;
    private Context context;
    Fragment fragment;
    FragmentManager fragmentManager;

    /**
     * The only needed constructor.
     * @param stations
     * @param context
     * @param fragmentManager
     */
    public StationAdapter(List<Station> stations, Context context, FragmentManager fragmentManager) {
        this.stations = stations;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    /**
     * Override method responsible for linking the ui-elements to the adapter.
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public StationAdapter.ViewHolderProjects onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_trains,parent,false);

        return new StationAdapter.ViewHolderProjects(v);
    }

    /**
     * Override method responsible for generating the different elements in the recycler view.
     * Complete with a listener.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(StationAdapter.ViewHolderProjects holder, int position) {
        final Station station = stations.get(position);

        holder.textViewHead.setText(station.getName());
        holder.textViewDesc.setText(station.getUri());

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=new StationFragment();
                station.setTotalDelay(0);
                ((StationFragment) fragment).setCredentials(station);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.screen_area,fragment);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    /**
     * Inner class necessary for the different cards.
     *
     * @author Pelle Reyniers
     */
    public class ViewHolderProjects extends RecyclerView.ViewHolder{

        public TextView textViewHead;
        public TextView textViewDesc;
        public LinearLayout linearLayout;

        public ViewHolderProjects(View itemView) {
            super(itemView);

            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutTrainCred);
        }
    }

}