package com.manoel.audiobooster;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView gainText;
    private TextView statusText;
    private int gainMb = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);

        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(32, 28, 32, 48);
        scroll.addView(root);

        ImageView hero = new ImageView(this);
        hero.setImageResource(com.manoel.audiobooster.R.drawable.manoel_bosteiro_header);
        hero.setScaleType(ImageView.ScaleType.CENTER_CROP);
        hero.setAdjustViewBounds(true);
        root.addView(hero, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 620));

        TextView title = new TextView(this);
        title.setText("MANOEL BOSTEIRO");
        title.setTextSize(31);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        title.setPadding(0, 18, 0, 4);
        root.addView(title);

        TextView subtitle = new TextView(this);
        subtitle.setText("AUDIO BOOSTER MAX");
        subtitle.setTextSize(17);
        subtitle.setGravity(Gravity.CENTER);
        subtitle.setTypeface(Typeface.DEFAULT_BOLD);
        subtitle.setPadding(0, 0, 0, 22);
        root.addView(subtitle);

        gainText = new TextView(this);
        gainText.setTextSize(23);
        gainText.setGravity(Gravity.CENTER);
        root.addView(gainText);

        SeekBar gainSeek = new SeekBar(this);
        gainSeek.setMax(2400);
        gainSeek.setProgress(gainMb);
        root.addView(gainSeek, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        updateGainText();
        gainSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar s, int p, boolean f) {
                gainMb = p; updateGainText();
                Intent i = new Intent(MainActivity.this, BoosterService.class);
                i.setAction(BoosterService.ACTION_SET_GAIN); i.putExtra(BoosterService.EXTRA_GAIN, gainMb); startService(i);
            }
            public void onStartTrackingTouch(SeekBar s) {}
            public void onStopTrackingTouch(SeekBar s) {}
        });

        Button enable = new Button(this);
        enable.setText("LIGAR MANOEL BOSTEIRO");
        enable.setTextSize(18);
        enable.setOnClickListener(v -> {
            Intent i = new Intent(this, BoosterService.class); i.setAction(BoosterService.ACTION_ENABLE); i.putExtra(BoosterService.EXTRA_GAIN, gainMb);
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
            statusText.setText("Booster ligado.");
        });
        root.addView(enable, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button disable = new Button(this);
        disable.setText("DESLIGAR BOOSTER");
        disable.setOnClickListener(v -> { Intent i = new Intent(this, BoosterService.class); i.setAction(BoosterService.ACTION_DISABLE); startService(i); statusText.setText("Booster desligado."); });
        root.addView(disable, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        statusText = new TextView(this); statusText.setText("Desligado"); statusText.setTextSize(16); statusText.setGravity(Gravity.CENTER); statusText.setPadding(0,18,0,10); root.addView(statusText);
        TextView warning = new TextView(this); warning.setText("0–12 dB: moderado • 12–24 dB: EXTREMO. Volume excessivo pode causar distorção, danificar o alto-falante e prejudicar a audição."); warning.setTextSize(13); warning.setGravity(Gravity.CENTER); root.addView(warning);
        setContentView(scroll);
    }

    private void updateGainText() { gainText.setText(String.format("Ganho: +%.1f dB%s", gainMb / 100.0, gainMb > 1200 ? " • EXTREMO" : "")); }
}
