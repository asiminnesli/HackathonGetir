package com.asiminnesli.hackathon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{
    String url = "https://getir-bitaksi-hackathon.herokuapp.com/getElements";
    String httpBack;
    PostClass post = new PostClass();
    TextView textView;
    Button button;
    String[][] elementsStringArray = new String[100][100];
    int elementsNumber=0,screenWidth,screenHeight,clickCounter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeUIElements();


    }
    private void initializeUIElements() {

        Display display = getWindowManager().getDefaultDisplay();
        Point size=new Point();
        display.getSize(size);
        screenWidth=size.x;
        screenHeight=size.y;

        textView=(TextView)findViewById(R.id.textView);
        button=(Button)findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Post().execute();
            }
        });
    }

    private void ErrorFunction() {
        Toast.makeText(getApplicationContext(),"BİR HATA İLE KARŞILAŞILDI....\n UYGULAMA KAPATILIYOR....", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }


    class Post extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... unused) {
            clickCounter=0;
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("email", "asimmurat17@gmail.com"));
            params.add(new BasicNameValuePair("name", "asim"));
            params.add(new BasicNameValuePair("gsm", "05387721691"));

            httpBack = post.httpPost(url,"POST",params,20000);
            Log.d("httpBack",httpBack);
            return null;
        }

        protected void onPostExecute(Void unused) {

            try {

                JSONObject jsonObject = new JSONObject(httpBack);

                String code = jsonObject.getString("code");
                String msg = jsonObject.getString("msg");

                Log.d("code degeri -->", code);
                Log.d("msg degeri -->", msg);

                int codeCheck=Integer.valueOf(code);

                if(codeCheck!=0){
                    ErrorFunction();
                    Log.d("code 0 degil code->", codeCheck+"");

                }else{
                    String invitecode = jsonObject.getString("inviteCode");

                    Log.d("invitecode degeri -->", invitecode);

                    JSONArray elements = jsonObject.getJSONArray("elements");
                    for (int i = 0; i < elements.length(); i++) {
                        JSONObject elementsObject = elements.getJSONObject(i);
                        String type = elementsObject.getString("type");
                        elementsStringArray[i][0] = type;
                        if (type.equals("circle")) {

                            int xPosition = elementsObject.getInt("xPosition");
                            int yPosition = elementsObject.getInt("yPosition");
                            int r = elementsObject.getInt("r");
                            String color = elementsObject.getString("color");

                            elementsStringArray[i][1] = Integer.toString(xPosition);
                            elementsStringArray[i][2] = Integer.toString(yPosition);
                            elementsStringArray[i][3] = Integer.toString(r);
                            elementsStringArray[i][4] = color;


                        } else if (type.equals("rectangle")) {

                            int xPosition = elementsObject.getInt("xPosition");
                            int yPosition = elementsObject.getInt("yPosition");
                            int width = elementsObject.getInt("width");
                            int height = elementsObject.getInt("height");
                            String color = elementsObject.getString("color");

                            elementsStringArray[i][1] = Integer.toString(xPosition);
                            elementsStringArray[i][2] = Integer.toString(yPosition);
                            elementsStringArray[i][3] = Integer.toString(width);
                            elementsStringArray[i][4] = Integer.toString(height);
                            elementsStringArray[i][5] = color;
                        }

                        if (i > elementsNumber) {
                            elementsNumber = i;
                        }


                    }
                    setContentView(new MyView(MainActivity.this));

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                ErrorFunction();
                e.printStackTrace();

            }


        }



    }

    public class MyView extends View {
        Paint paint=null;

        public MyView(Context context)
        {
            super(context);
            paint=new Paint();

        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);

            canvas.drawPaint(paint);

            paint.setColor(Color.parseColor("#000000"));
            canvas.drawText("ELEMENTS NUMBER-->" + Integer.toString(elementsNumber+1), 10, 25, paint);


            paint.setColor(Color.parseColor("#FF0000"));
            RectF AgainButton=new RectF(50,screenHeight-200,screenWidth-50,screenHeight-100);
            canvas.drawRect(AgainButton,paint);

            paint.setColor(Color.parseColor("#0000FF"));
            canvas.drawText("TEKRAR POST ETME BUTONU",AgainButton.centerX(),AgainButton.centerY(),paint);



            for (int z = 0; z <= elementsNumber; z++) {

                paint.setColor(Color.parseColor("#000000"));


                if(elementsStringArray[z][0].equals("circle")) {

                    String color="#"+elementsStringArray[z][4];

                    paint.setColor(Color.parseColor(color));
                    canvas.drawCircle(Float.parseFloat(elementsStringArray[z][1]),Float.parseFloat(elementsStringArray[z][2]),Float.parseFloat(elementsStringArray[z][3]), paint);

                    paint.setColor(Color.parseColor("#000000"));
                    canvas.drawText((z+1)+"",Float.parseFloat(elementsStringArray[z][1]),Float.parseFloat(elementsStringArray[z][2]),paint);

                }else if(elementsStringArray[z][0].equals("rectangle")) {

                    String color="#"+elementsStringArray[z][5];

                    paint.setColor(Color.parseColor(color));
                    RectF myRect=new RectF(Float.parseFloat(elementsStringArray[z][1]),Float.parseFloat(elementsStringArray[z][2]),Float.parseFloat(elementsStringArray[z][1])+Float.parseFloat(elementsStringArray[z][3]),Float.parseFloat(elementsStringArray[z][2])+Float.parseFloat(elementsStringArray[z][4]));
                    canvas.drawRect(myRect,paint);

                    paint.setColor(Color.parseColor("#000000"));
                    canvas.drawText((z+1)+"",myRect.centerX(),myRect.centerY(),paint);

                }
            }



        }

        }
    public boolean onTouchEvent(MotionEvent event) {

        float touchX = event.getX();
        float touchY = event.getY();

            if (touchX > 50 && touchX < screenWidth - 50 && touchY > screenHeight - 200 && touchY < screenHeight - 100) {
                clickCounter++;
                if(clickCounter==2) {
                    Toast.makeText(getApplicationContext(), "....TIKLANDI.....", Toast.LENGTH_SHORT).show();
                    new Post().execute();
                }
        }
            return true;
    }
}

