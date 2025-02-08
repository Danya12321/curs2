package com.example.kurs_vasilev;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

/*
    private CarouselAdapter carouselAdapter;
*/
    private ImageButton closeButton;
    private ImageButton openButton;
    private ImageButton SwitchFlorUp;
    private ImageButton SwitchFlorDown;
    private TextView FlorNamber;
    private ConstraintLayout containerBigMap;
    private ConstraintLayout containerBigMap2;
    private ConstraintLayout containerBigMap3;
    private ConstraintLayout containerBigMap4;
    private ConstraintLayout containerSmallMap;
    private ConstraintLayout containerSmallMap2;
    private ConstraintLayout containerSmallMap3;
    private ConstraintLayout containerSmallMap4;
    private ConstraintLayout infoPanel;
    private ImageView infoF1;
    private ImageView infoF2;
    private ImageView infoF3;
    private ImageView infoF4;
    private ImageButton zoomInButton;
    private ImageButton zoomOutButton;
    private TextView zoomValueTextView;
    private float scaleFactor = 1.0f;
    private int currentFloor = 1;
    private boolean isNewsVisible = true;
    private ImageButton reboot;
    private ImageButton openInfo;
    private ImageButton closeInfo;
    private HorizontalScrollView carouselRecyclerView;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupListeners();

        float scaleFactors = ZoomableConstraintLayout.scaleFactors;
        Data.connectionToDataBase.start();
        try {
            Data.connectionToDataBase.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Data.getNews.start();
        Data.getCabinets.start();


        try {
            Data.getCabinets.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            Data.getNews.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        for (Cabinets el : Data.cabinets) {
            Data.getSchedule.setCabinetNumber(el.number);
            Data.getSchedule.start();
            try {
                Data.getSchedule.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (Data.schedules.size() != 0){
                el.schedules = (ArrayList<Schedule>) Data.schedules.clone();
            }
            Data.getSchedule = new Data.GetSchedule();
        }

        ArrayList imageViews = new ArrayList();
        imageViews.add(R.id.imageView1);
        imageViews.add(R.id.imageView2);
        imageViews.add(R.id.imageView3);
        imageViews.add(R.id.imageView4);
        for (int i = 0; i < imageViews.size(); i++){
            Picasso.get().load(Data.news.get(i).url).into((ImageView) findViewById((int)imageViews.get(i)));
        }


        getInfoOfCabinet();
        getInfoOfCabinetB();


    }

    private void initViews() {
        carouselRecyclerView = findViewById(R.id.carouselRecyclerView);
        containerSmallMap = findViewById(R.id.containerSmallMap);
        containerSmallMap2 = findViewById(R.id.containerSmallMap2);
        containerSmallMap3 = findViewById(R.id.containerSmallMap3);
        containerSmallMap4 = findViewById(R.id.containerSmallMap4);
        containerBigMap = findViewById(R.id.containerBigMap);
        containerBigMap2 = findViewById(R.id.containerBigMap2);
        containerBigMap3 = findViewById(R.id.containerBigMap3);
        containerBigMap4 = findViewById(R.id.containerBigMap4);
        infoPanel = findViewById(R.id.infoPanel);
        closeButton = findViewById(R.id.closeButton);
        openButton = findViewById(R.id.openButton);
        SwitchFlorUp = findViewById(R.id.SwitchFlorUp);
        SwitchFlorDown = findViewById(R.id.SwitchFlorDown);
        FlorNamber = findViewById(R.id.FlorNamber);
        zoomInButton = findViewById(R.id.Plas);
        zoomOutButton = findViewById(R.id.Min);
        zoomValueTextView = findViewById(R.id.ZoomValue);
        reboot = findViewById(R.id.reboot);
        openInfo = findViewById(R.id.openInfo);
        closeInfo = findViewById(R.id.closeInfo);
        infoF1 = findViewById(R.id.infoFlor1);
        infoF2 = findViewById(R.id.infoFlor2);
        infoF3 = findViewById(R.id.infoFlor3);
        infoF4 = findViewById(R.id.infoFlor4);

    }
    public void switchFloorUp() {
        if (currentFloor < 4) {
            currentFloor++;
            updateMapVisibilityBasedOnNewsState();
            namberFlor();
        }
    }
    public void switchFloorDown() {
        if (currentFloor > 1) {
            currentFloor--;
            updateMapVisibilityBasedOnNewsState();
            namberFlor();
        }
    }
    private void activateBigMap() {
        hideAllMaps();  // Сначала скрываем все карты

        // Отображаем большую карту в зависимости от текущего этажа
        switch (currentFloor) {
            case 1:
                containerBigMap.setVisibility(View.VISIBLE);
                infoF1.setVisibility(View.VISIBLE);
                break;
            case 2:
                containerBigMap2.setVisibility(View.VISIBLE);
                infoF2.setVisibility(View.VISIBLE);
                break;
            case 3:
                containerBigMap3.setVisibility(View.VISIBLE);
                infoF3.setVisibility(View.VISIBLE);
                break;
            case 4:
                containerBigMap4.setVisibility(View.VISIBLE);
                infoF4.setVisibility(View.VISIBLE);
                break;
            default:
                containerBigMap.setVisibility(View.VISIBLE); // Показываем первую карту, если этаж не определен
                infoF1.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void activateSmallMap() {
        hideAllMaps();  // Сначала скрываем все карты

        // Отображаем маленькую карту в зависимости от текущего этажа
        switch (currentFloor) {
            case 1:
                containerSmallMap.setVisibility(View.VISIBLE);
                infoF1.setVisibility(View.VISIBLE);
                break;
            case 2:
                containerSmallMap2.setVisibility(View.VISIBLE);
                infoF2.setVisibility(View.VISIBLE);
                break;
            case 3:
                containerSmallMap3.setVisibility(View.VISIBLE);
                infoF3.setVisibility(View.VISIBLE);
                break;
            case 4:
                containerSmallMap4.setVisibility(View.VISIBLE);
                infoF4.setVisibility(View.VISIBLE);
                break;
            default:
                containerSmallMap.setVisibility(View.VISIBLE); // Показываем первую карту, если этаж не определен
                infoF1.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void hideAllMaps() {
        // Скрыть все карты
        containerSmallMap.setVisibility(View.GONE);
        containerSmallMap2.setVisibility(View.GONE);
        containerSmallMap3.setVisibility(View.GONE);
        containerSmallMap4.setVisibility(View.GONE);
        containerBigMap.setVisibility(View.GONE);
        containerBigMap2.setVisibility(View.GONE);
        containerBigMap3.setVisibility(View.GONE);
        containerBigMap4.setVisibility(View.GONE);
        infoF1.setVisibility(View.GONE);
        infoF2.setVisibility(View.GONE);
        infoF3.setVisibility(View.GONE);
        infoF4.setVisibility(View.GONE);

    }
    private void updateMapVisibilityBasedOnNewsState() {
        if (isNewsVisible) {
            activateSmallMap();  // Активируем маленькую карту, если News открыты
        } else {
            activateBigMap();  // Активируем большую карту, если News закрыты
        }

    }
    private void namberFlor(){
        switch (currentFloor) {
            case 1:
                FlorNamber.setText("1");
                break;
            case 2:
                FlorNamber.setText("2");
                break;
            case 3:
                FlorNamber.setText("3");
                break;
            case 4:
                FlorNamber.setText("4");
                break;
            default:
                FlorNamber.setText("1");
                break;
        }
    }
    // Кнопки
    private void setupListeners() {

        openButton.setOnClickListener(v -> {
            carouselRecyclerView.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.VISIBLE);
            openButton.setVisibility(View.GONE);
            isNewsVisible = true;
            activateSmallMap();

        });

        closeButton.setOnClickListener(v -> {
            carouselRecyclerView.setVisibility(View.GONE);

            openButton.setVisibility(View.VISIBLE);
            closeButton.setVisibility(View.GONE);
            isNewsVisible = false;
            activateBigMap();

        });

        openInfo.setOnClickListener(v -> {
            infoPanel.setVisibility(View.VISIBLE);
            openInfo.setVisibility(View.GONE);

        });
        closeInfo.setOnClickListener(v -> {
            infoPanel.setVisibility(View.GONE);
            openInfo.setVisibility(View.VISIBLE);
        });


        reboot.setOnClickListener(v -> {

            scaleFactor=1.0f;
            changeScaleFactor(1.0f);
            ZoomableConstraintLayout.scaleFactors = 1.0f;


            containerSmallMap.setScaleX(1.0f);
            containerSmallMap.setScaleY(1.0f);
            containerSmallMap.setTranslationX(0);
            containerSmallMap.setTranslationY(0);

            containerSmallMap2.setScaleX(1.0f);
            containerSmallMap2.setScaleY(1.0f);
            containerSmallMap2.setTranslationX(0);
            containerSmallMap2.setTranslationY(0);

            containerSmallMap3.setScaleX(1.0f);
            containerSmallMap3.setScaleY(1.0f);
            containerSmallMap3.setTranslationX(0);
            containerSmallMap3.setTranslationY(0);

            containerSmallMap4.setScaleX(1.0f);
            containerSmallMap4.setScaleY(1.0f);
            containerSmallMap4.setTranslationX(0);
            containerSmallMap4.setTranslationY(0);

            containerBigMap.setScaleX(1.0f);
            containerBigMap.setScaleY(1.0f);
            containerBigMap.setTranslationX(0);
            containerBigMap.setTranslationY(0);

            containerBigMap2.setScaleX(1.0f);
            containerBigMap2.setScaleY(1.0f);
            containerBigMap2.setTranslationX(0);
            containerBigMap2.setTranslationY(0);

            containerBigMap3.setScaleX(1.0f);
            containerBigMap3.setScaleY(1.0f);
            containerBigMap3.setTranslationX(0);
            containerBigMap3.setTranslationY(0);

            containerBigMap4.setScaleX(1.0f);
            containerBigMap4.setScaleY(1.0f);
            containerBigMap4.setTranslationX(0);
            containerBigMap4.setTranslationY(0);






        });

        SwitchFlorUp.setOnClickListener(v -> switchFloorUp());
        SwitchFlorDown.setOnClickListener(v -> switchFloorDown());
        setupZoomControls();
    }

    private void setupZoomControls() {
        zoomInButton.setOnClickListener(v -> {
            changeScaleFactor(1.25f); // Увеличиваем масштаб на 25%
        });

        zoomOutButton.setOnClickListener(v -> {
            changeScaleFactor(0.75f); // Уменьшаем масштаб на 20%, что эквивалентно уменьшению на 25% от предыдущего значения
    });
    }
    private void changeScaleFactor(float factor) {
        scaleFactor *= factor;
        scaleFactor = Math.max(0.75f, Math.min(scaleFactor, 1.5f)); // Ограничиваем масштаб

        // Применяем масштаб к соответствующим контейнерам
        containerBigMap.setScaleX(scaleFactor);
        containerBigMap2.setScaleX(scaleFactor);
        containerBigMap3.setScaleX(scaleFactor);
        containerBigMap4.setScaleX(scaleFactor);
        containerBigMap.setScaleY(scaleFactor);
        containerBigMap2.setScaleY(scaleFactor);
        containerBigMap3.setScaleY(scaleFactor);
        containerBigMap4.setScaleY(scaleFactor);
        containerSmallMap.setScaleX(scaleFactor);
        containerSmallMap2.setScaleX(scaleFactor);
        containerSmallMap3.setScaleX(scaleFactor);
        containerSmallMap4.setScaleX(scaleFactor);
        containerSmallMap.setScaleY(scaleFactor);
        containerSmallMap2.setScaleY(scaleFactor);
        containerSmallMap3.setScaleY(scaleFactor);
        containerSmallMap4.setScaleY(scaleFactor);

        // Обновление текста в TextView
        int percentage = (int) (scaleFactor * 100);
        zoomValueTextView.setText(percentage + "%");
    }

    public void getInfoOfCabinet(){
        Context context = this; // или getActivity() в фрагменте, this в Activity
        for (int i = 0; i < Data.cabinets.size(); i++){
            String cabinetName = Data.cabinets.get(i).number;
            String buttonName = "button" + cabinetName;
            int resId = context.getResources().getIdentifier(buttonName , "id", context.getPackageName());
           Log.e("S", "getInfoOfCabinet: " + resId + "\t" + "button" + Data.cabinets.get(i).number);
            ImageButton myButton = findViewById(resId);

            myButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int buttonId = v.getId();
                    String buttonIdString = getResources().getResourceName(buttonId).replaceAll("com.example.kurs_vasilev:id/button", "");
                    Log.e("Ss", "onClick: " + buttonId + " " + buttonIdString);
                    Cabinets t = Data.cabinets.get(0);
                    for (Cabinets c : Data.cabinets) {
                        //Log.e("Ss", "\tonClick: |" + c.number + "| == |" + buttonIdString + "| -> " + (c.number.equals(buttonIdString)));
                        if (c.number.equals(buttonIdString)){
                            t = c;
                            break;
                        }
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);

                    View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null);
                    builder.setView(dialogView);

                    TextView textView = dialogView.findViewById(R.id.title);
                    TextView descriptionTextView = dialogView.findViewById(R.id.description);

                    textView.setText("Кабинет " + t.number);

                    String tt = "";
                    if (t.schedules != null){
                        for (Schedule s : t.schedules){

                            tt += s.getFormattedSchedule() + "\n";

                        }
                    }
                    else {
                        tt = "пошел нахуй" + t.number;
                    }
                    descriptionTextView.setText(tt);

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
            });
        }

    }
    public void getInfoOfCabinetB() {
        Context context = this; // или getActivity() в фрагменте, this в Activity
        for (int i = 0; i < Data.cabinets.size(); i++) {
            int resId = context.getResources().getIdentifier("button" + Data.cabinets.get(i).number + "B", "id", context.getPackageName());
            ImageButton myButton = findViewById(resId);

            if (myButton != null) {
                myButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        int buttonId = v.getId();
                        String buttonIdString = getResources().getResourceName(buttonId).replaceAll("com.example.kurs_vasilev:id/button", "");
                        String cabinetNumber = buttonIdString.substring(0, buttonIdString.length() - 1); // Удаляем букву "B" из buttonIdString
                        Cabinets t = Data.cabinets.get(0);
                        for (Cabinets c : Data.cabinets) {
                            if (c.number.equals(cabinetNumber)) {
                                t = c;
                                break;
                            }
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);

                        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_custom, null);
                        builder.setView(dialogView);

                        TextView textView = dialogView.findViewById(R.id.title);
                        TextView descriptionTextView = dialogView.findViewById(R.id.description);

                        textView.setText("Кабинет " + t.number);
                        String tt = "";
                        if (t.schedules != null){
                            for (Schedule s : t.schedules){
                                tt += s.getFormattedSchedule() + "\n";

                            }
                        }
                        else {
                            tt = "пошел нахуй" + t.number;
                        }
                        descriptionTextView.setText(tt);

                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                });
            } else {
                Log.e("B", "Button not found for cabinet: " + Data.cabinets.get(i).number);
            }
        }
    }



}
