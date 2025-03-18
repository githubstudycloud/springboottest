/**
 * 处理复杂嵌套表格并导出为真正的XLSX格式
 * 
 * 此代码首先加载SheetJS库，然后提供函数处理嵌套表格、合并单元格等复杂情况，
 * 并生成真正的Excel XLSX文件，而不是简单的CSV文件。
 */

// 第一步：加载SheetJS库（如果页面还没有加载）
function loadSheetJS() {
  return new Promise((resolve, reject) => {
    if (window.XLSX) {
      resolve(window.XLSX);
      return;
    }
    
    const script = document.createElement('script');
    script.src = 'https://cdn.jsdelivr.net/npm/xlsx@0.18.5/dist/xlsx.full.min.js';
    script.onload = () => resolve(window.XLSX);
    script.onerror = () => reject(new Error('无法加载SheetJS库'));
    document.head.appendChild(script);
  });
}

/**
 * 处理表格内容，包括嵌套表格和合并单元格
 * @param {HTMLElement} container - 包含表格的容器元素
 * @param {Object} options - 配置选项
 * @returns {Array} 处理后的数据
 */
function processTableData(container, options = {}) {
  const defaults = {
    includeHeaders: true,      // 是否包含标题
    processNestedTables: true, // 是否处理嵌套表格
    detectMergedCells: true,   // 是否检测合并单元格
    debug: false               // 是否输出调试信息
  };
  
  const settings = { ...defaults, ...options };
  const tables = container.querySelectorAll('table');
  
  if (settings.debug) {
    console.log(`找到 ${tables.length} 个表格`);
  }
  
  // 如果只有一个表格，直接处理它
  if (tables.length === 1) {
    return processTableWithCorrectStructure(tables[0], settings);
  }
  
  // 处理多个表格的情况，假设第一个是标题，第二个是数据
  const headerTable = tables[0];
  const dataTable = tables[1];
  
  // 分析表格获取正确的列数
  const maxColumns = determineMaxColumns([headerTable, dataTable]);
  
  if (settings.debug) {
    console.log(`检测到最大列数: ${maxColumns}`);
  }
  
  // 从标题表格提取标题数据（保持正确的列对齐）
  const headerData = extractTableData(headerTable, maxColumns, settings);
  
  // 从数据表格提取数据（保持正确的列对齐）
  const bodyData = extractTableData(dataTable, maxColumns, settings);
  
  // 合并标题和数据
  const combinedData = [...headerData.rows, ...bodyData.rows];
  
  // 合并合并单元格信息，注意调整行索引
  const merges = [
    ...headerData.merges,
    ...bodyData.merges.map(merge => ({
      s: { r: merge.s.r + headerData.rows.length, c: merge.s.c },
      e: { r: merge.e.r + headerData.rows.length, c: merge.e.c }
    }))
  ];
  
  // 转换为普通数组（只含值）
  const flatData = combinedData.map(row => row.map(cell => cell.value));
  
  return { data: flatData, merges };
}

/**
 * 确定表格的最大列数
 * @param {Array<HTMLTableElement>} tables - 表格元素数组
 * @returns {number} 最大列数
 */
function determineMaxColumns(tables) {
  let maxCols = 0;
  
  tables.forEach(table => {
    const rows = table.querySelectorAll('tr');
    rows.forEach(row => {
      let colCount = 0;
      const cells = row.querySelectorAll('th, td');
      cells.forEach(cell => {
        colCount += parseInt(cell.colSpan || 1, 10);
      });
      maxCols = Math.max(maxCols, colCount);
    });
  });
  
  return maxCols;
}

/**
 * 从表格提取数据，正确处理合并单元格和保持列对齐
 * @param {HTMLTableElement} table - 表格元素
 * @param {number} maxColumns - 最大列数
 * @param {Object} settings - 配置设置
 * @returns {Object} 处理后的行数据和合并单元格信息
 */
