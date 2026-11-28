package com.xiatech.mercyai;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int REQUEST_VOICE = 101;
    private static final int REQUEST_FILE = 102;
    private static final int REQUEST_IMAGE = 103;

    private static final int BG = Color.rgb(13, 10, 24);
    private static final int PANEL = Color.rgb(29, 22, 46);
    private static final int ACCENT = Color.rgb(174, 111, 255);
    private static final int TEXT = Color.rgb(245, 240, 255);
    private static final int MUTED = Color.rgb(177, 164, 199);

    private LinearLayout root;
    private LinearLayout content;
    private String currentPage = "Home";
    private SharedPreferences memory;
    private EditText chatMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        memory = getSharedPreferences("mercy_local_memory", MODE_PRIVATE);
        showPage("Home");
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView text(String value, float size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }

    private LinearLayout vertical() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    private void addSpace(LinearLayout parent, int height) {
        View space = new View(this);
        parent.addView(space, new LinearLayout.LayoutParams(1, dp(height)));
    }

    private void showPage(String page) {
        currentPage = page;
        root = vertical();
        root.setBackgroundColor(BG);

        LinearLayout header = vertical();
        header.setPadding(dp(22), dp(22), dp(22), dp(14));
        TextView brand = text("MERCY AI", 13, ACCENT);
        brand.setLetterSpacing(0.18f);
        header.addView(brand);
        addSpace(header, 7);
        header.addView(text(page, 27, TEXT));
        root.addView(header);

        ScrollView scroll = new ScrollView(this);
        content = vertical();
        content.setPadding(dp(18), dp(8), dp(18), dp(22));
        scroll.addView(content);
        root.addView(scroll, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 0, 1));

        buildPage(page);
        root.addView(buildNavigation());
        setContentView(root);
    }

    private void buildPage(String page) {
        if ("Home".equals(page)) {
            addCard(content, "Hello. I'm Mercy.", "Your private assistant workspace. This early build stores memory on this device.");
            addSpace(content, 12);
            addAction(content, "Open chat", "Type a message or use speech recognition.", "Chat");
            addAction(content, "Local memory", "Save and review notes stored inside the app.", "Memory");
            addAction(content, "File tools", "Choose a document or image from your device.", "Files");
            addSpace(content, 10);
            addCard(content, "Development status", "AI responses: not connected yet\nOCR engine: not integrated yet\nPhone controls: not available in this build");
        } else if ("Chat".equals(page)) {
            addCard(content, "Chat workspace", "The chat interface is ready. An AI model or API connection has not been added yet, so messages are not sent to an AI service.");
            addSpace(content, 12);
            chatMessage = input("Write a message...");
            content.addView(chatMessage);
            addSpace(content, 8);
            LinearLayout actions = horizontal();
            actions.addView(makeButton("Voice input", () -> startVoice()), new LinearLayout.LayoutParams(0, dp(48), 1));
            actions.addView(makeButton("Clear", () -> chatMessage.setText("")),
                    new LinearLayout.LayoutParams(0, dp(48), 1));
            content.addView(actions);
            addSpace(content, 10);
            addCard(content, "Privacy", "This starter chat does not upload your text. Connect an AI backend in a later development step.");
        } else if ("Memory".equals(page)) {
            addCard(content, "On-device memory", "Notes saved here stay in this app's private storage on this device.");
            addSpace(content, 12);
            EditText note = input("Enter a memory or note...");
            content.addView(note);
            addSpace(content, 8);
            content.addView(makeButton("Save memory", () -> {
                String value = note.getText().toString().trim();
                if (value.isEmpty()) {
                    toast("Write a note first.");
                    return;
                }
                String old = memory.getString("items", "");
                String updated = old.isEmpty() ? value : old + "\n" + value;
                memory.edit().putString("items", updated).apply();
                note.setText("");
                showPage("Memory");
                toast("Saved on this device.");
            }));
            addSpace(content, 14);
            content.addView(text("SAVED NOTES", 12, ACCENT));
            addSpace(content, 6);
            String saved = memory.getString("items", "");
            addCard(content, "Your saved memory", saved.isEmpty() ? "No notes saved yet." : saved);
            addSpace(content, 8);
            content.addView(makeButton("Clear all memory", () -> {
                memory.edit().remove("items").apply();
                showPage("Memory");
                toast("Local memory cleared.");
            }));
        } else if ("Files".equals(page)) {
            addCard(content, "File tools", "Choose files using Android's system picker. This build does not modify or upload selected files.");
            addSpace(content, 12);
            content.addView(makeButton("Choose a document", () -> chooseFile(false)));
            addSpace(content, 8);
            content.addView(makeButton("Choose an image", () -> chooseFile(true)));
            addSpace(content, 14);
            addCard(content, "OCR status", "Image text recognition is planned but not implemented yet.");
            addCard(content, "File safety", "Only select files you intend to open. No file is changed by this screen.");
        }
    }

    private LinearLayout horizontal() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        return layout;
    }

    private void addCard(LinearLayout parent, String title, String body) {
        LinearLayout card = vertical();
        card.setBackgroundColor(PANEL);
        card.setPadding(dp(16), dp(15), dp(16), dp(15));
        card.addView(text(title, 17, TEXT));
        addSpace(card, 7);
        TextView description = text(body, 14, MUTED);
        description.setLineSpacing(dp(3), 1f);
        card.addView(description);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(10);
        parent.addView(card, params);
    }

    private void addAction(LinearLayout parent, String title, String description, String page) {
        LinearLayout card = vertical();
        card.setBackgroundColor(PANEL);
        card.setPadding(dp(16), dp(14), dp(16), dp(14));
        card.addView(text(title, 17, TEXT));
        addSpace(card, 5);
        card.addView(text(description, 13, MUTED));
        card.setOnClickListener(v -> showPage(page));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.bottomMargin = dp(10);
        parent.addView(card, params);
    }

    private EditText input(String hint) {
        EditText edit = new EditText(this);
        edit.setHint(hint);
        edit.setHintTextColor(MUTED);
        edit.setTextColor(TEXT);
        edit.setTextSize(15);
        edit.setPadding(dp(13), dp(10), dp(13), dp(10));
        edit.setBackgroundColor(PANEL);
        return edit;
    }

    private Button makeButton(String label, Runnable action) {
        Button button = new Button(this);
        button.setText(label);
        button.setTextColor(Color.WHITE);
        button.setTextSize(13);
        button.setAllCaps(false);
        button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(ACCENT));
        button.setOnClickListener(v -> action.run());
        return button;
    }

    private LinearLayout buildNavigation() {
        LinearLayout nav = horizontal();
        nav.setBackgroundColor(PANEL);
        nav.setPadding(dp(6), dp(6), dp(6), dp(6));
        String[] pages = {"Home", "Chat", "Memory", "Files"};
        for (String page : pages) {
            Button button = new Button(this);
            button.setText(page);
            button.setAllCaps(false);
            button.setTextSize(11);
            button.setTextColor("".equals(page) ? TEXT : (currentPage.equals(page) ? ACCENT : MUTED));
            button.setBackgroundColor(Color.TRANSPARENT);
            button.setOnClickListener(v -> showPage(page));
            nav.addView(button, new LinearLayout.LayoutParams(0, dp(48), 1));
        }
        return nav;
    }

    private void startVoice() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your message");
        try {
            startActivityForResult(intent, REQUEST_VOICE);
        } catch (ActivityNotFoundException error) {
            toast("Speech recognition is not available on this device.");
        }
    }

    private void chooseFile(boolean imageOnly) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType(imageOnly ? "image/*" : "*/*");
        startActivityForResult(intent, imageOnly ? REQUEST_IMAGE : REQUEST_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == REQUEST_VOICE) {
            ArrayList<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                showPage("Chat");
                chatMessage.setText(results.get(0));
                chatMessage.setSelection(chatMessage.length());
                toast("Speech captured. You can edit it before using it.");
            }
        } else if (requestCode == REQUEST_FILE || requestCode == REQUEST_IMAGE) {
            Uri uri = data.getData();
            if (uri != null) {
                String kind = requestCode == REQUEST_IMAGE ? "Image" : "File";
                showPage("Files");
                addCard(content, "Selected " + kind, uri.toString());
                if (requestCode == REQUEST_IMAGE) {
                    toast("Image selected. OCR is not integrated yet.");
                } else {
                    toast("File selected.");
                }
            }
        }
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
