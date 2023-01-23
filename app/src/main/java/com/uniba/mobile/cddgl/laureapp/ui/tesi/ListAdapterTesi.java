package com.uniba.mobile.cddgl.laureapp.ui.tesi;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.uniba.mobile.cddgl.laureapp.R;
import com.uniba.mobile.cddgl.laureapp.data.model.Tesi;

import java.util.ArrayList;
import java.util.List;

public class ListAdapterTesi extends BaseAdapter {

    private Context mContext;
    private List<Tesi> mDataList;
    private DatabaseReference mDatabaseReference;

    public ListAdapterTesi(Context context, DatabaseReference ref) {
        mContext = context;
        mDatabaseReference = ref;
        mDataList = new ArrayList<>();
        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mDataList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Tesi tesi = postSnapshot.getValue(Tesi.class);
                    mDataList.add(tesi);
                }
                notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("FirebaseListAdapter", "The read failed: " + databaseError.getMessage());
            }
        });
    }

    @Override
    public int getCount() {
        return mDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.lista_tesi, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.textView1 = (TextView) convertView.findViewById(R.id.nometesi);
            viewHolder.textView2 = (TextView) convertView.findViewById(R.id.nomerelatore);
            viewHolder.imageButton1 = (ImageButton) convertView.findViewById(R.id.visualizzaTesi);
            viewHolder.imageButton2 = (ImageButton) convertView.findViewById(R.id.addTesi);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Tesi tesi = mDataList.get(position);
        viewHolder.textView1.setText(tesi.getNomeTesi());
        viewHolder.textView2.setText(tesi.getRelatore());
        return convertView;
    }

    private class ViewHolder {
        TextView textView1;
        TextView textView2;
        ImageButton imageButton1;
        ImageButton imageButton2;
    }
}

