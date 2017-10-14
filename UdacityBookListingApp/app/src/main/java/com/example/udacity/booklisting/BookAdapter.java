package com.example.udacity.booklisting;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


class BookAdapter extends RecyclerView.Adapter<BookAdapter.MyViewHolder>{


    private static final String TAG = BookAdapter.class.getName();

    private ArrayList<Book> mBookList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private ImageView bookCover;
        private TextView title;
        private TextView author;
        private View currentView;

        public MyViewHolder(View view){
            super(view);
            currentView = view;
            title = (TextView) view.findViewById(R.id.tv_book_title);
            author = (TextView) view.findViewById(R.id.tv_book_author);
            bookCover = (ImageView) view.findViewById(R.id.book_cover);
        }
    }

    public BookAdapter(Context context, List bookList){
        mContext = context;
        if(bookList != null){
            mBookList = (ArrayList<Book>) bookList;
        }else{
            mBookList = new ArrayList<Book>();
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,
                parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position){

        final Book data = (Book) mBookList.get(position);

        String thumbnailId = data.getThumbnail();
        if(thumbnailId != null && !thumbnailId.isEmpty()){
            new LoadThumbnailTask(holder.bookCover).execute(thumbnailId);
        }else{
            holder.bookCover.setImageResource(R.drawable.no_image);
        }

        holder.title.setText(data.getTitle());
        holder.author.setText(data.getAuthor());
        holder.currentView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                String url = data.getUrl();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount(){
        return mBookList.size();
    }

    private class LoadThumbnailTask extends AsyncTask<String, Void, Bitmap>{
        ImageView mThumbnail;

        public LoadThumbnailTask(ImageView bmImage){
            this.mThumbnail = bmImage;
        }

        protected Bitmap doInBackground(String... urls){
            String mThumbnailLink = urls[0];
            Bitmap bitmap = null;
            try{
                InputStream in = new URL(mThumbnailLink).openStream();
                bitmap = BitmapFactory.decodeStream(in);
            }catch(Exception e){
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap result){
            mThumbnail.setImageBitmap(result);
        }
    }

    public void clear(){
        if(mBookList != null){
            mBookList.clear();
            notifyDataSetChanged();
        }
    }

    public void addAll(List<Book> bookList){
        if(mBookList == null){
            mBookList = new ArrayList<Book>();
        }
        mBookList.addAll(bookList);
        notifyDataSetChanged();
    }
}
