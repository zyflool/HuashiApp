package net.muxi.huashiapp.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.muxi.huashiapp.App;
import net.muxi.huashiapp.R;
import net.muxi.huashiapp.common.util.AnimationUtil;

/**
 * Created by ybao on 16/5/16.
 */
public class MySearchView extends FrameLayout implements View.OnClickListener{

    private ImageView mBtnBack;
    private ImageView mBtnClear;
    private RelativeLayout mSearchBar;
    private ListView mSearchListview;
    private FrameLayout mSearchLayout;
    private EditText mEditSearch;
    private View mTintView;

    private MenuItem mMenuItem;

    private boolean mIsSearchOpen = false;
    private boolean mIsSuggestionShown = true;

    private Context mContext;
    private String[] suggestions;
    private MySearchAdapter mSearchAdapter;

    private OnQueryTextListener mOnQueryTextListener;
    private OnSearchViewListener mOnSearchViewListener;


    private Drawable suggestionIcon = App.getContext().getResources().getDrawable(R.drawable.ic_suggestion);

    public MySearchView(Context context) {
        this(context, null);
    }

    public MySearchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MySearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs);

        mContext = context;

        initView();

        initStyle(attrs, defStyleAttr);
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.view_search, this, true);
        initSearchView();
    }



    private void initSearchView() {
        mBtnBack = (ImageView) findViewById(R.id.btn_back);
        mBtnClear = (ImageView) findViewById(R.id.btn_clear);
        mSearchBar = (RelativeLayout) findViewById(R.id.search_bar);
        mSearchListview = (ListView) findViewById(R.id.search_listview);
        mSearchLayout = (FrameLayout) findViewById(R.id.search_layout);
        mEditSearch = (EditText) findViewById(R.id.edit_search);
        mTintView = (View) findViewById(R.id.tint_view);

        mBtnBack.setOnClickListener(this);
        mBtnClear.setOnClickListener(this);
        mTintView.setOnClickListener(this);

        mEditSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                onSubmitQuery(v.getText().toString());
                return true;
            }
        });

        mEditSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                MySearchView.this.onQueryTextChanged(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mEditSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showKeyboard(mEditSearch);
                    showSuggestions();
                }
            }
        });
    }

    public void onSubmitQuery(String text) {
        if (mOnQueryTextListener != null) {
            mOnQueryTextListener.onQueryTextSubmit(text);
        }
    }


    public void onQueryTextChanged(String newText) {
        if (mOnQueryTextListener != null) {
            mOnQueryTextListener.onQueryTextSubmit(newText);
        }
    }

    private void initStyle(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mContext.obtainStyledAttributes(attrs, R.styleable.MySearchView, defStyleAttr, 0);

        if (a != null) {
            if (a.hasValue(R.styleable.MySearchView_searchBackground)) {
                setBackground(a.getDrawable(R.styleable.MySearchView_searchBackground));
            }
            if (a.hasValue(R.styleable.MySearchView_searchClearIcon)) {
                setSearchClearIcon(a.getDrawable(R.styleable.MySearchView_searchClearIcon));
            }
            if (a.hasValue(R.styleable.MySearchView_searchBackIcon)) {
                setSearchBackIcon(a.getDrawable(R.styleable.MySearchView_searchBackIcon));
            }
            if (a.hasValue(R.styleable.MySearchView_searchSuggestionIcon)) {
                setSearchSuggestionIcon(a.getDrawable(R.styleable.MySearchView_searchSuggestionIcon));
            }
            if (a.hasValue(R.styleable.MySearchView_searchSuggestionBackground)) {
                setSearchSuggestionBackground(a.getDrawable(R.styleable.MySearchView_searchSuggestionBackground));
            }
            if (a.hasValue(R.styleable.MySearchView_android_hint)) {
                setHint(a.getString(R.styleable.MySearchView_android_hint));
            }
            if (a.hasValue(R.styleable.MySearchView_android_textColor)) {
                setTextColor(a.getInt(R.styleable.MySearchView_android_textColor, 0));
            }
            if (a.hasValue(R.styleable.MySearchView_android_textColorHint)) {
                setTextColorHint(a.getInt(R.styleable.MySearchView_android_textColorHint, 0));
            }

            a.recycle();
        }
    }

    public void setSearchBackground(Drawable drawable) {
        mSearchBar.setBackground(drawable);
    }

    public void setSearchClearIcon(Drawable drawable) {
        mBtnClear.setImageDrawable(drawable);
    }

    public void setSearchBackIcon(Drawable drawable) {
        mBtnBack.setImageDrawable(drawable);
    }

    public void setSearchSuggestionIcon(Drawable drawable) {
        suggestionIcon = drawable;
    }

    public void setSearchSuggestionBackground(Drawable drawable) {
        mSearchListview.setBackground(drawable);
    }

    public void setHint(String hint) {
        mEditSearch.setHint(hint);
    }

    public void setTextColorHint(int color) {
        mEditSearch.setHintTextColor(color);
    }

    public void setTextColor(int color) {
        mEditSearch.setTextColor(color);
    }


    public void setSuggestions(String[] suggestions) {
        mSearchAdapter = new MySearchAdapter(mContext, suggestions, suggestionIcon);
        mSearchListview.setAdapter(mSearchAdapter);
        mSearchListview.setVisibility(VISIBLE);
    }


    public void setMenuItem(MenuItem menuItem) {
        this.mMenuItem = menuItem;
        mMenuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                showSearchView();
                return true;
            }
        });

    }

    public void showSearchView() {
        if (mIsSearchOpen){
            return;
        }
        mSearchLayout.setVisibility(VISIBLE);
        mEditSearch.setText(null);
        mEditSearch.requestFocus();

        setVisibleWithAnimation();
        if (mOnSearchViewListener != null){
            mOnSearchViewListener.onSearchShown();
        }
        mSearchListview.setVisibility(VISIBLE);
        mTintView.setVisibility(VISIBLE);
        mIsSearchOpen = true;
    }

    private void setVisibleWithAnimation() {
        AnimationUtil.AnimationListener animationListener = new AnimationUtil.AnimationListener() {
            @Override
            public boolean onAnimationStart(View view) {
                return false;
            }

            @Override
            public boolean onAnimationEnd(View view) {
                if (mOnSearchViewListener != null) {
                    mOnSearchViewListener.onSearchShown();
                }
                return false;
            }

            @Override
            public boolean onAnimationCancel(View view) {
                return false;
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            this.setVisibility(VISIBLE);
            AnimationUtil.reveal(mSearchBar,animationListener);
        }else {
            AnimationUtil.fadeInView(this,AnimationUtil.ANIMATION_DURATION_SHORT,animationListener);
        }
    }


    public void showSuggestions() {
        mSearchListview.setVisibility(VISIBLE);
    }


    public void hideSuggestions() {
        mSearchListview.setVisibility(GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_clear:
                mEditSearch.setText(null);
                break;
            case R.id.btn_back:
                closeSearchView();
                break;
            case R.id.tint_view:
                closeSearchView();
                break;
        }
    }

    public void closeSearchView() {
        if (!mIsSearchOpen) {
            return;
        }
        mEditSearch.setText(null);
        hideKeyboard(this);
        mEditSearch.clearFocus();
        this.clearFocus();
        this.setVisibility(GONE);
        mIsSearchOpen = false;
    }

    public void hideKeyboard(View view) {
        InputMethodManager imManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showKeyboard(View view) {
        view.requestFocus();
        InputMethodManager imManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imManager.showSoftInput(view, 0);

    }

    public boolean isSearchOpen(){
        return mIsSearchOpen;
    }

    public void setOnQueryTextListener(OnQueryTextListener onQueryTextListener) {
        mOnQueryTextListener = onQueryTextListener;
    }

    public void setOnSearchViewListener(OnSearchViewListener listener) {
        mOnSearchViewListener = listener;
    }

    public interface OnSearchViewListener {
        void onSearchShown();

        void onSeachClose();
    }

    public interface OnQueryTextListener {

        boolean onQueryTextSubmit(String queryText);

        boolean onQueryTextChange(String newText);
    }
}