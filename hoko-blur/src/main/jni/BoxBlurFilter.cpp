//
// Created by yuxfzju on 16/7/28.
//

#include "include/BoxBlurFilter.h"

JNIEXPORT void JNICALL Java_com_hoko_blurlibrary_filter_NativeBlurFilter_nativeBoxBlur
        (JNIEnv *env, jobject j_object, jobject jbitmap, jint j_radius, jint j_cores, jint j_index, jint j_direction) {


    if (jbitmap == NULL) {
        return;
    }

    AndroidBitmapInfo bmpInfo={0};
    if (AndroidBitmap_getInfo(env, jbitmap, &bmpInfo) < 0) {
        return;
    }

    int * pixels = NULL;
    if (AndroidBitmap_lockPixels(env, jbitmap, (void **)&pixels) < 0) {
        return;
    }

    int w = bmpInfo.width;
    int h = bmpInfo.height;

    jint *copy = NULL;
    copy = (jint *) malloc(sizeof(jint) * w * h);

    for (int i = 0; i < w * h; i++) {
        copy[i] = pixels[i];
    }

    if (j_direction == HORIZONTAL) {
        int deltaY = h / j_cores;
        int startY = j_index * deltaY;

        if (j_index == j_cores - 1) {
            deltaY = h - (j_cores - 1) * deltaY;
        }

        boxBlurHorizontal(copy, pixels, w, h, j_radius, 0, startY, w, deltaY);

    } else if (j_direction == VERTICAL){
        int deltaX = w / j_cores;
        int startX = j_index * deltaX;

        if (j_index == j_cores - 1) {
            deltaX = w - (j_cores - 1) * (w / j_cores);
        }

        boxBlurVertical(copy, pixels, w, h, j_radius, startX, 0, deltaX, h);
    }

    AndroidBitmap_unlockPixels(env, jbitmap);

    free(copy);

}

void boxBlurHorizontal(jint *in, jint *out, jint width, jint height, jint radius, jint startX, jint startY, jint deltaX, jint deltaY) {
    jint widthMinus1 = width - 1;
    jint tableSize = 2 * radius + 1;
    jint divide[256 * tableSize];

    for (jint i = 0; i < 256 * tableSize; i++)
        divide[i] = i / tableSize;

    for (jint y = startY; y < startY + deltaY; y++) {
        jint ta = 0, tr = 0, tg = 0, tb = 0;

        for (jint i = -radius; i <= radius; i++) {
            jint rgb = in[y * width +
                          clamp(i, startX, startX + deltaX - 1)];
            ta += (rgb >> 24) & 0xff;
            tr += (rgb >> 16) & 0xff;
            tg += (rgb >> 8) & 0xff;
            tb += rgb & 0xff;
        }

        jint baseIndex = y * width;

        for (jint x = startX; x < startX + deltaX; x++) {

            jint i1 = x + radius + 1;
            if (i1 > startX + deltaX - 1)
                i1 = startX + deltaX - 1;
            jint i2 = x - radius;
            if (i2 < startX)
                i2 = startX;
            jint rgb1 = in[baseIndex + i1];
            jint rgb2 = in[baseIndex + i2];

            ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
            tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
            tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
            tb += (rgb1 & 0xff) - (rgb2 & 0xff);

            out[baseIndex + x] = (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) |
                                 divide[tb];
        }
    }
}


void boxBlurVertical(jint *in, jint *out, jint width, jint height, jint radius, jint startX, jint startY, jint deltaX, jint deltaY) {
    jint heightMinus1 = height - 1;
    jint tableSize = 2 * radius + 1;
    jint divide[256 * tableSize];

    for (jint i = 0; i < 256 * tableSize; i++)
        divide[i] = i / tableSize;

    for (jint x = startX; x < startX + deltaX; x++) {
        jint ta = 0, tr = 0, tg = 0, tb = 0;

        for (jint i = -radius; i <= radius; i++) {
            jint rgb = in[x + clamp(i, startY, startY + deltaY - 1) * width];
            ta += (rgb >> 24) & 0xff;
            tr += (rgb >> 16) & 0xff;
            tg += (rgb >> 8) & 0xff;
            tb += rgb & 0xff;
        }

        for (jint y = startY; y < startY + deltaY; y++) {
            out[y * width + x] = (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) |
                                 divide[tb];

            jint i1 = y + radius + 1;
            if (i1 > startY + deltaY - 1)
                i1 = startY +deltaY - 1;
            jint i2 = y - radius;
            if (i2 < startY)
                i2 = startY;
            jint rgb1 = in[x + i1 * width];
            jint rgb2 = in[x + i2 * width];

            ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
            tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
            tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
            tb += (rgb1 & 0xff) - (rgb2 & 0xff);
        }
    }
}