function extractTableData(table, maxColumns, settings) {
  const rows = table.querySelectorAll('tr');
  const resultRows = [];
  const merges = [];
  
  // 创建跟踪矩阵，记录哪些单元格被跨行单元格占用
  // 矩阵索引为：[行][列]
  const spanMatrix = Array(rows.length).fill().map(() => Array(maxColumns).fill(false));
  
  // 第一遍：收集所有合并单元格信息
  const spanInfo = [];
  let rowIndex = 0;
  rows.forEach(row => {
    let colIndex = 0;
    const cells = row.querySelectorAll('th, td');
    
    cells.forEach(cell => {
      // 跳过被跨行单元格占用的位置
      while (colIndex < maxColumns && spanMatrix[rowIndex][colIndex]) {
        colIndex++;
      }
      
      if (colIndex >= maxColumns) return; // 超出最大列数，忽略此单元格
      
      const rowSpan = parseInt(cell.rowSpan || 1, 10);
      const colSpan = parseInt(cell.colSpan || 1, 10);
      
      // 记录此单元格跨行跨列的信息
      spanInfo.push({
        row: rowIndex,
        col: colIndex,
        rowSpan,
        colSpan,
        value: cell.textContent.trim()
      });
      
      // 在占用矩阵中标记被此单元格占用的位置
      for (let r = 0; r < rowSpan; r++) {
        for (let c = 0; c < colSpan; c++) {
          if (rowIndex + r < rows.length && colIndex + c < maxColumns) {
            spanMatrix[rowIndex + r][colIndex + c] = true;
          }
        }
      }
      
      colIndex += colSpan;
    });
    
    rowIndex++;
  });
  
  // 第二遍：构建结果行，正确处理合并单元格
  // 初始化空结果数组
  for (let i = 0; i < rows.length; i++) {
    resultRows.push(Array(maxColumns).fill({ value: '' }));
  }
  
  // 填充单元格值，包括合并单元格的值
  spanInfo.forEach(info => {
    // 添加单元格值到结果数组
    resultRows[info.row][info.col] = { value: info.value };
    
    // 记录合并单元格信息
    if (info.rowSpan > 1 || info.colSpan > 1) {
      merges.push({
        s: { r: info.row, c: info.col },
        e: { r: info.row + info.rowSpan - 1, c: info.col + info.colSpan - 1 }
      });
      
      if (settings.debug) {
        console.log(`检测到合并单元格: (${info.row},${info.col}) 跨 ${info.rowSpan} 行 ${info.colSpan} 列, 值: "${info.value}"`);
      }
    }
  });
  
  return { rows: resultRows, merges };
}

/**
 * 处理单个表格的数据，确保正确处理合并单元格
 * @param {HTMLTableElement} table - 表格元素
 * @param {Object} settings - 配置设置
 * @returns {Object} 处理后的数据和合并单元格信息
 */
function processTableWithCorrectStructure(table, settings) {
  // 确定最大列数
  const maxColumns = determineMaxColumns([table]);
  
  // 提取表格数据，保持正确的列对齐
  const processedData = extractTableData(table, maxColumns, settings);
  
  // 转换为简单数组格式
  const flatData = processedData.rows.map(row => row.map(cell => cell.value));
  
  return { data: flatData, merges: processedData.merges };
}

/**
 * 将处理好的数据导出为Excel文件，并自动调整列宽和行高
 * @param {Array} data - 表格数据
 * @param {Array} merges - 合并单元格信息
 * @param {String} fileName - 文件名
 * @param {Object} options - 其他选项
 */
