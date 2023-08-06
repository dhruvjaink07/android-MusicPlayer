package com.company.dsangeet;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

public class PlaySong extends AppCompatActivity {
    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }
    TextView textView;
    ImageView play,previous,next;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    SeekBar seekBar;
    Thread updateSeek;
    int position;
    Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        play = findViewById(R.id.play);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position = intent.getIntExtra("position",0);
         uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this, uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread(){
            @Override
            public void run(){
                int currentPosition = 0;
                try{
                    while(currentPosition < mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
int currPosition = mediaPlayer.getCurrentPosition();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                // Play the next music track here
                mediaPlayer.reset();
                position++;
                try {
                    mediaPlayer.setDataSource(getApplicationContext(),Uri.parse(songs.get(position).toString()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName();
                textView.setText(textContent);
                seekBar.setProgress(mediaPlayer.getCurrentPosition());
            }
        });
        updateSeek.start();
        play.setOnClickListener(v->{
            if(mediaPlayer.isPlaying()){
                play.setImageResource(R.drawable.play);
                mediaPlayer.pause();
            }
            else{
                play.setImageResource(R.drawable.pause);
                mediaPlayer.start();
            }
        });

        previous.setOnClickListener(v->{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(position != 0){
                position = position -1;
            }
            else{
                position = songs.size()-1;
            }
             uri = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            play.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            textContent  = songs.get(position).getName().toString();
            textView.setText(textContent);
            seekBar.setProgress(mediaPlayer.getCurrentPosition());
        });
        next.setOnClickListener(v->{
            mediaPlayer.stop();
            mediaPlayer.release();

            if(position != songs.size()-1){
                position = position +1;
            }
            else{
                position = 0;
            }
            uri = Uri.parse(songs.get(position).toString());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            play.setImageResource(R.drawable.pause);
            seekBar.setMax(mediaPlayer.getDuration());
            textContent = songs.get(position).getName().toString();
            textView.setText(textContent);
            seekBar.setProgress(mediaPlayer.getCurrentPosition());

        });
    }


}

//    int musics = songs.size();
//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//@Override
//public void onCompletion(MediaPlayer mp) {
//
//        mediaPlayer.stop();
//        mediaPlayer.release();
//        if (position != songs.size() - 1) {
//        position ++;
//        } else {
//        position = 0;
//        }
//        uri = Uri.parse(songs.get(position).toString());
//        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
//        mediaPlayer.start();
//        seekBar.setMax(mediaPlayer.getDuration());
//        textContent = songs.get(position).getName().toString();
//        textView.setText(textContent);
//        seekBar.setProgress(mediaPlayer.getCurrentPosition());
//        }
//
//        });