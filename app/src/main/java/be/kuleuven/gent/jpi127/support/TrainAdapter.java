package be.kuleuven.gent.jpi127.support;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.w3c.dom.Text;

import be.kuleuven.gent.jpi127.R;
import be.kuleuven.gent.jpi127.model.Train;

public class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolderProjects>{

    private List<Train> trains;
    private Context context;
    Fragment fragment;
    FragmentManager fragmentManager;

    /**
     * The only needed constructor.
     * @param trains
     * @param context
     * @param fragmentManager
     */
    public TrainAdapter(List<Train> trains, Context context, FragmentManager fragmentManager) {
        this.trains = trains;
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
    public ViewHolderProjects onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_trains,parent,false);

        return new ViewHolderProjects(v);
    }

    /**
     * Override method responsible for generating the different elements in the recycler view.
     * Complete with a listener.
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolderProjects holder, int position) {
        final Train train = trains.get(position);

        holder.textViewHead.setText(train.getDestination());
        holder.textViewDesc.setText(train.getCode());
        holder.textViewDelay.setText(String.valueOf(Integer.parseInt(train.getDelay())/60));
        holder.textViewPlatform.setText(train.getPlatform() + "  ");
    }

    @Override
    public int getItemCount() {
        return trains.size();
    }

    /**
     * Inner class necessary for the different cards.
     *
     * @author Pelle Reyniers
     */
    public class ViewHolderProjects extends RecyclerView.ViewHolder{

        public TextView textViewHead;
        public TextView textViewDesc;
        public TextView textViewDelay;
        public TextView textViewPlatform;
        public LinearLayout linearLayout;

        public ViewHolderProjects(View itemView) {
            super(itemView);


            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
            textViewDelay = (TextView)itemView.findViewById(R.id.textViewDelay);
            textViewPlatform = (TextView)itemView.findViewById(R.id.textViewPlatform);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutTrainCred);
        }
    }

}
