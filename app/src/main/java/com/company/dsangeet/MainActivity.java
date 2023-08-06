package com.company.dsangeet;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    private String [] items;
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Intent main = getIntent();
        listView = findViewById(R.id.listView);
        appExternalStorage();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void appExternalStorage(){
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_MEDIA_AUDIO)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                        displaySongs();
                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }
    public ArrayList<File> fetchSongs(File file){
        ArrayList<File> arrayList = new ArrayList<>();
        File [] songs = file.listFiles();
        if(songs !=null){
            for(File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll(fetchSongs(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mp3") || myFile.getName().endsWith(".aac") || myFile.getName().endsWith(".wav") && !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }

    public void displaySongs(){
        final ArrayList<File> audioSongs = fetchSongs(Environment.getExternalStorageDirectory());
        items = new String[audioSongs.size()];
        for(int i = 0 ; i < audioSongs.size();i++){
            items[i] = audioSongs.get(i).getName().replace(".mp3","");
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1,items);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this,PlaySong.class);
                String currentSong = listView.getItemAtPosition(position).toString();
                intent.putExtra("songList",audioSongs);
                intent.putExtra("currentSong",currentSong);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });
    }
}