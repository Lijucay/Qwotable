package com.lijukay.quotesAltDesign.fragments;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.provider.MediaStore;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.lijukay.quotesAltDesign.AddDialogFragment;
import com.lijukay.quotesAltDesign.Database.MyDatabaseHelper;
import com.lijukay.quotesAltDesign.R;
import com.lijukay.quotesAltDesign.adapter.OwnQwotableAdapter;
import com.lijukay.quotesAltDesign.interfaces.RecyclerViewInterface;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AddOwnQuotes extends Fragment implements RecyclerViewInterface {

    RecyclerView recyclerView;
    View v;
    ExtendedFloatingActionButton efab;
    MyDatabaseHelper db;
    ArrayList<String> id, qwotable, author, source;

    OwnQwotableAdapter ownQwotableAdapter;
    boolean tablet;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_add_own_quotes, container, false);

        recyclerView = v.findViewById(R.id.add_own_quotes_rv);

        efab = v.findViewById(R.id.button_add);

        db = new MyDatabaseHelper(requireContext());

        id = new ArrayList<>();
        qwotable = new ArrayList<>();
        author = new ArrayList<>();
        source = new ArrayList<>();

        storeDataInArrays();

        ownQwotableAdapter = new OwnQwotableAdapter(requireActivity(), requireContext(), id, qwotable, author, source, this);
        recyclerView.setAdapter(ownQwotableAdapter);

        tablet = getResources().getBoolean(R.bool.isTablet);
        if (tablet){
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(requireContext().getApplicationContext()));
        }

        recyclerView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (oldScrollY < scrollY){
                efab.shrink();
            } else {
                efab.extend();
            }
        });

        efab.setOnClickListener(v -> {
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered);

            builder.setTitle(getString(R.string.add_own_qwotable));
            View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.fragment_dialog, (ViewGroup) getView(), false);
            TextInputLayout qwotable_input = viewInflated.findViewById(R.id.qwotable_tf);
            TextInputLayout author_input = viewInflated.findViewById(R.id.author_tf);
            TextInputLayout foundIn_input = viewInflated.findViewById(R.id.found_in_tf);
            builder.setView(viewInflated);

            builder.setPositiveButton(getString(R.string.add_button), (dialog, which) -> {
                MyDatabaseHelper mydb = new MyDatabaseHelper(requireContext());
                if (!Objects.requireNonNull(qwotable_input.getEditText()).getText().toString().trim().equals("") && !Objects.requireNonNull(author_input.getEditText()).getText().toString().trim().equals("")){
                    mydb.addQwotable(Objects.requireNonNull(qwotable_input.getEditText()).getText().toString().trim(),
                            Objects.requireNonNull(author_input.getEditText()).getText().toString().trim(),
                            Objects.requireNonNull(foundIn_input.getEditText()).getText().toString().trim()); //Add data to Database
                    refreshLayout();
                    dialog.dismiss();
                } else {
                    Toast.makeText(requireContext(), getString(R.string.not_allowed), Toast.LENGTH_SHORT).show();
                }

            });
            builder.setNeutralButton(R.string.neutral_button_text_cancel, (dialog, which) -> dialog.cancel());

            builder.show();
        });


            ViewCompat.setOnApplyWindowInsetsListener(v.findViewById(R.id.no_data), (v, windowInsets) -> {
                Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
                // Apply the insets as a margin to the view. Here the system is setting
                // only the bottom, left, and right dimensions, but apply whichever insets are
                // appropriate to your layout. You can also update the view padding
                // if that's more appropriate.
                ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
                mlp.bottomMargin = insets.bottom;
                v.setLayoutParams(mlp);

                // Return CONSUMED if you don't want want the window insets to keep being
                // passed down to descendant views.
                return WindowInsetsCompat.CONSUMED;
            });


        ViewCompat.setOnApplyWindowInsetsListener(efab, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Apply the insets as a margin to the view. Here the system is setting
            // only the bottom, left, and right dimensions, but apply whichever insets are
            // appropriate to your layout. You can also update the view padding
            // if that's more appropriate.
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) efab.getLayoutParams();
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            mlp.bottomMargin = insets.bottom + lp.bottomMargin;
            v.setLayoutParams(mlp);

            // Return CONSUMED if you don't want want the window insets to keep being
            // passed down to descendant views.
            return WindowInsetsCompat.CONSUMED;
        });

        ViewCompat.setOnApplyWindowInsetsListener(recyclerView, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());

            recyclerView.setPadding(0,0,0,insets.bottom);

            return WindowInsetsCompat.CONSUMED;
        });

        return v;
    }

    private void storeDataInArrays() {

        Cursor cursor = db.readAllData();
        if (cursor.getCount() == 0){
            v.findViewById(R.id.no_data).setVisibility(View.VISIBLE);
        } else {
            while (cursor.moveToNext()){
                id.add(cursor.getString(0));
                qwotable.add(cursor.getString(1));
                author.add(cursor.getString(2));
                source.add(cursor.getString(3));
            }
            v.findViewById(R.id.no_data).setVisibility(View.GONE);
        }

    }

    public void refreshLayout(){
        qwotable.clear();
        author.clear();
        source.clear();
        id.clear();
        storeDataInArrays();
        ownQwotableAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(int position, String type) {
        if (type.equals("updateOrDelete")){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered);
            builder.setTitle(getString(R.string.update_delete_dialog));

            View viewInflated = LayoutInflater.from(requireContext()).inflate(R.layout.fragment_dialog, null);
            TextInputLayout qwotable_input = viewInflated.findViewById(R.id.qwotable_tf);
            TextInputLayout author_input = viewInflated.findViewById(R.id.author_tf);
            TextInputLayout foundIn_input = viewInflated.findViewById(R.id.found_in_tf);

            Objects.requireNonNull(qwotable_input.getEditText()).setText(qwotable.get(position));
            Objects.requireNonNull(author_input.getEditText()).setText(author.get(position));
            Objects.requireNonNull(foundIn_input.getEditText()).setText(source.get(position));

            builder.setView(viewInflated);

            builder.setPositiveButton(getString(R.string.updater_positive_button), (dialog, which) -> {
                dialog.dismiss();
                MyDatabaseHelper mydb = new MyDatabaseHelper(requireContext());
                mydb.updateData(Objects.requireNonNull(id.get(position)), Objects.requireNonNull(qwotable_input.getEditText()).getText().toString().trim(),
                        Objects.requireNonNull(author_input.getEditText()).getText().toString().trim(),
                        Objects.requireNonNull(foundIn_input.getEditText()).getText().toString().trim()); //Add new data to Database
                refreshLayout();
            });
            builder.setNegativeButton(getString(R.string.delete_button), (dialog, which) -> {

                new MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.ThemeOverlay_Material3_MaterialAlertDialog_Centered)
                        .setTitle(getString(R.string.delete_dialog_title))
                        .setMessage(getString(R.string.delete_dialog_title))
                        .setPositiveButton(getString(R.string.delete_button), (dialog1, which1) -> {
                            MyDatabaseHelper db = new MyDatabaseHelper(requireContext());
                            db.deleteOneRow(id.get(position)); //delete item at id.get(position)
                            refreshLayout(); //call refresh method to show updated data
                            dialog.dismiss();
                            dialog1.dismiss();
                        })
                        .setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog1, which1) -> dialog1.dismiss())
                        .show();
            });

            builder.setNeutralButton(getString(R.string.neutral_button_text_cancel), (dialog, which) -> dialog.dismiss());

            builder.show();
        } else if (type.equals("copy")){
            String quote = qwotable.get(position);
            String authorString = author.get(position);

            copyText(quote + "\n\n~ " + authorString);
        }
    }

    private void copyText(String quote) {
        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Quotes", quote);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(requireContext(), getString(R.string.quote_copied_toast_message), Toast.LENGTH_SHORT).show();
    }


}