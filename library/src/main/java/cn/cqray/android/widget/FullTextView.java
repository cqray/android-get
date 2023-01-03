package cn.cqray.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.DynamicDrawableSpan;
import android.util.AttributeSet;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.WeakHashMap;

import cn.cqray.android.R;

/**
 * 图文混排TextView，防止不满一行换行
 * @author Cqray
 */
public class FullTextView extends androidx.appcompat.widget.AppCompatTextView {

    /** 缓存测量过的数据 **/
    private static WeakHashMap<CharSequence, MeasuredData> sMeasuredData = new WeakHashMap<>();
    private static int sHashIndex = 0;
    /** 段间距,-1为默认 **/
    private int mParagraphSpacing;
    /** 只有一行时的宽度 **/
    private int mOneLineWidth = -1;
    /** 已绘的行中最宽的一行的宽度 **/
    private float mLineWidthMax = -1;
    /** 是否使用默认的TextView **/
    private Boolean mUseDefault;
    /** 存储当前文本内容，每个item为一行 **/
    private ArrayList<Line> mContentList = new ArrayList<>();
    /** 存储当前文本内容,每个item为一个字符或者一个SpanObject **/
    private final ArrayList<Object> mItemList = new ArrayList<>();
    private final Rect mTextBackgroundColorRect = new Rect();
    private final Paint mTextBackgroundColorPaint = new Paint();
    /** 用于测量span高度 **/
    private final Paint.FontMetricsInt mSpanFmInt = new Paint.FontMetricsInt();

    public FullTextView(Context context) {
        this(context, null);
    }

