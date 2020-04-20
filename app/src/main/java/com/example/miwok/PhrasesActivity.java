package com.example.miwok;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class PhrasesActivity extends AppCompatActivity {
    private MediaPlayer mMediaPlayer;

    //handles audio focus when playing a sound file
    private AudioManager mAudioManager;

    AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        //stop playback and clean resources
                        releaseMediaPlayer();
                    }
                    else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT ||
                            focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                        //pause and reset audio to start at the beginning
                        mMediaPlayer.pause();
                        mMediaPlayer.seekTo(0);
                    } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                        // Your app has been granted audio focus again
                        // Raise volume to normal, restart playback if necessary
                        mMediaPlayer.start();
                    }
                }
            };

    //listener get triggered when the media player has completed playing the audio file
    private MediaPlayer.OnCompletionListener mCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            releaseMediaPlayer();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phrases);

        //create and setup the audio manager to request audio focus
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        //create an array of phrases
        final ArrayList<Word> phrases = new ArrayList<Word>();
        phrases.add(new Word("Good morning","Wasuze otya nno", R.raw.phrases_good_morning));
        phrases.add(new Word("Good evening","Osiibye otya nno",  R.raw.phrases_good_evening));
        phrases.add(new Word("Hi","Ki kati", R.raw.phrases_hi));
        phrases.add(new Word("How are you?","Oli otya", R.raw.phrases_how_are_you));
        phrases.add(new Word("I am OK","Gyendi", R.raw.phrases_am_ok));
        phrases.add(new Word("Have a nice day","Siiba bulungi", R.raw.phrases_nice_day));
        phrases.add(new Word("Good night","Sula bulungi", R.raw.phrases_good_night));
        phrases.add(new Word("Thank you ","Webale", R.raw.phrases_thank_you));
        phrases.add(new Word("Please","Mwattu", R.raw.phrases_please));

        // Create an {@link WordAdapter}, whose data source is a list of {@link Word}s. The
        // adapter knows how to create list items for each item in the list.
        WordAdapter adapter = new WordAdapter(this, phrases, R.color.category_phrases);

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // There should be a {@link ListView} with the view ID called list, which is declared in the
        // word_list.xml layout file.
        ListView listView = (ListView) findViewById(R.id.list);

        // Make the {@link ListView} use the {@link WordAdapter} we created above, so that the
        // {@link ListView} will display list items for each {@link Word} in the list.
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Word word = phrases.get(position);
                //release media player if it currently exists because we are about to play a different sound file
                releaseMediaPlayer();
                mMediaPlayer = MediaPlayer.create(PhrasesActivity.this, word.getmAudioResourceID());
                mMediaPlayer.start();
                //setup a listener on media player so that we can stop and release the media player once the words have finished playing
                mMediaPlayer.setOnCompletionListener(mCompletionListener);
            }
        });
    }
    @Override
    protected void onStop() {

        super.onStop();

        //when the activity is stoped, release the media player resource
        releaseMediaPlayer();
    }
    /**
     * Clean up the media player by releasing its resources.
     */
    private void releaseMediaPlayer() {
        // If the media player is not null, then it may be currently playing a sound.
        if (mMediaPlayer != null) {
            // Regardless of the current state of the media player, release its resources
            // because we no longer need it.
            mMediaPlayer.release();

            // Set the media player back to null. For our code, we've decided that
            // setting the media player to null is an easy way to tell that the media player
            // is not configured to play an audio file at the moment.
            mMediaPlayer = null;

            //release audio focus when nolonger needed
            //Regardless of whether or not we were granted audio focus, abandon it. this also
            //unregisters the AudioFocusChangeListener so we don't get any call backs
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
        }
    }
}
