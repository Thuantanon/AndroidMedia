package com.cxh.androidmedia.render_old;

import java.util.List;

/**
 * Created by Cxh
 * Time : 2020-08-31  23:46
 * Desc : 流水线形式的绘制
 */
public interface IDrawableProviders {

    List<BaseFboDrawable> getDrawables(int w, int h);
}
