package net.risesoft.util;

import lombok.Getter;
import lombok.Setter;


import java.io.Serializable;

@Setter
@Getter
public class DbColumn implements Serializable {
    private static final long serialVersionUID = -7176298428774384422L;

    /**
     * 列名
     */
    private String column_name;

    private String column_name_old;
    /**
     * 字段类型
     */
    private int data_type;

    private String type_name;
    /**
     * 字段长度
     */
    private Integer data_length;

    /**
     * 字段精度
     */
    private Integer data_precision;

    /**
     * 小数位数
     */
    private Integer data_scale;

    /**
     * 所属表名
     */
    private String table_name;

    /**
     * 是否主键
     */
    private Boolean primaryKey;

    /**
     * 能否为空
     */
    private Boolean nullable;

    /**
     * 字段备注，用来中文化
     */
    private String comment;

    private Integer isPrimaryKey;

    private Integer isNull;

    public DbColumn() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        DbColumn other = (DbColumn) obj;
        if (column_name == null) {
            if (other.column_name != null) {
                return false;
            }
        } else if (!column_name.equals(other.column_name)) {
            return false;
        }
        if (column_name_old == null) {
            if (other.column_name_old != null) {
                return false;
            }
        } else if (!column_name_old.equals(other.column_name_old)) {
            return false;
        }
        if (comment == null) {
            if (other.comment != null) {
                return false;
            }
        } else if (!comment.equals(other.comment)) {
            return false;
        }
        if (data_length == null) {
            if (other.data_length != null) {
                return false;
            }
        } else if (!data_length.equals(other.data_length)) {
            return false;
        }
        if (data_precision == null) {
            if (other.data_precision != null) {
                return false;
            }
        } else if (!data_precision.equals(other.data_precision)) {
            return false;
        }
        if (data_scale == null) {
            if (other.data_scale != null) {
                return false;
            }
        } else if (!data_scale.equals(other.data_scale)) {
            return false;
        }
        if (data_type != other.data_type) {
            return false;
        }
        if (nullable == null) {
            if (other.nullable != null) {
                return false;
            }
        } else if (!nullable.equals(other.nullable)) {
            return false;
        }
        if (primaryKey == null) {
            if (other.primaryKey != null) {
                return false;
            }
        } else if (!primaryKey.equals(other.primaryKey)) {
            return false;
        }
        if (table_name == null) {
            if (other.table_name != null) {
                return false;
            }
        } else if (!table_name.equals(other.table_name)) {
            return false;
        }
        if (type_name == null) {
            return other.type_name == null;
        } else return type_name.equals(other.type_name);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((column_name == null) ? 0 : column_name.hashCode());
        result = prime * result + ((column_name_old == null) ? 0 : column_name_old.hashCode());
        result = prime * result + ((comment == null) ? 0 : comment.hashCode());
        result = prime * result + ((data_length == null) ? 0 : data_length.hashCode());
        result = prime * result + ((data_precision == null) ? 0 : data_precision.hashCode());
        result = prime * result + ((data_scale == null) ? 0 : data_scale.hashCode());
        result = prime * result + data_type;
        result = prime * result + ((nullable == null) ? 0 : nullable.hashCode());
        result = prime * result + ((primaryKey == null) ? 0 : primaryKey.hashCode());
        result = prime * result + ((table_name == null) ? 0 : table_name.hashCode());
        result = prime * result + ((type_name == null) ? 0 : type_name.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "DbColumn [column_name=" + column_name + ", column_name_old=" + column_name_old + ", data_type="
                + data_type + ", type_name=" + type_name + ", data_length=" + data_length + ", data_precision="
                + data_precision + ", data_scale=" + data_scale + ", table_name=" + table_name + ", primaryKey="
                + primaryKey + ", nullable=" + nullable + ", comment=" + comment + "]";
    }

}
