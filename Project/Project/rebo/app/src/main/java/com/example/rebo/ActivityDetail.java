package com.example.rebo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.google.firebase.database.ValueEventListener;
import com.ms.square.android.expandabletextview.ExpandableTextView;
import com.r0adkll.slidr.Slidr;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ActivityDetail extends AppCompatActivity {
    private ExpandableTextView expandableTextView;
    private TextView name, author,theloai,nhaxuatban,ngayxuanban;
    private Button read_book,tai_sach;
    //private ImageView img;
    private ImageView img;
    private LottieAnimationView lottieAnimationView;
    //firebase
    private DatabaseReference databaseReference,book,love_book;
    private FirebaseDatabase firebaseDatabase;
    private Book b;
    public SharedPreferences sharedPrefManager;
    public String uid;
    public List<MyLoveBook> LoveB ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_book);
        // slide activity
        Slidr.attach(this);

        // Nhan du lieu
        //xem them
        expandableTextView = (ExpandableTextView) findViewById(R.id.expandable_text_view);
        img = (ImageView) findViewById(R.id.img_detail);
        name = (TextView) findViewById(R.id.name_detail);
        author = (TextView) findViewById(R.id.author_detail);
        theloai = (TextView) findViewById(R.id.the_loai);
        nhaxuatban = (TextView) findViewById(R.id.nha_xuat_ban);
        ngayxuanban = (TextView) findViewById(R.id.ngay_xuat_ban);
        sharedPrefManager = getSharedPreferences("UserInformation", Context.MODE_PRIVATE);
        Intent recv_from_online = getIntent();
        final String tenSach = recv_from_online.getStringExtra("tenSach");
        uid = sharedPrefManager.getString("uid",null);
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        book = databaseReference.child("Books");
        love_book = (DatabaseReference) databaseReference.child("users").child(uid).child("mylovebook").child(tenSach);
        love_book.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()&&dataSnapshot.getValue().equals("1")){
                        lottieAnimationView.playAnimation();
                        lottieAnimationView.setRepeatCount(0);
                    }
                }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
       book.orderByChild("tenSach").equalTo(tenSach).addChildEventListener(new ChildEventListener() {
           @Override
           public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
               b = dataSnapshot.getValue(Book.class);
               name.setText(b.getTenSach());
               author.setText(b.getTacGia());
               Picasso.get().load(b.getBiaSach()).into(img);
               expandableTextView.setText(b.getNoiDung());
               theloai.setText(b.getTheLoai());
               nhaxuatban.setText(b.getNhaXuatBan());
               ngayxuanban.setText(b.getNgayXuatBan());

           }

           @Override
           public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

           }

           @Override
           public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

           }

           @Override
           public void onCancelled(@NonNull DatabaseError databaseError) {

           }
       });

        // LIKE
        lottieAnimationView = (LottieAnimationView) findViewById(R.id.animation_view);
        lottieAnimationView.setOnClickListener(new View.OnClickListener(){
                @Override
            public void onClick(View v) {
                    if(lottieAnimationView.getFrame() == 0) {
                        lottieAnimationView.playAnimation();
                        lottieAnimationView.setRepeatCount(0);
                        love_book.setValue("1");

                    }
                    else{
                        //104 frame
                        lottieAnimationView.pauseAnimation();
                        lottieAnimationView.setFrame(0);
                        love_book.setValue("0");


                    }
            }
        });


        // chuyen sang doc sach
        read_book = (Button) findViewById(R.id.doc_sach);
        read_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ActivityDetail.this, ReadBook.class);
                intent.putExtra("tenSach",name.getText());
                startActivity(intent);
            }
        });

        // tai sach
        tai_sach = (Button) findViewById(R.id.tai_sach);

    }

}