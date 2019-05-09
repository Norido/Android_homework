package com.example.rebo;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

// Tạo adapter gắn các sách vào list view
public class Adapter_Book extends RecyclerView.Adapter<Adapter_Book.ViewHolder> {

    private ArrayList<Book> data;

    public Adapter_Book(ArrayList<Book> data) {
        this.data = data;
    }
    @Override
    public Adapter_Book.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_item_moi_nhat,viewGroup,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final Adapter_Book.ViewHolder viewHolder, int position) {
        Book book = data.get(position);
        viewHolder.title.setText(book.getTenSach());
        viewHolder.author.setText(book.getTacGia());
        viewHolder.img.setImageResource(book.getBiaSach());
        // set event listen cho no khi duoc goi
        viewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {

                Intent intent = new Intent(view.getContext(),ActivityDetail.class);
                Book b = data.get(position);
                intent.putExtra("img",b.getBiaSach());
                intent.putExtra("title",b.getBiaSach());
                intent.putExtra("author",b.getBiaSach());
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView title,author;
        public ImageView img;
        private ItemClickListener itemClickListener; //Khai báo interface
        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById((R.id.item_title));
            author = itemView.findViewById((R.id.item_author));
            img = itemView.findViewById((R.id.item_book));
            //listen click
            itemView.setOnClickListener(this); //  set sự kiên onClick cho View
        }
        // Gọi interface , false là vì đây là onClick, true là longclick
        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition(),false);
        }
        //Tạo setter cho biến itemClickListenenr
        public void setItemClickListener(ItemClickListener itemClickListener)
        {
            this.itemClickListener = itemClickListener;
        }
    }
}