// plugin.json
{
    "name": "button-hider",
    "author": "YourName",
    "url": "https://github.com/yourusername/siyuan-button-hider",
    "version": "0.0.1",
    "minAppVersion": "2.10.14",
    "backends": ["all"],
    "frontends": ["all"],
    "displayName": {
        "default": "Button Hider",
        "zh_CN": "按钮隐藏器"
    },
    "description": {
        "default": "Hide buttons by data-id",
        "zh_CN": "通过data-id隐藏按钮"
    },
    "readme": {
        "default": "Hide specific buttons in SiYuan interface",
        "zh_CN": "在思源笔记界面中隐藏特定按钮"
    }
}

// index.js
class ButtonHiderPlugin {
    constructor() {
        this.hiddenButtons = ['share2Liandi']; // 默认隐藏的按钮data-id列表
    }

    onload() {
        // 注册设置面板
        this.registerSettings({
            buttonIds: {
                title: "要隐藏的按钮ID",
                description: "输入要隐藏的按钮data-id，用逗号分隔",
                type: "string",
                default: "share2Liandi"
            }
        });

        // 初始化CSS
        this.style = document.createElement('style');
        document.head.appendChild(this.style);
        
        // 更新按钮隐藏状态
        this.updateHiddenButtons();
        
        // 监听DOM变化
        this.observer = new MutationObserver(() => {
            this.updateHiddenButtons();
        });
        
        this.observer.observe(document.body, {
            childList: true,
            subtree: true
        });
    }

    updateHiddenButtons() {
        // 获取设置中的按钮ID列表
        const buttonIds = this.getSettings('buttonIds').split(',').map(id => id.trim());
        
        // 构建CSS规则
        let cssRules = buttonIds.map(id => `
            [data-id="${id}"] {
                display: none !important;
            }
        `).join('\n');
        
        // 更新样式
        this.style.textContent = cssRules;
    }

    onunload() {
        // 清理工作
        this.observer.disconnect();
        this.style.remove();
    }
}

module.exports = ButtonHiderPlugin;

// i18n/zh_CN.json
{
    "buttonIds": "要隐藏的按钮ID",
    "buttonIds_description": "输入要隐藏的按钮data-id，用逗号分隔"
}
