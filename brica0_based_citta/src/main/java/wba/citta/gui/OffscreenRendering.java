package wba.citta.gui;

import java.awt.Image;
import java.util.Deque;
import java.util.LinkedList;

/**
 * オフスクリーンイメージのリングバッファ
 * @author moriyoshi
 */
public class OffscreenRendering {
    /* ダブルバッファリング用 オフスクリーンイメージ */
    private Image[] offscreenImages;
    private int activeOffscreenImageIndex;
    private int headIndex;
    private Deque<Image> disposed = new LinkedList<Image>();

    public OffscreenRendering(int bufferSize) {
        offscreenImages = new Image[bufferSize];
        activeOffscreenImageIndex = 0;
        headIndex = -1;
    }

    public void push(Image im) {
        synchronized(this) {
            ++headIndex;
            if (headIndex >= offscreenImages.length) {
                headIndex = headIndex - offscreenImages.length;
                if (activeOffscreenImageIndex <= headIndex) {
                    synchronized (disposed) {
                        for (int i = activeOffscreenImageIndex; i < headIndex; i++) {
                            disposed.addLast(offscreenImages[i]);
                        }
                    }
                    activeOffscreenImageIndex = headIndex + 1;
                }
            }
            offscreenImages[headIndex] = im;
        }
    }

    public synchronized Image getCurrentImage() {
        if (headIndex < 0)
            return null;
        return offscreenImages[activeOffscreenImageIndex];
    }

    public Deque<Image> getDisposedImages() {
        return disposed;
    }
    
    public synchronized void next() {
        if (headIndex < 0)
            throw new IllegalStateException();
        if (activeOffscreenImageIndex != headIndex) {
            disposed.addLast(offscreenImages[activeOffscreenImageIndex]);
            ++activeOffscreenImageIndex;
            if (activeOffscreenImageIndex >= offscreenImages.length) {
                activeOffscreenImageIndex = 0;
            }
        }
    }
}
