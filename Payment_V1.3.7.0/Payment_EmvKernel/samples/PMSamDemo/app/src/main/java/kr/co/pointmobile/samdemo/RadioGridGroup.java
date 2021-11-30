package kr.co.pointmobile.samdemo;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;

public class RadioGridGroup extends TableLayout implements View.OnClickListener {
    public interface OnRadioGripGroupChangeListener {
        void onCheckedChanged(RadioGridGroup group, int checkedId);
    }
    private int checkedButtonId = -1;
    private OnRadioGripGroupChangeListener groupChangeListener;

    public RadioGridGroup(Context context) {
        super(context);
    }

    public RadioGridGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setOnRadioGripGroupListener(OnRadioGripGroupChangeListener listener) {
        groupChangeListener = listener;
    }

    @Override
    public void onClick(View v) {
        if(v instanceof RadioButton) {
            check(v.getId());
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if(checkedView != null && checkedView instanceof RadioButton) {
            ((RadioButton) checkedView).setChecked(checked);
        }
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        super.addView(child, params);
        setChildrenOnClickListener((TableRow)child);
    }

    private void setChildrenOnClickListener(TableRow tableRow) {
        final int count = tableRow.getChildCount();
        for(int i=0; i<count; i++) {
            final View view = tableRow.getChildAt(i);
            if(view instanceof RadioButton) view.setOnClickListener(this);
        }
    }

    private void check(int id) {
        if((id != -1) && (id == checkedButtonId)) {
            return;
        }
        if(checkedButtonId != -1) {
            setCheckedStateForView(checkedButtonId, false);
        }
        if(id != -1) {
            setCheckedStateForView(id, true);
        }
        setCheckdId(id, true);
    }

    public void setCheck(int id) {
        if((id != -1) && (id == checkedButtonId)) {
            return;
        }
        if(checkedButtonId != -1) {
            setCheckedStateForView(checkedButtonId, false);
        }
        if(id != -1) {
            setCheckedStateForView(id, true);
        }
        setCheckdId(id, false);
    }

    private void setCheckdId(int id, boolean isCallback) {
        this.checkedButtonId = id;
        if(groupChangeListener != null && isCallback){
            groupChangeListener.onCheckedChanged(this, checkedButtonId);
        }
    }

    public void clearCheck() {
        check(-1);
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());

        this.checkedButtonId = ss.buttonId;
        setCheckedStateForView(checkedButtonId, true);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.buttonId = checkedButtonId;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int buttonId;

        /**
         * Constructor used when reading from a parcel. Reads the state of the superclass.
         *
         * @param source
         */
        public SavedState(Parcel source) {
            super(source);
            buttonId = source.readInt();
        }

        /**
         * Constructor called by derived classes when creating their SavedState objects
         *
         * @param superState The state of the superclass of this view
         */
        public SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(buttonId);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
