package com.manoel.nikolaracing;

import android.app.*;
import android.os.*;
import android.graphics.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends Activity {
    LinearLayout root;
    EditText nameInput, ipInput;
    Spinner colorSpinner, decalSpinner;
    TextView status;
    GameView game;
    String playerName = "Piloto";
    int carColor = Color.RED;
    int decal = 0;

    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
        showMenu();
    }

    TextView title(String s, int sp) { TextView t=new TextView(this); t.setText(s); t.setTextSize(sp); t.setTextColor(Color.WHITE); t.setGravity(Gravity.CENTER); t.setPadding(8,8,8,8); return t; }
    Button btn(String s) { Button b=new Button(this); b.setText(s); b.setTextSize(17); b.setAllCaps(false); return b; }
    void baseScreen() { root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setGravity(Gravity.CENTER); root.setPadding(28,20,28,20); root.setBackgroundColor(Color.rgb(16,18,24)); setContentView(root); }

    void showMenu() {
        baseScreen(); root.addView(title("NIKOLA RACING", 34), new LinearLayout.LayoutParams(-1,-2));
        root.addView(title("Corrida arcade • salto • voo • nitro", 18), new LinearLayout.LayoutParams(-1,-2));
        Button garage=btn("Garagem / Personalizar"), solo=btn("Corrida contra IA"), multi=btn("Multiplayer Wi‑Fi local (beta)"), help=btn("Como jogar");
        for(Button b:new Button[]{garage,solo,multi,help}) root.addView(b,new LinearLayout.LayoutParams(600,-2));
        garage.setOnClickListener(v->showGarage()); solo.setOnClickListener(v->startGame(false,false,null)); multi.setOnClickListener(v->showNetwork()); help.setOnClickListener(v->showHelp());
    }

    void showGarage() {
        baseScreen(); root.addView(title("GARAGEM",30));
        nameInput=new EditText(this); nameInput.setHint("Nome do competidor"); nameInput.setText(playerName); nameInput.setTextColor(Color.WHITE); nameInput.setHintTextColor(Color.GRAY); root.addView(nameInput,new LinearLayout.LayoutParams(600,-2));
        colorSpinner=new Spinner(this); String[] cs={"Vermelho","Azul","Verde","Amarelo","Roxo","Laranja","Preto","Branco"}; colorSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, cs)); root.addView(colorSpinner,new LinearLayout.LayoutParams(600,-2));
        decalSpinner=new Spinner(this); String[] ds={"Sem desenho","Faixa central","Raio","Número 7","X esportivo"}; decalSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, ds)); root.addView(decalSpinner,new LinearLayout.LayoutParams(600,-2));
        Button save=btn("Salvar e voltar"); root.addView(save,new LinearLayout.LayoutParams(600,-2));
        save.setOnClickListener(v->{ playerName=nameInput.getText().toString().trim(); if(playerName.isEmpty())playerName="Piloto"; int[] cc={Color.RED,Color.BLUE,Color.GREEN,Color.YELLOW,Color.MAGENTA,0xFFFF7A00,Color.BLACK,Color.WHITE}; carColor=cc[colorSpinner.getSelectedItemPosition()]; decal=decalSpinner.getSelectedItemPosition(); showMenu(); });
    }

    void showNetwork() {
        baseScreen(); root.addView(title("MULTIPLAYER WI‑FI LOCAL",28)); root.addView(title("Os dois celulares devem estar na mesma rede Wi‑Fi.",16));
        Button host=btn("Criar corrida (Host)"); ipInput=new EditText(this); ipInput.setHint("IP do Host, ex.: 192.168.1.20"); ipInput.setTextColor(Color.WHITE); ipInput.setHintTextColor(Color.GRAY); Button join=btn("Entrar na corrida"), back=btn("Voltar"); status=title("",15);
        root.addView(host,new LinearLayout.LayoutParams(600,-2)); root.addView(ipInput,new LinearLayout.LayoutParams(600,-2)); root.addView(join,new LinearLayout.LayoutParams(600,-2)); root.addView(status,new LinearLayout.LayoutParams(700,-2)); root.addView(back,new LinearLayout.LayoutParams(600,-2));
        host.setOnClickListener(v->startGame(true,true,null)); join.setOnClickListener(v->{String ip=ipInput.getText().toString().trim(); if(ip.isEmpty())status.setText("Digite o IP do Host."); else startGame(true,false,ip);}); back.setOnClickListener(v->showMenu());
    }

    void showHelp(){ baseScreen(); root.addView(title("COMO JOGAR",30)); root.addView(title("◀ ▶ direcionam • ACELERAR e FREAR controlam a velocidade\nNITRO dá impulso • SALTO pula obstáculos • VOAR mantém o carro no ar por alguns segundos\nPasse pelos checkpoints, complete 3 voltas e tente chegar em 1º.\nNo multiplayer beta, crie a corrida em um aparelho e use o IP do host no outro.",18)); Button b=btn("Voltar"); root.addView(b,new LinearLayout.LayoutParams(500,-2)); b.setOnClickListener(v->showMenu()); }
    void startGame(boolean multiplayer, boolean host, String ip){ game=new GameView(this, playerName, carColor, decal, multiplayer, host, ip); setContentView(game); }
    @Override public void onBackPressed(){ if(game!=null){game.shutdown(); game=null; showMenu();} else showMenu(); }
}
