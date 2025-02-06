DELIMITER //

-- 存储过程1: 比较前缀表结构
CREATE PROCEDURE compare_prefix_tables(
    IN base_table VARCHAR(64),
    IN table_prefix VARCHAR(64)
)
BEGIN
    -- 获取所有指定前缀的表
    SELECT GROUP_CONCAT(
        CONCAT(
            'SELECT ''', TABLE_NAME, ''' as table_name, ',
            'CONCAT(''SET @table1 = ''', '''\'', base_table, '\'; '', ',
            '''SET @table2 = ''', '''\'', TABLE_NAME, '\'; '') as variables, ',
            '(SELECT COUNT(*) = 0 FROM (',
            'SELECT t1.COLUMN_NAME, t1.COLUMN_TYPE, t1.IS_NULLABLE, t1.COLUMN_DEFAULT, t1.EXTRA ',
            'FROM information_schema.COLUMNS t1 ',
            'WHERE t1.TABLE_NAME = ''', base_table, ''' ',
            'AND NOT EXISTS (',
            '    SELECT 1 FROM information_schema.COLUMNS t2 ',
            '    WHERE t2.TABLE_NAME = ''', TABLE_NAME, ''' ',
            '    AND t1.COLUMN_NAME = t2.COLUMN_NAME ',
            '    AND t1.COLUMN_TYPE = t2.COLUMN_TYPE ',
            '    AND t1.IS_NULLABLE = t2.IS_NULLABLE ',
            '    AND IFNULL(t1.COLUMN_DEFAULT,'''') = IFNULL(t2.COLUMN_DEFAULT,'''') ',
            '    AND t1.EXTRA = t2.EXTRA',
            ') UNION ALL ',
            'SELECT t2.COLUMN_NAME, t2.COLUMN_TYPE, t2.IS_NULLABLE, t2.COLUMN_DEFAULT, t2.EXTRA ',
            'FROM information_schema.COLUMNS t2 ',
            'WHERE t2.TABLE_NAME = ''', TABLE_NAME, ''' ',
            'AND NOT EXISTS (',
            '    SELECT 1 FROM information_schema.COLUMNS t1 ',
            '    WHERE t1.TABLE_NAME = ''', base_table, ''' ',
            '    AND t1.COLUMN_NAME = t2.COLUMN_NAME',
            ')) diff) as is_identical'
        )
        SEPARATOR ' UNION ALL '
    ) INTO @sql
    FROM information_schema.TABLES 
    WHERE TABLE_NAME LIKE CONCAT(table_prefix, '%')
    AND TABLE_NAME != base_table;

    -- 执行动态SQL
    SET @sql = CONCAT('SELECT table_name, variables, is_identical FROM (', @sql, ') results');
    PREPARE stmt FROM @sql;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;
END//

-- 存储过程2: 生成表结构修改语句
CREATE PROCEDURE generate_alter_statements(
    IN base_table VARCHAR(64),
    IN target_table VARCHAR(64)
)
BEGIN
    SET @table1 = base_table;
    SET @table2 = target_table;

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
END//

DELIMITER ;
