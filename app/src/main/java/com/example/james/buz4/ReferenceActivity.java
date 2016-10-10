package com.example.james.buz4;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *This class represent activity to show open source licensing and description of developer.
 *
 * Developed by James Joung
 * Version Updated: 3 Oct 2016
 */

public class ReferenceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reference);
        String source = getReferencings();

        TextView reference = (TextView)findViewById(R.id.references);
        reference.setMovementMethod(new ScrollingMovementMethod());
        reference.setText(source);
    }

    /**
     * Read text file that contain open source licensing description
     * and outputs onto the TextView in Reference XML
     * @return String value of open source licensing document
     */
    public String getReferencings(){
        String sources = "";
        try {
            InputStream iStream = getResources().getAssets().open("open_source.txt");
            InputStreamReader iStreamReader = new InputStreamReader(iStream);
            BufferedReader buffReader = new BufferedReader(iStreamReader);
            String line;
            while ((line = buffReader.readLine()) != null)
                sources += line+"\n";
        }catch(Exception e){sources += "Failed to load open source description";}

        return sources;
    }

}
