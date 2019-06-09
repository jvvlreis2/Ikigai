package ikigai.example.dispositivosmoveis.ikigai;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

public class PaintMainView extends AppCompatActivity{
    Button btTip;
    private AlertDialog alerta;
    Button btStart;
    private PaintView paintView;
    int color = Color.GREEN;
    private int color1;
    Dialog dialog;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_view);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.information))
                .setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
        alerta = builder.create();
        alerta.show();
        paintView = (PaintView) findViewById(R.id.paintView);
        paintView.setParo(false);
        paintView.setDEFAULT_COLOR(Color.argb(122,10,50,30));
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);
        Toolbar toolbar = findViewById(R.id.toolBar);
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setSubtitleTextColor(Color.BLACK);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        dialog = gerarDialog();
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.doodle_fragment_menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {


        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        //noinspection SimplifiableIfStatement
        else if (id == R.id.color) {
            dialog.show();

        }

        else if (id == R.id.delete){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.title_delete));
            builder.setMessage(getString(R.string.subtitle_delete1));
            builder.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    paintView.clear();
                    //new removeButtonView().execute(position);
                    paintView.setParo(false);
                }
            });
            builder.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {

                }
            });
            alerta = builder.create();
            alerta.show();

        }

        return super.onOptionsItemSelected(item);
    }
    private Dialog gerarDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.fragment_color);
        dialog.setTitle(getString(R.string.color_selection));
        SeekBar redSeekBar =
                dialog.findViewById(R.id.redSeekBar);
        SeekBar greenSeekBar =
                dialog.findViewById(R.id.greenSeekBar);
        SeekBar blueSeekBar =
                dialog.findViewById(R.id.blueSeekBar);
        View colorView = dialog.findViewById(R.id.colorView);
        Button buttonOk = (Button) dialog.findViewById(R.id.button2);
        Button buttonCancel = (Button) dialog.findViewById(R.id.button);
        final SeekBar.OnSeekBarChangeListener colorChangedListener =
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean
                            fromUser) {
                        color = Color.argb(
                                122,
                                redSeekBar.getProgress(),
                                greenSeekBar.getProgress(),
                                blueSeekBar.getProgress()
                        );
                        colorView.setBackgroundColor(color);
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                };
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redSeekBar.setProgress(Color.red(paintView.getCurrentColor()));
                greenSeekBar.setProgress(Color.green(paintView.getCurrentColor()));
                blueSeekBar.setProgress(Color.blue(paintView.getCurrentColor()));
                dialog.dismiss();
            }
        });

        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paintView.setCurrentColor(color);
                dialog.dismiss();
            }
        });
        //registra o observador em todas as SeekBars
        redSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        //pega o DoodleView para pegar a cor atual e configurar
        //configura o progresso de cada barra
        redSeekBar.setProgress(Color.red(paintView.getCurrentColor()));
        greenSeekBar.setProgress(Color.green(paintView.getCurrentColor()));
        blueSeekBar.setProgress(Color.blue(paintView.getCurrentColor()));
        return dialog;
    }
}
