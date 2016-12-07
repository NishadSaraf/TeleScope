package edu.pdx.telescope.finalproject.ece558.ece558telescope;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

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
    private boolean isScanning=false;

    private Button mScanButton;
    private Button mAddTagButton;
    private ListView mAvailableTags;
    private EditText mMACAddressText;
    private EditText mTagNameText;
    private BLETag mSelectedTag;

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
        mScanButton = (Button) inflatedView.findViewById(R.id.scan_button);
        mAddTagButton = (Button) inflatedView.findViewById(R.id.add_tag_button);
        mAvailableTags = (ListView) inflatedView.findViewById(R.id.available_tag_list);
        mMACAddressText= (EditText)  inflatedView.findViewById(R.id.mac_address_text);
        mTagNameText= (EditText)  inflatedView.findViewById(R.id.tag_name_text);

        //setting adapter for available tag list view
        mAvailableTags.setAdapter(((TelescopeActivity)getActivity()).getmScannedListAdapter());

        //setting onitemclick listener for tag list
        mAvailableTags.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                mSelectedTag= (BLETag)parent.getItemAtPosition(position);
                mMACAddressText.setText(mSelectedTag.getmMACAddress());
                mTagNameText.setText(mSelectedTag.getmDeviceName());
            }
        });

        //Setting listener for scan button
        mScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button clickedButton= (Button) view;
                if(isScanning) {
                    ((TelescopeActivity) getActivity()).stopScan();
                    isScanning=!isScanning;
                    clickedButton.setText(getString(R.string.start_scan_button));

                }
                else {
                    ((TelescopeActivity) getActivity()).startScan();
                    isScanning=!isScanning;
                    clickedButton.setText(getString(R.string.stop_scan_button));
                }

            }
        });

        //Setting onclicklistener for mAddTagButton
        mAddTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Adds the tag only if there is any
                if (mSelectedTag != null) {
                    //Fetching tag name given by user
                    String userSelectedTagName = mTagNameText.getText().toString();

                    //TODO: remove dummy code to add category
                    mSelectedTag.setmGroup("General");
                    //Takes the user given name only if it is valid
                    if (!userSelectedTagName.isEmpty()) {
                        mSelectedTag.setmDeviceName(userSelectedTagName);
                    }

                    //Invoking listener method in parent activity
                    //to signaling that a tag should be added
                    onAddTagListener listener = (onAddTagListener) getActivity();
                    listener.onTagAdded(mSelectedTag);

                    //Clearing textboxes
                    mMACAddressText.setText("");
                    mTagNameText.setText("");

                    //Clearing selected tag reference
                    mSelectedTag = null;
                }
            }
        });

        return inflatedView;
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
        void onTagAdded(BLETag selectedtag);
    }


}
