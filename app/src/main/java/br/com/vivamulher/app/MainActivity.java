package br.com.vivamulher.app;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import org.json.*;
import java.text.*;
import java.util.*;

public class MainActivity extends Activity {
    private static final int RED = Color.rgb(201,20,53), BLUE = Color.rgb(17,85,204);
    private static final int BLACK = Color.rgb(16,16,20), SOFT = Color.rgb(245,246,250);
    private LinearLayout page, content;
    private final ArrayList<Report> reports = new ArrayList<>();
    private android.content.SharedPreferences prefs;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        prefs = getSharedPreferences("viva_mulher", MODE_PRIVATE);
        load();
        showWelcome();
    }

    private void showWelcome() {
        if (prefs.getBoolean("accepted", false)) { buildApp(); return; }
        ScrollView scroll = new ScrollView(this);
        LinearLayout box = column(24); box.setGravity(Gravity.CENTER_HORIZONTAL); scroll.addView(box);
        TextView logo = label("🤝", 64, RED); box.addView(logo);
        box.addView(label("Viva Mulher", 34, BLACK, true));
        box.addView(label("Segurança compartilhada em cada caminho", 18, BLUE));
        box.addView(space(20));
        box.addView(cardText("Este aplicativo registra percepções de segurança em corridas. As notas não comprovam crimes e não substituem a polícia nem os recursos de segurança da plataforma de transporte."));
        box.addView(cardText("Use informações verdadeiras. Não publique acusações, documentos, endereço, telefone ou fotografia do motorista."));
        CheckBox agree = new CheckBox(this); agree.setText("Li e concordo com o uso responsável e com o armazenamento local neste aparelho."); agree.setTextSize(16); box.addView(agree, matchWrap());
        Button start = button("COMEÇAR", RED); start.setEnabled(false); agree.setOnCheckedChangeListener((b,c)->start.setEnabled(c));
        start.setOnClickListener(v->{ prefs.edit().putBoolean("accepted",true).apply(); buildApp(); }); box.addView(start, match(58));
        setContentView(scroll);
    }

    private void buildApp() {
        page = column(0); page.setBackgroundColor(SOFT);
        page.addView(header(), match(112));
        content = column(16);
        ScrollView scroll = new ScrollView(this); scroll.addView(content); page.addView(scroll, new LinearLayout.LayoutParams(-1,0,1));
        page.addView(nav(), match(66)); setContentView(page); showHome();
    }

    private View header() {
        LinearLayout h = new LinearLayout(this); h.setPadding(20,18,20,12); h.setGravity(Gravity.CENTER_VERTICAL); h.setBackgroundColor(RED);
        TextView hand = label("🤝",42,Color.WHITE); h.addView(hand, new LinearLayout.LayoutParams(62,-1));
        LinearLayout text = column(0); text.addView(label("Viva Mulher",27,Color.WHITE,true)); text.addView(label("Juntas, voltamos mais seguras",14,Color.WHITE)); h.addView(text,new LinearLayout.LayoutParams(0,-2,1));
        Button emergency = button("SOS", BLACK); emergency.setOnClickListener(v->showEmergency()); h.addView(emergency,new LinearLayout.LayoutParams(88,54)); return h;
    }

    private View nav() {
        LinearLayout n = new LinearLayout(this); n.setBackgroundColor(BLACK); n.setPadding(4,4,4,4);
        n.addView(navButton("⌂\nINÍCIO", v->showHome()), new LinearLayout.LayoutParams(0,-1,1));
        n.addView(navButton("＋\nAVALIAR", v->showForm()), new LinearLayout.LayoutParams(0,-1,1));
        n.addView(navButton("⌕\nBUSCAR", v->showSearch()), new LinearLayout.LayoutParams(0,-1,1));
        n.addView(navButton("☻\nPERFIL", v->showProfile()), new LinearLayout.LayoutParams(0,-1,1)); return n;
    }

    private void showHome() {
        clear(); content.addView(title("Como você está se sentindo?"));
        LinearLayout quick = new LinearLayout(this); quick.setOrientation(LinearLayout.HORIZONTAL);
        Button danger = button("ESTOU EM PERIGO", RED); danger.setOnClickListener(v->showEmergency()); quick.addView(danger,new LinearLayout.LayoutParams(0,62,1));
        Button ok = button("CORRIDA SEGURA", BLUE); ok.setOnClickListener(v->showForm()); LinearLayout.LayoutParams qp=new LinearLayout.LayoutParams(0,62,1); qp.setMargins(10,0,0,0); quick.addView(ok,qp); content.addView(quick);
        content.addView(space(18)); content.addView(title("Maior atenção"));
        ArrayList<Report> sorted = new ArrayList<>(reports); sorted.sort(Comparator.comparingInt(r->r.score));
        if(sorted.isEmpty()) content.addView(cardText("Ainda não existem avaliações neste aparelho. Toque em AVALIAR depois de uma corrida."));
        for(int i=0;i<Math.min(5,sorted.size());i++) content.addView(reportCard(sorted.get(i)));
        content.addView(space(14)); content.addView(cardText("As classificações representam relatos pessoais. Em risco imediato, procure um local movimentado e ligue para 190."));
    }

    private void showForm() {
        clear(); content.addView(title("Avaliar uma corrida"));
        Spinner company = new Spinner(this); String[] companies={"Uber","99","inDrive","Maxim","Outro"}; company.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,companies)); content.addView(fieldWrap("Aplicativo de transporte",company));
        EditText name = edit("Primeiro nome do motorista", InputType.TYPE_CLASS_TEXT); content.addView(name,match(58));
        EditText plate = edit("Placa (ex.: ABC1D23)", InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS); content.addView(plate,match(58));
        TextView scoreText = label("Nível de segurança: 5 — atenção",18,BLACK,true); content.addView(scoreText);
        SeekBar score = new SeekBar(this); score.setMax(9); score.setProgress(4); content.addView(score,match(52));
        score.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){ public void onProgressChanged(SeekBar b,int p,boolean f){int s=p+1;scoreText.setText("Nível de segurança: "+s+" — "+level(s));scoreText.setTextColor(scoreColor(s));} public void onStartTrackingTouch(SeekBar b){} public void onStopTrackingTouch(SeekBar b){} });
        RadioGroup when = new RadioGroup(this); when.setOrientation(RadioGroup.HORIZONTAL); RadioButton during=radio("Durante a corrida",true), after=radio("Após a corrida",false); when.addView(during); when.addView(after); content.addView(when);
        CheckBox honest = new CheckBox(this); honest.setText("Confirmo que esta avaliação descreve uma experiência real."); content.addView(honest);
        Button save=button("SALVAR AVALIAÇÃO",RED); save.setOnClickListener(v->{
            String p=plate.getText().toString().replaceAll("[^A-Za-z0-9]","").toUpperCase(Locale.ROOT);
            String nm=name.getText().toString().trim();
            if(p.length()!=7 || nm.length()<2 || !honest.isChecked()){toast("Preencha primeiro nome, placa válida e confirme a experiência.");return;}
            reports.add(new Report(company.getSelectedItem().toString(),nm,p,score.getProgress()+1,during.isChecked(),System.currentTimeMillis())); save(); toast("Avaliação salva neste aparelho."); showHome();
        }); content.addView(save,match(60));
    }

    private void showSearch() {
        clear(); content.addView(title("Consultar motorista")); content.addView(cardText("Digite a placa completa. O resultado exibirá a placa mascarada para reduzir exposição indevida."));
        EditText plate=edit("Placa completa",InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS); content.addView(plate,match(60));
        Button search=button("PESQUISAR",BLUE); content.addView(search,match(58)); LinearLayout results=column(8); content.addView(results);
        search.setOnClickListener(v->{ results.removeAllViews(); String q=plate.getText().toString().replaceAll("[^A-Za-z0-9]","").toUpperCase(Locale.ROOT); int found=0; for(Report r:reports) if(r.plate.equals(q)){results.addView(reportCard(r));found++;} if(found==0)results.addView(cardText("Nenhuma avaliação encontrada neste aparelho para essa placa.")); });
    }

    private void showProfile() {
        clear(); content.addView(title("Meu perfil")); TextView avatar=label(prefs.getString("avatar","🌺"),72,RED); avatar.setGravity(Gravity.CENTER); content.addView(avatar,match(100));
        EditText nickname=edit("Apelido",InputType.TYPE_CLASS_TEXT); nickname.setText(prefs.getString("nickname","")); content.addView(nickname,match(58));
        Spinner avatars=new Spinner(this); String[] options={"🌺 Flor","🦋 Borboleta","🐱 Gatinha","🌙 Lua","⭐ Estrela","🌈 Arco-íris"}; avatars.setAdapter(new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,options)); content.addView(avatars,match(58));
        Button saveProfile=button("SALVAR PERFIL",BLUE); saveProfile.setOnClickListener(v->{String a=options[avatars.getSelectedItemPosition()].substring(0,2).trim(); prefs.edit().putString("nickname",nickname.getText().toString().trim()).putString("avatar",a).apply();avatar.setText(a);toast("Perfil salvo.");}); content.addView(saveProfile,match(58));
        Button clear=button("APAGAR DADOS DESTE APARELHO",BLACK); clear.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Apagar avaliações?").setMessage("Esta ação remove permanentemente os registros locais.").setNegativeButton("Cancelar",null).setPositiveButton("Apagar",(d,w)->{reports.clear();save();showHome();}).show()); content.addView(clear,match(58));
        content.addView(cardText("Versão 1.0 — dados armazenados somente neste celular. Nenhuma avaliação é enviada à internet nesta versão."));
    }

    private void showEmergency() {
        new AlertDialog.Builder(this).setTitle("Ajuda de emergência").setMessage("Se houver risco imediato, saia do veículo apenas quando for seguro, vá para um local movimentado e acione a polícia. O aplicativo abrirá o discador; confira e toque em ligar.").setNegativeButton("Cancelar",null).setNeutralButton("Avaliar agora",(d,w)->showForm()).setPositiveButton("DISCAR 190",(d,w)->startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:190")))).show();
    }

    private View reportCard(Report r) {
        LinearLayout c=column(8); c.setPadding(16,14,16,14); c.setBackgroundColor(Color.WHITE);
        LinearLayout top=new LinearLayout(this); TextView badge=label(String.valueOf(r.score),25,Color.WHITE,true); badge.setGravity(Gravity.CENTER); badge.setBackgroundColor(scoreColor(r.score)); top.addView(badge,new LinearLayout.LayoutParams(54,54));
        LinearLayout detail=column(0); detail.setPadding(12,0,0,0); detail.addView(label(r.company+" • "+r.name,18,BLACK,true)); detail.addView(label(mask(r.plate)+" • "+level(r.score),15,scoreColor(r.score))); top.addView(detail,new LinearLayout.LayoutParams(0,-2,1)); c.addView(top);
        c.addView(label((r.during?"Registrada durante a corrida":"Registrada após a corrida")+" • "+new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date(r.time)),13,Color.DKGRAY));
        LinearLayout.LayoutParams p=matchWrap(); p.setMargins(0,0,0,10); c.setLayoutParams(p); return c;
    }

    private void save(){JSONArray a=new JSONArray();for(Report r:reports)a.put(r.json());prefs.edit().putString("reports",a.toString()).apply();}
    private void load(){try{JSONArray a=new JSONArray(prefs.getString("reports","[]"));for(int i=0;i<a.length();i++)reports.add(new Report(a.getJSONObject(i)));}catch(Exception ignored){}}
    private String mask(String p){return p.length()==7?p.substring(0,3)+"••"+p.substring(5):"•••••••";}
    private String level(int s){if(s<=2)return "perigo extremo";if(s<=4)return "alto risco percebido";if(s<=6)return "atenção";if(s<=8)return "confiável";return "muito confiável";}
    private int scoreColor(int s){if(s<=3)return RED;if(s<=6)return Color.rgb(224,126,0);return s<=8?BLUE:Color.rgb(0,125,90);}
    private void clear(){content.removeAllViews();}
    private LinearLayout column(int pad){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);l.setPadding(pad,pad,pad,pad);return l;}
    private TextView title(String s){TextView t=label(s,23,BLACK,true);t.setPadding(0,8,0,14);return t;}
    private TextView label(String s,int size,int color){return label(s,size,color,false);}
    private TextView label(String s,int size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(Typeface.DEFAULT,Typeface.BOLD);return t;}
    private TextView cardText(String s){TextView t=label(s,16,BLACK);t.setPadding(18,16,18,16);t.setBackgroundColor(Color.WHITE);LinearLayout.LayoutParams p=matchWrap();p.setMargins(0,0,0,12);t.setLayoutParams(p);return t;}
    private Button button(String s,int color){Button b=new Button(this);b.setText(s);b.setTextColor(Color.WHITE);b.setTextSize(14);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setBackgroundColor(color);return b;}
    private Button navButton(String s,View.OnClickListener l){Button b=button(s,BLACK);b.setOnClickListener(l);b.setTextSize(11);return b;}
    private EditText edit(String hint,int type){EditText e=new EditText(this);e.setHint(hint);e.setInputType(type);e.setTextSize(17);e.setSingleLine(true);return e;}
    private RadioButton radio(String s,boolean checked){RadioButton r=new RadioButton(this);r.setText(s);r.setChecked(checked);return r;}
    private View fieldWrap(String name,View field){LinearLayout l=column(0);l.addView(label(name,13,Color.DKGRAY,true));l.addView(field,match(52));return l;}
    private Space space(int h){Space s=new Space(this);s.setLayoutParams(match(h));return s;}
    private LinearLayout.LayoutParams match(int h){return new LinearLayout.LayoutParams(-1,h);}
    private LinearLayout.LayoutParams matchWrap(){return new LinearLayout.LayoutParams(-1,-2);}
    private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_LONG).show();}

    static class Report {
        String company,name,plate; int score; boolean during; long time;
        Report(String c,String n,String p,int s,boolean d,long t){company=c;name=n;plate=p;score=s;during=d;time=t;}
        Report(JSONObject o)throws JSONException{this(o.getString("company"),o.getString("name"),o.getString("plate"),o.getInt("score"),o.getBoolean("during"),o.getLong("time"));}
        JSONObject json(){JSONObject o=new JSONObject();try{o.put("company",company);o.put("name",name);o.put("plate",plate);o.put("score",score);o.put("during",during);o.put("time",time);}catch(Exception ignored){}return o;}
    }
}
