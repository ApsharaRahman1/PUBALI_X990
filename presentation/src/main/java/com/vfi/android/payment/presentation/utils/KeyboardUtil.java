package com.vfi.android.payment.presentation.utils;

import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.view.View;
import android.widget.TextView;

import com.vfi.android.domain.entities.consts.TAGS;
import com.vfi.android.domain.utils.LogUtil;
import com.vfi.android.domain.utils.StringUtil;
import com.vfi.android.payment.presentation.view.adapters.PasswdListAdapter;

import java.util.List;

/**
 * @Author:
 * @date
 * @Description: 键盘功能控制工具类
 */
public class KeyboardUtil {
	private KeyboardView keyboardView;

	public final static int TYPE_NUMBER = 1; // number
	public final static int TYPE_PRICE = 2; // price
	public final static int TYPE_TRACE_NUM = 3; // trace/invoice number
	public final static int TYPE_CARD_NUM = 4; // card number
	public final static int TYPE_APPROVAL_CODE = 5; //  approval code
	public final static int TYPE_CARD_EXPIRY_DATE = 6; // card expiry date
	public final static int TYPE_ORG_TRANS_DATE = 7; // original tranaction date
	public final static int TYPE_REFERENCE_NUM = 8; // reference number
	public final static int TYPE_PAYMENT_NUM = 9;//paymentID
	public final static int TYPE_MONTH = 10;// intallment number of month
	public final static int TYPE_PROMOTION_CODE = 11;// installment promotion code
	public final static int TYPE_LOG_EXPORT_PASSWD = 12;// log export passwd
	public final static int TYPE_POINT = 13;// point of OLS trx
	public final static int TYPE_IPP_PRODUCT_SN = 14 ; // IPP productSN
	public final static int TYPE_AMEX_4DBC = 15;
	public final static int TYPE_CVV2 = 16; // cvv2

	private final int MAX_TRACE_NUM_LEN = 6;
	private final int MAX_CARD_NUM_LEN = 19;
	private final int MAX_APPROVAL_CODE_LEN = 6;
	private final int MAX_REFERENCE_NUM_LEN = 12;
	private final int MAX_PAYMENT_NUM_LEN = 32;
	private final int MAX_MONTH_LEN = 2;
	private final int MAX_PROMOTION_CODE = 9;
	private final int MAX_POINT = 10;
	private final int MAX_4DBC = 4;
	private final int MAX_CVV2_LEN = 9;

	private KeyboardListener keyboardListener;

	public interface KeyboardListener {
		void confirmKeyPressed();
		void cancelKeyPressed();
		void numberKeyPressed(int num);
		void pointKeyPressed();
		void deleteKeyPressed();
		void changeKeyboardPressed();
		void shiftPressed();
		void engWordsKeyPressed(String ch);
	}

	public KeyboardUtil(KeyboardView keyboardView, Keyboard keyboard, TextView textView, int type, int maxTextLen) {
		if (type == TYPE_PRICE) {
		    initKeyboard(keyboardView, keyboard);
			this.keyboardListener = new KeyboardAmountTextViewListener(textView, maxTextLen);
			keyboardView.setOnKeyboardActionListener(listener);
		} else {
			initKeyboradAndSetListener(keyboardView, keyboard, textView, type);
		}
    }

	public KeyboardUtil(KeyboardView keyboardView, Keyboard keyboard, TextView textView, int type) {
		initKeyboradAndSetListener(keyboardView, keyboard, textView, type);
	}

