package com.mihailovalex.taskslist.adapter;

import android.database.Cursor;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.mihailovalex.taskslist.data.TaskSchedulerClass;

public abstract class CursorRecyclerAdapter<ViewHolder extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<ViewHolder> {

    protected Cursor cursor; // Курсор
    protected boolean isDataValid; // Валидны ли данные
    protected int idColumnIndex; // Индекс столбца ID в курсоре

    public CursorRecyclerAdapter(Cursor cursor) {
        super();

        this.cursor = cursor;

        // Данные корректны если курсор не null
        isDataValid = cursor != null;

        // Пытаемся получить индекс столбца ID, если курсор не null, в ином случае -1
        idColumnIndex = cursor != null
                ? cursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks._ID)
                : -1;

        // Каждый элемент имеет уникальный ID
        setHasStableIds(true);
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        // Если данные некорректны — кидаем исключение
        if (!isDataValid) {
            throw new IllegalStateException("Cursor is not valid!");
        }

        // Попробовали перейти к определённой строке, но это не получилось
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Can not move to position " + position);
        }

        // Вызываем новый метод
        onBindViewHolder(viewHolder, cursor);
    }
    @Override
    public int getItemCount() {
        if (isDataValid && cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }
    @Override
    public long getItemId(int position) {

        // Если с данными всё хорошо и есть курсор
        if (isDataValid && cursor != null) {

            // Если смогли найти нужную строку в курсоре
            if (cursor.moveToPosition(position)) {

                // Возвращаем значение столбца ID
                return cursor.getLong(idColumnIndex);
            }
        }

        // Во всех остальных случаях возвращаем дефолтное значение
        return RecyclerView.NO_ID;
    }

    public Cursor swapCursor(Cursor newCursor) {
        // Если курсор не изменился — ничего не заменяем
        if (newCursor == this.cursor) {
            return null;
        }

        Cursor oldCursor = this.cursor;
        this.cursor = newCursor;

        if (newCursor != null) {
            idColumnIndex = newCursor.getColumnIndexOrThrow(TaskSchedulerClass.Tasks._ID);
            isDataValid = true;
            notifyDataSetChanged();
        } else {
            idColumnIndex = -1;
            isDataValid = false;
            // Сообщаем, что данных в адаптере больше нет
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;

    }
    public abstract void onBindViewHolder(RecyclerView.ViewHolder viewHolder, Cursor cursor);

}
