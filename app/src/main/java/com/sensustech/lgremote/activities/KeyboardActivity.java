package com.sensustech.lgremote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;

import com.connectsdk.device.ConnectableDevice;
import com.sensustech.lgremote.R;
import com.sensustech.lgremote.SingletonTV;

public class KeyboardActivity extends AppCompatActivity {
    ConnectableDevice mTV;
    EditText text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);
        SingletonTV tv = com.sensustech.lgremote.SingletonTV.getInstance();
        mTV = tv.getTV();
        if (mTV != null) {
            mTV.getTextInputControl().subscribeTextInputStatus(null);
        }
        text = findViewById(R.id.editInput);
        Button[] letterButtons = {
        findViewById(R.id.keyA),
        findViewById(R.id.keyB),
        findViewById(R.id.keyC),
        findViewById(R.id.keyD),
        findViewById(R.id.keyE),
        findViewById(R.id.keyF),
        findViewById(R.id.keyG),
        findViewById(R.id.keyH),
        findViewById(R.id.keyI),
        findViewById(R.id.keyJ),
        findViewById(R.id.keyK),
        findViewById(R.id.keyL),
        findViewById(R.id.keyM),
        findViewById(R.id.keyN),
        findViewById(R.id.keyO),
        findViewById(R.id.keyP),
        findViewById(R.id.keyQ),
        findViewById(R.id.keyR),
        findViewById(R.id.keyS),
        findViewById(R.id.keyT),
        findViewById(R.id.keyU),
        findViewById(R.id.keyV),
        findViewById(R.id.keyW),
        findViewById(R.id.keyX),
        findViewById(R.id.keyY),
        findViewById(R.id.keyZ)
};

for (Button button : letterButtons) {
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button clickedButton = (Button) v;
            text.append(clickedButton.getText().toString());
        }
    });
}

findViewById(R.id.keySpace).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        text.append(" ");
    }
});

findViewById(R.id.keyDelete).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        int position = text.getSelectionStart();
        if (position > 0) {
            text.getText().delete(position - 1, position);
        }
    }
});

findViewById(R.id.keyEnter).setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (mTV != null) {
            mTV.getTextInputControl().sendEnter();
            finish();
        }
    }
});
        text.addTextChangedListener(new TextWatcher() {
            String lastString = "";

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 0) {
                    editable.append("\u200B");
                }
                lastString = editable.toString().replace("\u200B", "");
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int beforeLength, int charsChanged) {
                if (mTV != null) {
                    if (mTV.getTextInputControl() == null) {
                        System.err.println("Keyboard Control is null");
                        return;
                    }
//                    System.out.println("[DEBUG] appside: " + s);
//                    System.out.println("[DEBUG] len: " + s.length());
//                    System.out.println("[DEBUG] lastString: " + lastString);
                    if (s.length() == 0) {
                        // all characters including the sentinel were deleted
                        mTV.getTextInputControl().sendDelete();
                    }
                    else {
                        String newString = s.toString().replace("\u200B", ""); // nasty hack
//                        System.out.println("[DEBUG] newString: " + newString);
                        int matching = getMatchingCharacterLength(lastString, newString);

                        if (matching < lastString.length()) {
                      for (int i = 0; i < lastString.length() - matching; i++) {
                         mTV.getTextInputControl().sendDelete();
                }
         }

                     if (matching < newString.length()) {
                            mTV.getTextInputControl().sendText(newString.substring(matching));
                        }
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Device is not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
        text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
                if (mTV != null) {
                    mTV.getTextInputControl().sendEnter();
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Device is not connected", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        text.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mTV != null) {
                    if(keyCode == KeyEvent.KEYCODE_DEL){
                        mTV.getTextInputControl().sendDelete();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Device is not connected", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
    }

    public void closeClick(View view) {
        finish();
    }

    public void sendClick(View view) {
          if (mTV != null) {
                mTV.getTextInputControl().sendEnter();
                finish();
          }
    }

    int getMatchingCharacterLength (String oldString, String newString) {
        char [] oldChars = oldString.toCharArray();
        char [] newChars = newString.toCharArray();

        int length = Math.min(oldChars.length, newChars.length);

        for (int i = 0; i < length; i++) {
            if (oldChars[i] != newChars[i]) {
                return i;
            }
        }
        return length;
    }
}
