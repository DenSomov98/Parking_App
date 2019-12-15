package sample.utils;

import sample.utils.PairIJ;

import java.util.ArrayList;
import java.util.List;

public class WaveAlg {
    int width;
    int height;
    int wall = 99;
    int[][] map;
    ArrayList<PairIJ> wave = new ArrayList<PairIJ>();

    public ArrayList<PairIJ> getWave() {
        return wave;
    }

    public WaveAlg(int[][] map) {
        this.width = map.length;
        this.height = map[0].length;
        this.map = map;
    }

    public void block(int x, int y) {
        //заполняем карту препятствиями
        map[y][x] = wall;
    }

    public void findPath(int x, int y, int nx, int ny) {
        if (map[y][x] == wall || map[ny][nx] == wall) {
            System.out.println("Вы выбрали препятствие");
            return;
        }

        // волновой алгоритм поиска пути (заполнение значений достижимости)
        // начинаЯ от конца пути
        int[][] cloneMap = clone(map);
        List<PairIJ> oldWave = new ArrayList<PairIJ>();
        oldWave.add(new PairIJ(nx, ny));
        int nstep = 0;
        map[ny][nx] = nstep;

        int[] dx = { 0, 1, 0, -1 };
        int[] dy = { -1, 0, 1, 0 };

        while (oldWave.size() > 0) {
            nstep++;
            wave.clear();
            for (PairIJ i : oldWave) {
                for (int d = 0; d < 4; d++) {
                    nx = i.getI() + dx[d];
                    ny = i.getJ() + dy[d];

                    if (map[ny][nx] == -1) {
                        wave.add(new PairIJ(nx, ny));
                        map[ny][nx] = nstep;
                    }
                }
            }
            oldWave = new ArrayList<PairIJ>(wave);
        }
        // traceOut(map); //посмотреть распоространие волны

        // волновой алгоритм от начала
        boolean flag = true;
        wave.clear();
        wave.add(new PairIJ(x, y));
        while (map[y][x] != 0) {
            flag = true;
            for (int d = 0; d < 4; d++) {
                nx = x + dx[d];
                ny = y + dy[d];
                if (map[y][x] - 1 == map[ny][nx]) {
                    x = nx;
                    y = ny;
                    wave.add(new PairIJ(x, y));
                    flag = false;
                    break;
                }
            }
            if (flag) {
                System.out.println("Пути нет");
                break;
            }
        }

        map = cloneMap;

        for (PairIJ i : wave) {
            map[i.getJ()][i.getI()] = 0;
        }
    }

    public void waveOut() //вывод координат пути
    {
        for (PairIJ i : wave) {
            System.out.println("x = " + i.getJ() + ", y = " + i.getI());
        }
    }

    public void traceOut() // вывод таблицы
    {
        String m = null;
        System.out.print("   ");
        for (int i = 0; i < height; i++) // вывод верхней нумерации
        {
            System.out.print(i > 9 ? i + " " : i + "  ");
        }
        System.out.println();
        for (int i = 0; i < width; i++) {
            m = i > 9 ? i + " " : i + "  "; // вывод боковой нумерации
            for (int j = 0; j < height; j++) {
                m += map[i][j] > 9 || map[i][j] < 0 ? map[i][j] + " " : map[i][j] + "  ";
            }
            System.out.println(m);
        }
    }

    private int[][] clone(int[][] map) {
        int[][] cloneMap = new int[width][height];
        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                cloneMap[i][j] = map[i][j];
        return cloneMap;
    }
}