	private void initKeyboradAndSetListener(KeyboardView keyboardView, Keyboard keyboard, TextView textView, int type) {
		initKeyboard(keyboardView, keyboard);
		if (type == TYPE_PRICE) {
			this.keyboardListener = new KeyboardAmountTextViewListener(textView, 12);
		} else if (type == TYPE_TRACE_NUM) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, MAX_TRACE_NUM_LEN);
		} else if (type == TYPE_NUMBER) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, 20);
		} else if (type == TYPE_CARD_NUM) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, MAX_CARD_NUM_LEN);
		} else if (type == TYPE_APPROVAL_CODE) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, MAX_APPROVAL_CODE_LEN);
		} else if (type == TYPE_CARD_EXPIRY_DATE) {
			this.keyboardListener = new KeyboardTextDateFourListener(textView);
		} else if (type == TYPE_ORG_TRANS_DATE) {
			this.keyboardListener = new KeyboardTextDateFourListener(textView);
		} else if (type == TYPE_REFERENCE_NUM) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, MAX_REFERENCE_NUM_LEN);
		} else if (type == TYPE_PAYMENT_NUM) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, MAX_PAYMENT_NUM_LEN);
		} else if (type == TYPE_MONTH) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, MAX_MONTH_LEN);
		} else if (type == TYPE_PROMOTION_CODE) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, MAX_PROMOTION_CODE);
		} else if (type == TYPE_POINT) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView,MAX_POINT);
		} else if (type == TYPE_AMEX_4DBC) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, MAX_4DBC);
		} else if (type == TYPE_CVV2) {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, MAX_CVV2_LEN);
		} else {
			this.keyboardListener = new KeyboardTextViewNumberListener(textView, 19);
		}
		keyboardView.setOnKeyboardActionListener(listener);
	}

	public KeyboardUtil(KeyboardView keyboardView, Keyboard digitKeyboard, Keyboard fullKeyboard, TextView textView, int type) {
	    initKeyboard(keyboardView, digitKeyboard);
	    if (type == TYPE_APPROVAL_CODE) {
			this.keyboardListener = new FullKeyboardListener(digitKeyboard, fullKeyboard, textView, MAX_APPROVAL_CODE_LEN);
		} else {
			this.keyboardListener = new FullKeyboardListener(digitKeyboard, fullKeyboard, textView, 25);
		}
	    keyboardView.setOnKeyboardActionListener(listener);
    }

	public KeyboardUtil(KeyboardView keyboardView, Keyboard digitKeyboard, Keyboard fullKeyboard, PasswdListAdapter passwdListAdapter, int type) {
		initKeyboard(keyboardView, digitKeyboard);
		if (type == TYPE_LOG_EXPORT_PASSWD) {
			this.keyboardListener = new FullKeyboardPasswdListener(digitKeyboard, fullKeyboard, passwdListAdapter);
		} else if (type == TYPE_IPP_PRODUCT_SN) {
			this.keyboardListener = new FullKeyboardPasswdListener(digitKeyboard, fullKeyboard, passwdListAdapter);
		} else {
			this.keyboardListener = new FullKeyboardPasswdListener(digitKeyboard, fullKeyboard, passwdListAdapter);
		}
		keyboardView.setOnKeyboardActionListener(listener);
	}

    public KeyboardUtil(KeyboardView keyboardView, Keyboard keyboard, PasswdListAdapter passwdListAdapter) {
        initKeyboard(keyboardView, keyboard);
		this.keyboardListener = new KeyboardPasswdListener(passwdListAdapter);
        keyboardView.setOnKeyboardActionListener(listener);
    }

	public KeyboardUtil(KeyboardView keyboardView, Keyboard keyboard) {
		initKeyboard(keyboardView, keyboard);
	}

	private void initKeyboard(KeyboardView keyboardView, Keyboard keyboard) {
		//此处可替换键盘xml
		this.keyboardView = keyboardView;
		try {
			keyboardView.setKeyboard(keyboard);
			keyboardView.setEnabled(true);
			keyboardView.setPreviewEnabled(false); // 禁用按键预览
		} catch (Exception e) {
			LogUtil.e("TAG", "Init keyboard failed.");
			e.printStackTrace();
		}
	}

	public void setKeyboardListener(KeyboardListener keyboardListener) {
		this.keyboardListener = keyboardListener;
	}

	private OnKeyboardActionListener listener = new OnKeyboardActionListener() {
		@Override
		public void swipeUp() {
		}

		@Override
		public void swipeRight() {
		}

		@Override
		public void swipeLeft() {
		}

		@Override
		public void swipeDown() {
		}

		@Override
		public void onText(CharSequence text) {
		}

		@Override
		public void onRelease(int primaryCode) {
		}

		@Override
		public void onPress(int primaryCode) {
		}

		@Override
		public void onKey(int primaryCode, int[] keyCodes) {
			LogUtil.d("TAG", "onKey primaryCode=" + primaryCode + " keyCodes=" + keyCodes.toString());
			if (primaryCode == Keyboard.KEYCODE_CANCEL) {// 完成
				LogUtil.d("TAG", "cancelKeyPressed");
				if (keyboardListener != null) {
					try {
						keyboardListener.cancelKeyPressed();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (primaryCode == Keyboard.KEYCODE_DELETE) {// 回退
				LogUtil.d("TAG", "deleteKeyPressed");
				if (keyboardListener != null) {
					try {
						keyboardListener.deleteKeyPressed();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (primaryCode == Keyboard.KEYCODE_SHIFT) {// 大小写切换
				keyboardListener.shiftPressed();
			} else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {// 键盘切换
				keyboardListener.changeKeyboardPressed();
			} else if (primaryCode == 57419) { // go left
			} else if (primaryCode == 57421) { // go right
			} else if (primaryCode == 46) {	   // 小数点
				LogUtil.d("TAG", "pointKeyPressed");
				if (keyboardListener != null) {
					try {
						keyboardListener.pointKeyPressed();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (primaryCode >= 48 && primaryCode <=57){
				LogUtil.d("TAG", "numberKeyPressed primaryCode=" + primaryCode);
				if (keyboardListener != null) {
					try {
						keyboardListener.numberKeyPressed(primaryCode);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if ((primaryCode >= 97 && primaryCode <= 122) || (primaryCode >= 65 && primaryCode <= 90)) {
				LogUtil.d("TAG", "engWordsKeyPressed primaryCode=" + primaryCode);
				if (keyboardListener != null) {
					try {
						keyboardListener.engWordsKeyPressed(Character.toString((char)primaryCode));
					} catch (Exception e) {
					    e.printStackTrace();
					}
				}
			}
		}
	};


    private class KeyboardAmountTextViewListener implements KeyboardListener {
        private StringBuffer stringBuffer;
        private TextView keyboardInputView;
		private int maxTextViewLen = 0;

        public KeyboardAmountTextViewListener(TextView textView, int maxTextViewLen) {
            this.keyboardInputView = textView;
            this.maxTextViewLen = maxTextViewLen;
            stringBuffer = new StringBuffer();
            showAmount();
        }

        @Override
        public void confirmKeyPressed() {

        }

        @Override
        public void cancelKeyPressed() {

        }

        @Override
        public void numberKeyPressed(int number) {
        	LogUtil.d(TAGS.UILevel, "========number=" + number);
			LogUtil.d(TAGS.UILevel, "========len=" + stringBuffer.length() + " maxTextViewLen=" + maxTextViewLen);
			if (stringBuffer.length() < maxTextViewLen) {
				final int ASC_NUMBER_ZERO = 48;

				if ((stringBuffer.length() == 0 && number == ASC_NUMBER_ZERO)
						|| isInvalidAmountMaxLen()) {
					return;
				}

				stringBuffer.append("" + (number - ASC_NUMBER_ZERO));
				showAmount();
			}
        }

        @Override
        public void pointKeyPressed() {
        }

        @Override
        public void deleteKeyPressed() {
            if (stringBuffer.length() > 0) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                showAmount();
            } else if (stringBuffer.length() == 0 && !keyboardInputView.getText().toString().equals("0.00")) {
            	String amount = keyboardInputView.getText().toString();
            	amount = amount.replace(",", "");
				amount = amount.replace(".", "");
				if (amount.length() > 1) {
					amount = amount.substring(0, amount.length() - 1);
					long amountLong = StringUtil.parseLong(amount, 0);
					if (amountLong > 0) {
						stringBuffer.append("" + amountLong);
					}
					showAmount();
				}
			}
		}

		@Override
		public void changeKeyboardPressed() {

		}

		@Override
		public void shiftPressed() {

		}

		@Override
		public void engWordsKeyPressed(String ch) {

		}

		private boolean isInvalidAmountMaxLen() {
            final int MAX_AMOUNT_LEN = 12;

			int amountLen = stringBuffer.length();
			return amountLen >= MAX_AMOUNT_LEN ? true : false;
		}

		private void showAmount() {
            String amount = TvShowUtil.formatAmount(stringBuffer.toString());
            if (stringBuffer.length() <= 4) {
                //do nothing
            } else if (stringBuffer.length() > 4 && stringBuffer.length() <= 6) {
                keyboardInputView.setTextSize(78);
            } else if (stringBuffer.length() > 6 && stringBuffer.length() <= 8) {
                keyboardInputView.setTextSize(66);
            } else if (stringBuffer.length() > 8 && stringBuffer.length() <= 10) {
                keyboardInputView.setTextSize(54);
            } else if (stringBuffer.length() > 10) {
                keyboardInputView.setTextSize(42);
            }

            if (stringBuffer.length() == 0) {
                keyboardInputView.setText("0.00");
            } else {
                keyboardInputView.setText(amount);
            }

        }
	}

    private class KeyboardPasswdListener implements KeyboardListener {
    	private  PasswdListAdapter passwdListAdapter;

    	public KeyboardPasswdListener(PasswdListAdapter passwdListAdapter) {
    	    this.passwdListAdapter = passwdListAdapter;
		}

		@Override
		public void confirmKeyPressed() {

		}

		@Override
		public void cancelKeyPressed() {

		}

		@Override
		public void numberKeyPressed(int num) {
			final int ASC_NUMBER_ZERO = 48;
			num = num - ASC_NUMBER_ZERO;
			passwdListAdapter.addOnePasswordChar("" + num);
		}

		@Override
		public void pointKeyPressed() {

		}

		@Override
		public void deleteKeyPressed() {
    	    passwdListAdapter.delOnePasswordChar();
		}

		@Override
		public void changeKeyboardPressed() {

		}

		@Override
		public void shiftPressed() {

		}

		@Override
		public void engWordsKeyPressed(String ch) {

		}
	}

	private class KeyboardTextViewNumberListener implements KeyboardListener {
    	private TextView showView;
    	private StringBuffer stringBuffer;
    	private int maxTextViewLen = 0;

        public KeyboardTextViewNumberListener(TextView view, int maxNumLen) {
            this.showView = view;
            stringBuffer = new StringBuffer();
            maxTextViewLen = maxNumLen;
            LogUtil.d("TAG", "Max text len=" + maxNumLen);
		}

		@Override
		public void confirmKeyPressed() {

		}

		@Override
		public void cancelKeyPressed() {

		}

		@Override
		public void numberKeyPressed(int num) {
        	if (stringBuffer.length() < maxTextViewLen) {
				final int ASC_NUMBER_ZERO = 48;
				String numStr = "" + (num - ASC_NUMBER_ZERO);

				stringBuffer.append(numStr);
				showView.setText(stringBuffer.toString());
			}
		}

		@Override
		public void pointKeyPressed() {

		}

		@Override
		public void deleteKeyPressed() {
        	int strBufLen = stringBuffer.length();

            if (strBufLen > 0) {
            	stringBuffer.delete(strBufLen - 1, strBufLen);
			}

			if (stringBuffer.length() == 0) {
            	showView.setText("");
			} else {
				showView.setText(stringBuffer.toString());
			}
		}

		@Override
		public void changeKeyboardPressed() {

		}

		@Override
		public void shiftPressed() {

		}

		@Override
		public void engWordsKeyPressed(String ch) {

		}
	}

	public class KeyboardTextDateFourListener implements KeyboardListener {
		private TextView showView;
		private StringBuffer stringBuffer;
		private int maxTextViewLen = 0;
		private final int MAX_DATE_FOUR_LEN = 4;

		public KeyboardTextDateFourListener(TextView textView) {
		    maxTextViewLen = MAX_DATE_FOUR_LEN;
		    stringBuffer = new StringBuffer();
		    this.showView = textView;
		}

		@Override
		public void confirmKeyPressed() {

		}

		@Override
		public void cancelKeyPressed() {

		}

		@Override
		public void numberKeyPressed(int num) {
			if (stringBuffer.length() < maxTextViewLen) {
				final int ASC_NUMBER_ZERO = 48;
				String numStr = "" + (num - ASC_NUMBER_ZERO);

				stringBuffer.append(numStr);
				LogUtil.d("TAG", "stringBuffer len=[" + stringBuffer.length() + "]");
				String showText = stringBuffer.toString() + "0000".substring(stringBuffer.length());
				showText = showText.substring(0, 2) + "/" + showText.substring(2);
				LogUtil.d("TAG", "showText=[" + showText + "]");
				showView.setText(showText);
			}
		}

		@Override
		public void pointKeyPressed() {

		}

		@Override
		public void deleteKeyPressed() {
			int strBufLen = stringBuffer.length();

			if (strBufLen > 0) {
				stringBuffer.delete(strBufLen - 1, strBufLen);
			}

			LogUtil.d("TAG", "stringBuffer len=[" + stringBuffer.length() + "]");
			String showText;
			if (stringBuffer.length() == 0) {
			    showText = "0000";
			} else {
				showText = stringBuffer.toString() + "0000".substring(stringBuffer.length());
			}
			showText = showText.substring(0, 2) + "/" + showText.substring(2);
			LogUtil.d("TAG", "showText=[" + showText + "]");
			showView.setText(showText);
		}

		@Override
		public void changeKeyboardPressed() {

		}

		@Override
		public void shiftPressed() {

		}

		@Override
		public void engWordsKeyPressed(String ch) {

		}
	}

	public class FullKeyboardListener implements KeyboardListener {
		TextView keyboardInputView;
		private StringBuffer stringBuffer;
		private int maxTextViewLen = 0;
    	private Keyboard digitKeyboard;
    	private Keyboard fullKeyboard;
    	boolean isCurrentDiagitKeyboard;
    	boolean isUpperMode;

        public FullKeyboardListener(Keyboard digitKeyboard, Keyboard fullKeyboard, TextView textView, int maxTextViewLen) {
            this.keyboardInputView = textView;
            this.digitKeyboard = digitKeyboard;
            this.fullKeyboard = fullKeyboard;
            isCurrentDiagitKeyboard = true;
            isUpperMode = false;
            stringBuffer = new StringBuffer();
            this.maxTextViewLen = maxTextViewLen;
		}

		@Override
		public void confirmKeyPressed() {
		}

		@Override
		public void cancelKeyPressed() {
		}

		@Override
		public void numberKeyPressed(int num) {
			if (stringBuffer.length() < maxTextViewLen) {
				final int ASC_NUMBER_ZERO = 48;
				String numStr = "" + (num - ASC_NUMBER_ZERO);

				stringBuffer.append(numStr);
				keyboardInputView.setText(stringBuffer.toString());
			}
		}

		@Override
		public void pointKeyPressed() {

		}

		@Override
		public void deleteKeyPressed() {
			int strBufLen = stringBuffer.length();

			if (strBufLen > 0) {
				stringBuffer.delete(strBufLen - 1, strBufLen);
			}

			if (stringBuffer.length() == 0) {
				keyboardInputView.setText("");
			} else {
				keyboardInputView.setText(stringBuffer.toString());
			}
		}

		@Override
		public void changeKeyboardPressed() {
            if (isCurrentDiagitKeyboard) {
                initKeyboard(keyboardView, fullKeyboard);
			} else {
				initKeyboard(keyboardView, digitKeyboard);
			}
		}

		@Override
		public void shiftPressed() {
			List<Keyboard.Key> keyList = fullKeyboard.getKeys();

			LogUtil.d("TAG", "isUpperMode=" + isUpperMode);
			if(isUpperMode){
				isUpperMode = false;
				for(Keyboard.Key key:keyList){
					if(key.label!=null && isword(key.label.toString())){
						key.label = key.label.toString().toLowerCase();
						key.codes[0] =  key.codes[0] + 32;
					}
				}
			}else{
				isUpperMode = true;
				for(Keyboard.Key key:keyList){
					if(key.label !=null && isword(key.label.toString())){
						key.label = key.label.toString().toUpperCase();
						key.codes[0] = key.codes[0] - 32;
					}
				}
			}

			initKeyboard(keyboardView, fullKeyboard);
		}

		@Override
		public void engWordsKeyPressed(String ch) {
			if (stringBuffer.length() < maxTextViewLen) {
			    if (isUpperMode) {
			        stringBuffer.append(ch.toUpperCase());
				} else {
			    	stringBuffer.append(ch.toLowerCase());
				}

				keyboardInputView.setText(stringBuffer.toString());
			}
		}

	}

	public class FullKeyboardPasswdListener implements KeyboardListener {
    	private PasswdListAdapter passwdListAdapter;
    	private Keyboard digitKeyboard;
    	private Keyboard fullKeyboard;
    	boolean isCurrentDiagitKeyboard;
    	boolean isUpperMode;

        public FullKeyboardPasswdListener(Keyboard digitKeyboard, Keyboard fullKeyboard, PasswdListAdapter passwdListAdapter) {
            this.passwdListAdapter = passwdListAdapter;
            this.digitKeyboard = digitKeyboard;
            this.fullKeyboard = fullKeyboard;
            isCurrentDiagitKeyboard = true;
            isUpperMode = false;
		}

		@Override
		public void confirmKeyPressed() {
		}

		@Override
		public void cancelKeyPressed() {
		}

		@Override
		public void numberKeyPressed(int num) {
			final int ASC_NUMBER_ZERO = 48;
			num = num - ASC_NUMBER_ZERO;
			passwdListAdapter.addOnePasswordChar("" + num);
		}

		@Override
		public void pointKeyPressed() {

		}

		@Override
		public void deleteKeyPressed() {
			passwdListAdapter.delOnePasswordChar();
		}

		@Override
		public void changeKeyboardPressed() {
            if (isCurrentDiagitKeyboard) {
                initKeyboard(keyboardView, fullKeyboard);
			} else {
				initKeyboard(keyboardView, digitKeyboard);
			}
		}

		@Override
		public void shiftPressed() {
			List<Keyboard.Key> keyList = fullKeyboard.getKeys();

			LogUtil.d("TAG", "isUpperMode=" + isUpperMode);
			if(isUpperMode){
				isUpperMode = false;
				for(Keyboard.Key key:keyList){
					if(key.label!=null && isword(key.label.toString())){
						key.label = key.label.toString().toLowerCase();
						key.codes[0] =  key.codes[0] + 32;
					}
				}
			}else{
				isUpperMode = true;
				for(Keyboard.Key key:keyList){
					if(key.label !=null && isword(key.label.toString())){
						key.label = key.label.toString().toUpperCase();
						key.codes[0] = key.codes[0] - 32;
					}
				}
			}

			initKeyboard(keyboardView, fullKeyboard);
		}

		@Override
		public void engWordsKeyPressed(String ch) {
			if (isUpperMode) {
				passwdListAdapter.addOnePasswordChar("" + ch.toUpperCase());
			} else {
				passwdListAdapter.addOnePasswordChar("" + ch.toLowerCase());
			}
		}
	}

	public void showKeyboard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == View.GONE || visibility == View.INVISIBLE) {
			keyboardView.setVisibility(View.VISIBLE);
		}
	}

	public void hideKeyboard() {
		int visibility = keyboardView.getVisibility();
		if (visibility == View.VISIBLE) {
			keyboardView.setVisibility(View.INVISIBLE);
		}
	}

	private boolean isword(String str) {
		String wordstr = "abcdefghijklmnopqrstuvwxyz";
		if (str.length() == 1 && wordstr.indexOf(str.toLowerCase()) > -1) {
			return true;
		}
		return false;
	}
}
