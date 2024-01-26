package net.risesoft.util;

import java.io.Serializable;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public class DbColumn implements Serializable {

    private static final long serialVersionUID = -7176298428774384422L;

    /**
     * 列名
     */
    private String columnName;

    private String columnNameOld;
    /**
     * 字段类型
     */
    private int dataType;

    private String typeName;
    /**
     * 字段长度
     */
    private Integer dataLength;

    /**
     * 字段精度
     */
    private Integer dataPrecision;

    /**
     * 小数位数
     */
    private Integer dataScale;

    /**
     * 所属表名
     */
    private String tableName;

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

    public DbColumn() {}

    /**
     * @return the columnName
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * @param columnName the columnName to set
     */
    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    /**
     * @return the columnNameOld
     */
    public String getColumnNameOld() {
        return columnNameOld;
    }

    /**
     * @param columnNameOld the columnNameOld to set
     */
    public void setColumnNameOld(String columnNameOld) {
        this.columnNameOld = columnNameOld;
    }

    /**
     * @return the dataType
     */
    public int getDataType() {
        return dataType;
    }

    /**
     * @param dataType the dataType to set
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @return the dataLength
     */
    public Integer getDataLength() {
        return dataLength;
    }

    /**
     * @param dataLength the dataLength to set
     */
    public void setDataLength(Integer dataLength) {
        this.dataLength = dataLength;
    }

    /**
     * @return the dataPrecision
     */
    public Integer getDataPrecision() {
        return dataPrecision;
    }

    /**
     * @param dataPrecision the dataPrecision to set
     */
    public void setDataPrecision(Integer dataPrecision) {
        this.dataPrecision = dataPrecision;
    }

    /**
     * @return the dataScale
     */
    public Integer getDataScale() {
        return dataScale;
    }

    /**
     * @param dataScale the dataScale to set
     */
    public void setDataScale(Integer dataScale) {
        this.dataScale = dataScale;
    }

    /**
     * @return the tableName
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName the tableName to set
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return the primaryKey
     */
    public Boolean getPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey the primaryKey to set
     */
    public void setPrimaryKey(Boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    /**
     * @return the nullable
     */
    public Boolean getNullable() {
        return nullable;
    }

    /**
     * @param nullable the nullable to set
     */
    public void setNullable(Boolean nullable) {
        this.nullable = nullable;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the isPrimaryKey
     */
    public Integer getIsPrimaryKey() {
        return isPrimaryKey;
    }

    /**
     * @param isPrimaryKey the isPrimaryKey to set
     */
    public void setIsPrimaryKey(Integer isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }

    /**
     * @return the isNull
     */
    public Integer getIsNull() {
        return isNull;
    }

    /**
     * @param isNull the isNull to set
     */
    public void setIsNull(Integer isNull) {
        this.isNull = isNull;
    }

}
