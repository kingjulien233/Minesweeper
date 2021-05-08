import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author ssss
 * @version 1.0.0
 * @ClassName Mine.java
 * @Description TODO
 * @createTime 2020年 11月 19日 19:56:00
 */
public class Mine {

    public static Timer timer;

    public static JButton[] buttons;
    public static JFrame frame;
    public static JPanel fieldPanel;
    public static JPanel statusPanel;
    public static JButton resetButton;
    public static JLabel statusLabel;

    public static int[] mines;
    public static int[] displays;
    public static int[] flags;

    public static int gameStart;
    public static int gameOver;
    public static int displayBlocks;

    public static final int SIZE_OF_BUTTON = 50;
    public static final int UP_DOWN_SPACE = 90;
    public static final int LEFT_RIGHT_SPACE = 20;

    // 可配置
    public static final int SIZE_OF_FIELD = 16;
    // 可配置
    public static final int SIZE_OF_MINES = 40;
    public static final int SIZE_OF_BLOCKS = SIZE_OF_FIELD * SIZE_OF_FIELD;
    public static final int SIZE_OF_NOT_MINES = SIZE_OF_BLOCKS - SIZE_OF_MINES;

    public static final int LEFT_BORDER = 0;
    public static final int RIGHT_BORDER = SIZE_OF_FIELD - 1;
    public static final int UP_BORDER = 0;
    public static final int DOWN_BORDER = SIZE_OF_FIELD - 1;

    public static final int LEFT_RIGHT_DISTANCE = 1;
    public static final int UP_DOWN_DISTANCE = SIZE_OF_FIELD;

    public static final int MICROSECONDS_PER_SECOND = 1000;
    public static final int SECONDS_PER_MINUTE = 60;

    public static final int NOT_MINE = 0;
    public static final int IS_MINE = 1;

    public static final int NOT_DISPLAY = 0;
    public static final int IS_DISPLAY = 1;

    public static final int NOT_OVER = 0;
    public static final int IS_OVER = 1;

    public static final int NOT_FLAG = 0;
    public static final int IS_FLAG = 1;

    public static final int NOT_START = 0;
    public static final int IS_START = 1;

    public static void main(String[] args) {
        init();
    }

    public static void init() {
        frame = new JFrame();
        statusPanel = new JPanel();
        fieldPanel = new JPanel();
        resetButton = new JButton("\uD83D\uDE0A");
        statusLabel = new JLabel();
        buttons = new JButton[SIZE_OF_BLOCKS];
        for (int i = 0; i < SIZE_OF_BLOCKS; i++) {
            buttons[i] = new JButton();
        }
        reset();
        frame.setTitle("扫雷");
        frame.setBounds(0, 0, SIZE_OF_BUTTON * SIZE_OF_FIELD + LEFT_RIGHT_SPACE, SIZE_OF_BUTTON * SIZE_OF_FIELD + UP_DOWN_SPACE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fieldPanel.setLayout(new GridLayout(SIZE_OF_FIELD, SIZE_OF_FIELD));
        fieldPanel.setBounds(0, SIZE_OF_BUTTON, SIZE_OF_BUTTON * SIZE_OF_FIELD, SIZE_OF_BUTTON * SIZE_OF_FIELD);
        for (int i = 0; i < SIZE_OF_BLOCKS; i++) {
            int position = i;
            buttons[i].addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        leftClick(position);
                    }
                    if (e.getButton() == MouseEvent.BUTTON3) {
                        rightClick(position);
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });
        }
        for (int i = 0; i < SIZE_OF_BLOCKS; i++) {
            fieldPanel.add(buttons[i]);
        }
        frame.add(fieldPanel);

        statusPanel.setLayout(null);
        resetButton.setBounds((SIZE_OF_FIELD * SIZE_OF_BUTTON - SIZE_OF_BUTTON) / 2, 0, SIZE_OF_BUTTON, SIZE_OF_BUTTON);
        statusPanel.add(resetButton);
        resetButton.addActionListener(e -> reset());

