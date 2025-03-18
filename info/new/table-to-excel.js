/**
 * 将网页中的表格导出为Excel文件
 * @param {HTMLTableElement|string} table - 表格元素或CSS选择器
 * @param {string} fileName - 导出的文件名（不包含扩展名）
 */
function exportTableToExcel(table, fileName = 'table-export') {
  // 如果传入的是CSS选择器，获取对应的表格元素
  const tableElement = typeof table === 'string' ? document.querySelector(table) : table;
  
  if (!tableElement || tableElement.tagName !== 'TABLE') {
    console.error('请提供有效的表格元素或CSS选择器');
    return;
  }
  
  // 创建一个工作簿
  let csv = '';
  
  // 获取所有行
  const rows = tableElement.querySelectorAll('tr');
  
  // 遍历每一行
  rows.forEach(row => {
    let rowData = [];
    
    // 获取当前行的所有单元格（th和td）
    const cells = row.querySelectorAll('th, td');
    
    // 遍历每个单元格
    cells.forEach(cell => {
      // 处理单元格内容，替换双引号并确保CSV格式正确
      let cellText = cell.textContent.replace(/"/g, '""');
      rowData.push(`"${cellText}"`);
    });
    
    // 将当前行添加到CSV
    csv += rowData.join(',') + '\n';
  });
  
  // 创建Blob对象
  const blob = new Blob(["\uFEFF" + csv], { type: 'text/csv;charset=utf-8;' });
  
  // 创建下载链接
  const link = document.createElement('a');
  
  // 支持文件下载的浏览器
  if (navigator.msSaveBlob) { // 针对IE和Edge浏览器
    navigator.msSaveBlob(blob, fileName + '.csv');
  } else {
    // 其他现代浏览器
    const url = URL.createObjectURL(blob);
    link.href = url;
    link.setAttribute('download', fileName + '.xlsx');
    link.style.visibility = 'hidden';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  }
}

// 使用示例：
// 1. 导出特定ID的表格
// exportTableToExcel('#myTable', '我的表格');

// 2. 导出页面上的第一个表格
// exportTableToExcel(document.querySelector('table'), '表格数据');

// 3. 导出指定的表格元素
// const tableElement = document.getElementById('dataTable');
// exportTableToExcel(tableElement, '数据表格');

// 4. 导出页面上的所有表格
function exportAllTables() {
  const tables = document.querySelectorAll('table');
  tables.forEach((table, index) => {
    exportTableToExcel(table, `表格-${index + 1}`);
  });
}
// exportAllTables();
