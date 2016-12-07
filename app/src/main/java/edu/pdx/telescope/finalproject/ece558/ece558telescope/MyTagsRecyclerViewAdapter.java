package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.pdx.telescope.finalproject.ece558.ece558telescope.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link MyTagsFragment.OnMyTagListInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyTagsRecyclerViewAdapter extends RecyclerView.Adapter<MyTagsRecyclerViewAdapter.ViewHolder> {


    //Attributes of MyTagsRecyclerViewAdapter
    private final ArrayList<BLETag> mValues;
    private final MyTagsFragment.OnMyTagListInteractionListener mListener;
    private int selectedPos = -1; //to keep track of selection

    /***
     * Parametrized constructor
     * @param items Dataset for adapter
     * @param listener Listener to interact with
     */
    public MyTagsRecyclerViewAdapter(ArrayList<BLETag> items, MyTagsFragment.OnMyTagListInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_holder_tag_info, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.itemView.setSelected(selectedPos == position);

        holder.mItem = mValues.get(position);
        holder.mDeviceName.setText(mValues.get(position).getmDeviceName());
        holder.mMACAddress.setText(mValues.get(position).getmMACAddress());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //modifying selected position
                notifyItemChanged(selectedPos);
                selectedPos = position;
                notifyItemChanged(selectedPos);

                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListItemSelected(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mDeviceName;
        public final TextView mMACAddress;
        public BLETag mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mDeviceName = (TextView) view.findViewById(R.id.tag_name);
            mMACAddress = (TextView) view.findViewById(R.id.tag_mac_address);
        }

    }
}
