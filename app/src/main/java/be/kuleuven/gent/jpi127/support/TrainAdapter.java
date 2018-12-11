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

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Bundle bundle = new Bundle();
                bundle.putParcelable("train", train);
                Intent intent = new Intent(context, ProjectActivity.class);
                intent.putExtra("project", bundle);
                context.startActivity(intent);
                */
            }
        });
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
        public LinearLayout linearLayout;

        public ViewHolderProjects(View itemView) {
            super(itemView);

            //todo: add delay and platform fields.

            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.linearLayoutTrainCred);
        }
    }

}