        statusLabel.setBounds(0, 0, SIZE_OF_BUTTON, SIZE_OF_BUTTON);
        statusPanel.add(statusLabel);
        frame.add(statusPanel);
        frame.setVisible(true);
    }

    public static void generateMines(int protect) {
        gameStart = IS_START;
        int[] randomNums = new int[SIZE_OF_MINES];
        boolean flag;
        for (int now = 0; now < SIZE_OF_MINES; now++) {
            do {
                flag = false;
                randomNums[now] = (int) (SIZE_OF_BLOCKS * Math.random());
                for (int former = 0; former < now; former++) {
                    if (randomNums[former] == randomNums[now] || randomNums[now] == protect) {
                        flag = true;
                        break;
                    }
                }
            } while (flag);
        }
        for (int i = 0; i < SIZE_OF_MINES; i++) {
            mines[randomNums[i]] = IS_MINE;
        }
        timer(System.currentTimeMillis());
    }

    public static void reset() {
        resetButton.setText("😃");
        mines = new int[SIZE_OF_BLOCKS];
        displays = new int[SIZE_OF_BLOCKS];
        flags = new int[SIZE_OF_BLOCKS];
        displayBlocks = 0;
        gameOver = NOT_OVER;
        gameStart = NOT_START;
        for (int i = 0; i < SIZE_OF_BLOCKS; i++) {
            buttons[i].setText("");
            buttons[i].setBackground(Color.LIGHT_GRAY);
        }
        try {
            timer.cancel();
        } catch (NullPointerException e) {
        }
        statusLabel.setText("00:00:00");
    }

    public static void show(int position) {
        displays[position] = IS_DISPLAY;
        buttons[position].setBackground(Color.GRAY);
        displayBlocks++;
        if (minesAround(position) == 0) {
            int column = position % SIZE_OF_FIELD;
            int row = position / SIZE_OF_FIELD;
            if (column != LEFT_BORDER && mines[position - LEFT_RIGHT_DISTANCE] == NOT_MINE && displays[position - LEFT_RIGHT_DISTANCE] == NOT_DISPLAY) {
                displays[position - LEFT_RIGHT_DISTANCE] = IS_DISPLAY;
                show(position - LEFT_RIGHT_DISTANCE);
            }
            if (column != RIGHT_BORDER && mines[position + LEFT_RIGHT_DISTANCE] == NOT_MINE && displays[position + LEFT_RIGHT_DISTANCE] == NOT_DISPLAY) {
                displays[position + LEFT_RIGHT_DISTANCE] = IS_DISPLAY;
                show(position + LEFT_RIGHT_DISTANCE);
            }
            if (row != UP_BORDER && mines[position - UP_DOWN_DISTANCE] == NOT_MINE && displays[position - UP_DOWN_DISTANCE] == NOT_DISPLAY) {
                displays[position - UP_DOWN_DISTANCE] = IS_DISPLAY;
                show(position - UP_DOWN_DISTANCE);
            }
            if (row != DOWN_BORDER && mines[position + UP_DOWN_DISTANCE] == NOT_MINE && displays[position + UP_DOWN_DISTANCE] == NOT_DISPLAY) {
                displays[position + UP_DOWN_DISTANCE] = IS_DISPLAY;
                show(position + UP_DOWN_DISTANCE);
            }
            if (column != LEFT_BORDER && row != UP_BORDER && mines[position - LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE] == NOT_MINE && displays[position - LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE] == NOT_DISPLAY) {
                displays[position - LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE] = IS_DISPLAY;
                show(position - LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE);
            }
            if (column != LEFT_BORDER && row != DOWN_BORDER && mines[position - LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE] == NOT_MINE && displays[position - LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE] == NOT_DISPLAY) {
                displays[position - LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE] = IS_DISPLAY;
                show(position - LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE);
            }
            if (column != RIGHT_BORDER && row != UP_BORDER && mines[position + LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE] == NOT_MINE && displays[position + LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE] == NOT_DISPLAY) {
                displays[position + LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE] = IS_DISPLAY;
                show(position + LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE);
            }
            if (column != RIGHT_BORDER && row != DOWN_BORDER && mines[position + LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE] == NOT_MINE && displays[position + LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE] == NOT_DISPLAY) {
                displays[position + LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE] = IS_DISPLAY;
                show(position + LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE);
            }
        }
    }

    public static int minesAround(int position) {
        int num = 0;
        int column = position % SIZE_OF_FIELD;
        int row = position / SIZE_OF_FIELD;
        if (column != LEFT_BORDER && mines[position - LEFT_RIGHT_DISTANCE] == IS_MINE) {
            num++;
        }
        if (column != RIGHT_BORDER && mines[position + LEFT_RIGHT_DISTANCE] == IS_MINE) {
            num++;
        }
        if (row != UP_BORDER && mines[position - UP_DOWN_DISTANCE] == IS_MINE) {
            num++;
        }
        if (row != DOWN_BORDER && mines[position + UP_DOWN_DISTANCE] == IS_MINE) {
            num++;
        }
        if (column != LEFT_BORDER && row != UP_BORDER && mines[position - LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE] == IS_MINE) {
            num++;
        }
        if (column != LEFT_BORDER && row != DOWN_BORDER && mines[position - LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE] == IS_MINE) {
            num++;
        }
        if (column != RIGHT_BORDER && row != UP_BORDER && mines[position + LEFT_RIGHT_DISTANCE - UP_DOWN_DISTANCE] == IS_MINE) {
            num++;
        }
        if (column != RIGHT_BORDER && row != DOWN_BORDER && mines[position + LEFT_RIGHT_DISTANCE + UP_DOWN_DISTANCE] == IS_MINE) {
            num++;
        }
        return num;
    }

    public static void fail() {
        gameOver();
        resetButton.setText("☹");
    }

    public static void win() {
        gameOver();
        resetButton.setText("😎");
    }

    public static void gameOver() {
        for (int i = 0; i < SIZE_OF_BLOCKS; i++) {
            if (mines[i] == IS_MINE) {
                buttons[i].setText("💣");
                buttons[i].setBackground(Color.ORANGE);
            } else {
                buttons[i].setText(minesAround(i) + "");
                buttons[i].setBackground(Color.GRAY);
            }
        }
        gameOver = IS_OVER;
        timer.cancel();
    }

    public static void display() {
        for (int i = 0; i < SIZE_OF_BLOCKS; i++) {
            if (displays[i] == IS_DISPLAY && mines[i] == NOT_MINE) {
                buttons[i].setText(minesAround(i) + "");
            }
        }
    }

    public static void leftClick(int choose) {
        if (gameStart == NOT_START) {
            generateMines(choose);
        }
        if (gameOver == NOT_OVER) {
            if (displays[choose] == NOT_DISPLAY) {
                if (mines[choose] == IS_MINE) {
                    fail();
                    buttons[choose].setBackground(Color.RED);
                    return;
                }
                show(choose);
                if (displayBlocks == SIZE_OF_NOT_MINES) {
                    win();
                    return;
                }
                display();
            }
        }
    }

    public static void rightClick(int choose) {
        if (gameOver == NOT_OVER) {
            if (displays[choose] == NOT_DISPLAY) {
                if (flags[choose] == NOT_FLAG) {
                    buttons[choose].setText("\uD83D\uDEA9");
                    flags[choose] = IS_FLAG;
                } else if (flags[choose] == IS_FLAG) {
                    buttons[choose].setText("");
                    flags[choose] = NOT_FLAG;
                }
            }
        }
    }

    public static String timeFormat(long time) {
        // 将日期中小于10的数前补0，如”9:1:4“应为”09:01:04“
        String str;
        if (time < 10) {
            str = "0" + time;
        } else {
            str = String.valueOf(time);
        }
        return str;
    }

    public static String printTime(long time) {
        return timeFormat(time / MICROSECONDS_PER_SECOND / SECONDS_PER_MINUTE) + ":" + timeFormat(time / MICROSECONDS_PER_SECOND % SECONDS_PER_MINUTE) + ":" + timeFormat(time % MICROSECONDS_PER_SECOND / 10);
    }

    public static void timer(long startTime) {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                statusLabel.setText(printTime(currentTime - startTime));
            }
        };
        timer.schedule(task, 0, 10);
    }
}