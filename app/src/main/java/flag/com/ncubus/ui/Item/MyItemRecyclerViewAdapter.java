package flag.com.ncubus.ui.Item;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import flag.com.ncubus.databinding.FragmentItemBinding;

import java.util.ArrayList;
import java.util.List;

public class MyItemRecyclerViewAdapter extends RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<String> src_times;
    private final ArrayList<String> dest_times;
    private final ArrayList<String> train_nos;
    private final ArrayList<String> train_types;
    public final ArrayList<Integer> costs;
    private final ArrayList<String> spend_times;
    public MyItemRecyclerViewAdapter(ArrayList<String> src_times, ArrayList<String> dest_times,
                                     ArrayList<String> train_nos, ArrayList<String> train_types,ArrayList<Integer> costs,
                                     ArrayList<String> spend_times) {
        this.src_times = src_times;
        this.dest_times = dest_times;
        this.train_nos = train_nos;
        this.train_types = train_types;
        this.costs = costs;
        this.spend_times = spend_times;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ViewHolder(FragmentItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.trainTypeView.setText(train_types.get(position));
        holder.trainNumberView.setText(train_nos.get(position));
        holder.srcTimeView.setText(src_times.get(position));
        holder.destTimeView.setText(dest_times.get(position));
        holder.costView.setText("$"+costs.get(position).toString());
        holder.timeView.setText(spend_times.get(position));
    }

    @Override
    public int getItemCount() {
        return src_times.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView trainTypeView;
        public final TextView trainNumberView;
        public final TextView srcTimeView;
        public final TextView destTimeView;
        public final TextView costView;
        public final TextView timeView;

        public ViewHolder(FragmentItemBinding binding) {
            super(binding.getRoot());
            trainTypeView = binding.trainType;
            trainNumberView = binding.trainNumber;
            srcTimeView = binding.srcTime;
            destTimeView = binding.destTime;
            costView = binding.trainCost;
            timeView = binding.spendingTime;
        }

        @Override
        public String toString() {
            return super.toString() + "extra_messages";
        }
    }
}