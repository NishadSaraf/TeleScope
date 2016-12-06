package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link onAddTagListener} interface
 * to handle interaction events.
 * Use the {@link AddTagFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddTagFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private ToggleButton mToggleButton;
    private ListView mAvailableTags;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private onAddTagListener mListener;

    public AddTagFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddTagFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTagFragment newInstance(String param1, String param2) {
        AddTagFragment fragment = new AddTagFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflatedView= inflater.inflate(R.layout.fragment_add_tag, container, false);

        //Instantiating view elements
        mToggleButton = (ToggleButton) inflatedView.findViewById(R.id.toggleButton);
        mAvailableTags = (ListView) inflatedView.findViewById(R.id.available_tag_list);

        //setting adapter for available tag list view
        ArrayList<BLETag> filteredtags= new ArrayList<BLETag>();
        ArrayList<BLETag> alltagslist= ((TelescopeActivity)getActivity()).getmBLETags();

        for (int i=0; i< alltagslist.size() ; i++)
        {
            if( !alltagslist.get(i).isSavedTag()){
               filteredtags.add(alltagslist.get(i));
            }
        }

        mAvailableTags.setAdapter(new AvailableTagListAdapter(getActivity(),filteredtags));

        //Setting listener for toggle button
        mToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    ((TelescopeActivity)getActivity()).startScan();
                else
                    ((TelescopeActivity)getActivity()).stopScan();
            }
        });



        return inflatedView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTagAdded(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onAddTagListener) {
            mListener = (onAddTagListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement onAddTagListener");
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
    public interface onAddTagListener {
        // TODO: Update argument type and name
        void onTagAdded(Uri uri);
    }

    public class AvailableTagListAdapter extends ArrayAdapter<BLETag>{
        public AvailableTagListAdapter(Context context, ArrayList<BLETag> availabletags) {
            super(context,R.layout.view_holder_tag_info, availabletags);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater= LayoutInflater.from(getContext());
            View inflatedView= inflater.inflate(R.layout.view_available_tag,parent,false);

            //Extracting tag reference
            BLETag availabletag= getItem(position);

            TextView tagname= (TextView) inflatedView.findViewById(R.id.available_tag_name);
            TextView tagmacaddress= (TextView) inflatedView.findViewById(R.id.available_tag_mac_address);

            tagname.setText(availabletag.getmDeviceName());
            tagmacaddress.setText(availabletag.getmMACAddress());

            return inflatedView;
        }
    }
}
