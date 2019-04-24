package com.capsa.prayer.activtites;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.capsa.prayer.time.R;

public class Qalmas extends AppCompatActivity {

    TextView tvQalma, tvQalmaTrans;
    ImageView /*qlma_ps,*/ shareQalima, btnPrev, btnNxt;
    MediaPlayer mediaPlayer;
    String qalma;
    int qalmaNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lay_qalma_s);

        initMain();
        setActions();
    }

    private void initMain() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setBackgroundDrawable(getDrawable(R.drawable.blue));
        getSupportActionBar().setTitle(getIntent().getStringExtra("qalma_count"));

        qalmaNum = getIntent().getIntExtra("qalmaNum", -1);
        /*qlma_ps = (ImageView) findViewById(R.id.qlma_ps);*/
        shareQalima = (ImageView) findViewById(R.id.shareQalima);
        btnPrev = (ImageView) findViewById(R.id.btnPrev);
        btnNxt = (ImageView) findViewById(R.id.btnNxt);
        tvQalma = (TextView) findViewById(R.id.tvQalmaText);
        tvQalmaTrans = (TextView) findViewById(R.id.tvQalmaTrans);
        qalma = getIntent().getStringExtra("qalma");
        tvQalma.setText(qalma);
        String qalmaTrans = getIntent().getStringExtra("qalmaTrans");
        tvQalmaTrans.setText(qalmaTrans);
    }

    private void setActions() {

        /*qlma_ps.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                qlma_ps.setSelected(!qlma_ps.isSelected());

                if (qlma_ps.isSelected()) {
                    playQ();
                } else {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        qlma_ps.setSelected(false);
                    }
                }
            }
        });*/

        shareQalima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(android.content.Intent.EXTRA_TEXT,
                        qalma);
                intent.putExtra(android.content.Intent.EXTRA_STREAM, R.drawable.ic_launcher);
                startActivity(Intent.createChooser(intent, "Choose from..."));
            }
        });

        btnPrev.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (qalmaNum != 0)
                    qalmaNum -= 1;
                else
                    qalmaNum = 5;
                updateView(qalmaNum);
            }
        });

        btnNxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (qalmaNum != 5)
                    qalmaNum += 1;
                else
                    qalmaNum = 0;
                updateView(qalmaNum);
            }
        });
    }

    /*void playQ() {
        if (mediaPlayer == null) {

            if (qalmaNum != -1) {

                switch (qalmaNum) {

                    case 0:
                        mediaPlayer = MediaPlayer.create(Qalmas.this,
                                R.raw.pehla_kalima);
                        break;
                    case 1:
                        mediaPlayer = MediaPlayer.create(Qalmas.this,
                                R.raw.dosra_kalima);
                        break;
                    case 2:
                        mediaPlayer = MediaPlayer.create(Qalmas.this,
                                R.raw.tesra_kalima);
                        break;
                    case 3:
                        mediaPlayer = MediaPlayer.create(Qalmas.this,
                                R.raw.chotha_kalima);
                        break;
                    case 4:
                        mediaPlayer = MediaPlayer.create(Qalmas.this,
                                R.raw.panchwan_kalima);
                        break;
                    case 5:
                        mediaPlayer = MediaPlayer.create(Qalmas.this,
                                R.raw.chata_kalima);
                        break;
                }
            }
        }

        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                qlma_ps.setSelected(false);
            }
        });

    }

    private void stopQ() {
        Log.d("Stopping", "media player");

        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void updateView(int qalmaNum) {

        /*stopQ();
        qlma_ps.setSelected(false);*/

        String[] resoTitle = getResources().getStringArray(R.array.qalma_name_list);
        String[] reso = getResources().getStringArray(R.array.qalma_list);
        String[] resoTrans = getResources().getStringArray(R.array.qalma_list_trans);

        getSupportActionBar().setTitle(resoTitle[qalmaNum]);
        tvQalma.setText(reso[qalmaNum]);
        tvQalmaTrans.setText(resoTrans[qalmaNum]);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*stopQ();*/
    }

    /*@Override
    protected void onDestroy() {

        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*stopQ();*/
        Qalmas.this.finish();
        return true;

    }
}
