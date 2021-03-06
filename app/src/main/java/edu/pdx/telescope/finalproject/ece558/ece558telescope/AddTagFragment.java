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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link onAddTagListener} interface
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class AddTagFragment extends Fragment {

    private boolean isScanning=false;

    private Button mScanButton;
    private Button mAddTagButton;
    private ListView mAvailableTags;
    private EditText mMACAddressText;
    private EditText mTagNameText;
    private BLETag mSelectedTag;
    private Spinner mGroupSpinner;
    private onAddTagListener mListener;

    public AddTagFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        mGroupSpinner= (Spinner) inflatedView.findViewById(R.id.spinner);

        //Setting adapter for spinner
        mGroupSpinner.setAdapter(((TelescopeActivity)getActivity()).getmTagGroupAdapter());

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
                if(!isScanning) {
                    if(((TelescopeActivity) getActivity()).startScan()) {
                        isScanning=!isScanning;
                        clickedButton.setText(getString(R.string.stop_scan_button));
                    }

                }
                else {
                    ((TelescopeActivity) getActivity()).stopScan();
                    isScanning = !isScanning;
                    clickedButton.setText(getString(R.string.start_scan_button));

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

                    mSelectedTag.setmGroup("General");
                    //Takes the user given name only if it is valid
                    if (!userSelectedTagName.isEmpty()) {
                        mSelectedTag.setmDeviceName(userSelectedTagName);
                    }

                    //Invoking listener method in parent activity
                    //to signal that a tag should be added
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

        void onTagAdded(BLETag selectedtag);
    }

}
