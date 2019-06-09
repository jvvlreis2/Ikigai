package ikigai.example.dispositivosmoveis.ikigai;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;


public class PaintView extends View {

    public static int BRUSH_SIZE = 20;
    public int DEFAULT_COLOR = Color.RED;
    public static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final float TOUCH_TOLERANCE = 4;
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private int i1,i2;
    private AlertDialog alerta;
    private AlertDialog.Builder builder = new AlertDialog.Builder(getContext());;
    private Path h;
    private ArrayList<FingerPath> paths = new ArrayList<>();
    //private ArrayList<PointF> circulo = new ArrayList<>();
    ArrayList<Path> circulos = new ArrayList<>();
    private int currentColor;
    private int backgroundColor = DEFAULT_BG_COLOR;
    private int strokeWidth;
    private boolean emboss;
    private boolean blur;
    private MaskFilter mEmboss;
    private MaskFilter mBlur;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);
    private boolean paro;

    public PaintView(Context context) {
        this(context, null);
    }

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(DEFAULT_COLOR);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setXfermode(null);
        mPaint.setAlpha(0xff);

        mEmboss = new EmbossMaskFilter(new float[]{1, 1, 1}, 0.4f, 6, 3.5f);
        mBlur = new BlurMaskFilter(5, BlurMaskFilter.Blur.NORMAL);
    }

    public void init(DisplayMetrics metrics) {
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        currentColor = DEFAULT_COLOR;
        strokeWidth = 11;
    }

    public void normal() {
        emboss = false;
        blur = false;
    }

    public void emboss() {
        emboss = true;
        blur = false;
    }

    public void blur() {
        emboss = false;
        blur = true;
    }

    Point point;

    public void setDEFAULT_COLOR(int color) {
        this.DEFAULT_COLOR = color;
    }

    public void clear() {
        circulos.clear();
        backgroundColor = DEFAULT_BG_COLOR;
        paths.clear();
        normal();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        mCanvas.drawColor(backgroundColor);

        for (FingerPath fp : paths) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mPaint.setMaskFilter(null);

            if (fp.emboss)
                mPaint.setMaskFilter(mEmboss);
            else if (fp.blur)
                mPaint.setMaskFilter(mBlur);

            mCanvas.drawPath(fp.path, mPaint);

        }
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    private void touchStart(float x, float y) {
        mPath = new Path();
        FingerPath fp = new FingerPath(currentColor, emboss, blur, strokeWidth, mPath);
        paths.add(fp);

        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
    }

    private void touchMove(float x, float y) {
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);


        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            h.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
            mX = x;
            mY = y;
        }
    }

    private void touchUp() {
        mPath.lineTo(mX, mY);
        h.lineTo(mX, mY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        if(!paro) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    h = new Path();
                    h.moveTo(x, y);
                    touchStart(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    PointF point = new PointF(x, y);
                    touchMove(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touchUp();
                    circulos.add(h);
                    if (circulos.size() >= 4) {
                        if (isIkigai()) {
                            builder.setMessage(getResources().getString(R.string.isIkigai))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            paro = true;
                                        }
                                    });
                        } else {
                            builder.setMessage(getResources().getString(R.string.isNotIkigai1) + " " + (i1 + 1) + " " + getResources().getString(R.string.isNotIkigai2) + " " + (i2 + 1))
                                    .setCancelable(false)
                                    .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            clear();
                                        }
                                    });
                        }
                        alerta = builder.create();
                        alerta.show();
                    }
                    invalidate();
                    break;
            }
        }
        return true;
    }
    boolean temIntersecção(int c1, int c2){
        Region clip = new Region(0, 0, getWidth(), getHeight());
        Region region1 = new Region();
        region1.setPath(circulos.get(c1), clip);
        Region region2 = new Region();
        region2.setPath(circulos.get(c2), clip);

        if (!region1.quickReject(region2) && region1.op(region2, Region.Op.INTERSECT)) {
            return true;
        }
        return false;
    }
