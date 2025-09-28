#!/bin/bash

# --- 配置 ---
# Mermaid 的配置文件名
CONFIG_FILE="mermaid-config.json"
# Markdown 文件扩展名
MD_EXTENSION=".md"
# 输出的 PNG 文件扩展名
PNG_EXTENSION=".png"
# 缩放比例 (数值越大，图像越清晰，文件也越大)
# 2 或 3 通常是很好的选择
SCALE_FACTOR=3

# --- 脚本开始 ---

# 检查 mmdc 命令是否存在
if ! command -v mmdc &> /dev/null
then
    echo "错误: 未找到 'mmdc' 命令。"
    echo "请先通过 'npm install -g @mermaid-js/mermaid-cli' 安装它。"
    exit 1
fi

# 检查配置文件是否存在
if [ ! -f "$CONFIG_FILE" ]; then
    echo "错误: 配置文件 '$CONFIG_FILE' 在当前目录下未找到。"
    exit 1
fi

echo "开始将 Markdown (*.md) 转换为高清 PNG..."
echo "缩放比例: ${SCALE_FACTOR}x"
echo "----------------------------------------------------"

# 计数器
count=0

# 遍历当前目录下所有的 .md 文件
for file in *"$MD_EXTENSION"; do
    if [ -e "$file" ]; then
        # 获取不带后缀的文件名
        base_name="${file%$MD_EXTENSION}"
        # 定义输出文件名
        output_filename="$base_name$PNG_EXTENSION"
        
        echo "正在转换: $file  --->  $output_filename"
        
        # 执行转换命令
        # -b transparent 设置背景为透明，如果需要白色背景，可以改为 -b "#FFFFFF" 或移除该选项
        mmdc -i "$file" -o "$output_filename" -c "$CONFIG_FILE" -s "$SCALE_FACTOR" -b transparent
        
        count=$((count+1))
    fi
done

echo "----------------------------------------------------"
if [ "$count" -eq 0 ]; then
    echo "未找到任何 Markdown 文件进行转换。"
else
    echo "转换完成！共处理了 $count 个文件。"
fi