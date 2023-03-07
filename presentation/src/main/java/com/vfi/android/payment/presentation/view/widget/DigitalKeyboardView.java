package com.vfi.android.payment.presentation.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;

import com.vfi.android.payment.R;

import java.util.List;


public class DigitalKeyboardView extends KeyboardView {
	
	public DigitalKeyboardView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}


	@Override
    public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
        List<Key> keys = getKeyboard().getKeys();
        for(Key key: keys) {
            if(key.label != null && key.label.equals("DE")) {
				resetOKBtn(key, canvas);
			}
        }
    }
	
	/**
	 * 绘制OK键的点9图
	 * @author Song
	 * @param key
	 * @param canvas
	 */
	private void resetOKBtn(Key key, Canvas canvas) {
//		LogUtil.d("TAG", "x=" + key.x + " y=" + key.y);
//		LogUtil.d("TAG", "width=" + canvas.getWidth() + " height=" + canvas.getHeight());
//		LogUtil.d("TAG", "cavasX=" + this.getX() + " cavasY=" + this.getY());
		float deleteCenterX = key.x + (key.width / 2.0F);
		float deleteCenterY = key.y + (key.height / 2.0F);
		drawdelete(canvas, deleteCenterX, deleteCenterY, 23);
	    /*
		//将OK键重新绘制
        NinePatchDrawable npd = (NinePatchDrawable) context.getResources().getDrawable(R.drawable.ok_undefined);
	    npd.setBounds(key.x, key.y + 1, key.x + key.width, key.y + key.height + 1);
	    npd.draw(canvas);
	    */
	}

	private void drawdelete(Canvas canvas, float deleteCenterX, float deleteCenterY, float radius)
	{
		Path path = new Path();
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setAlpha(255);
		paint.setColor(getResources().getColor(R.color.color_gray_blue, null));

		float leftPointOffset = radius / 3.0F * 2.0F;
		path.reset();
		path.moveTo(deleteCenterX - radius - leftPointOffset, deleteCenterY);
		path.lineTo(deleteCenterX - radius, deleteCenterY - radius);
		path.lineTo(deleteCenterX + radius, deleteCenterY - radius);
		path.lineTo(deleteCenterX + radius, deleteCenterY + radius);
		path.lineTo(deleteCenterX - radius, deleteCenterY + radius);
		path.close();
		canvas.drawPath(path, paint);

		float cancelRadius = radius / 2.0F;
		paint.setColor(Color.WHITE);
		paint.setStrokeWidth(4.0F);
		canvas.drawLine(deleteCenterX - cancelRadius, deleteCenterY - cancelRadius, deleteCenterX + cancelRadius, deleteCenterY + cancelRadius, paint);
		canvas.drawLine(deleteCenterX - cancelRadius, deleteCenterY + cancelRadius, deleteCenterX + cancelRadius, deleteCenterY - cancelRadius, paint);
	}
}