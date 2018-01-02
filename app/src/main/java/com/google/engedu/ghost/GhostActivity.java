/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();
    private boolean firstTurn = false;
    private static final String TAG = "StateChange";
    private static final String WHO_FIRST = "whoGoesFirst";
    private static final String CURRENT_WORD = "currentWord";
    private static final String CURRENT_STATUS = "currentStatus";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast.makeText(this, "Could not load dictionary.", Toast.LENGTH_LONG).show();
        }
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        firstTurn = userTurn;
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    public void onChallenge(View view) {
        TextView text = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);
        String word = text.getText().toString();

        if (dictionary.isWord(word)) {
            label.setText("You won!");
        } else {
            //String possibleWord = dictionary.getAnyWordStartingWith(word);
            String possibleWord = dictionary.getGoodWordStartingWith(word, firstTurn);
            if (possibleWord != null) {
                label.setText("Computer won! You missed " + possibleWord + ".");
            } else {
                label.setText("You won!");
            }
        }
    }

    private void computerTurn() {
        TextView label = (TextView) findViewById(R.id.gameStatus);
        // Do computer turn stuff then make it the user's turn again
        TextView text = (TextView) findViewById(R.id.ghostText);
        String word = text.getText().toString();
        if (dictionary.isWord(word)) {
            label.setText("Computer won!");
        } else if (dictionary.getGoodWordStartingWith(word, firstTurn) == null) {
            label.setText("Your challenge accepted! Computer won!");
        } else {
            String longerWord = dictionary.getGoodWordStartingWith(word, firstTurn);
            char nextLetter = longerWord.substring(word.length()).charAt(0);
            text.setText(word + String.valueOf(nextLetter));
            userTurn = true;
            label.setText(USER_TURN);
        }
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode >= 29 && keyCode <= 54) {
            TextView text = (TextView) findViewById(R.id.ghostText);
            //TextView label = (TextView) findViewById(R.id.gameStatus);
            String word = text.getText() + String.valueOf(event.getDisplayLabel()).toLowerCase();

            text.setText(word);
            /*if (dictionary.isWord(word)) {
                label.setText("Completed word!");
            } else {
                label.setText("Keep going.");
            }*/
            computerTurn();
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        TextView text = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);

        outState.putBoolean(WHO_FIRST, firstTurn);
        outState.putString(CURRENT_WORD, text.getText().toString());
        outState.putString(CURRENT_STATUS, label.getText().toString());

        Log.i(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(TAG, "onRestoreInstanceState");

        TextView text = (TextView) findViewById(R.id.ghostText);
        TextView label = (TextView) findViewById(R.id.gameStatus);

        firstTurn = savedInstanceState.getBoolean(WHO_FIRST);
        text.setText(savedInstanceState.getString(CURRENT_WORD));
        label.setText(savedInstanceState.getString(CURRENT_STATUS));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }
}
