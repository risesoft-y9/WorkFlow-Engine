/*
 * @Author: your name
 * @Date: 2022-01-13 17:34:55
 * @LastEditTime: 2025-12-04 14:06:43
 * @LastEditors: mengjuhua
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: \y9-vue\y9vue-attendance\src\layouts\components\useTopMenuWidth.ts
 */
/**
 * 设置 IndexLayout TopMenuWidth
 * @author LiQingSong  
 */
import { ComputedRef, nextTick, onMounted, ref, Ref, watch } from 'vue';

export default function useTopMenuWidth(topNavEnable: ComputedRef<boolean> | Ref<boolean>) {
    const topMenuCon = ref<HTMLElement | null>(null);

    const topMenuWidth = ref<string>('auto');

    const setWidth = async () => {
        await nextTick();
        if (topMenuCon.value && topNavEnable.value) {
            let width = 0;
            const child = topMenuCon.value.children;
            for (let index = 0, len = child.length; index < len; index++) {
                const element = child[index] as HTMLElement;
                width = width + element.offsetWidth + 0.5;
            }
            topMenuWidth.value = width + 'px';
        } else {
            topMenuWidth.value = 'auto';
        }
    };

    watch(topNavEnable, () => {
        setWidth();
    });

    onMounted(() => {
        setWidth();
    });

    return {
        topMenuCon,
        topMenuWidth
    };
}
