package com.mobiliteam.dbrepoexample;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobiliteam.dbrepoexample.model.User;

import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private final List<User> mItems;
    private final UserItemListener listener;

    public UserAdapter(List<User> users, UserItemListener listener){
        this.mItems = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        // Inflate the layout
        View view = inflater.inflate(R.layout.user_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setData(mItems.get(position));
        holder.imageDelete.setOnClickListener(view -> {
            if (listener != null){
                listener.onDelete(position, mItems.get(position));
            }
        });

        holder.itemLayout.setOnClickListener(view -> {
            if (listener != null){
                listener.onListItemClick(position, mItems.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mItems == null){
            return 0;
        }
        return mItems.size();
    }

    public void addItem(User user){
        mItems.add(user);
        notifyItemInserted(mItems.size() -1);
    }

    public void clear(){
        mItems.clear();
        notifyDataSetChanged();
    }

    public void updateItem(User user, int position){
        mItems.set(position, user);
        notifyItemChanged(position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView textRecordId;
        private final TextView textName;
        private final TextView textEmail;
        private final TextView textMobile;
        final ImageView imageDelete;
        final RelativeLayout itemLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRecordId = itemView.findViewById(R.id.record_id);
            textName = itemView.findViewById(R.id.text_name);
            textEmail = itemView.findViewById(R.id.text_email);
            textMobile = itemView.findViewById(R.id.text_mobile);
            imageDelete = itemView.findViewById(R.id.button_delete);
            itemLayout = itemView.findViewById(R.id.item_layout);
        }

        public void setData(User user){
            textRecordId.setText(String.format(Locale.ENGLISH, "%d", user.getId()));
            textName.setText(user.getName());
            textEmail.setText(user.getEmail());
            textMobile.setText(user.getMobile());
        }
    }

    public interface UserItemListener{

        void onListItemClick(int position, User user);

        void onDelete(int position, User user);

    }
}
