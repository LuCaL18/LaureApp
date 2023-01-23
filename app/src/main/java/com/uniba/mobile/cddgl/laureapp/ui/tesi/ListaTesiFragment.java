package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;

public class ListaTesiFragment extends Fragment {
    private ListView listView;
    private ListAdapterTesi adapter;
    private List<Tesi> dataList;
    private DatabaseReference mDatabase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_fragment, container, false);
        listView = (ListView) view.findViewById(R.id.list_view);
        dataList = new ArrayList<>();
        adapter = new MyListAdapter(getActivity(), dataList);
        listView.setAdapter(adapter);
        mDatabase = FirebaseDatabase.getInstance().getReference("data");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    MyData data = postSnapshot.getValue(MyData.class);
                    dataList.add(data);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return view;
    }
}
