package com.pharaohofra.sper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.gridlayout.widget.GridLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;

import com.pharaohofra.sper.databinding.ActivityMainBinding;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import static com.pharaohofra.sper.constants.Constants.BOMB;
import static com.pharaohofra.sper.constants.Constants.MYLOG_TEG;
import static com.pharaohofra.sper.constants.Constants.NO_BOMB;

public class MainActivity extends AppCompatActivity {


    private static final int CELLS_COUNT_X = 5;
    private static final int CELLS_COUNT_Y = 5;

    private static int cellsCount = CELLS_COUNT_X * CELLS_COUNT_Y;
    private ActivityMainBinding binding;
    private int bombCaunter;
    private boolean clic;
    private boolean isMarked = false;

    private String noFlag = "noFlag";
    private String flag = "flag";

    private int pushButtonId;
    private ArrayList<Integer> celes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        schowBord(binding.bordGridLayout);

        celes = new ArrayList<>();
        binding.clicImageButton.setOnClickListener(v -> {
            if (clic) {
                clic = false;
                binding.clicImageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.flaged));
            } else {
                clic = true;
                binding.clicImageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.closed));
            }
        });

        binding.newGameButton.setOnClickListener(v -> {
            cellsCount = CELLS_COUNT_X * CELLS_COUNT_Y;
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            ///TODO
        });

    }

    private void schowBord(GridLayout gridLayout) {
        while (cellsCount > 0) {
            ImageView imageView = eadToGridLayout(gridLayout, R.drawable.closed, cellsCount);
            imageView.requestLayout();
            setSize(imageView);
            imageView.setOnClickListener(v -> {
                Log.e(MYLOG_TEG, "imageView.getTag().toString()" + imageView.getTag().toString());
                receiveClick(imageView);
                pushButtonId = imageView.getId();

                celes.clear();
                Log.e(MYLOG_TEG, " imageView.getId() =  " + pushButtonId);

                celes.add(pushButtonId + CELLS_COUNT_X);
                celes.add(pushButtonId - CELLS_COUNT_X);
                celes.add(pushButtonId - 1);
                celes.add(pushButtonId + 1);

                celes.add(pushButtonId + CELLS_COUNT_X + 1);
                celes.add(pushButtonId + CELLS_COUNT_X - 1);
                celes.add(pushButtonId - CELLS_COUNT_X + 1);
                celes.add(pushButtonId - CELLS_COUNT_X - 1);

                for (int id : celes) {
                    if (inBounds(id) && id % CELLS_COUNT_X != 0) {
                        ImageView imageViewTmp = findViewById(id);
                        receiveClick(imageViewTmp);
                    }
                }
                //TODO записат кодинаты соседних клеток
            });
            cellsCount--;
        }

        binding.timeTextView.setText(String.valueOf(bombCaunter));


    }

    private ImageView eadToGridLayout(GridLayout gridLayout, int imageResource, int id) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(imageResource);
        imageView.setId(id);
        imageView.setBackground(ContextCompat.getDrawable(this, R.drawable.blak_boder));
        imageView.setClickable(true);
        setContentDescriptionAndTag(imageView);
        gridLayout.addView(imageView);
        return imageView;
    }

    private void setContentDescriptionAndTag(ImageView imageView) {
        imageView.setContentDescription(noFlag);
        Random rnd = new Random();
        boolean isMine = rnd.nextInt(100) < 30;
        if (isMine) {
            Log.e(MYLOG_TEG, "isMine ");
            bombCaunter++;
            Log.e(MYLOG_TEG, "bombCaunter =  " + bombCaunter);

            imageView.setTag(BOMB);
        } else {
            imageView.setTag(NO_BOMB);

        }
    }

    private void setSize(ImageView imageView) {
        Display display = getWindowManager().getDefaultDisplay();
        imageView.getLayoutParams().height = (display.getWidth() * 18) / 100;
        imageView.getLayoutParams().width = (display.getWidth() * 18) / 100;
    }


    public int receiveClick(ImageView imageView) {

        ///Нет смысла обрабатывать клики по уже открытым полям
        if (clic && imageView.getContentDescription() == noFlag) {
            ///Здесь обработаем щелчки левой кнопкой
            ///Заметим, что щёлкать левой кнопкой по флагам
            ///абсолютно бессмысленно

            ///Открываем клетку
            imageView.setClickable(false);

            if ((int) imageView.getTag() == BOMB) {
                ///Если это была мина, меняем состояние
                ///на взорванную и передаём сигнал назад
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.bombed));
                return BOMB;
            }

            if ((int) imageView.getTag() == NO_BOMB) {
                ///Если мы попали в нолик, нужно открыть
                ///Все соседние ячейки. Этим займётся GUI :)
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.zero));
                return NO_BOMB;
            }

        } else if (!clic) {
            ///В любой ситуации, щелчок правой кнопкой
            ///либо снимает отметку, либо ставит её
            if (imageView.getContentDescription() == noFlag) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.flaged));
                imageView.setContentDescription(flag);
                binding.timeTextView.setText(String.valueOf(bombCaunter++));


            } else if (imageView.getContentDescription() == flag) {
                imageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.closed));
                imageView.setContentDescription(noFlag);
                binding.timeTextView.setText(String.valueOf(bombCaunter--));


            }
        }

        return NO_BOMB;
    }

    private boolean inBounds(int id) {
        if (id < 1|| id >= CELLS_COUNT_X * CELLS_COUNT_Y)
            return false;
        else
            return true;
    }


    // Получение соседних клеток
//
//    public static Cell[][] generate() {
//        {
//            Random rnd = new Random();
//
//            ///Карта, которую мы вернём
//            Cell[] map = new Cell[CELLS_COUNT_X];
//
//            ///Матрица с пометками, указывается кол-во мин рядом с каждой клеткой
//            int[] []counts = new int[CELLS_COUNT_X][CELLS_COUNT_Y];
//
//            for (int x = 0; x < CELLS_COUNT_X; x++) {
//                for (int y = 0; y < CELLS_COUNT_Y; y++) {
//                    boolean isMine = rnd.nextInt(100) < 15;
//
//                    if (isMine) {
//                        map[x][y] = new Cell(x * CELL_SIZE, y * CELL_SIZE, MINE);
//
//                        for (int i = -1; i < 2; i++) {
//                            for (int j = -1; j < 2; j++) {
//                                try {
//                                    if (map[x + i][y + j] == null) {
//                                        ///Если клетки там ещё нет, записываем сведение
//                                        ///о мине в матрицу
//                                        counts[x + i][y + j] += 1;
//                                    } else {
//                                        ///Если есть, говорим ей о появлении мины
//                                        map[x + i][y + j].incNearMines();
//                                    }
//                                } catch (java.lang.ArrayIndexOutOfBoundsException e) {
//                                    //ignore
//                                }
//                            }
//                        }
//                    } else {
//                        ///Если была сгенерирована обычная клетка, создаём её, со
//                        ///state равным значению из матрицы
//                        map[x][y] = new Cell(x * CELL_SIZE, y * CELL_SIZE, counts[x][y]);
//                    }
//                }
//            }
//
//            return map;
//        }
//    }
}
