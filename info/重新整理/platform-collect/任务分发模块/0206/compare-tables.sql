SET @table1 = 'table1'; -- 基准表名
SET @table2 = 'table2'; -- 需要修改的表名

SELECT 
    CASE 
        WHEN operation = 'ADD' THEN
            CONCAT('ALTER TABLE ', table2, ' ADD COLUMN ', column_def, ';')
        WHEN operation = 'MODIFY' THEN 
            CONCAT('ALTER TABLE ', table2, ' MODIFY COLUMN ', column_def, ';')
        WHEN operation = 'DROP' THEN
            CONCAT('ALTER TABLE ', table2, ' DROP COLUMN ', column_name, ';')
    END as alter_statement
FROM (
    -- 需要添加的列
    SELECT 
        'ADD' as operation,
        @table2 as table2,
        CONCAT(
            t1.COLUMN_NAME, ' ', t1.COLUMN_TYPE,
            CASE WHEN t1.IS_NULLABLE = 'NO' THEN ' NOT NULL' ELSE ' NULL' END,
            CASE WHEN t1.COLUMN_DEFAULT IS NOT NULL 
                THEN CONCAT(' DEFAULT ', 
                    CASE 
                        WHEN t1.COLUMN_DEFAULT = 'CURRENT_TIMESTAMP' THEN t1.COLUMN_DEFAULT
                        WHEN t1.DATA_TYPE IN ('char','varchar','text','date','datetime','timestamp') 
                        THEN CONCAT('''', t1.COLUMN_DEFAULT, '''')
                        ELSE t1.COLUMN_DEFAULT
                    END
                )
                ELSE ''
            END,
            CASE WHEN t1.EXTRA != '' THEN CONCAT(' ', t1.EXTRA) ELSE '' END
        ) as column_def,
        t1.COLUMN_NAME as column_name
    FROM information_schema.COLUMNS t1
    LEFT JOIN information_schema.COLUMNS t2 
    ON t1.COLUMN_NAME = t2.COLUMN_NAME 
    AND t2.TABLE_NAME = @table2
    WHERE t1.TABLE_NAME = @table1
    AND t2.COLUMN_NAME IS NULL

    UNION ALL

    -- 需要修改的列
    SELECT 
        'MODIFY' as operation,
        @table2 as table2,
        CONCAT(
            t1.COLUMN_NAME, ' ', t1.COLUMN_TYPE,
            CASE WHEN t1.IS_NULLABLE = 'NO' THEN ' NOT NULL' ELSE ' NULL' END,
            CASE WHEN t1.COLUMN_DEFAULT IS NOT NULL 
                THEN CONCAT(' DEFAULT ', 
                    CASE 
                        WHEN t1.COLUMN_DEFAULT = 'CURRENT_TIMESTAMP' THEN t1.COLUMN_DEFAULT
                        WHEN t1.DATA_TYPE IN ('char','varchar','text','date','datetime','timestamp') 
                        THEN CONCAT('''', t1.COLUMN_DEFAULT, '''')
                        ELSE t1.COLUMN_DEFAULT
                    END
                )
                ELSE ''
            END,
            CASE WHEN t1.EXTRA != '' THEN CONCAT(' ', t1.EXTRA) ELSE '' END
        ) as column_def,
        t1.COLUMN_NAME as column_name
    FROM information_schema.COLUMNS t1
    JOIN information_schema.COLUMNS t2 
    ON t1.COLUMN_NAME = t2.COLUMN_NAME 
    AND t2.TABLE_NAME = @table2
    WHERE t1.TABLE_NAME = @table1
    AND (
        t1.COLUMN_TYPE != t2.COLUMN_TYPE OR
        t1.IS_NULLABLE != t2.IS_NULLABLE OR
        IFNULL(t1.COLUMN_DEFAULT,'') != IFNULL(t2.COLUMN_DEFAULT,'') OR
        t1.EXTRA != t2.EXTRA
    )

    UNION ALL

    -- 需要删除的列
    SELECT 
        'DROP' as operation,
        @table2 as table2,
        '' as column_def,
        t2.COLUMN_NAME as column_name
    FROM information_schema.COLUMNS t2
    LEFT JOIN information_schema.COLUMNS t1 
    ON t1.COLUMN_NAME = t2.COLUMN_NAME 
    AND t1.TABLE_NAME = @table1
    WHERE t2.TABLE_NAME = @table2
    AND t1.COLUMN_NAME IS NULL
) changes
ORDER BY 
    CASE operation
        WHEN 'DROP' THEN 1
        WHEN 'MODIFY' THEN 2
        WHEN 'ADD' THEN 3
    END;
