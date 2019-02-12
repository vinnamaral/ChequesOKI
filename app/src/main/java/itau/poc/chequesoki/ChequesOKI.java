package itau.poc.chequesoki;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.okibrasil.android.apichequeoki.InterfaceCapturaCheque;
import com.okibrasil.android.apichequeoki.classes.ResultadoCapturaCheque;

public class ChequesOKI extends Activity {

    private static final String stringAppReceiver = "com.okibrasil.android.RESULTADO_CAPTURA";

    private TextView results;

    private ImageView imgPb;
    private ImageView imgTons;

    private Button btnSave;
    private Button btnLerCheque;

    private ResultadoCapturaCheque resultadoCapturaCheque;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_chequesoki);

        results = (TextView)findViewById(R.id.txtresult);

        imgPb = (ImageView) findViewById(R.id.img_chequepb);
        imgTons = (ImageView) findViewById(R.id.img_chequetons);

        btnSave = ((Button)findViewById(R.id.btnsave));
        btnLerCheque = ((Button)findViewById(R.id.btnlercheque));


        btnSave.setEnabled(false);

        btnLerCheque.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                InterfaceCapturaCheque.InicializarAPI(getApplicationContext());

//				ResultadoCapturaCheque result = InterfaceCapturaCheque.IdentificarCheque(30);

                ApiReceiver apiReceiver = new ApiReceiver();
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(stringAppReceiver);
                ChequesOKI.this.registerReceiver(apiReceiver, intentFilter);

//				append("cod:  " + result.getCodigoErro());
//				append("cmc7: " + result.getCodigoCMC7());
//				
//				String path = null;
//				
//				path = saveImg(result.getImagemBinarizada(), "chequeColor.jpg");
//				append("img color: " + path);
//				
//				path = saveImg(result.getImagemTonsCinza(), "chequePB.jpg");
//				append("img pb: " + path);

//				InterfaceCapturaCheque.FinalizarAPI();
            }
        });

        btnSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String path = null;

                    final String time = "_"+System.currentTimeMillis();

                    append("\nsaved to:");

                    path = saveImg(resultadoCapturaCheque.getImagemBinarizada(), "oki"+time+"_chequePB.jpg");
                    append("path img pb: " + path);

                    path = saveImg(resultadoCapturaCheque.getImagemTonsCinza(), "oki"+time+"_chequeTonsCinza.jpg");
                    append("path img tonscinza: " + path);

                    String result = "cod retorno: " + resultadoCapturaCheque.getCodigoErro() +
                            "\nmsg retorno: " + resultadoCapturaCheque.getMensagemErro() +
                            "\ncmc7: " + resultadoCapturaCheque.getCodigoCMC7() +
                            "\ncmc7 score: " + resultadoCapturaCheque.getScoreReconhecimento();
                    path = saveImg(result.getBytes(), "oki"+time+"_result.txt");
                    append("path result: " + path);

                    btnSave.setEnabled(false);
                }
                catch(Exception ignore){}
            }
        });
    }

    private class ApiReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context contextReceiver, Intent intentReceiver) {
            if (intentReceiver.getAction().equals(stringAppReceiver))  {
                ResultadoCapturaCheque result = (ResultadoCapturaCheque) intentReceiver.getSerializableExtra("ResultadoCapturaCheque");

                append("cod:  " + result.getCodigoErro());
                append("msg:  " + result.getMensagemErro());
                append("score: " + result.getScoreReconhecimento());
                append("cmc7: " + result.getCodigoCMC7());

//				String path = null;
//				
//				final String time = "_"+System.currentTimeMillis();

                {
                    Bitmap bmpPb = BitmapFactory.decodeByteArray(result.getImagemBinarizada(), 0, result.getImagemBinarizada().length);
                    imgPb.setImageBitmap(bmpPb);
                }
                {
                    Bitmap bmpTons = BitmapFactory.decodeByteArray(result.getImagemTonsCinza(), 0, result.getImagemTonsCinza().length);
                    imgTons.setImageBitmap(bmpTons);
                }

                resultadoCapturaCheque = result;

//				path = saveImg(result.getImagemBinarizada(), "oki"+time+"_chequePB.jpg");
//				append("img pb: " + path);
//				
//				path = saveImg(result.getImagemTonsCinza(), "oki"+time+"_chequeTonsCinza.jpg");
//				append("img tonscinza: " + path);

                InterfaceCapturaCheque.FinalizarAPI();

                btnSave.setEnabled(true);
            }
        }
    }






    final String saveImg(byte[] img, String file) {
        try {
            File myFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + file);
            FileOutputStream fOut = new FileOutputStream(myFile);
            fOut.write(img);
            fOut.flush();
            fOut.close();
            return myFile.getAbsolutePath();
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    final void append(final String s) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                results.append(s+"\n");
            }
        });
    }



}
