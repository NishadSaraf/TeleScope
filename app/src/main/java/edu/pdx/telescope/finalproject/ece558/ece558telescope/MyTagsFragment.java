package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment representing a my_tags_list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnMyTagListInteractionListener}
 * interface.
 */
public class MyTagsFragment extends Fragment {

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MyTagsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_tags, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {

            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setAdapter(((TelescopeActivity)getActivity()).getmBLETagsAdapter());
        }
        return view;
    }


    public interface OnMyTagListInteractionListener {
        void onListItemSelected(BLETag item);
    }
}
