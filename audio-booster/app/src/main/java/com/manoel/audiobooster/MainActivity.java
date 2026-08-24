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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView gainText;
    private TextView statusText;
    private SeekBar gainSeek;
    private int gainMb = 1200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 10);
        }

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER_HORIZONTAL);
        root.setPadding(48, 72, 48, 48);

        TextView title = new TextView(this);
        title.setText("Audio Booster MAX");
        title.setTextSize(30);
        title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title);

        TextView warning = new TextView(this);
        warning.setText("Faixa normal: 0 a +12 dB. Faixa EXTREMA: +12 a +24 dB. Ganho alto pode causar clipping, distorção, danificar alto-falantes e prejudicar a audição.");
        warning.setTextSize(15);
        warning.setPadding(0, 30, 0, 30);
        root.addView(warning);

        gainText = new TextView(this);
        gainText.setTextSize(22);
        root.addView(gainText);

        gainSeek = new SeekBar(this);
        gainSeek.setMax(2400);
        gainSeek.setProgress(gainMb);
        root.addView(gainSeek, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        updateGainText();

        gainSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                gainMb = progress;
                updateGainText();
                Intent i = new Intent(MainActivity.this, BoosterService.class);
                i.setAction(BoosterService.ACTION_SET_GAIN);
                i.putExtra(BoosterService.EXTRA_GAIN, gainMb);
                startService(i);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        Button enable = new Button(this);
        enable.setText("LIGAR BOOSTER MAX");
        enable.setTextSize(18);
        enable.setOnClickListener(v -> {
            Intent i = new Intent(this, BoosterService.class);
            i.setAction(BoosterService.ACTION_ENABLE);
            i.putExtra(BoosterService.EXTRA_GAIN, gainMb);
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(i); else startService(i);
            statusText.setText("Booster solicitado. Veja o status na notificação.");
        });
        root.addView(enable, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        Button disable = new Button(this);
        disable.setText("DESLIGAR BOOSTER");
        disable.setTextSize(18);
        disable.setOnClickListener(v -> {
            Intent i = new Intent(this, BoosterService.class);
            i.setAction(BoosterService.ACTION_DISABLE);
            startService(i);
            statusText.setText("Booster desligado.");
        });
        root.addView(disable, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        statusText = new TextView(this);
        statusText.setText("Desligado");
        statusText.setTextSize(16);
        statusText.setPadding(0, 30, 0, 0);
        root.addView(statusText);

        TextView note = new TextView(this);
        note.setText("Compatibilidade: alguns aparelhos limitam ou bloqueiam efeitos globais. +24 dB é o valor solicitado ao efeito; o ganho real pode ser menor dependendo do hardware e do Android.");
        note.setTextSize(13);
        note.setPadding(0, 30, 0, 0);
        root.addView(note);

        setContentView(root);
    }

    private void updateGainText() {
        String zone = gainMb > 1200 ? "  •  EXTREMO" : "";
        gainText.setText(String.format("Ganho: +%.1f dB%s", gainMb / 100.0, zone));
    }
}
