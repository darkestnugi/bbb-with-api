package com.example.bbbwithapi.helper;

import android.text.InputFilter;
import android.text.Spanned;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalFormatInputFilter implements InputFilter {
    //numeric
    Pattern mPatternV1 = Pattern.compile("(0|[1-9]+[0-9]*)?");
    //decimal
    Pattern mPatternV2 = Pattern.compile("(0|[1-9]+[0-9]*)?(\\.[0-9]{0,2})?");

    @Override
    public CharSequence filter(
            CharSequence source,
            int start,
            int end,
            Spanned dest,
            int dstart,
            int dend
    ) {
        try {
            String result =
                    dest.subSequence(0, dstart)
                            + source.toString()
                            + dest.subSequence(dend, dest.length());

            Matcher matcher = mPatternV2.matcher(result);

            if (!matcher.matches()) return dest.subSequence(dstart, dend);

            return null;
        } catch (Exception err) {
            return null;
        }
    }
}
