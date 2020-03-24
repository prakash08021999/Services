package com.example.ratingbarviewcontroller;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AnimatedProgressBar extends View {

    private Paint bgPaint,paint,mSuccess,mError;
    private Path path;
    private Bitmap loadingBitmap,downloadBitmap;

    private Paint textPaint;

    float[] pos = new float[2];

    float radius = 0;
    float scaleX = 0;
    float scaleY = 0;

    private Matrix matrix;
    private String strText="";

    float center_scaleX=1;
    float center_scaleY=1;
    private RectF moveBounds;
    private Camera camera;
    private Matrix cameraMatrix = new Matrix();

    int rotateX = 0;
    int rotateY = 0;

    private PathMeasure pm;
    int offsetY = 0;
    float progressOffsetX =0;

    public static final int STATE_READY = 0;
    public static final int STATE_READY_CHANGING = 1;
    public static final int STATE_READYING = 2;
    public static final int STATE_ERROR = 3;
    public static final int STATE_STARTING = 4;
    private static final int STATE_SUCCESS = 5;
    private static final int STATE_BACK = 6;
    private static final int STATE_BACK_HOME = 7;
    private static final int DONE = 8;

    private int state = STATE_READY;

    private int startX=0,startY=0,endX=0,endY=0;

    private List<ValueAnimator> vas_list =new ArrayList<>();

    private int max=0;
    private  int progress;

    private float pointStartX = -1f;
    float downX;

    private AnimationEndListener animationEndListener;
    private OntextChangeListener ontextChangeListener;

    private Path pp=new Path();

    private float textSize;
    private float progressBarHeight;
    private int textColorSuccess;
    private int textColorError;
    private int textColorNormal;
    private int startDrawable;
    private int progressBarBgColor;
    private int progressBarColor;
    private boolean isFirstSetListener = false;
    private boolean isCanDrag = true;
    private boolean isCanReBack = true;

    private int endSuccessDrawable;
    private int endSuccessBackgroundColor;

    private boolean isCanEndSuccessClickable = true;

    private boolean isTempCanEndSuccessClickable = true;

    private BitmapFactory.Options optionsEndSuccess = null;
    private BitmapFactory.Options optionsLoadingDrawable = null;

    private RectF rectClickRange;

    private Rect bounds;

    private Bitmap getLoadingBitmap(){
        return loadingBitmap;
    }

    private void setOptionsLoadingDrawable(BitmapFactory.Options optionsLoadingDrawable){
        if(optionsLoadingDrawable != null){
            this.optionsLoadingDrawable = optionsLoadingDrawable;
            loadingBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.ic_chat_bubble_white_36dp,optionsLoadingDrawable);
        }
    }

    public AnimatedProgressBar setCanEndSuccessClickable(boolean isCanEndSuccessClickable){
        this.isTempCanEndSuccessClickable = isCanEndSuccessClickable;
        return this;
    }

    public AnimatedProgressBar setCanReBack(boolean isCanReBack){
        this.isCanReBack = isCanReBack;
        return this;
    }

    public void setOntextChangeListener(OntextChangeListener ontextChangeListener){
        this.ontextChangeListener = ontextChangeListener;
    }

    public AnimatedProgressBar setCanDragChangeProgress(boolean isCanDrag){
        this.isCanDrag = isCanDrag;
        return this;
    }

    public void setOnAnimationEndListener(AnimationEndListener mlistener){
        this.animationEndListener = mlistener;
        isFirstSetListener = true;
    }

    public AnimatedProgressBar(Context context) {
        this(context,null);
    }

    public AnimatedProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs,0);
    }

    public AnimatedProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AnimatedProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

        TypedArray typedArray =  context.getResources().obtainAttributes(attrs,R.styleable.SpecialProgressBarStyle);
        textSize = typedArray.getDimension(R.styleable.SpecialProgressBarStyle_textSize, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,12, getResources().getDisplayMetrics()));
        progressBarHeight = typedArray.getDimension(R.styleable.SpecialProgressBarStyle_progressBarHeight, dip2px(getContext(), 4));
        textColorSuccess = typedArray.getColor(R.styleable.SpecialProgressBarStyle_textColorSuccess, Color.parseColor("#66A269"));
        textColorError = typedArray.getColor(R.styleable.SpecialProgressBarStyle_textColorError, Color.parseColor("#BC5246"));
        textColorNormal = typedArray.getColor(R.styleable.SpecialProgressBarStyle_textColorNormal, Color.parseColor("#491C14"));
        startDrawable = typedArray.getResourceId(R.styleable.SpecialProgressBarStyle_startDrawable, R.drawable.ic_get_app_white_36dp);
        endSuccessDrawable = typedArray.getResourceId(R.styleable.SpecialProgressBarStyle_endSuccessDrawable, R.drawable.ic_done_white_36dp);
        progressBarBgColor = typedArray.getColor(R.styleable.SpecialProgressBarStyle_progressBarBgColor, Color.parseColor("#491C14"));
        progressBarColor = typedArray.getColor(R.styleable.SpecialProgressBarStyle_progressBarColor, Color.WHITE);
        isCanReBack = typedArray.getBoolean(R.styleable.SpecialProgressBarStyle_canReBackable, true);
        isCanDrag = typedArray.getBoolean(R.styleable.SpecialProgressBarStyle_canDragable,true);
        endSuccessBackgroundColor = progressBarBgColor;
        typedArray.recycle();
        init();
    }

    public AnimatedProgressBar setTextSize(float textSize) {
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        return this;
    }

    public AnimatedProgressBar setProgressBarHeight(float progressBarHeight) {
        this.progressBarHeight = progressBarHeight;
        bgPaint.setStrokeWidth(progressBarHeight);
        paint.setStrokeWidth(progressBarHeight);
        return this;
    }

    public AnimatedProgressBar setTextSuccessColor(float textSuccessColor){
        this.textColorSuccess = textColorSuccess;
        mSuccess.setColor(textColorSuccess);
        return this;
    }

    public AnimatedProgressBar setTextErrorColor(int textErrorColor){
        this.textColorError = textErrorColor;
        mError.setColor(textErrorColor);
        return this;
    }

    public AnimatedProgressBar setTextNormalColor(float textNormalColor){
        this.textColorNormal = textColorNormal;
        textPaint.setColor(textColorSuccess);
        return this;
    }

    public AnimatedProgressBar setStartDrawable(int startDrawable,BitmapFactory.Options options){
        this.startDrawable = startDrawable;
        if (options !=null){
            downloadBitmap = BitmapFactory.decodeResource(getResources(),startDrawable,options);
        }else{
            downloadBitmap = BitmapFactory.decodeResource(getResources(),startDrawable);
        }
        return this;
    }

    public AnimatedProgressBar setEndSuccessDrawable(int endSuccessDrawable,BitmapFactory.Options options){
        this.endSuccessDrawable = endSuccessDrawable;
        this.optionsEndSuccess = options;
        return this;
    }

    public AnimatedProgressBar setEndSuccessBackgroundColor(int endSuccessBackgroundColor){
        this.endSuccessBackgroundColor = endSuccessBackgroundColor;
        return this;
    }

    public AnimatedProgressBar setProgressBarBgColor(int progressBarBgColor){
        this.progressBarBgColor = progressBarBgColor;
        bgPaint.setColor(progressBarBgColor);
        return this;
    }

    public AnimatedProgressBar setProgressBarColor(int progressBarColor){
        this.progressBarColor = progressBarColor;
        paint.setColor(progressBarColor);
        return this;
    }

    private void init(){
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setColor(progressBarBgColor);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(progressBarHeight);

        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(progressBarColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(progressBarHeight);

        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColorNormal);
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setTextSize(textSize);
        textPaint.setFakeBoldText(true);

        mError = new Paint(textPaint);
        mError.setColor(textColorError);
        mError.setTextSize(textSize);

        mSuccess = new Paint(mError);
        mSuccess.setColor(textColorSuccess);

        path = new Path();
        downloadBitmap = BitmapFactory.decodeResource(getResources(), startDrawable);
        loadingBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_chat_bubble_white_36dp);

        bounds= new Rect();
        camera = new Camera();
    }

    private void initStateData(){
        center_scaleX = 1;
        center_scaleY = 1;
        rotateX = 0;
        rotateY = 0;
        radius = 0;
        scaleX = 0;
        scaleY = 0;
        strText = "";
        offsetY = 0;
        startX = getWidth()/2;
        startY = getHeight()/2;
        endX = getWidth()/2;
        endY = getHeight()/2;
        state = STATE_READY;
        progressOffsetX = 0;
        pointStartX = -1f;
        downX = 0;
        moveBounds = null;
        paint.setColor(progressBarColor);
        downloadBitmap = BitmapFactory.decodeResource(getResources(),startDrawable);
        vas_list.clear();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMeasureSpec == MeasureSpec.AT_MOST && heightSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(dip2px(getContext(),300),loadingBitmap.getHeight()*2);
        }else if(widthSpecMode == MeasureSpec.AT_MOST){
            setMeasuredDimension(dip2px(getContext(),300),heightSpecSize);
        }else if (heightMeasureSpec == MeasureSpec.AT_MOST){
            setMeasuredDimension(widthSpecSize,loadingBitmap.getHeight()*2);
        }

        startX = getWidth()/2;
        startY = getHeight()/2;
        endX = getWidth()/2;
        endY = getHeight()/2;
        radius = (Math.min(getWidth(),getHeight())-bgPaint.getStrokeWidth()*2)/2;

        rectClickRange = new RectF(getWidth()/2-radius,getHeight()/2-radius,getWidth()/2+radius,getHeight()/2+radius);

        matrix = new Matrix();
        matrix.setTranslate(progressOffsetX, getHeight() / 2 - bgPaint.getStrokeWidth() / 2-downloadBitmap.getHeight());

        if (progress == 0){
            progressOffsetX = 0;
        }else {
            progressOffsetX = (progress*(getWidth()- bgPaint.getStrokeWidth()-loadingBitmap.getWidth())*1.0f/max);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        switch (state){
            case STATE_BACK_HOME:
            case STATE_READY:
                path.reset();
                bgPaint.setStyle(Paint.Style.FILL);

                path.addCircle(getWidth()/2,getHeight()/2,radius, Path.Direction.CCW);
                canvas.drawPath(path,bgPaint);
                matrix.reset();
                matrix.setScale(center_scaleX,center_scaleY);
                matrix.preTranslate(0,0);
                matrix.postTranslate(getWidth() / 2 - downloadBitmap.getWidth() / 2*Math.max(center_scaleX,center_scaleY), getHeight() / 2 - downloadBitmap.getHeight() / 2*Math.max(center_scaleX,center_scaleY));
                canvas.drawBitmap(downloadBitmap,matrix,bgPaint);
                break;
            case STATE_READY_CHANGING:
                path.reset();
                path.moveTo(startX,startY);
                path.lineTo(endX,endY);
                canvas.drawPath(path,bgPaint);
                break;
            case STATE_READYING:
                path.reset();
                path.moveTo(bgPaint.getStrokeWidth(),getHeight()/2);//-offsetY*0.6f
                if (offsetY == 0){
                    path.quadTo(0+bgPaint.getStrokeWidth(),getHeight()/2,getWidth()-bgPaint.getStrokeWidth()-loadingBitmap.getWidth(),getHeight()/2);
                }else {
                    path.quadTo(getWidth()/2,getHeight()/2-offsetY, getWidth()- bgPaint.getStrokeWidth() -loadingBitmap.getWidth(), getHeight()/2);
                }
                canvas.drawPath(path,bgPaint);

                matrix.reset();
                matrix.setScale(scaleX, scaleY);
                matrix.setTranslate(progressOffsetX, getHeight() / 2 - bgPaint.getStrokeWidth() / 2 - downloadBitmap.getHeight());
                canvas.drawBitmap(loadingBitmap, matrix, bgPaint);
                break;
            case STATE_BACK:

                matrix.reset();;
                matrix.setScale(startX,startY);

                matrix.preTranslate(0,0);
                matrix.postTranslate(pos[0],pos[1]-bgPaint.getStrokeWidth()/2-downloadBitmap.getHeight()*scaleY);
                canvas.drawBitmap(loadingBitmap,matrix,bgPaint);

                path.reset();
                path.moveTo(startX,getHeight()/2);
                path.quadTo(startX,getHeight()/2,endX,getHeight()/2);

                canvas.drawPath(path, paint);

                break;
            case STATE_SUCCESS:
                camera.save();
                camera.rotateY(rotateY);
                camera.getMatrix(cameraMatrix);
                camera.restore();

                cameraMatrix.preTranslate(0,-loadingBitmap.getHeight()/2);
                cameraMatrix.postTranslate(pos[0],pos[1]-loadingBitmap.getHeight()/2);

                if (rotateY>=330){
                    strText = "done";
                    if (ontextChangeListener != null && ontextChangeListener.OnSuccessTextChange(this,max,progress)!=null){
                        strText = ontextChangeListener.OnSuccessTextChange(this,max,progress);
                    }

                    mSuccess.getTextBounds(strText,0,strText.length(),bounds);
                    canvas.drawText(strText,pos[0]+loadingBitmap.getWidth() / 2-bounds.right / 2,pos[1]- loadingBitmap.getHeight()/2 + bounds.bottom ,mSuccess);
                }
                path.reset();
                path.moveTo(bgPaint.getStrokeWidth(),getHeight()/2);
                path.quadTo(bgPaint.getStrokeWidth(), getHeight() / 2, getWidth() - bgPaint.getStrokeWidth() - loadingBitmap.getWidth(), getHeight() / 2);

                canvas.drawPath(path,paint);
                break;
            case STATE_ERROR:
            case STATE_STARTING:
                path.reset();
                path.moveTo(bgPaint.getStrokeWidth(),getHeight()/2);
                if (progressOffsetX>radius){
                    if (progressOffsetX>=(getWidth()-bgPaint.getStrokeWidth()-loadingBitmap.getWidth())){
                        path.quadTo(getWidth()/2, getHeight()/2 - offsetY, getWidth()- bgPaint.getStrokeWidth()-loadingBitmap.getWidth(), getHeight()/2);
                    }else{
                        path.quadTo(progressOffsetX, getHeight(), getWidth()- bgPaint.getStrokeWidth()-loadingBitmap.getWidth(), getHeight()/2);
                    }
                }else{
                    path.quadTo(getWidth() / 2, getHeight() / 2 - offsetY, getWidth() - bgPaint.getStrokeWidth() - loadingBitmap.getWidth(), getHeight() / 2);
                }

                canvas.drawPath(path,bgPaint);

                pm = new PathMeasure(path,false);
                pp.reset();

                pp.rLineTo(0,0);
                if (progressOffsetX>=pm.getLength()){
                    pm.getSegment(0,pm.getLength(),pp,true);
                    pm.getPosTan(pm.getLength(),pos,null);
                }else{
                    pm.getSegment(0,progressOffsetX,pp,true);
                    pm.getPosTan(progressOffsetX,pos,null);
                }
                canvas.drawPath(pp,paint);

                moveBounds = new RectF(pos[0],pos[1]-loadingBitmap.getHeight(),pos[0]+loadingBitmap.getWidth(),pos[1]);

                if (state == STATE_ERROR){
                    camera.save();
                    camera.rotateX(rotateX);
                    camera.getMatrix(cameraMatrix);
                    camera.restore();

                    cameraMatrix.preTranslate(-loadingBitmap.getWidth()/2,-loadingBitmap.getHeight()/2);
                    cameraMatrix.postTranslate(pos[0]+loadingBitmap.getWidth()/2,pos[1]+loadingBitmap.getHeight()/2);

                    canvas.drawBitmap(loadingBitmap,cameraMatrix,bgPaint);
                }else{
                    if (state != STATE_SUCCESS){
                        matrix.reset();
                        matrix.setScale(scaleX,scaleY);
                        matrix.setTranslate(pos[0],pos[1]-bgPaint.getStrokeWidth()/ 2 - downloadBitmap.getHeight());

                        canvas.drawBitmap(loadingBitmap,matrix,bgPaint);
                    }
                }

                bounds.setEmpty();
                if (progressOffsetX>=pm.getLength())
                    progressOffsetX = pm.getLength();
                strText = (int)(progressOffsetX*100/pm.getLength())+"%";
                if (ontextChangeListener!=null && ontextChangeListener.onProgressTextChange(this,max,progress)!=null){
                    strText = ontextChangeListener.onProgressTextChange(this,max,progress);
                }
                textPaint.getTextBounds(strText, 0, strText.length(), bounds);

                if (state == STATE_ERROR){
                    moveBounds = new RectF(pos[0],pos[1],pos[0]+loadingBitmap.getWidth(),pos[1]+loadingBitmap.getHeight());
                    if (rotateX <= -100){
                        strText ="fail";
                        if (ontextChangeListener!=null && ontextChangeListener.onErrorTextChange(this, max, progress)!=null){
                            strText = ontextChangeListener.onErrorTextChange(this, max, progress);
                        }
                        mError.getTextBounds(strText, 0, strText.length(), bounds);
                        canvas.drawText(strText,pos[0]+ bgPaint.getStrokeWidth(),pos[1]+loadingBitmap.getHeight()*5/6,mError);
                    }
                }else{
                    if (state!=STATE_SUCCESS)
                        canvas.drawText(strText,pos[0]+loadingBitmap.getWidth() / 2-bounds.right / 2,pos[1]-loadingBitmap.getHeight() / 2 - bounds.bottom / 2,textPaint);
                }

                if (progressOffsetX == pm.getLength() && state != STATE_SUCCESS){
                    changeStateSuccess();
                }
                break;
        }
    }

    public int getMax(){
        return max;
    }

    public void setMax(int max){
        this.max = max;
    }

    public  int getProgress(){
        return progress;
    }

    public void setProgress(int progress){
        if (state != STATE_STARTING)
            return;
        this.progress = progress;
        if (progress == 0){
            progressOffsetX = 0;
            return;
        }
        if (max == 0){
            throw new RuntimeException("max 0!");
        }
        strText = progress*100/max+"%";
        progressOffsetX = (progress*(getWidth()- bgPaint.getStrokeWidth() -loadingBitmap.getWidth())*1.0f/max);

        for (ValueAnimator va:vas_list){
            if (va.isRunning()){
                va.end();
            }
        }
        state = STATE_STARTING;
        postInvalidate();
    }

    public void changeStateSuccess(){
        if (state != STATE_SUCCESS){
            this.progress = max;
            state = STATE_SUCCESS;
            final ValueAnimator va = ValueAnimator.ofInt(0,360);
            va.setInterpolator(new AnticipateOvershootInterpolator());
            va.setDuration(1000);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (Integer) animation.getAnimatedValue();
                    rotateY = value;
                    invalidate();
                }
            });

            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isCanReBack){
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                changeStateBack();
                            }
                        },500);
                    }
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            postDelayed(new Runnable() {
                @Override
                public void run() {
                    va.start();
                }
            }, 100);
            vas_list.add(va);

        }
    }

    public void beginStarting(){
        initStateData();

        if (state == STATE_READY){
            ValueAnimator va = ValueAnimator.ofInt((int)(Math.min(getWidth(), getHeight())- bgPaint.getStrokeWidth()*2)/2,(int) bgPaint.getStrokeWidth());
            va.setInterpolator(new AnticipateInterpolator());
            va.setDuration(800);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    int value = (Integer) animation.getAnimatedValue();
                    radius = value;
                    center_scaleX = (1-animation.getAnimatedFraction());
                    center_scaleY = (1-animation.getAnimatedFraction());
                    invalidate();
                }
            });

            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    state = STATE_READY_CHANGING;
                    bgPaint.setStyle(Paint.Style.STROKE);
                    bgPaint.setColor(Color.BLACK);
                    changeStateReadyChanging();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            va.start();
            vas_list.add(va);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isCanEndSuccessClickable){
            return false;
        }

        for (ValueAnimator va:vas_list){
            if (va.isRunning()){
                return false;
            }
        }

        if (state == STATE_READY || state == DONE){
            if (rectClickRange.contains(event.getX(),event.getY())){
                state = STATE_READY;
                for (ValueAnimator va:vas_list)
                {
                    va.cancel();
                }
                beginStarting();
            }
            return false;
        }
        if (state != STATE_STARTING || isFirstSetListener || !isCanDrag){
            return false;
        }

        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                float downY = (int) event.getY();
                if (moveBounds.contains(downX,downY)){
                    pointStartX = downX;
                }else {
                    pointStartX = -1f;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (pointStartX!=-1){
                    float moveX = (int)event.getX();
                    progressOffsetX = moveX;

                    if (progressOffsetX>=(getWidth()- bgPaint.getStrokeWidth() -loadingBitmap.getWidth())){
                        progressOffsetX = (getWidth()- bgPaint.getStrokeWidth() -loadingBitmap.getWidth());
                    }

                    if (progressOffsetX<0)
                        progressOffsetX = 0;

                    progress = (int)(pos[0]*max*1.0f/(getWidth()- bgPaint.getStrokeWidth() -loadingBitmap.getWidth()));

                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                pointStartX = -1f;
                break;
        }
        return  true;
    }

    private void changeStateBack() {
        state = STATE_BACK;

        ValueAnimator va = ValueAnimator.ofInt(getWidth()/2-(int) bgPaint.getStrokeWidth(),(int) bgPaint.getStrokeWidth());
        va.setInterpolator(new AccelerateInterpolator());
        va.setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                startX = getWidth() / 2 + value;
                endX = getWidth() / 2 - value;
                pos[0] = startX;
                scaleX = 1 - animation.getAnimatedFraction();
                scaleY = 1 - animation.getAnimatedFraction();
                invalidate();
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                changeStateBackHome();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        va.start();
        vas_list.add(va);//添加动画到集合中
    }

    public void changeStateError(){
        final ValueAnimator va = ValueAnimator.ofInt(0,-180);
        va.setInterpolator(new DampingInterpolator());
        va.setDuration(300);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                rotateX = value;
                invalidate();
            }
        });
        va.start();
        vas_list.add(va);
    }

    private void changeStateBackHome(){
        state = STATE_BACK_HOME;
        if (optionsEndSuccess!=null){
            downloadBitmap = BitmapFactory.decodeResource(getResources(),endSuccessDrawable,optionsEndSuccess);
        }else {
            downloadBitmap = BitmapFactory.decodeResource(getResources(),endSuccessDrawable);
        }

        bgPaint.setColor(endSuccessBackgroundColor);

        ValueAnimator va = ValueAnimator.ofInt((int) bgPaint.getStrokeWidth(),(int)(Math.min(getWidth(), getHeight())- bgPaint.getStrokeWidth()*2)/2);
        va.setInterpolator(new AnticipateOvershootInterpolator());
        va.setDuration(800);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                radius = value;
                center_scaleX = animation.getAnimatedFraction();
                center_scaleY = animation.getAnimatedFraction();

                invalidate();
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isCanEndSuccessClickable = isTempCanEndSuccessClickable;
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        state = DONE;
                    }
                }, 50);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.start();
        vas_list.add(va);
    }

    public void changeStateReadyChanging(){
        ValueAnimator va = ValueAnimator.ofInt((int) bgPaint.getStrokeWidth(),getWidth()/2-(int) bgPaint.getStrokeWidth());
        va.setInterpolator(new LinearInterpolator());
        va.setDuration(200);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (Integer) animation.getAnimatedValue();
                startX = getWidth() / 2 - value;
                endX = getWidth() / 2 + value;
                invalidate();
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                changeStateStart();
            }
            @Override
            public void onAnimationCancel(Animator animation) {
            }
            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        va.start();
        vas_list.add(va);
    }

    private void changeStateStart(){
        state = STATE_READYING;

        offsetY = 0;
        invalidate();

        ValueAnimator va = ValueAnimator.ofInt(-getHeight()/2,0,getHeight()/2,0);
        va.setInterpolator(new DampingInterpolator(2, 0.3f));
        va.setDuration(1000);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offsetY = (Integer) animation.getAnimatedValue();
                scaleX = animation.getAnimatedFraction();
                scaleY = animation.getAnimatedFraction();

                if (animation.getAnimatedFraction() >= 1.0f) {
                    state = STATE_STARTING;
                }
                invalidate();
            }
        });

        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (animationEndListener != null) {
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            animationEndListener.onAnimationEnd();
                            strText = progress*100/max+"%";
                            isFirstSetListener = false;
                        }
                    }, 200);
                } else {
                    isFirstSetListener = false;
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        va.start();
        vas_list.add(va);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        for (ValueAnimator va:vas_list){
            va.cancel();
        }
        vas_list.clear();
        getHandler().removeCallbacksAndMessages(null);
    }

    public interface OntextChangeListener{

        String onProgressTextChange(AnimatedProgressBar animatedProgressBar,int max,int progress);

        String onErrorTextChange(AnimatedProgressBar animatedProgressBar,int max,int progress);

        String OnSuccessTextChange(AnimatedProgressBar animatedProgressBar,int max,int progress);
    }

    public interface AnimationEndListener{
        void onAnimationEnd();
    }

    public int dip2px(Context context,float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }
}
