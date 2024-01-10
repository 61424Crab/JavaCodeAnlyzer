#!/bin/bash

DIRECTORY="指定的目录"  # 替换为您的目标目录路径

# 遍历目录下的所有 .sql 文件（不区分大小写）
find "$DIRECTORY" -type f -iname "*.sql" | while read -r file; do
    # 检测文件编码
    charset=$(file -bi "$file" | sed -e 's/.*[ ]charset=//')

    # 如果编码不是 utf-8 或 us-ascii（us-ascii 可以不变），则转换
    if [ "$charset" != "utf-8" ] && [ "$charset" != "us-ascii" ]; then
        echo "Converting $file from $charset to utf-8"
        iconv -f "$charset" -t "UTF-8" "$file" -o "${file}.tmp" && mv "${file}.tmp" "$file"
        
        if [ $? -ne 0 ]; then
            echo "Error converting file: $file"
            rm "${file}.tmp"
        fi
    fi
done
