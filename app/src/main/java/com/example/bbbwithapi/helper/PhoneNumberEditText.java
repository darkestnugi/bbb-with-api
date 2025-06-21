package com.example.bbbwithapi.helper;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

public class PhoneNumberEditText extends androidx.appcompat.widget.AppCompatEditText {
    private static String prefix = "+62";
    private static final int MAX_LENGTH = 20;
    private static final int MAX_DECIMAL = 0;
    private PhoneNumberTextWatcher phone_numberTextWatcher = new PhoneNumberTextWatcher(this, prefix);

    public PhoneNumberEditText(Context context) {
        this(context, null);
    }

    public PhoneNumberEditText(Context context, AttributeSet attrs) {
        this(context, attrs, androidx.appcompat.R.attr.editTextStyle);
    }

    public PhoneNumberEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setInputType(InputType.TYPE_CLASS_NUMBER);
        this.setHint(prefix);
        this.setFilters(new InputFilter[] { new InputFilter.LengthFilter(MAX_LENGTH) });
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        if (focused) {
            this.addTextChangedListener(phone_numberTextWatcher);
        } else {
            this.removeTextChangedListener(phone_numberTextWatcher);
        }
        handleCasePhoneNumberEmpty(focused);
    }

    /**
     * When phone_number empty <br/>
     * + When focus EditText, set the default text = prefix (ex: VND) <br/>
     * + When EditText lose focus, set the default text = "", EditText will display hint (ex:VND)
     */
    private void handleCasePhoneNumberEmpty(boolean focused) {
        if (focused) {
            if (getText().toString().isEmpty()) {
                setText(prefix);
            }
        } else {
            if (getText().toString().equals(prefix)) {
                setText("");
            }
        }
    }

    private static class PhoneNumberTextWatcher implements TextWatcher {
        private final EditText editText;
        private String previousCleanString;
        private String prefix;

        PhoneNumberTextWatcher(EditText editText, String prefix) {
            this.editText = editText;
            this.prefix = prefix;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // do nothing
        }

        @Override
        public void afterTextChanged(Editable editable) {
            try {
                String str = editable.toString();

                if (str.length() < prefix.length()) {
                    editText.setText(prefix);
                    editText.setSelection(prefix.length());
                    return;
                }

                if (str.equals(prefix)) {
                    return;
                }

                // cleanString this the string which not contain prefix and ,
                String cleanString = str.replace(prefix, "");

                if (cleanString.length() >= 1 && cleanString.substring(0, 1).equals("0"))  {
                    cleanString = cleanString.substring(1);
                }

                if (cleanString.length() >= 2 && cleanString.substring(0, 2).equals("62"))  {
                    cleanString = cleanString.substring(2);
                }

                // for prevent afterTextChanged recursive call
                if (cleanString.equals(previousCleanString) || cleanString.isEmpty()) {
                    return;
                }

                previousCleanString = cleanString;

                String formattedString = prefix + cleanString;

                editText.removeTextChangedListener(this); // Remove listener
                editText.setText(formattedString);
                handleSelection();
                editText.addTextChangedListener(this); // Add back the listener
            } catch (Exception err) {
                editText.setText(prefix);
                editText.setSelection(prefix.length());
                return;
            }
        }

        private void handleSelection() {
            if (editText.getText().length() <= MAX_LENGTH) {
                editText.setSelection(editText.getText().length());
            } else {
                editText.setSelection(MAX_LENGTH);
            }
        }
    }
}
