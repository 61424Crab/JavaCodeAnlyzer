#!/bin/bash

DIRECTORY="指定的目录"  # 替换为您的目标目录路径

# 遍历目录下的所有 .sql 文件（不区分大小写）
find "$DIRECTORY" -type f -iname "*.sql" | while read -r file; do
    # 检测文件编码
    charset=$(file -bi "$file" | sed -e 's/.*[ ]charset=//')

if [ "$charset" = "shift-jis" ]; then
    echo "Converting $file from Shift-JIS to UTF-8"
    iconv -f SHIFT-JIS -t UTF-8 "$file" -o "${file}.tmp" && mv "${file}.tmp" "$file"
    
    if [ $? -ne 0 ]; then
        echo "Error converting file: $file"
        rm "${file}.tmp"
    fi
fi
done
