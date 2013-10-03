package ar.sgt.android.smsscheduler.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import ar.sgt.android.smsscheduler.R;

public class RecipientsAutoCompleteTextView extends MultiAutoCompleteTextView
		implements OnItemClickListener {

	public final static char TOKEN = ';';
	
	public RecipientsAutoCompleteTextView(Context context) {
		super(context);
		init(context);
	}

	public RecipientsAutoCompleteTextView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public RecipientsAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		setOnItemClickListener(this);
		addTextChangedListener(textWatcher);
		loadContacts();
		//setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
		setTokenizer(new Tokenizer() {
			@Override
			public CharSequence terminateToken(CharSequence text) {
				int i = text.length();
				while (i > 0 && text.charAt(i - 1) == TOKEN) {
				    i--;
				}
				if (i > 0 && text.charAt(i - 1) == TOKEN) {
				    return text;
				} else {
				    if (text instanceof Spanned) {
				        SpannableString sp = new SpannableString(text + Character.toString(TOKEN));
				        TextUtils.copySpansFrom((Spanned) text, 0, text.length(),
				                Object.class, sp, 0);
				        return sp;
				    } else {
				        return text + Character.toString(TOKEN);
				    }
				}
			}
			@Override
			public int findTokenStart(CharSequence text, int cursor) {
				int i = cursor;
				while (i > 0 && text.charAt(i - 1) != TOKEN) {
				    i--;
				}
				while (i < cursor && text.charAt(i) == TOKEN) {
				    i++;
				}
				return i;
			}
			@Override
			public int findTokenEnd(CharSequence text, int cursor) {
				int i = cursor;
				int len = text.length();
				while (i < len) {
				    if (text.charAt(i) == TOKEN) {
				        return i;
				    } else {
				        i++;
				    }
				}
				return len;
			}
		});
	}

	private void loadContacts() {
		setAdapter(new ContactListAdapter(getContext()));
	}

	/*
	 * TextWatcher, If user type any name and press space then following code
	 * will regenerate chips
	 */
	private TextWatcher textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (count >= 1) {
				if (s.charAt(start) == TOKEN)
					setChips(); // generate chips
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {
		}
	};

	/* This function has whole logic for chips generate */
	public void setChips() {
		if (getText().toString().contains(Character.toString(TOKEN))) {// check space in string
			SpannableStringBuilder ssb = new SpannableStringBuilder(getText());
			// split string wich comma
			String chips[] = getText().toString().trim().split(Character.toString(TOKEN));
			int x = 0;
			// loop will generate ImageSpan for every country name separated by
			// comma
			for (String c : chips) {
				// inflate chips_edittext layout
				LayoutInflater lf = (LayoutInflater) getContext()
						.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
				TextView textView = (TextView) lf.inflate(
						R.layout.recipient_edittext, null);
				textView.setText(c); // set text
				// TODO: recipient image
				//setFlags(textView, c); // set flag image
				// capture bitmap of generated textview
				int spec = MeasureSpec.makeMeasureSpec(0,
						MeasureSpec.UNSPECIFIED);
				textView.measure(spec, spec);
				textView.layout(0, 0, textView.getMeasuredWidth(),
						textView.getMeasuredHeight());
				
				Bitmap b = Bitmap.createBitmap(textView.getWidth(),
						textView.getHeight(), Bitmap.Config.ARGB_8888);
				Canvas canvas = new Canvas(b);
				canvas.translate(-textView.getScrollX(), -textView.getScrollY());
				textView.draw(canvas);
				textView.setDrawingCacheEnabled(true);
				Bitmap cacheBmp = textView.getDrawingCache();
				Bitmap viewBmp = cacheBmp.copy(Bitmap.Config.ARGB_8888, true);
				textView.destroyDrawingCache(); // destory drawable
				// create bitmap drawable for imagespan
				BitmapDrawable bmpDrawable = new BitmapDrawable(viewBmp);
				bmpDrawable.setBounds(0, 0, bmpDrawable.getIntrinsicWidth(),
						bmpDrawable.getIntrinsicHeight());
				// create and set imagespan
				ssb.setSpan(new ImageSpan(bmpDrawable), x, x + c.length(),
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				x = x + c.length() + 1;
			}
			// set chips span
			setText(ssb);
			// move cursor to last
			setSelection(getText().length());
		}

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		setChips();
	}

}