async function exportToExcel(data, merges, fileName = 'table-export', options = {}) {
  try {
    // 加载SheetJS库
    const XLSX = await loadSheetJS();
    
    // 创建工作簿
    const wb = XLSX.utils.book_new();
    
    // 创建工作表
    const ws = XLSX.utils.aoa_to_sheet(data.data || data);
    
    // 如果有合并单元格，添加到工作表
    if (merges && merges.length > 0) {
      ws['!merges'] = merges;
    }
    
    // 自动调整列宽
    autoAdjustColumnWidths(ws, data.data || data);
    
    // 自动调整行高（通过设置wch属性）
    autoAdjustRowHeights(ws, data.data || data);
    
    // 添加样式信息
    addStylesInfo(ws, data.data || data, options);
    
    // 将工作表添加到工作簿
    XLSX.utils.book_append_sheet(wb, ws, 'Sheet1');
    
    // 在浏览器中导出为Excel文件（使用浏览器兼容的方法）
    // 将工作簿转换为二进制数据（使用binary字符串而不是array）
    const wbout = XLSX.write(wb, { bookType: 'xlsx', type: 'binary' });
    
    // 将二进制字符串转换为ArrayBuffer
    function s2ab(s) {
      const buf = new ArrayBuffer(s.length);
      const view = new Uint8Array(buf);
      for (let i = 0; i < s.length; i++) {
        view[i] = s.charCodeAt(i) & 0xFF;
      }
      return buf;
    }
    
    // 创建Blob对象
    const blob = new Blob([s2ab(wbout)], { type: 'application/octet-stream' });
    
    // 创建下载链接
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${fileName}.xlsx`;
    
    // 添加到文档并触发点击
    document.body.appendChild(a);
    a.click();
    
    // 清理
    setTimeout(() => {
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    }, 0);
    
    console.log(`成功导出为 ${fileName}.xlsx`);
  } catch (error) {
    console.error('导出Excel时出错:', error);
  }
}

/**
 * 自动调整工作表的列宽以适应内容
 * @param {Object} ws - 工作表对象
 * @param {Array} data - 表格数据
 */
function autoAdjustColumnWidths(ws, data) {
  // 初始化列宽数组
  const colWidths = [];
  
  // 计算每列的最大宽度，基于字符数量
  data.forEach(row => {
    row.forEach((cell, colIndex) => {
      const content = cell ? String(cell) : '';
      
      // 根据内容类型和长度估算合适的宽度
      let width = estimateTextWidth(content);
      
      // 更新列的最大宽度
      colWidths[colIndex] = Math.max(colWidths[colIndex] || 0, width);
    });
  });
  
  // 应用列宽到工作表
  const cols = [];
  colWidths.forEach((width, i) => {
    // 设置最小宽度，确保不会太窄
    const adjustedWidth = Math.max(width, 6);
    
    // 列宽不能超过最大值，Excel中列宽的最大值为255
    const finalWidth = Math.min(adjustedWidth, 100);
    
    cols.push({ wch: finalWidth });
  });
  
  ws['!cols'] = cols;
}

/**
 * 估算文本在Excel中的宽度
 * @param {String} text - 文本内容
 * @returns {Number} - 估算的宽度值
 */
function estimateTextWidth(text) {
  if (!text) return 0;
  
  const str = String(text);
  
  // 中文字符和特殊符号一般占用更多宽度
  const wideChars = str.replace(/[\x00-\xff]/g, '').length; // 非ASCII字符
  const narrowChars = str.length - wideChars; // ASCII字符
  
  // 根据字符类型估算宽度
  // 中文等宽字符占用约2个单位宽度
  return narrowChars + (wideChars * 2) + 1; // 加1作为缓冲
}

/**
 * 自动调整工作表的行高以适应内容
 * @param {Object} ws - 工作表对象
 * @param {Array} data - 表格数据
 */
function autoAdjustRowHeights(ws, data) {
  // 创建行高数组
  const rowHeights = [];
  
  // 计算每行的适当高度，基于内容行数和文本长度
  data.forEach((row, rowIndex) => {
    let maxLinesInRow = 1;
    
    row.forEach(cell => {
      if (!cell) return;
      
      const content = String(cell);
      
      // 检测文本中的换行符，估算行数
      const lines = content.split(/\r\n|\r|\n/).length;
      
      // 检测长文本，可能需要自动换行
      const textLength = content.length;
      const estimatedLines = Math.ceil(textLength / 50); // 假设每50个字符可能需要换行
      
      // 取行数和估算行数的较大值
      const totalLines = Math.max(lines, estimatedLines);
      
      // 更新当前行的最大行数
      maxLinesInRow = Math.max(maxLinesInRow, totalLines);
    });
    
    // 根据行数计算行高
    // Excel中行高以磅为单位，一般默认行高约为15磅
    // 我们根据行数来设置合适的高度
    const rowHeight = maxLinesInRow * 15; // 每行15磅
    
    rowHeights[rowIndex] = { hpt: rowHeight };
  });
  
  // 应用行高到工作表
  ws['!rows'] = rowHeights;
}

/**
 * 添加样式信息到工作表
 * @param {Object} ws - 工作表对象
 * @param {Array} data - 表格数据
 * @param {Object} options - 样式选项
 */
function addStylesInfo(ws, data, options = {}) {
  // 默认样式选项
  const defaultOptions = {
    headerStyle: true,     // 是否应用表头样式
    zebra: false,          // 是否应用斑马纹
    borders: true,         // 是否应用边框
    wrapText: true         // 是否启用自动换行
  };
  
  const styleOptions = { ...defaultOptions, ...options };
  
  // SheetJS不直接支持样式设置，但我们可以设置一些基础属性
  // 在高级场景下，需要使用SheetJS Pro或其他库
  
  // 为每个单元格设置通用属性
  const range = XLSX.utils.decode_range(ws['!ref']);
  for (let row = range.s.r; row <= range.e.r; ++row) {
    for (let col = range.s.c; col <= range.e.c; ++col) {
      const cellAddress = XLSX.utils.encode_cell({ r: row, c: col });
      const cell = ws[cellAddress];
      
      if (!cell) continue;
      
      // 启用自动换行
      if (styleOptions.wrapText) {
        if (!cell.s) cell.s = {};
        cell.s.alignment = { wrapText: true, vertical: 'top' };
      }
    }
  }
}

/**
 * 导出包含嵌套表格的容器为Excel
 * @param {HTMLElement|String} container - 容器元素或选择器
 * @param {String} fileName - 导出的文件名
 * @param {Object} options - 配置选项
 */
async function exportNestedTablesToExcel(container, fileName = 'nested-tables-export', options = {}) {
  // 如果传入的是选择器，获取对应的元素
  const containerElement = typeof container === 'string' 
    ? document.querySelector(container) 
    : container;
  
  if (!containerElement) {
    console.error('无法找到指定的容器元素');
    return;
  }
  
  // 处理表格数据
  const processedData = processTableData(containerElement, options);
  
  // 导出为Excel
  await exportToExcel(processedData, processedData.merges, fileName, options);
}

/**
 * 自动识别页面上的表格结构并导出
 * 此函数会尝试智能识别页面上的表格结构，包括嵌套表格和独立表格
 */
async function autoDetectAndExportTables() {
  // 获取页面上所有表格
  const allTables = document.querySelectorAll('table');
  console.log(`页面上共有 ${allTables.length} 个表格`);
  
  // 如果没有表格，直接返回
  if (allTables.length === 0) {
    console.error('页面上没有找到表格');
    return;
  }
  
  // 检查是否存在嵌套表格
  const nestedTableContainers = [];
  
  // 遍历所有表格，寻找包含其他表格的容器
  allTables.forEach(table => {
    const nestedTables = table.querySelectorAll('table');
    if (nestedTables.length > 0) {
      nestedTableContainers.push(table);
    }
  });
  
  // 如果找到嵌套表格，逐个导出
  if (nestedTableContainers.length > 0) {
    console.log(`找到 ${nestedTableContainers.length} 个嵌套表格容器`);
    
    // 遍历每个容器并导出
    for (let i = 0; i < nestedTableContainers.length; i++) {
      await exportNestedTablesToExcel(
        nestedTableContainers[i], 
        `嵌套表格-${i+1}`, 
        { debug: true }
      );
    }
    return;
  }
  
  // 如果没有嵌套表格，检查是否有多个表格共同组成一个数据集
  // 这种情况通常是标题表格和数据表格分开
  if (allTables.length > 1) {
    // 查找彼此靠近的表格
    const tableGroups = [];
    let currentGroup = [allTables[0]];
    
    for (let i = 1; i < allTables.length; i++) {
      const prevTable = allTables[i-1];
      const currTable = allTables[i];
      
      // 计算两个表格之间的距离
      const prevRect = prevTable.getBoundingClientRect();
      const currRect = currTable.getBoundingClientRect();
      const distance = currRect.top - (prevRect.top + prevRect.height);
      
      // 如果距离小于阈值，认为它们是同一组
      if (distance < 50) { // 50像素作为阈值
        currentGroup.push(currTable);
      } else {
        tableGroups.push([...currentGroup]);
        currentGroup = [currTable];
      }
    }
    
    // 添加最后一组
    if (currentGroup.length > 0) {
      tableGroups.push(currentGroup);
    }
    
    console.log(`将表格分组为 ${tableGroups.length} 组`);
    
    // 为每组创建一个包裹元素并导出
    for (let i = 0; i < tableGroups.length; i++) {
      const group = tableGroups[i];
      
      // 创建一个临时容器
      const container = document.createElement('div');
      
      // 将组中的表格复制到容器中
      group.forEach(table => {
        container.appendChild(table.cloneNode(true));
      });
      
      // 导出这个容器
      await exportNestedTablesToExcel(
        container, 
        `表格组-${i+1}`, 
        { debug: true }
      );
    }
    return;
  }
  
  // 如果只有一个表格，直接导出
  if (allTables.length === 1) {
    const table = allTables[0];
    const { data, merges } = processSingleTable(table, { detectMergedCells: true });
    await exportToExcel(data, merges, '单表格导出');
  }
}

// 示例使用
// 1. 导出特定容器内的嵌套表格
// exportNestedTablesToExcel('#tableContainer', '我的嵌套表格');

// 2. 自动检测页面上的表格结构并导出
// autoDetectAndExportTables();
