package com.mihailovalex.reminder_room.adapter;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.mihailovalex.reminder_room.R;
import com.mihailovalex.reminder_room.data.Birthday;
import com.mihailovalex.reminder_room.data.Item;
import com.mihailovalex.reminder_room.databinding.BirthdayItemBinding;
import com.mihailovalex.reminder_room.databinding.SeparatorItemBinding;
import com.mihailovalex.reminder_room.databinding.TaskItemBinding;
import com.mihailovalex.reminder_room.ui.birthdays.BirthdayItemUserActionsListener;
import com.mihailovalex.reminder_room.ui.birthdays.BirthdayViewModel;
import com.mihailovalex.reminder_room.utils.DateUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public  class BirthdayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Item> items;
    BirthdayViewModel birthdayViewModel;
    private static final int TYPE_BIRTHDAY = 0;
    private static final int TYPE_SEPARATOR = 1;
    SharedPreferences sPref;

    public boolean containsSeparatorOverdue;
    public boolean containsSeparatorToday;
    public boolean containsSeparatorTomorrow;
    public boolean containsSeparatorFuture;

    public BirthdayAdapter(List<Item> birthdays,
                           BirthdayViewModel birthdayViewModel) {
        this.birthdayViewModel = birthdayViewModel;
        items = new ArrayList<>();
        setList(birthdays);

    }
    public Item getItem(int position){
        return items.get(position);
    }
    public void addItem(Item item){
        items.add(item);
        notifyItemInserted(getItemCount()-1);
    }
    public void addItem(int location,Item item){
        items.add(location,item);
        notifyItemInserted(location);
    }
    public void updateBirthday(Birthday updateBirthday){
        for (int i = 0; i < getItemCount(); i++) {
            if(getItem(i).isTask()){
                Birthday birthday = (Birthday) getItem(i);
                if(birthday.getId()==updateBirthday.getId()){
                    removeItem(i);
                    //getTaskFragment().addTask(updateBirthday,false);
                    setSeparator(updateBirthday);
                }
            }
        }
    }
    public void removeItem(int location){
        if(location>=0 && location<=getItemCount()-1){
            items.remove(location);
            notifyItemRemoved(location);
            if(location-1>=0 && location<=getItemCount()-1){
                if(!getItem(location).isTask() && !getItem(location-1).isTask()){
                    Separator separator = (Separator) getItem(location-1);
                    checkSeparator(separator.getType());
                    items.remove(location-1);
                    notifyItemRemoved(location-1);
                }

            }
            if(getItemCount()-1>=0 && !getItem(getItemCount()-1).isTask()){
                Separator separator = (Separator) getItem(getItemCount()-1);
                checkSeparator(separator.getType());
                int locationTemp = getItemCount()-1;
                items.remove(locationTemp);
                notifyItemRemoved(locationTemp);
            }
        }
    }

    private void checkSeparator(int type) {
        switch (type){
            case Separator.TYPE_OVERDUE:
                containsSeparatorOverdue = false;
                break;
            case Separator.TYPE_TODAY:
                containsSeparatorToday = false;
                break;
            case Separator.TYPE_TOMORROW:
                containsSeparatorTomorrow = false;
                break;
            case Separator.TYPE_FUTURE:
                containsSeparatorFuture = false;
                break;
        }
    }

    public void removeAllItems()
    {
        if(getItemCount()!=0){
            items = new ArrayList<>();
            notifyDataSetChanged();
            containsSeparatorOverdue = false;
            containsSeparatorToday = false;
            containsSeparatorTomorrow = false;
            containsSeparatorFuture = false;
        }
    }
    @Override
    public int getItemCount() {
        return items.size();
    }

    protected class BirthdayViewHolder extends RecyclerView.ViewHolder{
        protected Context context;
        protected BirthdayItemBinding birthdayItemBinding;

        public BirthdayViewHolder(BirthdayItemBinding  binding) {
            super(binding.getRoot());
            context = itemView.getContext();
            birthdayItemBinding = binding;
        }

    }
    protected class SeparatorViewHolder extends RecyclerView.ViewHolder{
        protected Context context;
        protected SeparatorItemBinding separatorItemBinding;

        public SeparatorViewHolder(SeparatorItemBinding  binding) {
            super(binding.getRoot());
            context = itemView.getContext();
            separatorItemBinding = binding;
        }
    }

    private void setList(List<Item> tasks) {
        //items = tasks;
        removeAllItems();
        for (Item item :tasks){
            setSeparator((Birthday) item);
        }
        notifyDataSetChanged();
    }
    public void replaceData(List<Item> birthdays) {
        setList(birthdays);
    }
    private void setSeparator(Birthday newBirthday){
        int position = -1;
        Separator separator = null;
        for (int i = 0; i < getItemCount(); i++) {
            if(getItem(i).isTask()){
                Birthday birthday = (Birthday) getItem(i);
                if(newBirthday.getDate()<birthday.getDate()){
                    position = i;
                    break;
                }
            }
        }
        if(newBirthday.getDate()!=0){
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(newBirthday.getDate());
            if(calendar.get(Calendar.YEAR)>Calendar.getInstance().get(Calendar.YEAR)){
                newBirthday.setDateStatus(Separator.TYPE_FUTURE);
                if(!containsSeparatorFuture){
                    containsSeparatorFuture = true;
                    separator = new Separator(Separator.TYPE_FUTURE);
                }
            }else if(calendar.get(Calendar.DAY_OF_YEAR)<Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                newBirthday.setDateStatus(Separator.TYPE_OVERDUE);
                if(!containsSeparatorOverdue){
                    containsSeparatorOverdue = true;
                    separator = new Separator(Separator.TYPE_OVERDUE);
                }
            } else if(calendar.get(Calendar.DAY_OF_YEAR)==Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                newBirthday.setDateStatus(Separator.TYPE_TODAY);
                if(!containsSeparatorToday){
                    containsSeparatorToday = true;
                    separator = new Separator(Separator.TYPE_TODAY);
                }
            } else if(calendar.get(Calendar.DAY_OF_YEAR) ==Calendar.getInstance().get(Calendar.DAY_OF_YEAR)+1){
                newBirthday.setDateStatus(Separator.TYPE_TOMORROW);
                if(!containsSeparatorTomorrow){
                    containsSeparatorTomorrow = true;
                    separator = new Separator(Separator.TYPE_TOMORROW);
                }
            } else if(calendar.get(Calendar.DAY_OF_YEAR)>Calendar.getInstance().get(Calendar.DAY_OF_YEAR)){
                newBirthday.setDateStatus(Separator.TYPE_FUTURE);
                if(!containsSeparatorFuture){
                    containsSeparatorFuture = true;
                    separator = new Separator(Separator.TYPE_FUTURE);
                }
            }
        }

        if(position!=-1){
            if(!getItem(position -1).isTask()){
                if(position-2>=0 && getItem(position -2 ).isTask()){
                    Birthday birthday = (Birthday) getItem(position -2 );
                    if(birthday.getDateStatus()==newBirthday.getDateStatus()){
                        position -= 1;
                    }
                }
            }
            if (separator != null){
                addItem(position-1,separator);
            }
            addItem(position,newBirthday);
        } else {
            if (separator != null){
                addItem(separator);
            }
            addItem(newBirthday);
        }
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType){
            case TYPE_BIRTHDAY:
                BirthdayItemBinding binding = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.birthday_item, parent, false);

                BirthdayViewHolder holder = new BirthdayViewHolder(binding);
                return holder;
            case TYPE_SEPARATOR:
                SeparatorItemBinding binding1 = DataBindingUtil.inflate(
                        LayoutInflater.from(parent.getContext()),
                        R.layout.separator_item, parent, false);

                SeparatorViewHolder holder1 = new SeparatorViewHolder(binding1);
                return holder1;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder rawHolder, int position) {
        final int currentPosiotion = position;
        final Item item = getItem(position);
        final Resources resources =rawHolder.itemView.getResources();
        if(item.isTask()){
            final BirthdayViewHolder holder = (BirthdayViewHolder) rawHolder;
            holder.itemView.setEnabled(true);
            final Birthday birthday = (Birthday) item;

            final View itemView = holder.itemView;
            holder.birthdayItemBinding.tvBirthdayTitle.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    removeBirthdayDialog(v,birthday,currentPosiotion);
                    return true;
                }
            });
            sPref = PreferenceManager.getDefaultSharedPreferences(holder.context);
            String fontText = sPref.getString("font", "middle");
            switch (fontText){
                case "small":
                    holder.birthdayItemBinding.tvBirthdayTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                    break;
                case "middle":
                    holder.birthdayItemBinding.tvBirthdayTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    break;
                case "big":
                    holder.birthdayItemBinding.tvBirthdayTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
                    break;
            }
            BirthdayItemUserActionsListener userActionsListener = new BirthdayItemUserActionsListener() {


                @Override
                public void onBirthdayClicked(Birthday birthday) {
                    birthdayViewModel.getOpenBirthdayEvent().setValue(birthday.getId());
                }

                @Override
                public void onLongBirthdayClicked(View v, Birthday birthday) {
                    removeBirthdayDialog(v,birthday,currentPosiotion);
                }
                @Override
                public void onCompleteChanged(Birthday birthday, View v) {
                    boolean checked = birthday.isActive();
                    if(birthday.getDate()<Calendar.getInstance().getTimeInMillis()){
                        ObjectAnimator flipin = ObjectAnimator.ofFloat(holder.birthdayItemBinding.cvBirthdayPriority,"rotationY",-180f,0f);
                        flipin.addListener(new Animator.AnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {

                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(birthday.isActive()){
                                    holder.birthdayItemBinding.cvBirthdayPriority.setImageResource(R.drawable.ic_circle_check);
                                    ObjectAnimator translationX = ObjectAnimator.ofFloat(itemView,"translationX",0f,itemView.getWidth());
                                    ObjectAnimator translationXBack = ObjectAnimator.ofFloat(itemView,"translationX",itemView.getWidth(),0f);
                                    translationX.addListener(new Animator.AnimatorListener() {
                                        @Override
                                        public void onAnimationStart(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            itemView.setVisibility(View.GONE);
                                            if (!birthday.isRepeated()) {
                                                removeItem(position);
                                            }else {
                                                birthday.setDate(DateUtils.repeatTask(birthday.getDate(),birthday.getRepeat()));
                                                birthdayViewModel.getBirthdayRepository().saveBirthday(birthday);
                                                updateBirthday(birthday);
                                            }
                                        }

                                        @Override
                                        public void onAnimationCancel(Animator animation) {

                                        }

                                        @Override
                                        public void onAnimationRepeat(Animator animation) {

                                        }
                                    });
                                    AnimatorSet animatorSet = new AnimatorSet();
                                    animatorSet.play(translationX).before(translationXBack);
                                    animatorSet.start();
                                }
                            }

                            @Override
                            public void onAnimationCancel(Animator animation) {

                            }

                            @Override
                            public void onAnimationRepeat(Animator animation) {

                            }
                        });
                        flipin.start();
                    }
                }
            };
            holder.birthdayItemBinding.setBirthday((Birthday) item);
            holder.birthdayItemBinding.setViewmodel(birthdayViewModel);
            holder.birthdayItemBinding.setListener(userActionsListener);
            // holder.taskItemBinding.executePendingBindings();
        } else {
            Separator separator = (Separator) item;
            SeparatorViewHolder separatorViewHolder = (SeparatorViewHolder) rawHolder;
            separatorViewHolder.separatorItemBinding.setTask((Separator) item);
            //separatorViewHolder.type.setText(resources.getString(separator.getType()));
        }
    }


    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isTask()) return TYPE_BIRTHDAY;
        else return TYPE_SEPARATOR;
    }
    public void removeBirthdayDialog(View v, final Birthday birthday, int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext());
        Item item = birthday;
        if(item.isTask()){
            Birthday removingBirthday = (Birthday)item;
            final long birthdayId = removingBirthday.getId();
            final boolean[] isRemoved = {false};
            builder.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    removeItem(position);

                    isRemoved[0] = true;
                    Snackbar snackbar = Snackbar.make(v,
                            R.string.removed, Snackbar.LENGTH_LONG);
                    snackbar.setAction(R.string.dialog_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    setSeparator(birthday);
                                }
                            },1000);
                            //tasksViewModel.getTasksRepository().saveTask(birthday);
                            isRemoved[0] =false;
                        }
                    });
                    snackbar.getView().addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
                        @Override
                        public void onViewAttachedToWindow(View v) {

                        }

                        @Override
                        public void onViewDetachedFromWindow(View v) {
                            if(isRemoved[0]){
                                birthdayViewModel.getBirthdayRepository().deleteBirthday(birthdayId);
                            }
                        }
                    });
                    snackbar.show();
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        }
        builder.setMessage(R.string.dialog_removing_mess_birthday);
        builder.show();
    }
}
