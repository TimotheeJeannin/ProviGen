package com.tjeannin.provigen;

import android.provider.BaseColumns;
import com.tjeannin.provigen.annotation.Column;
import com.tjeannin.provigen.annotation.Column.Type;
import com.tjeannin.provigen.annotation.Id;

/**
 * Base interface for a {@link ProviGenProvider} contract.
 */
public interface ProviGenBaseContract {

    /**
     * The unique ID for a row.
     */
    @Id(autoincrement = true)
    @Column(Type.INTEGER)
    String _ID = BaseColumns._ID;

}
