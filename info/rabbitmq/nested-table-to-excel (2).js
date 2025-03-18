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
    return processSingleTable(tables[0], settings);
  }
  
  // 处理多个表格的情况，假设第一个是标题，第二个是数据
  const headerTable = tables[0];
  const dataTable = tables[1];
  
  // 从标题表格提取标题数据
  const headerData = [];
  const headerRows = headerTable.querySelectorAll('tr');
  headerRows.forEach(row => {
    const rowData = [];
    const cells = row.querySelectorAll('th, td');
    cells.forEach(cell => {
      rowData.push({
        value: cell.textContent.trim(),
        rowspan: cell.rowSpan || 1,
        colspan: cell.colSpan || 1
      });
    });
    headerData.push(rowData);
  });
  
  // 从数据表格提取数据
  const bodyData = [];
  const dataRows = dataTable.querySelectorAll('tr');
  dataRows.forEach(row => {
    const rowData = [];
    const cells = row.querySelectorAll('td, th');
    cells.forEach(cell => {
      rowData.push({
        value: cell.textContent.trim(),
        rowspan: cell.rowSpan || 1,
        colspan: cell.colSpan || 1
      });
    });
    bodyData.push(rowData);
  });
  
  // 合并标题和数据
  const combinedData = [...headerData, ...bodyData];
  
  // 收集合并单元格信息
  const merges = [];
  if (settings.detectMergedCells) {
    let rowIndex = 0;
    combinedData.forEach(row => {
      let colIndex = 0;
      row.forEach(cell => {
        if (cell.rowspan > 1 || cell.colspan > 1) {
          merges.push({
            s: { r: rowIndex, c: colIndex },
            e: { r: rowIndex + cell.rowspan - 1, c: colIndex + cell.colspan - 1 }
          });
        }
        colIndex += cell.colspan;
      });
      rowIndex++;
    });
  }
  
  // 转换为普通数组（只含值）
  const flatData = combinedData.map(row => 
    row.map(cell => cell.value)
  );
  
  return { data: flatData, merges };
}

/**
 * 处理单个表格的数据
 * @param {HTMLTableElement} table - 表格元素
 * @param {Object} settings - 配置设置
 * @returns {Object} 处理后的数据和合并单元格信息
 */
function processSingleTable(table, settings) {
  const rows = table.querySelectorAll('tr');
  const data = [];
  const merges = [];
  
  let rowIndex = 0;
  rows.forEach(row => {
    const rowData = [];
    const cells = row.querySelectorAll('th, td');
    
    let colIndex = 0;
    cells.forEach(cell => {
      rowData.push(cell.textContent.trim());
      
      // 检测合并单元格
      if (settings.detectMergedCells && (cell.rowSpan > 1 || cell.colSpan > 1)) {
        merges.push({
          s: { r: rowIndex, c: colIndex },
          e: { r: rowIndex + cell.rowSpan - 1, c: colIndex + cell.colSpan - 1 }
        });
      }
      
      colIndex += (cell.colSpan || 1);
    });
    
    data.push(rowData);
    rowIndex++;
  });
  
  return { data, merges };
}

/**
 * 将处理好的数据导出为Excel文件
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