//    public boolean tem3in(int c){
//        int inters=0;
//        for (int i = 0 ; i<circulos.size(); i++){
//            if(temIntersecção(c,i)){
//                inters++;
//            }
//        }
//        if (inters >= 3){
//            return true;
//        }
//        return false;
//    }
//    public boolean tem2in(int c,int c1){
//        int inters=0;
//        for (int i = 0 ; i<circulos.size(); i++){
//            if(i != c1 && temIntersecção(c,i) ){
//                inters++;
//            }
//        }
//        if (inters >= 2){
//            return true;
//        }
//        return false;
//    }
//    public boolean tem1in(int c,int c1, int c2) {
//        int inters = 0;
//        for (int i = 0; i < circulos.size(); i++) {
//            if (i != c1 && i !=c2 && temIntersecção(c, i)) {
//                inters++;
//            }
//        }
//        if (inters >= 1) {
//            return true;
//        }
//        return false;
//    }



//    public boolean isIkigai(){
//        for(int i = 0; i<circulos.size(); i++){
//            int ci1 = 0, ci2=0, ci3 = 0, ci4 = 0;
//            if(tem3in(i)){
//                ci1 = i;
//                for(int j = 0; j<circulos.size(); j++){
//                    if(temIntersecção(j,ci1) && tem2in(j,ci1)){
//                            ci2 = j;
//                            for(int x = 0; x<circulos.size(); x++){
//                                if(temIntersecção(x,ci1) && temIntersecção(x,ci2) && tem1in(x,ci1,ci2)){
//                                            ci3 = x;
//                                            for(int y = 0; y<circulos.size(); y++) {
//                                                if(temIntersecção(y,ci1) && temIntersecção(y,ci2) && temIntersecção(y,ci3)) {
//                                                    return true;
//                                                }
//                                            }
//                                }
//                            }
//                    }
//                }
//            }
//        }
//        return false;
//    }
//
//    public boolean isIkigai(){
//        if(temIntersecção(0,1)&&
//                temIntersecção(0,2)&&
//                temIntersecção(0,3)&&
//                temIntersecção(1,0)&&
//                temIntersecção(1,2)&&
//                temIntersecção(1,3)&&
//                temIntersecção(2,0)&&
//                temIntersecção(2,1)&&
//                temIntersecção(2,3)&&
//                temIntersecção(3,0)&&
//                temIntersecção(3,1)&&
//                temIntersecção(3,2))
//            return true;
//        return false;
//    }
    public boolean isIkigai(){
        i1 = 0;
        i2 = 0;
        if(temIntersecção(0,1)){
            if(temIntersecção(0,2)){
                if(temIntersecção(0,3)){
                    if(temIntersecção(1,0)){
                        if(temIntersecção(1,2)){
                            if(temIntersecção(1,3)){
                                if(temIntersecção(2,0)){
                                    if(temIntersecção(2,1)){
                                        if(temIntersecção(2,3)){
                                            if(temIntersecção(3,0)){
                                                if(temIntersecção(3,1)){
                                                    if(temIntersecção(3,2)){
                                                        return true;
                                                    }
                                                    else{
                                                        i1 = 3;
                                                        i2 = 0;
                                                        return false;
                                                    }
                                                }
                                                else{
                                                    i1 = 3;
                                                    i2 = 1;
                                                    return false;
                                                }
                                            }
                                            else{
                                                i1 = 3;
                                                i2 = 0;
                                                return false;
                                            }
                                        }
                                        else{
                                            i1 = 2;
                                            i2 = 3;
                                            return false;
                                        }
                                    }
                                    else{
                                        i1 = 2;
                                        i2 = 1;
                                        return false;
                                    }
                                }
                                else{
                                    i1 = 2;
                                    i2 = 0;
                                    return false;
                                }
                            }
                            else{
                                i1 = 1;
                                i2 = 3;
                                return false;
                            }
                        }
                        else{
                            i1 = 1;
                            i2 = 2;
                            return false;
                        }
                    }
                    else{
                        i1 = 1;
                        i2 = 0;
                        return false;
                    }
                }
                else{
                    i1 = 0;
                    i2 = 3;
                    return false;
                }
            }
            else{
                i1 = 0;
                i2 = 2;
                return false;
            }
        }
        i1 = 0;
        i2 = 1;
        return false;
    }
    public void setCurrentColor(int currentColor) {
        this.currentColor = currentColor;
    }
    public int getCurrentColor(){
        return this.currentColor;
    }
    public int getDEFAULT_COLOR() {
        return DEFAULT_COLOR;
    }

    public void setParo(boolean paro) {
        this.paro = paro;
    }
}



