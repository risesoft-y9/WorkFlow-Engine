import { createPinia } from 'pinia';
import { useSettingStore } from './modules/settingStore';
import { useFlowableStore } from './modules/flowableStore';
// init pinia
const pinia = createPinia();

export function setupStore(app) {
    app.use(pinia);

    //  示例代码（持久化pinia的settingStore模块）
    const settingStore = useSettingStore();
    // settingStore-persist-key
    const persistKey = 'userSettingData';
    const object = localStorage.getItem(persistKey) && JSON.parse(localStorage.getItem(persistKey));
    if (object) {
        for (const key in object) {
            settingStore.$patch({
                [key]: object[key]
            });
        }
    }
    settingStore.$subscribe((mutation, state) => {
        localStorage.setItem(persistKey, JSON.stringify(state));
    });

    const flowableStore = useFlowableStore();
    // flowableStore-persist-key
    const persistKey1 = 'useFlowableData';
    const object1 = localStorage.getItem(persistKey1) && JSON.parse(localStorage.getItem(persistKey1));
    if (object) {
        for (const key in object) {
            flowableStore.$patch({
                [key]: object[key]
            });
        }
    }
    flowableStore.$subscribe((mutation, state) => {
        localStorage.setItem(persistKey1, JSON.stringify(state));
    });
}

export default pinia;
