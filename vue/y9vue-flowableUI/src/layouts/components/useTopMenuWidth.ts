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
