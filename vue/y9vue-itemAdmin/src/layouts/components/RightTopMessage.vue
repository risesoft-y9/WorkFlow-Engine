<!--
 * @Author: your name
 * @Date: 2022-01-13 17:30:06
 * @LastEditTime: 2022-04-01 16:46:22
 * @LastEditors: hongzhew
 * @Description: 打开koroFileHeader查看配置 进行设置: https://github.com/OBKoro1/koro1FileHeader/wiki/%E9%85%8D%E7%BD%AE
 * @FilePath: /sz- team-frontend-9.6.x/y9vue-home/src/layouts/components/RightTopMessage.vue
-->
<template>
    <router-link class="indexlayout-top-message" to="/">
        <icon-svg :style="{ fontSize: '16px' }" type="message"></icon-svg>
        <el-badge :value="message" class="indexlayout-top-message-badge" type="danger" />
    </router-link>
</template>
<script lang="ts">
    import { computed, ComputedRef, defineComponent, onMounted } from 'vue';
    import IconSvg from './IconSvg';

    interface RightTopMessageSetupData {
        message: ComputedRef<number>;
    }

    export default defineComponent({
        name: 'RightTopMessage',
        components: {
            IconSvg
        },
        setup(): RightTopMessageSetupData {
            // const store = useStore<{user: UserStateType}>();

            const message = computed<number>(() => 2);

            onMounted(() => {
                // store.dispatch("user/fetchMessage");
            });

            return {
                message
            };
        }
    });
</script>
<style lang="scss" scoped>
    // @import '../../assets/css/global.scss';
    .indexlayout-top-message {
        height: $headerHeight;
        line-height: $headerHeight;
        /* display: inline-block; */
        display: inline;
        color: #c0c4cc;

        .indexlayout-top-message-badge {
            margin-left: -5px;
            margin-top: -20px;

            :deep(.el-badge__content) {
                border: 0;
            }
        }
    }
</style>
