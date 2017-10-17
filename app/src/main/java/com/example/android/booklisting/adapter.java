package com.example.android.booklisting;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Toshiba on 21/08/17.
 */

public class adapter extends ArrayAdapter<Book> {

    public adapter(Context context, ArrayList<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list, parent, false);
        }

        Book book = getItem(position);

        ImageView bookImage = listItemView.findViewById(R.id.image);

        if (book.getImage() == null) {
            bookImage.setImageResource(R.drawable.image_placeholder_dark1);
        } else {
            bookImage.setImageBitmap(book.getImage());
        }

        TextView titleText = listItemView.findViewById(R.id.book_title);
        titleText.setText(book.getTitle());

        TextView authorsText = listItemView.findViewById(R.id.author_name);
        authorsText.setText(book.getAuthor());

        return listItemView;

    }

}
