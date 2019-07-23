package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

public class DrawAlgorithms {
    private GraphicsContext gc;
    private Color background;

    public static final String ALG_DDA = "ЦДА";
    public static final String ALG_BDA = "Брезенхем вещественный";
    public static final String ALG_BIA = "Брезенхем целый";
    public static final String ALG_BSA = "Брезенхем без ступенек";
    public static final String ALG_WOO = "Ву";
    public static final String ALG_LIB = "Библиотечный";

    public DrawAlgorithms(GraphicsContext gc, Color background) {
        this.gc = gc;
        this.background = background;
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    private static int round(float value) {
        return (int) (value + 0.5f);
    }

    private static int sign(int value) {
        if (value == 0) {
            return 0;
        }
        return value > 0 ? 1 : -1;
    }

    private static boolean isPoint(int x0, int y0, int xe, int ye) {
        return x0 == xe && y0 == ye;
    }

    private static int getItself(int itself, int dummy) {
        return itself;
    }

    private static Color rgba2rgb(Color background, Color rgba) {
        double alpha = rgba.getOpacity();
        return new Color(
                (1 - alpha) * background.getRed() + alpha * rgba.getRed(),
                (1 - alpha) * background.getGreen() + alpha * rgba.getGreen(),
                (1 - alpha) * background.getBlue() + alpha * rgba.getBlue(),
                1
        );
    }

    private void drawIntesivity(PixelWriter writer, boolean steep, int x, int y, Color color, double intensivity) {
        Color rgba = Color.color(color.getRed(), color.getGreen(), color.getBlue(), intensivity);
        if (steep) {
            writer.setColor(y, x, rgba2rgb(background, rgba));
        } else {
            writer.setColor(x, y, rgba2rgb(background, rgba));
        }
    }

    public void runSun(String alg, int xc, int yc, double len, double step, Color color) {
        for (float angle = 0; Math.abs(angle) < 360; angle += step) {
            int xe = round((float) (xc + Math.cos(Math.toRadians(angle)) * len));
            int ye = round((float) (yc + Math.sin(Math.toRadians(angle)) * len));
            runAlgorithm(alg, xc, yc, xe, ye, color);
        }
    }

    public void runAlgorithm(String alg, int x0, int y0, int xe, int ye, Color color) {
        switch (alg) {
            case ALG_DDA:
                DigitalDiffAnalyzeDraw(x0, y0, xe, ye, color);
                break;
            case ALG_BDA:
                BrezenhemDoubleDraw(x0, y0, xe, ye, color);
                break;
            case ALG_BIA:
                BrezenhemIntDraw(x0, y0, xe, ye, color);
                break;
            case ALG_BSA:
                BrezenhemStairsDraw(x0, y0, xe, ye, color);
                break;
            case ALG_LIB:
                DrawLib(x0, y0, xe, ye, color);
                break;
            case ALG_WOO:
                WooAlgorithm(x0, y0, xe, ye, color);
                break;
        }
    }

    public void DrawLib(int x0, int y0, int xe, int ye, Color color) {
        gc.setStroke(color);
        gc.strokeLine(x0, y0, xe, ye);
    }

    public void DigitalDiffAnalyzeDraw(int x0, int y0, int xe, int ye, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        if (isPoint(x0, y0, xe, ye)) {
            writer.setColor(x0, y0, color);
            return;
        }

        int diffX = Math.abs(xe - x0);
        int diffY = Math.abs(ye - y0);
        int len = diffX > diffY ? diffX : diffY;

        float dX = ((float) (xe - x0)) / len;
        float dY = ((float) (ye - y0)) / len;
        float curX = x0;
        float curY = y0;
        for (int i = 1; i < len + 1; i++) {
            writer.setColor(round(curX), round(curY), color);
            curX += dX;
            curY += dY;
        }
    }


    public void BrezenhemDoubleDraw(int x0, int y0, int xe, int ye, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        if (isPoint(x0, y0, xe, ye)) {
            writer.setColor(x0, y0, color);
            return;
        }

        int dX = xe - x0;
        int dY = ye - y0;
        int sX = sign(dX);
        int sY = sign(dY);
        dX = Math.abs(dX);
        dY = Math.abs(dY);

        float m = 0;

        boolean flag;
        if (dY > dX) {
            dX = getItself(dY, dY = dX);
            flag = true;
        } else {
            flag = false;
        }

        if (dX != 0) {
            m = (float) dY / (float) dX;
        }

        float f = m - 0.5f;
        int curX = x0;
        int curY = y0;
        for (int i = 1; i < dX + 1; i++) {
            writer.setColor(curX, curY, color);
            if (f >= 0) {
                if (flag) {
                    curX += sX;
                } else {
                    curY += sY;
                }
                f--;
            }
            if (f < 0) {
                if (flag) {
                    curY += sY;
                } else {
                    curX += sX;
                }
                f += m;
            }
        }
    }

    public void BrezenhemIntDraw(int x0, int y0, int xe, int ye, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        if (isPoint(x0, y0, xe, ye)) {
            writer.setColor(x0, y0, color);
            return;
        }

        int dx = Math.abs(xe - x0);
        int dy = Math.abs(ye - y0);

        boolean flag = false;
        if (dx <= dy) {
            dx = getItself(dy, dy = dx);
            flag = true;
        }


        int sx = sign(xe - x0);
        int sy = sign(ye - y0);

        int x = x0;
        int y = y0;
        int e = 2 * dy - dx;
        int i = dx;
        while (i > 0) {
            writer.setColor(x, y, color);
            if (e >= 0) {
                if (!flag) {
                    y += sy;
                } else {
                    x += sx;
                }
                e -= 2 * dx;
            }

            if (e < 0) {
                if (!flag) {
                    x += sx;
                } else {
                    y += sy;
                }
                e += 2 * dy;
            }
            i--;
        }

    }

    public void BrezenhemStairsDraw(int x0, int y0, int xe, int ye, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        if (isPoint(x0, y0, xe, ye)) {
            writer.setColor(x0, y0, color);
            return;
        }

        int dX = xe - x0;
        int dY = ye - y0;
        int sX = sign(dX);
        int sY = sign(dY);
        dX = Math.abs(dX);
        dY = Math.abs(dY);

        boolean flag;
        double m = 0;
        if (dX != 0) {
            m = (double) dY / dX;
        }
        if (dY > dX) {
            dX = getItself(dY, dY = dX);
            if (m != 0) {
                m = 1 / m;
            }
            flag = true;
        } else {
            flag = false;
        }

        double I = 100.0;
        double f = I / 2;
        int curX = x0;
        int curY = y0;
        m *= I;
        double W = I - m;
        Color rgba = Color.color(color.getRed(), color.getGreen(), color.getBlue(), f / 100);
        writer.setColor(curX, curY, rgba2rgb(background, rgba));
        for (int i = 1; i < dX + 1; i++) {
            if (f <= W) {
                if (!flag) {
                    curX += sX;
                } else {
                    curY += sY;
                }
                f += m;
            } else {
                curX += sX;
                curY += sY;
                f -= W;
            }
            rgba = Color.color(color.getRed(), color.getGreen(), color.getBlue(), f / 100);
            writer.setColor(curX, curY, rgba2rgb(background, rgba));
        }
    }


    public void WooAlgorithm(int x0, int y0, int xe, int ye, Color color) {
        PixelWriter writer = gc.getPixelWriter();
        if (isPoint(x0, y0, xe, ye)) {
            writer.setColor(x0, y0, color);
            return;
        }

        boolean steep = Math.abs(ye - y0) > Math.abs(xe - x0);
        if (steep) {
            x0 = getItself(y0, y0 = x0);
            xe = getItself(ye, ye = xe);
        }

        if (x0 > xe) {
            x0 = getItself(xe, xe = x0);
            y0 = getItself(ye, ye = y0);
        }

        drawIntesivity(writer, steep, x0, y0, color, 1);
        drawIntesivity(writer, steep, xe, ye, color, 1);
        double dx = xe - x0;
        double dy = ye - y0;
        double gradient = dy / dx;
        double y = y0 + gradient;
        for (int x = x0 + 1; x <= xe - 1; x++) {
            drawIntesivity(writer, steep, x, (int) y, color, 1 - (y - (int) y));
            drawIntesivity(writer, steep, x, (int) y + 1, color, y - (int) y);
            y += gradient;
        }
    }
}
