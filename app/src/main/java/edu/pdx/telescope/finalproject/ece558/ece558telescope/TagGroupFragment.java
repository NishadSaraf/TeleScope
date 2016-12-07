package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnTagGroupInteractionListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class TagGroupFragment extends Fragment {

    private OnTagGroupInteractionListener mListener;
    private ListView mGroupsListView;

    public TagGroupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView= inflater.inflate(R.layout.fragment_tag_group, container, false);
        mGroupsListView = (ListView) inflatedView.findViewById(R.id.available_group_list);
        mGroupsListView.setAdapter(((TelescopeActivity)getActivity()).getmTagGroupAdapter());

        return inflatedView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTagGroupInteractionListener) {
            mListener = (OnTagGroupInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnTagGroupInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTagGroupInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