    public FullTextView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public FullTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.FullTextView);
        mParagraphSpacing = ta.getDimensionPixelSize(R.styleable.FullTextView_sParagraphSpacing, -1);
        mUseDefault = ta.getBoolean(R.styleable.FullTextView_sUseDefault, false);
        ta.recycle();
        if (!mUseDefault) {
            calculateSpanList(getText());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mUseDefault) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            return;
        }
        int width = 0, height = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        switch (widthMode) {
            case MeasureSpec.EXACTLY:
            case MeasureSpec.AT_MOST:
                width = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                width =  getContext().getResources().getDisplayMetrics().widthPixels;
                break;
            default:
                break;
        }
        if (getMaxWidth() > 0) {
            width = Math.min(width, getMaxWidth());
        }
        int realHeight = measureContentHeight((int) width);
        // 如果实际行宽少于预定的宽度，减少行宽以使其内容横向居中
        int leftPadding = getCompoundPaddingLeft();
        int rightPadding = getCompoundPaddingRight();
        width = Math.min(width, (int) mLineWidthMax + leftPadding + rightPadding);
        if (mOneLineWidth > -1) {
            width = mOneLineWidth;
        }
        switch (heightMode) {
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                height = realHeight;
                break;
            default:
                break;
        }
        height += getCompoundPaddingTop() + getCompoundPaddingBottom();
        height = Math.max(height, getMinHeight());
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mUseDefault) {
            super.onDraw(canvas);
            return;
        }
        if (mContentList.isEmpty()) {
            return;
        }
        int width;
        Object ob;
        int leftPadding = getCompoundPaddingLeft();
        int topPadding = getCompoundPaddingTop();
        float lineSpacing = getLineSpacingExtra();
        float height = 0 + topPadding + lineSpacing;
        // 只有一行时
        if (mOneLineWidth != -1) {
            height = getMeasuredHeight() / 2f - mContentList.get(0).height / 2;
        }
        CharSequence text = getText();
        for (Line aContentList : mContentList) {
            // 绘制一行
            float realDrewWidth = leftPadding;
            // 是否换新段落
            boolean newParagraph = false;
            for (int j = 0; j < aContentList.line.size(); j++) {
                ob = aContentList.line.get(j);
                width = aContentList.widthList.get(j);
                FontMetrics fontMetrics = getPaint().getFontMetrics();
                float x = realDrewWidth;
                float y = height + aContentList.height - fontMetrics.descent;
                float top = y - aContentList.height;
                float bottom = y + fontMetrics.descent;
                if (ob instanceof String) {
                    canvas.drawText((String) ob, realDrewWidth, y, getPaint());
                    realDrewWidth += width;
                    if(((String)ob).endsWith("\n") && j == aContentList.line.size()-1){
                        newParagraph = true;
                    }
                } else if (ob instanceof SpanObject) {
                    Object span = ((SpanObject) ob).span;
                    if (span instanceof DynamicDrawableSpan) {
                        int start = ((Spanned) text).getSpanStart(span);
                        int end = ((Spanned) text).getSpanEnd(span);
                        ((DynamicDrawableSpan) span).draw(canvas, text, start, end, (int) x, (int) top, (int) y, (int) bottom, getPaint());
                        realDrewWidth += width;
                    } else if (span instanceof BackgroundColorSpan) {
                        int textHeight = (int) getTextSize();
                        mTextBackgroundColorPaint.setColor(((BackgroundColorSpan) span).getBackgroundColor());
                        mTextBackgroundColorPaint.setStyle(Style.FILL);
                        mTextBackgroundColorRect.left = (int) realDrewWidth;
                        mTextBackgroundColorRect.top = (int) (height + aContentList.height - textHeight - fontMetrics.descent);
                        mTextBackgroundColorRect.right = mTextBackgroundColorRect.left + width;
                        mTextBackgroundColorRect.bottom = (int) (height + aContentList.height + lineSpacing - fontMetrics.descent);
                        canvas.drawRect(mTextBackgroundColorRect, mTextBackgroundColorPaint);
                        canvas.drawText(((SpanObject) ob).source.toString(), realDrewWidth, height + aContentList.height - fontMetrics.descent, getPaint());
                        realDrewWidth += width;
                    } else {
                        // 做字符串处理
                        canvas.drawText(((SpanObject) ob).source.toString(), realDrewWidth, height + aContentList.height - fontMetrics.descent, getPaint());
                        realDrewWidth += width;
                    }
                }
            }
            // 如果要绘制段间距
            if (newParagraph) {
                height += aContentList.height + mParagraphSpacing;
            } else {
                height += aContentList.height + lineSpacing;
            }
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(text, type);
        if (mUseDefault != null && !mUseDefault) {
            // mUserDefault == null 代表还未初始化完成
            calculateSpanList(text);
        }
    }

    public void setText(CharSequence text, boolean useDefault) {
        mUseDefault = useDefault;
        setText(text);
    }

    public void setParagraphSpacing(int spacing) {
        mParagraphSpacing = spacing;
    }

    public void setUseDefault(boolean useDefault) {
        mUseDefault = useDefault;
        calculateSpanList(getText());
    }

    /**
     * 用于带ImageSpan的文本内容所占高度测量
     *
     * @param maxWidth 预定的宽度
     * @return 所需的高度
     */
    private int measureContentHeight(int maxWidth) {
        int cachedHeight = getCachedData(getText(), maxWidth);
        if (cachedHeight > 0) {
            return cachedHeight;
        }
        if (TextUtils.isEmpty(getText())) {
            return -1;
        }
        FontMetrics fontMetrics = getPaint().getFontMetrics();
        float obWidth = 0, obHeight = 0;
        float textSize = getTextSize();
        // 行间距
        float lineSpacing = getLineSpacingExtra();
        // 行高
        float lineHeight = fontMetrics.bottom - fontMetrics.top;
        // 计算出的所需高度
        float height = lineSpacing;
        // 左右间距
        int leftPadding = getCompoundPaddingLeft();
        int rightPadding = getCompoundPaddingRight();
        // 已绘制宽度
        float drewWidth = 0;
        boolean splitFlag = false;
        int width = maxWidth - leftPadding - rightPadding;
        mOneLineWidth = -1;
        mContentList.clear();
        CharSequence text = getText();
        Line line = new Line();
        for (int i = 0; i < mItemList.size(); i++) {
            Object ob = mItemList.get(i);
            if (ob instanceof String) {
                obWidth = getPaint().measureText((String) ob);
                obHeight = textSize;
                if ("\n".equals(ob)) {
                    // 遇到"\n"则换行
                    obWidth = width - drewWidth;
                }
            } else if (ob instanceof SpanObject) {
                Object span = ((SpanObject) ob).span;
                if (span instanceof DynamicDrawableSpan) {
                    int start = ((Spanned) text).getSpanStart(span);
                    int end = ((Spanned) text).getSpanEnd(span);
                    obWidth = ((DynamicDrawableSpan) span).getSize(getPaint(), text, start, end, mSpanFmInt);
                    obHeight = Math.abs(mSpanFmInt.top) + Math.abs(mSpanFmInt.bottom);
                    if (obHeight > lineHeight) {
                        lineHeight = obHeight;
                    }
                } else if (span instanceof BackgroundColorSpan) {
                    String str = ((SpanObject) ob).source.toString();
                    obWidth = getPaint().measureText(str);
                    obHeight = textSize;
                    // 如果太长,拆分
                    int k = str.length() - 1;
                    while (width - drewWidth < obWidth) {
                        obWidth = getPaint().measureText(str.substring(0, k--));
                    }
                    if (k < str.length() - 1) {
                        splitFlag = true;
                        SpanObject so1 = new SpanObject();
                        so1.start = ((SpanObject) ob).start;
                        so1.end = so1.start + k;
                        so1.source = str.substring(0, k + 1);
                        so1.span = ((SpanObject) ob).span;
                        SpanObject so2 = new SpanObject();
                        so2.start = so1.end;
                        so2.end = ((SpanObject) ob).end;
                        so2.source = str.substring(k + 1);
                        so2.span = ((SpanObject) ob).span;
                        ob = so1;
                        mItemList.set(i, so2);
                        i--;
                    }
                } else {
                    //做字符串处理
                    String str = ((SpanObject) ob).source.toString();
                    obWidth = getPaint().measureText(str);
                    obHeight = textSize;
                }
            }

            // 这一行满了，存入contentList,新起一行
            if (width - drewWidth < obWidth || splitFlag) {
                splitFlag = false;
                mContentList.add(line);
                if (drewWidth > mLineWidthMax) {
                    mLineWidthMax = drewWidth;
                }
                drewWidth = 0;
                // 判断是否有分段
                int objNum = line.line.size();
                if (mParagraphSpacing > 0
                        && objNum > 0
                        && line.line.get(objNum - 1) instanceof String
                        && "\n".equals(line.line.get(objNum - 1))) {
                    height += line.height + mParagraphSpacing;
                } else {
                    height += line.height + lineSpacing;
                }
                lineHeight = obHeight;
                line = new Line();
            }

            drewWidth += obWidth;

            if (ob instanceof String && line.line.size() > 0 && (line.line.get(line.line.size() - 1) instanceof String)) {
                int size = line.line.size();
                ob = String.valueOf(line.line.get(size - 1)) + ob;
                obWidth = obWidth + line.widthList.get(size - 1);
                line.line.set(size - 1, ob);
                line.widthList.set(size - 1, (int) obWidth);
            } else {
                line.line.add(ob);
                line.widthList.add((int) obWidth);
            }
            line.height = (int) lineHeight;
        }
        if (drewWidth > mLineWidthMax) {
            mLineWidthMax = drewWidth;
        }
        if (line.line.size() > 0) {
            mContentList.add(line);
            height += lineHeight + lineSpacing;
        }
        if (mContentList.size() <= 1) {
            mOneLineWidth = (int) drewWidth + leftPadding + rightPadding;
            height = lineSpacing + lineHeight + lineSpacing;
        }
        setCacheData(width, (int) height);
        return (int) height;
    }

    /**
     * 获取缓存的测量数据，避免多次重复测量
     *
     * @param text 测量的文本
     * @param width 所需宽度
     * @return height
     */
    @SuppressWarnings("unchecked")
    private int getCachedData(CharSequence text, int width) {
        MeasuredData md = sMeasuredData.get(text);
        if (md == null) {
            return -1;
        }
        if (md.mTextSize == getTextSize() && width == md.mWidth) {
            mLineWidthMax = md.mLineWidthMax;
            mOneLineWidth = md.mOneLineWidth;
            mContentList = (ArrayList<Line>) md.mContentList.clone();
            return md.mHeight;
        } else {
            return -1;
        }
    }

    /**
     * 缓存已测量的数据
     * @param width 所需宽度
     * @param height 所需高度
     */
    @SuppressWarnings("unchecked")
    private void setCacheData(int width, int height) {
        MeasuredData md = new MeasuredData();
        md.mHeight = height;
        md.mWidth = width;
        md.mLineWidthMax = mLineWidthMax;
        md.mOneLineWidth = mOneLineWidth;
        md.mHashIndex = ++sHashIndex;
        md.mTextSize = getTextSize();
        md.mContentList = (ArrayList<Line>) mContentList.clone();
        CharSequence text = getText();
        if (!TextUtils.isEmpty(text)) {
            sMeasuredData.put(text, md);
        }
    }

    private void calculateSpanList(CharSequence cs) {
        mItemList.clear();
        ArrayList<SpanObject> isList = new ArrayList<>();
        if (cs instanceof Spanned) {
            CharacterStyle[] spans = ((Spanned) cs).getSpans(0, cs.length(), CharacterStyle.class);
            for (CharacterStyle span : spans) {
                int start = ((Spanned) cs).getSpanStart(span);
                int end = ((Spanned) cs).getSpanEnd(span);
                SpanObject so = new SpanObject();
                so.span = span;
                so.start = start;
                so.end = end;
                so.source = cs.subSequence(start, end);
                isList.add(so);
            }
        }
        // 对span进行排序，以免不同种类的span位置错乱
        Collections.sort(isList, new SpanObjectComparator());

        String str = cs.toString();
        for (int i = 0, j = 0; i < cs.length(); ) {
            if (j < isList.size()) {
                SpanObject is = isList.get(j);
                if (i < is.start) {
                    int cp = str.codePointAt(i);
                    // 支持增补字符
                    if (Character.isSupplementaryCodePoint(cp)) {
                        i += 2;
                    } else {
                        i++;
                    }
                    mItemList.add(new String(Character.toChars(cp)));
                } else {
                    mItemList.add(is);
                    j++;
                    i = is.end;
                }
            } else {
                int cp = str.codePointAt(i);
                if (Character.isSupplementaryCodePoint(cp)) {
                    i += 2;
                } else {
                    i++;
                }
                mItemList.add(new String(Character.toChars(cp)));
            }
        }
        requestLayout();
    }
    
    /** 存储Span对象及相关信息 **/
    static class SpanObject {
        public Object span;
        public int start;
        public int end;
        public CharSequence source;
    }

    /** 对SpanObject进行排序 **/
    static class SpanObjectComparator implements Comparator<SpanObject> {
        @Override
        public int compare(@NonNull SpanObject lhs, @NonNull SpanObject rhs) {
            return lhs.start - rhs.start;
        }
    }

    /** 存储测量好的一行数据 **/
    static class Line {
        public ArrayList<Object> line = new ArrayList<>();
        public ArrayList<Integer> widthList = new ArrayList<>();
        public float height;
    }

    /** 缓存的数据 **/
    static class MeasuredData {
        public int mHeight;
        public int mWidth;
        public int mOneLineWidth;
        public float mLineWidthMax;
        public float mTextSize;
        public int mHashIndex;
        ArrayList<Line> mContentList = new ArrayList<>();
    }
}