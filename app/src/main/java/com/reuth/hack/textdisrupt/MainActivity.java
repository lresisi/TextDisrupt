package com.reuth.hack.textdisrupt;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Layout;
import android.view.MotionEvent;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TextViewInterface {

    ArrayList<Word> words_array = new ArrayList<>();
    private int touchedWordIndex = -1;

    SpannableString span_str;

    TextView text_view;

    Boolean shouldEmphBegin = false, shouldEmphEnd = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String displayedText = getIntent().getStringExtra("IMPORTED_TEXT");

        ((TextView) findViewById(R.id.main_text_view)).setText(displayedText);

        init_app();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


    }


    public void init_app() {

        // build words array (will be used by other activities of this app)
        text_view = (TextView) findViewById(R.id.main_text_view);
        String text_str = text_view.getText().toString();

        buildWordsArray(text_str);


        setTextViewOnLongTouchListener();
        setTextViewOnLongClickListener();


    }


    public TextView getTextView() {
        return text_view;
    }

    public ArrayList<Word> getWordsArray() {
        return words_array;
    }

    public SpannableString getSpanStr() {
        return span_str;
    }

    public void buildWordsArray(String text_str) {

        BreakIterator bi = BreakIterator.getWordInstance(Locale.US);
        bi.setText(text_str);

        int lastIndex = bi.first();
        while (lastIndex != BreakIterator.DONE) {
            int firstIndex = lastIndex;
            lastIndex = bi.next();

            if (lastIndex != BreakIterator.DONE
                    && Character.isLetterOrDigit(
                    text_str.charAt(firstIndex))) {
                String value = text_str.substring(firstIndex, lastIndex);
                words_array.add(new Word(firstIndex, lastIndex, value));
            }
        }

        span_str = new SpannableString(text_str);
        text_view.setText(span_str);
        text_view.setMovementMethod(LinkMovementMethod.getInstance());
    }

    // This is used in order to calulate the current word that has being touched
    private void setTextViewOnLongTouchListener() {

        TextView tv = (TextView) findViewById(R.id.main_text_view);
        tv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                Layout layout = ((TextView) v).getLayout();
                int x = (int) event.getX();
                int y = (int) event.getY();

                if (layout != null) {

                    int line = layout.getLineForVertical(y);
                    int offset = layout.getOffsetForHorizontal(line, x);

                    touchedWordIndex = getWordIndex(offset);
                }

                return false;
            }
        });
    }


    private void setTextViewOnLongClickListener() {

        TextView tv = (TextView) findViewById(R.id.main_text_view);
        tv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("TOUCHED_WORD_ARRAY", words_array);

                Intent intent = new Intent(getBaseContext(), SingleWordActivity.class);
                intent.putExtra("TOUCHED_WORD_INDEX", touchedWordIndex);
                intent.putExtra("TOUCHED_WORD_BUNDLE", bundle);

                startActivity(intent);

                // open some shitty dialog.
                return false;
            }
        });


    }

    private int getWordIndex(int offset) {

        for (int i = 0; i < words_array.size(); i++) {
            Word word = words_array.get(i);

            if (word != null && word.getBegin() <= offset && offset <= word.getEnd()) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_change_font) {

        } else if (id == R.id.nav_change_size_smaller) {

        } else if (id == R.id.nav_change_size_bigger) {

        } else if (id == R.id.nav_change_line_spacing_smaller) {

        } else if (id == R.id.nav_change_line_spacing_bigger) {

        } else if (id == R.id.nav_emphasize_prefix) {
            boolean emphBegin = shouldEmphBegin;
            for (Word w : this.words_array) {
                int beginIndex = w.getBegin();
                int endIndex = Math.min(beginIndex + 2, w.getEnd());
                int text_color = Color.RED;
                SpannableString ss = (SpannableString) text_view.getText();
                ForegroundColorSpan[] spans = ss.getSpans(beginIndex, endIndex,
                        ForegroundColorSpan.class);
                int spans_length = spans.length;
                if ((spans_length > 0) && emphBegin) {
                    span_str.removeSpan(spans[0]);
                    shouldEmphBegin = false;
                } else {
                    span_str.setSpan(new ForegroundColorSpan(text_color),
                            beginIndex, endIndex, 0);
                    shouldEmphBegin = true;
                }

            }
        } else if (id == R.id.nav_emphasize_middle) {
            for (Word w : this.words_array) {
                int beginIndex = w.getBegin() + 2;
                int endIndex = w.getEnd() - 2;
                if (beginIndex < endIndex) {
                    int text_color = Color.RED;
                    SpannableString ss = (SpannableString) text_view.getText();
                    ForegroundColorSpan[] spans = ss.getSpans(beginIndex, endIndex,
                            ForegroundColorSpan.class);
                    int spans_length = spans.length;
                    if (spans_length > 0) {
                        span_str.removeSpan(spans[0]);
                    } else {
                        span_str.setSpan(new ForegroundColorSpan(text_color),
                                beginIndex, endIndex, 0);
                    }
                }
            }
        } else if (id == R.id.nav_emphasize_suffix) {
            boolean emphEnd = shouldEmphEnd;
            for (Word w : this.words_array) {
                int endIndex = w.getEnd();
                int beginIndex = Math.max(w.getBegin(), endIndex - 2);
                int text_color = Color.RED;
                SpannableString ss = (SpannableString) text_view.getText();
                ForegroundColorSpan[] spans = ss.getSpans(beginIndex, endIndex,
                        ForegroundColorSpan.class);
                int spans_length = spans.length;
                if ((spans_length > 0) && emphEnd) {
                    span_str.removeSpan(spans[0]);
                    shouldEmphEnd = false;
                } else {
                    span_str.setSpan(new ForegroundColorSpan(text_color),
                            beginIndex, endIndex, 0);
                    shouldEmphEnd = true;
                }
            }
        } else if (id == R.id.nav_emphasize_margin) {

        } else if (id == R.id.nav_text_to_speach) {

        }
        text_view.setText(span_str);
        text_view.setMovementMethod(LinkMovementMethod.getInstance());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
