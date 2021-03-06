package com.example.tsparking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

import com.example.tsparking.R;
import com.example.tsparking.classes.MainActivity;
import com.example.tsparking.classes.Parking;
import com.example.tsparking.classes.Slot;
import com.example.tsparking.classes.SlotAdapter;
import com.example.tsparking.classes.listSlot;
import com.example.tsparking.classes.ObserverToReport;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchingSlot#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchingSlot extends Fragment {
    RecyclerView recyclerViewSlot;
    SlotAdapter adapterSlot;

    private static final String TAG = "MyActivity";

    private ArrayList<String> areaList =new ArrayList<String>();

    private Spinner spinner;
    private ObserverToReport newR2;
    private static String areaSelected = null;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchingSlot() {
        // Required empty public constructor
    }
    public SearchingSlot(ObserverToReport newR) {
        newR2=newR;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchingSlot.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchingSlot newInstance(String param1, String param2) {
        SearchingSlot fragment = new SearchingSlot();
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
                             Bundle savedInstanceState)  {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_searching_slot, container, false);
        MainActivity mainActivity = (MainActivity) getActivity();
        Button BackB = (Button) view.findViewById(R.id.BackB);
        BackB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.BackToProfile();
            }
        });

        Query query2= FirebaseDatabase.getInstance().getReference("Parking").orderByChild("area");
        query2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot adSnapshot: snapshot.getChildren()) {
                    if(!(areaList.contains(adSnapshot.getValue(Parking.class).getArea()))) {
                        areaList.add(adSnapshot.getValue(Parking.class).getArea());
                    }
                }
                spinner = view.findViewById(R.id.spinner);
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(mainActivity,android.R.layout.simple_spinner_item, areaList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(dataAdapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        areaSelected = parent.getItemAtPosition(position).toString();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Button searchingslotbp = (Button) view.findViewById(R.id.SearchingSlotBP);
        searchingslotbp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) getActivity();
                CheckBox IsDisableCB = view.findViewById(R.id.IsDisableCB);
                Boolean isdisable = false;
                if(IsDisableCB.isChecked())
                    isdisable = true;

                CheckBox IsFreeCB = view.findViewById(R.id.IsFreeCB);
                Boolean isFreeCB = false;
                if(IsFreeCB.isChecked())
                    isFreeCB = true;

                CheckBox IsIndoorCB = view.findViewById(R.id.IsIndoorCB);
                Boolean isIndoorCB = false;
                if(IsIndoorCB.isChecked())
                    isIndoorCB = true;


                mainActivity.getMySingeltonMSlot().clear();

                Query query= FirebaseDatabase.getInstance().getReference("Slot").orderByChild("ParkingNum");

                Boolean finalIsdisable = isdisable;
                Boolean finalIsFreeCB = isFreeCB;
                Boolean finalIsIndoorCB = isIndoorCB;
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot adSnapshot: dataSnapshot.getChildren()) {
                            double price= mainActivity.getParkingByNum(adSnapshot.getValue(Slot.class).getParkingNum()).getPrice();
                            if(finalIsFreeCB==true) {
                                if (adSnapshot.getValue(Slot.class).isDisable() == finalIsdisable &&
                                        adSnapshot.getValue(Slot.class).isIndoor() == finalIsIndoorCB && price == 0.0 && mainActivity.getParkingByNum(adSnapshot.getValue(Slot.class).getParkingNum()).getArea().equals(areaSelected)
                                        && adSnapshot.getValue(Slot.class).isFree() == true)
                                    mainActivity.getMySingeltonMSlot().addSlot(adSnapshot.getValue(Slot.class));
                            }
                            else
                            if(adSnapshot.getValue(Slot.class).isDisable() == finalIsdisable &&
                                    adSnapshot.getValue(Slot.class).isIndoor()== finalIsIndoorCB &&
                                    mainActivity.getParkingByNum(adSnapshot.getValue(Slot.class).getParkingNum()).getArea().equals(areaSelected)&&
                                    adSnapshot.getValue(Slot.class).isFree()== true)
                                    mainActivity.getMySingeltonMSlot().addSlot(adSnapshot.getValue(Slot.class));
                        }
                        recyclerViewSlot=(RecyclerView)view.findViewById(R.id.recycleViewSlot);
                        recyclerViewSlot.setHasFixedSize(true);
                        recyclerViewSlot.setLayoutManager(new LinearLayoutManager(view.getContext()));
                        listSlot listslot=mainActivity.getMySingeltonMSlot();
                        adapterSlot=new SlotAdapter(view.getContext(),listslot.getList(),newR2);
                        recyclerViewSlot.setAdapter(adapterSlot);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });


        return view;
    }
}