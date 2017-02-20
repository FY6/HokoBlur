package com.hoko.blurlibrary.generator;

import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;

import com.hoko.blurlibrary.Blur;
import com.hoko.blurlibrary.origin.BoxBlurFilter;
import com.hoko.blurlibrary.origin.GaussianBlurFilter;
import com.hoko.blurlibrary.origin.OriginBlurHelper;
import com.hoko.blurlibrary.origin.StackBlurFilter;
import com.hoko.blurlibrary.task.BlurSubTask;
import com.hoko.blurlibrary.task.BlurTaskManager;
import com.hoko.blurlibrary.util.BitmapUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiangpi on 16/9/7.
 */
public class OriginBlurGenerator extends BlurGenerator {

    @Override
    protected Bitmap doInnerBlur(Bitmap scaledInBitmap, boolean concurrent) {
        if (scaledInBitmap == null) {
            return null;
        }

        if (concurrent) {
            try {
                int cores = BlurTaskManager.getCores();
                List<BlurSubTask> hTasks = new ArrayList<BlurSubTask>(cores);
                List<BlurSubTask> vTasks = new ArrayList<BlurSubTask>(cores);

                for (int i = 0; i < cores; i++) {
                    hTasks.add(new BlurSubTask(Blur.SCHEME_JAVA, mMode, scaledInBitmap, mRadius, cores, i, Blur.HORIZONTAL));
                    vTasks.add(new BlurSubTask(Blur.SCHEME_JAVA, mMode, scaledInBitmap, mRadius, cores, i, Blur.VERTICAL));
                }

                BlurTaskManager.getInstance().invokeAll(hTasks);
                BlurTaskManager.getInstance().invokeAll(vTasks);

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            OriginBlurHelper.doFullBlur(mMode, scaledInBitmap, mRadius);
        }

        return scaledInBitmap;
    }

    @Override
    public void setBlurRadius(int radius) {
        super.setBlurRadius(radius);

    }
}

